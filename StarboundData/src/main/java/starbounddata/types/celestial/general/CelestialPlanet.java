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

package starbounddata.types.celestial.general;

import io.netty.buffer.ByteBuf;
import starbounddata.types.SbData;

public class CelestialPlanet extends SbData<CelestialPlanet> {

    private CelestialParameters planetParameters = new CelestialParameters();
    private SatelliteParameters satelliteParameters = new SatelliteParameters();

    public CelestialPlanet() {
    }

    public CelestialPlanet(CelestialParameters planetParameters, SatelliteParameters satelliteParameters) {
        this.planetParameters = planetParameters;
        this.satelliteParameters = satelliteParameters;
    }

    public CelestialPlanet(ByteBuf in) {
        super(in);
    }

    public CelestialPlanet(CelestialPlanet celestialPlanet) {
        this.planetParameters = celestialPlanet.getPlanetParameters().copy();
        this.satelliteParameters = celestialPlanet.getSatelliteParameters().copy();
    }

    public CelestialParameters getPlanetParameters() {
        return planetParameters;
    }

    public void setPlanetParameters(CelestialParameters planetParameters) {
        this.planetParameters = planetParameters;
    }

    public SatelliteParameters getSatelliteParameters() {
        return satelliteParameters;
    }

    public void setSatelliteParameters(SatelliteParameters satelliteParameters) {
        this.satelliteParameters = satelliteParameters;
    }

    @Override
    public void read(ByteBuf in) {
        this.planetParameters.read(in);
        this.satelliteParameters.read(in);
    }

    @Override
    public void write(ByteBuf out) {
        this.planetParameters.write(out);
        this.satelliteParameters.write(out);
    }

    @Override
    public String toString() {
        return "CelestialPlanet{" +
                "planetParameters=" + planetParameters +
                ", satelliteParameters=" + satelliteParameters +
                "} " + super.toString();
    }
}
