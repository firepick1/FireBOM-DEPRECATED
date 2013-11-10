package org.firepick.relation;
/*
   DoubleAggregator.java
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

public class DoubleAggregator implements IAggregator<Double> {
    private NumericAggregationType aggregationType;
    private Double sum;
    private int count;
    private double aggregateValue;

    public DoubleAggregator(NumericAggregationType aggregationType) {
        this.aggregationType = aggregationType;
        clear();
    }

    @Override
    public void clear() {
        switch (aggregationType) {
            case MIN:
                aggregateValue = Double.MAX_VALUE;
                break;
            case MAX:
                aggregateValue = Double.MIN_VALUE;
                break;
            default:
                aggregateValue = 0d;
                sum = 0d;
                break;
        }
    }

    @Override
    public DoubleAggregator aggregate(Object that) {
        Double value = (Double) that;
        switch (aggregationType) {
            case MIN:
                if (value < aggregateValue) {
                    aggregateValue = value;
                }
                break;
            case MAX:
                if (value > aggregateValue) {
                    aggregateValue = value;
                }
                break;
            case AVERAGE:
                sum += value;
                break;
            default:
            case SUM:
                aggregateValue += value;
                break;
        }

        count++;
        return this;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public Double getAggregate() {
        switch (aggregationType) {
            case COUNT:
                return (double) getCount();
            case MIN:
            case MAX:
                return getCount() == 0 ? Double.NaN : aggregateValue;
            case AVERAGE:
                return getCount() == 0 ? 0d : sum / getCount();
            default:
                return aggregateValue;
        }
    }
}
