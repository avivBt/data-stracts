/**
 * FibonacciHeap An implementation of a Fibonacci Heap over Integers. The heap
 * is represented by two variables of the class HeapNode - one that points to
 * the left node in the highest level of the node, and one that points to the
 * node with the smallest key, and by integers that hold its size (number of
 * nodes), the number of trees in it, the number of nodes that are marked for
 * cuts, the numbers of cuts and links that have been made in the heap since its
 * initialization. defualt values are for an empty heap.
 */
public class FibonacciHeap {
	public HeapNode min = null;
	public HeapNode first = null;
	private int size = 0;
	public int treeNum = 0;
	public int markedNodes = 0;
	public static int links = 0;
	public static int cuts = 0;

	/**
	 * public boolean isEmpty()
	 *
	 * Returns true if and only if the heap is empty. runs in O(1) complexity.
	 */
	public boolean isEmpty() {
		return first == null;
	}

	/**
	 * public HeapNode insert(int key)
	 *
	 * Creates a node (of type HeapNode) which contains the given key. Calls the
	 * function insertNode (HeapNode node) that inserts it to the heap. The added
	 * key is assumed not to already belong to the heap. Returns the newly created
	 * node. Runs in O(1) complexity.
	 */
	public HeapNode insert(int key) {
		HeapNode node = new HeapNode(key);
		insertNode(node); // insertNode will insert the node as the first root in the heap.
		return node;
	}

	/**
	 * public void insertNode(HeapNode node) updates the input node to be the first
	 * in the heap, and connects it to other nodes in the heap. updates the fields
	 * size, min and treeNum. The added node is assumed not to already belong to the
	 * heap. Runs in O(1) complexity.
	 */
	public void insertNode(HeapNode node) {
		size++; // number of nodes
		treeNum++; // number of trees
		if (this.isEmpty()) {
			min = node; // only node in the heap
			first = node; // only node in the heap
			return;
		}
		node.next = first; // insertion before the current first node
		node.prev = first.prev; // same; updating pointers
		first.prev.next = node;
		first.prev = node;
		first = node; // same
		if (node.key < min.key) // updating min field if needed
			min = node;
		return;
	}

	/**
	 * public void deleteMin()
	 *
	 * Deletes the node containing the minimum key. Updates the highest levels of
	 * the heap after the deletion and heapifies, by updating pointers and
	 * recursively (using a while loop) linking trees with same ranks. runs in O(n)
	 * complexity in the worst case of linking n trees.
	 */
	public void deleteMin() {
		if (isEmpty()) // no node to delete
			return;
		if (size == 1) {
			min = null; // updating the fields of the heap to represent an empty heap.
			first = null;
			size--;
			treeNum--;
			return;
		}
		if (min.next == min) { // min has no 'siblings' in its level
			first = min.child; // in this case, after deletion, the child's level will be the highest level
		} else if (min.child == null) { // min has siblings but no children
			min.prev.next = min.next; // in this case, we skip min in its own level's pointers
			min.next.prev = min.prev;
			first = min.next; // temporary
		} else { // min has siblings and a child
			min.prev.next = min.child; // min's children will replace it in its level; merging levels
			min.child.prev.next = min.next; // same; updating pointers
			min.next.prev = min.child.prev;
			min.child.prev = min.prev;
			first = min.next; // temporary
		}
		size--; // due to deletion
		HeapNode[] sl = new HeapNode[2 * (int) (Math.log(size) / Math.log(2)) + 2]; // array that holds subtrees
																					// according to their ranks
		HeapNode pointer = first; // the root connected in circle so it dosen't matter 
		HeapNode nextpointer;
		do {    
			nextpointer = pointer.next; // we save pointer.next before we change pointer
			pointer.parent = null;
			if (pointer.mark) ///  unmarked marked nodes because they now roots.
				markedNodes--;
			pointer.mark = false;
			int rnk = pointer.rank;
			while (sl[rnk] != null) { // there's a tree with the same rank as pointer, so we link them
				links++;
				HeapNode small;
				HeapNode big;
				if (pointer.key < sl[rnk].key) { // determining which tree should be the father tree
					small = pointer;
					big = sl[rnk];
				} else {
					small = sl[rnk];
					big = pointer;
				}
				small.rank++; // the father tree now has one more child
				if (small.child == null) { // only need to add child
					small.child = big;
					big.parent = small;
					big.next = big; // the child has no siblings, since there was no child before
					big.prev = big;
				} else {
					big.next = small.child; // the son tree has siblings and need to be connected to them
					big.prev = small.child.prev;
					big.next.prev = big;
					big.prev.next = big;
					big.parent = small;
					small.child = big;
				}
				pointer = small; // the smaller node now is the root of the new tree
				sl[rnk] = null; // there aren't anymore trees in the old rank, we linked it
				rnk++;
			}
			sl[rnk] = pointer; // the new tree, that pointer is its root, should be stored in the array 
			                     // (rank was increased)
			pointer = nextpointer; // do it all over with the next tree
		} while (nextpointer != first); // the last tree steal has first as it's next, when we get there we stop
		
		HeapNode temp = new HeapNode(Integer.MAX_VALUE);
		pointer = temp;
		int cnt = 0;
		min = temp;
		for (HeapNode node : sl) // all trees of the heap are in sl array
			if (node != null) {
				pointer.next = node; // connecting roots of all subtrees
				node.prev = pointer; // same
				pointer = node; 
				cnt++; // count trees on heap
				if (pointer.key < min.key) // updating min
					min = pointer;
			}

		first = temp.next; // first now is the first tree who got linked (with smallest rank)
		pointer.next = first; // pointer now is the last tree how got linked
		first.prev = pointer; // cutting out temp
		treeNum = cnt;
		return;

	}

