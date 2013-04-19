package mmaroti.ua.alg;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import mmaroti.ua.util.*;

public class DecisionDiagram
{
	public static class Node
	{
		public int count;
		public Node[] subNodes;

		public boolean equals(Object o)
		{
			if( !(o instanceof NodeBuffer) )
				return this == o;
				
			Node node = (Node)o;
			return count == node.count &&
				Arrays2.shallowEquals(subNodes, node.subNodes);
		}

		public int hashCode()
		{
			int a = count;

			int i = subNodes.length;
			while( --i >= 0 )
				a += System.identityHashCode(subNodes[i]) ^ i;
			
			return a;
		}
		
		public Node(int count)
		{
			this.count = count;
			subNodes = new Node[0];
		}

		protected Node(NodeBuffer buffer)
		{
			count = buffer.count;
			subNodes = buffer.subNodes.clone();
		}
		
		protected Node() { }
		
		public String toString()
		{
			StringBuffer s = new StringBuffer();
			for(int i = 0; i < subNodes.length; ++i)
			{
				if( i != 0 )
					s.append(',');
					
				s.append(subNodes[i].toString());
			}
		
			return "(DecisionDiagram.Node 0x" + 
				Integer.toHexString(super.hashCode()) +
				" count " + Integer.toString(count) +
				" hashCode " + Integer.toString(hashCode()) + 
				" [" + s + "])";
		}
	}

	protected static class NodeBuffer extends Node
	{
		public boolean equals(Object o)
		{
			Node node = (Node)o;

			return count == node.count &&
				Arrays2.shallowEquals(subNodes, node.subNodes);
		}
		
		public void set(Node[] subNodes)
		{
			count = 0;

			int i = subNodes.length;
			while( --i >= 0 )
				count += subNodes[i].count;

			this.subNodes = subNodes;
		}
	}
				
	protected WeakHashSet nodes;

	protected NodeBuffer buffer;
	public Node canonicalize(Node[] subNodes)
	{
		buffer.set(subNodes);

		Node node = (Node)nodes.canonicalize(buffer);
		if( node == null )
		{
			node = new Node(buffer);
			nodes.add(node);
		}
		
		return node;
	}
	
	public DecisionDiagram()
	{
		nodes = new WeakHashSet();
		buffer = new NodeBuffer();
	}
}
