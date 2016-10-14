package chess.bots;

import java.util.Comparator;
import java.util.List;

import cse332.exceptions.NotYetImplementedException;

public class TopKSort {
    

    public static <E> void sort(List<E> array, int k, Comparator<E> comparator) {
        MinFourHeap<E> heap = new MinFourHeap<E>(comparator);
        for(E e: array){
            if(heap.size() < k){
                heap.add(e);
            }
            else{
                E temp = heap.peek();
                if(comparator.compare(e, temp) > 0){
                    heap.next();
                    heap.add (e);
                }
            }
        }
        for(int i = 0; i < array.size(); i++){
            if(heap.hasWork()){
                array.set(i, (E) heap.next());
            }
            else{
                array.set(i, null);
            }
        }
    }
}
