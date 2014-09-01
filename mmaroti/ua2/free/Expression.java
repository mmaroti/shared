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

import java.util.*;

public class Expression {
	public Expression(Symbol symbol, Expression[] subs) {
		checkInput(symbol, subs);

		this.symbol = symbol;
		this.subs = subs;

		int level = 1;
		for (Expression sub : subs)
			level += sub.level;

		this.level = level;
	}

	public Expression(Symbol symbol) {
		checkInput(symbol, EMPTY);

		this.symbol = symbol;
		this.subs = EMPTY;
		this.level = 1;
	}

	public Expression(Symbol symbol, Expression sub0) {
		Expression[] subs = new Expression[] { sub0 };
		checkInput(symbol, subs);

		this.symbol = symbol;
		this.subs = subs;
		this.level = 1 + sub0.level;
	}

	public Expression(Symbol symbol, Expression sub0, Expression sub1) {
		Expression[] subs = new Expression[] { sub0, sub1 };
		checkInput(symbol, subs);

		this.symbol = symbol;
		this.subs = subs;
		this.level = 1 + sub0.level + sub1.level;
	}

	public Expression(Symbol symbol, Expression sub0, Expression sub1,
			Expression sub2) {
		Expression[] subs = new Expression[] { sub0, sub1, sub2 };
		checkInput(symbol, subs);

		this.symbol = symbol;
		this.subs = subs;
		this.level = 1 + sub0.level + sub1.level + sub2.level;
	}

	public Expression(Symbol symbol, Expression sub0, Expression sub1,
			Expression sub2, Expression sub3) {
		Expression[] subs = new Expression[] { sub0, sub1, sub2, sub3 };
		checkInput(symbol, subs);

		this.symbol = symbol;
		this.subs = subs;
		this.level = 1 + sub0.level + sub1.level + sub2.level + sub3.level;
	}

	private void checkInput(Symbol symbol, Expression[] subs) {
		assert (symbol.arity == subs.length || symbol == Symbol.LAND || symbol == Symbol.LOR);

		if (symbol.type == Symbol.VARIABLE)
			assert (subs.length == 0);
		else if (symbol.type == Symbol.OPERATION
				|| symbol.type == Symbol.RELATION) {
			for (Expression sub : subs)
				assert (sub.symbol.isTerm());
		} else if (symbol.type == Symbol.CONNECTIVE) {
			for (Expression sub : subs)
				assert (sub.symbol.isFormula());
		} else
			assert (false);
	}

	public final Symbol symbol;
	public Expression[] subs;
	public final int level;

	public boolean isTerm() {
		return symbol.isTerm();
	}

	public boolean isFormula() {
		return symbol.isFormula();
	}

	public boolean isEquation() {
		return symbol == Symbol.EQUALS;
	}

	public Symbol[] getVariables() {
		HashSet<Symbol> symbols = new HashSet<Symbol>();
		extractSymbols(Symbol.VARIABLE, symbols);
		return symbols.toArray(new Symbol[symbols.size()]);
	}

	public Symbol[] getOperations() {
		HashSet<Symbol> symbols = new HashSet<Symbol>();
		extractSymbols(Symbol.OPERATION, symbols);
		return symbols.toArray(new Symbol[symbols.size()]);
	}

	public void extractSymbols(int type, HashSet<Symbol> symbols) {
		if (symbol.type == type)
			symbols.add(symbol);
		else {
			for (Expression sub : subs)
				sub.extractSymbols(type, symbols);
		}
	}

	private static final Expression[] EMPTY = new Expression[0];

	public static final Expression VARX = new Expression(Symbol.VARX);
	public static final Expression VARY = new Expression(Symbol.VARY);
	public static final Expression VARZ = new Expression(Symbol.VARZ);
}
