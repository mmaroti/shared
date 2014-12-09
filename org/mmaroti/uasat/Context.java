/**
 *	Copyright (C) Miklos Maroti, 2014
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

public abstract class Context {
	public interface Operator1 {
		public Object apply(Object input);
	}

	public interface Operator2 {
		public Object apply(Object input1, Object input2);
	}

	public abstract Object constant(Domain domain, int index);

	public abstract Operator1 operator1(String name, Domain domain);

	public abstract Operator2 operator2(String name, Domain domain);

	public abstract Object not(Object input);

	public abstract Object and(Object input1, Object input2);

	public abstract Object or(Object input1, Object input2);

	public final Object FALSE = constant(Domain.BOOLEAN, 0);
	public final Object TRUE = constant(Domain.BOOLEAN, 1);
}
