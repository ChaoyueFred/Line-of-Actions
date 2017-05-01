package main;

/**
 * Created by Chaoyue on 4/29/16.
 */
public class BoardLocation {
    /*
    Combine row and column into a location.
     */
    private final int column;
    private final int row;

    public BoardLocation(int row, int column){
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {

        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoardLocation that = (BoardLocation) o;

        if (column != that.column) return false;
        return row == that.row;

    }

    @Override
    public int hashCode() {
        int result = column;
        result = 31 * result + row;
        return result;
    }
}
