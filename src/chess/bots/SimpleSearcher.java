package chess.bots;

import java.util.List;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;
import cse332.exceptions.NotYetImplementedException;

/**
 * This class should implement the minimax algorithm as described in the
 * assignment handouts.
 */
public class SimpleSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
	
	
    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
        BestMove<M> best = minimax(this.evaluator, board, ply);
        return best.move;
    }

    static <M extends Move<M>, B extends Board<M, B>> BestMove<M> minimax(Evaluator<B> evaluator, B board, int depth) {
    	if(depth == 0) {
    		return new BestMove<M>(evaluator.eval(board));
    	}
    	List<M> moves = board.generateMoves();
    	if(moves.isEmpty()) {
    		if(board.inCheck()) {
    			return new BestMove<M>(-evaluator.mate() - depth);
    		} else {
    			return new BestMove<M>(-evaluator.stalemate());
    		}
    	} 
    	BestMove<M> bestValue = new BestMove<M>(-evaluator.infty());

    	for(M move : moves) {
    		board.applyMove(move);
    		BestMove<M> bestMove;
    		bestMove = minimax(evaluator, board, depth-1).negate();
    		bestMove.move = move;
    		if(bestMove.value > bestValue.value) {
       			bestValue = bestMove;
       		}
    		board.undoMove();    		
    	}
    	return bestValue;
    }
}