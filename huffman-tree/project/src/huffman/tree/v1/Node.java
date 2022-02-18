package huffman.tree;

public class Node
{

	public Node parent = null;
	public Node left = null;
	public Node right = null;

	public int weight;
	public int value = -1;
	public int index;


	/**
	 * Loading constructor
	 */
	public Node()
	{

	}

	/**
	 * Internal node constructor
	 * @param parent
	 * @param left
	 * @param right
	 * @param weight
	 * @param index
	 */
	public Node(Node parent, Node left, Node right, int weight, int index) {
		this.parent = parent;
		this.weight = weight;
		this.index = index;
		this.left = left;
		this.right = right;
	}
	
	/**
	 * NYT Node constructor.
	 * Only needs to know parent node, as NYT
	 * has no children and is always weight 0 
	 * and index 0.
	 * 
	 * @param parent
	 */
	public Node(Node parent) {
		this.parent = parent;
		this.weight = 0;
		this.index = 0;
	}
	
	/**
	 * Leaf Node constructor. 
	 * New leaf is always index 1 and weight 1.
	 * 
	 * @param parent
	 * @param value
	 */
	public Node(Node parent, int value) {
		if (value<0)
			throw new IllegalArgumentException("value < 0");
		this.parent = parent;
		this.weight = 1;
		this.index = 1;
		this.value = value;
//		this.isNYT = false;
	}
	
	public boolean isLeaf() {
		return value>-1 || isNYT();
	}
	
	public boolean isNYT() {
		return index == 0;
	}
	
	/**
	 * Return some sensible string representation of node
	 * - always returns index and weight and message about what 
	 *   sort of node it is
	 * - also returns value if is a leaf node.
	 */
	public String toString()
	{
		return "Node "+index+", "+(isLeaf()?"leaf":"internal")+". weight: "+this.weight+(isLeaf()?", value: "+this.value:", left: "+this.left.index+", right: "+this.right.index);
	}
	
	/**************************************************************
	 * GETTERS AND SETTERS
	 **************************************************************/
	
	public void increment() {
		this.weight++;
	}

}
