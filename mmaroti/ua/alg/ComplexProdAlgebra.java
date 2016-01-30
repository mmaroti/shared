package mmaroti.ua.alg;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.List;
import mmaroti.ua.util.*;

public class ComplexProdAlgebra {
	protected Algebra algebra;
	protected int algebraSize;

	public Algebra algebra() {
		return algebra;
	}

	protected ComplexProdAlgebra next;
	protected int productLength;

	protected DecisionDiagram diagram;

	protected DecisionDiagram.Node emptySet;

	public DecisionDiagram.Node emptySet() {
		return emptySet;
	}

	protected DecisionDiagram.Node fullSet;

	public DecisionDiagram.Node fullSet() {
		return fullSet;
	}

	protected DecisionDiagram.Node[] nodePair;
	protected DecisionDiagram.Node[] subNodes;

	protected SoftArrayHashMap<DecisionDiagram.Node, DecisionDiagram.Node> unionCache;

	public DecisionDiagram.Node union(DecisionDiagram.Node a,
			DecisionDiagram.Node b) {
		if (a == emptySet || a == b)
			return b;
		else if (b == emptySet)
			return a;
		else if (a == fullSet || b == fullSet)
			return fullSet;

		nodePair[0] = a;
		nodePair[1] = b;
		DecisionDiagram.Node ret = unionCache.get(nodePair);
		if (ret != null)
			return ret;

		DecisionDiagram.Node[] as = a.subNodes;
		DecisionDiagram.Node[] bs = b.subNodes;

		for (int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.union(as[i], bs[i]);

		ret = diagram.canonicalize(subNodes);
		unionCache.put(nodePair.clone(), ret);

		return ret;
	}

	protected SoftArrayHashMap<DecisionDiagram.Node, DecisionDiagram.Node> intersectionCache;

	public DecisionDiagram.Node intersection(DecisionDiagram.Node a,
			DecisionDiagram.Node b) {
		if (a == fullSet || a == b)
			return b;
		else if (b == fullSet)
			return a;
		else if (a == emptySet || b == emptySet)
			return emptySet;

		nodePair[0] = a;
		nodePair[1] = b;
		DecisionDiagram.Node ret = intersectionCache.get(nodePair);
		if (ret != null)
			return ret;

		DecisionDiagram.Node[] as = a.subNodes;
		DecisionDiagram.Node[] bs = b.subNodes;
		for (int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.intersection(as[i], bs[i]);

		ret = diagram.canonicalize(subNodes);
		intersectionCache.put(nodePair.clone(), ret);

		return ret;
	}

	protected SoftHashMap<DecisionDiagram.Node, DecisionDiagram.Node> complementCache;

	public DecisionDiagram.Node complement(DecisionDiagram.Node a) {
		if (a == emptySet)
			return fullSet;
		else if (a == fullSet)
			return emptySet;

		DecisionDiagram.Node ret = complementCache.get(a);
		if (ret != null)
			return ret;

		DecisionDiagram.Node[] as = a.subNodes;
		for (int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.complement(as[i]);

		ret = diagram.canonicalize(subNodes);
		complementCache.put(a, ret);

		return ret;
	}

	protected class Arg implements Argument {
		protected int[] intArgs; // arity

		@Override
		public int[] args() {
			return intArgs;
		}

		protected DecisionDiagram.Node[][] matrix; // arity * algebraSize
		protected DecisionDiagram.Node[] nodeArgs; // arity

		public Arg(int arity) {
			intArgs = new int[arity];
			nodeArgs = new DecisionDiagram.Node[arity];
			matrix = new DecisionDiagram.Node[arity][];
		}

		@Override
		public boolean first() {
			DecisionDiagram.Node nullEntry = next.emptySet;

			int i = intArgs.length;
			while (--i >= 0) {
				DecisionDiagram.Node[] row = matrix[i];

				int j = 0;
				while (j < algebraSize && row[j] == nullEntry)
					++j;

				if (j >= algebraSize)
					return false;

				intArgs[i] = j;
				nodeArgs[i] = row[j];
			}

			return true;
		}

		@Override
		public boolean next() {
			DecisionDiagram.Node nullEntry = next.emptySet;

			int i = intArgs.length;
			while (--i >= 0) {
				DecisionDiagram.Node[] row = matrix[i];

				int j = intArgs[i];
				while (++j < algebraSize && row[j] == nullEntry)
					;

				if (j < algebraSize) {
					intArgs[i] = j;
					nodeArgs[i] = row[j];
					return true;
				}

				j = 0;
				while (row[j] == nullEntry)
					++j;

				intArgs[i] = j;
				nodeArgs[i] = row[j];
			}

			return false;
		}

	}

	protected Op[] operations;

	public DecisionDiagram.Node operationValue(int opIndex,
			DecisionDiagram.Node[] args) {
		return operations[opIndex].value(args);
	}

	protected class Op extends Arg {
		protected Function operation;
		protected Op nextOp;

		public Op() {
			super(0);
		}

		public Op(int opIndex) {
			super(algebra.operations()[opIndex].arity());

			operation = algebra.operations()[opIndex];
			nextOp = next.operations[opIndex];
			valueCache = new SoftArrayHashMap<DecisionDiagram.Node, DecisionDiagram.Node>();
		}

		protected SoftArrayHashMap<DecisionDiagram.Node, DecisionDiagram.Node> valueCache;

		public DecisionDiagram.Node value(DecisionDiagram.Node[] args) {
			if (next == null) {
				int i = args.length;
				while (--i >= 0)
					if (args[i] == emptySet)
						return emptySet;

				return fullSet;
			}

			DecisionDiagram.Node ret = valueCache.get(args);
			if (ret != null)
				return ret;

			int i = args.length;
			while (--i >= 0)
				matrix[i] = args[i].subNodes;

			i = algebraSize;
			while (--i >= 0)
				subNodes[i] = next.emptySet;

			if (first())
				do {
					int value = operation.value(intArgs);
					subNodes[value] = next.union(subNodes[value],
							nextOp.value(nodeArgs));
				} while (next());

			ret = diagram.canonicalize(subNodes);
			valueCache.put(args.clone(), ret);

			return ret;
		}
	}

	protected ComplexProdAlgebra[] productLevels() {
		ComplexProdAlgebra[] levels = new ComplexProdAlgebra[productLength + 1];

		ComplexProdAlgebra p = this;
		for (int i = 0; i <= productLength; ++i) {
			levels[i] = p;
			p = p.next;
		}

		return levels;
	}

	public Algebra[] algebras() {
		Algebra[] algebras = new Algebra[productLength];

		ComplexProdAlgebra p = this;
		for (int i = 0; i < productLength; ++i) {
			algebras[i] = p.algebra;
			p = p.next;
		}

		return algebras;
	}

	protected final DecisionDiagram.Node spike(int a, DecisionDiagram.Node rest) {
		for (int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.emptySet;

		subNodes[a] = rest;

		return diagram.canonicalize(subNodes);
	}

	public DecisionDiagram.Node spike(int[] coords) {
		if (coords.length != productLength)
			throw new IllegalArgumentException();

		ComplexProdAlgebra[] levels = productLevels();

		DecisionDiagram.Node node = levels[productLength].fullSet;
		int i = productLength;
		while (--i >= 0)
			node = levels[i].spike(coords[i], node);

		return node;
	}

	public DecisionDiagram.Node closure2(DecisionDiagram.Node a) {
		if (next == null)
			return a;

		DecisionDiagram.Node old;
		do {
			old = a;

			for (int i = 0; i < operations.length; ++i) {
				DecisionDiagram.Node[] args = new DecisionDiagram.Node[operations[i].operation
						.arity()];
				for (int j = 0; j < args.length; ++j)
					args[j] = a;

				a = union(a, operations[i].value(args));
			}
		} while (old != a);

		return a;
	}

	public DecisionDiagram.Node closure(DecisionDiagram.Node a) {
		if (next == null)
			return a;

		int index = 0;
		int completed = 0;
		while (completed < operations.length) {
			for (;;) {
				DecisionDiagram.Node[] args = new DecisionDiagram.Node[operations[index].operation
						.arity()];

				for (int j = 0; j < args.length; ++j)
					args[j] = a;

				DecisionDiagram.Node old = a;
				a = union(a, operations[index].value(args));
				System.out.println("closure size: " + a.count);

				if (old == a)
					break;

				completed = 0;
			}
			;

			++completed;
			if (++index >= operations.length)
				index = 0;
		}
		;

		return a;
	}

	protected void InitBoolean(int operationCount, int relationCount) {
		algebraSize = 0;
		productLength = 0;

		emptySet = new DecisionDiagram.Node(0);
		fullSet = new DecisionDiagram.Node(1);

		Op op = new Op();
		operations = new Op[operationCount];
		for (int i = 0; i < operationCount; ++i)
			operations[i] = op;

		// Rel rel = new Rel();
		// relations = new Rel[relationCount];
		// for(int i = 0; i < relationCount; ++i)
		// relations[i] = rel;
	}

	protected void InitAlgebra(Algebra algebra) {
		if (next == null)
			throw new IllegalStateException();

		this.algebra = algebra;
		algebraSize = algebra.size();

		productLength = next.productLength + 1;
		diagram = new DecisionDiagram();

		nodePair = new DecisionDiagram.Node[2];
		subNodes = new DecisionDiagram.Node[algebraSize];

		unionCache = new SoftArrayHashMap<DecisionDiagram.Node, DecisionDiagram.Node>();
		intersectionCache = new SoftArrayHashMap<DecisionDiagram.Node, DecisionDiagram.Node>();
		complementCache = new SoftHashMap<DecisionDiagram.Node, DecisionDiagram.Node>();

		for (int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.emptySet;
		emptySet = diagram.canonicalize(subNodes);

		for (int i = 0; i < algebraSize; ++i)
			subNodes[i] = next.fullSet;
		fullSet = diagram.canonicalize(subNodes);

		Function[] funcs = algebra.operations();
		operations = new Op[funcs.length];
		for (int i = 0; i < funcs.length; ++i)
			operations[i] = new Op(i);

		// funcs = algebra.relations();
		// relations = new Rel[funcs.length];
		// for(int i = 0; i < funcs.length; ++i)
		// relations[i] = new Rel(i);
	}

	public ComplexProdAlgebra(int operationCount, int relationCount) {
		InitBoolean(operationCount, relationCount);
	}

	public ComplexProdAlgebra(Algebra algebra, ComplexProdAlgebra next) {
		this.next = next;
		InitAlgebra(algebra);
	}

	public ComplexProdAlgebra(Algebra algebra) {
		next = new ComplexProdAlgebra(algebra.operations().length,
				algebra.relations().length);
		InitAlgebra(algebra);
	}

	public ComplexProdAlgebra(List<Algebra> algebras) {
		if (algebras.isEmpty())
			throw new IllegalArgumentException();

		Algebra a = (Algebra) algebras.get(0);
		next = new ComplexProdAlgebra(a.operations().length,
				a.relations().length);

		int i = algebras.size();
		while (--i >= 1)
			next = new ComplexProdAlgebra((Algebra) algebras.get(i), next);

		InitAlgebra((Algebra) algebras.get(0));
	}
}
