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

public abstract class Solver<BOOL> extends BoolAlg<BOOL> {
	public Solver(BOOL TRUE) {
		super(TRUE);
	}

	public abstract Tensor<BOOL> tensor(int[] shape);

	public abstract void clear();

	public abstract void ensure(BOOL literal);

	public void ensure(BOOL[] clause) {
		ensure(all(Arrays.asList(clause)));
	}

	public abstract Map<String, Tensor<Boolean>> solveOne(Problem problem);

	public abstract List<Map<String, Tensor<Boolean>>> solveAll(Problem problem);
}
