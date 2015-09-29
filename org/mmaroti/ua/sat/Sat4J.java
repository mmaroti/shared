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

import java.text.*;
import org.sat4j.core.*;
import org.sat4j.minisat.*;
import org.sat4j.specs.*;

public class Sat4J extends SatSolver {
	protected int addedClauses = 0;
	protected ISolver solver = null;

	public void clear() {
		addedClauses = 0;
		solver = SolverFactory.newDefault();
		super.clear();
	}

	protected static DecimalFormat TIME_FORMAT = new DecimalFormat("0.00");

	@Override
	public boolean[] solve() {
		try {
			for (int i = addedClauses; i < clauses.size(); i++)
				solver.addClause(new VecInt(clauses.get(i)));
			addedClauses = clauses.size();

			if (debugging)
				System.err.print("Running Sat4J with " + variables
						+ " variables and " + clauses.size() + " clauses ... ");
			long time = System.currentTimeMillis();

			boolean satisfiable = solver.isSatisfiable();

			if (debugging)
				System.err.println("finished in "
						+ TIME_FORMAT.format(0.001 * time) + " seconds: "
						+ (satisfiable ? "satisfiable." : "unsatisfiable."));

			if (!satisfiable)
				return null;

			int[] model = solver.model();
			boolean[] solution = new boolean[variables + 1];

			for (int i = 0; i < model.length; i++) {
				int a = model[i];
				if (a == 0 || Math.abs(i) > variables)
					throw new RuntimeException(
							"Sat4J produced unexpected output");

				if (a > 0)
					solution[a] = true;
			}

			return solution;
		} catch (ContradictionException e) {
			return null;
		} catch (TimeoutException e) {
			System.err.println("SAT4J timeout");
			throw new RuntimeException(e);
		}
	}
}
