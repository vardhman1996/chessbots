package traffic;

import java.util.List;

import chess.bots.BestMove;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;
import cse332.exceptions.NotYetImplementedException;

public class TrafficSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {

    public M getBestMove(B board, int myTime, int opTime) {
    	BestMove<M> best = alphabeta(this.evaluator, board, -evaluator.infty(), evaluator.infty(), ply);
    	return best.move;
    }
    static <M extends Move<M>, B extends Board<M, B>> BestMove<M> alphabeta(Evaluator<B> evaluator, B board, int alpha, int beta, int depth) {
    	if(depth == 0) {
    		return new BestMove<M>(evaluator.eval(board));
    	}
    	List<M> moves = board.generateMoves();
    	if(moves.isEmpty()) {
    		return new BestMove<M>(evaluator.eval(board));

    	} 
    	
    	BestMove<M> bestMove = new BestMove<M>(-evaluator.infty());
    	for(M move : moves) {
    		board.applyMove(move);
    		BestMove<M> result = alphabeta(evaluator, board, -beta, -alpha, depth - 1).negate();
    		result.move = move;
    		board.undoMove();
    		
    		if(result.value > alpha) {
    			alpha = result.value;
    			bestMove = result;
    		}
    		if(alpha >= beta) {
    			return new BestMove<M>(move, alpha);
    		}
    	}
    	
    	return bestMove;
    }
}