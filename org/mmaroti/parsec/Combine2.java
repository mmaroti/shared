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

public abstract class Combine2<RESULT1, RESULT2, RESULT> extends Parser<RESULT> {

	public final Parser<RESULT1> first;
	public final Parser<RESULT2> second;

	public abstract RESULT compute(RESULT1 first);

	public abstract RESULT combine(RESULT1 first, RESULT2 second);

	public Combine2(Parser<RESULT1> first, Parser<RESULT2> second) {
		this.first = first;
		this.second = second;
	}

	public Consumption<RESULT> getConsumption(Input input) {
		final Consumption<RESULT1> fc = first.getConsumption(input);

		if (fc.consumed) {
			return new Consumption<RESULT>(true) {
				@Override
				public Result<RESULT> getResult() throws Error {
					Result<RESULT1> fr = fc.getResult();
					Consumption<RESULT2> sc = second
							.getConsumption(fr.leftover);
					Result<RESULT2> sr = sc.getResult();

					return new Result<RESULT>(combine(fr.result, sr.result),
							sr.leftover);
				}
			};
		}

		return null;
	}
}
