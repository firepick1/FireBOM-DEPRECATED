package org.firepick.firebom.bom;
/*
   BOMHtmlPrinter.java
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

import org.firepick.firebom.Main;
import org.firepick.relation.IRelation;
import org.firepick.relation.IRow;
import org.firepick.relation.IRowVisitor;
import org.firepick.relation.RelationPrinter;

import java.io.*;

public class BOMHtmlPrinter extends RelationPrinter {
    private boolean printHtmlWrapper;
    private String title;

    public BOMHtmlPrinter() {
        super.setPrintTitleRow(false);
        super.setPrintTotalRow(true);
    }

    public String getTitle() {
        return title;
    }

    public BOMHtmlPrinter setTitle(String value) {
        title = value;
        return this;
    }

    @Override
    protected void printTotalRow(PrintStream printStream, IRelation relation) {
        BOM bom = (BOM) relation;
        printStream.println("<tr class='firebom_tr'>");

        printStream.print("<td class='firebom_td firebom_blanktotal'>");
        printStream.print("&nbsp;");
        printStream.println("</td>");


        printStream.print("<td class='firebom_td firebom_number firebom_total'>");
        printStream.print(bom.getColumn(BOMColumn.COST).getFormat().format(bom.totalCost()));
        printStream.println("</td>");

        printStream.print("<td class='firebom_td firebom_total'>");
        printStream.print("&nbsp;");
        printStream.println("</td>");

        printStream.print("<td class='firebom_td firebom_number firebom_total'>");
        printStream.print(bom.getColumn(BOMColumn.QUANTITY).getFormat().format(bom.partCount()));
        printStream.println("</td>");

        printStream.print("<td class='firebom_td  firebom_total'>");
        printStream.print("&nbsp;");
        printStream.println("</td>");

        printStream.print("<td class='firebom_td firebom_longtext  firebom_blanktotal'>");
        printStream.print("&nbsp;");
        printStream.println("</td>");

        printStream.println("</tr>");
    }

    @Override
    public BOMHtmlPrinter print(IRelation relation, PrintStream printStream, IRowVisitor rowVisitor) {
        BOM bom = (BOM) relation;

        if (printHtmlWrapper) {
            printStream.println("<html>");
            printStream.println("<style>");
            printCSS(printStream);
            printStream.println("</style>");
            printStream.println("<body>");
        }
        if (title != null) {
            printStream.print("<h3 class='firebom_H3'>");
            printStream.print("<a href='");
            printStream.print(bom.getRootPart().getUrl());
            printStream.print("'>");
            printStream.print(bom.getRootPart().getId());
            printStream.print("</a>: ");
            printStream.print(title);
            printStream.println("</h3>");
        }

        printStream.println("<table cellpadding=0 cellspacing=0 class='firebom_table'>");
        printStream.println("<tr class='firebom_tr'>");
        printStream.print("<th class='firebom_th'>#</th>");
        printStream.print("<th class='firebom_th'>COST</th>");
        printStream.print("<th class='firebom_th'>ID</th>");
        printStream.print("<th class='firebom_th'>QTY</th>");
        printStream.print("<th class='firebom_th'>VENDOR</th>");
        printStream.print("<th class='firebom_th firebom_longtext'>TITLE</th>");
        printStream.println("</tr>");

        super.print(relation, printStream, rowVisitor);

        printStream.println("</table>");

        if (printHtmlWrapper) {
            printStream.println("</body>");
            printStream.println("</html>");
        }

        return this;
    }

    private void printCSS(PrintStream printStream) {
        InputStream is = Main.class.getResourceAsStream("/firebom.css");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        try {
            while (br.ready()) {
                String line = br.readLine();
                printStream.println(line);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void printRow(PrintStream printStream, IRow row, int iRow, IRowVisitor rowVisitor) {
        BOMRow bomRow = (BOMRow) row;
        boolean isRowResolved = bomRow.isResolved();
        if (rowVisitor != null) {
            HtmlRowVisitor htmlRowVisitor = (HtmlRowVisitor) rowVisitor;
            htmlRowVisitor.visit(row);
            isRowResolved = htmlRowVisitor.isVisitedRowResolved();
        }
        BOM bom = (BOM) row.getRelation();
        printStream.println("<tr class='firebom_tr'>");

        printStream.print("<td class='firebom_td'>");
        printStream.print(iRow);
        printStream.println("</td>");


        printStream.print("<td class='firebom_td firebom_number'>");
        super.printColumnValue(printStream, bom.getColumn(BOMColumn.COST), row);
        printStream.println("</td>");

        printStream.print("<td class='firebom_td'>");
        printStream.print("<a href='");
        printStream.print(bomRow.getPart().getUrl());
        printStream.print("'>");
        printStream.print(bomRow.getPart().getId());
        printStream.print("</a>");
        printStream.println("</td>");

        printStream.print("<td class='firebom_td firebom_number'>");
        printStream.print(bom.getColumn(BOMColumn.QUANTITY).getFormat().format(bomRow.getQuantity()));
        printStream.print("@");
        printStream.print(bom.getColumn(BOMColumn.COST).getFormat().format(bomRow.getUnitCost()));
        printStream.println("</td>");

        printStream.print("<td class='firebom_td'>");
        printStream.print("<a href='");
        printStream.print(bomRow.getPart().getSourceUrl());
        printStream.print("'>");
        printStream.print(bomRow.getVendor());
        printStream.print("</a>");
        printStream.println("</td>");

        printStream.print("<td class='firebom_td firebom_longtext");
        if (bomRow.getPart().getRefreshException() != null) {
            printStream.print(" firebom_error");
        }
        printStream.print("'>");
        if (isRowResolved) {
            printStream.print(bomRow.getPart().getTitle());
        } else {
            printStream.print("<img src='/firebom/processing.gif' height='20px'>");
        }
        printStream.println("</td>");

        printStream.println("</tr>");
    }

    public boolean isPrintHtmlWrapper() {
        return printHtmlWrapper;
    }

    public BOMHtmlPrinter setPrintHtmlWrapper(boolean printHtmlWrapper) {
        this.printHtmlWrapper = printHtmlWrapper;
        return this;
    }

}
