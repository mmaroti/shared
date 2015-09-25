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

package org.mmaroti.ua.sat;

public abstract class Calculator<BOOL> {
	public final BOOL FALSE;
	public final BOOL TRUE;

	public abstract BOOL not(BOOL b);

	public BOOL or(BOOL a, BOOL b) {
		return not(and(not(a), not(b)));
	}

	public BOOL and(BOOL a, BOOL b) {
		return not(or(not(a), not(b)));
	}

	public BOOL leq(BOOL a, BOOL b) {
		return or(not(a), b);
	}

	public BOOL add(BOOL a, BOOL b) {
		return not(eq(a, b));
	}

	public BOOL eq(BOOL a, BOOL b) {
		return not(add(a, b));
	}

	public final Func1<BOOL, BOOL> NOT;
	public final Func2<BOOL, BOOL, BOOL> OR;
	public final Func2<BOOL, BOOL, BOOL> AND;
	public final Func2<BOOL, BOOL, BOOL> LEQ;
	public final Func2<BOOL, BOOL, BOOL> ADD;
	public final Func2<BOOL, BOOL, BOOL> EQ;

	public final Func1<BOOL, Iterable<BOOL>> ALL;
	public final Func1<BOOL, Iterable<BOOL>> ANY;
	public final Func1<BOOL, Iterable<BOOL>> SUM;

	public Calculator(BOOL TRUE) {
		this.TRUE = TRUE;
		FALSE = not(TRUE);

		assert TRUE != null && FALSE != null;
		
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

		EQ = new Func2<BOOL, BOOL, BOOL>() {
			@Override
			public BOOL call(BOOL elem1, BOOL elem2) {
				assert elem1 != null && elem2 != null;
				return eq(elem1, elem2);
			}
		};

		ALL = Func1.reducer(TRUE, AND);
		ANY = Func1.reducer(FALSE, OR);
		SUM = Func1.reducer(FALSE, ADD);
	}

	public static Calculator<Boolean> BOOLEAN = new Calculator<Boolean>(true) {
		@Override
		public Boolean not(Boolean b) {
			return !b;
		}

		@Override
		public Boolean or(Boolean a, Boolean b) {
			return a || b;
		}

		@Override
		public Boolean add(Boolean a, Boolean b) {
			return a.booleanValue() != b.booleanValue();
		}
	};
}
