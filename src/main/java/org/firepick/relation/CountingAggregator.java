package org.firepick.relation;
/*
   CountinAggregator.java
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

public class CountingAggregator<T> implements IAggregator<Integer> {
    private int count ;

    @Override
    public CountingAggregator aggregate(Object value) {
        count++;
        return this;
    }

    @Override
    public void clear() {
        count = 0;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public Integer getAggregate() {
        return count;
    }
}
