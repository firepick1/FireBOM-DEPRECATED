package org.firepick.firebom;
/*
   IOExceptionPartFactory.java
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

import org.firepick.firebom.part.PartFactory;

import java.io.IOException;
import java.net.URL;

public class IOExceptionPartFactory extends PartFactory {
    private boolean isAvailable;

    @Override
    public String urlTextContent(URL url) throws IOException {
        if (isAvailable) {
            return super.urlTextContent(url);
        }
        throw new IOException("test exception");
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public IOExceptionPartFactory setAvailable(boolean available) {
        isAvailable = available;
        return this;
    }
}
