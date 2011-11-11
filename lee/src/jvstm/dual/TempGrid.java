package jvstm.dual;

import jvstm.VBoxInt;
import static jvstm.UtilUnsafe.UNSAFE;

public class TempGrid {
    // values needed for sun.misc.Unsafe operations
    private static int ARRAY_VBOX_2D_BASE_OFFSET = UNSAFE.arrayBaseOffset(VBoxInt[][].class);
    private static int ARRAY_VBOX_2D_INDEX_SCALE = UNSAFE.arrayIndexScale(VBoxInt[][].class);
    private static int ARRAY_VBOX_1D_BASE_OFFSET = UNSAFE.arrayBaseOffset(VBoxInt[].class);
    private static int ARRAY_VBOX_1D_INDEX_SCALE = UNSAFE.arrayIndexScale(VBoxInt[].class);

    private VBoxInt[][] grid;
    private int colSize;

    TempGrid(int rows, int cols) {
        this.colSize = cols;
        // will lazily allocate the required inner arrays
        this.grid = new VBoxInt[rows][];
    }

    int get(int x, int y) {
        return ensureBoxAt(x,y).getInt();
    }

    void put(int value, int x, int y) {
        ensureBoxAt(x,y).putInt(value);
    }

    private VBoxInt[] ensureRowAt(int x) {
        VBoxInt[] currentRow = this.grid[x];
        VBoxInt[] rowToReturn = currentRow;
        if (currentRow == null) {
            long offset = (long)ARRAY_VBOX_2D_BASE_OFFSET + x * ARRAY_VBOX_2D_INDEX_SCALE;
            VBoxInt[] newRow = new VBoxInt[this.colSize];
            rowToReturn = newRow;

            // loop until either: my CAS succeeds or I've read some rowToReturn
            while (!UNSAFE.compareAndSwapObject(this.grid, offset, null, rowToReturn)
                   && ((rowToReturn = this.grid[x]) == null)) {
                rowToReturn = newRow;
            }
        }
        return rowToReturn;
    }

    private VBoxInt ensureBoxAt(int x, int y) {
        VBoxInt[] currentRow = ensureRowAt(x);

        VBoxInt currentBox = currentRow[y];
        VBoxInt boxToReturn = currentBox;
        if (currentBox == null) {
            long offset = (long)ARRAY_VBOX_1D_BASE_OFFSET + y * ARRAY_VBOX_1D_INDEX_SCALE;
            VBoxInt newBox = new VBoxInt(LeeRouter.TEMP_EMPTY);
            boxToReturn = newBox;

            // loop until either: my CAS succeeds or I've read some boxToReturn
            while (!UNSAFE.compareAndSwapObject(currentRow, offset, null, boxToReturn)
                   && ((boxToReturn = currentRow[y]) == null)) {
                boxToReturn = newBox;
            }
        }
        return boxToReturn;
    }


}
