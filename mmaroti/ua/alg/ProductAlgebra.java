package mmaroti.ua.alg;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.*;
import mmaroti.ua.util.*;

public class ProductAlgebra implements Algebra {
	protected Algebra[] algebras;
	protected int[] sizes;
	protected int size;

	@Override
	public int size() {
		return size;
	}

	public Algebra[] algebras() {
		return algebras;
	}

	protected Op[] operations;

	@Override
	public Function[] operations() {
		return operations;
	}

	protected class Op implements Function {
		protected int arity;
		protected Function[] ops;
		protected int[][] funcs;
		protected int[] args2;

		@Override
		public int size() {
			return size;
		}

		@Override
		public int arity() {
			return arity;
		}

		@Override
		public int value(int[] args) {
			if (args.length != arity)
				throw new IllegalArgumentException();

			int i, j;
			for (i = 0; i < arity; ++i)
				toConcreteElem(args[i], funcs[i]);

			for (i = 0; i < ops.length; ++i) {
				for (j = 0; j < arity; ++j)
					args2[j] = funcs[j][i];

				funcs[0][i] = ops[i].value(args2);
			}

			return toAbstractElem(funcs[0]);
		}

		public Op(int opIndex) {
			ops = new Function[algebras.length];
			for (int i = 0; i < ops.length; ++i)
				ops[i] = algebras[i].operations()[opIndex];

			arity = ops[0].arity();

			funcs = new int[arity][ops.length];
			args2 = new int[arity];
		}
	}

	protected Rel[] relations;

	@Override
	public Function[] relations() {
		return relations;
	}

	protected class Rel implements Function {
		protected int arity;
		protected Function[] rels;
		protected int[][] funcs;
		protected int[] args2;

		@Override
		public int size() {
			return size;
		}

		@Override
		public int arity() {
			return arity;
		}

		@Override
		public int value(int[] args) {
			if (args.length != arity)
				throw new IllegalArgumentException();

			int i, j;
			for (i = 0; i < arity; ++i)
				toConcreteElem(args[i], funcs[i]);

			for (i = 0; i < rels.length; ++i) {
				for (j = 0; j < arity; ++j)
					args2[j] = funcs[j][i];

				if (rels[i].value(args2) == 0)
					return 0;
			}

			return 1;
		}

		public Rel(int relIndex) {
			rels = new Function[algebras.length];
			for (int i = 0; i < rels.length; ++i)
				rels[i] = algebras[i].relations()[relIndex];

			arity = rels[0].arity();

			funcs = new int[arity][rels.length];
			args2 = new int[arity];
		}
	}

	public final int toAbstractElem(int[] func) {
		if (func.length != sizes.length)
			throw new IllegalArgumentException();

		int a = 0;

		for (int i = 0; i < sizes.length; ++i) {
			a *= sizes[i];
			a += func[i];
		}

		return a;
	}

	public final void toConcreteElem(int a, int[] func) {
		if (a < 0 || a >= size || func.length != sizes.length)
			throw new IllegalArgumentException();

		int i = sizes.length;
		while (--i >= 0) {
			func[i] = a % sizes[i];
			a /= sizes[i];
		}
	}

	public List<IntArray> concreteElems() {
		List<IntArray> list = new ArrayList<IntArray>(size);
		int[] func = new int[sizes.length];

		for (int i = 0; i < size; ++i) {
			toConcreteElem(i, func);
			list.add(new IntArrayBuffer(func.clone()));
		}

		return list;
	}

	public ProductAlgebra(Algebra[] algebras) {
		if (algebras.length == 0)
			throw new IllegalArgumentException();

		this.algebras = algebras;

		size = 1;

		sizes = new int[algebras.length];
		for (int i = 0; i < algebras.length; ++i) {
			sizes[i] = algebras[i].size();
			size *= sizes[i];
		}

		operations = new Op[algebras[0].operations().length];
		for (int i = 0; i < operations.length; ++i)
			operations[i] = new Op(i);

		relations = new Rel[algebras[0].relations().length];
		for (int i = 0; i < relations.length; ++i)
			relations[i] = new Rel(i);
	}
}
