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

package org.mmaroti.uasat;

/**
 * A class representing the signature of structures.
 * 
 * @author mmaroti@math.vanderbilt.edu
 */
public class Signature {
	protected final Symbol[] operations;
	protected final Symbol[] relations;

	private static final Symbol[] EMPTY = new Symbol[0];

	/**
	 * Creates a signature whose operation and relation symbols are the
	 * specified symbols. Neither of the lists can be <code>null</code>.
	 */
	public Signature(Symbol[] operations, Symbol[] relations) {
		assert (operations != null && relations != null);

		this.operations = operations;
		this.relations = relations;
	}

	/**
	 * Returns the list of operation symbols of this signature.
	 */
	public Symbol[] getOperations() {
		return operations;
	}

	/**
	 * Returns the list of relation symbols of this signature.
	 */
	public Symbol[] getRelations() {
		return relations;
	}

	public static Signature SET = new Signature(EMPTY, EMPTY);

	public static Signature GROUPOID = new Signature(new Symbol[] { new Symbol(
			"*", 2, 0, Symbol.INFIX | Symbol.LEFT_ASSOCIATIVE) }, EMPTY);
}
