package chess.bots;


import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * See cse332/interfaces/worklists/PriorityWorkList.java
 * for method specifications.
 */
public class MinFourHeap<E> extends PriorityWorkList<E> {
    private static final int DEFAULT_SIZE = 10; 
    private static final int D = 4;
    
    private Comparator<E> comp;
	private E[] data;
    private int heapSize;
    
    public MinFourHeap(Comparator<E> comp) {
        data = (E[]) new Object[DEFAULT_SIZE];  
        this.comp = comp;
    	heapSize = 0;
    }

    @Override
    public boolean hasWork() {
        return (heapSize > 0);
    }

    @Override
    public void add(E work) {
    	// If we've filled up our array, move to a new array with twice the length
    	if (heapSize >= data.length){
    		// Create the new array
        	E[] newData = (E[]) new Object[data.length * 2];
        	// Copy values over
        	for (int i = 0; i < heapSize; i++){
        		newData[i] = data[i];
        	}
        	// Update the field
        	data = newData;
        } 
    	// Move the given work to the next open space
    	data[heapSize] = work;
    	// Mark the increase in size
    	heapSize++;
    	// Make sure the heap is still properly ordered
    	percolateUp();
    }

    @Override
    public E peek() {
    	if (heapSize == 0) {
    		throw new NoSuchElementException("WorkList is empty");   	
    	}
    	return data[0];
    }

    @Override
    public E next() {
    	if (heapSize == 0) {
    		throw new NoSuchElementException("WorkList is empty");   
    	}
    	// Store a copy of the data at the root, to return
    	E min = data[0];
    	// Move the last element to the root
    	data[0] = data[heapSize - 1];
    	// Shorten the array
    	heapSize--;
    	// Make sure the heap is still properly ordered
    	percolateDown();

    	return min;
    }

    @Override
    public int size() {
        return heapSize;
    }

    @Override
    public void clear() {
    	data = (E[]) new Object[DEFAULT_SIZE];       	
    	heapSize = 0;
    }
    
    private void percolateUp() {
    	// Start at the last element in the heap
    	int currentChild = heapSize - 1;
    	int parent = parentIndex(currentChild);
    	// Swap the current child with the parent until properly ordered
    	while(currentChild > 0 && (comp.compare(data[currentChild], data[parent]) < 0)){
    		swap(currentChild, parent);
    		currentChild = parent;
    		parent = parentIndex(currentChild);
    	}
    }
    private void percolateDown() {
    	// Start at the first element in the heap
    	int parent = 0;
    	// Try to find a child that's smaller than it
    	int smallestChildIndex = findSmallestChildIndex(parent);
    	// While there's a child that is smaller than the parent
    	while(smallestChildIndex != -1){
    		// Swap the parent with that child
    		swap(parent, smallestChildIndex);
    		// Find the new position of the parent
    		parent = smallestChildIndex;
    		// And keep checking for a smaller child
    		smallestChildIndex = findSmallestChildIndex(parent);
    	}
    }
    // Finds the index of the smallest child of a given node
    // Returns -1 if no child is smaller than the parent
    private int findSmallestChildIndex(int parent) {
    	int smallestIndex = -1;
    	E min = data[parent];
    	
    	// Start at the first child
    	int currentChild = firstChildIndex(parent);
    	// Check up to D children for a smaller one (but don't check beyond the bounds of the heap!)
    	while(currentChild < heapSize && currentChild < firstChildIndex(parent) + D){
    		if(comp.compare(min, data[currentChild]) > 0){
    			smallestIndex = currentChild;
    			min = data[currentChild];
    		}
    		currentChild++;
    	}
    	
    	return smallestIndex;
    }
    
    // Swaps data at two positions in the heap
    private void swap(int index1, int index2) {
    	E temp = data[index1];
		data[index1] = data[index2];
		data[index2] = temp;
    }
    
    // Returns the index of the parent node of a given node (assuming one exists)
    private int parentIndex(int child) {
    	return (child - 1)/D;
    }
    
    // Returns the index for the first child node of a given node (assuming one exists)
    private int firstChildIndex(int parent) {
    	return D * parent + 1;
    }
 }
