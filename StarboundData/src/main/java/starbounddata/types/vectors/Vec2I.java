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

package starbounddata.types.vectors;

import io.netty.buffer.ByteBuf;
import starbounddata.types.SbData;

/**
 * Represents a 2 dimensional integer vector of (x, y)
 * <p>
 * Starbound 1.0 Compliant (Versions 622, Update 1)
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Vec2I extends SbData<Vec2I> implements VecI<VecI>{

    private int x;
    private int y;

    public Vec2I() {
    }

    public Vec2I(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2I(ByteBuf in) {
        super(in);
    }

    public Vec2I(Vec2I vec2I) {
        this.x = vec2I.getX();
        this.y = vec2I.getY();
    }

    public Vec2I(float x, float y){
        this.x = Math.round(x);
        this.y = Math.round(y);
    }

    public Vec2I(Vec2F vec2F) {
        this.x = Math.round(vec2F.getX());
        this.y = Math.round(vec2F.getY());
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void read(ByteBuf in) {
        this.x = in.readInt();
        this.y = in.readInt();
    }

    @Override
    public void write(ByteBuf out) {
        out.writeInt(this.x);
        out.writeInt(this.y);
    }

    @Override
    public String toString() {
        return "Vec2I{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
