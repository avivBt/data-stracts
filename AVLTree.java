
import java.util.Arrays;

/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with distinct integer keys and info.
 *
 */

public class AVLTree {

	private IAVLNode root = AVLNode.externalLeaf; 
	//externalLeaf is a static final show of the class AVLNode, that will be defined as the default value of a root
	
		

	/**
	 * public boolean empty()
	 *
	 * Returns true if and only if the tree is empty.
	 *O(1) complexity.
	 */
	public boolean empty() {
		return (!root.isRealNode()); //the tree is empty if its root is not "real" (meaning its an external leaf/null)
	}

	/**
	 * public String search(int k)
	 *
	 * Returns the info of an item with key k if it exists in the tree. otherwise,
	 * returns null. 
	 * runs in O(logn) complexity.
	 */
	public String search(int k) {
		if (empty())
			return null;
		IAVLNode res = search_rec(k, this.root, this.root); //calling a recursive binary search function
		if (res.getKey() == k) 
			return res.getValue();
		else //no node with key k was found; another node was returned by search_rec
			return null; 
	}

	/** private IAVLNode search_rec(int k, IAVLNode pointer, IAVLNode pointerForInsert)
	 * @pre: AVLTree !empty()
	 * a recursive function that performs binary search and returns the node of the tree with key k if exists.
	 * otherwise, returns the node that was expected to be the parent of a node with key k (pointerForInsert).
	 * runs in O(logn) complexity.
	 */

	private IAVLNode search_rec(int k, IAVLNode pointer, IAVLNode pointerForInsert) {
		if (pointer.getKey() < 0) // bottom of tree reached without finding key k; reached the expected location of key k
			return pointerForInsert;
		if (pointer.getKey() == k) //key k found
			return pointer;
		if (pointer.getKey() < k) 
			return search_rec(k, pointer.getRight(), pointer); //search continues on the right subtree. 
		return search_rec(k, pointer.getLeft(), pointer); //search continues on the left subtree. 

	}

	/**public int insert(int k, String i)
	 *
	 * Inserts an item with key k and info i to the AVL tree. The tree must remain
	 * valid, i.e. keep its invariants. Returns the number of re-balancing
	 * operations, or 0 if no re-balancing operations were necessary. A
	 * promotion/rotation counts as one re-balance operation, double-rotation is
	 * counted as 2. Returns -1 if an item with key k already exists in the tree.
	 * runs in O(logn) complexity.
	 */
	public int insert(int k, String i) {
		IAVLNode leaf = new AVLNode(k, i); //builder
		return insert_node(leaf); 
	}

	/**private int insert_node(IAVLNode leaf)
	 * 
	 * searches the required location of the node according to its key, and inserts it there if it wasn't found
	 * return the numeric result of a recursive function that balances the tree,
	 * updates the sub-fields of the nodes (min, max, size, height..)
	 * and counts the balancing operations
	 * runs in O(logn) complexity.
	 */
	
