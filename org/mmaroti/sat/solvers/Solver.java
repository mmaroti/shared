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

package org.mmaroti.sat.solvers;

import java.util.*;
import org.mmaroti.sat.core.*;

public abstract class Solver<BOOL> extends BoolAlg<BOOL> {
	public boolean debugging = false;

	public Solver(BOOL TRUE) {
		super(TRUE);
	}

	public abstract void clear();

	public abstract BOOL variable();

	public abstract void clause(List<BOOL> clause);

	public abstract boolean solve();

	public abstract boolean decode(BOOL term);

	public final Func0<BOOL> VARIABLE = new Func0<BOOL>() {
		@Override
		public BOOL call() {
			return variable();
		}
	};

	public final Func1<Boolean, BOOL> DECODE = new Func1<Boolean, BOOL>() {
		@Override
		public Boolean call(BOOL elem) {
			return decode(elem);
		}
	};
}
