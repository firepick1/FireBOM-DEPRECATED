package org.firepick.firebom;
/*
   IRefreshableProxy.java
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

public interface IRefreshableProxy {

    /**
     * Synchronize proxy with remote resource
     */
    void refresh();

    /**
     * A newly constructed proxy is not fresh until it is refreshed.
     * Freshness lasts until a refresh timeout. Unsampled proxies stay fresh
     * forever.
     * @return true if proxy has been recently refreshed or never sampled
     */
    boolean isFresh();

    /**
     * Use the information provided by the proxy. Frequently sampled proxies should be
     * refreshed more often than rarely sampled proxies. Sampling a proxy affects its
     * freshness as well as the refresh interval.
     */
    void sample();

    /**
     * A resolved proxy is one that has been successfully refreshed at least once in its
     * lifetime.
     * @return
     */
    boolean isResolved();

    /**
     * Return age since last refresh or construction.
     * @return
     */
    long getAge();

}
