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

import java.io.*;
import java.text.*;
import java.util.*;

public class MiniSat extends SatSolver {
	protected List<String> options = new ArrayList<String>();

	public void addOption(String option) {
		if (option == null || !option.startsWith("'"))
			throw new IllegalArgumentException();

		options.add(option);
	}

	protected static DecimalFormat TIME_FORMAT = new DecimalFormat("0.00");

	public boolean[] solve() {
		File input = null;
		PrintStream stream = null;

		BufferedReader reader = null;
		File output = null;

		try {
			input = File.createTempFile("minisat_input_", ".tmp");
			output = File.createTempFile("minisat_output_", ".tmp");

			stream = new PrintStream(input);
			dimacs(stream);
			stream.close();
			stream = null;

			List<String> args = new ArrayList<String>();
			args.add("minisat");
			args.addAll(options);
			args.add(input.getAbsolutePath());
			args.add(output.getAbsolutePath());

			if (debugging)
				System.err.print("Running minisat with " + variables
						+ " variables and " + clauses.size() + " clauses ... ");
			long time = System.currentTimeMillis();

			Process proc = Runtime.getRuntime().exec(
					args.toArray(new String[args.size()]));

			int result = -1;
			try {
				result = proc.waitFor();
			} catch (InterruptedException e) {
				throw new RuntimeException(e.getMessage());
			}

			time = System.currentTimeMillis() - time;

			if (result != 10 && result != 20) {
				if (debugging)
					System.err.println("exited with error code " + result);

				throw new RuntimeException("Minisat failed with error code "
						+ result);
			}

			if (debugging)
				System.err.println("finished in "
						+ TIME_FORMAT.format(0.001 * time) + " seconds: "
						+ (result == 10 ? "satisfiable." : "unsatisfiable."));

			if (result == 20)
				return null;

			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(output)));

			String line = reader.readLine();
			if (line == null || !line.equals("SAT"))
				throw new RuntimeException("Minisat failed to produce output");

			line = reader.readLine();
			assert line != null;

			String[] sol = line.split("\\s+");
			if (sol.length > variables + 1 || !sol[sol.length - 1].equals("0"))
				throw new RuntimeException("Minisat produced unexpected output");

			boolean[] solution = new boolean[variables + 1];

			for (int i = 0; i < sol.length; i++) {
				int n = Integer.parseInt(sol[i]);
				if (!sol[i].equals(Integer.toString(n))
						|| Math.abs(n) > variables)
					throw new RuntimeException("Minisat produced unexpected literal");

				if (n > 0)
					solution[n] = true;
				else
					solution[-n] = false;
			}

			return solution;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (stream != null)
				stream.close();

			if (input != null)
				input.delete();

			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
				}

			if (output != null)
				output.delete();
		}
	}
}
