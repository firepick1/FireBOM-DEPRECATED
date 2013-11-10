package org.firepick.firebom;
/*
   RefreshableTimerTest.java
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

import org.firepick.firebom.exception.ProxyResolutionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RefreshableTimerTest {

    @Test
    public void testRefreshableTimer() throws InterruptedException {
        new RefreshableProxyTester().testRefreshSuccess(new RefreshableTimer());
        new RefreshableProxyTester().testRefreshFailure(new MockTimer());

        RefreshableTimer timer = new RefreshableTimer();

        // At construction, the proxy is not fresh and not resolved
        assertEquals(0, timer.getRefreshInterval());
        assertEquals(0, timer.getSamplesSinceRefresh());
        assertEquals(0, timer.getAge());
        assertEquals(0, timer.getRefreshInterval());

        // default sensitivity has a half-life of about 6 refreshes
        assertEquals(0.8d, timer.getSensitivity(), 0);

        // Sampling is counted and affects sampling/refresh intervals
        Thread.sleep(10);
        timer.sample();
        assertEquals(1, timer.getSamplesSinceRefresh());
        assertEquals(10, timer.getSampleInterval());
        assertEquals(10, timer.getRefreshInterval());

        // Refresh does not affect sampling interval
        timer.refresh();
        assertEquals(0, timer.getSamplesSinceRefresh());
        assertEquals(10, timer.getRefreshInterval());
        assertEquals(10, timer.getSampleInterval());

        // Sampling affects refresh/sampling intervals and, therefore, freshness
        Thread.sleep(50);
        timer.sample();
        assertEquals(1, timer.getSamplesSinceRefresh());
        assertEquals(42, timer.getRefreshInterval());
    }

    @Test
    public void testMinRefreshInterval() {
        RefreshableTimer timer = new RefreshableTimer();

        assertEquals(0, timer.getMinRefreshInterval());
        timer.setMinRefreshInterval(100);
        assertEquals(100, timer.getMinRefreshInterval());
        assertEquals(100, timer.getRefreshInterval());

        timer.refresh();
        assert (timer.isFresh());
        assertEquals(100, timer.getMinRefreshInterval());
        assertEquals(100, timer.getRefreshInterval());
    }

    public class MockTimer extends RefreshableTimer {
        @Override
        public void refresh() {
            super.refresh();
            throw new ProxyResolutionException("test");
        }

        @Override
        public long getMinRefreshInterval() {
            return 10000;
        }
    }


}
