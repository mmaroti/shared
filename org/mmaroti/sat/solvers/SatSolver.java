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

import java.text.*;
import java.util.*;
import org.mmaroti.sat.core.*;

public abstract class SatSolver<ELEM> extends BoolAlgebra<ELEM> {
	public boolean debugging = false;

	public SatSolver(ELEM FALSE, ELEM TRUE) {
		super(FALSE, TRUE);
	}

	public abstract void clear();

	public abstract ELEM variable();

	public abstract void clause(List<ELEM> clause);

	public abstract boolean solve();

	public abstract boolean decode(ELEM term);

	public final Func0<ELEM> VARIABLE = new Func0<ELEM>() {
		@Override
		public ELEM call() {
			return variable();
		}
	};

	public final Func1<Boolean, ELEM> DECODE = new Func1<Boolean, ELEM>() {
		@Override
		public Boolean call(ELEM elem) {
			return decode(elem);
		}
	};

	protected static DecimalFormat TIME_FORMAT = new DecimalFormat("0.00");

	@Override
	public ELEM and(ELEM a, ELEM b) {
		if (a == FALSE || b == FALSE)
			return FALSE;
		else if (a == TRUE)
			return b;
		else if (b == TRUE)
			return a;
		else if (a == b)
			return a;
		else if (a == not(b))
			return FALSE;

		ELEM var = variable();
		clause(Arrays.asList(a, not(var)));
		clause(Arrays.asList(b, not(var)));
		clause(Arrays.asList(not(a), not(b), var));
		return var;
	}

	@Override
	public ELEM all(Iterable<ELEM> elems) {
		ArrayList<ELEM> list = new ArrayList<ELEM>();
		for (ELEM a : elems) {
			if (a == FALSE)
				return FALSE;
			else if (a != TRUE)
				list.add(a);
		}

		if (list.size() == 0)
			return TRUE;
		else if (list.size() == 1)
			return list.get(0);

		ELEM var = variable();
		for (ELEM a : list)
			clause(Arrays.asList(a, not(var)));

		for (int i = 0; i < list.size(); i++)
			list.set(i, not(list.get(i)));

		list.add(var);
		clause(list);

		return var;
	}

	@Override
	public ELEM or(ELEM a, ELEM b) {
		if (a == TRUE || b == TRUE)
			return TRUE;
		else if (a == FALSE)
			return b;
		else if (b == FALSE)
			return a;
		else if (a == b)
			return a;
		else if (a == not(b))
			return TRUE;

		ELEM var = variable();
		clause(Arrays.asList(not(a), var));
		clause(Arrays.asList(not(b), var));
		clause(Arrays.asList(a, b, not(var)));
		return var;
	}

	@Override
	public ELEM any(Iterable<ELEM> elems) {
		ArrayList<ELEM> list = new ArrayList<ELEM>();
		for (ELEM a : elems) {
			if (a == TRUE)
				return TRUE;
			else if (a != FALSE)
				list.add(a);
		}

		if (list.size() == 0)
			return FALSE;
		else if (list.size() == 1)
			return list.get(0);

		ELEM var = variable();
		for (ELEM a : list)
			clause(Arrays.asList(not(a), var));

		list.add(not(var));
		clause(list);

		return var;
	}

	@Override
	public ELEM add(ELEM a, ELEM b) {
		if (a == TRUE)
			return not(b);
		else if (a == FALSE)
			return b;
		else if (b == TRUE)
			return not(a);
		else if (b == FALSE)
			return a;

		ELEM var = variable();
		clause(Arrays.asList(a, b, not(var)));
		clause(Arrays.asList(a, not(b), var));
		clause(Arrays.asList(not(a), b, var));
		clause(Arrays.asList(not(a), not(b), not(var)));
		return var;
	}

	public static void main(String[] args) {
		SatSolver<Integer> solver = new Sat4J();
		Integer a = solver.variable();
		Integer b = solver.variable();
		solver.clause(Arrays.asList(solver.or(a, b)));
		if (solver.solve())
			System.out.println(solver.decode(a) + " " + solver.decode(b));
		solver.clause(Arrays.asList(
				solver.add(a, solver.lift(solver.decode(a))),
				solver.add(b, solver.lift(solver.decode(b)))));
		if (solver.solve())
			System.out.println(solver.decode(a) + " " + solver.decode(b));
		solver.clause(Arrays.asList(
				solver.add(a, solver.lift(solver.decode(a))),
				solver.add(b, solver.lift(solver.decode(b)))));
		if (solver.solve())
			System.out.println(solver.decode(a) + " " + solver.decode(b));
		solver.clause(Arrays.asList(
				solver.add(a, solver.lift(solver.decode(a))),
				solver.add(b, solver.lift(solver.decode(b)))));
		if (solver.solve())
			System.out.println(solver.decode(a) + " " + solver.decode(b));
	}
}
