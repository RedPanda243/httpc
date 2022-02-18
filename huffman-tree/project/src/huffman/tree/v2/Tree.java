package huffman.tree;

import bitutils.BitSequence;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Tree
{
	/*---- FIELDS ----*/
	public Node root;
	// Easily access a node based on its value.
	protected Map<Integer, Node> leaves = new HashMap<>();

	public Tree()
	{
		root = new Node(null);
		leaves.put(0,root);
	}

	public void init()
	{
		extend(0);
	}

	/**
	 * Check if character exists in tree already.
	 *
	 * @param value - char to check.
	 * @return true if exists in tree.
	 */
	public boolean contains(Integer value) {
		return leaves.containsKey(value);
	}

	public boolean contains(BitSequence sequence)
	{
		Node x = getNode(sequence);
		return x!=null && x.isLeaf();
	}

	public void extend(Integer value)
	{
		Node leaf = leaves.get(value);
	//	System.out.println(leaf);
		if (leaf!=null) {
			leaf.left = new Node(leaf);
			leaf.right = new Node(leaf);
			leaf.value = -1;
			//Update values. breadthfirst sorting, left to right
			Queue<Node> queue = new LinkedList<>();
			int v = 0;
			if (root == null)
				return;
			queue.add(root);
			while(!queue.isEmpty()){
				Node node = queue.remove();
		//		System.out.println(node);
				if (node.left == null)
				{
					node.value = v;
					leaves.put(v,node);
					v++;
				}
				else
				{
					queue.add(node.left);
					queue.add(node.right);
				}
			}
		}
	}

	public boolean hasPrefix(BitSequence sequence)
	{
		Node x = getNode(sequence);
		return x!=null && x.left!=null;
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

	public BitSequence getCode(Integer c)
	{
		return (contains(c)?generateCode(this.leaves.get(c)):null);
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

	public boolean isEmpty() {
		return root.left == null;
	}
}
