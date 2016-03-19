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

public abstract class Token {
	abstract int precedence();

	abstract Expr initial(Parser parser);

	abstract Expr combine(Parser parser, Expr left);

	public static abstract class Op extends Token {
		public final String name;

		public Op(String name) {
			this.name = name;
		}
	}

	public static class InfixOp extends Op {
		public final int prec;

		public InfixOp(String name, int prec) {
			super(name);
			this.prec = prec;
		}

		@Override
		int precedence() {
			return prec;
		}

		@Override
		Expr initial(Parser parser) {
			throw new IllegalStateException(
					"no left operand for infix operator " + name);
		}

		@Override
		Expr combine(Parser parser, Expr left) {
			Expr right = parser.parse(prec);
			return new Expr.Binary(name, left, right);
		}

		@Override
		public String toString() {
			return "InfixOp(" + name + "," + prec + ")";
		}
	}

	public static class PrefixOp extends Op {
		public final int prec;

		public PrefixOp(String name, int prec) {
			super(name);
			this.prec = prec;
		}

		@Override
		int precedence() {
			return 0;
		}

		@Override
		Expr initial(Parser parser) {
			Expr expr = parser.parse(prec);
			return new Expr.Unary(name, expr);
		}

		@Override
		Expr combine(Parser parser, Expr left) {
			throw new IllegalStateException(
					"extra left operand for prefix operator " + name);
		}

		@Override
		public String toString() {
			return "PrefixOp(" + name + "," + prec + ")";
		}
	}

	public static class PreInfOp extends Op {
		public final int prec;

		public PreInfOp(String name, int prec) {
			super(name);
			this.prec = prec;
		}

		@Override
		int precedence() {
			return prec;
		}

		@Override
		Expr initial(Parser parser) {
			Expr expr = parser.parse(prec);
			return new Expr.Unary(name, expr);
		}

		@Override
		Expr combine(Parser parser, Expr left) {
			Expr right = parser.parse(prec);
			return new Expr.Binary(name, left, right);
		}

		@Override
		public String toString() {
			return "PreInfOp(" + name + "," + prec + ")";
		}
	}

	public static class IntLiteral extends Token {
		public final int value;

		public IntLiteral(int value) {
			this.value = value;
		}

		@Override
		int precedence() {
			return 0;
		}

		@Override
		Expr initial(Parser parser) {
			return new Expr.IntLiteral(value);
		}

		@Override
		Expr combine(Parser parser, Expr left) {
			throw new IllegalStateException();
		}

		@Override
		public String toString() {
			return "IntLiteral(" + value + ")";
		}
	}

	public static class Identifier extends Token {
		public final String value;

		public Identifier(String value) {
			this.value = value;
		}

		@Override
		int precedence() {
			return 0;
		}

		@Override
		Expr initial(Parser parser) {
			return new Expr.Identifier(value);
		}

		@Override
		Expr combine(Parser parser, Expr left) {
			throw new IllegalStateException();
		}

		@Override
		public String toString() {
			return "Identifier(" + value + ")";
		}
	}
}
