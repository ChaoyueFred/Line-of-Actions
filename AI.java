package main;

import java.util.*;

import static main.PlayerColor.*;
import static main.Checker.*;
import static main.MoveDirection.*;

/**
 * Created by Chaoyue on 5/3/16.
 */
public class AI {
    public final static float MAX = 100000; // Max value of utility. Return this value if computer wins.
    public final static float MIN = -100000;    // Min value of utility. Return this value if human wins.
    public final static long TIMELIMIT = 10000;     //Time limit to 10s.
    private final static MoveDirection[] VALIDMOVES= new MoveDirection[]{UP,DOWN,LEFT,RIGHT,UP_MAINDIAGONAL,DOWN_MAINDIAGONAL,UP_COUNTERDIAGONAL,DOWN_COUNTERDIAGONAL}; //The 8 valid directions. Used when go through all the possible moves.
    private HashMap<Move, Float> actions = new HashMap<>(); //Stored all the possible moves and its value of the root status.
    private final PlayerColor computer;
    private final PlayerColor human;
    private final int limit; // Deepest depth the search will go. In easy mode it is 5, while in medium and hard mode it's 7.
    private final int difficulty;   //Choose the evaluation function depend on the difficulty.
    private int finalDepth = 0;     // Store the deepest depth search go.
    private int nodeNum = 0;    //Store the number of node discovered.
    private int maxNum = 0;     //Store the number of time evaluation function called in maxValue function.
    private int minNum = 0;     //Store the number of time evaluation function called in minValue function.
    private int maxPruningNum = 0;   //Store the number of time the tree was pruned in maxValue function.
    private int minPruningNum = 0;  //Store the number of time the tree was pruned in minValue function.
    private long time;     //Store the time used.



    public AI(int difficulty, PlayerColor human, PlayerColor commputer){
        this.difficulty = difficulty;
        this.human = human;
        this.computer = commputer;
        if (difficulty == 1){
            this.limit = 5;
        }else{
            this.limit = 7;
        }
    }

    public void aiInitialize(){ //Initialize all the count before calling the ai.
        actions.clear();
        finalDepth = 0;
        nodeNum = 0;
        maxNum = 0;
        minNum = 0;
        maxPruningNum = 0;
        minPruningNum = 0;
    }

    public float computeAverageSquareDistanceToCenter(PlayerColor playerColor, ChessBoard board){
        /*
        Compute the average distance of each checkers to the center of mass(COM), color decided by input.
        The distance is decided by the absolute difference of row and column.
        In difficulty 1 (easy), distance between checker and COM is the smaller difference value of row and column.
        In difficulty 2 (medium), distance between checker and COM is the larger difference value of row and column.
        In difficulty 3 (hard), distance between checker and COM is the sum of difference value of row and column.
         */
        Checker checker = getCheckerColor(playerColor);
        ArrayList<BoardLocation> boardLocations = board.getCheckerLocations(checker);
        float sumR = 0;
        float sumC = 0;
        int checkerNum = boardLocations.size();
        for (BoardLocation location : boardLocations){
            sumR +=location.getRow();
            sumC +=location.getColumn();
        }
        float aveR = sumR/checkerNum;   //row num of center of mass
        float aveC = sumC/checkerNum;   //column num of center of mass
        float sum = 0;
        for (BoardLocation location: boardLocations){
            float dR = Math.abs(aveR - location.getRow());
            float dC = Math.abs(aveC - location.getColumn());
            if (difficulty == 1){
                sum += Math.min(dR, dC);    //In the easy mode, sum up the smaller difference value of row and column to COM.
            }else if (difficulty ==2){
                sum += Math.max(dR, dC);    //In the medium mode, sum up the larger difference value of row and column to COM.
            }else{
                sum += dR + dC;     //In the hard mode, sum up the both difference value of row and column to COM.
            }
        }
        sum *= 1000;     //Multiply the sum by 1000 to make it easier to read and tell difference.
        return sum/checkerNum;
    }

