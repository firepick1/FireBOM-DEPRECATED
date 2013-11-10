package org.firepick.relation;
/*
   TextFormat.java
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

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class TextFormat extends Format {
    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        toAppendTo.append(obj.toString());
        return toAppendTo;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return source;
    }
}
