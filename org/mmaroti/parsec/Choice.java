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

public class Choice<T> extends Parser<T> {
	public final Parser<T> first;
	public final Parser<T> second;

	public Choice(Parser<T> first, Parser<T> second) {
		this.first = first;
		this.second = second;
	}

	public Consumed<T> parse(State state) {
		final Consumed<T> fc = first.parse(state);
		if (fc.consumed)
			return fc;

		final Consumed<T> sc = second.parse(state);
		if (sc.consumed)
			return sc;

		try {
			final Reply<T> fr = fc.getReply();
			return new Consumed<T>(false) {
				@Override
				public Reply<T> getReply() throws ParserException {
					return fr;
				}

				@Override
				public void addExpected(List<String> expected) {
					fc.addExpected(expected);
					sc.addExpected(expected);
				}
			};
		} catch (final ParserException exception) {
			return new Consumed<T>(false) {
				@Override
				public Reply<T> getReply() throws ParserException {
					throw exception;
				}

				@Override
				public void addExpected(List<String> expected) {
					fc.addExpected(expected);
					sc.addExpected(expected);
				}
			};
		}
	}
}
