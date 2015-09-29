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

package org.mmaroti.satold1;

import java.io.*;
import java.util.*;

public abstract class Solver {
	public boolean debugging = false;

	// variable indices in clauses and solution start at 1
	public abstract boolean[] solve(int variables, List<int[]> clauses)
			throws IOException;

	public static void dimacs(int variables, List<int[]> clauses,
			PrintStream stream) {
		stream.println("p cnf " + variables + " " + clauses.size());
		for (int[] clause : clauses) {
			for (int i : clause) {
				assert i != 0 && Math.abs(i) <= variables;

				stream.print(i);
				stream.print(' ');
			}
			stream.println('0');
		}
	}
}
