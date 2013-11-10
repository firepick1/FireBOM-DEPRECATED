package org.firepick.relation;
/*
   FixedWidthFormat.java
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

import java.text.*;

public class FixedWidthFormat extends Format {
    private Format format;
    private int width;

    public FixedWidthFormat(int width, Format format) {
        this.format = format;
        this.setWidth(width);
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        int length = toAppendTo.length();
        if (obj instanceof String) {
            toAppendTo.append(obj);
        } else if (obj == null) {
            toAppendTo.append("null");
        } else if (format != null) {
                format.format(obj, toAppendTo, pos);
        } else {
            toAppendTo.append(obj.toString());
        }
        int padding = getWidth() - (toAppendTo.length() - length);
        for (int iPad = 0; iPad < padding; iPad++) {
//            if (format instanceof NumberFormat) {
//                toAppendTo.insert(0, "\u2007");
//            } else {
                toAppendTo.insert(0, " ");
//            }
        }
        return toAppendTo;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return format.parseObject(source, pos);
    }

    public Format getFormat() {
        return format;
    }

    public int getWidth() {
        return width;
    }

    public FixedWidthFormat setWidth(int width) {
        this.width = width;
        return this;
    }
}
