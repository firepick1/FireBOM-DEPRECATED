package org.firepick.firebom.bom;
/*
   HtmlRowVisitor.java
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

import org.firepick.relation.IRow;
import org.firepick.relation.IRowVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlRowVisitor implements IRowVisitor {
    private static Logger logger = LoggerFactory.getLogger(HtmlRowVisitor.class);

    private boolean isResolved = true;
    private boolean lastResolved;

    public boolean isResolved() {
        return isResolved;
    }

    @Override
    public void visit(IRow row) {
        BOMRow bomRow = (BOMRow) row;
        logger.debug("visit BOMRow {}", bomRow.getPart().getUrl());
        lastResolved = bomRow.isResolved();
        isResolved = lastResolved && isResolved;
    }

    public boolean isVisitedRowResolved() {
        return lastResolved;
    }
}
