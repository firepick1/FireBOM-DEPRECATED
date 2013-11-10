package org.firepick.firebom.part;
/*
   VendorComparator.java
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

import org.firepick.firebom.IPartComparable;

import java.util.Comparator;

public class VendorComparator implements Comparator<IPartComparable> {
    @Override
    public int compare(IPartComparable o1, IPartComparable o2) {
        Part part1 = o1.getPart();
        Part part2 = o2.getPart();
        int cmp = 0;
        if (part1 != part2) {
            if (part1 == null) {
                cmp = -1;
            } else if (part2 == null) {
                cmp = 1;
            } else {
                String vendor1 = part1.getVendor();
                String vendor2 = part2.getVendor();
                if (vendor1 != vendor2) {
                    if (vendor1 == null) {
                        cmp = -1;
                    } else if (vendor2 == null) {
                        cmp = 1;
                    } else {
                        cmp = vendor1.compareTo(vendor2);
                    }
                }
            }
        }
        if (cmp == 0) {
            cmp = part1.getUrl().toString().compareTo(part2.getUrl().toString());
        }

        return cmp;
    }
}
