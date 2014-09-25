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

package org.mmaroti.ua.alg;

/**
 * A class representing the signature of structures.
 * 
 * @author mmaroti@math.vanderbilt.edu
 */
public class Signature {
	/**
	 * Creates a signature whose operation and relation symbols are the
	 * specified symbols. Either of the lists can be <code>null</code>.
	 */
	public Signature(Symbol[] operations) {
		this.operations = operations;
		this.relations = null;
	}

	/**
	 * Constructs the signature object of an algebra
	 */
	public Signature(Algebra algebra) {
		operations = new Symbol[algebra.getOperations().length];
		for (int i = 0; i < operations.length; ++i)
			operations[i] = algebra.getOperations()[i].getSymbol();

		relations = null;
	}

	/**
	 * Creates a signature whose operation and relation symbols are the
	 * specified symbols. Either of the lists can be <code>null</code>.
	 */
	public Signature(Symbol[] operations, Symbol[] relations) {
		this.operations = operations;
		this.relations = relations;
	}

	protected Symbol[] operations;

	/**
	 * Returns the list of operation symbols of this signature.
	 */
	public Symbol[] getOperations() {
		return operations;
	}

	protected Symbol[] relations;

	/**
	 * Returns the list of relation symbols of this signature.
	 */
	public Symbol[] getRelations() {
		return relations;
	}

	public boolean equals(Object other) {
		Signature s = (Signature) other;
		return operations.equals(s.operations) && relations.equals(s.relations);
	}

	public static Signature SET = new Signature(new Symbol[0]);

	public static Signature GROUPOID = new Signature(new Symbol[] { new Symbol(
			"*", 2, 0, Symbol.INFIX | Symbol.LEFT_ASSOCIATIVE), });
}
