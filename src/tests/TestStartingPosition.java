package tests;

import java.util.*;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.bots.AlphaBetaSearcher;
import chess.bots.JamboreeSearcher;
import chess.bots.LazySearcher;
import chess.bots.ParallelSearcher;
import chess.bots.SimpleSearcher;
import chess.game.SimpleEvaluator;
import chess.game.SimpleTimer;
import cse332.chess.interfaces.Move;
import cse332.chess.interfaces.Searcher;

public class TestStartingPosition {
    public static final String START = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -";
    public static final String MIDDLE = "r3k2r/pp5p/2n1p1p1/2pp1p2/5B2/2qP1Q2/P1P2PPP/R4RK1 w Hkq -";
    public static final String END = "2k3r1/p6p/2n5/3pp3/1pp5/2qPP3/P1P1K2P/R1R5 w Hh -";
    public static final int depth = 5;
    
    public static ArrayMove getBestMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) { 
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);
        searcher.setEvaluator(new SimpleEvaluator());

        return searcher.getBestMove(ArrayBoard.FACTORY.create().init(fen), 0, 0);
    }
    
    public static void printMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
//        String botName = searcher1.getClass().toString().split(" ")[1].replace("chess.bots.", "");
        getBestMove(fen, searcher, depth, cutoff);
    }
    public static void main(String[] args) {
    	Searcher<ArrayMove, ArrayBoard> dumb = new ParallelSearcher<>();
        Searcher<ArrayMove, ArrayBoard> dumb2 = new JamboreeSearcher<>();
        long start, end;
        	
    	start = System.currentTimeMillis();
    	printMove(START, dumb, depth, 3);
    	end = System.currentTimeMillis();
    	System.out.println("ParallelSearcher (FEN: START) time " + (end - start));
        	
    	start = System.currentTimeMillis();
    	printMove(START, dumb2, depth, 3);
    	end = System.currentTimeMillis();
    	System.out.println("JamboreeSearcher (FEN: START) time " + (end - start));
        	
    	start = System.currentTimeMillis();
    	printMove(MIDDLE, dumb, depth, 3);
    	end = System.currentTimeMillis();
    	System.out.println("ParallelSearcher (FEN: MIDDLE), time " + (end - start));
        	
    	start = System.currentTimeMillis();
    	printMove(MIDDLE, dumb2, depth, 3);
    	end = System.currentTimeMillis();
    	System.out.println("JamboreeSearcher (FEN: MIDDLE), time " + (end - start));
        	
    	start = System.currentTimeMillis();
    	printMove(END, dumb, depth, 3);
    	end = System.currentTimeMillis();
    	System.out.println("ParallelSearcher (FEN: END), time " + (end - start));
        	
    	start = System.currentTimeMillis();
    	printMove(END, dumb2, depth, 3);
    	end = System.currentTimeMillis();
    	System.out.println("JamboreeSearcher (FEN: END), time " + (end - start));
        	
        	
    	System.out.println("--------------------------------------------------------------------------------------------------------------------------");
        
    }
}
