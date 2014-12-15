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

package starnubserver.events.events;

import starnubserver.events.starnub.StarNubEventRouter;

public class StarNubEventTwo extends StarNubEvent{

    private final Object EVENT_DATA_2;

    public StarNubEventTwo(Object EVENT_KEY, Object EVENT_DATA, Object EVENT_DATA_2) {
        super(EVENT_KEY, EVENT_DATA);
        this.EVENT_DATA_2 = EVENT_DATA_2;
        StarNubEventRouter.getInstance().eventNotify(this);
    }

    public Object getEVENT_DATA_2() {
        return EVENT_DATA_2;
    }
}
