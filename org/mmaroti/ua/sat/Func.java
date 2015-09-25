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

public abstract class Func<ELEM, ELEM1> {
	public abstract ELEM call(ELEM1 elem);

	public <ELEM2> Func<ELEM, ELEM2> combine(final Func<ELEM1, ELEM2> fun) {
		final Func<ELEM, ELEM1> me = this;
		return new Func<ELEM, ELEM2>() {
			@Override
			public ELEM call(ELEM2 elem) {
				return me.call(fun.call(elem));
			}
		};
	}

	public <ELEM2, ELEM3> Func2<ELEM, ELEM2, ELEM3> combine(
			final Func2<ELEM1, ELEM2, ELEM3> fun) {
		final Func<ELEM, ELEM1> me = this;
		return new Func2<ELEM, ELEM2, ELEM3>() {
			@Override
			public ELEM call(ELEM2 elem2, ELEM3 elem3) {
				return me.call(fun.call(elem2, elem3));
			}
		};
	}

	@SuppressWarnings("rawtypes")
	public final static Func OBJ_ID = new Func() {
		@Override
		public Object call(Object elem) {
			return elem;
		}
	};

	@SuppressWarnings("unchecked")
	public final static Func<Integer, Integer> INT_ID = OBJ_ID;

	@SuppressWarnings("unchecked")
	public final static Func<Boolean, Boolean> BOOL_ID = OBJ_ID;

	@SuppressWarnings("unchecked")
	public final static Func<Double, Double> REAL_ID = OBJ_ID;

	public final static Func<Integer, Integer> INT_NEG = new Func<Integer, Integer>() {
		@Override
		public Integer call(Integer elem) {
			return -elem;
		}
	};

	public final static Func<Boolean, Boolean> BOOL_NEG = new Func<Boolean, Boolean>() {
		@Override
		public Boolean call(Boolean elem) {
			return !elem;
		}
	};

	public final static Func<Double, Double> REAL_NEG = new Func<Double, Double>() {
		@Override
		public Double call(Double elem) {
			return -elem;
		}
	};
}
