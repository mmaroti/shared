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

public class Return2<RESULT, TOKEN> extends
		Parser2<RESULT, TOKEN> {
	public final RESULT result;

	public Return2(RESULT result) {
		this.result = result;
	}

	public Consumption<RESULT, TOKEN> parse(final Input<TOKEN> input) {
		return new Consumption<RESULT, TOKEN>(false) {
			@Override
			public Result<RESULT, TOKEN> getResult() {
				return new Result<RESULT, TOKEN>(result, input);
			}
		};
	}
}
