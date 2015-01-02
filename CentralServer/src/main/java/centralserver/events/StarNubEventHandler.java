///*
// * Copyright (C) 2014 www.StarNub.org - Underbalanced
// *
// * This file is part of org.starnub a Java Wrapper for Starbound.
// *
// * This above mentioned StarNub software is free software:
// * you can redistribute it and/or modify it under the terms
// * of the GNU General Public License as published by the Free
// * Software Foundation, either version  3 of the License, or
// * any later version. This above mentioned CodeHome software
// * is distributed in the hope that it will be useful, but
// * WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
// * the GNU General Public License for more details. You should
// * have received a copy of the GNU General Public License in
// * this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package centralserver.starnubdata.events;
//
//import utilities.starnubdata.events.EventHandler;
//import utilities.starnubdata.events.types.Event;
//
///**
// * Represents StarNubs StarNubEventHandler that handles {@link utilities.starnubdata.events.types.Event} the
// * onEvent method is overridden so that you may conduct logic with the Event
// *
// * @author Daniel (Underbalanced) (www.StarNub.org)
// * @since 1.0 Beta
// */
//public abstract class StarNubEventHandler<T> extends EventHandler<Event<String>> {
//
//    /**
//     * Recommended: For Plugin Developers & Anyone else.
//     * <p>
//     * Uses: When your {@link starnubserver.starnubdata.events.packet.PacketEventSubscription} is called this EventHandler
//     * will be called and the onEvent() method invoked.
//     *
//     * NOTE: - YOU WILL NEED TO CAST THE EVENT TO THE DATA TYPE BASED ON THE EVENT LIST OR PLUGIN EVENT LIST
//     *              {
//     *                  Player player = (Player) eventData.getEVENT_DATA();
//     *                  //Do Stuff, player.disconnect();
//     *                  return player; OR return null;
//     *              }
//     *       - YOU DO NOT HAVE TO RETURN THE EVENT.
//     *
//     * @param eventData Event representing the events being shared
//     * @return Event you are not required to return the events
//     */
//    @Override
//    public abstract void onEvent(Event<String> eventData);
//
//}
