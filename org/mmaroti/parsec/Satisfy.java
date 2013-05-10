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

public abstract class Satisfy<TOKEN> extends Parser<TOKEN, TOKEN> {
	public final String name;

	public abstract boolean test(TOKEN token);

	public Satisfy(String name) {
		this.name = name;
	}

	public Consumption<TOKEN, TOKEN> getConsumption(final Input<TOKEN> input) {
		if (input == null) {
			return new Consumption<TOKEN, TOKEN>(false) {
				@Override
				public Result<TOKEN, TOKEN> getResult() throws Error {
					throw new Error("unexpected end of input");
				}

				@Override
				public void addExpected(List<String> expected) {
					expected.add(name);
				}
			};
		}

		final TOKEN token = input.head;
		if (test(token)) {
			return new Consumption<TOKEN, TOKEN>(true) {
				@Override
				public Result<TOKEN, TOKEN> getResult() throws Error {
					return new Result<TOKEN, TOKEN>(token, input.tail());
				}

				@Override
				public void addExpected(List<String> expected) {
				}
			};
		} else {
			return new Consumption<TOKEN, TOKEN>(false) {
				@Override
				public Result<TOKEN, TOKEN> getResult() throws Error {
					throw new Error("unexpected token \"" + token + "\" at "
							+ input.getPosition());
				}

				@Override
				public void addExpected(List<String> expected) {
					expected.add(name);
				}
			};
		}
	}

	public static final Satisfy<Character> DIGIT = new Satisfy<Character>(
			"digit") {
		@Override
		public boolean test(Character token) {
			return Character.isDigit(token);
		}
	};

	public static final Satisfy<Character> LETTER = new Satisfy<Character>(
			"digit") {
		@Override
		public boolean test(Character token) {
			return Character.isLetter(token);
		}
	};
}
