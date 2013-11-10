package org.firepick.firebom.part;
/*
   PartUsageTest.java
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

import org.firepick.firebom.part.Part;
import org.firepick.firebom.part.PartFactory;
import org.firepick.firebom.part.PartUsage;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertEquals;

public class PartUsageTest {
    @Test
    public void testComparable() throws Exception{
        URL url1 = new URL("http://shpws.me/nekC");
        Part part1 = PartFactory.getInstance().createPart(url1);
        URL url2 = new URL("http://shpws.me/nuwV");
        Part part2 = PartFactory.getInstance().createPart(url2);
        part1.refresh();
        part2.refresh();
        testPartOrder(part1, part2);

        // unrefreshed parts should order the same
        part1 = new Part().setUrl(url1);
        part2 = new Part().setUrl(url2);
        testPartOrder(part1, part2);
    }

    private void testPartOrder(Part part1, Part part2) {
        PartUsage partUsage1 = new PartUsage().setPart(part1);
        PartUsage partUsage2 = new PartUsage().setPart(part2);
        PartUsage partUsage1_2 = new PartUsage(part1, 2);

        assertEquals(-1, (int) Math.signum(part1.compareTo(part2)));
        assertEquals(1, (int) Math.signum(part2.compareTo(part1)));
        assertEquals(0, (int) Math.signum(part1.compareTo(part1)));

        assertEquals(-1, (int) Math.signum(partUsage1.compareTo(part2)));
        assertEquals(1, (int) Math.signum(partUsage2.compareTo(part1)));
        assertEquals(0, (int) Math.signum(partUsage1.compareTo(part1)));

        assertEquals(-1, (int) Math.signum(partUsage1.compareTo(partUsage2)));
        assertEquals(1, (int) Math.signum(partUsage2.compareTo(partUsage1)));
        assertEquals(0, (int) Math.signum(partUsage1.compareTo(partUsage1)));

        assertEquals(-1, (int) Math.signum(partUsage1.compareTo(partUsage1_2)));
        assertEquals(1, (int) Math.signum(partUsage1_2.compareTo(partUsage1)));
        assertEquals(0, (int) Math.signum(partUsage1_2.compareTo(part1)));
    }
}