    public float evaluationFunc(ChessBoard board){
        return computeAverageSquareDistanceToCenter(human, board) - computeAverageSquareDistanceToCenter(computer, board);
    }

    public Move alphaBetaSearch(ChessBoard startBoard){ //The alpha-beta search.
        Float value;    //Store the value returned by search. And decide the move by this value.
        time = System.currentTimeMillis();  // Set the start time when search begin
        Move oneMove = oneStepMoveCheck(startBoard);    //Check if the computer can win by one step. If so, take this step. In case it choose other step which also has the highest value.
        if (oneMove != null){
            return oneMove;
        }
        value = maxValue(startBoard,MIN,MAX,0);
        System.out.printf("Total depth is %d. %nTotal number of nodes generated is %d. %n" +
                "Number of times the evaluation function was called in MaxValue function is %d. %n" +
                "Number of times the evaluation function was called in MinValue function is %d. %n" +
                "Number of times pruning in MaxValue is %d. %n" +
                "Number of times pruning in MinValue is %d. %n", finalDepth, nodeNum, maxNum, minNum, maxPruningNum, minPruningNum);
        Move move = retrieveMoveFromMap(actions,value); //Get the move depend on the value.
        System.out.printf("Take move: %d %d %d %d. With value %f.%n",move.getFrom().getRow(),move.getFrom().getColumn(),move.getTo().getRow(),move.getTo().getColumn(),value);
        return move;
    }

    public Move oneStepMoveCheck(ChessBoard startBoard){   //Check if the computer can win by one step.

        for (BoardLocation location: startBoard.getCheckerLocations(getCheckerColor(computer))){
            for (MoveDirection direction: VALIDMOVES){
                Move oneStepMove = generateMove(location, direction, startBoard.moveCount(location,direction));
                if (startBoard.moveCheck(oneStepMove,computer)){
                    ChessBoard oneStepBoard = new ChessBoard(startBoard);
                    oneStepBoard.makeMove(oneStepMove,computer);
                    if (oneStepBoard.continuousCheck(computer)){
                        return oneStepMove;
                    }
                }
            }
        }
        return null;
    }

    public float maxValue(ChessBoard currentBoard, float alpha, float beta, int depth ){
        nodeNum++;  //Each time called means one more node discovered.
        if (currentBoard.continuousCheck(computer)){    //If one of the side wins, return the utility value.
                return MAX;}
        else if (currentBoard.continuousCheck(human)){
                return MIN;
            }
        Checker currentChecker = getCheckerColor(computer);
        finalDepth = Math.max(finalDepth, depth);   //Update the deepest depth.
        long dTime = System.currentTimeMillis() - time; //Check the time goes by since search start.
        if (depth == limit || dTime > TIMELIMIT){   //If time out or depth limit reached, return the evaluation value.
            maxNum++;   //number of evaluation functiong called in maxValue function plus one.
            return evaluationFunc(currentBoard);
        }
        float value = MIN;
        for (BoardLocation location: currentBoard.getCheckerLocations(currentChecker)){ //Go through all the locations of current checker.
            for (MoveDirection direction: VALIDMOVES){  //Go through all the 8 directions.
                Move tempMove = generateMove(location, direction, currentBoard.moveCount(location,direction));  //Generate the move by given location and direction.
                if (currentBoard.moveCheck(tempMove,computer)){     //If the tempMove is valid, create a copy of the current board and take the move.
                    ChessBoard tempBoard = new ChessBoard(currentBoard);
                    tempBoard.makeMove(tempMove, computer);
                    value = Math.max(value, minValue(tempBoard, alpha, beta, depth+1));
                    if (depth == 0 ){   //If the depth is 0(the move is took on the origin board), save the move and value.
                        actions.put(tempMove, value);
                    }
                    if (value >= beta){ // If the value >= beta, pruning.
                        maxPruningNum++;
                        return value;
                    }
                    alpha = Math.max(alpha, value);
                }
            }
        }
        return value;
    }

