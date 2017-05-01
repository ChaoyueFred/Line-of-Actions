package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import static main.Checker.*;
import static main.MoveDirection.*;
import static main.PlayerColor.*;

/**
 * Created by Chaoyue on 4/28/16.
 */
public class ChessBoard {

    public final int boardSize;
    public Checker[][] checkers;   //The contents of board
    public int[] checkerNumInRow;  // # of checkers in each row
    public int[] checkerNumInColumn;   // # of checkers in each column
    public int[] checkerNumInCounterDiagonal = new int[9];  // # of checkers in each up-diagonal "/"
    public int[] checkerNumInMainDiagonal = new int[9];    //# of checkers in each down-diagonal "\"

    private static final Checker[][] INITIALCHESSBOARD_5 = {    //The initial status of a 5*5 chess board.
            {EMPTY, BLACK, BLACK, BLACK, EMPTY},
            {WHITE,EMPTY,EMPTY,EMPTY,WHITE},
            {WHITE,EMPTY,EMPTY,EMPTY,WHITE},
            {WHITE,EMPTY,EMPTY,EMPTY,WHITE},
            {EMPTY, BLACK, BLACK, BLACK, EMPTY},
    };

    private static final Checker[][] INITIALCHESSBOARD_6 = {    //The initial status of a 6*6 chess board.
            {EMPTY,BLACK,BLACK,BLACK,BLACK,EMPTY},
            {WHITE,EMPTY,EMPTY,EMPTY,EMPTY,WHITE},
            {WHITE,EMPTY,EMPTY,EMPTY,EMPTY,WHITE},
            {WHITE,EMPTY,EMPTY,EMPTY,EMPTY,WHITE},
            {WHITE,EMPTY,EMPTY,EMPTY,EMPTY,WHITE},
            {EMPTY,BLACK,BLACK,BLACK,BLACK,EMPTY},
    };

    public ChessBoard(int boardSize){   //Construction function of Chessboard, need acquire the board size firstly.
        this.boardSize = boardSize;
        if (boardSize == 5){
            this.checkers = INITIALCHESSBOARD_5;
            this.checkerNumInColumn = new int[]{3,2,2,2,3};
            this.checkerNumInRow = new int[]{3,2,2,2,3};
            this.checkerNumInMainDiagonal = new int[]{0,2,2,2,0,2,2,2,0};
            this.checkerNumInCounterDiagonal = new int[]{0,2,2,2,0,2,2,2,0};
        }
        else{
            this.checkers = INITIALCHESSBOARD_6;
            this.checkerNumInColumn = new int[]{4,2,2,2,2,4};
            this.checkerNumInRow = new int[]{4,2,2,2,2,4};
            this.checkerNumInMainDiagonal = new int[]{0,2,2,2,2,0,2,2,2,2,0};
            this.checkerNumInCounterDiagonal = new int[]{0,2,2,2,2,0,2,2,2,2,0};

        }

    }


    public ChessBoard(ChessBoard board){    // Board copy constructor
        this.boardSize = board.boardSize;
        this.checkers = new Checker[boardSize][boardSize];
        this.checkerNumInRow = new int[boardSize];
        this.checkerNumInColumn = new int[boardSize];
        this.checkerNumInMainDiagonal = new int[boardSize*2-1];
        this.checkerNumInCounterDiagonal = new int[boardSize*2-1];
        for (int i = 0; i < boardSize; i++){
            for (int j = 0; j < boardSize; j++){
                this.checkers[i][j] = board.checkers[i][j];
            }
            this.checkerNumInRow[i] = board.checkerNumInRow[i];
            this.checkerNumInColumn[i] = board.checkerNumInColumn[i];
            this.checkerNumInMainDiagonal[i] = board.checkerNumInMainDiagonal[i];
            this.checkerNumInMainDiagonal[i + boardSize - 1] = board.checkerNumInMainDiagonal[i + boardSize - 1];
            this.checkerNumInCounterDiagonal[i] = board.checkerNumInCounterDiagonal[i];
            this.checkerNumInCounterDiagonal[i + boardSize - 1] = board.checkerNumInCounterDiagonal[i + boardSize - 1];
        }
    }

