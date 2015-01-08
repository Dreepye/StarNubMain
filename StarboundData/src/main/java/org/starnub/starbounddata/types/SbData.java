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

package org.starnub.starbounddata.types;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class SbData<T> implements SbDataInterface<T> {

    public SbData() {
    }

    public SbData(ByteBuf in){
        read(in);
    }

    public abstract void read(ByteBuf in);
    public abstract void write(ByteBuf out);

    public T copy(){
        Class<? extends SbData> clazz = this.getClass();
        try {
            Constructor constructor = clazz.getConstructor(clazz);
            return (T) constructor.newInstance(this);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace(); /* THIS SHOULD NEVER HAPPEN */
        }
        return null;
    }
}
