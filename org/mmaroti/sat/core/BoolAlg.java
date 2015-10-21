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

public abstract class BoolAlg<BOOL> {
	public final BOOL FALSE;
	public final BOOL TRUE;

	public BOOL lift(boolean elem) {
		return elem ? TRUE : FALSE;
	}

	public abstract BOOL not(BOOL elem);

	public BOOL or(BOOL elem1, BOOL elem2) {
		return not(and(not(elem1), not(elem2)));
	}

	public BOOL and(BOOL elem1, BOOL elem2) {
		return not(or(not(elem1), not(elem2)));
	}

	public BOOL leq(BOOL elem1, BOOL elem2) {
		return or(not(elem1), elem2);
	}

	public BOOL add(BOOL elem1, BOOL elem2) {
		return not(equ(elem1, elem2));
	}

	public BOOL equ(BOOL elem1, BOOL elem2) {
		return not(add(elem1, elem2));
	}

	public BOOL all(Iterable<BOOL> elems) {
		BOOL ret = TRUE;

		for (BOOL elem : elems)
			ret = and(ret, elem);

		return ret;
	}

	public BOOL any(Iterable<BOOL> elems) {
		BOOL ret = FALSE;

		for (BOOL elem : elems)
			ret = or(ret, elem);

		return ret;
	}

	public BOOL sum(Iterable<BOOL> elems) {
		BOOL ret = FALSE;

		for (BOOL elem : elems)
			ret = add(ret, elem);

		return ret;
	}

	public BOOL one(Iterable<BOOL> elems) {
		BOOL any = FALSE;
		BOOL err = FALSE;

		for (BOOL elem : elems) {
			err = or(err, and(any, elem));
			any = or(any, elem);
		}

		return and(any, not(err));
	}

	public BOOL lexless(Iterable<BOOL> elem1, Iterable<BOOL> elem2) {
		BOOL less = FALSE;
		BOOL equal = TRUE;

		Iterator<BOOL> iter1 = elem1.iterator();
		Iterator<BOOL> iter2 = elem2.iterator();
		while (iter1.hasNext()) {
			assert iter2.hasNext();

			BOOL a = iter1.next();
			BOOL b = iter2.next();

			less = or(less, and(equal, and(not(a), b)));
			equal = and(equal, equ(a, b));
		}
		assert !iter2.hasNext();

		return less;
	}

	public final Func1<BOOL, BOOL> ID;
	public final Func1<BOOL, BOOL> NOT;
	public final Func2<BOOL, BOOL, BOOL> OR;
	public final Func2<BOOL, BOOL, BOOL> AND;
	public final Func2<BOOL, BOOL, BOOL> LEQ;
	public final Func2<BOOL, BOOL, BOOL> ADD;
	public final Func2<BOOL, BOOL, BOOL> EQU;

	public final Func1<BOOL, Boolean> LIFT;
	public final Func1<BOOL, Iterable<BOOL>> ALL;
	public final Func1<BOOL, Iterable<BOOL>> ANY;
	public final Func1<BOOL, Iterable<BOOL>> SUM;
	public final Func1<BOOL, Iterable<BOOL>> ONE;
	public final Func1<BOOL, Iterable<BOOL>> EQS;

	public BoolAlg(final BOOL FALSE, final BOOL TRUE) {
		this.FALSE = FALSE;
		this.TRUE = TRUE;

		assert TRUE != null && FALSE != null && TRUE != FALSE;

		ID = new Func1<BOOL, BOOL>() {
			@Override
			public BOOL call(BOOL elem) {
				return elem;
			}
		};

		NOT = new Func1<BOOL, BOOL>() {
			@Override
			public BOOL call(BOOL elem) {
				assert elem != null;
				return not(elem);
			}
		};

		OR = new Func2<BOOL, BOOL, BOOL>() {
			@Override
			public BOOL call(BOOL elem1, BOOL elem2) {
				assert elem1 != null && elem2 != null;
				return or(elem1, elem2);
			}
		};

		AND = new Func2<BOOL, BOOL, BOOL>() {
			@Override
			public BOOL call(BOOL elem1, BOOL elem2) {
				assert elem1 != null && elem2 != null;
				return and(elem1, elem2);
			}
		};

		LEQ = new Func2<BOOL, BOOL, BOOL>() {
			@Override
			public BOOL call(BOOL elem1, BOOL elem2) {
				assert elem1 != null && elem2 != null;
				return leq(elem1, elem2);
			}
		};

		ADD = new Func2<BOOL, BOOL, BOOL>() {
			@Override
			public BOOL call(BOOL elem1, BOOL elem2) {
				assert elem1 != null && elem2 != null;
				return add(elem1, elem2);
			}
		};

		EQU = new Func2<BOOL, BOOL, BOOL>() {
			@Override
			public BOOL call(BOOL elem1, BOOL elem2) {
				assert elem1 != null && elem2 != null;
				return equ(elem1, elem2);
			}
		};

		LIFT = new Func1<BOOL, Boolean>() {
			@Override
			public BOOL call(Boolean elem) {
				return lift(elem);
			}
		};

		ALL = new Func1<BOOL, Iterable<BOOL>>() {
			@Override
			public BOOL call(Iterable<BOOL> elems) {
				return all(elems);
			}
		};

		ANY = new Func1<BOOL, Iterable<BOOL>>() {
			@Override
			public BOOL call(Iterable<BOOL> elems) {
				return any(elems);
			}
		};

		SUM = new Func1<BOOL, Iterable<BOOL>>() {
			@Override
			public BOOL call(Iterable<BOOL> elems) {
				return sum(elems);
			}
		};

		ONE = new Func1<BOOL, Iterable<BOOL>>() {
			@Override
			public BOOL call(Iterable<BOOL> elems) {
				return one(elems);
			}
		};

		EQS = new Func1<BOOL, Iterable<BOOL>>() {
			@Override
			public BOOL call(Iterable<BOOL> elems) {
				Iterator<BOOL> iter = elems.iterator();

				assert iter.hasNext();
				BOOL fst = iter.next();

				BOOL res = TRUE;
				while (iter.hasNext())
					res = and(res, equ(fst, iter.next()));

				return res;
			}
		};
	}

	public static BoolAlg<Boolean> BOOLEAN = new BoolAlg<Boolean>(
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
