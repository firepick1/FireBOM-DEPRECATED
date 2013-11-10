package org.firepick.firebom.part;
/*
   InventablesPart.java
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

public class InventablesPart extends Part {
    private static Pattern startId = Pattern.compile("<label for=\"sample_cart_item_sample_id_[0-9]*\">");
    private static Pattern endId = Pattern.compile("</label>");
    private static Pattern startPrice = Pattern.compile("<td>\\$");
    private static Pattern endPrice = Pattern.compile("</td>");
    private static Pattern startTitle = Pattern.compile("</label></td>\\s*<td>", Pattern.MULTILINE);
    private static Pattern endTitle = Pattern.compile("</td>");

    public InventablesPart(PartFactory partFactory, URL url, CachedUrlResolver urlResolver) {
        super(partFactory, url, urlResolver);
    }

    @Override
    protected void refreshFromRemoteContent(String content) throws IOException {
        String [] urlTokens = getUrl().toString().split("#");
        String searchContent = content;
        if (urlTokens.length > 1) {
            searchContent = content.split(urlTokens[1])[1];
        }
        String price = PartFactory.getInstance().scrapeText(searchContent, startPrice, endPrice);
        if (price != null) {
            setPackageCost(Double.parseDouble(price));
        }
        String id = PartFactory.getInstance().scrapeText(searchContent, startId, endId);
        if (id != null) {
            setId(id);
        }
        String title = PartFactory.getInstance().scrapeText(searchContent, startTitle, endTitle);
        if (title != null) {
            if (urlTokens.length > 1) {
                String [] urlPath = getUrl().getPath().split("/");
                title = urlPath[urlPath.length-1].toUpperCase() + " " + title;
            }
            setTitle(title);
        }
    }
}
