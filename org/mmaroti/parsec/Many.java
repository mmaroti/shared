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

public abstract class Many<ELEM, RESULT> extends Parser<RESULT> {
	public final Parser<ELEM> elem;

	public Many(Parser<ELEM> elem) {
		this.elem = elem;
	}

	public abstract RESULT create();

	public abstract void combine(RESULT result, ELEM elem);

	public Consumption<RESULT> getConsumption(final Input input) {
		final Consumption<ELEM> c = elem.getConsumption(input);
		if (c.consumed) {
			return new Consumption<RESULT>(true) {
				@Override
				public Result<RESULT> getResult() throws Error {
					RESULT accumulator = create();

					Consumption<ELEM> d = c;
					Result<ELEM> r;
					do {
						r = d.getResult();
						combine(accumulator, r.result);
						d = elem.getConsumption(r.leftover);
					} while (d.consumed);

					return new Result<RESULT>(accumulator, r.leftover);
				}
			};
		} else {
			return new Consumption<RESULT>(false) {
				@Override
				public Result<RESULT> getResult() throws Error {
					return new Result<RESULT>(create(), input);
				}
			};
		}
	}
}
