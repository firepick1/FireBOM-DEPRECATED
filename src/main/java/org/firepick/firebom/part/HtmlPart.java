package org.firepick.firebom.part;
/*
   HtmlPart.java
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HtmlPart extends Part {
    protected static final Pattern startTitle = Pattern.compile("<title>");
    protected static final Pattern endTitle = Pattern.compile("</title>");

    public HtmlPart(PartFactory partFactory, URL url, CachedUrlResolver urlResolver) {
        super(partFactory, url, urlResolver);
    }

    @Override
    protected void refreshFromRemoteContent(String content) throws IOException {
        setId("UNSUPPORTED");
        setTitle("Unsupported FireBOM vendor http://bit.ly/16jPAOr");
        String[] ulParts = content.split("</ul>");
        List<String> newSourceList = null;
        PartUsage newSourcePartUsage = null;
        List<PartUsage> newRequiredParts = null;
        for (String ulPart : ulParts) {
            if (ulPart.contains("@Source")) {
                newSourceList = parseListItemStrings(ulPart);
                if (newSourceList.size() == 0) {
                    throw new ProxyResolutionException("Html page has no @Sources tag");
                }
                String primarySource = newSourceList.get(0);
                URL sourceUrl = parseLink(primarySource);
                Part sourcePart = PartFactory.getInstance().createPart(sourceUrl);
                Double quantity = parseQuantity(primarySource, null);
                if (quantity != null) {
                    // Package Unit Override
                    // ======================
                    // If source package units are specified, they apply to the source, which may not have the ability to
                    // specify the package units. Using the source package unit override automatically forces a
                    // package unit of 1 for THIS part. This convention permits the simplest specification of required parts
                    // (i.e., per single source unit).
                    newSourcePartUsage = new PartUsage(sourcePart, quantity);
                    this.setPackageUnits(1d);
                } else {
                    newSourcePartUsage = new PartUsage(sourcePart, 1);
                }
            } else if (ulPart.contains("@Require")) {
                List<String> requiredItems = parseListItemStrings(ulPart);
                newRequiredParts = new ArrayList<PartUsage>();
                for (String required : requiredItems) {
                    try {
                        URL link = parseLink(required);
                        double quantity = parseQuantity(required, 1d);
                        Part part = PartFactory.getInstance().createPart(link);
                        PartUsage partUsage = new PartUsage(part, quantity);
                        newRequiredParts.add(partUsage);
                    }
                    catch (MalformedURLException ex) {
                        if (required.startsWith("http")) {
                            throw ex;
                        } else {
                            // skip this part
                        }
                    }
                }
            }
        }

        if (newRequiredParts != null) {
            requiredParts = newRequiredParts;
        }
        if (newSourcePartUsage != null) {
            sourceList = newSourceList;
            setSourcePartUsage(newSourcePartUsage);
        }
    }

}


