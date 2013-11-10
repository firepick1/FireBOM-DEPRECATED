package org.firepick.relation;
/*
   ColumnDescription.java
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

import java.text.Format;

public class ColumnDescription<T> implements IColumnDescription<T> {
    private String id;
    private String title;
    private FixedWidthFormat format;
    private int index;
    private IAggregator<T> aggregator;

    public String getId() {
        return id;
    }

    public ColumnDescription setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ColumnDescription setTitle(String title) {
        this.title = title;
        return this;
    }

    public Format getFormat() {
        return format;
    }

    public ColumnDescription setFormat(Format format) {
        this.format = new FixedWidthFormat(0, format);
        return this;
    }

    public int getItemIndex() {
        return index;
    }

    public ColumnDescription setIndex(int index) {
        this.index = index;
        return this;
    }

    public IAggregator<T> getAggregator() {
        return aggregator;
    }

    public ColumnDescription setAggregator(IAggregator<T> aggregator) {
        this.aggregator = aggregator;
        return this;
    }

    public int getWidth() {
        return format == null ? 0 : format.getWidth();
    }

    public ColumnDescription setWidth(int width) {
        format.setWidth(width);
        return this;
    }

    @Override
    public String toString() {
        return getId();
    }
}
