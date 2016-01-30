package mmaroti.ua.alg;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import java.util.*;

public class FreeAlgebra implements Algebra {
	public static boolean extendToHomomorphism(Algebra domain,
			Algebra coDomain, HashMap<Integer, Integer> map) {
		ArrayList<Integer> elements = new ArrayList<Integer>(map.keySet());

		int radius = -2;
		while (++radius < elements.size()) {
			int opIndex = domain.operations().length;
			while (--opIndex >= 0) {
				Function op = domain.operations()[opIndex];
				Function coOp = coDomain.operations()[opIndex];

				SphereArgument arg = new SphereArgument(op.arity(), radius);
				int[] as = arg.args();

				int[] args = new int[as.length];
				int[] coArgs = new int[as.length];

				if (arg.first())
					do {
						int i = as.length;
						while (--i >= 0) {
							Integer n = elements.get(as[i]);
							args[i] = n.intValue();
							n = map.get(n);
							coArgs[i] = n.intValue();

						}

						Integer a = new Integer(op.value(args));
						Integer b = map.get(a);
						int c = coOp.value(coArgs);

						if (b == null) {
							elements.add(a);
							map.put(a, new Integer(c));
						} else if (b.intValue() != c)
							return false;

					} while (arg.next());
			}
		}

		return true;
	}

	public static boolean isRedundantFactor(List<Algebra> algebras,
			List<List<Integer>> generators, int target, int testSize) {
		if (testSize < 1 || target < 0 || target >= algebras.size())
			throw new IllegalArgumentException();

		SubsetArgument arg = new SubsetArgument(testSize, algebras.size());
		int[] args = arg.args();
		arg.selected()[target] = true; // skip the target algebra

		if (arg.first())
			outer: do {
				Algebra[] algs = new Algebra[testSize];
				for (int i = 0; i < testSize; ++i)
					algs[i] = (Algebra) algebras.get(args[i]);

				ProductAlgebra prod = new ProductAlgebra(algs);

				HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
				int[] func = new int[testSize];

				Iterator<List<Integer>> iter = generators.iterator();
				while (iter.hasNext()) {
					List<Integer> function = iter.next();
					for (int i = 0; i < testSize; ++i) {
						Integer n = function.get(args[i]);
						func[i] = n.intValue();
					}

					Integer a = new Integer(prod.toAbstractElem(func));
					Integer b = map.get(a);
					Integer c = function.get(target);

					if (b != null && !b.equals(c))
						continue outer;

					map.put(a, c);
				}

				if (extendToHomomorphism(prod, algebras.get(target), map))
					return true;

			} while (arg.next());

		return false;
	}

	public static void removeFactor(List<Algebra> algebras,
			List<List<Integer>> generators, int target) {
		algebras.remove(target);

		Iterator<List<Integer>> iter = generators.iterator();
		while (iter.hasNext()) {
			List<Integer> function = iter.next();
			function.remove(target);
		}
	}

	public static void removeRedundantFactors(List<Algebra> algebras,
			List<List<Integer>> generators) {
		for (int testSize = 1; testSize <= 5; ++testSize) {
			int size = algebras.size();
			int target = size;
			while (--target >= 0)
				if (isRedundantFactor(algebras, generators, target, testSize))
					removeFactor(algebras, generators, target);

			if (algebras.size() == size && testSize >= 3)
				break;
		}
	}

	protected SubProdAlgebra algebra;
	protected int[] generators;

	@Override
	public int size() {
		return algebra.size();
	}

	@Override
	public Function[] operations() {
		return algebra.operations();
	}

	@Override
	public Function[] relations() {
		return algebra.relations();
	}

	public int[] generators() {
		return generators;
	}

	protected void Init(List<Algebra> algs, List<List<Integer>> gens) {
		removeRedundantFactors(algs, gens);
		algebra = new SubProdAlgebra(algs, gens);

		generators = new int[gens.size()];
		Iterator<List<Integer>> iter = gens.iterator();
		for (int i = 0; i < generators.length; ++i)
			generators[i] = algebra.toAbstractElem(iter.next());
	}

	protected void Init(List<Algebra> algs, int size) {
		List<Algebra> algebras = new ArrayList<Algebra>();
		List<List<Integer>> generators = new ArrayList<List<Integer>>();
		for (int i = 0; i < size; ++i)
			generators.add(new ArrayList<Integer>());

		Iterator<Algebra> iter = algs.iterator();
		while (iter.hasNext()) {
			Algebra algebra = iter.next();

			SquareArgument arg = new SquareArgument(size, algebra.size());
			int[] args = arg.args();

			if (arg.first())
				do {
					SubAlgebra alg = new SubAlgebra(algebra);

					for (int i = 0; i < size; ++i) {
						// add the generator
						int b = alg.toAbstractElem(args[i]);

						List<Integer> function = generators.get(i);
						function.add(b);
					}

					alg.generate();
					algebras.add(new AlgebraBuffer(alg));

					int target = algebras.size() - 1;
					if (isRedundantFactor(algebras, generators, target, 1)
							|| isRedundantFactor(algebras, generators, target,
									2)) {
						removeFactor(algebras, generators, target);
					}
				} while (arg.next());
		}

		Init(algebras, generators);
	}

	public FreeAlgebra(List<Algebra> algebras, int size) {
		if (algebras.isEmpty() || size < 0)
			throw new IllegalArgumentException();

		Init(algebras, size);
	}

	public FreeAlgebra(Algebra algebra, int size) {
		if (size < 0)
			throw new IllegalArgumentException();

		List<Algebra> algebras = new LinkedList<Algebra>();
		algebras.add(algebra);

		Init(algebras, size);
	}

	public FreeAlgebra(List<Algebra> algebras, List<List<Integer>> generators) {
		Init(algebras, generators);
	}
}
