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

public class Test {

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

	public static final Parser<StringBuffer> LETTERS = new Many<Character, StringBuffer>(
			LETTER) {
		@Override
		public StringBuffer create() {
			return new StringBuffer();
		}

		@Override
		public void combine(StringBuffer result, Character elem) {
			result.append(elem);
		}
	};

	public static final Parser<Character> IDENTIFIER_START = new Char(
			"identifier start") {
		@Override
		public boolean test(char head) {
			return Character.isUnicodeIdentifierStart(head);
		}
	};

	public static final Parser<Character> IDENTIFIER_PART = new Char(
			"identifier part") {
		@Override
		public boolean test(char head) {
			return Character.isUnicodeIdentifierPart(head);
		}
	};

	public static final Parser<StringBuffer> IDENTIFIER = new Sequence<Character, StringBuffer>(
			IDENTIFIER_START) {
		@Override
		public Parser<StringBuffer> getSecond(final Character result) {
			return new Many<Character, StringBuffer>(IDENTIFIER_PART) {
				@Override
				public StringBuffer create() {
					StringBuffer buffer = new StringBuffer();
					buffer.append(result);
					return buffer;
				}

				@Override
				public void combine(StringBuffer result, Character elem) {
					result.append(elem);
				}
			};
		}
	};

	public static void main(String[] args) throws Exception {
		Parser.Input input = StringList.create("Ab1");
		System.out.println(IDENTIFIER.parse(input));
	}
}