	private int insert_node(IAVLNode leaf) {
		int k = leaf.getKey();
		if (empty()) { //tree is currently empty and will now only contain the recieved node as a root
			this.root = leaf;
			return 0;
		}
		IAVLNode pointer = search_rec(k, this.root, this.root); //searching the required location
		if (pointer.getKey() == k) //the node was already in the tree
			return -1;
		
		leaf.setParent(pointer); //the node was not found; its expected parent was returned by search_rec
		if (pointer.getKey() < k) //the node belongs to the right subtree of its parent
			pointer.setRight(leaf);
		else //the node belongs to the left subtree of its parent
			pointer.setLeft(leaf);
		return balance_rec(pointer); //balancing and counting operations
		
	}
	/**private int balance_rec(IAVLNode pointer)
	 * 
	 * balances the tree from the node pointer up, by adjusting the height of the nodes in the tree
	 * and by rotating the tree in the direction needed. 
	 * counts and returns the number of balancing operations committed.
	 * also updates other fields of the nodes (min, max, size).
	 * runs in O(logn) complexity.
	 */
	private int balance_rec(IAVLNode pointer) {

		if (pointer == null) //reached the top of the tree
			return 0;
		int pointer_bf = balanceFactor(pointer); 
		int k = pointer.getHeight();
		if (pointer_bf == 0 || pointer_bf == 1 || pointer_bf == -1) { //no need of rotation around the current node
			fixNode(pointer); 
			if (pointer.getHeight() != k) //the height was changed; this operation should be in count
				return 1 + balance_rec(pointer.getParent()); //recursive call up the tree
			else //no balancing operation was committed
				return balance_rec(pointer.getParent()); //recursive call up the tree
		}		
		if (pointer_bf == 2) { // one of the cases that requires certain rotations
			int leftSon_bf = balanceFactor(pointer.getLeft());
			if (leftSon_bf > -1) { //a case that requires one rotation to the left
				LL_rotate(pointer);
				fixNode(pointer); //updating fields of the node
				if (pointer.getHeight() != k) //the height was changed; height change and rotation should be in count
					return 2 + balance_rec(pointer.getParent()); //recursive call up the tree
				else //only rotation should be in count.
					return 1 + balance_rec(pointer.getParent()); //recursive call up the tree
			}
			else{ //leftSon_bf = -1; a case that requires double rotation from the left son
				pointer = pointer.getLeft();
				RR_rotate(pointer);          //count = +1
				fixNode(pointer);   // count = +2
				pointer = pointer.getParent().getParent();
				LL_rotate(pointer);             // counter = +3
				fixNode(pointer);     // counter = +4
				return 4 + balance_rec(pointer.getParent()); //recursive call
			}
		}
		if (pointer_bf == -2) { //second case that requires certain rotations
			int rightSon_bf = balanceFactor(pointer.getRight());
			if (rightSon_bf < 1) { //rotation to the right is required
				RR_rotate(pointer);
				fixNode(pointer); //node fields update
				if (pointer.getHeight() != k)
					return 2 + balance_rec(pointer.getParent()); //height change and rotation should be in count
				else //only rotation should be in count
					return 1 + balance_rec(pointer.getParent());
			}
			else{ //rightSon_bf = 1; double rotation is required
				pointer = pointer.getRight(); 
				LL_rotate(pointer);          //count = +1
				fixNode(pointer);   // count = +2
				pointer = pointer.getParent().getParent();
				RR_rotate(pointer);             // counter = +3
				fixNode(pointer);     // counter = +4
				return 4 + balance_rec(pointer.getParent()); //recursive call
			}
		}
		return Integer.MIN_VALUE;     // indicate of error
	}

 /**private int balanceFactor(IAVLNode pointer)
	 *calculates difference between sons' heights, in order to check which balancing operations are required
	 *runs in O(1) complexity
 */
	private int balanceFactor(IAVLNode pointer) { 
		return pointer.getLeft().getHeight() - pointer.getRight().getHeight();
	}

