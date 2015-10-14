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

public abstract class SatSolver<BOOL> extends BoolAlg<BOOL> {
	public boolean debugging = false;

	public SatSolver(BOOL TRUE) {
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

	protected static DecimalFormat TIME_FORMAT = new DecimalFormat("0.00");

	@Override
	public BOOL and(BOOL a, BOOL b) {
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

		BOOL var = variable();
		clause(Arrays.asList(a, not(var)));
		clause(Arrays.asList(b, not(var)));
		clause(Arrays.asList(not(a), not(b), var));
		return var;
	}

	@Override
	public BOOL all(Iterable<BOOL> elems) {
		ArrayList<BOOL> es = new ArrayList<BOOL>();
		for (BOOL a : elems) {
			if (a == FALSE)
				return FALSE;
			else if (a != TRUE)
				es.add(not(a));
		}

		if (es.size() == 0)
			return TRUE;
		else if (es.size() == 1)
			return es.get(0);

		BOOL var = variable();
		for (BOOL a : es)
			clause(Arrays.asList(not(a), not(var)));

		es.add(var);
		clause(es);
		return var;
	}

	@Override
	public BOOL or(BOOL a, BOOL b) {
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

		BOOL var = variable();
		clause(Arrays.asList(not(a), var));
		clause(Arrays.asList(not(b), var));
		clause(Arrays.asList(a, b, not(var)));
		return var;
	}

	@Override
	public BOOL any(Iterable<BOOL> elems) {
		ArrayList<BOOL> es = new ArrayList<BOOL>();
		for (BOOL a : elems) {
			if (a == TRUE)
				return TRUE;
			else if (a != FALSE)
				es.add(a);
		}

		if (es.size() == 0)
			return FALSE;
		else if (es.size() == 1)
			return es.get(0);

		BOOL var = variable();
		for (BOOL a : es)
			clause(Arrays.asList(not(a), var));

		es.add(not(var));
		clause(es);
		return var;
	}

	@Override
	public BOOL add(BOOL a, BOOL b) {
		if (a == TRUE)
			return not(b);
		else if (a == FALSE)
			return b;
		else if (b == TRUE)
			return not(a);
		else if (b == FALSE)
			return a;

		BOOL var = variable();
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
