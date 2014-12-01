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
import java.util.HashSet;

/**
 * Represents a Vec2I Array which contains 0-SomeNumber of 2 dimensional integer vector of (x, y)
 * <p/>
 * Note: This Array is not a synchronized collection. This should not make a deference as only one thread should be handling this packet at a time
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Vec2IArray extends ArrayList<Vec2I> {

    public Vec2IArray() {
        super();
    }

    /**
     * This will create a vector array using the two vectors provided. In short this will provide a fill or remove method
     * anything between vector 1 and vector 2 will be added or removed.
     *
     * @param vector1 Vec2I representing the first vector that you want to start your array
     * @param vector2 Vec2I representing the second vector that you want to end your array
     * @throws ArrayIndexOutOfBoundsException
     */
    public Vec2IArray(Vec2I vector1, Vec2I vector2) throws ArrayIndexOutOfBoundsException{
        int lowX = vector1.getX() > vector2.getX() ? vector2.getX() : vector1.getX();
        int highX = vector1.getX() > vector2.getX() ? vector1.getX() : vector2.getX();
        int lowY = vector1.getY() > vector2.getY() ? vector2.getY() : vector1.getY();
        int highY = vector1.getY() > vector2.getY() ? vector1.getY() : vector2.getY();
        while (lowX <= highX){
            int tempY = lowY;
            while(tempY <= highY){
                this.add(new Vec2I(lowX, tempY));
                tempY++;
            }
            lowX++;
        }
    }

    /**
     * @param in ByteBuf data to be read into the Vec2I Array. 100 is set as a cap for data to prevent attacks against the starnubserver. This is still a sizable area
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

    public HashSet<Vec2IArray> getVec2IGroups(int groupSize){
        HashSet<Vec2IArray> groups = new HashSet<>();
        while (this.size() > 0){
            Vec2IArray group = new Vec2IArray();
            while (group.size() < groupSize && this.size() > 0){
                group.add(this.remove(0));
            }
            groups.add(group);
        }
        return groups;
    }
}
