package com.smarttable.data;

public class Cell {

    public static final int INVALID = -1;

    public int col;
    public int row;
    public Cell realCell;
    public int width;
    public int height;

    public Cell(int col, int row) {
        this.col = col;
        this.row = row;
        realCell = this;
    }

    public Cell(Cell realCell) {
        this.col = INVALID;
        this.row = INVALID;
        this.realCell = realCell;
    }

}