	/**
	 * public HeapNode findMin()
	 *
	 * Returns the node of the heap whose key is minimal, or null if the heap is
	 * empty. runs in O(1) complexity
	 */

	public HeapNode findMin() {
		return min;
	}

	/**
	 * public void meld (FibonacciHeap heap2)
	 *
	 * Melds heap2 with the current heap by updating pointers of nodes in both
	 * heaps, and updating fields in the heaps (min, size..) runs in O(1)
	 * complexity.
	 */
	public void meld(FibonacciHeap heap2) {
		size += heap2.size; // updating fields
		treeNum += heap2.treeNum;
		HeapNode first2 = heap2.first; // linking heaps
		HeapNode firstprev = first.prev;
		firstprev.next = first2;
		first2.prev.next = first;
		first.prev = first2.prev;
		first2.prev = firstprev;
		HeapNode min2 = heap2.min;
		if (min2.key < min.key) // updating min in self heap
			min = min2;
		return;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of elements in the heap. runs in O(1) complexity.
	 */
	public int size() {
		return size;
	}

	/**
	 * public int[] countersRep()
	 *
	 * Return an array of counters. The i-th entry contains the number of trees of
	 * order i in the heap. Note: The size of of the array depends on the maximum
	 * order of a tree, and an empty heap returns an empty array. runs in O(1)
	 * complexity.
	 */
	public int[] countersRep() {
		int[] arr = new int[2 * (int) (Math.log(size) / Math.log(2)) + 2];
		HeapNode pointer = first;
		do {
			arr[pointer.rank]++; // updating relevant counter
			pointer = pointer.next;

		} while (pointer != first);
		return arr;
	}

	/**
	 * public void delete(HeapNode x)
	 *
	 * Deletes the node x from the heap. It is assumed that x indeed belongs to the
	 * heap. runs in O(n) complexity in the worst case.
	 */
	public void delete(HeapNode x) {
		x.key = Integer.MIN_VALUE;
		if (x.parent != null) { // else x is already a root
			if (!x.mark)
				markedNodes++;
			x.mark = true;
			cutNode(x); // updating the structure after the change of the key of x
		}
		min = x;
		deleteMin();
	}

	/**
	 * public void decreaseKey(HeapNode x, int delta)
	 *
	 * Decreases the key of the node x by a non-negative value delta. updates the
	 * structure of the heap by performing the needed cuts. runs in O(logn)
	 * complexity at worst case.
	 */
	public void decreaseKey(HeapNode x, int delta) {
		x.key -= delta;
		if (x.parent == null) // no need to cut above
			return;
		if (x.key >= x.parent.key) // no need to cut, structure is legal
			return;
		if (!x.mark)
			markedNodes++; // x wasn't marked and now it will be, counter updated
		x.mark = true;
		cutNode(x); // there's a need to cut
	}

	/**
	 * private void cutNode(HeapNode x) recieves a node and performs cuts from it
	 * and up to the root, recursively. checks the marks of the nodes and updates
	 * them. uses insertNode (HeapNode x) in order to insert the node in its new
	 * place. runs in O(logn) complexity at worst case.
	 */
	//@pre x.parent != null
	private void cutNode(HeapNode x) {

		if (!x.mark) { // no need to cut
			if (x.parent != null) { // else x is root and can't be marked
				x.mark = true;
				markedNodes++; // x wasn't marked, counter updated
			}
			return; // no cut is made
		}
		cuts++; // this iteration will perform a cut
		HeapNode parent = x.parent;
		parent.rank--; // due to the cut, rank is degraded
		if (x.next == x) // x has no siblings, therefore its parent remains with no children
			parent.child = null;
		else { // x has siblings, updating pointers
			if (parent.child == x)
				parent.child = x.next;
			x.next.prev = x.prev;
			x.prev.next = x.next;
		}
		// x has no siblings anymore
		x.next = x;
		x.prev = x;
		x.parent = null;
		x.mark = false; // canceling the mark, the cut was made
		markedNodes--;
		size--; // insertNode will increase the size, so we decrease it
		insertNode(x); // reinserting x as a new node
		cutNode(parent); // recursively up the tree, perhaps we need to keep cutting until the root.
	}

	/**
	 * public int potential()
	 *
	 * This function returns the current potential of the heap, which is: Potential
	 * = #trees + 2*#marked runs in O(1) complexity.
	 */
	public int potential() {
		return treeNum + 2 * markedNodes; // should be replaced by student code
	}

	/**
	 * public static int totalLinks()
	 *
	 * This static function returns the total number of link operations made during
	 * the run-time of the program. A link operation is the operation which gets as
	 * input two trees of the same rank, and generates a tree of rank bigger by one,
	 * by hanging the tree which has larger value in its root under the other tree.
	 * returns the field links of the heap, therefore runs in O(1) complexity.
	 */
	public static int totalLinks() {
		return links; // should be replaced by student code
	}

	/**
	 * public static int totalCuts()
	 *
	 * This static function returns the total number of cut operations made during
	 * the run-time of the program. A cut operation is the operation which
	 * disconnects a subtree from its parent (during decreaseKey/delete methods).
	 * returns the field cuts of the heap, therefore runs in O(1) complexity.
	 */
	public static int totalCuts() {
		return cuts; // should be replaced by student code
	}

	/**
	 * public static int[] kMin(FibonacciHeap H, int k)
	 *
	 * Returns an array of the k smallest keys in a Fibonacci heap that contains a
	 * single tree. Each time, it uses the function deleteMin and the field min.
	 * Since H contains a single tree, each time, the maximal time complexity of
	 * deleteMin is logn (as n is the size of H), which is degH. Therefore the total
	 * time complexity is klogn = kdeg(H). H isn't changed by the function.
	 */
	public static int[] kMin(FibonacciHeap H, int k) {
		k = Math.min(k, H.size); // size of array is k, unless there are no k nodes in the heap/tree.
		int[] arr = new int[k];
		for (int i = 0; i < k; i++) {
			arr[i] = H.min.key;
			H.deleteMin(); // each time we insert the minimal node to the tree, and remove it.
		}
		return arr;
	}

	/**
	 * public class HeapNode
	 * 
	 * An implementation of a Fibonacci Heap over integers. The node is represented
	 * by its integer key, rank, and by other nodes that represent the next and
	 * preview nodes in its level (never null), its parent and child (can be null).
	 * Additionally it holds a boolean value that is true iff the node is marked for
	 * cut.
	 * 
	 */
	public static class HeapNode {

		public int key;
		public int rank;
		public boolean mark;
		public HeapNode child;
		public HeapNode next;
		public HeapNode prev;
		public HeapNode parent;

		/**
		 * public HeapNode(int key) A builder in the class, initializes an unmarked
		 * node, detached from other nodes, with the input key. Runs in O(1) complexity.
		 */

		public HeapNode(int key) {
			this.key = key;
			this.mark = false; // default value is not marked for cut
			this.next = this;
			this.prev = this;
			this.parent = null; // default value is a single detached node
			this.child = null; // default value is a single detached node
			this.rank = 0; // default value is a single detached node
		}

		/**
		 * public int getKey() Returns the 'key' field of the node Runs in O(1)
		 * complexity.
		 */
		public int getKey() {
			return this.key;
		}

	}
}
