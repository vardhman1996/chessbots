package chess.bots;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import chess.bots.ParallelSearcher.BestMoveTask;
import chess.game.SimpleTimer;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Move;
import cse332.exceptions.NotYetImplementedException;

public class JamboreeSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
	
	public static final int DIVIDE_CUT_OFF = 2;
	public static final double PERCENTAGE_SEQ = 0.5;
	public static final ForkJoinPool POOL = new ForkJoinPool(32);
	
    public M getBestMove(B board, int myTime, int opTime) {
    	BestMove<M> move = new BestMove<M>(-evaluator.infty());
    	List<M> moves = board.generateMoves();
    	SimpleTimer timer = new SimpleTimer((int) System.currentTimeMillis(), 60000);
    	timer.start(450);
    	timer.notOkToTimeup();
    	int i = 1;
    	while(i <= ply){
    		if(ply == 6){
    			timer.okToTimeup();
    		}
    		if(timer.timeup()){
    			break;
    		}
    		BestMoveTask task = new BestMoveTask(board, myTime, opTime, i, -evaluator.infty(), evaluator.infty(), moves, 0, moves.size());
		    move = POOL.invoke(task);
	    	moves.remove(move.move);
	    	moves.add(0, move.move);
	    	i++;
    	}
		timer.stop();
    	return move.move;
    }
    
    class BestMoveTask extends RecursiveTask<BestMove<M>>{
    	B board;
    	int myTime;
    	int opTime;
    	int ply;
    	List<M> moves;
    	int alpha;
    	int beta;
    	int lo;
    	int hi;
    	
		public BestMoveTask(B board, int myTime, int opTime, int ply, int alpha, int beta, List<M> moves, int lo, int hi){
			this.board = board;
			this.myTime = myTime;
			this.opTime = opTime;
			this.ply = ply;
			this.moves = moves;
			this.alpha = alpha;
			this.beta = beta;
			this.lo = lo;
			this.hi = hi;
		}
		
		@Override
		protected BestMove<M> compute() {
			if(ply <= cutoff){
				return AlphaBetaSearcher.alphabeta(evaluator, board, alpha, beta, ply);
			}
			if(hi - lo <= 0){
				return new BestMove<M>(-evaluator.infty());
			}
			if(moves.isEmpty()) {
	    		if(board.inCheck()) {
	    			return new BestMove<M>(-evaluator.mate() - ply);
	    		} else {
	    			return new BestMove<M>(-evaluator.stalemate());
	    		}
	    	}
									
			if(hi - lo <= DIVIDE_CUT_OFF) {
				BestMove<M> bestValue = new BestMove<M>(alpha);
				@SuppressWarnings("unchecked")
				BestMoveTask[] tasks = new JamboreeSearcher.BestMoveTask[hi - lo];
				for(int i = lo; i < hi; i++){
					B boardNew = board.copy();
					boardNew.applyMove(moves.get(i));
					List<M> movesNew = boardNew.generateMoves();
					tasks[i - lo] = new BestMoveTask(boardNew, myTime, opTime, ply - 1, -beta, -alpha,  movesNew, 0, movesNew.size());
					if(i < hi - 1){
						tasks[i - lo].fork();
					}	
				}

				BestMove<M> bestMoves = tasks[tasks.length - 1].compute().negate();
				bestMoves.move = moves.get(hi - 1);
				bestValue = bestMoves;
				for(int i = 0; i < tasks.length - 1; i++){
					BestMove<M> temp = tasks[i].join().negate();
					temp.move = moves.get(i + lo);
					if(bestValue.value < temp.value){
						bestValue = temp;
					}
					if(bestValue.value > alpha) {
		    			alpha = bestValue.value;
		    		}
		    		if(alpha >= beta) {
		    			return new BestMove<M>(bestValue.move, alpha);
		    		}
				}
				return bestValue;
			}
			
			
			BestMove<M> bestMove = new BestMove<M>(alpha);
			if(hi - lo >= moves.size()){
				int length = (int) (PERCENTAGE_SEQ * moves.size());
				for(int i = 0; i < length; i++) {
					board.applyMove(moves.get(i));
					List<M> movesNew = board.generateMoves();
		    		BestMove<M> result = new BestMoveTask(board, myTime, opTime, ply - 1, -beta, -alpha, movesNew, 0, movesNew.size()).compute().negate();
		    		board.undoMove();
		    		result.move = moves.get(i);
		    		if(result.value > alpha) {
		    			alpha = result.value;
		    			bestMove = result;
		    		}
		    		if(alpha >= beta) {
		    			return new BestMove<M>(moves.get(i), alpha);
		    		}
		    	}
				lo = length;
			}
			int mid = lo + (hi - lo)/2;
			BestMoveTask left  = new BestMoveTask(board, myTime, opTime, ply, alpha, beta, moves, lo, mid); 
			BestMoveTask right = new BestMoveTask(board, myTime, opTime, ply, alpha, beta, moves, mid, hi);
			
			left.fork();
			
			BestMove<M> rResult = right.compute();
			BestMove<M> lResult = left.join();
			
			
			BestMove<M> temp = null;
			if(rResult.value >= lResult.value) {
				temp = rResult;
			} else {
				temp = lResult;
			}

			return temp.value > bestMove.value ? temp : bestMove;

		}
		
		
	    	
    }
    
    
}