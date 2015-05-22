/*
Copyright (C) 2014 Martin Frické (mfricke@u.arizona.edu http://softoption.us mfricke@softoption.us)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, 
modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the 
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package us.softoption.tree;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;



public class TTreeTableModelListener implements TableModelListener {
        JTable table;

        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        TTreeTableModelListener(JTable table) {
            this.table = table;
        }

        public void tableChanged(TableModelEvent e) {
            int firstRow = e.getFirstRow();
            int lastRow = e.getLastRow();
            int mColIndex = e.getColumn();

            switch (e.getType()) {
              case TableModelEvent.INSERT:
                // The inserted rows are in the range [firstRow, lastRow]
                for (int r=firstRow; r<=lastRow; r++) {
                    // Row r was inserted
                }
                break;
              case TableModelEvent.UPDATE:
                if (firstRow == TableModelEvent.HEADER_ROW) {
                    if (mColIndex == TableModelEvent.ALL_COLUMNS) {
                        // A column was added
                    } else {
                        // Column mColIndex in header changed
                    }
                } else {
                    // The rows in the range [firstRow, lastRow] changed
                    for (int r=firstRow; r<=lastRow; r++) {
                        // Row r was changed

                        if (mColIndex == TableModelEvent.ALL_COLUMNS) {
                            // All columns in the range of rows have changed
                        } else {
                            // Column mColIndex changed
                        }
                    }
                }
                break;
              case TableModelEvent.DELETE:
                // The rows in the range [firstRow, lastRow] changed
                for (int r=firstRow; r<=lastRow; r++) {
                    // Row r was deleted
                }
                break;
            }
        }
    }
