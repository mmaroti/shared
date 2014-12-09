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

public class TypeCheck extends Context {
	public static class Op1 implements Operator1 {
		public final String name;
		public final Domain domain;

		public Op1(String name, Domain domain) {
			assert (domain != null && name != null);

			this.name = name;
			this.domain = domain;
		}

		public Object apply(Object input) {
			assert (input instanceof Domain);

			if (input != domain)
				throw new IllegalArgumentException("Operation " + name
						+ " is expecting " + domain.getName() + " instead of "
						+ ((Domain) input).getName());

			return domain;
		}
	}

	public static class Op2 implements Operator2 {
		public final String name;
		public final Domain domain;

		public Op2(String name, Domain domain) {
			assert (domain != null && name != null);

			this.name = name;
			this.domain = domain;
		}

		public Object apply(Object input1, Object input2) {
			assert (input1 instanceof Domain && input2 instanceof Domain);

			Object input = input1 != domain ? input1 : input2;
			if (input != domain)
				throw new IllegalArgumentException("Operation " + name
						+ " is expecting " + domain.getName() + " instead of "
						+ ((Domain) input).getName());

			return domain;
		}
	}

	public Operator1 operator1(String name, Domain domain) {
		return new Op1(name, domain);
	}

	public Operator2 operator2(String name, Domain domain) {
		return new Op2(name, domain);
	}

	public Object constant(Domain domain, int index) {
		assert (domain != null);

		if (index < 0 || index >= domain.getSize())
			throw new IllegalArgumentException("Domain " + domain.getName()
					+ " does not have an element of index " + index);

		return domain;
	}

	public Op1 NOT = new Op1("not", Domain.BOOLEAN);
	public Op2 AND = new Op2("and", Domain.BOOLEAN);
	public Op2 OR = new Op2("or", Domain.BOOLEAN);

	public Object not(Object input) {
		return NOT.apply(input);
	}

	public Object and(Object input1, Object input2) {
		return AND.apply(input1, input2);
	}

	public Object or(Object input1, Object input2) {
		return OR.apply(input1, input2);
	}
}
