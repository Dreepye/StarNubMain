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

package org.starnub.starnubserver.resources.predicates;

import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.utilities.connectivity.ConnectionType;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PSPredicates {

    public static Predicate<PlayerSession> multiFilter(Predicate<PlayerSession>... predicates) {
        return Stream.of(predicates).reduce(Predicate::and).orElse(x -> false);
    }

    public static Predicate<PlayerSession> filterOutSpecificPlayer(PlayerSession playerSessions) {
        return ps -> ps.getCONNECTION().getCLIENT_CTX() != playerSessions.getCONNECTION().getCLIENT_CTX();
    }

    public static Predicate<PlayerSession> filterOutAllPlayersBut(PlayerSession playerSessions) {
        return ps -> ps.getCONNECTION().getCLIENT_CTX() == playerSessions.getCONNECTION().getCLIENT_CTX();
    }


    public static Predicate<PlayerSession> inGamePlayers() {
        return ps -> ps.getCONNECTION_TYPE() == ConnectionType.PROXY_IN_GAME;
    }

    public static Predicate<PlayerSession> remotePlayers() {
        return ps -> ps.getCONNECTION_TYPE() == ConnectionType.REMOTE;
    }

    public static Predicate<PlayerSession> hasPermissionBase(String basePermission) {
        return ps -> ps.hasBasePermission(basePermission);
    }

    public static Predicate<PlayerSession> hasPermissionSub(String basePermission, String subPermission, boolean checkWildCards) {
        return ps -> ps.hasSubPermission(basePermission, subPermission, checkWildCards);
    }

    public static Predicate<PlayerSession> hasPermission(String permission, boolean checkWildCards) {
        return ps -> ps.hasPermission(permission, checkWildCards);
    }

    public static Predicate<PlayerSession> hasPermission(String basePermission, String subPermission, String fullPermission, boolean checkWildCards) {
        return ps -> ps.hasPermission(basePermission, subPermission, fullPermission, checkWildCards);
    }

    public static Predicate<PlayerSession> doesNotHavePermissionBase(String basePermission) {
        return ps -> !ps.hasBasePermission(basePermission);
    }

    public static Predicate<PlayerSession> doesNotHavePermissionSub(String basePermission, String subPermission, boolean checkWildCards) {
        return ps -> !ps.hasSubPermission(basePermission, subPermission, checkWildCards);
    }

    public static Predicate<PlayerSession> doesNotHavePermission(String permission, boolean checkWildCards) {
        return ps -> !ps.hasPermission(permission, checkWildCards);
    }

    public static Predicate<PlayerSession> doesNotHavePermission(String basePermission, String subPermission, String fullPermission, boolean checkWildCards) {
        return ps -> !ps.hasPermission(basePermission, subPermission, fullPermission, checkWildCards);
    }

    public static Predicate<PlayerSession> isListed(Collection collection) {
        return collection::contains;
    }

    public static Predicate<PlayerSession> isNotListed(Collection collection) {
        return ps -> !collection.contains(ps);
    }

    public static Predicate<PlayerSession> isListed(Map map) {
        return map::containsKey;
    }

    public static Predicate<PlayerSession> isNotListed(Map map) {
        return ps -> !map.containsKey(ps);
    }

    public static Predicate<PlayerSession> isCtxListed(Collection collection) {
        return ps -> collection.contains(ps.getCONNECTION().getCLIENT_CTX());
    }

    public static Predicate<PlayerSession> isCtxNotListed(Collection collection) {
        return ps -> !collection.contains(ps.getCONNECTION().getCLIENT_CTX());
    }

    public static Predicate<PlayerSession> isCtxListed(Map map) {
        return ps -> map.containsKey(ps.getCONNECTION().getCLIENT_CTX());
    }

    public static Predicate<PlayerSession> isCtxNotListed(Map map) {
        return ps -> !map.containsKey(ps.getCONNECTION().getCLIENT_CTX());
    }
}
