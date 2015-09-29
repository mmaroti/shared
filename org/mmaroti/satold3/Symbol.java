/**
 *	Copyright (C) Miklos Maroti, 2003
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

package org.mmaroti.satold3;

/**
 * A class representing operation and relation symbols. The symbol name, arity
 * and special properties are stored in this object.
 * 
 * @author mmaroti@math.vanderbilt.edu
 */
public class Symbol {
	protected final String name;
	protected final int arity;
	protected final int priority;
	public final int properties;

	/**
	 * The symbol is a infix operation. If an operation is not infix, then it
	 * will be expressed as a function.
	 */
	public static final int INFIX = 0x0010;

	/**
	 * The symbol is a left-associative binary operation, that is,
	 * <code>(xy)z</code> will be written as <code>xyz</code>.
	 */
	public static final int LEFT_ASSOCIATIVE = 0x0001;

	/**
	 * The symbol is a right-associative binary operation, that is,
	 * <code>x(yz)</code> will be written as <code>xyz</code>.
	 */
	public static final int RIGHT_ASSOCIATIVE = 0x0002;

	/**
	 * The symbol is an associative binary operation, that is,
	 * <code>(xy)(uv)</code> will be written as <code>xyuv</code>.
	 * 
	 * @see #LEFT_ASSOCIATIVE
	 * @see #RIGHT_ASSOCIATIVE
	 */
	public static final int ASSOCIATIVE = 0x0003;

	/**
	 * Creates a new symbol with the given parameters.
	 */
	public Symbol(String name, int arity, int priority, int properties) {
		if (arity < 0)
			throw new IllegalArgumentException("The arity must be nonnegative");

		this.name = name;
		this.arity = arity;
		this.priority = priority;
		this.properties = properties;
	}

	/**
	 * Returns the name of the symbol as it would be produced in LaTeX.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the arity of the symbol.
	 */
	public final int getArity() {
		return arity;
	}

	/**
	 * Returns the priority of the operation. Higher priority operations are
	 * performed first, then lower priority ones. For example, the priority of
	 * multiplication is a higher number than that of addition.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Decides whether an expression with the given topmost operation symbol
	 * should be enclosed in parenthesis if it is an argument of this symbol.
	 * 
	 * @param index
	 *            The coordinate of this symbol into which the argument is
	 *            plugged in.
	 * @param argument
	 *            The topmost operation symbol of the argument. If
	 *            <code>null</code>, then it is not enclosed.
	 * @return <code>true</code> if the argument should be enclosed in
	 *         parenthesis.
	 */
	public boolean isBraced(int index, Symbol argument) {
		if (hasProperty(INFIX)) {
			if (argument == null || argument.priority > priority
					|| !argument.hasProperty(INFIX))
				return false;

			if (arity == 2 && index == 0 && hasProperty(LEFT_ASSOCIATIVE)
					&& argument == this)
				return false;

			if (arity == 2 && index == 1 && hasProperty(RIGHT_ASSOCIATIVE)
					&& argument == this)
				return false;

			return true;
		}

		return false;
	}

	/**
	 * Returns <code>true</code> if the symbol has the specified property.
	 */
	public boolean hasProperty(int property) {
		return (properties & property) == property;
	}
}
