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

package starnubserver.resources;

import org.apache.commons.lang3.exception.ExceptionUtils;
import starnubserver.StarNub;
import starnubserver.StarNubTask;
import starnubserver.connections.player.generic.Ban;
import starnubserver.events.events.StarNubEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BansList extends ConcurrentHashMap<String, Ban> {



    /**
     * Creates a new, empty map with the default initial table size (16).
     */
    public BansList() {
        banLoad();
        new StarNubTask("StarNub", "StarNub - Bans - Auto Purge", true , 1, 1, TimeUnit.MINUTES, this::banPurge);
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is used to load restrictions from the database into memory
     * <p>
     */
    private void banLoad() {
        new StarNubEvent("StarNub_Bans_Loading", this);
        try {
            List<Ban> bans = StarNub.getDatabaseTables().getBans().getTableDao().queryForAll();
            for (Ban ban : bans) {
                ban = banPurgeCheck(ban);
                if (ban != null){
                    this.put(ban.getBanIdentifier(), ban);
                }
            }
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        new StarNubEvent("StarNub_Bans_Loaded", this);
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is the actual restriction purging method
     * <p>
     * @param ban Restriction to be check for purge
     */
    private Ban banPurgeCheck(Ban ban) {
        new StarNubEvent("StarNub_Bans_Purging", this);
        if (ban.getDateExpires() != null && ban.getDateExpires().isBeforeNow()) {
            ban.removeBan();
            StarNub.getLogger().cInfoPrint("StarNub", "A temporary ban has expired and was removed for " + ban.getPlayerCharacter().getCleanName() + ".");
            new StarNubEvent("StarNun_Ban_Purged_Expired", ban);
            return null;
        }
        return ban;
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is used to automatically remove any restrictions that have expired
     * <p>
     */
    public void banPurge(){
        for (Map.Entry<String, Ban> banEntry : this.entrySet()){
            Ban ban = banPurgeCheck(banEntry.getValue());
            if (ban == null){
                this.remove(banEntry.getKey());
            }
        }
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is used to check when a player logs in, if they are restrictions or not
     * <p>
     * @param ip String representing the IP to be checked
     * @param uuid String representing the UUID to be checked
     * @return boolean returns true if this ip or uuid is banned
     */
    public boolean banCheck(String ip, String uuid){
        return this.containsKey(ip) || this.containsKey(uuid);
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will check a single identifier for a ban
     * <p>
     * @param anyIdentifier String representing the IP to be checked
     * @return boolean returns true if this ip or uuid is banned
     */
    public boolean banCheck(String anyIdentifier){
        return this.containsKey(anyIdentifier);
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This is used to check when a player logs in, if they are restrictions or not
     * <p>
     * @param ip String representing the IP to be checked
     * @param uuid String representing the UUID to be checked
     * @return boolean returns true if this ip or uuid is banned
     */
    public Ban banGet(String ip, String uuid){
        Ban ban = this.get(ip);
        if (ban != null){
            return ban;
        } else {
            return this.get(uuid);
        }
    }
}
