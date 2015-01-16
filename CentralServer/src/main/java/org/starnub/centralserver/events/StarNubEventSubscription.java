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
//import starnubserver.StarNub;
//import utilities.starnubdata.events.EventSubscription;
//import utilities.starnubdata.events.types.Event;
//
///**
// * Represents StarNubs StarNubEventSubscription that can self register or be manually registered with the
// * {@link starnubserver.starnubdata.events.starnub.StarNubE555555555ventRouter}
// *
// * @author Daniel (Underbalanced) (www.StarNub.org)
// * @since 1.0 Beta
// */
//public class StarNubEventSubscription extends EventSubscription<Event<String>> {
//
//
//    private final String EVENT_KEY;
//
//    /**
//     * Recommended: For Plugin Developers & Anyone else.
//     * <p>
//     * Uses: This will set up an events subscription but it will not be registered, you must invoke register
//     *
//     * @param SUBSCRIBER_NAME String representing the subscribers name
//     * @param EVENT_KEY Class extending Packet which represents the Packet Event Key
//     * @param EVENT_HANDLER EventHandler representing the events handler that will do some logic you choose to write
//     */
//    public StarNubEventSubscription(String SUBSCRIBER_NAME, String EVENT_KEY, StarNubEventHandler<Event<String>> EVENT_HANDLER) {
//        super(SUBSCRIBER_NAME, EVENT_HANDLER);
//        this.EVENT_KEY = EVENT_KEY;
//    }
//
//    /**
//     * Recommended: For Plugin Developers & Anyone else.
//     * <p>
//     * Uses: This will set up an events subscription and register it if you supplied a true boolean for the value register
//     *
//     * @param SUBSCRIBER_NAME String representing the subscribers name
//     * @param EVENT_KEY Class extending Packet which represents the Packet Event Key
//     * @param register boolean do you want to auto register this events
//     * @param EVENT_HANDLER EventHandler representing the events handler that will do some logic you choose to write
//     */
//    public StarNubEventSubscription(String SUBSCRIBER_NAME, String EVENT_KEY, boolean register, StarNubEventHandler<Event<String>> EVENT_HANDLER) {
//        super(SUBSCRIBER_NAME, EVENT_HANDLER);
//        this.EVENT_KEY = EVENT_KEY;
//        if (register) {
//            register();
//        }
//    }
//
//    /**
//     * Recommended: For Plugin Developers & Anyone else.
//     * <p>
//     * Uses: This will register this Event Subscription with the {@link starnubserver.starnubdata.events.starnub.StarNubEventRouter} - If you used auto register DO NOT USE this
//     */
//    @Override
//    public void register() {
//        StarNub.getStarNubEventRouter().registerEventSubscription(EVENT_KEY, this);
//    }
//
//    /**
//     * Recommended: For Plugin Developers & Anyone else.
//     * <p>
//     * Uses: This will remove this Event Subscription from the {@link starnubserver.starnubdata.events.starnub.StarNubEventRouter}
//     */
//    @Override
//    public void unregister() {
//        StarNub.getStarNubEventRouter().removeEventSubscription(this);
//    }
//}
