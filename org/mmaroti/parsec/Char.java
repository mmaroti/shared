/**
 *	Copyright (C) Miklos Maroti, 2013
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

package org.mmaroti.parsec;

import java.util.List;

public abstract class Char extends Parser<Character> {
	protected final String name;

	protected abstract boolean test(char head);

	protected Char(String name) {
		this.name = name;
	}

	protected Consumption<Character> getConsumption(final Input input) {
		if (input == null) {
			return new Consumption<Character>(false) {
				@Override
				public Result<Character> getResult() throws Error {
					throw new Error("unexpected end of input", name);
				}

				@Override
				public void addExpected(List<String> expected) {
					expected.add(name);
				}
			};
		}

		final char head = input.head;
		if (test(head)) {
			return new Consumption<Character>(true) {
				@Override
				public Result<Character> getResult() throws Error {
					return new Result<Character>(head, input.tail());
				}
			};
		} else {
			return new Consumption<Character>(false) {
				@Override
				public Result<Character> getResult() throws Error {
					throw new Error("unexpected token \"" + head + "\" at "
							+ input.getPosition(), name);
				}

				@Override
				public void addExpected(List<String> expected) {
					expected.add(name);
				}
			};
		}
	}
}
