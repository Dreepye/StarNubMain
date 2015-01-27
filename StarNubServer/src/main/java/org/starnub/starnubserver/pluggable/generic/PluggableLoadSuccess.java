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

package org.starnub.starnubserver.pluggable.generic;

import org.starnub.starnubserver.pluggable.Pluggable;
import org.starnub.starnubserver.pluggable.Plugin;
import org.starnub.starnubserver.pluggable.UnloadedPluggable;

public class PluggableLoadSuccess {

    private UnloadedPluggable UNLOADED_PLUGGABLE;
    private Pluggable PLUGGABLE;
    private final boolean SUCCESS;
    private final String REASON;

    public PluggableLoadSuccess(UnloadedPluggable UNLOADED_PLUGGABLE, boolean SUCCESS, String REASON) {
        this.UNLOADED_PLUGGABLE = UNLOADED_PLUGGABLE;
        this.SUCCESS = SUCCESS;
        this.REASON = REASON;
    }

    public PluggableLoadSuccess(Pluggable PLUGGABLE, boolean SUCCESS, String REASON) {
        this.PLUGGABLE = PLUGGABLE;
        this.SUCCESS = SUCCESS;
        this.REASON = REASON;
    }

    public boolean isSUCCESS() {
        return SUCCESS;
    }

    public String getREASON() {
        return REASON;
    }

    public String getNameVersion(){
        if(PLUGGABLE != null){
            return PLUGGABLE.getDetails().getNameVersion();
        } else {
            return UNLOADED_PLUGGABLE.getDetails().getNameVersion();
        }
    }

    /**
     * For Plugins only
     *
     * @return
     */
    public boolean isEnabled(){
        return ((Plugin)PLUGGABLE).isEnabled();
    }
}
