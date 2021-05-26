/*
 * Copyright 2018-2020 The Code Department.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tcdng.unify.core.annotation;

/**
 * Enumerates various periodic types.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public enum PeriodicType {
    /** 1 hour */
    EON(60 * 60000),

    /** 30 minutes */
    ERA(30 * 60000),

    /** 10 minutes */
    UBER_SLOW(10 * 60000),

    /** 5 minutes */
    EXTREME_SLOW(5 * 60000),

    /** 60 seconds */
    SLOWEST(60000),

    /** 30 seconds */
    SLOWER(30000),

    /** 20 seconds */
    SLOW(20000),

    /** 10 seconds */
    NORMAL(10000),

    /** 5 seconds */
    FAST(5000),

    /** 2 seconds */
    FASTER(2000),

    /** 1 second */
    FASTEST(1000),

    /** .5 seconds */
    EXTREME_FAST(500),

    /** .1 second */
    LIGHTSPEED(100);

    private final long periodInMillSec;

    private PeriodicType(long periodInMillSec) {
        this.periodInMillSec = periodInMillSec;
    }

    public long getPeriodInMillSec() {
        return periodInMillSec;
    }
}
