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

public abstract class Parser<RESULT, TOKEN> {
	public abstract Consumption<RESULT, TOKEN> parse(Input<TOKEN> input);

	public static abstract class Input<TOKEN> {
		public final TOKEN head;

		public abstract Input<TOKEN> tail() throws Error;

		public abstract String getPosition();

		public Input(TOKEN head) {
			this.head = head;
		}
	}

	public static class Error extends Exception {
		private static final long serialVersionUID = -151114319314581777L;

		public final String message;

		public Error(String message) {
			this.message = message;
		}
	}

	public static abstract class Consumption<RESULT, TOKEN> {
		public final boolean consumed;

		public Consumption(boolean consumed) {
			this.consumed = consumed;
		}

		public abstract Result<RESULT, TOKEN> getResult() throws Error;

		public void addExpected(List<String> expected) {
		}
	}

	public static final class Result<RESULT, TOKEN> {
		public final RESULT result;
		public final Input<TOKEN> leftover;

		public Result(RESULT result, Input<TOKEN> leftover) {
			this.result = result;
			this.leftover = leftover;
		}
	}
}