    public void chessBoardDisplay(){    //Display the board on terminal.
        for (Checker[] line : checkers){
            System.out.printf("|");
            for (Checker checker : line){
                if (checker.equals(EMPTY)){
                    System.out.printf(" - |");
                }else if(checker.equals(BLACK)){
                    System.out.printf(" B |");
                }else{
                    System.out.printf(" W |");
                }
            }
            System.out.printf("%n");
        }
        System.out.printf("%n");
    }

    public boolean inputRCnumCheck(int num){    // Check the number of row and column, must located in [0,BOARDSIZE-1]
        if (num >= 0 && num <= boardSize-1){
            return true;
        }else{
            return false;
        }
    }

    public boolean locationCheck(BoardLocation location){   // Check the location whether on the board or not
        int row = location.getRow();
        int column = location.getColumn();
        if (inputRCnumCheck(row) && inputRCnumCheck(column)){
            return true;
        }else{
            return false;
        }
    }

    public Checker getChecker(BoardLocation location){  // Return the checker of a certain location.
        int row = location.getRow();
        int column = location.getColumn();
        if (inputRCnumCheck(row) && inputRCnumCheck(column)){
            return checkers[row][column];
        }else{
            throw new IllegalArgumentException("Illegal location!");
        }
    }

    public void setChecker(BoardLocation location, Checker checkerToBeSet){     //Set the given location of chess board to the given checker.
        this.checkers[location.getRow()][location.getColumn()] = checkerToBeSet;
    }

    /*
    Get how many steps can a checker located in (r,c) move in the given direction.
     */
    public int moveCount(BoardLocation location, MoveDirection direction){
        if (direction.equals(UP) || direction.equals(DOWN)){
            return checkerNumInColumn[location.getColumn()];
        }else if (direction.equals(LEFT) || direction.equals(RIGHT)){
            return checkerNumInRow[location.getRow()];
        }else if (direction.equals(UP_COUNTERDIAGONAL) || direction.equals(DOWN_COUNTERDIAGONAL)){
            return checkerNumInCounterDiagonal[location.getColumn()+location.getRow()];
        }else if (direction.equals(UP_MAINDIAGONAL) || direction.equals(DOWN_MAINDIAGONAL)){
            return checkerNumInMainDiagonal[(boardSize-1-location.getRow()) + location.getColumn()];
        }else{
            throw new IllegalArgumentException("Illegal direction!");
        }
    }


    public boolean moveCheck(Move move, PlayerColor playerColor){   //Check whether the move is valid.
        if (!fromCheckerValidityCheck(move.getFrom(), playerColor)){
            return false;
        }
        if (!moveLocationCheck(move)){
            return false;
        }
        MoveDirection direction = moveDirectionDiscover(move);
        if (direction.equals(INVALID_DIRECTION)){
            return false;
        }
        if (!moveCountCheck(move,direction)){
            return false;
        }
        if (!moveBlockCheck(move,direction,playerColor)){
            return false;
        }
        return true;
    }

    public boolean fromCheckerValidityCheck(BoardLocation fromLocation, PlayerColor playerColor){   //Check whether the checker human picked is the right color.
        if (playerColor.equals(PLAYER_WH)){
            if (!this.getChecker(fromLocation).equals(WHITE)){
                return false;
            }
        }else if (playerColor.equals(PLAYER_BK)){
            if (!this.getChecker(fromLocation).equals(BLACK)){
                return false;
            }
        }else{
            return false;
        }
       return true;
    }

    public boolean moveLocationCheck(Move move){    //Check whether the start location and destination are on the board.
        BoardLocation from = move.getFrom();
        BoardLocation to = move.getTo();
        if (!locationCheck(to) || !locationCheck(from)){
            return false;
        }
        return true;
    }

