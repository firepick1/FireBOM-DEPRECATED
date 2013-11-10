package org.firepick.firebom.part;
/*
   MockPart.java
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

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

public class MockPart extends Part {
    private int refreshFromRemoteCount;

    public MockPart(PartFactory partFactory, URL url, CachedUrlResolver urlResolver) {
        super(partFactory, url, urlResolver);
        refreshFromRemote();
    }

    public void refreshFromRemote() {
        refreshFromRemoteCount++;
        try {
            URL url = getUrl();
            PartFactory partFactory = PartFactory.getInstance();
            String[] pathSegments = url.getQuery().split("&");
            ArrayList<PartUsage> newRequired = new ArrayList<PartUsage>();
            for (String pathSegment : pathSegments) {
                String[] tokens = pathSegment.split(":");
                String key = tokens[0];
                String value = tokens.length > 1 ? tokens[1] : null;
                String decodedValue = URLDecoder.decode(value,"utf-8");
                double qty = 1;
                if (tokens.length > 2) {
                    qty = Double.parseDouble(tokens[2]);
                }
                if (key.equals("id")) {
                    setId(value);
                } else if (key.equals("title")) {
                    setTitle(value);
                } else if (key.equals("source")) {
                    Part sourcePart = partFactory.createPart(new URL(decodedValue));
                    setSourcePartUsage(new PartUsage(sourcePart, qty));
                } else if (key.equals("require")) {
                    Part part = partFactory.createPart(new URL(decodedValue));
                    newRequired.add(new PartUsage(part, qty));
                } else if (key.equals("cost")) {
                    setPackageCost(Double.parseDouble(value));
                } else if (key.equals("units")) {
                    setPackageUnits(Double.parseDouble(value));
                } else if (key.equals("vendor")) {
                    setVendor(value);
                }
            }
            requiredParts = newRequired;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public int getRefreshFromRemoteCount() {
        return refreshFromRemoteCount;
    }
}
