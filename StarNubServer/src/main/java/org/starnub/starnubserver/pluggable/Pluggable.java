/*
 * Copyright (C) 2014 www.StarNub.org - Underbalanced
 *
 * This file is part of org.starnub a Java Wrapper for Starbound.
 *
 * This above mentioned StarNub software is free software:
 * you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free
 * Software Foundation, either version  3 of the License, or
 * any later version. This above mentioned CodeHome software
 * is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU General Public License for more details. You should
 * have received a copy of the GNU General Public License in
 * this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.starnub.starnubserver.pluggable;

import org.starnub.starbounddata.packets.Packet;
import org.starnub.starbounddata.types.color.Colors;
import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.starnubserver.events.packet.PacketEventHandler;
import org.starnub.starnubserver.events.packet.PacketEventSubscription;
import org.starnub.starnubserver.events.starnub.StarNubEventHandler;
import org.starnub.starnubserver.events.starnub.StarNubEventSubscription;
import org.starnub.starnubserver.pluggable.exceptions.DirectoryCreationFailed;
import org.starnub.starnubserver.pluggable.exceptions.MissingData;
import org.starnub.starnubserver.pluggable.generic.PluggableDetails;
import org.starnub.starnubserver.pluggable.resources.PluggableConfiguration;
import org.starnub.starnubserver.resources.StringTokens;
import org.starnub.starnubserver.resources.tokens.StringToken;
import org.starnub.starnubserver.resources.tokens.TokenHandler;
import org.starnub.utilities.events.Priority;
import org.starnub.utilities.file.yaml.YamlUtilities;
import org.starnub.utilities.file.yaml.YamlWrapper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

public abstract class Pluggable {

    private PluggableFileType fileType;
    private File file;
    private PluggableDetails details;
    private PluggableConfiguration configuration;
    private boolean enabled;

    private final Object S_E_S_LOCK = new Object();
    private final Object P_E_S_LOCK = new Object();
    private final Object S_TA_LOCK = new Object();
    private final Object S_TO_LOCK = new Object();
    private final HashSet<StarNubEventSubscription> STARNUB_EVENT_SUBSCRIPTIONS = new HashSet<>();
    private final HashSet<PacketEventSubscription> PACKET_EVENT_SUBSCRIPTIONS = new HashSet<>();
    private final HashSet<StarNubTask> STARNUB_TASK = new HashSet<>();
    private final HashSet<StringToken> STRING_TOKENS = new HashSet<>();

    public Pluggable() {
    }

    public void setPluggable(UnloadedPluggable unloadedPluggable) throws DirectoryCreationFailed, MissingData, IOException {
        file = unloadedPluggable.getFile();
        fileType = unloadedPluggable.getFileType();
        details = unloadedPluggable.getDetails();
        configuration = unloadedPluggable.getConfiguration();
        YamlWrapper yamlWrapper = unloadedPluggable.getYamlWrapper();
        loadData(yamlWrapper);
        newStarNubTask("StarNub - Internal Pluggable Clean Up", true, 2, 2, TimeUnit.MINUTES, this::cleanStarNubTask);
        new StarNubEvent("Pluggable_Loaded", this);
        StarNub.getLogger().cInfoPrint("StarNub", "Pluggable " + details.getNameVersion() + " successfully loaded.");
        try {
            dumpPluggableDetails();
        } catch (IOException e) {
            StarNub.getLogger().cErrPrint("StarNub", "StarNub was unable to dump " + details.getNameVersion() + " information to disk.");
            new StarNubEvent("Plugin_Information_Dump_Error", unloadedPluggable);
            e.printStackTrace();
        }
    }

    public PluggableFileType getFileType() {
        return fileType;
    }

    public File getFile() {
        return file;
    }

    public PluggableDetails getDetails() {
        return details;
    }

    public PluggableConfiguration getConfiguration() {
        return configuration;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getRegistrationName(){
        String nameString = details.getNAME();
        String organization = details.getORGANIZATION();
        String classString = details.getCLASS();
        if(fileType == PluggableFileType.JAVA && classString.contains(".")){
            int lastIndexOf = classString.lastIndexOf(".");
            classString = classString.substring(lastIndexOf + 1);
        }
        String typeString = "";
        if (details.getTYPE() == PluggableType.PLUGIN){
            typeString = "Plugin";
        } else if (details.getTYPE() == PluggableType.COMMAND){
            typeString = "Command";
        }
        return organization + " - " + nameString + " - " + classString + " - " + typeString;
    }

    public StarNubEventSubscription newStarNubEventSubscription(Priority priority, String eventKey, StarNubEventHandler starNubEventHandler){
        StarNubEventSubscription starNubEventSubscription = new StarNubEventSubscription(getRegistrationName(), priority, eventKey, starNubEventHandler);
        synchronized (S_E_S_LOCK) {
            this.STARNUB_EVENT_SUBSCRIPTIONS.add(starNubEventSubscription);
        }
        return starNubEventSubscription;
    }

    public PacketEventSubscription newPacketEventSubscription(Priority priority, Class<? extends Packet> eventKey, PacketEventHandler packetEventHandler){
        PacketEventSubscription packetEventSubscription = new PacketEventSubscription(getRegistrationName(), priority, eventKey, packetEventHandler);
        synchronized (P_E_S_LOCK) {
            this.PACKET_EVENT_SUBSCRIPTIONS.add(packetEventSubscription);
        }
        return packetEventSubscription;
    }

    public StarNubTask newStarNubTask(String taskName, long timeDelay, TimeUnit timeUnit, Runnable runnable){
        StarNubTask starNubTask = new StarNubTask(getRegistrationName(), taskName, timeDelay, timeUnit, runnable);
        synchronized (S_TA_LOCK) {
            this.STARNUB_TASK.add(starNubTask);
        }
        return starNubTask;
    }

    public StarNubTask newStarNubTask(String taskName, boolean fixedDelay, long initialDelay, long timeDelay, TimeUnit timeUnit, Runnable runnable){
        StarNubTask starNubTask = new StarNubTask(getRegistrationName(), taskName, fixedDelay, initialDelay, timeDelay, timeUnit, runnable);
        this.STARNUB_TASK.add(starNubTask);
        return starNubTask;
    }

    public StringToken newStringToken(String tokenName, String token, String description, TokenHandler tokenHandler){
        StringToken stringToken = new StringToken(getRegistrationName(), tokenName, token, description, tokenHandler);
        synchronized (S_TO_LOCK) {
            this.STRING_TOKENS.add(stringToken);
        }
        return stringToken;
    }

    private void cleanStarNubTask(){
        synchronized (S_TA_LOCK){
            STARNUB_TASK.stream().filter(starNubTask -> starNubTask.getScheduledFuture().isDone()).forEach(STARNUB_TASK::remove);
        }
    }

    public String validateColor(String string){
        return Colors.validate(string);
    }

    public String replaceColors(String string){
        return Colors.shortcutReplacement(string);
    }

    public String replaceTokens(String string){
        return StringTokens.replaceTokens(string);
    }

    public String replaceColorAndTokens(String string){
        string = Colors.shortcutReplacement(string);
        return StringTokens.replaceTokens(string);
    }
    public void enable(){
        onEnable();
        String type = details.getTypeString();
        new StarNubEvent(type + "_Enabled", this);
        StarNub.getLogger().cInfoPrint("StarNub", getDetails().getNameVersion() + " enabled.");

        this.enabled = true;
    }

    public void disable(){
        unregister();
        onDisable();
        String type = details.getTypeString();
        new StarNubEvent(type + "_Disabled", this);
        StarNub.getLogger().cInfoPrint("StarNub", getDetails().getNameVersion() + " disabled.");
        this.enabled = false;
    }

    public void unregister(){
        unregisterTask();
        unregisterStarNubEventSubscriptions();
        unregisterPacketEventSubscriptions();
        unregisterStringTokens();
    }

    public void unregisterStarNubEventSubscriptions(){
        synchronized (S_E_S_LOCK) {
            STARNUB_EVENT_SUBSCRIPTIONS.forEach(StarNubEventSubscription::unregister);
            new StarNubEvent("Pluggable_StarNubEvents_Unregistered", this);
        }
    }

    public void unregisterPacketEventSubscriptions(){
        synchronized (P_E_S_LOCK) {
            PACKET_EVENT_SUBSCRIPTIONS.forEach(PacketEventSubscription::unregister);
            new StarNubEvent("Pluggable_PacketEvents_Unregistered", this);
        }
    }

    public void unregisterTask(){
        synchronized (S_TA_LOCK) {
            STARNUB_TASK.forEach(StarNubTask::unregister);
            new StarNubEvent("Pluggable_StarNubTask_Unregistered", this);
        }
    }

    public void unregisterStringTokens(){
        synchronized (S_TO_LOCK) {
            STRING_TOKENS.forEach(StringToken::unregister);
            new StarNubEvent("Pluggable_StringTokens_Unregistered", this);
        }
    }

    public abstract void loadData(YamlWrapper pluggableInfo) throws IOException, DirectoryCreationFailed;
    public abstract void onEnable();
    public abstract void onDisable();
    public abstract LinkedHashMap<String, Object> getDetailsMap() throws IOException;

    @SuppressWarnings("unchecked")
    private void dumpPluggableDetails() throws IOException {
        LinkedHashMap<String, Object> specificDetails  = getDetailsMap();
        LinkedHashMap pluginDetailDump = new LinkedHashMap();
        String typeString = null;
        String fileTypeString = null;
        String informationPath = null;
        if(details.getTYPE() == PluggableType.PLUGIN){
            typeString = "Plugin";
            informationPath = PluggableManager.getInstance().getPLUGIN_DIRECTORY_STRING() + getDetails().getNAME() + "/";
        } else if (details.getTYPE() == PluggableType.COMMAND){
            typeString = "Command";
            informationPath = PluggableManager.getInstance().getCOMMAND_DIRECTORY_STRING() + getDetails().getNAME() + "/";
        }
        if(fileType == PluggableFileType.JAVA){
            fileTypeString = "Java";
        } else if (fileType == PluggableFileType.PYTHON){
            fileTypeString = "Python";
        }
        pluginDetailDump.put("Type", typeString);
        pluginDetailDump.put("Language", fileTypeString);
        pluginDetailDump.put("File", file.getAbsolutePath());
        LinkedHashMap<String, Object> detailsMap = getDetails().getDetailsMap();
        pluginDetailDump.put("Details", detailsMap);
        pluginDetailDump.put(typeString + " Details", specificDetails);
        YamlUtilities.toFileYamlDump(pluginDetailDump, informationPath, details.getNAME().toLowerCase() + "_details.yml");
    }

    @Override
    public String toString() {
        return "Pluggable{" +
                "details=" + details +
                ", type=" + fileType +
                "} " + super.toString();
    }
}
