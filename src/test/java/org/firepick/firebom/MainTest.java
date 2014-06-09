package org.firepick.firebom;
/*
   MainTest.java
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

import org.firepick.firebom.bom.BOMFactory;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class MainTest {
    @Test
    public void testHelp() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        PrintStream printWriter = new PrintStream(bos);
        Main.mainStream(new String[0], printWriter);
        printWriter.flush();
        String help = baos.toString();
        System.out.println(help);
        assert(help.contains("USAGE"));
        assert(help.contains("OPTIONS"));
        assert(help.contains("EXAMPLES"));
    }

    @Test
    public void testBOMFactory() {
        BOMFactory bomFactory = new BOMFactory();
        assertEquals(BOMFactory.OutputType.DEFAULT, bomFactory.getOutputType());
        bomFactory.setOutputType(BOMFactory.OutputType.MARKDOWN);
        assertEquals(BOMFactory.OutputType.MARKDOWN, bomFactory.getOutputType());
    }
}
