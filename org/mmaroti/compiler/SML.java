/**
 * Copyright (C) Miklos Maroti, 2013
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

package org.mmaroti.compiler;

public class SML {

	// --- Constants

	public interface Constant {
		public String toString();
	}

	public static class BoolConst implements Constant {
		public boolean value;

		public BoolConst(boolean value) {
			this.value = value;
		}

		public String toString() {
			if (value)
				return "true";
			else {
				return "false";
			}
		}
	}

	public static class IntConst implements Constant {
		public int value;

		public IntConst(int value) {
			this.value = value;
		}

		public String toString() {
			return Integer.toString(value);
		}
	}

	public static class RealConst implements Constant {
		public double value;

		public RealConst(double value) {
			this.value = value;
		}

		public String toString() {
			return Double.toString(value);
		}
	}

	public static class StringConst implements Constant {
		public String value;

		public StringConst(String value) {
			this.value = value;
		}

		public String toString() {
			return '"' + value + '"';
		}
	}

	// --- Operators

	public interface BinaryOp {
		public int getPrecedence();

		public String getSymbol();
	}

	public static class PlusOp implements BinaryOp {
		public int getPrecedence() {
			return 4;
		}

		public String getSymbol() {
			return "+";
		}
	}

	public static class TimesOp implements BinaryOp {
		public int getPrecedence() {
			return 6;
		}

		public String getSymbol() {
			return "*";
		}
	}

	public static class MinusOp implements BinaryOp {
		public int getPrecedence() {
			return 5;
		}

		public String getSymbol() {
			return "-";
		}
	}

	public static class LessOrEqOp implements BinaryOp {
		public int getPrecedence() {
			return 7;
		}

		public String getSymbol() {
			return "<=";
		}
	}

	// --- Expressions

	public static abstract class Expression {
		public abstract boolean isValue();

		public abstract int getPrecedence();

		public abstract String toString();

		public String toString(int precedence) {
			String s = toString();

			if (getPrecedence() > precedence)
				return s;
			else
				return '(' + s + ')';
		}
	}

	public static class ConstExpr extends Expression {
		public Constant value;

		public ConstExpr(Constant value) {
			this.value = value;
		}

		public boolean isValue() {
			return true;
		}

		public int getPrecedence() {
			return 10;
		}

		public String toString() {
			return value.toString();
		}
	}

	public static class BinaryOpExpr extends Expression {
		public BinaryOp operator;
		public Expression left, right;

		public BinaryOpExpr(BinaryOp operator, Expression left, Expression right) {
			this.operator = operator;
			this.left = left;
			this.right = right;
		}

		public boolean isValue() {
			return false;
		}

		public int getPrecedence() {
			return operator.getPrecedence();
		}

		public String toString() {
			int precedence = getPrecedence();
			return left.toString(precedence) + " " + operator.getSymbol() + " "
					+ right.toString(precedence);
		}
	}

	public static class VariableExpr extends Expression {
		public String variable;

		public boolean isValue() {
			return false;
		}

		public int getPrecedence() {
			return 10;
		}

		public String toString() {
			return variable;
		}
	}

	public static class FunctionExpr extends Expression {
		public String parameter;
		public Expression body;

		public boolean isValue() {
			return true;
		}

		public int getPrecedence() {
			return 1;
		}

		public String toString() {
			return "fn " + parameter + " => " + body.toString(1);
		}
	}

	public static class ApplicationExpr extends Expression {
		public Expression function, argument;

		public boolean isValue() {
			return false;
		}

		public int getPrecedence() {
			return 2;
		}

		public String toString() {
			return function.toString(2) + " " + argument.toString(10);
		}
	}

	public static class TupleExpr extends Expression {
		public Expression[] members;

		public boolean isValue() {
			for (Expression member : members) {
				if (!member.isValue())
					return false;
			}
			return true;
		}

		public int getPrecedence() {
			return 10;
		}

		public String toString() {
			String s = "(";

			for (int i = 0; i < members.length; ++i) {
				if (i != 0)
					s += ",";

				s += members[i].toString(0);
			}

			s += ")";
			return s;
		}
	}

	public static class ProjectionExpr extends Expression {
		public int position;
		public Expression tuple;

		public boolean isValue() {
			return false;
		}

		public int getPrecedence() {
			return 2;
		}

		public String toString() {
			return "#" + Integer.toString(position) + " " + tuple.toString(2);
		}
	}

	public static class LetExpr extends Expression {
		public String variable;
		public Expression value, expr;

		public boolean isValue() {
			return false;
		}

		public int getPrecedence() {
			return 10;
		}

		public String toString() {
			return "let val " + variable + " = " + value.toString(0) + " in "
					+ expr.toString(0) + " end";
		}
	}

	public static class IfExpr extends Expression {
		public Expression conditional, first, second;

		public boolean isValue() {
			return false;
		}

		public int getPrecedence() {
			return 3;
		}

		public String toString() {
			return "if " + conditional.toString(0) + " then "
					+ first.toString(0) + " else " + second.toString(3);
		}
	}

	public static void main(String[] args) {
		Expression expr = new BinaryOpExpr(new PlusOp(), new ConstExpr(
				new IntConst(300)), new ConstExpr(new IntConst(12)));

		System.out.println(expr.toString());
	}
}
