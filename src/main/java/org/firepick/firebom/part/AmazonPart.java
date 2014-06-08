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

public class AmazonPart extends HtmlPart {
  private static Pattern startPrice = Pattern.compile("\"buyingPrice\":");
  private static Pattern endPrice = Pattern.compile(",");
  //  private static Pattern startTitle = Pattern.compile("id=\"title\"\\s*class=\"[^\"]*\">", Pattern.MULTILINE);
  //  private static Pattern endTitle = Pattern.compile("\\s*<");
  private static Pattern startUnitCost = Pattern.compile("price\">\\s*\\(\\$", Pattern.MULTILINE);
  private static Pattern endUnitCost = Pattern.compile("\\s*/\\s*count");

  public AmazonPart(PartFactory partFactory, URL url, CachedUrlResolver urlResolver) {
    super(partFactory, url, urlResolver);
  }

  @Override
  protected void refreshFromRemoteContent(String content) throws IOException {
    String title = PartFactory.getInstance().scrapeText(content, startTitle, endTitle);
    if (title != null) {
      title = title.replaceAll("&amp;", "&");
      String [] phrases = title.split(":");
      for (String phrase: phrases) {
        if (phrase.contains("Amazon.com")) {
          continue;
        } else if (getTitle() == null) {
          setTitle(phrase);
        } else {
          setTitleCategory(phrase);
        }
      }
    }
    String price = PartFactory.getInstance().scrapeText(content, startPrice, endPrice);
    if (price != null) {
      setPackageCost(Double.parseDouble(price));
    }
    String unitCostStr = PartFactory.getInstance().scrapeText(content, startUnitCost, endUnitCost);
    if (unitCostStr != null) {
      try {
//                double unitCost = Double.parseDouble(unitCostStr);
//                long units = PartFactory.estimateQuantity(getPackageCost(), unitCost);
//                setPackageUnits((double) units);
      }
      catch (Exception e) {
        // ignore
      }
    }
    String[] urlTokens = getUrl().toString().split("/");
    String id;
    switch (urlTokens.length) {
      case 5:
        id = urlTokens[4];
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
