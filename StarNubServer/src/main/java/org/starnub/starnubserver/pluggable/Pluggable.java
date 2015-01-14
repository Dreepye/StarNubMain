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
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.events.packet.PacketEventHandler;
import org.starnub.starnubserver.events.packet.PacketEventSubscription;
import org.starnub.starnubserver.events.starnub.StarNubEventHandler;
import org.starnub.starnubserver.events.starnub.StarNubEventSubscription;
import org.starnub.starnubserver.pluggable.exceptions.MissingData;
import org.starnub.starnubserver.pluggable.exceptions.PluginDirectoryCreationFailed;
import org.starnub.starnubserver.pluggable.generic.PluggableDetails;
import org.starnub.utilities.events.Priority;
import org.starnub.utilities.file.yaml.YAMLWrapper;
import org.starnub.utilities.file.yaml.YamlUtilities;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

public abstract class Pluggable {

    protected PluggableType type;
    protected PluggableFileType fileType;
    protected File file;
    protected PluggableDetails details;
    private final HashSet<StarNubEventSubscription> STARNUB_EVENT_SUBSCRIPTIONS = new HashSet<>();
    private final HashSet<PacketEventSubscription> PACKET_EVENT_SUBSCRIPTIONS = new HashSet<>();
    private final HashSet<StarNubTask> STARNUB_TASK = new HashSet<>();

    public Pluggable() {
    }

    public void setPluggable(UnloadedPluggable unloadedPluggable) throws PluginDirectoryCreationFailed, MissingData, IOException {
        details = unloadedPluggable.getPluggableDetails();
        file = unloadedPluggable.getPluggableFile();
        fileType = unloadedPluggable.getPluggableFileType();
        YAMLWrapper yamlWrapper = unloadedPluggable.getYamlWrapper();
        loadData(yamlWrapper);
    }

    public PluggableDetails getDetails() {
        return details;
    }

    public File getFile() {
        return file;
    }

    public PluggableFileType getFileType() {
        return fileType;
    }

    public String getRegistrationName(){
        String nameString = details.getNAME();
        String ownerString = details.getOWNER();
        String classString = details.getCLASS();
        if(fileType == PluggableFileType.JAVA && classString.contains(".")){
            int lastIndexOf = classString.lastIndexOf(".");
            classString = classString.substring(lastIndexOf + 1);
        }
        String typeString = "";
        if (type == PluggableType.PLUGIN){
            typeString = "Plugin";
        } else if (type == PluggableType.COMMAND){
            typeString = "Command";
        }
        return ownerString + " - " + nameString + " - " + classString + " - " + typeString;
    }

    public StarNubEventSubscription newStarNubEventSubscription(Priority priority, String eventKey, StarNubEventHandler starNubEventHandler){
        StarNubEventSubscription starNubEventSubscription = new StarNubEventSubscription(getRegistrationName(), priority, eventKey, starNubEventHandler);
        this.STARNUB_EVENT_SUBSCRIPTIONS.add(starNubEventSubscription);
        return starNubEventSubscription;
    }

    public PacketEventSubscription newPacketEventSubscription(Priority priority, Class<? extends Packet> eventKey, PacketEventHandler packetEventHandler){
        PacketEventSubscription packetEventSubscription = new PacketEventSubscription(getRegistrationName(), priority, eventKey, packetEventHandler);
        this.PACKET_EVENT_SUBSCRIPTIONS.add(packetEventSubscription);
        return packetEventSubscription;
    }

    public StarNubTask newStarNubTask(String taskName, long timeDelay, TimeUnit timeUnit, Runnable runnable){
        StarNubTask starNubTask = new StarNubTask(getRegistrationName(), taskName, timeDelay, timeUnit, runnable);
        this.STARNUB_TASK.add(starNubTask);
        return starNubTask;
    }

    public StarNubTask newStarNubTask(String taskName, boolean fixedDelay, long initialDelay, long timeDelay, TimeUnit timeUnit, Runnable runnable){
        StarNubTask starNubTask = new StarNubTask(getRegistrationName(), taskName, fixedDelay, initialDelay, timeDelay, timeUnit, runnable);
        this.STARNUB_TASK.add(starNubTask);
        return starNubTask;
    }

    public void unregisterSubscriptions(){
        STARNUB_EVENT_SUBSCRIPTIONS.forEach(org.starnub.starnubserver.events.starnub.StarNubEventSubscription::removeRegistration);
        PACKET_EVENT_SUBSCRIPTIONS.forEach(org.starnub.starnubserver.events.packet.PacketEventSubscription::removeRegistration);
    }

    public void unregisterTask(){
        STARNUB_TASK.forEach(org.starnub.starnubserver.StarNubTask::removeTask);
    }

    public abstract void loadData(YAMLWrapper pluggableInfo) throws IOException, PluginDirectoryCreationFailed;
    public abstract void register();
    public abstract void dumpDetails() throws IOException;

    public void dumpPluggableDetails(LinkedHashMap<String, Object> specificDetails, String path) throws IOException {
        LinkedHashMap pluginDetailDump = new LinkedHashMap();
        pluginDetailDump.put("Type", type);
        pluginDetailDump.put("File Type", fileType);
        pluginDetailDump.put("File", file.getAbsolutePath());
        LinkedHashMap<String, Object> detailsMap = getDetails().getDetailsMap();
        pluginDetailDump.put("Details", detailsMap);
        pluginDetailDump.put("Specific Details", specificDetails);
        YamlUtilities.toFileYamlDump(pluginDetailDump, path, details.getOWNER() + "_Details.yml");
    }

    @Override
    public String toString() {
        return "Pluggable{" +
                "details=" + details +
                ", type=" + fileType +
                "} " + super.toString();
    }
}
