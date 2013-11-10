package org.firepick.firebom.bom;
/*
   BOMMarkdownPrinter.java
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

import org.firepick.relation.IRelation;
import org.firepick.relation.IRow;
import org.firepick.relation.IRowVisitor;
import org.firepick.relation.RelationPrinter;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class BOMMarkdownPrinter extends RelationPrinter {

    public BOMMarkdownPrinter() {
        super.setPrintTitleRow(false);
        super.setPrintTotalRow(false);
    }

    @Override
    public RelationPrinter print(IRelation relation, PrintStream printStream, IRowVisitor rowVisitor) {
        BOM bom = (BOM)  relation;
        printStream.print("#### Bill Of Materials (");
        DecimalFormat currencyFormat = (DecimalFormat) NumberFormat.getCurrencyInstance();
        printStream.print(currencyFormat.format(bom.totalCost()));
        printStream.print("; ");
        printStream.print(bom.partCount());
        printStream.println(" parts)");

        return super.print(relation, printStream, rowVisitor);
    }

    @Override
    protected void printRow(PrintStream printStream, IRow row, int iRow, IRowVisitor rowVisitor) {
        BOMRow bomRow = (BOMRow) row;
        BOM bom = (BOM) bomRow.getRelation();
        printStream.print(iRow);
        printStream.print(". ");
        printColumnValue(printStream, bom.getColumn(BOMColumn.COST), row);
        printStream.print(" [");
        printColumnValue(printStream, bom.getColumn(BOMColumn.ID), row);
        printStream.print("](");
        printColumnValue(printStream, bom.getColumn(BOMColumn.URL), row);
        printStream.print(") ");
        printColumnValue(printStream, bom.getColumn(BOMColumn.QUANTITY), row);
        printStream.print(" [");
        printColumnValue(printStream, bom.getColumn(BOMColumn.VENDOR), row);
        printStream.print("](");
        printColumnValue(printStream, bom.getColumn(BOMColumn.SOURCE), row);
        printStream.print(") ");
        printColumnValue(printStream, bom.getColumn(BOMColumn.TITLE), row);
        printStream.println();
    }

    @Override
    protected void printTotalRow(PrintStream printStream, IRelation relation) {
        BOM bom = (BOM)  relation;
        printStream.print("1. Total cost:");
        DecimalFormat currencyFormat = (DecimalFormat) NumberFormat.getCurrencyInstance();
        printStream.print(currencyFormat.format(bom.totalCost()));
        printStream.print(" parts:");
        printStream.print(bom.partCount());
        printStream.println();
    }
}
