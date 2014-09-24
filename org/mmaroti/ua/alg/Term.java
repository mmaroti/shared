/**
 *	Copyright (C) Miklos Maroti, 2000-2007
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

package org.mmaroti.ua.alg;

import org.mmaroti.ua.util.*;
import java.util.*;

public class Term {
	/**
	 * Constructs a term whose topmost operation is <code>symbol</code> and
	 * subterms are <code>subterms</code>.
	 */
	protected Term(Symbol symbol, Term[] subterms) {
		this.symbol = symbol;
		this.subterms = subterms;
	}

	protected Symbol symbol;

	/**
	 * Returns the topmost operation of this term. If this term is a variable
	 * then this method returns <code>null</code>.
	 */
	public final Symbol getSymbol() {
		return symbol;
	}

	protected Term[] subterms;

	/**
	 * Returns the subterms of this term. If this term is a variable then this
	 * method returns <code>null</code>.
	 */
	public final Term[] getSubterms() {
		return subterms;
	}

	/**
	 * Returns the length of this term (the number of variables plus the number
	 * of operations). The length is always positive.
	 */
	public int getLength() {
		int length = 1;

		for (int i = 0; i < subterms.length; ++i)
			length += subterms[i].getLength();

		return length;
	}

	/**
	 * Returns the depth of this term. This number is always non-negative. The
	 * depth of variables and constants is zero.
	 */
	public int getDepth() {
		int depth = 0;

		for (int i = 0; i < subterms.length; ++i) {
			int d = subterms[i].getDepth();
			if (d > depth)
				depth = d;
		}

		return depth + 1;
	}

	/**
	 * Returns the number of occurrences of a subterm.
	 */
	public int getNumberOfOccurences(Term subterm) {
		if (subterm == this)
			return 1;

		int occurences = 0;
		for (int i = 0; i < subterms.length; ++i)
			occurences += subterms[i].getNumberOfOccurences(subterm);

		return occurences;
	}

	protected String subtermToString(int index) {
		if (symbol.isBraced(index, subterms[index].symbol))
			return "(" + subterms[index].toString() + ")";
		else
			return subterms[index].toString();
	}

	public String toString() {
		if (symbol.hasProperty(Symbol.VARIABLE))
			return symbol.name + symbol.index;

		if (symbol.hasProperty(Symbol.INFIX)) {
			if (subterms.length == 0)
				return symbol.getName();
			else if (subterms.length == 1)
				return symbol.getName() + subtermToString(0);
			else if (subterms.length == 2)
				return subtermToString(0) + symbol.getName()
						+ subtermToString(1);
		}

		String s = symbol.getName() + '(';
		for (int i = 0; i < subterms.length; ++i) {
			if (i > 0)
				s += ",";

			s += subterms[i];
		}
		return s + ')';
	}

	protected static Parser parser = new Parser();

	private static boolean bracedLastParse;

	protected static Term parseSubterm(Symbol symbol, int index,
			TermAlgebra algebra, String substring) {
		Term term = parse(algebra, substring);

		if (term != null && symbol.isBraced(index, term.symbol)
				&& !bracedLastParse)
			term = null;

		return term;
	}

	public static Term parse(TermAlgebra algebra, String string) {
		string = string.trim();

		if (string.startsWith("(") && string.endsWith(")")) {
			Term term = parse(algebra, string.substring(1, string.length() - 1));
			if (term != null) {
				bracedLastParse = true;
				return term;
			}
		}

		Symbol symbol = (Symbol) Symbol.VARIABLES.parse(string);
		if (symbol != null) {
			bracedLastParse = false;
			return new Term(symbol, new Term[0]);
		}

		Operation[] ops = algebra.getOperations();
		for (int i = 0; i < ops.length; ++i) {
			symbol = ops[i].getSymbol();
			String name = symbol.getName();

			if (symbol.hasProperty(Symbol.INFIX)) {
				if (symbol.getArity() == 0 && string.equals(name)) {
					bracedLastParse = false;
					return new Term(symbol, new Term[0]);
				} else if (symbol.getArity() == 1 && string.startsWith(name)) {
					Term subterm = parseSubterm(symbol, 0, algebra,
							string.substring(name.length()));

					if (subterm != null) {
						bracedLastParse = false;
						return new Term(symbol, new Term[] { subterm });
					}
				} else if (symbol.getArity() == 2) {
					int pos = -1;
					for (;;) {
						pos = parser.indexOf(string, name, pos + 1);
						if (pos < 0)
							break;

						Term subterm1 = parseSubterm(symbol, 0, algebra,
								string.substring(0, pos));
						Term subterm2 = parseSubterm(symbol, 1, algebra,
								string.substring(pos + name.length()));

						if (subterm1 != null && subterm2 != null) {
							bracedLastParse = false;
							return new Term(symbol, new Term[] { subterm1,
									subterm2 });
						}
					}
				}
			}

			if (string.startsWith(name + "(") && string.endsWith(")")) {
				String[] substrings = parser.parseList(string.substring(
						name.length() + 1, string.length() - 1), ",");
				if (substrings != null && substrings.length == symbol.arity) {
					Term[] subterms = new Term[substrings.length];

					int j = subterms.length;
					while (--j >= 0)
						if ((subterms[j] = parse(algebra, substrings[j])) == null)
							break;

					if (j < 0)
						return new Term(symbol, subterms);
				}
			}
		}

		return null;
	}

	/**
	 * Returns <code>true</code> if this term is a variable, <code>false</code>
	 * otherwise.
	 */
	public boolean isVariable() {
		return symbol.isVariable();
	}

	/**
	 * This function returns the set of variables of this term.
	 */
	public Set<Symbol> getVariables() {
		HashSet<Symbol> variables = new HashSet<Symbol>();
		addMyVariablesTo(variables);
		return variables;
	}

	/**
	 * This function adds the generators of this term to the set of generators
	 * stored in <code>set</code>.
	 */
	public void addMyVariablesTo(Collection<Symbol> collection) {
		if (!isVariable()) {
			for (int i = 0; i < subterms.length; ++i)
				subterms[i].addMyVariablesTo(collection);
		} else
			collection.add(symbol);
	}

	/**
	 * Checks if this term contains the specified subterm.
	 * 
	 * @param subterm
	 *            another term of this algebra
	 * @return <code>true</code> if <code>subterm</code> is a subterm of this
	 *         term.
	 */
	public boolean hasSubterm(Term subterm) {
		if (subterm == this)
			return true;

		for (int i = 0; i < subterms.length; ++i)
			if (subterms[i].hasSubterm(subterm))
				return true;

		return false;
	}

	public int hashCode() {
		int hashcode = symbol.hashCode();

		int i = subterms.length;
		while (--i >= 0)
			hashcode += subterms[i].hashCode();

		return hashcode;
	}

	public boolean equals(Object object) {
		Term other = (Term) object;

		if (symbol != other.symbol)
			return false;

		int i = subterms.length;
		while (--i >= 0)
			if (!subterms[i].equals(other.subterms[i]))
				return false;

		return true;
	}
}
