/**
 *	Copyright (C) Miklos Maroti, 2015
 *
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation; either version 2 of the License, or (at your 
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.mmaroti.sat.univalg;

import org.mmaroti.sat.core.*;

public final class Relation<BOOL> {
	private final BoolAlgebra<BOOL> alg;
	private final Tensor<BOOL> tensor;

	public BoolAlgebra<BOOL> getAlg() {
		return alg;
	}

	public Tensor<BOOL> getTensor() {
		return tensor;
	}

	public int getSize() {
		return tensor.getDim(0);
	}

	public int getArity() {
		return tensor.getOrder();
	}

	public Relation(BoolAlgebra<BOOL> alg, Tensor<BOOL> tensor) {
		assert 1 <= tensor.getOrder();

		int size = tensor.getDim(0);
		for (int i = 1; i < tensor.getOrder(); i++)
			assert tensor.getDim(i) == size;

		this.alg = alg;
		this.tensor = tensor;
	}

	public static Relation<Boolean> wrap(Tensor<Boolean> tensor) {
		return new Relation<Boolean>(BoolAlgebra.INSTANCE, tensor);
	}

	public static <BOOL> Relation<BOOL> wrap(BoolAlgebra<BOOL> alg,
			Tensor<BOOL> tensor) {
		return new Relation<BOOL>(alg, tensor);
	}

	private static int[] createShape(int size, int arity) {
		assert size > 1 && arity >= 0;

		int[] shape = new int[arity];
		for (int i = 0; i < arity; i++)
			shape[i] = size;
		return shape;
	}

	public static Relation<Boolean> makeFull(int size, int arity) {
		Tensor<Boolean> tensor = Tensor.constant(createShape(size, arity),
				Boolean.TRUE);
		return wrap(tensor);
	}

	public static Relation<Boolean> makeEmpty(int size, int arity) {
		Tensor<Boolean> tensor = Tensor.constant(createShape(size, arity),
				Boolean.FALSE);
		return wrap(tensor);
	}

	public static Relation<Boolean> makeEqual(int size) {
		Tensor<Boolean> tensor = Tensor.generate(size, size,
				new Func2<Boolean, Integer, Integer>() {
					@Override
					public Boolean call(Integer elem1, Integer elem2) {
						return elem1.intValue() == elem2.intValue();
					}
				});
		return wrap(tensor);
	}

	public static Relation<Boolean> makeNotEqual(int size) {
		Tensor<Boolean> tensor = Tensor.generate(size, size,
				new Func2<Boolean, Integer, Integer>() {
					@Override
					public Boolean call(Integer elem1, Integer elem2) {
						return elem1.intValue() != elem2.intValue();
					}
				});
		return wrap(tensor);
	}

	public static Relation<Boolean> makeLessThan(int size) {
		Tensor<Boolean> tensor = Tensor.generate(size, size,
				new Func2<Boolean, Integer, Integer>() {
					@Override
					public Boolean call(Integer elem1, Integer elem2) {
						return elem1.intValue() < elem2.intValue();
					}
				});
		return wrap(tensor);
	}

	public static Relation<Boolean> makeLessOrEqual(int size) {
		Tensor<Boolean> tensor = Tensor.generate(size, size,
				new Func2<Boolean, Integer, Integer>() {
					@Override
					public Boolean call(Integer elem1, Integer elem2) {
						return elem1.intValue() <= elem2.intValue();
					}
				});
		return wrap(tensor);
	}

	public static Relation<Boolean> makeGreaterThan(int size) {
		Tensor<Boolean> tensor = Tensor.generate(size, size,
				new Func2<Boolean, Integer, Integer>() {
					@Override
					public Boolean call(Integer elem1, Integer elem2) {
						return elem1.intValue() > elem2.intValue();
					}
				});
		return wrap(tensor);
	}

	public static Relation<Boolean> makeGreaterOrEqual(int size) {
		Tensor<Boolean> tensor = Tensor.generate(size, size,
				new Func2<Boolean, Integer, Integer>() {
					@Override
					public Boolean call(Integer elem1, Integer elem2) {
						return elem1.intValue() >= elem2.intValue();
					}
				});
		return wrap(tensor);
	}

	private void checkSize(Relation<BOOL> rel) {
		assert getAlg() == rel.getAlg();
		assert getSize() == rel.getSize();
	}

	protected void checkArity(Relation<BOOL> rel) {
		checkSize(rel);
		assert getArity() == rel.getArity();
	}

	public Relation<BOOL> intersect(Relation<BOOL> rel) {
		checkArity(rel);
		Tensor<BOOL> tmp = Tensor.map2(alg.AND, tensor, rel.tensor);
		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> union(Relation<BOOL> rel) {
		checkArity(rel);
		Tensor<BOOL> tmp = Tensor.map2(alg.OR, tensor, rel.tensor);
		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> symmdiff(Relation<BOOL> rel) {
		checkArity(rel);
		Tensor<BOOL> tmp = Tensor.map2(alg.ADD, tensor, rel.tensor);
		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> complement() {
		Tensor<BOOL> tmp = Tensor.map(alg.NOT, tensor);
		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> subtract(Relation<BOOL> rel) {
		return intersect(rel.complement());
	}

	public Relation<BOOL> revert() {
		int[] map = new int[getArity()];
		for (int i = 0; i < map.length; i++)
			map[i] = map.length - 1 - i;

		Tensor<BOOL> tmp = Tensor.reshape(tensor, tensor.getShape(), map);
		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> rotate() {
		int[] map = new int[getArity()];
		map[0] = map.length - 1;
		for (int i = 1; i < map.length; i++)
			map[i] = 1 - i;

		Tensor<BOOL> tmp = Tensor.reshape(tensor, tensor.getShape(), map);
		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> project(int... coords) {
		assert 0 <= coords.length && coords.length <= getArity();

		boolean[] kept = new boolean[getArity()];
		for (int i = 0; i < coords.length; i++) {
			assert kept[coords[i]] == false;
			kept[coords[i]] = true;
		}

		int[] map = new int[getArity()];

		int pos = 0;
		for (int i = 0; i < kept.length; i++)
			if (!kept[i])
				map[pos++] = i;

		assert pos + coords.length == map.length;
		System.arraycopy(coords, 0, map, pos, coords.length);

		Tensor<BOOL> tmp;
		tmp = Tensor.reshape(tensor, tensor.getShape(), map);
		if (pos != 0)
			tmp = Tensor.fold(alg.ANY, pos, tmp);

		return new Relation<BOOL>(alg, tmp);
	}

	private Tensor<BOOL> combine(Relation<BOOL> rel) {
		checkSize(rel);
		assert getArity() + rel.getArity() >= 3;

		int[] shape = createShape(getSize(), getArity() + rel.getArity() - 1);

		int[] map = new int[getArity()];
		for (int i = 0; i < map.length - 1; i++)
			map[i] = i + 1;
		Tensor<BOOL> tmp1 = Tensor.reshape(tensor, shape, map);

		map = new int[rel.getArity()];
		for (int i = 1; i < map.length; i++)
			map[i] = getArity() + i - 1;
		Tensor<BOOL> tmp2 = Tensor.reshape(rel.tensor, shape, map);

		return Tensor.map2(alg.AND, tmp1, tmp2);
	}

	public Relation<BOOL> compose(Relation<BOOL> rel) {
		Tensor<BOOL> tmp = combine(rel);
		tmp = Tensor.fold(alg.ANY, 1, tmp);
		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> multiply(Relation<BOOL> rel) {
		Tensor<BOOL> tmp = combine(rel);
		tmp = Tensor.fold(alg.SUM, 1, tmp);
		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> diagonal() {
		int[] shape = new int[] { getSize() };
		int[] map = new int[getArity()];

		Tensor<BOOL> tmp = Tensor.reshape(tensor, shape, map);
		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> product(Relation<BOOL> rel) {
		assert getAlg() == rel.getAlg();
		assert getArity() == rel.getArity();

		final int a = getSize();
		int s = a * rel.getSize();
		int[] shape = new int[getArity()];
		for (int i = 0; i < shape.length; i++)
			shape[i] = s;

		final Tensor<BOOL> t1 = tensor;
		final Tensor<BOOL> t2 = rel.getTensor();

		final int[] idx1 = new int[getArity()];
		final int[] idx2 = new int[getArity()];
		Tensor<BOOL> tmp = Tensor.generate(shape, new Func1<BOOL, int[]>() {
			@Override
			public BOOL call(int[] elem) {
				for (int i = 0; i < elem.length; i++) {
					idx1[i] = elem[i] % a;
					idx2[i] = elem[i] / a;
				}

				return alg.and(t1.getElem(idx1), t2.getElem(idx2));
			}
		});

		return new Relation<BOOL>(alg, tmp);
	}

	public Relation<BOOL> power(int exp) {
		assert exp >= 1;

		Relation<BOOL> rel = this;
		for (int i = 1; i < exp; i++)
			rel = product(rel);

		return rel;
	}

	public Relation<BOOL> reflexiveClosure() {
		return union(lift(alg, makeEqual(getSize())));
	}

	public Relation<BOOL> symmetricClosure() {
		assert getArity() == 2;
		return union(rotate());
	}

	public static Relation<Boolean> transitiveClosure(Relation<Boolean> rel) {
		for (;;) {
			Relation<Boolean> r = rel;
			rel = rel.union(rel.compose(rel));
			if (r.isEqualTo(rel))
				return r;
		}
	}

	public BOOL isFull() {
		return Tensor.fold(alg.ALL, getArity(), tensor).get();
	}

	public BOOL isEmpty() {
		return alg.not(isNotEmpty());
	}

	public BOOL isNotEmpty() {
		return Tensor.fold(alg.ANY, getArity(), tensor).get();
	}

	public BOOL isOddCard() {
		return Tensor.fold(alg.SUM, getArity(), tensor).get();
	}

	public BOOL isEqualTo(Relation<BOOL> rel) {
		checkArity(rel);
		Tensor<BOOL> tmp = Tensor.map2(alg.EQU, tensor, rel.tensor);
		tmp = Tensor.fold(alg.ALL, getArity(), tmp);
		return tmp.get();
	}

	public BOOL isSubsetOf(Relation<BOOL> rel) {
		checkArity(rel);

		Tensor<BOOL> tmp = Tensor.map2(alg.LEQ, tensor, rel.tensor);
		tmp = Tensor.fold(alg.ALL, getArity(), tmp);
		return tmp.get();
	}

	public BOOL isFunction() {
		Tensor<BOOL> rel = Tensor.fold(alg.ONE, 1, tensor);
		return Tensor.fold(alg.ALL, rel.getOrder(), rel).get();
	}

	public BOOL isReflexive() {
		return diagonal().isFull();
	}

	public BOOL isSymmetric() {
		return isSubsetOf(rotate());
	}

	public BOOL isTransitive() {
		assert tensor.getOrder() == 2;
		// mask out diagonal to get fewer literals
		Relation<BOOL> rel = intersect(lift(alg, makeNotEqual(getSize())));
		return rel.compose(rel).isSubsetOf(this);
	}

	public BOOL isAntiSymmetric() {
		assert tensor.getOrder() == 2;
		Relation<BOOL> rel = intersect(lift(alg, makeNotEqual(getSize())));
		rel = rel.intersect(rel.rotate());
		return rel.isEmpty();
	}

	public BOOL isTrichotome() {
		assert tensor.getOrder() == 2;
		Relation<BOOL> rel1, rel2;
		rel1 = lift(alg, makeLessThan(getSize()));
		rel2 = rotate().complement().intersect(rel1);
		rel1 = intersect(rel1);
		return rel1.isEqualTo(rel2);
	}

	public BOOL isEquivalence() {
		BOOL b = isReflexive();
		b = alg.and(b, isSymmetric());
		return alg.and(b, isTransitive());
	}

	public BOOL isPartialOrder() {
		BOOL b = isReflexive();
		b = alg.and(b, isAntiSymmetric());
		return alg.and(b, isTransitive());
	}

	public BOOL isQuasiOrder() {
		BOOL b = isReflexive();
		return alg.and(b, isTransitive());
	}

	public BOOL isTotalOrder() {
		BOOL b = isReflexive();
		b = alg.and(b, isTrichotome());
		return alg.and(b, isTransitive());
	}

	public PartialOrder<BOOL> asPartialOrder() {
		return new PartialOrder<BOOL>(alg, tensor);
	}

	public Operation<BOOL> asOperation() {
		return new Operation<BOOL>(alg, tensor);
	}

	public BOOL isEssential() {
		if (getArity() <= 1)
			return alg.or(isEmpty(), isFull());

		int[] v = new int[getArity()];
		for (int i = 0; i < v.length; i++)
			v[i] = i;

		Contract<BOOL> c = Contract.logical(alg);
		for (int i = 0; i < getArity(); i++) {
			v[i] = i + getArity();
			c.add(tensor, v);
			v[i] = i;
		}
		c.add(Tensor.map(alg.NOT, tensor), v);
		return c.get(new int[0]).get();
	}

	public static <BOOL> Relation<BOOL> lift(BoolAlgebra<BOOL> alg,
			Relation<Boolean> rel) {
		Tensor<BOOL> tensor = Tensor.map(alg.LIFT, rel.tensor);
		return new Relation<BOOL>(alg, tensor);
	}

	private static char formatIndex(int elem) {
		if (0 <= elem && elem < 10)
			return (char) ('0' + elem);
		else if (10 <= elem && elem < 36)
			return (char) ('a' + elem - 10);
		else
			throw new IllegalArgumentException();
	}

	private static int parseIndex(int size, char c) {
		int i;
		if ('0' <= c && c <= '9')
			i = c - '0';
		else if ('a' <= c && c <= 'z')
			i = c - 'a' + 10;
		else
			i = size;

		if (i < size)
			return i;
		else
			throw new IllegalArgumentException("invalid coordinate: " + c);
	}

	public static String formatMembers(Relation<Boolean> rel) {
		String s = "";

		Tensor<Boolean> tensor = rel.getTensor();
		int[] index = new int[tensor.getOrder()];
		outer: for (;;) {
			if (tensor.getElem(index)) {
				if (s.length() != 0)
					s += ' ';
				for (int i = 0; i < index.length; i++)
					s += formatIndex(index[i]);
			}

			for (int i = index.length - 1; i >= 0; i--) {
				if (++index[i] >= tensor.getDim(i))
					index[i] = 0;
				else
					continue outer;
			}
			break;
		}

		return s;
	}

	public static Relation<Boolean> parseMembers(int size, int arity, String str) {
		Tensor<Boolean> tensor;
		tensor = Tensor.constant(createShape(size, arity), false);

		int[] index = new int[arity];
		int p = 0;

		for (int i = 0; i <= str.length(); i++) {
			if (i == str.length() || str.charAt(i) == ' ') {
				if (p == 0)
					continue;
				else if (p != index.length)
					throw new IllegalArgumentException("too few dims: " + p);

				tensor.setElem(true, index);
				p = 0;
			} else {
				if (p > index.length)
					throw new IllegalArgumentException("too many dims: " + p);

				index[p++] = parseIndex(size, str.charAt(i));
			}
		}
		assert p == 0;

		return new Relation<Boolean>(BoolAlgebra.INSTANCE, tensor);
	}
}
