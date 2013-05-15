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

public abstract class Sequence<RESULT1, RESULT2> extends
		Parser<RESULT2> {
	public final Parser<RESULT1> first;

	public abstract Parser<RESULT2> getSecond(RESULT1 result);

	public Sequence(Parser<RESULT1> first) {
		this.first = first;
	}

	public Consumption<RESULT2> getConsumption(Input input) {
		final Consumption<RESULT1> fc = first.getConsumption(input);
		if (fc.consumed) {
			return new Consumption<RESULT2>(true) {
				@Override
				public Result<RESULT2> getResult() throws Error {
					Result<RESULT1> fr = fc.getResult();
					Parser<RESULT2> second = getSecond(fr.result);
					Consumption<RESULT2> sc = second
							.getConsumption(fr.leftover);
					return sc.getResult();
				}
			};
		}

		try {
			Result<RESULT1> fr = fc.getResult();
			Parser<RESULT2> second = getSecond(fr.result);
			final Consumption<RESULT2> sc = second
					.getConsumption(fr.leftover);
			if (sc.consumed)
				return sc;
			else
				return new Consumption<RESULT2>(false) {
					@Override
					public Result<RESULT2> getResult() throws Error {
						return sc.getResult();
					}

					@Override
					public void addExpected(List<String> expected) {
						fc.addExpected(expected);
						sc.addExpected(expected);
					}
				};
		} catch (final Error error) {
			return new Consumption<RESULT2>(false) {
				@Override
				public Result<RESULT2> getResult() throws Error {
					throw error;
				}

				@Override
				public void addExpected(List<String> expected) {
					fc.addExpected(expected);
				}
			};
		}
	}
}
