package org.firepick.firebom.part;
/*
   PartUsage.java
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

import java.io.Serializable;

public class PartUsage implements Serializable, IPartComparable {
    private Part part;
    private double quantity;
    private String vendor;

    public PartUsage(Part part, double quantity) {
        setPart(part);
        addQuantity(quantity);
    }

    public PartUsage() {}

    public Part getPart() {
        return part;
    }

    public PartUsage setPart(Part part) {
        this.part = part;
        return this;
    }

    public double getQuantity() {
        return quantity;
    }

    public synchronized PartUsage addQuantity(double quantity) {
        double oldQuantity = this.quantity;
        this.quantity = oldQuantity + quantity;
        return this;
    }

    public double getCost() {
        return getQuantity() * getUnitCost();
    }

    public double getUnitCost() {
        return part.getUnitCost();
    }

    public String getVendor() {
        if (vendor == null) {
            return getPart().getVendor();
        }
        return vendor;
    }

    public PartUsage setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    @Override
    public String toString() {
        return part.toString();
    }

    @Override
    public int compareTo(IPartComparable that) {
        int cmp = getPart().compareTo(that.getPart());
        if (cmp == 0 && that instanceof PartUsage) {
            PartUsage thatPartUsage = (PartUsage) that;
            cmp = (int) Math.signum(getQuantity() - thatPartUsage.getQuantity());
        }
        return cmp;
    }

}
