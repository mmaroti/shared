/**
 *	Copyright (C) Miklos Maroti, 2014
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

package org.mmaroti.uasat;

import java.io.*;
import java.util.*;
import org.mmaroti.ua.util.Comparator;

public class FormulaSolver {
	public static abstract class Formula {
		abstract Formula negate();
	}

	private static Comparator<Formula[]> COMPARATOR = new Comparator<Formula[]>() {
		@Override
		public int hashCode(Formula[] formulas) {
			int hash0 = 0x12a3fe2d;
			int hash1 = 0x37abe8f9;

			for (int i = 0; i < formulas.length; ++i) {
				int hash = hash1
						+ (hash0 ^ (System.identityHashCode(formulas[i]) * 71523));
				if (hash < 0)
					hash -= 0x7fffffff;

				hash1 = hash0;
				hash0 = hash;
			}

			return hash0;
		}

		@Override
		public boolean equals(Formula[] formulas1, Formula[] formulas2) {
			if (formulas1.length != formulas2.length)
				return false;

			for (int i = 0; i < formulas1.length; ++i)
				if (formulas1[i] != formulas2[i])
					return false;

			return true;
		}

		@Override
		public Formula[] clone(Formula[] formulas) {
			return formulas.clone();
		}
	};

	private static class Literal extends Formula {
		final Negated negated = new Negated(this);
		final SatSolver.Literal literal;

		Literal(SatSolver.Literal literal) {
			this.literal = literal;
		}
		
		Formula negate() {
			return negated;
		}
	}

	private static class Negated extends Formula {
		private final Literal literal;

		Negated(Literal literal) {
			this.literal = literal;
		}

		public Formula negate() {
			return literal;
		}
	}

	public Formula addLiteral(String name) {
		return new Literal(name);
	}

	public Formula negate(Formula formula) {
		return null;
	}

	public void addFormula(Formula formula) {
	}

	public boolean getValue(boolean[] solution, Literal lit) {
		return solution[lit.id - 1];
	}

	public boolean[] solve() throws IOException {
		throw new UnsupportedOperationException();
	}
}
