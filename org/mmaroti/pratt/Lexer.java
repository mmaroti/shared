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

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class Lexer implements Iterator<Token> {
	private final Reader reader;
	private final HashMap<String, Token> operators;
	int head = NONE;

	private Lexer(Reader reader) {
		this.reader = reader;
		operators = new HashMap<String, Token>();
		operators.put(PLUS.name, PLUS);
		operators.put(MINUS.name, MINUS);
		operators.put(MULT.name, MULT);
		operators.put(DIV.name, DIV);
	}

	public Lexer(String input) {
		this(new StringReader(input));
	}

	public Lexer(File file) throws FileNotFoundException {
		this(new BufferedReader(new InputStreamReader(
				new FileInputStream(file), Charset.forName("UTF-8"))));
	}

	private static int EOF = -1;
	private static int NONE = -2;

	private int peek() {
		if (head == NONE) {
			try {
				head = reader.read();
			} catch (IOException e) {
				throw new IllegalStateException("read error");
			}
		}

		return head;
	}

	private void whitespace() {
		while (Character.isWhitespace(peek()))
			head = NONE;
	}

	private int integer() {
		long a = 0;
		for (;;) {
			int c = peek();
			if (!Character.isDigit(c))
				return (int) a;

			head = NONE;
			a = a * 10 + Character.digit(c, 10);
			if (a < 0 || a > Integer.MAX_VALUE)
				throw new IllegalStateException("too large constant");
		}
	}

	private String identifier() {
		StringBuilder builder = new StringBuilder();

		for (;;) {
			int c = peek();
			if (!Character.isLetterOrDigit(c))
				return builder.toString();

			head = NONE;
			builder.appendCodePoint(c);
		}
	}

	private boolean isOperator(int cp) {
		return (33 <= cp && cp <= 39) || (42 <= cp && cp <= 47)
				|| (58 <= cp && cp <= 63) || cp == 92 || cp == 94;
	}

	private String operator() {
		StringBuilder builder = new StringBuilder();

		for (;;) {
			int c = peek();
			if (!isOperator(c))
				return builder.toString();

			head = NONE;
			builder.appendCodePoint(c);
		}
	}

	@Override
	public boolean hasNext() {
		whitespace();
		return peek() != EOF;
	}

	public final static Token.InfixOp PLUS = new Token.InfixOp("+", 1);
	public final static Token.InfixOp MINUS = new Token.InfixOp("-", 1);
	public final static Token.InfixOp MULT = new Token.InfixOp("*", 2);
	public final static Token.InfixOp DIV = new Token.InfixOp("/", 2);

	@Override
	public Token next() {
		int c = peek();

		if (Character.isDigit(c))
			return new Token.IntLiteral(integer());
		else if (Character.isLetter(c))
			return new Token.Identifier(identifier());
		else if (isOperator(c)) {
			String op = operator();
			Token t = operators.get(op);
			if (t != null)
				return t;
			else
				throw new IllegalStateException("unknown operator " + op);
		} else {
			assert !Character.isWhitespace(c);
			throw new IllegalStateException("invalid character " + c);
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public static void main(String[] args) {
		Lexer lexer = new Lexer(" 12+3 * 2");
		while (lexer.hasNext())
			System.out.println(lexer.next());
	}
}
