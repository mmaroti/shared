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

import java.util.*;

public abstract class Parser<RESULT> {
	public abstract Consumption<RESULT> getConsumption(Input input);

	public RESULT parse(Input input) throws Error {
		return getConsumption(input).getResult().result;
	}

	protected static abstract class Input {
		public final char head;

		public abstract Input tail() throws Error;

		public abstract String getPosition();

		public Input(char head) {
			this.head = head;
		}
	}

	protected static class Error extends Exception {
		private static final long serialVersionUID = -151114319314581777L;
		public List<String> expecting = new ArrayList<String>();

		public String getMessage() {
			String s = " expecting: ";
			for (int i = 0; i < expecting.size(); ++i) {
				if (i != 0)
					s += ", ";

				s += expecting.get(i);
			}

			return super.getMessage() + s;
		}

		public Error(String message) {
			super(message);
		}

		public Error(String message, String expected) {
			super(message);
			expecting.add(expected);
		}
	}

	protected static abstract class Consumption<RESULT> {
		public final boolean consumed;

		public Consumption(boolean consumed) {
			this.consumed = consumed;
		}

		public abstract Result<RESULT> getResult() throws Error;

		public void addExpected(List<String> expected) {
			assert (consumed == false);
		}
	}

	protected static final class Result<RESULT> {
		public final RESULT result;
		public final Input leftover;

		public Result(RESULT result, Input leftover) {
			this.result = result;
			this.leftover = leftover;
		}
	}
}
