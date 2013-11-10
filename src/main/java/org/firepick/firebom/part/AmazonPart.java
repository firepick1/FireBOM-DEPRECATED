package org.firepick.firebom.part;
/*
   AmazonPart.java
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

import org.firepick.firebom.exception.ProxyResolutionException;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

public class AmazonPart extends Part {
    private static Pattern startPrice = Pattern.compile("priceLarge\">\\$");
    private static Pattern endPrice = Pattern.compile("<");
    private static Pattern startTitle = Pattern.compile("AsinTitle\"\\s*>");
    private static Pattern endTitle = Pattern.compile("</");
    private static Pattern startUnitCost = Pattern.compile("actualPriceExtraMessaging\">\\s*<span class=\"pricePerUnit\">\\(\\$", Pattern.MULTILINE);
    private static Pattern endUnitCost = Pattern.compile("/\\s?count");

    public AmazonPart(PartFactory partFactory, URL url, CachedUrlResolver urlResolver)  {
        super(partFactory, url, urlResolver);
    }

    @Override
    protected void refreshFromRemoteContent(String content) throws IOException {
        String title = PartFactory.getInstance().scrapeText(content, startTitle, endTitle);
        if (title != null) {
            title = title.replaceAll("Amazon.com:\\s?", "");
            setTitle(title);
        }
        String price = PartFactory.getInstance().scrapeText(content, startPrice, endPrice);
        if (price != null) {
            setPackageCost(Double.parseDouble(price));
        }
        String unitCostStr = PartFactory.getInstance().scrapeText(content, startUnitCost, endUnitCost);
        if (unitCostStr != null) {
            try {
                double unitCost = Double.parseDouble(unitCostStr);
                long units = PartFactory.estimateQuantity(getPackageCost(), unitCost);
                setPackageUnits((double) units);
            } catch (Exception e) {
                // ignore
            }
        }
        String [] urlTokens = getUrl().toString().split("/");
        String id;
        switch (urlTokens.length) {
            case 5: id = urlTokens[4];
                break;
            case 6:
            case 7:
                id = urlTokens[5];
                break;
            default:
                throw new ProxyResolutionException("Could not parse www.amazon.com url: " + getUrl());
        }
        if (id != null) {
            setId(id);
        }
    }

}