    public MoveDirection moveDirectionDiscover(Move move){  // Given the move(from and to location), check whether the direction is valid and return the direction.
        BoardLocation from = move.getFrom();
        BoardLocation to = move.getTo();
        int fromRow = from.getRow();
        int fromColumn = from.getColumn();
        int toRow = to.getRow();
        int toColumn = to.getColumn();
        int dRow = toRow - fromRow;
        int dColumn = toColumn - fromColumn;
        if (fromColumn == toColumn){
            if (toRow > fromRow){
                return DOWN;
            }else{
                return UP;
            }
        }else if (fromRow == toRow){
            if (toColumn > fromColumn){
                return RIGHT;
            }else{
                return LEFT;
            }
        }else if (dRow == dColumn){
            if (dRow > 0){
                return DOWN_MAINDIAGONAL;
            }else {
                return UP_MAINDIAGONAL;
            }
        }else if (dRow == -dColumn){
            if(dRow > 0){
                return DOWN_COUNTERDIAGONAL;
            }else{
                return UP_COUNTERDIAGONAL;
            }
        }else{
            return INVALID_DIRECTION;
        }
    }

    public boolean moveCountCheck(Move move, MoveDirection direction){  //Check whether the number of steps of the given move is valid. (# of steps = # of checkers in the direction)
        BoardLocation from = move.getFrom();
        BoardLocation to = move.getTo();
        int moveCount = moveCount(from, direction);
        if (direction.equals(UP) || direction.equals(DOWN)){
            return Math.abs(from.getRow() - to.getRow()) == moveCount;
        }else if (direction.equals(LEFT) || direction.equals(RIGHT)){
            return Math.abs(from.getColumn() - to.getColumn()) == moveCount;
        }else if (direction.equals(UP_COUNTERDIAGONAL) || direction.equals(DOWN_COUNTERDIAGONAL)){
            return Math.abs(from.getColumn() - to.getColumn()) == moveCount;
        }else if (direction.equals(UP_MAINDIAGONAL) || direction.equals(DOWN_MAINDIAGONAL)){
            return Math.abs(from.getColumn() - to.getColumn()) == moveCount;
        }else{
            return false;
        }
    }

    public boolean moveBlockCheck(Move move, MoveDirection direction, PlayerColor playerColor){
        //check if the move been blocked by opponent checker in the path or by friendly checker on destination.
        Checker checkerOpponent;
        Checker checkerSelf;
        if (playerColor.equals(PLAYER_BK)){
            checkerSelf = BLACK;
            checkerOpponent = WHITE;
        }else if (playerColor.equals(PLAYER_WH)){
            checkerSelf = WHITE;
            checkerOpponent = BLACK;
        }else{
            return false;
        }
        BoardLocation from = move.getFrom();
        int moveCount = moveCount(from, direction);
        int fromRow = from.getRow();
        int fromColumn = from.getColumn();
        if (moveCount == 0){
            return true;
        }
        if (direction.equals(UP)){
            for (int i = 0; i <= moveCount - 1; i++){       //check opponent in the path, if so the path is blocked
                if (checkers[fromRow - i][fromColumn].equals(checkerOpponent)){
                    return false;
                }
            }
            if (checkers[fromRow - moveCount][fromColumn].equals(checkerSelf)){ //check the destination. if the destination has a friendly checker, the path is blocked.
                return false;
            }
        }else if (direction.equals(DOWN)){
            for (int i = 0; i <= moveCount - 1; i++){
                if (checkers[fromRow + i][fromColumn].equals(checkerOpponent)){
                    return false;
                }
            }
            if (checkers[fromRow + moveCount][fromColumn].equals(checkerSelf)){
                return false;
            }
        }else if (direction.equals(LEFT)){
            for (int i = 0; i <= moveCount-1; i++){
                if (checkers[fromRow][fromColumn - i].equals(checkerOpponent)){
                    return false;
                }
            }
            if (checkers[fromRow][fromColumn - moveCount].equals(checkerSelf)){
                return false;
            }
        }else if (direction.equals(RIGHT)){
            for (int i = 0; i <= moveCount-1; i++){
                if (checkers[fromRow][fromColumn + i].equals(checkerOpponent)){
                    return false;
                }
            }
            if (checkers[fromRow][fromColumn + moveCount].equals(checkerSelf)){
                return false;
            }
        }else if (direction.equals(UP_MAINDIAGONAL)){
            for (int i = 0; i <= moveCount-1; i++){
                if (checkers[fromRow - i][fromColumn - i].equals(checkerOpponent)){
                    return false;
                }
            }
            if (checkers[fromRow - moveCount][fromColumn - moveCount].equals(checkerSelf)){
                return false;
            }
        }else if (direction.equals(DOWN_MAINDIAGONAL)){
            for (int i = 0; i <= moveCount-1; i++){
                if (checkers[fromRow + i][fromColumn + i].equals(checkerOpponent)){
                    return false;
                }
            }
            if (checkers[fromRow + moveCount][fromColumn + moveCount].equals(checkerSelf)){
                return false;
            }
        }else if (direction.equals(UP_COUNTERDIAGONAL)){
            for (int i = 0; i <= moveCount-1; i++){
                if (checkers[fromRow - i][fromColumn + i].equals(checkerOpponent)){
                    return false;
                }
            }
            if (checkers[fromRow - moveCount][fromColumn + moveCount].equals(checkerSelf)){
                return false;
            }
        }else if (direction.equals(DOWN_COUNTERDIAGONAL)){
            for (int i = 0; i <= moveCount-1; i++){
                if (checkers[fromRow + i][fromColumn - i].equals(checkerOpponent)){
                    return false;
                }
            }
            if (checkers[fromRow + moveCount][fromColumn - moveCount].equals(checkerSelf)){
                return false;
            }
        }
        return true;
    }

