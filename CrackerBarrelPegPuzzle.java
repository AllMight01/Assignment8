package crackerbarrelpegpuzzle;
import java.util.*;

/*
##### BOARD ####
# 24 23 22 21 20
# 19 18 17 16 15
# 14 13 12 11 10
#  9  8  7  6  5
#  4  3  2  1  0
 */

public class CrackerBarrelPegPuzzle {

    // list of seen boards - this is used to prevent rechecking of paths
    private static final HashSet<Long> seenBoards = new HashSet<Long>();

    // holds all 36 moves that are possible
    private static final long[][] moves = new long[36][];

    // list of solution boards in ascending order - filled in once the solution is found
    private static final ArrayList<Long> solution = new ArrayList<Long>();

    private static final ArrayList<long[]> finalMoves = new ArrayList<long[]>();

    public static void setInitialBoard(long initialBoard) {
        INITIAL_BOARD = initialBoard;
    }

    public static void setGoalBoard(long goalBoard) {
        GOAL_BOARD = goalBoard;
    }

    // initial board (one marble free in center)
    private static long INITIAL_BOARD = 105983;

    // goal board (one marble in center)
    private static long GOAL_BOARD = 1048576;

    // board that contains a ball in every available slot
    private static final long VALID_BOARD_CELLS = 1154559;


    static void initialize(int initial_position) {
        int boardBits = 0;
        String boardOppBits = "";
        int counter = 0;

        // printing the initial position of board
        for (int i = 0; i < 5; i++) {
            System.out.print("  ");
            for (int k = 0; k < 4 - i; k++) {
                boardOppBits += "0";
            }

            for (int j = 0; j < 5; j++) {
                if (j <= i) {
                    if (counter == initial_position) {
                        boardOppBits += "1";
                    } else {
                        boardOppBits += "0";
                    }

                    counter += 1;
                }

            }

        }
        boardBits = Integer.parseInt(boardOppBits, 2) ^ 1154559;

        //initial board
        setInitialBoard(boardBits);

        //goal board
        if ((initial_position) == 4)
            setGoalBoard(4);
        else if ((initial_position) == 8)
            setGoalBoard(4096);
        else if(initial_position == 7)
            setGoalBoard(1024);
        else
            setGoalBoard(Integer.parseInt(boardOppBits, 2));


    }

    // create the two possible moves for the three added pegs
    private static void createMoves(int bit1, int bit2, int bit3, ArrayList<long[]> moves) {
        moves.add(new long[]{(1L << bit1), (1L << bit2) | (1L << bit3), (1L << bit1) | (1L << bit2) | (1L << bit3)});
        moves.add(new long[]{(1L << bit3), (1L << bit2) | (1L << bit1), (1L << bit1) | (1L << bit2) | (1L << bit3)});

    }

    // do the calculation recursively by starting from
    private static boolean search(long board) {
        // for all possible moves
        for (long[] move : moves) {
            // check if the move is valid
            if ((move[1] & board) == 0L && (move[0] & board) != 0L) {
                // calculate the board after this move was applied
                long newBoard = board ^ move[2];
                // only continue processing if we have not seen this board before
                if (!seenBoards.contains(newBoard)) {
                    seenBoards.add(newBoard);
                    // check if the initial board is reached
                    if (newBoard == INITIAL_BOARD || search(newBoard)) {
                        solution.add(board);
                        finalMoves.add(move);
                        return true;
                    }
                }
            }
        }

        return false;
    }


    private static void convertMove(long[] moves) {
        String Newmove = Long.toBinaryString(moves[2]);
        int count = 0;
        ArrayList<Integer> tof = new ArrayList<>();


        int count1 = 5;
        if ((Newmove).length() >= 5) {
            for (int x = ((Newmove).length() - 1); x > -1; x -= 5) {
                for (int y = x; y > (x - count1); y--) {
                    if (y > -1 && (Newmove.charAt(y)) == '0')
                        count += 1;
                    if (y > -1 && (Newmove.charAt(y)) == '1') {
                        count += 1;
                        tof.add(15 - count);
                    }
                }
                count1--;
            }
        } else {

            for (int y = (Newmove).length() - 1; y > -1; y--) {
                if (y > -1 && (Newmove.charAt(y)) == '0')
                    count += 1;
                if (y > -1 && (Newmove.charAt(y)) == '1') {
                    count += 1;
                    tof.add(15 - count);
                }
            }

        }
        
        System.out.println();
        System.out.println();
    }

    // print the board
    private static void printBoard(long board) {
        for (int i = 24; i > -1; i--) {
            boolean validCell = ((1L << i) & VALID_BOARD_CELLS) != 0L;
            System.out.print(validCell ? (((1L << i) & board) != 0L ? "x " : ". ") : " ");
            if (i % 5 == 0) System.out.println();
        }
    }

    public static void main(String[] args) {
        
        int initialPos = 0, i, j;
        initialize(initialPos);


        // add starting board
        solution.add(INITIAL_BOARD);

        for(j = 0; j < 5; j++) {
            
            System.out.println();
            System.out.println("=== " + j + " ===");
            
            seenBoards.clear();
            ArrayList<long[]> moves = new ArrayList<long[]>();
            int[] startsX = new int[]{4, 3, 2, 8, 7, 12};
            for (int x : startsX) {
                createMoves(x, x - 1, x - 2, moves);
            }
            // holds all starting positions in north-south direction
            int[] startsY = new int[]{20, 15, 10, 16, 11, 12};
            for (int y : startsY) {
                createMoves(y, y - 5, y - 10, moves);
            }
            // holds all starting positions in north-south direction
            int[] startsZ = new int[]{4, 8, 12, 3, 7, 2};
            for (int z : startsZ) {
                createMoves(z, z + 4, z + 8, moves);
            }

            Collections.shuffle(moves);
            moves.toArray(CrackerBarrelPegPuzzle.moves);
            // start recursively search for the initial board from the goal
            search(GOAL_BOARD);


            // the found solution
            i = 0;
            for (long step : solution) {
                printBoard(step);

                if (i < (finalMoves).size()) {
                    convertMove(finalMoves.get(i));
                    i += 1;
                }
            }
        }
    }
}