package org.firepick.firebom;
/*
   RefreshableTimer.java
   Copyright (C) 2013 Karl Lew <karl@firepick.org>. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import java.io.Serializable;

/**
 * An exponentially averaged timer for implementing refreshable proxies.
 * Timer adapts the expected refresh interval based on past refresh() and sample() calls.
 */
public class RefreshableTimer implements IRefreshableProxy, Serializable {
    private long minRefreshInterval;
    private long lastRefreshMillis;
    private long lastSampleMillis;
    private double sensitivity;
    private boolean isResolved;
    private long samplesSinceRefresh;
    private long sampleInterval;

    public RefreshableTimer() {
        this(0.8d);
    }

    public RefreshableTimer(double sensitivity) {
        if (sensitivity < 0 || 1 < sensitivity) {
            throw new IllegalArgumentException("sensitivity must be between [0..1]");
        }
        this.sensitivity = sensitivity;
        this.lastRefreshMillis = System.currentTimeMillis();
        this.lastSampleMillis = lastRefreshMillis;
    }

    public void refresh() {
        lastRefreshMillis = System.currentTimeMillis();
        samplesSinceRefresh = 0;
        isResolved = true;
    }

    public void sample() {
        samplesSinceRefresh++;
        long nowMillis = System.currentTimeMillis();
        long msElapsed = nowMillis - lastSampleMillis;
        if (isResolved()) {
            sampleInterval =(long)(getSensitivity() * msElapsed + (1 - getSensitivity()) * sampleInterval);
        } else {
            sampleInterval = Math.max(1, msElapsed);
        }
        lastSampleMillis = nowMillis;
    }

    public boolean isFresh() {
        long refreshInterval = getRefreshInterval();
        long ageDiff = refreshInterval - getAge();
        return isResolved() && ageDiff >= 0;
    }

    public double getSensitivity() {
        return sensitivity;
    }

    public long getSamplesSinceRefresh() {
        return samplesSinceRefresh;
    }

    public boolean isResolved() {
        return isResolved;
    }

    protected RefreshableTimer setResolved(boolean value) {
        isResolved = value;
        return this;
    }

    public long getAge() {
        return System.currentTimeMillis() - lastRefreshMillis;
    }

    public long getRefreshInterval() {
        Long value  = getSampleInterval();
        return Math.max(getMinRefreshInterval(), value);
    }

    public long getSampleInterval() {
        return sampleInterval;
    }

    public long getMinRefreshInterval() {
        return minRefreshInterval;
    }

    public RefreshableTimer setMinRefreshInterval(long minRefreshInterval) {
        this.minRefreshInterval = minRefreshInterval;
        return this;
    }
}