    public void chessBoardUpdate(Move move){    //update the board's info of start location and destination. Including checker and valid move count in different direction.
        changedLocationUpdate(move.getFrom());
        changedLocationUpdate(move.getTo());
    }

    public void changedLocationUpdate(BoardLocation location){  // Update the count of checkers in 4 path(-,|,/,\) of a location.
        checkerNumInRowUpdate(location.getRow());
        checkerNumInColumnUpdate(location.getColumn());
        checkerNumInMainDiagonalUpdate(location);
        checkerNumInCounterDiagonalUpdate(location);
    }

    public void checkerNumInRowUpdate(int row){     //Update the count of checkers horizontally.
        int count = 0;
        for (int i = 0; i < boardSize; i++){
            if (this.checkers[row][i].equals(WHITE) || this.checkers[row][i].equals(BLACK)){
                count++;
            }
        }
        this.checkerNumInRow[row] = count;
    }

    public void checkerNumInColumnUpdate(int column){   //Update the count of checkers vertically.
        int count = 0;
        for (int i = 0; i < boardSize; i++){
            if (this.checkers[i][column].equals(WHITE) || this.checkers[i][column].equals(BLACK)){
                count++;
            }
        }
        this.checkerNumInColumn[column] = count;
    }

    public void checkerNumInMainDiagonalUpdate(BoardLocation location){     //Update the count of checkers in main diagonal.
        int row = location.getRow();
        int column = location.getColumn();
        int rowUp = row;
        int rowDown = row+ 1;
        int columnUp = column;
        int columnDown = column + 1;
        int count = 0;
        while (inputRCnumCheck(rowUp) && inputRCnumCheck(columnUp)){
            if (this.checkers[rowUp][columnUp].equals(WHITE) || this.checkers[rowUp][columnUp].equals(BLACK)){
                count++;
            }
            rowUp--;
            columnUp--;
        }
        while (inputRCnumCheck(rowDown) && inputRCnumCheck(columnDown)){
            if (this.checkers[rowDown][columnDown].equals(WHITE) || this.checkers[rowDown][columnDown].equals(BLACK)){
                count++;
            }
            rowDown++;
            columnDown++;
        }
        this.checkerNumInMainDiagonal[(boardSize-1-row) + column] = count;

    }

