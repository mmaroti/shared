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

public abstract class Sequence<S, T> extends Parser<S> {
	public final Parser<T> first;

	public abstract Parser<S> getSecond(T result);

	public Sequence(Parser<T> first) {
		this.first = first;
	}

	public Consumed<S> parse(State state) {
		final Consumed<T> fc = first.parse(state);
		if (fc.consumed) {
			return new Consumed<S>(true) {
				@Override
				public Reply<S> getReply() throws ParserException {
					Reply<T> fr = fc.getReply();
					Parser<S> second = getSecond(fr.result);
					Consumed<S> sc = second.parse(fr.state);
					return sc.getReply();
				}

				@Override
				public void addExpected(List<String> expected) {
				}
			};
		}

		try {
			Reply<T> fr = fc.getReply();
			Parser<S> second = getSecond(fr.result);
			final Consumed<S> sc = second.parse(fr.state);
			if (sc.consumed)
				return sc;
			else
				return new Consumed<S>(false) {
					@Override
					public Reply<S> getReply() throws ParserException {
						return sc.getReply();
					}

					@Override
					public void addExpected(List<String> expected) {
						fc.addExpected(expected);
						sc.addExpected(expected);
					}
				};
		} catch (final ParserException exception) {
			return new Consumed<S>(false) {
				@Override
				public Reply<S> getReply() throws ParserException {
					throw exception;
				}

				@Override
				public void addExpected(List<String> expected) {
					fc.addExpected(expected);
				}
			};
		}
	}
}
