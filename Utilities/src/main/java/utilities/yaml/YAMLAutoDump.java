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

package utilities.yaml;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class YAMLAutoDump {

    private final boolean AUTO_DUMPER;
    private final int AUTO_DUMP_INTERVAL;
    private final ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR;

    /**
     * @param AUTO_DUMPER                    boolean if auto dumping is turned on
     * @param AUTO_DUMP_INTERVAL             int auto bumping in minutes
     * @param SCHEDULED_THREAD_POOL_EXECUTOR ScheduledThreadPoolExecutor of which we have scheduled a auto dumping task to
     */
    public YAMLAutoDump(boolean AUTO_DUMPER, int AUTO_DUMP_INTERVAL, ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR) {
        this.AUTO_DUMPER = AUTO_DUMPER;
        this.AUTO_DUMP_INTERVAL = AUTO_DUMP_INTERVAL;
        this.SCHEDULED_THREAD_POOL_EXECUTOR = SCHEDULED_THREAD_POOL_EXECUTOR;
    }

    public boolean isAUTO_DUMPER() {
        return AUTO_DUMPER;
    }

    public int getAUTO_DUMP_INTERVAL() {
        return AUTO_DUMP_INTERVAL;
    }

    public ScheduledThreadPoolExecutor getSCHEDULED_THREAD_POOL_EXECUTOR() {
        return SCHEDULED_THREAD_POOL_EXECUTOR;
    }

    public void scheduleAutoDumping(YAMLFile yamlFile, Map map) {
        Runnable runnable = () -> {
            try {
                yamlFile.dumpToFile(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        if (SCHEDULED_THREAD_POOL_EXECUTOR instanceof TaskManager) {
            ((TaskManager) SCHEDULED_THREAD_POOL_EXECUTOR).scheduleWithFixedDelayRepeatingTask(yamlFile.getOWNER(), "YAML Wrapper - Auto Dumper - File: " + yamlFile.getFILE_NAME(), runnable, 1, AUTO_DUMP_INTERVAL, TimeUnit.MINUTES);
        } else {
            SCHEDULED_THREAD_POOL_EXECUTOR.scheduleAtFixedRate(runnable, 1, AUTO_DUMP_INTERVAL, TimeUnit.MINUTES);
        }
    }
}
