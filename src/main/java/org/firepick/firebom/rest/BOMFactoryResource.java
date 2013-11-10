package org.firepick.firebom.rest;
/*
   BOMFactoryResource.java
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
import org.firepick.firebom.Main;
import org.firepick.firebom.bom.HtmlRowVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

@Path("/build")
public class BOMFactoryResource {
    private static Logger logger = LoggerFactory.getLogger(BOMFactoryResource.class);

    @Context
    private javax.servlet.http.HttpServletRequest request;

    @GET
    @Produces("text/html; charset=UTF-8")
    public String createBOM(@QueryParam("url") String urlString) throws IOException, InterruptedException {
        BOMFactory bomFactory = new BOMFactory();
        URL url;
        try {
            url = new URL(urlString);
        } catch(MalformedURLException e) {
            url = new URL("http://" + urlString);
        }
        HttpSession session = request.getSession();
        BOM bom = null;
        if (urlString.equals(session.getAttribute("url"))) {
            Object bomObj = session.getAttribute("BOM");
            if (bomObj != null) {
                bom = (BOM) bomObj;
            }
        } else {
            session.setAttribute("url", urlString);
            session.setAttribute("BOM", null);
        }
        if (bom == null) {
            bom = bomFactory.createBOM(url);
            session.setAttribute("BOM", bom);
        }

        ByteArrayOutputStream bosHtml = new ByteArrayOutputStream();
        PrintStream psHtml = new PrintStream(bosHtml);
        InputStream is;
        logger.info("createBOM {}", urlString);
        if (request.getContextPath().contains("/firebom")) {
            is = Main.class.getResourceAsStream("/index.html");
        } else {
            is = Main.class.getResourceAsStream("/app-engine/index.html");
        }
        InputStreamReader isr = new InputStreamReader(is);
        bomFactory.setOutputType(BOMFactory.OutputType.HTML_TABLE);
        HtmlRowVisitor rowVisitor = new HtmlRowVisitor();
        BufferedReader br = new BufferedReader(isr);
        while (br.ready()) {
            String line = br.readLine();
            if (line.contains("<!--BOM-->")) {
                bomFactory.printBOM(psHtml, bom, rowVisitor);
                psHtml.println("<script>$('#url').val('" + urlString + "')</script>");
                if (rowVisitor.isResolved()) {
                    psHtml.print("<div class='firebom_copylink'>");
                    psHtml.print("<button type='button' onclick='copyLink()'>");
                    psHtml.print("Link this <img style='position:relative;top:2px;' src='http://upload.wikimedia.org/wikipedia/commons/4/45/FireBOM.JPG' height=12px/>");
                    psHtml.print("</button>");
                    psHtml.print("</div>");
                    psHtml.println();
                    session.invalidate();
                } else {
                    psHtml.println("<script>setTimeout(function() {location.reload();}, 1000)</script>");
                }
            } else {
                psHtml.println(line);
            }
        }

        return bosHtml.toString();
    }
}
