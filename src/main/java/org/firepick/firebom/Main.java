package org.firepick.firebom;
/*
   Main.java
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

import org.firepick.firebom.bom.BOM;
import org.firepick.firebom.bom.BOMFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws IOException {
        mainStream(args, System.out);
    }

    public static void mainStream(String[] args, PrintStream printStream) throws IOException {
        BOMFactory bomFactory = new BOMFactory();

        try {
            if (!parseArgs(args, bomFactory, printStream)) {
                printHelp(printStream);
            }
        }
        finally {
            bomFactory.shutdown();
        }
    }


    private static boolean parseArgs(String[] args, BOMFactory bomFactory, PrintStream printStream) throws IOException {
        int urlCount = 0;

        for (String arg: args) {
            if ("-markdown".equalsIgnoreCase(arg)) {
                bomFactory.setOutputType(BOMFactory.OutputType.MARKDOWN);
            } else if ("-csv".equalsIgnoreCase(arg)) {
                bomFactory.setOutputType(BOMFactory.OutputType.CSV);
            } else if ("-html".equalsIgnoreCase(arg)) {
                bomFactory.setOutputType(BOMFactory.OutputType.HTML);
            } else {
                try {
                    URL url = new URL(arg);
                    urlCount++;
                    BOM bom = new BOM(url);
                    bom.resolve(0);
                    bomFactory.printBOM(printStream, bom, null);
                } catch (MalformedURLException e) {
                    return false;
                }
            }
        }
        return urlCount > 0;
    }

    public static void printHelp(PrintStream printStream) throws IOException {
        InputStream is = Main.class.getResourceAsStream("/help.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        while (br.ready()) {
            String line = br.readLine();
            printStream.println(line);
        }
    }

}
