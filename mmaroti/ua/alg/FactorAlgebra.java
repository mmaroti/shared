package mmaroti.ua.alg;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.*;
import mmaroti.ua.util.*;

public class FactorAlgebra implements Algebra {
	protected Algebra algebra;
	protected int[] abstractElems;
	protected int[] concreteElems;

	@Override
	public int size() {
		return concreteElems.length;
	}

	public int toAbstractElem(int elem) {
		return abstractElems[elem];
	}

	public int toConcreteElem(int block) {
		return concreteElems[block];
	}

	protected Op[] operations;

	@Override
	public Function[] operations() {
		return operations;
	}

	protected class Op implements Function {
		protected Function op;

		@Override
		public int size() {
			return concreteElems.length;
		}

		@Override
		public int arity() {
			return op.arity();
		}

		protected int[] args;

		@Override
		public int value(int[] args) {
			for (int i = 0; i < args.length; ++i)
				this.args[i] = toConcreteElem(args[i]);

			return toAbstractElem(op.value(this.args));
		}

		public Op(Function operation) {
			this.op = operation;
			this.args = new int[op.arity()];
		}
	}

	@Override
	public Function[] relations() {
		return new Function[0];
	}

	public List<IntArray> concreteElems() {
		ArrayList<IntArray> list = new ArrayList<IntArray>(concreteElems.length);

		for (int i = 0; i < concreteElems.length; ++i) {
			int c = 0;
			for (int j = 0; j < abstractElems.length; ++j)
				if (abstractElems[j] == i)
					++c;

			int a[] = new int[c];

			c = 0;
			for (int j = 0; j < abstractElems.length; ++j)
				if (abstractElems[j] == i)
					a[c++] = j;

			list.add(new IntArrayBuffer(a));
		}

		return list;
	}

	public FactorAlgebra(Algebra algebra, Equivalence equ) {
		int size = algebra.size();
		if (size != equ.size())
			throw new IllegalArgumentException();

		int[] repr = equ.repr;
		abstractElems = new int[size];

		int blockCount = 0;
		for (int i = 0; i < size; ++i) {
			abstractElems[i] = (repr[i] != i) ? abstractElems[repr[i]]
					: blockCount++;
		}

		concreteElems = new int[blockCount];
		blockCount = 0;
		for (int i = 0; i < size; ++i)
			if (repr[i] == i)
				concreteElems[blockCount++] = i;

		this.algebra = algebra;

		Function[] operations = algebra.operations();
		this.operations = new Op[operations.length];
		for (int i = 0; i < operations.length; ++i)
			this.operations[i] = new Op(operations[i]);
	}

	public static Equivalence congruence(Algebra algebra,
			LinkedList<IntPair> pairs) {
		int size = algebra.size();
		Function[] operations = algebra.operations();
		Equivalence equ = Equivalence.zero(size);

		Iterator<IntPair> iter = pairs.iterator();
		while (iter.hasNext()) {
			IntPair pair = iter.next();
			equ.join(pair.first, pair.second);
		}

		while (!pairs.isEmpty()) {
			IntPair pair = pairs.removeFirst();
			int a = pair.first;
			int b = pair.second;

			for (int i = 0; i < operations.length; ++i) {
				Function op = operations[i];
				int arity = op.arity();
				for (int j = 0; j < arity; ++j) {
					UnaryPolArgument arg = new UnaryPolArgument(arity, size, j);
					int[] args = arg.args();
					if (arg.first())
						do {
							args[j] = a;
							int c = op.value(args);

							args[j] = b;
							int d = op.value(args);

							if (!equ.related(c, d)) {
								equ.join(c, d);
								pairs.add(new IntPair(c, d));
							}
						} while (arg.next());
				}
			}
		}

		return equ;
	}

	public static Equivalence congruence(Algebra algebra, int a, int b) {
		LinkedList<IntPair> pairs = new LinkedList<IntPair>();
		pairs.add(new IntPair(a, b));
		return congruence(algebra, pairs);
	}

	public static Equivalence getMonolith(Algebra algebra) {
		int size = algebra.size();
		Equivalence cong = Equivalence.one(size);

		for (int i = 0; i < size - 1; ++i)
			for (int j = i + 1; j < size; ++j) {
				if (cong.blockCount() == size)
					break;

				cong.meet(congruence(algebra, i, j));
			}

		return cong;
	}

	public static boolean isSubdirectlyIrreducible(Algebra algebra) {
		return getMonolith(algebra).blockCount() != algebra.size();
	}

}
