/**
 * Copyright (C) Miklos Maroti, 2011
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

package org.reflocator.cites4;

import java.util.regex.Pattern;

public class Conversion
{
	public static String removeTex(String input)
	{
		input = input.replace("{\\'a}", "a");
		input = input.replace("{\\'i}", "i");
		input = input.replace("{\\i}", "i");
		input = input.replace("{\\'\\i}", "i");
		input = input.replace("{\\'o}", "o");
		input = input.replace("{\\'u}", "u");
		input = input.replace("{\\'e}", "e");
		input = input.replace("{\\'y}", "y");
		input = input.replace("{\\'c}", "c");
		input = input.replace("{\\`y}", "y");
		input = input.replace("{\\'A}", "A");
		input = input.replace("{\\'I}", "I");
		input = input.replace("{\\'O}", "O");
		input = input.replace("{\\'U}", "U");
		input = input.replace("{\\'E}", "E");
		input = input.replace("{\\\\\"a}", "a");
		input = input.replace("{\\\\\"o}", "o");
		input = input.replace("{\\\\\"O}", "O");
		input = input.replace("{\\\\\"u}", "u");
		input = input.replace("{\\v{z}}", "z");
		input = input.replace("{\\v{C}}", "C");
		input = input.replace("{\\v{c}}", "c");
		input = input.replace("{\\u{a}}", "a");
		input = input.replace("{\\c{c}}", "c");
		input = input.replace("{\\c{C}}", "C");
		input = input.replace("{\\c{S}}", "S");
		input = input.replace("{\\ss}", "ss");
		input = input.replace("{\\~n}", "n");
		input = input.replace("{\\u{g}}", "g");
		input = input.replace("{\\DJ}", "D");
		input = input.replace("\u2019", "'");
		input = input.replace("{\\`e}", "e");
		
		input = input.replace("$\\mu$", "u");
		input = input.replace(" \\& ", " and ");
		input = input.replace("\\& ", " and ");
		input = input.replace(" \\&", " and ");
		input = input.replace("\\&", " and ");
		
		return input;
	}
	
	public static String removeAccents(String input)
	{
		input = input.replace("�", "a");
		input = input.replace("�", "e");
		input = input.replace("�", "o");
		input = input.replace("�", "a");
		input = input.replace("�", "u");
		input = input.replace("�", "u");
		input = input.replace("�", "y");
		input = input.replace("�", "z");
		
		return input;
	}
	
	private static final Pattern bibTexPattern = Pattern.compile("^[a-zA-Z0-9']*$");
	
	public static void verifyBibTexId(String bibid)
	{
		if( ! bibTexPattern.matcher(bibid).matches() )
			throw new IllegalArgumentException("Illegal character in BibTex identifier: " + bibid); 
	}

	private static final Pattern titlePattern = Pattern.compile("^[0-9a-zA-Z,.:/()?+; \\-']*$");
	
	public static void verifyTitle(String title)
	{
		if( ! titlePattern.matcher(title).matches() )
			throw new IllegalArgumentException("Illegal character in title: " + title); 
	}

	private static final Pattern authorPattern = Pattern.compile("^[a-zA-Z,. \\-']*$");
	
	public static void verifyAuthor(String author)
	{
		if( ! authorPattern.matcher(author).matches() )
			throw new IllegalArgumentException("Illegal character in author: " + author); 
	}

	private static final Pattern issnPattern = Pattern.compile("^[0-9]{4}\\-[0-9]{4}$");
	
	public static void verifyIssn(String issn)
	{
		if( ! issnPattern.matcher(issn).matches() )
			throw new IllegalArgumentException("Illegal character in author: " + issn); 
	}
}
