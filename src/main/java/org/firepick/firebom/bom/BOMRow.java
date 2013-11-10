package org.firepick.firebom.bom;
/*
   BOMRow.java
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
import org.firepick.firebom.exception.ProxyResolutionException;
import org.firepick.firebom.part.Part;
import org.firepick.firebom.part.PartUsage;
import org.firepick.relation.IColumnDescription;
import org.firepick.relation.IRelation;
import org.firepick.relation.IRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Format;

public class BOMRow extends PartUsage implements IRow, IPartComparable {
    private static Logger logger = LoggerFactory.getLogger(BOMRow.class);

    private BOM bom;
    private boolean isResolved;
    private boolean isPartsAdded;

    public BOMRow(BOM bom, Part part) {
        this.bom = bom;
        setPart(part);
    }

    public synchronized boolean resolve() {
        if (!isResolved) {
            Part part = getPart();
            if (!part.isResolved()) {
                refreshPart(part);
            }
            Part sourcePart = part.getSourcePart();
            if (sourcePart != null && !sourcePart.isResolved()) {
                refreshPart(sourcePart);
            }
            if (part.isResolved() && !isPartsAdded) {
                isPartsAdded = true;
                for (PartUsage partUsage : part.getRequiredParts()) {
                    bom.addPart(partUsage.getPart(), partUsage.getQuantity() * getQuantity());
                }
                isResolved = true;
            }
        }
        return isResolved;
    }

    private void refreshPart(Part part) {
        try {
            part.refresh();
        }
        catch (ProxyResolutionException e) {
            logger.warn(part.getUrl().toString(), e);
        }
    }

    @Override
    public IRelation getRelation() {
        return bom;
    }

    @Override
    public double getUnitCost() {
        Part part = getPart();
        if (part.isAbstractPart()) {
            return part.getSourcePartUsage().getCost();
        } else if (part.isVendorPart()) {
            return part.getUnitCost();
        }

        return 0;
    }

    @Override
    public Object item(int index) {
        Object value = null;
        switch (BOMColumn.values()[index]) {
            case ID:
                value = getPart().getId();
                break;
            case QUANTITY:
                value = getQuantity();
                break;
            case COST:
                value = getCost();
                break;
            case URL:
                value = getPart().getUrl();
                break;
            case SOURCE:
                value = getPart().getSourceUrl();
                break;
            case TITLE:
                value = getPart().getTitle();
                break;
            case VENDOR:
                value = getVendor();
                break;
            case PROJECT:
                value = getPart().getProject();
                break;
        }
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IColumnDescription columnDescription : getRelation().describeColumns()) {
            Format format = columnDescription.getFormat();
            Object value = item(columnDescription.getItemIndex());
            if (sb.length() > 0) {
                sb.append(", ");
            }
            if (format == null) {
                sb.append(value);
            } else {
                sb.append(format.format(value));
            }
        }
        return sb.toString();
    }

    public boolean isResolved() {
        return isResolved;
    }

}
