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
import org.firepick.firebom.part.CachedUrlResolver;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws Exception {
        mainStream(args, System.out);
    }

    public static void mainStream(String[] args, PrintStream printStream) throws Exception {
        BOMFactory bomFactory = new BOMFactory();

        try {
            if (!parseArgs(args, bomFactory, printStream)) {
                printHelp(printStream);
            }
	} finally {
            bomFactory.shutdown();
        }
    }

    private static boolean getUrl(String urlString) throws Exception {
      CachedUrlResolver resolver = new CachedUrlResolver();
      URL url = new URL(urlString);
      String content = resolver.get(url);
      System.out.println(content);
      return true;
    }

    private static boolean parseArgs(String[] args, BOMFactory bomFactory, PrintStream printStream) throws Exception {
      int urlCount = 0;
      boolean ok = false;

      for (int i=1; i<args.length; i++) {
	String arg = args[i];
	if ("-u".equals(arg)) {
	  if (i+1 >= args.length) {
	    throw new RuntimeException("Expected URL after \"-u\"");
	  }
	  ok = getUrl(args[++i]) || ok;
	} else if ("-nocache".equalsIgnoreCase(arg)) {
	    CachedUrlResolver.setIsCached(false);
	} else if ("-markdown".equalsIgnoreCase(arg)) {
	    bomFactory.setOutputType(BOMFactory.OutputType.MARKDOWN);
	} else if ("-csv".equalsIgnoreCase(arg)) {
	    bomFactory.setOutputType(BOMFactory.OutputType.CSV);
	} else if ("-html".equalsIgnoreCase(arg)) {
	    bomFactory.setOutputType(BOMFactory.OutputType.HTML);
	} else {
	    System.out.println("Resolving uri:" + arg);
	    URL url = new URL(arg);
	    urlCount++;
	    BOM bom = new BOM(url);
	    bom.resolve(0);
	    bomFactory.printBOM(printStream, bom, null);
	}
      }
      return ok || urlCount > 0;
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
