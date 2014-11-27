/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This utilities.file is part of org.starnub a Java Wrapper for Starbound.
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

package starbounddata.vectors;

import io.netty.buffer.ByteBuf;
import starbounddata.variants.VLQ;

import java.util.ArrayList;

/**
 * Represents a Vec2I Array which contains 0-SomeNumber of 2 dimensional integer vector of (x, y)
 * <p/>
 * Note: This Array is not a synchronized collection. This should not make a deference as only one thread should be handling this packet at a time
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Vec2IArray extends ArrayList<Vec2I> {

    /**
     * @param in ByteBuf data to be read into the Vec2I Array. 100 is set as a cap for data to prevent attacks against the starnub. This is still a sizable area
     */
    public Vec2IArray(ByteBuf in) throws ArrayIndexOutOfBoundsException{
        int arrayLength = VLQ.readUnsignedFromBufferNoObject(in);
        if (arrayLength > 100) {
            throw new ArrayIndexOutOfBoundsException();
        }
        for (int i = 0; i < arrayLength; i++) {
            this.add(new Vec2I(in));
        }
    }

    /**
     * @param out ByteBuf out representing a {@link io.netty.buffer.ByteBuf} to write this Vec2IArray to
     */
    public void writeVec2IArray(ByteBuf out) {
        VLQ.writeVLQNoObject(out, (long) this.size());
        for (Vec2I vec2I : this) {
            vec2I.writeVec2I(out);
        }
    }
}
