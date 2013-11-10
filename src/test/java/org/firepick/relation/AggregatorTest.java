package org.firepick.relation;
/*
   AggregatorTest.java
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AggregatorTest {
    @Test
    public void testDoubleAggregator() {
        DoubleAggregator aggAverage = new DoubleAggregator(NumericAggregationType.AVERAGE);
        DoubleAggregator aggMin = new DoubleAggregator(NumericAggregationType.MIN);
        DoubleAggregator aggMax = new DoubleAggregator(NumericAggregationType.MAX);
        DoubleAggregator aggCount = new DoubleAggregator(NumericAggregationType.COUNT);
        DoubleAggregator aggSum = new DoubleAggregator(NumericAggregationType.SUM);

        assertEquals(0d, aggAverage.getAggregate(), 0);
        assertEquals(Double.NaN, aggMin.getAggregate(), 0);
        assertEquals(Double.NaN, aggMax.getAggregate(), 0);
        assertEquals(0d, aggCount.getAggregate(), 0);
        assertEquals(0d, aggSum.getAggregate(), 0);

        for (double d = 1.0; d < 5.0; d += 1.0) {
            assertEquals((int) d, aggAverage.aggregate(d).getCount());
            assertEquals((int) d, aggMin.aggregate(d).getCount());
            assertEquals((int) d, aggMax.aggregate(d).getCount());
            assertEquals((int) d, aggCount.aggregate(d).getCount());
            assertEquals((int) d, aggSum.aggregate(d).getCount());
        }

        assertEquals(2.5d, aggAverage.getAggregate(), 0);
        assertEquals(1d, aggMin.getAggregate(), 0);
        assertEquals(4d, aggMax.getAggregate(), 0);
        assertEquals(4d, aggCount.getAggregate(), 0);
        assertEquals(10d, aggSum.getAggregate(), 0);
    }
}