	 /**private void LL_rotate(IAVLNode pointer)
		 *performs left rotation on the node pointer
		 *runs in O(1) complexity
	 */
	private void LL_rotate(IAVLNode pointer) {
		IAVLNode pointer_parent = pointer.getParent();
		IAVLNode pointer_son = pointer.getLeft(); 
		if (pointer_parent != null) { //basic case where the received node is not the root of the tree
			if (pointer_parent.getRight() == pointer) //the pointer is a right son; rotation step
				pointer_parent.setRight(pointer_son); 
			else // the pointer is a left son
				pointer_parent.setLeft(pointer_son);
		} 
		else //the received node is the root
			this.root = pointer_son; 
		pointer_son.setParent(pointer_parent); //rotation steps that are performed in any case
		pointer.setLeft(pointer_son.getRight());
		pointer.getLeft().setParent(pointer);

		pointer_son.setRight(pointer);
		pointer.setParent(pointer_son);
	}
	/**private void RR_rotate(IAVLNode pointer)
	 *performs right rotation on the node pointer
	 *runs in O(1) complexity
	 */
	private void RR_rotate(IAVLNode pointer) { 
		IAVLNode pointer_parent = pointer.getParent();
		IAVLNode pointer_son = pointer.getRight();

		if (pointer_parent != null) { //the received node is not the root of the tree
			if (pointer_parent.getRight() == pointer) //the pointer is a right son; rotation step
				pointer_parent.setRight(pointer_son);
			else // the pointer is a left son
				pointer_parent.setLeft(pointer_son);
		} else //the received node is the root
			this.root = pointer_son;
		pointer_son.setParent(pointer_parent); //rotation steps that are performed in any case
		pointer.setRight(pointer_son.getLeft());
		pointer.getRight().setParent(pointer);

		pointer_son.setLeft(pointer);
		pointer.setParent(pointer_son);
	}

	/**private void fixNode(IAVLNode pointer)
	 * assumes that the fields height, size, min, and max of the children on the node pointer, are correct.
	 *updates the height, size, min and max fields of a certain node, according to its children. 
	 *runs in O(1) complexity
	 */
	private void fixNode(IAVLNode pointer) {
		pointer.setHeight(1 + Math.max(pointer.getRight().getHeight(), pointer.getLeft().getHeight()));
		pointer.setSize(1 + pointer.getRight().getSize() + pointer.getLeft().getSize());
		pointer.setMin(pointer.getLeft().getMin());
		pointer.setMax(pointer.getRight().getMax());
	}

