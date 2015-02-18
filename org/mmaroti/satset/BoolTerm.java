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

package org.mmaroti.satset;

import java.util.*;

public abstract class BoolTerm {
	private final int hashcode;

	public BoolTerm(int hashcode) {
		this.hashcode = hashcode;
	}

	@Override
	public int hashCode() {
		return hashcode;
	}

	public static BoolTerm newVariable() {
		return new BoolVar();
	}

	private static class BoolVar extends BoolTerm {
		private static int counter = 1234;

		public BoolVar() {
			super(counter++);
		}

		@Override
		public String toString() {
			return "v" + hashCode();
		}

		@Override
		public boolean equals(Object other) {
			return this == other;
		}

		@Override
		public BoolTerm not() {
			return new BoolNot(this);
		}

		@Override
		protected int generate(Instance instance) {
			int lit = instance.getLiteral(this);
			if (lit != 0)
				return lit;

			return instance.addVariable(this);
		}

		@Override
		protected boolean decode(Instance instance, boolean[] solution) {
			int lit = instance.getLiteral(this);
			if (lit != 0)
				return solution[lit];
			else {
				// throw new IllegalArgumentException("unsolved variable");
				return false;
			}
		}
	}

	public final static BoolTerm FALSE = new BoolTerm(1233) {
		@Override
		public boolean equals(Object other) {
			return this == other;
		}

		@Override
		public BoolTerm not() {
			return TRUE;
		}

		@Override
		protected int generate(Instance instance) {
			int lit = instance.getLiteral(this);
			if (lit != 0)
				return lit;

			lit = instance.addVariable(this);
			instance.addClause(new int[] { -lit });

			return lit;
		}

		@Override
		protected boolean decode(Instance instance, boolean[] solution) {
			return false;
		}
	};

	public final static BoolTerm TRUE = new BoolNot(FALSE);

	public static BoolTerm lift(boolean bool) {
		return bool ? TRUE : FALSE;
	}

	public abstract BoolTerm not();

	private static class BoolNot extends BoolTerm {
		public final BoolTerm term;

		public BoolNot(BoolTerm term) {
			super(-term.hashcode);

			this.term = term;
		}

		@Override
		public String toString() {
			return "-" + term.toString();
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof BoolNot) {
				BoolNot o = (BoolNot) other;
				return term.equals(o.term);
			}

			return false;
		}

		@Override
		public BoolTerm not() {
			return term;
		}

		@Override
		protected int generate(Instance instance) {
			return -term.generate(instance);
		}

		@Override
		protected boolean decode(Instance instance, boolean[] solution) {
			return !term.decode(instance, solution);
		}
	}

	public BoolTerm and(BoolTerm other) {
		if (this == TRUE)
			return other;
		else if (this == FALSE)
			return FALSE;
		else if (other == TRUE)
			return this;
		else if (other == FALSE)
			return FALSE;
		else
			return new BoolAnd(this, other);
	}

	private static class BoolAnd extends BoolTerm {
		public final BoolTerm first;
		public final BoolTerm second;

		public BoolAnd(BoolTerm first, BoolTerm second) {
			super(first.hashcode * 73019 + second.hashcode);

			this.first = first;
			this.second = second;
		}

		@Override
		public String toString() {
			return "(" + first.toString() + "&" + second.toString() + ")";
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof BoolAnd) {
				BoolAnd o = (BoolAnd) other;
				return first.equals(o.first) && second.equals(o.second);
			}

			return false;
		}

		@Override
		public BoolTerm not() {
			return new BoolNot(this);
		}

		@Override
		protected int generate(Instance instance) {
			int lit = instance.getLiteral(this);
			if (lit != 0)
				return lit;

			int a = first.generate(instance);
			int b = second.generate(instance);
			lit = instance.addVariable(this);

			instance.addClause(new int[] { -a, -b, lit });
			instance.addClause(new int[] { -lit, a });
			instance.addClause(new int[] { -lit, b });

			return lit;
		}

		@Override
		protected boolean decode(Instance instance, boolean[] solution) {
			int lit = instance.getLiteral(this);
			if (lit != 0)
				return solution[lit];

			boolean a = first.decode(instance, solution);
			boolean b = second.decode(instance, solution);
			return a && b;
		}
	}

	public BoolTerm or(BoolTerm other) {
		return not().and(other.not()).not();
	}

	public BoolTerm leq(BoolTerm other) {
		return and(other.not()).not();
	}

	public BoolTerm xor(BoolTerm other) {
		if (this == TRUE)
			return other.not();
		else if (this == FALSE)
			return other;
		else if (other == TRUE)
			return not();
		else if (other == FALSE)
			return this;
		else if (this instanceof BoolNot) {
			BoolNot t = (BoolNot) this;
			if (other instanceof BoolNot) {
				BoolNot o = (BoolNot) other;
				return new BoolXor(t.term, o.term);
			} else
				return new BoolXor(t.term, other).not();
		} else if (other instanceof BoolNot) {
			BoolNot o = (BoolNot) other;
			return new BoolXor(this, o.term).not();
		} else
			return new BoolXor(this, other);
	}

	private static class BoolXor extends BoolTerm {
		public final BoolTerm first;
		public final BoolTerm second;

		public BoolXor(BoolTerm first, BoolTerm second) {
			super(first.hashcode * 72077 + second.hashcode);

			this.first = first;
			this.second = second;
		}

		@Override
		public String toString() {
			return "(" + first.toString() + "+" + second.toString() + ")";
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof BoolXor) {
				BoolXor o = (BoolXor) other;
				return first.equals(o.first) && second.equals(o.second);
			}

			return false;
		}

		@Override
		public BoolTerm not() {
			return new BoolNot(this);
		}

		@Override
		protected int generate(Instance instance) {
			int lit = instance.getLiteral(this);
			if (lit != 0)
				return lit;

			int a = first.generate(instance);
			int b = second.generate(instance);
			lit = instance.addVariable(this);

			instance.addClause(new int[] { -a, -b, -lit });
			instance.addClause(new int[] { -a, b, lit });
			instance.addClause(new int[] { a, -b, lit });
			instance.addClause(new int[] { a, b, -lit });

			return lit;
		}

		@Override
		protected boolean decode(Instance instance, boolean[] solution) {
			int lit = instance.getLiteral(this);
			if (lit != 0)
				return solution[lit];

			boolean a = first.decode(instance, solution);
			boolean b = second.decode(instance, solution);
			return a ^ b;
		}
	}

	public BoolTerm equ(BoolTerm other) {
		return xor(other).not();
	}

	protected abstract int generate(Instance instance);

	protected abstract boolean decode(Instance instance, boolean[] solution);

	static class Instance {
		public int variables = 0;
		public final List<int[]> clauses = new ArrayList<int[]>();
		private final Map<BoolTerm, Integer> literals = new HashMap<BoolTerm, Integer>();

		int addVariable(BoolTerm term) {
			assert term != null && !(term instanceof BoolNot);
			assert !literals.containsKey(term);

			int literal = ++variables;
			literals.put(term, literal);

			return literal;
		}

		void addClause(int[] clause) {
			assert clauses != null;
			for (int c : clause)
				assert c != 0 && Math.abs(c) <= variables;

			clauses.add(clause);
		}

		int getLiteral(BoolTerm term) {
			assert term != null && !(term instanceof BoolNot);

			Integer literal = literals.get(term);
			return literal != null ? literal : 0;
		}
	}

	public Instance instance() {
		Instance instance = new Instance();
		int lit = generate(instance);
		instance.addClause(new int[] { lit });
		return instance;
	}
}
