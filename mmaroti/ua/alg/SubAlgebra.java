package mmaroti.ua.alg;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.*;

public class SubAlgebra implements Algebra {
	protected Algebra algebra;
	protected Map<Integer, Integer> abstractMap;
	protected List<Integer> concreteMap;

	protected Op[] operations;

	@Override
	public Function[] operations() {
		return operations;
	}

	protected class Op implements Function {
		protected Function op;

		@Override
		public int size() {
			return concreteMap.size();
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

	protected Rel[] relations;

	@Override
	public Function[] relations() {
		return relations;
	}

	class Rel implements Function {
		protected Function rel;

		@Override
		public int size() {
			return concreteMap.size();
		}

		@Override
		public int arity() {
			return rel.arity();
		}

		protected int[] args;

		@Override
		public int value(int[] args) {
			for (int i = 0; i < args.length; ++i)
				this.args[i] = toConcreteElem(args[i]);

			return rel.value(this.args);
		}

		public Rel(Function relation) {
			this.rel = relation;
			this.args = new int[rel.arity()];
		}
	}

	@Override
	public int size() {
		return concreteMap.size();
	}

	public final int toAbstractElem(int a) {
		Integer b = new Integer(a);
		Integer n = (Integer) abstractMap.get(b);

		if (n == null) {
			n = new Integer(concreteMap.size());
			abstractMap.put(b, n);
			concreteMap.add(b);
		}

		return n.intValue();
	}

	public final int toConcreteElem(int a) {
		return ((Integer) concreteMap.get(a)).intValue();
	}

	public List<Integer> concreteElems() {
		int size = size();
		List<Integer> list = new ArrayList<Integer>(size);

		for (int i = 0; i < size; ++i)
			list.add(toConcreteElem(i));

		return list;
	}

	public void generate() {
		int radius = -2;
		while (++radius < size()) {
			for (int i = 0; i < operations.length; ++i) {
				Function op = operations[i];
				SphereArgument arg = new SphereArgument(op.arity(), radius);
				int[] args = arg.args();

				if (arg.first())
					do {
						op.value(args);
					} while (arg.next());
			}
		}
	}

	public void addGenerator(int gen) {
		toAbstractElem(gen);
	}

	public void addGenerators(int[] gens) {
		for (int i = 0; i < gens.length; ++i)
			toAbstractElem(gens[i]);
	}

	public boolean addHomomorphismGenerator(int concreteElem, int targetElem,
			List<Integer> map) {
		int a = toAbstractElem(concreteElem);

		if (a == map.size()) {
			map.add(targetElem);
			return true;
		}

		return map.get(a) == targetElem;
	}

	public boolean generateHomomorphism(List<Integer> map, Algebra target) {
		int radius = -2;
		while (++radius < size()) {
			for (int i = 0; i < operations.length; ++i) {
				Function op = operations[i];
				Function targetOp = target.operations()[i];

				SphereArgument arg = new SphereArgument(op.arity(), radius);
				int[] args = arg.args();
				int[] targetArgs = new int[args.length];

				if (arg.first())
					do {
						int a = op.value(args);

						Integer n;
						for (int j = 0; j < args.length; ++j) {
							n = (Integer) map.get(args[j]);
							targetArgs[j] = n.intValue();
						}

						int b = targetOp.value(targetArgs);

						if (a == map.size())
							map.add(new Integer(b));
						else {
							n = (Integer) map.get(a);
							if (n.intValue() != b)
								return false;
						}

					} while (arg.next());
			}
		}

		return true;
	}

	public void clear() {
		abstractMap.clear();
		concreteMap.clear();
	}

	public SubAlgebra(Algebra algebra) {
		this.algebra = algebra;
		abstractMap = new HashMap<Integer, Integer>();
		concreteMap = new ArrayList<Integer>();

		Function[] funcs = algebra.operations();
		operations = new Op[funcs.length];
		for (int i = 0; i < funcs.length; ++i)
			operations[i] = new Op(funcs[i]);

		funcs = algebra.relations();
		relations = new Rel[funcs.length];
		for (int i = 0; i < funcs.length; ++i)
			relations[i] = new Rel(funcs[i]);
	}
}
