package org.firepick.firebom;
/*
   RefereshableProxyTester.java
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RefreshableProxyTester {
    private long ageIncrement = 100;

    public long getAgeIncrement() {
        return ageIncrement;
    }

    public RefreshableProxyTester setAgeIncrement(long ageIncrement) {
        this.ageIncrement = ageIncrement;
        return this;
    }

    public RefreshableProxyTester testRefreshSuccess(IRefreshableProxy proxy) {
        testInitialProxyState(proxy);

        // Sampling has no effect on freshness
        proxy.sample();
        testInitialProxyState(proxy);

        try {
            proxy.refresh();
        }
        catch (Exception e) {
            fail(e.getMessage());
        }

        assert (proxy.isFresh());
        assert (proxy.isResolved());
        // Sampling has no effect on freshness
        proxy.sample();
        assert (proxy.isFresh());
        assert (proxy.isResolved());
        testProxyAge(proxy);

        return this;
    }

    public RefreshableProxyTester testRefreshFailure(IRefreshableProxy proxy) {
        // Initial proxy state
        testInitialProxyState(proxy);

        // Sampling has no effect on freshness
        proxy.sample();
        testInitialProxyState(proxy);

        long ageBefore = proxy.getAge();
        try {
            proxy.refresh();
            fail("Expected refresh failure");
        }
        catch (Exception e) {
            assert (e instanceof ProxyResolutionException);
        }

        try {
            Thread.sleep(getAgeIncrement());
        }
        catch (InterruptedException e) {
            fail();
        }
        assert(proxy.getAge() > ageBefore);
        assert (proxy.isFresh());
        assert (proxy.isResolved());

        // Sampling has no effect on freshness
        proxy.sample();
        assert (proxy.isFresh());
        assert (proxy.isResolved());

        // proxy ages
        testProxyAge(proxy);
        return this;
    }

    private void testInitialProxyState(IRefreshableProxy proxy) {
        // Initial proxy state
        assert (!proxy.isFresh());
        assert (!proxy.isResolved());
    }

    private void testProxyAge(IRefreshableProxy proxy) {
        // proxy ages
        long ageBefore = proxy.getAge();
        try {
            Thread.sleep(getAgeIncrement());
        }
        catch (InterruptedException e) {
            fail(e.getMessage());
        }

        double expectedAgeChange =  (double) ageIncrement;
        double actualAgeChange = (double) proxy.getAge() - ageBefore;
        assertEquals(expectedAgeChange, actualAgeChange, ageIncrement/5);
    }


}
