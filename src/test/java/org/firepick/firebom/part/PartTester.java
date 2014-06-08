package org.firepick.firebom.part;
/*
   PartTester.java
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

import junit.framework.Assert;

import java.net.URL;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class PartTester {
  private URL url;
  private Part part;

  public PartTester(PartFactory partFactory, String url) throws Exception {
    this.url = new URL(url);
    part = partFactory.createPart(this.url);
    part.refreshAll();
    assert (part.isFresh());
  }

  public PartTester testId(String id) {
    assertEquals("part id", id, part.getId());
    return this;
  }

  public PartTester testVendor(String value) {
    Assert.assertEquals("vendor name", value, part.getVendor());
    return this;
  }

  public PartTester testUnitCost(double value) {
    assertEquals("part unit cost", value, part.getUnitCost(), .005d);
    return this;
  }

  public PartTester testUnitCost(double value, double tolerance) {
    assertEquals("part unit cost", value, part.getUnitCost(), tolerance);
    return this;
  }

  public PartTester testPackageCost(double value, double tolerance) {
    assertEquals("part package cost", value, part.getPackageCost(), tolerance);
    return this;
  }

  public PartTester testSourceCost(double value) {
    assertEquals(value, part.getSourcePartUsage().getCost(), .005d);
    return this;
  }

  public PartTester testSourcePackageUnits(double value) {
    assertEquals("source package units", value, part.getSourcePartUsage().getQuantity(), .5d);
    return this;
  }

  public PartTester testPackageUnits(double value) {
    assertEquals("part package units", value, part.getPackageUnits(), 0.005d);
    return this;
  }

  public PartTester testProject(String value) {
    assertEquals("project name for part", value, part.getProject());
    return this;
  }

  public PartTester testTitle(String value) {
    assertEquals("part title", value, part.getTitle());
    return this;
  }

  public PartTester testTitleCategory(String value) {
    assertEquals("part title", value, part.getTitleCategory());
    return this;
  }

  public PartTester testRequiredParts(int value) {
    List<PartUsage> partUsages = part.getRequiredParts();
    assertNotNull(partUsages);
    assertEquals("number of required parts", value, partUsages.size());
    return this;
  }

  public PartTester testRequiredPart(int index, String partId, double quantity, double unitCost) {
    List<PartUsage> partUsages = part.getRequiredParts();
    PartUsage partUsage = partUsages.get(index);
    assertEquals(partId, partUsage.getPart().getId());
    assertEquals(partId + " quantity", quantity, partUsage.getQuantity(), 0);
    assertEquals(partId + " unit cost", unitCost, partUsage.getPart().getUnitCost(), 0.5d);
    return this;
  }

  public Part getPart() {
    return part;
  }

}
