package org.firepick.firebom.part;
/*
   PonokoPart.java
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

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

public class PonokoPart extends Part {
    private static Pattern startPrice = Pattern.compile("class=\"price\">\\$");
    private static Pattern endPrice = Pattern.compile("<");

    public PonokoPart(PartFactory partFactory, URL url, CachedUrlResolver urlResolver)  {
        super(partFactory, url, urlResolver);
    }

    @Override
    protected void refreshFromRemoteContent(String content) throws IOException {
        String price = PartFactory.getInstance().scrapeText(content, startPrice, endPrice);
        if (price != null) {
            setPackageCost(Double.parseDouble(price));
        }
        String tokens[] = getUrl().getPath().split("/");
        setId(tokens[tokens.length-1]);
    }

}
