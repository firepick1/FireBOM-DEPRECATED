package org.firepick.firebom.bom;
/*
   BOMRowIterator.java
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
import org.firepick.firebom.part.VendorComparator;
import org.firepick.relation.IRow;

import java.util.Iterator;
import java.util.TreeSet;

public class BOMRowIterator implements Iterator<IRow> {
    private Iterator<BOMRow> iterator;


    public BOMRowIterator(Iterator<IPartComparable> iterator) {
        TreeSet<BOMRow> treeSet = new TreeSet(new VendorComparator());
        while (iterator.hasNext()) {
            treeSet.add((BOMRow) iterator.next());
        }
        this.iterator = treeSet.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public IRow next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}
