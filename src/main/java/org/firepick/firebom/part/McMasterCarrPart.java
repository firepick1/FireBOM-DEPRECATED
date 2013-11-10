package org.firepick.firebom.part;
/*
   McMasterCarrPart.java
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

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class McMasterCarrPart extends Part {
    private static Pattern startId = Pattern.compile("^");
    private static Pattern endId = Pattern.compile("[^0-9a-zA-Z]|$");
    private static Pattern startPrice = Pattern.compile("\"PrceTxt\":\"");
    private static Pattern endPrice = Pattern.compile("\"");
    private static Pattern startPackageUnits = Pattern.compile("\"SellStdPkgQty\":");
    private static Pattern endPackageUnits = Pattern.compile(",");
    private static Pattern startDetail = Pattern.compile("data-mcm-attr-comp-itm-ids=\"");
    private static Pattern endDetail = Pattern.compile("\"");
    private static Pattern startDetailItem = Pattern.compile("#");
    private static Pattern endDetailIndex = Pattern.compile("$");
    private static Pattern startDetailPrice = Pattern.compile("\"PrceTxt\":\"\\$");
    private static Pattern endDetailPrice = Pattern.compile("[^0-9.]*\"");
    private static String userDataUrl = "http://www.mcmaster.com/UserData.aspx";
    private static String queryUrlTemplate =
            "http://www.mcmaster.com/WebParts/Ordering/InLnOrdWebPart/InLnOrdWebPart.aspx?cntnridtxt=InLnOrd_ItmBxRw_1_{PART}&partnbrtxt={PART}&multipartnbrind=false&partnbrslctdmsgcntxtnm=FullPrsnttn&autoslctdind=false";
    private static String detailQueryTemplate =
            "http://www.mcmaster.com/WebParts/Content/ItmPrsnttnWebPart.aspx?partnbrtxt={PART}&attrnm=&attrval=&attrcompitmids=&cntnridtxt=MainContent&proddtllnkclickedInd=true&cntnrWdth=1188";
    private static String detailPriceQueryTemplate =
            "http://www.mcmaster.com/WebParts/Content/ItmPrsnttnDynamicDat.aspx?acttxt=dynamicdat&partnbrtxt={PART}&isinlnspec=true&attrCompIds={DETAIL}";

    public McMasterCarrPart(PartFactory partFactory, URL url, CachedUrlResolver urlResolver) {
        super(partFactory, url, urlResolver);
    }

    @Override
    public URL normalizeUrl(URL url) {
        String normalizedUrl = url.toString().replaceAll("/=[a-zA-Z0-9]*", "");
        try {
            return new URL(normalizedUrl);
        }
        catch (MalformedURLException e) {
            throw new ProxyResolutionException(url.toString(), e);
        }
    }

    @Override
    protected void refreshFromRemote() throws IOException {
        CachedUrlResolver urlResolver = new CachedUrlResolver();
        String urlRef = getUrl().getRef();
        String partNum = PartFactory.getInstance().scrapeText(urlRef, startId, endId).toUpperCase();
        String queryUrl = queryUrlTemplate.replaceAll("\\{PART\\}", partNum);
        String price = null;
        String queryContent = urlResolver.get(new URL(queryUrl));
        price = PartFactory.getInstance().scrapeText(queryContent, startPrice, endPrice);
        if (price != null && price.length() == 0) {
            HttpURLConnection conn = (HttpURLConnection) new URL(userDataUrl).openConnection();
            conn.connect();
            Map<String, List<String>> headerFields = conn.getHeaderFields();
            List<String> setCookies = headerFields.get("Set-Cookie");
            String cookies = "";
            for (String setCookie: setCookies) {
                cookies += setCookie.split(";")[0] + ";";
            }
            String detailUrl = detailQueryTemplate.replaceAll("\\{PART\\}", partNum);
            urlResolver.setBasicAuth("firebom@firepick.org", "McSecret123");
            urlResolver.setCookies(cookies);
            String detailContent = urlResolver.get(new URL(detailUrl));
            String [] details = PartFactory.getInstance().scrapeText(detailContent, startDetail, endDetail).split(",");
            String detailItemString = PartFactory.getInstance().scrapeText(urlRef, startDetailItem, endDetailIndex);
            int detailItem = 0;
            try {
                detailItem = Integer.parseInt(detailItemString) - 1;
            } catch (NumberFormatException e) {
                // do nothing
            }
            String detailPriceUrl = detailPriceQueryTemplate.replaceAll("\\{DETAIL\\}", details[detailItem]).replaceAll("\\{PART\\}", partNum);
            urlResolver.setCookies(cookies);
            String detailPriceContent = urlResolver.get(new URL(detailPriceUrl));
            price = PartFactory.getInstance().scrapeText(detailPriceContent, startDetailPrice, endDetailPrice);
        }
        if (price != null) {
            setPackageCost(Double.parseDouble(price));
        }
        String packageUnits = PartFactory.getInstance().scrapeText(queryContent, startPackageUnits, endPackageUnits);
        if (packageUnits != null) {
            double value = Double.parseDouble(packageUnits);
            if (value == 0) {
                value = 1;
            }
            setPackageUnits(value);
        }

        setId(partNum);
    }
}
