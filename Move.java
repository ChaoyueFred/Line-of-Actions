package main;

/**
 * Created by Chaoyue on 4/30/16.
 */
public class Move {
    /*
    Combine two locations to a move.
     */
    private final BoardLocation from;   //start location
    private final BoardLocation to;     //destination location

    public Move(BoardLocation from, BoardLocation to){
        this.from = from;
        this.to = to;
    }

    public BoardLocation getFrom() {
        return from;
    }

    public BoardLocation getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (from != null ? !from.equals(move.from) : move.from != null) return false;
        return to != null ? to.equals(move.to) : move.to == null;

    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }
}
