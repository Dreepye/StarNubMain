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

package org.starnub.starnubserver.resources.tokens;

import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.starnubserver.resources.StringTokens;

public class StringToken {

    private final String OWNER;
    private final String NAME;
    private final String TOKEN;
    private final String DESCRIPTION;
    private final TokenHandler TOKEN_HANDLER;

    public StringToken(String OWNER, String NAME, String TOKEN, String DESCRIPTION, TokenHandler TOKEN_HANDLER){
        this.OWNER = OWNER;
        this.NAME = NAME;
        this.TOKEN = tokenFormat(TOKEN);
        this.DESCRIPTION = DESCRIPTION;
        this.TOKEN_HANDLER = TOKEN_HANDLER;
        register();
    }

    public String getOWNER() {
        return OWNER;
    }

    public String getNAME() {
        return NAME;
    }

    public String getTOKEN() {
        return TOKEN;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public TokenHandler getTOKEN_HANDLER() {
        return TOKEN_HANDLER;
    }

    public void register(){
        StringTokens.getInstance().put(TOKEN, this);
        new StarNubEvent("String_Token_Registered", this);
    }

    public void unregister(){
        StringTokens.getInstance().remove(TOKEN);
        new StarNubEvent("String_Token_Unregistered", this);
    }

    private String tokenFormat(String string){
        if (!string.startsWith("{")){
            string = "{" + string;
        }
        if (!string.endsWith("}")){
            string = string + "}";
        }
        return string.toLowerCase();
    }

    @Override
    public String toString() {
        return "StringToken{" +
                "NAME='" + NAME + '\'' +
                ", TOKEN='" + TOKEN + '\'' +
                ", DESCRIPTION='" + DESCRIPTION + '\'' +
                '}';
    }
}
