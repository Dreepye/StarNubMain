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

package org.starnub.starnubserver.cache.wrappers;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.events.PermissionChange;
import org.starnub.starnubserver.events.events.PermissionEvent;
import org.starnub.starnubserver.events.starnub.StarNubEventHandler;
import org.starnub.starnubserver.events.starnub.StarNubEventSubscription;
import org.starnub.starnubserver.resources.connections.Players;
import org.starnub.utilities.cache.objects.BooleanCache;
import org.starnub.utilities.events.Priority;
import org.starnub.utilities.events.types.ObjectEvent;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * Represents {@link io.netty.channel.ChannelHandlerContext} CacheWrapper which uses a {@link io.netty.channel.ChannelHandlerContext}
 * as a key and can be used with any CacheObjects found at (@link starnubserver.utilities.cache.objects}
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class PermissionCacheWrapper extends PlayerCtxCacheWrapper {

    private final String PERMISSION;

    /**
     * Time specific constructor. RECOMMENDED.
     *
     * @param CACHE_OWNER                    String representing the owner of this utilities.cache, should be set to the plugins exact name
     *                                       not exist already, if you will not be using this which is not recommended, use null in its place.
     * @param PERMISSION                     String representing the permission to cache
     */
    public PermissionCacheWrapper(String CACHE_OWNER, String PERMISSION) {
        super(CACHE_OWNER, CACHE_OWNER + " - Permission Cache -  Permission: " + PERMISSION, true, 1, TimeUnit.SECONDS, 0, 0);
        this.PERMISSION = PERMISSION;// IS NULL DUE TO NOT FULLL INSTANTIATION FIX_URGENT
    }

    /**
     * This will purge ChannelHandlerContext from the list when players disconnect
     */
    @Override
    public void registerEvents() {
        super.registerEvents();
        playerConnectListener();
        permissionModificationListener();
        cacheVerifyCleanTask();
    }

    public void playerConnectListener(){
        new StarNubEventSubscription("StarNub", Priority.MEDIUM, "Player_Connected", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent eventData) {
                PlayerSession playerSession = (PlayerSession) eventData.getEVENT_DATA();
                addPermissionCache(playerSession);
            }
        });
    }

    private void permissionModificationListener(){
        new StarNubEventSubscription("StarNub", Priority.HIGH, "Permission_Modification_" + PERMISSION, new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent eventData) {
                PermissionEvent permissionEvent = (PermissionEvent) eventData;
                PermissionChange permissionChange = (PermissionChange) permissionEvent.getEVENT_DATA();
                PlayerSession playerSession = permissionChange.getPLAYER_SESSION();
                ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
                boolean hasPermission = playerSession.hasPermission(PERMISSION, true);
                BooleanCache booleanCache = (BooleanCache) getCache(clientCtx);
                booleanCache.setBool(hasPermission);
            }
        });
    }

    private void cacheVerifyCleanTask() {
        new StarNubTask(getCACHE_OWNER(), getCACHE_NAME(), true, 5, 5, TimeUnit.MINUTES, this::cacheVerifyClean);
    }

    private void cacheVerifyClean(){
        Players connectedPlayers = StarNub.getConnections().getCONNECTED_PLAYERS();
        HashSet<ChannelHandlerContext> cacheKeys = getCacheKeyList();

        /* Cache cleanup */
        cacheKeys.stream().filter(context -> !connectedPlayers.containsKey(context)).forEach(this::removeCache);

        /* Cache add */
        for(PlayerSession playerSession : connectedPlayers.values()){
            ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
            boolean containsCache = containsCache(clientCtx);
            if (!containsCache){
                addPermissionCache(playerSession);
            }
        }
    }

    private void addPermissionCache(PlayerSession playerSession){
        ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
        boolean hasPermission = playerSession.hasPermission(PERMISSION, true);
        BooleanCache booleanCache = new BooleanCache(hasPermission);
        addCache(clientCtx, booleanCache);
    }

}