	/**public int delete(int k)
	 * 
	 *
	 * Deletes an item with key k from the binary tree, if it is there. The tree
	 * must remain valid, i.e. keep its invariants. Returns the number of
	 * re-balancing operations, or 0 if no re-balancing operations were necessary. A
	 * promotion/rotation counts as one re-balance operation, double-rotation is
	 * counted as 2. Returns -1 if an item with key k was not found in the tree.
	 * runs in O(logn) time complexity.
	 */
	public int delete(int k) {
		if (empty())
			return -1;
		IAVLNode pointer = search_rec(k, this.root, this.root); //finds the node to delete
		if (pointer.getKey() != k) //the key wasn't found, no node to delete
			return -1;
		if (pointer.getLeft().isRealNode() && pointer.getRight().isRealNode()) {  //the node has two children
			return balance_rec(switchAndDelete(pointer));  //delete the node and balance the tree from the node up 
			
		}
		
		// pointer is a leaf or unary node
		IAVLNode parent = pointer.getParent();																				
		if (parent == null) { // the deleted node is the root of the tree
			if (pointer.getLeft().isRealNode()) //the deleted root only has left son
				root = pointer.getLeft();
			else //the deleted root only has right son
				root = pointer.getRight();
			root.setParent(null); 
			return 0; // no need to balance
		}
		// pointer.parent != null
		if (pointer.getLeft().isRealNode()) { // the deleted node is a unary node with a Left son
			if (parent.getRight() == pointer) //the deleted node is the right son of its parent
				parent.setRight(pointer.getLeft()); // we skip the deleted node
			else //the deleted node is the left son of its parent
				parent.setLeft(pointer.getLeft()); // we skip the deleted node
			pointer.getLeft().setParent(parent); //we skip the deleted node
		} 
		else { // the deleted node is a unary node with a Right son
			if (parent.getRight() == pointer) //the deleted node is the right son of its parent
				parent.setRight(pointer.getRight()); // we skip the deleted node
			else //the deleted node is the left son of its parent
				parent.setLeft(pointer.getRight()); // we skip the deleted node
			pointer.getRight().setParent(parent); // we skip the deleted node

		}
		return balance_rec(parent); //balancing after deletion and returning number of balancing operations

	}

	
	/**private IAVLNode switchAndDelete(IAVLNode pointer)
	 * @pre: pointer must have a two sons
	 * deletes the pointer and switches is with its successor, returns the successor in order to balance from it up 
	 * runs in O(1) time complexity.
	 */
	private IAVLNode switchAndDelete(IAVLNode pointer) {
		IAVLNode successor = pointer.getRight();
		while (successor.getLeft().isRealNode()) //finding successor in the right subtree
			successor = successor.getLeft();        //successor has no left son & has parent
		
		IAVLNode s_parent = successor.getParent();
		if (pointer.getParent() == null) //we deleted the root
			root = successor;			
		else { 
			if (pointer == pointer.getParent().getRight()) //the deleted node is a right son to its parent
				pointer.getParent().setRight(successor); 
			else //the deleted node is a left son to its parent
				pointer.getParent().setLeft(successor);
		}
		successor.setParent(pointer.getParent()); //we skip the deleted node
		successor.setLeft(pointer.getLeft());
		successor.getLeft().setParent(successor);
		successor.setHeight(pointer.getHeight()); //we update the fields of the successor
		successor.setSize(pointer.getSize());
		successor.setMin(pointer.getMin());
		successor.setMax(pointer.getMax());
		
		if (s_parent != pointer) { //we also need to skip the successor in its original place
			s_parent.setLeft(successor.getRight());      
			s_parent.getLeft().setParent(s_parent); 
			successor.setRight(pointer.getRight());
			successor.getRight().setParent(successor);
			return s_parent; //the balancing need to start where the successor has been before 
		}
		else                                                           
			return successor; //the balancing can start in the new place of the successor, no changes were made under it
		
	}

	
	
	
	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree, or null if
	 * the tree is empty.
	 * runs in O(1) complexity
	 */
	public String min() {
		if (empty())
			return null;
		return this.root.getMin().getValue();
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if the
	 * tree is empty.
	 * runs in O(1) complexity
	 */
	public String max() {
		if (empty())
			return null;
		return this.root.getMax().getValue();
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty array
	 * if the tree is empty.
	 * runs in O(n) complexity
	 */
	public int[] keysToArray() {
		if (empty())
			return new int[0];
		int[] arr = new int[this.size()]; 
		keysToArray_rec(this.root, arr, 0); //calls a recursive function
		return arr;

	}
	/**private int keysToArray_rec(IAVLNode pointer, int[] arr, int i)
	 * adds to array arr with i items, the next item in the subtree of the node pointer
	 * returns the last location in arr that is already filled
	 * runs in O(n) complexity
	 */
	private int keysToArray_rec(IAVLNode pointer, int[] arr, int i) {
		if (!pointer.isRealNode()) //no more nodes to add
			return i;
		int j = keysToArray_rec(pointer.getLeft(), arr, i); //finds next node and location to insert to arr
		arr[j] = pointer.getKey(); //adds the current minimal key 
		j++; //increases next location to insert
		return keysToArray_rec(pointer.getRight(), arr, j); //moves to the right subtree, left subtree is fully inserted

	}

	/**
	 * public String[] infoToArray()
	 * Returns an array which contains all info in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty.
	 * runs in O(n) complexity
	 */
	public String[] infoToArray() {
		if (empty())
			return new String[0];
		String[] arr = new String[this.size()];
		infoToArray_rec(this.root, arr, 0); //calls a recursive function
		return arr;
	}

	/**private int infoToArray_rec(IAVLNode pointer, String[] arr, int i)
	 * adds to array arr with i items, the next value String in the subtree of the node pointer, according to key order
	 * returns the last location in arr that is already filled
	 * runs in O(n) complexity
	 */
	private int infoToArray_rec(IAVLNode pointer, String[] arr, int i) {
		if (!pointer.isRealNode()) //no more nodes to add
			return i;
		int j = infoToArray_rec(pointer.getLeft(), arr, i); //finds next node and location to insert to arr
		arr[j] = pointer.getValue(); //adds the value String of the node with the current minimal key
		j++; //increases next location to insert
		return infoToArray_rec(pointer.getRight(), arr, j); //moves to the right subtree, left subtree is inserted

	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 * runs in O(1) complexity
	 */
	public int size() {
		return root.getSize(); // to be replaced by student code
	}

	/**
	 * public int getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 * runs in O(1) complexity
	 */
	public IAVLNode getRoot() {
		return root;
	}

	/**
	 * public AVLTree[] split(int x)
	 *
	 * splits the tree into 2 trees according to the key x. Returns an array [t1,
	 * t2] with two AVL trees. keys(t1) < x < keys(t2).
	 * 
	 * precondition: search(x) != null (i.e. you can also assume that the tree is
	 * not empty) postcondition: none
	 * runs in O(logn) complexity
	 */

	public AVLTree[] split(int x) {
		IAVLNode pointer = search_rec(x,root,root);	//the split will start from the node with key x
		AVLTree left = new AVLTree(); 
		AVLTree right = new AVLTree();
		left.root = pointer.getLeft(); //left is a tree that all of its keys are smaller than x. no need to go down in it.
		left.root.setParent(null);
		right.root = pointer.getRight(); //right is a tree that all of its keys are bigger than x. no need to go down in it.
		right.root.setParent(null);
		return split_rec(x , pointer.getParent() , left , right); //start climbing and join the split trees to left, right.
		
	}
	/**private AVLTree[] split_rec(int x , IAVLNode pointer , AVLTree left , AVLTree right)
	 * splits the tree from pointer up, and joins the trees left and right to the trees it creates. 
	 * than, moves up the tree.
	 * runs in O(logn) complexity
	 */
	private AVLTree[] split_rec(int x , IAVLNode pointer , AVLTree left , AVLTree right) {
		if (pointer == null) //reached the top of the tree; no nodes to add to any tree
			return new AVLTree[]{left , right};
		AVLTree treeForJoin = new AVLTree();
		IAVLNode parent = pointer.getParent(); 
		if (pointer.getKey() < x) { 
			treeForJoin.root = pointer.getLeft(); //all left subtree keys are smaller than x; can be joined to left
			treeForJoin.root.setParent(null); 
			left.join(pointer , treeForJoin);
		}
		else{ //all right subtree keys are bigger than x; can be joined to right
			treeForJoin.root = pointer.getRight();
			treeForJoin.root.setParent(null);
			right.join(pointer , treeForJoin);
		}
		return split_rec(x , parent , left , right); //climb to next level
	}


	/**
	 * public int join(IAVLNode x, AVLTree t)
	 *
	 * joins t and x with the tree. Returns the complexity of the operation
	 * (|tree.rank - t.rank| + 1).
	 *
	 * precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be
	 * empty (rank = -1). postcondition: none
	 * at worst case, runs in O(logn) complexity (n is the size of the bigger tree) since this is the maximal possible
	 * value of (|tree.rank - t.rank| + 1).
	 */

	public int join(IAVLNode x, AVLTree t) {
		x.setHeight(0); // define x as a leaf
		x.setSize(1);
		x.setParent(null);
		x.setRight(AVLNode.externalLeaf);
		x.setLeft(AVLNode.externalLeaf);
		AVLTree big, small, left, right;
		IAVLNode pointer;
		if (t.root.getHeight() > this.root.getHeight()) { //t is bigger
			big = t;
			small = this;
		} else { //t is smaller
			big = this;
			small = t;
		}
		if (t.root.getKey() > this.root.getKey()) { //t keys are bigger
			left = this;
			right = t;
		} else { //t keys are smaller
			left = t;
			right = this;
		}
		int k = small.getRoot().getHeight();
		int res = big.getRoot().getHeight() - k +1; //height differences
		if (small.empty()) { // only need to add x to the bigger tree
			big.insert_node(x);
			this.root = big.root;
			return res;
		}
		if (k == 0) { // the small tree is a leaf, only need to add it and the node x to the bigger tree
			big.insert_node(x);
			big.insert_node(small.root);
			this.root = big.root;
			return res;
		}

		if (res <= 2) { //small height differences, there will be no need to balance
			x.setLeft(left.getRoot());
			x.setRight(right.getRoot());
			x.getRight().setParent(x);
			x.getLeft().setParent(x);
			fixNode(x); //update fields in node x
			this.root = x;
			return res;
		}
		if (right == small) {  //the small tree should be joined in the right side of the big tree
			pointer = big.root; 
			while (pointer.getHeight() > k) //we add the small tree in a certain level in the big tree
				pointer = pointer.getRight();			
			pointer.getParent().setRight(x); //update fields of certain nodes
			x.setParent(pointer.getParent());
			x.setLeft(pointer);
			pointer.setParent(x);
			x.setRight(small.getRoot()); 
			small.getRoot().setParent(x);
			x.setHeight(k + 1); 
			balance_rec(x); //balancing from x and up
			this.root = big.getRoot(); //update root
		}
		else { //the small tree should be joined in the left side of the big tree
			pointer = big.root;
			while (pointer.getHeight() > k) //we add the small tree in a certain level in the big tree
				pointer = pointer.getLeft();
			pointer.getParent().setLeft(x); //update fields of certain nodes
			x.setParent(pointer.getParent());
			x.setRight(pointer);
			pointer.setParent(x);
			x.setLeft(small.getRoot());
			small.getRoot().setParent(x);
			x.setHeight(k + 1);
			balance_rec(x); //balancing from x and up
			this.root = big.getRoot(); //update root
		}
		return res;
	}
	
	
	// this code is for part 2

	
	
//	public AVLTree[] split2(int x) {
//		IAVLNode pointer = search_rec(x,root,root);		
//		AVLTree left = new AVLTree();
//		AVLTree right = new AVLTree();
//		left.root = pointer.getLeft();
//		left.root.setParent(null);
//		right.root = pointer.getRight();
//		right.root.setParent(null);
//		return split2_rec(x , pointer.getParent() , left , right , 0 , 0 , 0);
//		
//	}
	
	
//	
//	private AVLTree[] split2_rec(int x , IAVLNode pointer , AVLTree left , AVLTree right,
//									int max , int sum , int counter) {
//		if (pointer == null) {
//			System.out.println("sum is " + sum);
//			System.out.println("cnt is" + counter);
//			System.out.println("avg is " + (double)sum/counter);
//			System.out.println("max is " + max);
//			return new AVLTree[]{left , right};
//		}
//		int temp;
//		AVLTree treeForJoin = new AVLTree();
//		IAVLNode parent = pointer.getParent();
//		if (pointer.getKey() < x) {
//			treeForJoin.root = pointer.getLeft();
//			treeForJoin.root.setParent(null);
//			temp = left.join(pointer , treeForJoin);
//		}
//		else{
//			treeForJoin.root = pointer.getRight();
//			treeForJoin.root.setParent(null);
//			temp = right.join(pointer , treeForJoin);
//		}
//		max = Math.max(temp, max);
//		sum += temp;
//		counter++;
//		return split2_rec(x , parent , left , right , max , sum , counter);
//	}
	/*
		public int insert_sort(int k) {
			int searchCost = 1;
			if (empty())
				 return 1;
			IAVLNode pointer=this.getRoot().getMax();
			while (pointer.getParent()!=null && pointer.getParent().getKey()>k) { 
				pointer=pointer.getParent();
				searchCost++;
			}
			while (pointer.isRealNode()) {
				if (pointer.getKey() < k)
					pointer = pointer.getRight();
				else if (pointer.getKey() > k)
					pointer = pointer.getLeft();
				else if (pointer.getKey() == k)
					break;
				searchCost++;
			}
			
			return searchCost;
		}


	/**
	 * public interface IAVLNode ! Do not delete or modify this - otherwise all
	 * tests will fail !
	 */
	public interface IAVLNode {
		public int getKey(); // Returns node's key (for virtual node return -1).

		public String getValue(); // Returns node's value [info], for virtual node returns null.

		public void setLeft(IAVLNode node); // Sets left child.

		public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.

		public void setRight(IAVLNode node); // Sets right child.

		public IAVLNode getRight(); // Returns right child, if there is no right child return null.

		public void setParent(IAVLNode node); // Sets parent.

		public IAVLNode getParent(); // Returns the parent, if there is no parent return null.

		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.

		public void setHeight(int height); // Sets the height of the node.

		public int getHeight(); // Returns the height of the node (-1 for virtual nodes).
		
		public void setSize(int size);
		
		public int getSize();
		
		public void setMin(IAVLNode min);
		
		public IAVLNode getMin();
		
		public void setMax(IAVLNode max);
		
		public IAVLNode getMax();
	}

	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree (for example AVLNode), do
	 * it in this file, not in another file.
	 * 
	 * This class can and MUST be modified (It must implement IAVLNode).
	 */
	public static class AVLNode implements IAVLNode {
		private String info;
		private int key;
		private int height;
		private int size;
		private IAVLNode left;
		private IAVLNode right;
		private IAVLNode parent;
		private IAVLNode min;
		private IAVLNode max;


		private static final IAVLNode externalLeaf = new AVLNode();

		public AVLNode() { // the default value of a new node will be an external leaf
			this.info = null;
			this.key = -1;
			this.height = -1;
			this.size = 0;
			this.left = null;
			this.right = null;
			this.parent = null;
			this.min = null;
			this.max = null;
		}

		public AVLNode(int key, String info) { // the default value of a new node will be a leaf
			this.info = info;
			this.key = key;
			this.height = 0;
			this.size = 1;
			this.left = externalLeaf;
			this.right = externalLeaf;
			this.parent = null;
			this.min = this;
			this.max = this;
		}

		public int getKey() { 
			return key; 
		}

		public String getValue() {
			return info; 
		}

		public void setLeft(IAVLNode node) {
			left = node;
			return;
		}

		public IAVLNode getLeft() {
			return left; 
		}

		public void setRight(IAVLNode node) {
			right = node;
			return; 
		}

		public IAVLNode getRight() {
			return right; 
		}

		public void setParent(IAVLNode node) {
			if (this.isRealNode()) 
				this.parent = node;
			return; 
		}

		public IAVLNode getParent() {
			return parent; 
		}

		public boolean isRealNode() {
			if (this == null)
				return false;
			return (key >= 0); //a negative key indicates an external leaf
		}

		public void setHeight(int height) {
			this.height = height; 
		}

		public int getHeight() {
			return height; 
		}
		public void setSize(int size) {
			this.size = size; 
		}

		public int getSize() {
			return size; 
		}
		
		/**public void setMin(IAVLNode node)
		 * updates the field min of the node, which holds the node with the smallest key in its subtree. O(1) complexity.
		 */
		public void setMin(IAVLNode node){ 
			if (this!=null && this.isRealNode()) {
				if (node!=null && node.isRealNode())
					this.min = node;
				else
					this.min = this;
			}
		return;
	    }
		
		public IAVLNode getMin(){
			return this.min;			
		}
		/**public void setMax(IAVLNode node)
		 * updates the field max of the node, which holds the node with the biggest key in its subtree. O(1) complexity.
		 */
		public void setMax(IAVLNode node){
			if (this!=null && this.isRealNode()) {
				if (node!=null && node.isRealNode() )
					this.max = node;
				else
					this.max = this;
			}
			return;
		}
		
		public IAVLNode getMax(){
			return this.max;
		}
	}

}
