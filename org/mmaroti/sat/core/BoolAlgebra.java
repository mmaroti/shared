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

package org.mmaroti.sat.core;

import java.util.*;

public abstract class BoolAlgebra<ELEM> {
	public final ELEM FALSE;
	public final ELEM TRUE;

	public ELEM lift(boolean elem) {
		return elem ? TRUE : FALSE;
	}

	public abstract ELEM not(ELEM elem);

	public ELEM or(ELEM elem1, ELEM elem2) {
		return not(and(not(elem1), not(elem2)));
	}

	public ELEM and(ELEM elem1, ELEM elem2) {
		return not(or(not(elem1), not(elem2)));
	}

	public ELEM leq(ELEM elem1, ELEM elem2) {
		return or(not(elem1), elem2);
	}

	public ELEM add(ELEM elem1, ELEM elem2) {
		return not(equ(elem1, elem2));
	}

	public ELEM equ(ELEM elem1, ELEM elem2) {
		return not(add(elem1, elem2));
	}

	public ELEM all(Iterable<ELEM> elems) {
		ELEM ret = TRUE;

		for (ELEM elem : elems)
			ret = and(ret, elem);

		return ret;
	}

	public ELEM any(Iterable<ELEM> elems) {
		ELEM ret = FALSE;

		for (ELEM elem : elems)
			ret = or(ret, elem);

		return ret;
	}

	public ELEM sum(Iterable<ELEM> elems) {
		ELEM ret = FALSE;

		for (ELEM elem : elems)
			ret = add(ret, elem);

		return ret;
	}

	public ELEM one(Iterable<ELEM> elems) {
		ELEM any = FALSE;
		ELEM err = FALSE;

		for (ELEM elem : elems) {
			err = or(err, and(any, elem));
			any = or(any, elem);
		}

		return and(any, not(err));
	}

	public ELEM lexless(Iterable<ELEM> elem1, Iterable<ELEM> elem2) {
		ELEM less = FALSE;
		ELEM equal = TRUE;

		Iterator<ELEM> iter1 = elem1.iterator();
		Iterator<ELEM> iter2 = elem2.iterator();
		while (iter1.hasNext()) {
			assert iter2.hasNext();

			ELEM a = iter1.next();
			ELEM b = iter2.next();

			less = or(less, and(equal, and(not(a), b)));
			equal = and(equal, equ(a, b));
		}
		assert !iter2.hasNext();

		return less;
	}

	public final Func1<ELEM, ELEM> ID;
	public final Func1<ELEM, ELEM> NOT;
	public final Func2<ELEM, ELEM, ELEM> OR;
	public final Func2<ELEM, ELEM, ELEM> AND;
	public final Func2<ELEM, ELEM, ELEM> LEQ;
	public final Func2<ELEM, ELEM, ELEM> ADD;
	public final Func2<ELEM, ELEM, ELEM> EQU;

	public final Func1<ELEM, Boolean> LIFT;
	public final Func1<ELEM, Iterable<ELEM>> ALL;
	public final Func1<ELEM, Iterable<ELEM>> ANY;
	public final Func1<ELEM, Iterable<ELEM>> SUM;
	public final Func1<ELEM, Iterable<ELEM>> ONE;
	public final Func1<ELEM, Iterable<ELEM>> EQS;

	public BoolAlgebra(final ELEM FALSE, final ELEM TRUE) {
		this.FALSE = FALSE;
		this.TRUE = TRUE;

		assert TRUE != null && FALSE != null && TRUE != FALSE;

		ID = new Func1<ELEM, ELEM>() {
			@Override
			public ELEM call(ELEM elem) {
				return elem;
			}
		};

		NOT = new Func1<ELEM, ELEM>() {
			@Override
			public ELEM call(ELEM elem) {
				assert elem != null;
				return not(elem);
			}
		};

		OR = new Func2<ELEM, ELEM, ELEM>() {
			@Override
			public ELEM call(ELEM elem1, ELEM elem2) {
				assert elem1 != null && elem2 != null;
				return or(elem1, elem2);
			}
		};

		AND = new Func2<ELEM, ELEM, ELEM>() {
			@Override
			public ELEM call(ELEM elem1, ELEM elem2) {
				assert elem1 != null && elem2 != null;
				return and(elem1, elem2);
			}
		};

		LEQ = new Func2<ELEM, ELEM, ELEM>() {
			@Override
			public ELEM call(ELEM elem1, ELEM elem2) {
				assert elem1 != null && elem2 != null;
				return leq(elem1, elem2);
			}
		};

		ADD = new Func2<ELEM, ELEM, ELEM>() {
			@Override
			public ELEM call(ELEM elem1, ELEM elem2) {
				assert elem1 != null && elem2 != null;
				return add(elem1, elem2);
			}
		};

		EQU = new Func2<ELEM, ELEM, ELEM>() {
			@Override
			public ELEM call(ELEM elem1, ELEM elem2) {
				assert elem1 != null && elem2 != null;
				return equ(elem1, elem2);
			}
		};

		LIFT = new Func1<ELEM, Boolean>() {
			@Override
			public ELEM call(Boolean elem) {
				return lift(elem);
			}
		};

		ALL = new Func1<ELEM, Iterable<ELEM>>() {
			@Override
			public ELEM call(Iterable<ELEM> elems) {
				return all(elems);
			}
		};

		ANY = new Func1<ELEM, Iterable<ELEM>>() {
			@Override
			public ELEM call(Iterable<ELEM> elems) {
				return any(elems);
			}
		};

		SUM = new Func1<ELEM, Iterable<ELEM>>() {
			@Override
			public ELEM call(Iterable<ELEM> elems) {
				return sum(elems);
			}
		};

		ONE = new Func1<ELEM, Iterable<ELEM>>() {
			@Override
			public ELEM call(Iterable<ELEM> elems) {
				return one(elems);
			}
		};

		EQS = new Func1<ELEM, Iterable<ELEM>>() {
			@Override
			public ELEM call(Iterable<ELEM> elems) {
				Iterator<ELEM> iter = elems.iterator();

				assert iter.hasNext();
				ELEM fst = iter.next();

				ELEM res = TRUE;
				while (iter.hasNext())
					res = and(res, equ(fst, iter.next()));

				return res;
			}
		};
	}

	public static BoolAlgebra<Boolean> INSTANCE = new BoolAlgebra<Boolean>(
			Boolean.FALSE, Boolean.TRUE) {
		@Override
		public Boolean not(Boolean elem) {
			return !elem.booleanValue();
		}

		@Override
		public Boolean or(Boolean elem1, Boolean elem2) {
			return elem1.booleanValue() || elem2.booleanValue();
		}

		@Override
		public Boolean and(Boolean elem1, Boolean elem2) {
			return elem1.booleanValue() && elem2.booleanValue();
		}

		@Override
		public Boolean add(Boolean elem1, Boolean elem2) {
			return elem1.booleanValue() != elem2.booleanValue();
		}

		@Override
		public Boolean equ(Boolean elem1, Boolean elem2) {
			return elem1.booleanValue() == elem2.booleanValue();
		}

		@Override
		public Boolean leq(Boolean elem1, Boolean elem2) {
			return !elem1.booleanValue() || elem2.booleanValue();
		}

		@Override
		public Boolean all(Iterable<Boolean> elems) {
			for (boolean elem : elems)
				if (!elem)
					return false;

			return true;
		}

		@Override
		public Boolean any(Iterable<Boolean> elems) {
			for (boolean elem : elems)
				if (elem)
					return true;

			return false;
		}
	};
}
