package huffman.tree;

import bitutils.BitSequence;

import java.io.*;
import java.util.*;

public class Tree
{
	/*---- FIELDS ----*/
	public Node root;

	// Easily access a node based on its value.
	protected Map<Integer, Node> seen = new HashMap<>();

	// Keep nodes in order based on weight. Decreasing index breadthfirst!
	protected List<Node> order = new ArrayList<>();

	public Node NYT; // Current NYT node.

	private boolean nyt = true;

	/**
	 * Make a new tree with root == NYT node.
	 * Add NYT node to order list at index 0.
	 */
	public Tree() {
		root = new Node(null);
		order.add(root);
		this.NYT = root;
	}
/*
	public AdaptiveTree(BitSequence schema, List<Integer> values)
	{
		super(schema,values);
		//Fill order. Indexes decrese breadthfirst.
		Queue<Node> queue = new LinkedList<>() ;
		if (root == null)
			return;
		queue.clear();
		queue.add(root);
		while(!queue.isEmpty()){
			Node node = queue.remove();
			order.add(0,node);
			if(node.right != null) queue.add(node.right);
			if(node.left != null) queue.add(node.left);
		}
		this.NYT = order.get(0);
		updateNodeIndices();
	}
*/
	/**
	 * Check if character exists in tree already.
	 *
	 * @param value - char to check.
	 * @return true if exists in tree.
	 */
	public boolean contains(Integer value) {
		if(seen.containsKey(value)) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean contains(BitSequence sequence)
	{
		Node x = getNode(sequence);
		return x!=null && x.isLeaf();
	}

	public boolean hasPrefix(BitSequence sequence)
	{
		Node x = getNode(sequence);
		return x!=null && x.left!=null;
	}

	private Node getNode(BitSequence sequence)
	{
		Node x = this.root;
		try
		{
			for (int b:sequence)
			{
				if (b==0)
					x = x.left;
				else
					x = x.right;
			}
		}
		catch(NullPointerException npe){return null;}
		return x;
	}

	/**
	 * Insert a value into the tree.
	 * If value already exists in tree then update node weight
	 * and rearrange tree if necessary.
	 * 
	 * @param value - value to insert into tree
	 * @return false if value does not exist and NYT is removed. In this case the method does nothing.
	 */
	public boolean insertInto(Integer value) {
		// Deal with value that exists in tree first.
		if(seen.containsKey(value))
			updateTree(seen.get(value));
		else if (nyt)
			updateTree(giveBirth(value));
		else
			return false;
		return true;
	}
	
	/**
	 * Given a value, find its code by traversing the tree.
	 * Moving left = 0 , moving right = 1
	 * 
	 * If the given value is not contained in AdaptiveTree the NYT code is returned
	 * 
	 * @param c - value to find in tree.
	 * @return - number of bits in the code.
	 */
	public BitSequence getCode(Integer c)
	{
		if(contains(c))
			return generateCode(this.seen.get(c));
/*		if(isEmpty() && !nyt) {
			return new BitSequence(); // Nothing in tree;
		}*/
		if (nyt)
			return generateCode(NYT);
		else
			return new BitSequence();
	}

	public void removeNYT()
	{
		if (nyt)
		{
			Node parent = NYT.parent;
			Node leaf = parent.right;

			parent.value = leaf.value;
			parent.left = null;
			parent.right = null;
			order.remove(0);
			order.remove(0);
			seen.put(leaf.value,parent);
			NYT = null;
			updateNodeIndices();
			this.nyt = false;
		}
	}

	public void restoreNYT()
	{
		if (!nyt)
		{
			Node node = order.get(0);
			NYT = new Node(node);
			Node leaf = new Node(node,node.value);
			node.left = NYT;
			node.right = leaf;
			node.value = -1;
			order.add(0,leaf);
			order.add(0,NYT);
			updateNodeIndices();
			nyt = true;
		}

	}

	public boolean isNytEnabled()
	{
		return nyt;
	}

	public boolean isEmpty() {
		return root.left == null;
	}
	
	public void save(File file) throws IOException
	{
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
//		System.out.println(order.get(0));
		dos.writeBoolean(nyt);
		for (Node node:order)
		{
//			System.out.println("Saving "+node);
			if (node.isLeaf())
			{
				dos.writeBoolean(true);
				dos.writeInt(node.value);
				dos.writeInt(node.weight);
			}
			else
			{
				dos.writeBoolean(false);
			}
		}
		dos.close();

	}

	public static Tree load(File file) throws IOException
	{
		Tree tree = new Tree();
		tree.root = null;
		tree.NYT = null;
		tree.order.clear();

		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		Node node;
		int i=0;
		int cont=0;
		tree.nyt = dis.readBoolean();
		while(dis.available()>0)
		{
			node = new Node();

			if (dis.readBoolean())//Node is leaf
			{
				node.value = dis.readInt();
				node.weight = dis.readInt();
				tree.seen.put(node.value,node);
			}
			else
			{
				node.left = tree.order.get(cont);
//				System.out.println(cont+"//"+tree.order.get(cont)+"//"+node.left);
				node.left.parent = node;
				cont++;
				node.right = tree.order.get(cont);
//				System.out.println(cont+"//"+tree.order.get(cont)+"//"+node.right);
				node.right.parent = node;
				cont++;
				node.weight = node.left.weight+node.right.weight;
			}
			node.index = i;
			tree.order.add(node);
			i++;
//			System.out.println("Reading "+node);
		}
		tree.root = tree.order.get(i-1);
		tree.NYT = tree.order.get(0);
		return tree;
	}
	
	/**
	 * Take current NYT node and replace it in the tree with an internal node.
	 * The internal node has an NYT node as left child, and a new leaf as right child.
	 * Weight of new internal node is weight of leaf child + NYT (which is 0).
	 */
	private Node giveBirth(int value) {
		Node newNYT = new Node(NYT);
		Node leaf = new Node(NYT, value);
		seen.put(value, leaf); // Add new value to seen.
		order.add(0,leaf);
		order.add(0,newNYT);
		
		Node oldNYT = NYT;
//		NYT.isNYT = false; // Update the current NYT so that it is now internal node.
		NYT.left = newNYT; // Set children.
		NYT.right = leaf;
		NYT = newNYT; // Turn NYT pointer into the new NYT node.
		
		updateNodeIndices();
		return oldNYT;
	}
	
	/**
	 * Update the tree nodes to preserve the invariants that
	 * sibling nodes have adjacent indices, and that parents 
	 * have indices equal to the sum of child weights.
	 * 
	 * @param node
	 */
	private void updateTree(Node node) {
		while(node != root) {
			if(maxInWeightClass(node))  {
				Node toSwap = findHighestIndexWeight(node);
				swap(toSwap,node);
				
			}
			node.increment(); // Increment node weight.
			node = node.parent;
		}
		node.increment();
		node.index = order.indexOf(node);
	}
	
	/**
	 * Check if a node is the highest indexed node
	 * for its weight value.
	 * 
	 * @param node
	 * @return
	 */
	private boolean maxInWeightClass(Node node) {
		int index = order.indexOf(node);
		for(int i = index+1; i < order.size(); i++) {
			Node next = order.get(i);
			if(next != node.parent && next.weight == node.weight) {
				return true;
			}
			else if(next != node.parent && next.weight > node.weight){
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Find the node with the highest index that is the
	 * same weight as the argument node.
	 * 
	 * @param node
	 * @return
	 */
	private Node findHighestIndexWeight(Node node) {
		Node next;
		int index = node.index + 1;
		while(order.get(index).weight == node.weight) {
			index++;
		}
		next = order.get(--index); // Overshot correct index, need to decrement.
		return next;
		
	}
	
	/**
	 * Swap 2 nodes in a tree, preserving the indices of
	 * the positions, and the parent nodes.
	 * 
	 * @param newNodePosition
	 * @param oldNodeGettingSwapped - node which needs to
	 * 		change position due to weight increment.
	 */
	private void swap(Node newNodePosition, Node oldNodeGettingSwapped) {
		int newIndex = newNodePosition.index;
		int oldIndex = oldNodeGettingSwapped.index;
		
		// Keep track of parents of both nodes getting swapped.
		Node oldParent = oldNodeGettingSwapped.parent;
		Node newParent = newNodePosition.parent;
		
		// Need to know if nodes were left or right child.
		boolean oldNodeWasOnRight, newNodePositionOnRight;
		oldNodeWasOnRight = newNodePositionOnRight = false;
		
		if(newNodePosition.parent.right == newNodePosition)
			newNodePositionOnRight = true;

		if(oldNodeGettingSwapped.parent.right == oldNodeGettingSwapped)
			oldNodeWasOnRight = true;

		if(newNodePositionOnRight)
			newParent.right = oldNodeGettingSwapped;
		else
			newParent.left = oldNodeGettingSwapped;

		if(oldNodeWasOnRight)
			oldParent.right = newNodePosition;
		else
			oldParent.left = newNodePosition;

		// Update the parent pointers. 
		oldNodeGettingSwapped.parent = newParent;
		newNodePosition.parent = oldParent;
		// Swap the indices of the nodes in order arraylist.
		order.set(newIndex, oldNodeGettingSwapped);
		order.set(oldIndex, newNodePosition);
		updateNodeIndices();
	}
	
	/**
	 * Correct the index value of a node after
	 * inserting new nodes into the order list.
	 */
	private void updateNodeIndices() {
		for(int i = 0; i < order.size(); i++) {
			order.get(i).index = i;
		/*	Node node = order.get(i);
			node.index = order.indexOf(node);*/
		}
	}

	public int getValue(BitSequence sequence)
	{
		/*Node current = root;
		for (int i=0; i<sequence.length(); i++)
		{
			if (current.isLeaf())
				return -1;
			if (sequence.get(i)==0)
				current = current.left;
			else
				current = current.right;
		}*/
		Node n = getNode(sequence);
		return (n==null)?-2:n.value;
	}

	/**
	 * Generate in reverse order a list of bits for the code of a value in the tree.
	 *
	 * List generated in reverse order because we traverse the tree
	 * from node up to root.
	 *
	 * @param in - Node to start from (leaf or nyt)
	 * @return BitSequence representing the code
	 */
	protected BitSequence generateCode(Node in) {
		BitSequence seq = new BitSequence();
		Node node = in;
		Node parent;
		while(node.parent != null) //not root
		{
			parent = node.parent;
			if(parent.left == node)
				seq = BitSequence.zero.concat(seq);
			else
				seq = BitSequence.one.concat(seq);
			node = parent;
		}
		return seq;
	}

	/**
	 * Print the nodes of the tree using either pre-order
	 * or reverse breadth first (right child first) traversal
	 * depending on which print function is used.
	 *
	 * @param breadthFirst - flag to choose which print function.
	 */
	public void printTree(boolean breadthFirst) {
		if(breadthFirst) {
			printTreeBreadth(root);
		}
		else {
			printTreeDepth(root);
		}
	}

	/**
	 * Pre-order depth first print of tree nodes.
	 *
	 * @param node
	 */
	private void printTreeDepth(Node node){
		if(node.isNYT()) {
			System.out.println(node);
		}
		else if(node.isLeaf()) {
			System.out.println(node);
		}
		else { // Node is an internal node.
			System.out.println(node);
			printTreeDepth(node.left);
			printTreeDepth(node.right);
		}
	}

	/**
	 * Breadth first printing of tree.
	 *
	 * Goes to right child of node first before left,
	 * so that nodes are printed in decreasing indices.
	 *
	 * @param root
	 */
	private void printTreeBreadth(Node root) {
		Queue<Node> queue = new LinkedList<>() ;
		if (root == null)
			return;
		queue.add(root);
		while(!queue.isEmpty()){
			Node node = queue.remove();
			System.out.println(node);
			if(node.right != null) queue.add(node.right);
			if(node.left != null) queue.add(node.left);
		}

	}
}
