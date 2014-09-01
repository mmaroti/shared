/*
 * Copyright (C) 2014 Miklos Maroti
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package mmaroti.ua2.free;

public class Symbol {
	public Symbol(int type, String name, int arity) {
		assert (arity >= 0);

		if (type == VARIABLE)
			assert (arity == 0);

		this.type = type;
		this.name = name;
		this.arity = arity;
	}

	public final int type;
	public final String name;
	public final int arity;

	public boolean isTerm() {
		return type == VARIABLE || type == OPERATION;
	}

	public boolean isFormula() {
		return type == RELATION || type == CONNECTIVE;
	}

	public static final int VARIABLE = 1;
	public static final int OPERATION = 2;
	public static final int RELATION = 3;
	public static final int CONNECTIVE = 4;

	public static Symbol VARX = new Symbol(VARIABLE, "x", 0);
	public static Symbol VARY = new Symbol(VARIABLE, "y", 0);
	public static Symbol VARZ = new Symbol(VARIABLE, "z", 0);

	public static Symbol PLUS = new Symbol(OPERATION, "+", 2);
	public static Symbol PRODUCT = new Symbol(OPERATION, "*", 2);

	public static Symbol LESSEQ = new Symbol(RELATION, "<=", 2);
	public static Symbol EQUALS = new Symbol(RELATION, "=", 2);

	public static Symbol LNOT = new Symbol(CONNECTIVE, "!", 1);
	public static Symbol LAND = new Symbol(CONNECTIVE, "&", 2);
	public static Symbol LOR = new Symbol(CONNECTIVE, "|", 2);
	public static Symbol LIMP = new Symbol(CONNECTIVE, "->", 2);
	public static Symbol LIFF = new Symbol(CONNECTIVE, "<->", 2);
}
