package jvstm.dual;

import jvstm.VBoxInt;

public class TempGrid {
	
    private VBoxInt[][] grid;

    TempGrid(int x, int y) {
        this.grid = new VBoxInt[x][y];
        for (int i = 0; i < x; i++) {
            this.grid[i] = new VBoxInt[y];
            for (int j = 0; j < y; j++) {
                this.grid[i][j] = new VBoxInt();
            }
        }
    }

    int get(int x, int y) {
        return this.grid[x][y].get();
    }

    void put(int value, int x, int y) {
        this.grid[x][y].put(value);
    }
}
