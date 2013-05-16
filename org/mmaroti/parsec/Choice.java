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

public class Choice<RESULT> extends Parser<RESULT> {
	protected final Parser<RESULT> first;
	protected final Parser<RESULT> second;

	protected Choice(Parser<RESULT> first, Parser<RESULT> second) {
		this.first = first;
		this.second = second;
	}

	protected Consumption<RESULT> getConsumption(Input input) {
		final Consumption<RESULT> fc = first.getConsumption(input);
		if (fc.consumed)
			return fc;

		final Consumption<RESULT> sc = second.getConsumption(input);
		if (sc.consumed)
			return sc;

		try {
			final Result<RESULT> fr = fc.getResult();
			return new Consumption<RESULT>(false) {
				@Override
				public Result<RESULT> getResult() throws Error {
					return fr;
				}

				@Override
				public void addExpected(List<String> expected) {
					fc.addExpected(expected);
					sc.addExpected(expected);
				}
			};
		} catch (final Error error) {
			return new Consumption<RESULT>(false) {
				@Override
				public Result<RESULT> getResult() throws Error {
					throw error;
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
