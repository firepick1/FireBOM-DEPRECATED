package org.firepick.firebom.part;
/*
   MisumiPart.java
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

public class MisumiPart extends Part {
    private static final String priceTemplate = "http://us.misumi-ec.com/mydesk2/s/priceCalc?part_number={PART}&quantity=1&response_type=json&SKIP_LOGIN_CHECK=1";
    private static final String packageTemplate = "http://us.misumi-ec.com/us/StaticPageWysiwygArea.html?itemCd={ITEMCD}&tabNo=1";
    private static final Pattern startId = Pattern.compile("Keyword=|PNSearch=|KWSearch=");
    private static final Pattern endId = Pattern.compile("[^a-zA-Z0-9-]|$");
    private static final Pattern startPrice = Pattern.compile("\"CATALOG_PRICE\":");
    private static final Pattern endPrice = Pattern.compile(",");
    private static final Pattern startItem = Pattern.compile("/detail/");
    private static final Pattern endItem = Pattern.compile("/");
    private static final Pattern startPackage = Pattern.compile("\\]</font>");
    private static final Pattern endPackage = Pattern.compile(" pcs. per package");

    public MisumiPart(PartFactory partFactory, URL url, CachedUrlResolver urlResolver) {
        super(partFactory, url, urlResolver);
    }
    
    @Override
    protected void refreshFromRemoteContent(String content) throws IOException {
        String item = PartFactory.getInstance().scrapeText(content, startItem, endItem);
        item = item.substring(item.length()-11);
        String id = PartFactory.getInstance().scrapeText(getUrl().toString(), startId, endId);
        setId(id);
        String priceUrl = priceTemplate.replaceAll("\\{PART\\}",id);
        String partInfo = PartFactory.getInstance().urlTextContent(new URL(priceUrl));
        String price = PartFactory.getInstance().scrapeText(partInfo, startPrice, endPrice);
        if (price != null) {
            setPackageCost(Double.parseDouble(price));
        }
        String packageUrl = packageTemplate.replaceAll("\\{ITEMCD\\}", item);
        String packageText = PartFactory.getInstance().urlTextContent(new URL(packageUrl));
        String packageUnits = PartFactory.getInstance().scrapeText(packageText, startPackage, endPackage);
        if (packageUnits != null) {
            setPackageUnits(Double.parseDouble(packageUnits));
        }
    }
}
