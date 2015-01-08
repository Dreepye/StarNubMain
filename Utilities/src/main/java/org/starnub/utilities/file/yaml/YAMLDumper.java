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

package org.starnub.utilities.file.yaml;

import org.yaml.snakeyaml.DumperOptions;


/**
 * Represents a YAMLDumper this will be used to set dumping options and
 * auto dumper for files
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class YAMLDumper {

    private final YAMLAutoDump AUTO_DUMPER;
    private final boolean DUMP_ON_MODIFICATION;
    private final DumperOptions DUMPER_OPTIONS;

    /**
     * @param AUTO_DUMPER          YAMLAutoDump representing the auto dumper class
     * @param DUMP_ON_MODIFICATION boolean is dump on modification turned on
     */
    public YAMLDumper(YAMLAutoDump AUTO_DUMPER, boolean DUMP_ON_MODIFICATION) {
        this.AUTO_DUMPER = AUTO_DUMPER;
        this.DUMP_ON_MODIFICATION = DUMP_ON_MODIFICATION;
        this.DUMPER_OPTIONS = new DumperOptions();
        DUMPER_OPTIONS.setPrettyFlow(true);
        DUMPER_OPTIONS.setAllowUnicode(true);
    }

    public YAMLAutoDump getAUTO_DUMPER() {
        return AUTO_DUMPER;
    }

    public boolean isDUMP_ON_MODIFICATION() {
        return DUMP_ON_MODIFICATION;
    }

    public DumperOptions getDUMPER_OPTIONS() {
        return DUMPER_OPTIONS;
    }
}
