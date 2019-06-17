package com.smarttable.data;

import java.util.Arrays;

public class CellRange {

    private int[] cellRange;

    public CellRange(int firstRow , int lastRow, int firstCol, int lastCol) {
        this.cellRange = new int[4];
        this.cellRange[0] = firstRow;
        this.cellRange[1] = lastRow;
        this.cellRange[2] = firstCol;
        this.cellRange[3] = lastCol;
    }

    public int getFirstRow() {
        return cellRange[0];
    }

    public void setFirstRow(int firstRow) {
        this.cellRange[0] = firstRow;
    }

    public int getLastRow() {
        return cellRange[1];
    }

    public void setLastRow(int lastRow) {
        this.cellRange[1] = lastRow;
    }

    public int getFirstCol() {
        return cellRange[2];
    }

    public void setFirstCol(int firstCol) {
        this.cellRange[2] = firstCol;
    }

    public int getLastCol() {
        return cellRange[3];
    }

    public void setLastCol(int lastCol) {
        this.cellRange[3] = lastCol;
    }

    public int[] getCellRange() {
        return cellRange;
    }

    public boolean contain(int row,int col){

        return cellRange[0]<= row && cellRange[1]>= row
                &&  cellRange[2]<= col && cellRange[3]>= col;
    }

    public boolean isLeftAndTop(int row,int col){
        return cellRange[0]== row && cellRange[2]== col;
    }

    @Override
    public String toString() {
        return "CellRange{" +
                "cellRange=" + Arrays.toString(cellRange) +
                '}';
    }

}
