package main;

import java.util.Scanner;

import static main.PlayerColor.PLAYER_BK;
import static main.PlayerColor.PLAYER_WH;

/**
 * Created by Chaoyue on 5/2/16.
 */
public class Main {     //The game console function. The main running function.
    public static void main(String[] args){
        Main gameConsole = new Main();
        System.out.printf("Welcome to the world of Lines of Actions!%n");
        ChessBoard chessBoard = gameConsole.createBoard();
        int difficulty = gameConsole.getDifficulty();
        System.out.printf("Initial chessboard is followed:%n");
        chessBoard.chessBoardDisplay();
        PlayerColor human = gameConsole.getHumanColor();
        PlayerColor computer = gameConsole.getComputerColor(human);
        AI ai = new AI(difficulty, human, computer);
        PlayerColor currentPlayer;
        if (human.equals(PLAYER_BK)) {
            currentPlayer = computer;
        }else{
            currentPlayer = human;
        }
        do {
            currentPlayer = gameConsole.getOpponent(currentPlayer);
            if (human.equals(currentPlayer)){
                Move move = gameConsole.getMove(human,chessBoard);
                chessBoard.makeMove(move, human);
                chessBoard.chessBoardDisplay();
            }else{
                System.out.printf("Computer's turn: %n");
                ai.aiInitialize();
                Move move = ai.alphaBetaSearch(chessBoard);
                chessBoard.makeMove(move,computer);
                chessBoard.chessBoardDisplay();
            }
        }while(!chessBoard.continuousCheck(currentPlayer) && !chessBoard.continuousCheck(gameConsole.getComputerColor(currentPlayer))); //When one of the player wins. Jump out the loop.

        if (currentPlayer.equals(human)) {
            System.out.printf("Game Over! The winner is human!%n");
        }else{
            System.out.printf("Game Over! The winner is computer!%n");
        }
        System.exit(0);
    }

    private int getDifficulty() {   //Get difficulty from terminal
        System.out.print("Choose the difficulty for game, 1 for easy, 2 for medium, 3 for hard:");
        Scanner scanner = new Scanner(System.in);
        int difficulty = scanner.nextInt();
        while (difficulty != 1 && difficulty != 2 && difficulty != 3){
            System.out.printf("Wrong size input. Please reenter the size: ");
            difficulty = scanner.nextInt();
        }
        if (difficulty == 1){
            return 1;
        }else if (difficulty == 2){
            return 2;
        }else {
            return 3;
        }
    }

    public ChessBoard createBoard(){    //Get the board size from terminal and return a corresponding chess board.
        System.out.print("Choose the size of game, 5 for '5*5', 6 for '6*6':");
        Scanner scanner = new Scanner(System.in);
        int boardSize = scanner.nextInt();
        while (boardSize != 5 && boardSize != 6){
            System.out.printf("Wrong size input. Please re-enter the size: ");
            boardSize = scanner.nextInt();
        }
        if (boardSize == 5){
            return new ChessBoard(5);
        }else{
            return new ChessBoard(6);
        }
    }

    public PlayerColor getHumanColor(){     //Get the color chose by human from terminal.
        System.out.print("Choose your color, type in 'B' for black or 'W' for white:");
        Scanner scanner = new Scanner(System.in);
        String color = scanner.next();
        while (!color.equals("B") && !color.equals("W")){
            System.out.printf("Wrong color input. Please reenter the color: ");
            color = scanner.next();
        }
        if (color.equals("B")){
            return PLAYER_BK;
        }else{
            return PLAYER_WH;
        }
    }

    public PlayerColor getComputerColor(PlayerColor human){     //Decide the color of computer depending on the human color.
        if (human.equals(PLAYER_BK)){
            return PLAYER_WH;
        }else {
            return PLAYER_BK;
        }
    }

    public Move getMove(PlayerColor player, ChessBoard chessBoard) {    //Generate the move from the location input by human. If the move is wrong, print error message, and let human re-enter.
        boolean flag;
        Move move = null;
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.printf("Input the location of checker you pick and destination by row(1-%d) and column(1-%d) respectively, separate the four numbers by space: %n", chessBoard.boardSize, chessBoard.boardSize);
            String input = scanner.nextLine();
            input = input.trim();
            String[] inputs = input.split(" ");
            if (!inputFormatCheck(inputs)){
                flag = false;
            }else{
                move = generateMove(inputs);
                if (!chessBoard.moveCheck(move, player)){
                    System.out.printf("Invalid move!");
                    flag = false;
                }else{
                    flag = true;
                }
            }
        } while(!flag);
        return move;
    }

    public boolean inputFormatCheck(String[] inputs){   //Check the format of move input of human, which should be at least 4 numbers.
        if (inputs.length != 4){
            System.out.printf("Wrong number of locations.%n");
            return false;
        }
        try {
            Integer.parseInt(inputs[0]);
            Integer.parseInt(inputs[1]);
            Integer.parseInt(inputs[2]);
            Integer.parseInt(inputs[3]);
        }catch(NumberFormatException nfe){
            System.out.printf("Wrong number of format.%n");
            return false;
        }
        return true;
    }

    public Move generateMove(String[] inputs){      //Generate the move by the input of human, which is will be to send to the move validity check function.
        int fromRow = Integer.parseInt(inputs[0]);
        int fromColumn = Integer.parseInt(inputs[1]);
        int toRow = Integer.parseInt(inputs[2]);
        int toColumn = Integer.parseInt(inputs[3]);
        return new Move(new BoardLocation(fromRow-1,fromColumn-1), new BoardLocation(toRow-1,toColumn-1));
    }

    public PlayerColor getOpponent(PlayerColor player){     //Get the opponent color of current player.
        if (player.equals(PLAYER_BK)){
            return PLAYER_WH;
        }else{
            return PLAYER_BK;
        }
    }

}
