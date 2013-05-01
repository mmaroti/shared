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

	// --- Values

	public static abstract class Value {
		public abstract String toString();
	}

	public static class BoolValue extends Value {
		public boolean value;

		public BoolValue(boolean value) {
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

	public static class IntValue extends Value {
		public int value;

		public IntValue(int value) {
			this.value = value;
		}

		public String toString() {
			return Integer.toString(value);
		}
	}

	public static class RealValue extends Value {
		public double value;

		public RealValue(double value) {
			this.value = value;
		}

		public String toString() {
			return Double.toString(value);
		}
	}

	public static class StringValue extends Value {
		public String value;

		public StringValue(String value) {
			this.value = value;
		}

		public String toString() {
			return '"' + value + '"';
		}
	}

	public static class TupleValue extends Value {
		public Value[] members;
		
		public TupleValue(Value[] members) {
			this.members = members;
		}
		
		public String toString() {
			String s = "(";

			for (int i = 0; i < members.length; ++i) {
				if (i != 0)
					s += ",";

				s += members[i].toString();
			}

			s += ")";
			return s;
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
		public Value value;

		public ConstExpr(Value value) {
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
				new IntValue(300)), new ConstExpr(new IntValue(12)));

		System.out.println(expr.toString());
	}
}
