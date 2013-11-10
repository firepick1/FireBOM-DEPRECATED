package org.firepick.firebom.bom;
/*
   BOMColumnDescription.java
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

import org.firepick.relation.*;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;

public class BOMColumnDescription<T> extends ColumnDescription<T> {

    private BOMColumnDescription(int index, String id, String title, int width, Format format, IAggregator<T> aggregator) {
        setIndex(index).setAggregator(aggregator).setId(id).setTitle(title).setFormat(format).setWidth(width);
    }

    public static BOMColumnDescription create(BOMColumn column) {
        switch (column) {
            case ID:
                return new BOMColumnDescription<String>(column.ordinal(), "id", "ID", 4, new TextFormat(), new CountingAggregator());
            case TITLE:
                return new BOMColumnDescription<String>(column.ordinal(), "title", "TITLE", 0, null, new StringAggregator("TOTAL"));
            case QUANTITY:
                return new BOMColumnDescription<Double>(column.ordinal(), "qty", "QTY", 3, new DecimalFormat(), new DoubleAggregator(NumericAggregationType.SUM));
            case COST:
                return new BOMColumnDescription<Double>(column.ordinal(), "cost", "COST", 9, NumberFormat.getCurrencyInstance(), new DoubleAggregator(NumericAggregationType.SUM));
            case VENDOR:
                return new BOMColumnDescription<String>(column.ordinal(), "vendor", "VENDOR", 20, new TextFormat(), new StringAggregator("TOTAL"));
            case URL:
                return new BOMColumnDescription<String>(column.ordinal(), "url", "URL", 0, null, new StringAggregator("TOTAL"));
            case PROJECT:
                return new BOMColumnDescription<String>(column.ordinal(), "project", "PROJECT", 10, new TextFormat(), new StringAggregator("TOTAL"));
            case SOURCE:
                return new BOMColumnDescription<String>(column.ordinal(), "source", "SOURCE", 0, null, new StringAggregator("TOTAL"));
        }

        throw new RuntimeException("Unknown BOMColumn " + column);
    }

}
