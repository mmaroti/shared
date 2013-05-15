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

public class CodeTokenizer {
	public static final Parser<Character> DIGIT = new Char("digit") {
		@Override
		public boolean test(char head) {
			return Character.isDigit(head);
		}
	};

	public static final Parser<Character> LETTER = new Char("letter") {
		@Override
		public boolean test(char head) {
			return Character.isLetter(head);
		}
	};

	public static final Parser<Character> WHITE_SPACE = new Char("white space") {
		@Override
		public boolean test(char head) {
			return Character.isWhitespace(head);
		}
	};

	public static void main(String[] args) throws Exception {
		Parser.Input input = StringList.create("a");
		System.out.println(LETTER.parse(input));
	}
}