    public void checkerNumInCounterDiagonalUpdate(BoardLocation location){  //Update the count of checkers in counter diagonal.
        int row = location.getRow();
        int column = location.getColumn();
        int rowUp = row;
        int rowDown = row+ 1;
        int columnUp = column;
        int columnDown = column - 1;
        int count = 0;
        while (inputRCnumCheck(rowUp) && inputRCnumCheck(columnUp)){
            if (this.checkers[rowUp][columnUp].equals(WHITE) || this.checkers[rowUp][columnUp].equals(BLACK)){
                count++;
            }
            rowUp--;
            columnUp++;
        }
        while (inputRCnumCheck(rowDown) && inputRCnumCheck(columnDown)){
            if (this.checkers[rowDown][columnDown].equals(WHITE) || this.checkers[rowDown][columnDown].equals(BLACK)){
                count++;
            }
            rowDown++;
            columnDown--;
        }
        this.checkerNumInCounterDiagonal[row + column] = count;
    }

    public boolean continuousCheck(PlayerColor playerColor){    //Check whether all the checkers are continuous. Used to decided whether the game is over or not.
        Checker currentChecker;
        ArrayList<BoardLocation> checkersLocations;
        if (playerColor.equals(PLAYER_BK)){
            currentChecker = BLACK;
        }else if (playerColor.equals(PLAYER_WH)){
            currentChecker = WHITE;
        }else{
            throw new IllegalArgumentException("Invalid player!");
        }
        checkersLocations = this.getCheckerLocations(currentChecker);
        return breadthSearchChecker(checkersLocations, currentChecker);

    }

    public ArrayList<BoardLocation> getCheckerLocations(Checker currentChecker){    // Return all the locations of checkers in the given color.
        ArrayList<BoardLocation> checkersLocation = new ArrayList<>();
        for (int i = 0; i < boardSize; i++){
            for (int j = 0; j < boardSize; j++){
                if (this.checkers[i][j].equals(currentChecker)){
                    checkersLocation.add(new BoardLocation(i,j));
                }
            }
        }
        return checkersLocation;
    }

    public boolean breadthSearchChecker(ArrayList<BoardLocation> checkersLocations, Checker checker){
         /*
        Use BFS to check the continuity of checkers.
        For the given checker, check the location in 8 directions, if there are friendly checkers, add them into the queue.
        When the queue is empty, see if the number of checkers discovered equals to the total number of checker.
        If so, this color of checkers are continuous.
        */
        int checkerNum = checkersLocations.size();
        LinkedList<BoardLocation> locationQueue = new LinkedList<>();
        HashSet<BoardLocation> friendlyLocations = new HashSet<>();
        locationQueue.addFirst(checkersLocations.get(0));
        friendlyLocations.add(checkersLocations.get(0));
        int checkerCount = 0;
        while (!locationQueue.isEmpty()){
            BoardLocation currentLocation = locationQueue.pollFirst();
            checkerCount++;
            int row = currentLocation.getRow();
            int column = currentLocation.getColumn();
            ArrayList<BoardLocation> surroundedLocation = new ArrayList<>();
            surroundedLocation.add(new BoardLocation(row, column-1));
            surroundedLocation.add(new BoardLocation(row, column+1));
            surroundedLocation.add(new BoardLocation(row-1, column-1));
            surroundedLocation.add(new BoardLocation(row+1, column-1));
            surroundedLocation.add(new BoardLocation(row-1, column+1));
            surroundedLocation.add(new BoardLocation(row+1, column+1));
            surroundedLocation.add(new BoardLocation(row-1, column));
            surroundedLocation.add(new BoardLocation(row+1, column));
            for(BoardLocation location:surroundedLocation){
                if(this.locationCheck(location)){
                    if (this.getChecker(location).equals(checker) && !friendlyLocations.contains(location)){
                        locationQueue.addLast(location);
                        friendlyLocations.add(location);
                    }
                }
            }
        }
        if (checkerNum == checkerCount){
            return true;
        }else{
            return false;
        }
    }

    public void makeMove( Move move, PlayerColor player){ //Make a move and update the chessboard.

        if (moveCheck(move,player)){
            Checker temp = this.getChecker(move.getFrom());
            this.setChecker(move.getTo(),temp);
            this.setChecker(move.getFrom(),EMPTY);
            this.chessBoardUpdate(move);
        }
    }

}
