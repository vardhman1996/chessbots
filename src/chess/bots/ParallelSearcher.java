package chess.bots;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Move;
import cse332.exceptions.NotYetImplementedException;


public class ParallelSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
	public static final ForkJoinPool POOL = new ForkJoinPool(32);
	public static final int DIVIDE_CUT_OFF = 2;
	
    public M getBestMove(B board, int myTime, int opTime) {
    	return getBestMoveHelper(board, myTime, opTime, cutoff).move;
    }
    
    public BestMove<M> getBestMoveHelper(B board, int myTime, int opTime, int cutoff) {
    	List<M> moves = board.generateMoves();
    	BestMoveTask task = new BestMoveTask(board, myTime, opTime, ply, moves, 0, moves.size());
	    return POOL.invoke(task);
    }
    
    class BestMoveTask extends RecursiveTask<BestMove<M>>{
    	B board;
    	int myTime;
    	int opTime;
    	int ply;
    	List<M> moves;
    	int lo;
    	int hi;
		public BestMoveTask(B board, int myTime, int opTime, int ply, List<M> moves, int lo, int hi){
			this.board = board;
			this.myTime = myTime;
			this.opTime = opTime;
			this.ply = ply;
			this.moves = moves;
			this.lo = lo;
			this.hi = hi;
		}
		@Override
		protected BestMove<M> compute() {
			if(ply <= cutoff){
				return SimpleSearcher.minimax(evaluator, board, ply);
			}
			if(moves.isEmpty()) {
	    		if(board.inCheck()) {
	    			return new BestMove<M>(-evaluator.mate() - ply);
	    		} else {
	    			return new BestMove<M>(-evaluator.stalemate());
	    		}
	    	}
			
			if(hi - lo <= DIVIDE_CUT_OFF){
				BestMove<M> bestValue = new BestMove<M>(-evaluator.infty());
				BestMoveTask[] tasks = new ParallelSearcher.BestMoveTask[hi - lo];
				for(int i = lo; i < hi; i++){
					B boardNew = board.copy();
					boardNew.applyMove(moves.get(i));
					List<M> movesNew = boardNew.generateMoves();
					tasks[i - lo] = new BestMoveTask(boardNew, myTime, opTime, ply - 1,  movesNew, 0, movesNew.size());
				}
				for(int i = 0; i < tasks.length - 1; i++){
					tasks[i].fork();
				}
				BestMove<M>[] bestMoves = new BestMove[hi - lo];
				bestMoves[bestMoves.length - 1] = tasks[tasks.length - 1].compute().negate();
				bestMoves[bestMoves.length - 1].move = moves.get(bestMoves.length - 1 + lo);
				
				for(int i = 0; i < tasks.length - 1; i++){
					bestMoves[i] = tasks[i].join().negate();
					bestMoves[i].move = moves.get(i + lo);
				}
				
				
				for(BestMove<M> move: bestMoves){
					if(move.value > bestValue.value) {
	        			bestValue = move;
					}
				}
				return bestValue;
			}
			
			int mid = lo + (hi - lo)/2;
			
			BestMoveTask left  = new BestMoveTask(board, myTime, opTime, ply,  moves, lo, mid); 
			BestMoveTask right = new BestMoveTask(board, myTime, opTime, ply,  moves, mid, hi);
			
			left.fork();
			
			BestMove<M> rResult = right.compute();
			BestMove<M> lResult = left.join();
			
			if(rResult.value >= lResult.value) {
				return rResult;
			} else {
				return lResult;
			}	
		}
		
	}
}