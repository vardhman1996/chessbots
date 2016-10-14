package tests;

import java.util.Arrays;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.bots.AlphaBetaSearcher;
import chess.bots.JamboreeSearcher;
import chess.bots.ParallelSearcher;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Searcher;
import tests.gitlab.TestingInputs;

public class TestSeqMinimax {
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		System.out.println(depth(5,3));
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	protected static Searcher<ArrayMove, ArrayBoard> STUDENT = new JamboreeSearcher<ArrayMove, ArrayBoard>();

    protected static ArrayMove getBestMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);

        return searcher.getBestMove(ArrayBoard.FACTORY.create().init(fen), 0, 0);
    }

    protected static boolean checkResult(String fen, String[] valid, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        ArrayMove result = getBestMove(fen, searcher, depth, cutoff);
        System.out.println(result.toString() + " " + Arrays.asList(valid));
        System.out.println(fen);
        return Arrays.asList(valid).contains(result.toString());
    }  

    protected static int depth(int d, int c) {
        STUDENT.setEvaluator(new SimpleEvaluator());
        int result = 0;
        for (Object[] input : TestingInputs.FENS_TO_TEST) { 
        	boolean r = checkResult((String)input[0], ((String[][])input[1])[d - 2], STUDENT, d, c);
            System.out.println(r);
            if(!r){
            	//System.exit(0);
            }
            result += r ? 1 : 0;
//            if(result==4){
//            	System.exit(0);
//            }
        }
        return result;
    }
    
    

}
