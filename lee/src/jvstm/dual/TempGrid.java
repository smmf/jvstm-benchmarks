package jvstm.dual;

import jvstm.VBoxInt;

public class TempGrid {
	
    private VBoxInt[][] grid;
    private int rowSize;
    private int colSize;

    TempGrid(int rows, int cols) {
        this.rowSize = rows;
        this.colSize = cols;
        // will lazily allocate the required inner arrays
        this.grid = new VBoxInt[rows][];
    }

    int get(int x, int y) {
        VBoxInt[] row = this.grid[x];
        if (row == null) {
            return LeeRouter.TEMP_EMPTY;
        }
        VBoxInt box = row[y];
        if (box == null) {
            return LeeRouter.TEMP_EMPTY;
        }
        return box.getInt();
    }

    void put(int value, int x, int y) {
        if (this.grid[x] == null) {
            this.grid[x] = new VBoxInt[colSize];
        }

        if (this.grid[x][y] == null) {
            this.grid[x][y] = new VBoxInt();
        }
        this.grid[x][y].putInt(value);
    }
}
