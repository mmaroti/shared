/**
 *	Copyright (C) Miklos Maroti, 2016
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

package org.mmaroti.pratt;

import java.util.*;

public class Parser {
	private final Iterator<Token> input;
	private Token head;

	private Token peek() {
		if (head == null && input.hasNext())
			head = input.next();

		return head;
	}

	public Expr parse(int prec) {
		Token token = peek();
		if (token == null)
			throw new IllegalStateException("unexpected end of input");
		head = null;

		Expr expr = token.initial();
		for (;;) {
			token = peek();
			if (token == null || prec >= token.precedence())
				break;
			head = null;

			expr = token.combine(this, expr);
		}

		return expr;
	}

	public Parser(Iterator<Token> input) {
		this.input = input;
		this.head = null;
	}

	public static void main(String[] args) {
		Lexer lexer = new Lexer(" 12-3 * 2 - 2");
		Parser parser = new Parser(lexer);
		Expr expr = parser.parse(0);
		System.out.println(expr);
	}
}
