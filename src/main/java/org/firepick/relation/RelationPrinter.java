package org.firepick.relation;
/*
   RelationPrinter.java
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

import java.io.PrintStream;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

public class RelationPrinter {
    private List<IColumnDescription> columnDescriptionList = new ArrayList<IColumnDescription>();
    private String columnSeparator = ", ";
    private boolean printTotalRow = true;
    private boolean printTitleRow = true;

    public RelationPrinter print(IRelation relation, PrintStream printStream, IRowVisitor rowVisitor) {
        if (columnDescriptionList.size() == 0) {
            columnDescriptionList = new ArrayList<IColumnDescription>(relation.describeColumns());
        }

        if (printTitleRow) {
            printColumnTitles(printStream, relation);
        }
        synchronized (columnDescriptionList) {
            printRows(relation, printStream, rowVisitor);
        }
        return this;
    }

    private void printRows(IRelation relation, PrintStream printStream, IRowVisitor rowVisitor) {
        for (IColumnDescription columnDescription : columnDescriptionList) {
            columnDescription.getAggregator().clear();
        }

        int iRow = 1;
        for (IRow row : relation) {
            printRow(printStream, row, iRow++, rowVisitor);
        }

        if (printTotalRow) {
            printTotalRow(printStream, relation);
        }
    }

    protected void printTotalRow(PrintStream printStream, IRelation relation) {
        int columns = 0;
        for (IColumnDescription columnDescription : columnDescriptionList) {
            if (columns++ > 0) {
                printStream.print(columnSeparator);
            }
            Object aggregate = columnDescription.getAggregator().getAggregate();
            printValue(printStream, columnDescription, aggregate);
        }
        printStream.println();
    }

    protected void printRow(PrintStream printStream, IRow row, int iRow, IRowVisitor rowVisitor) {
        if (rowVisitor != null) {
            rowVisitor.visit(row);
        }

        int columns = 0;
        for (IColumnDescription columnDescription : columnDescriptionList) {
            if (columns++ > 0) {
                printStream.print(columnSeparator);
            }
            Object value = printColumnValue(printStream, columnDescription, row);
            if (printTotalRow) {
                columnDescription.getAggregator().aggregate(value);
            }
        }
        printStream.println();
    }

    protected Object printColumnValue(PrintStream printStream, IColumnDescription columnDescription, IRow row) {
        Object value = row.item(columnDescription.getItemIndex());
        printValue(printStream, columnDescription, value);
        return value;
    }

    protected void printValue(PrintStream printStream, IColumnDescription columnDescription, Object value) {
        Format format = columnDescription.getFormat();
        if (format == null) {
            printStream.print(value);
        } else {
            printStream.print(format.format(value));
        }
    }

    private void printColumnTitles(PrintStream printStream, IRelation relation) {
        int columns = 0;
        for (IColumnDescription columnDescription : columnDescriptionList) {
            if (columns++ > 0) {
                printStream.print(columnSeparator);
            }
            String title = columnDescription.getTitle();
            printValue(printStream, columnDescription, title);
        }
        printStream.println();
    }

    public List<IColumnDescription> getColumnDescriptionList() {
        return columnDescriptionList;
    }

    public RelationPrinter setColumnDescriptionList(List<IColumnDescription> columnDescriptionList) {
        this.columnDescriptionList = columnDescriptionList;
        return this;
    }

    public String getColumnSeparator() {
        return columnSeparator;
    }

    public RelationPrinter setColumnSeparator(String columnSeparator) {
        this.columnSeparator = columnSeparator;
        return this;
    }

    public boolean isPrintTotalRow() {
        return printTotalRow;
    }

    public RelationPrinter setPrintTotalRow(boolean printTotalRow) {
        this.printTotalRow = printTotalRow;
        return this;
    }

    public boolean isPrintTitleRow() {
        return printTitleRow;
    }

    public RelationPrinter setPrintTitleRow(boolean printTitleRow) {
        this.printTitleRow = printTitleRow;
        return this;
    }
}