    public float minValue(ChessBoard currentBoard, float alpha, float beta, int depth ){    //Logic is same as the maxValue function.
        nodeNum++;
        if (currentBoard.continuousCheck(computer)){
            //if (player.equals(PLAYER_BK)){
                return MAX;
            }
        else if(currentBoard.continuousCheck(human)){
                return MIN;
            }
        Checker currentChecker = getCheckerColor(human);
        finalDepth = Math.max(finalDepth, depth);
        long dTime = System.currentTimeMillis() - time;
        if (depth == limit || dTime > TIMELIMIT){
            minNum++;
            return evaluationFunc(currentBoard);
        }
        float value = MAX;
        for (BoardLocation location: currentBoard.getCheckerLocations(currentChecker)){
            for (MoveDirection direction: VALIDMOVES){
                Move tempMove = generateMove(location, direction, currentBoard.moveCount(location,direction));
                if (currentBoard.moveCheck(tempMove,human)){
                    //System.out.printf("Current depth of min is %d. %n",depth);
                    ChessBoard tempBoard = new ChessBoard(currentBoard);
                    tempBoard.makeMove(tempMove, human);
                    value = Math.min(value, maxValue(tempBoard, alpha, beta, depth+1));
                    if (depth == 0 ){
                        actions.put(tempMove, value);
                    }
                    if (value <= alpha){
                        minPruningNum++;
                        return value;
                    }
                    beta = Math.min(beta, value);
                }
            }
        }
        return value;
    }

    public Move retrieveMoveFromMap(HashMap<Move,Float> actions, float value){  //Choose a move from all possible moves, If there are more than one moves share the same highest value, randomly pick one.
        Move move;
        Random random = new Random();
        ArrayList<Move> moveList = new ArrayList<>();
        for (Map.Entry<Move, Float> entry: actions.entrySet()){
            move = entry.getKey();
            if (entry.getValue() == value){
                moveList.add(move);
            }
        }
        move = moveList.get(random.nextInt(moveList.size()));
        return move;
    }

    public Move generateMove(BoardLocation location, MoveDirection direction, int moveCount){   //generate a Move by given start location, move direction and move count,
        int fromRow = location.getRow();
        int fromColumn = location.getColumn();
        int toRow;
        int toColumn;
        BoardLocation toLocation;
        switch (direction){
            case UP:
                toRow = fromRow - moveCount;
                toColumn = fromColumn;
                break;
            case DOWN:
                toRow = fromRow + moveCount;
                toColumn = fromColumn;
                break;
            case LEFT:
                toRow = fromRow;
                toColumn = fromColumn - moveCount;
                break;
            case RIGHT:
                toRow = fromRow;
                toColumn = fromColumn + moveCount;
                break;
            case UP_MAINDIAGONAL:
                toRow = fromRow - moveCount;
                toColumn = fromColumn - moveCount;
                break;
            case DOWN_MAINDIAGONAL:
                toRow = fromRow + moveCount;
                toColumn = fromColumn + moveCount;
                break;
            case UP_COUNTERDIAGONAL:
                toRow = fromRow - moveCount;
                toColumn = fromColumn + moveCount;
                break;
            case DOWN_COUNTERDIAGONAL:
                toRow = fromRow + moveCount;
                toColumn = fromColumn - moveCount;
                break;
            default:
                System.out.printf("Invalid direction! %n");
                toRow = fromRow;
                toColumn = fromColumn;
                break;
        }
        toLocation = new BoardLocation(toRow,toColumn);
        return new Move(location,toLocation);
    }

    public Checker getCheckerColor(PlayerColor player){ //Get the checker type by player.
        if (player.equals(PLAYER_BK)){
            return BLACK;
        }else{
            return WHITE;
        }
    }



}
