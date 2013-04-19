/**
 *	Copyright (C) Miklos Maroti, 2002-2007
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

package org.mmaroti.ua.util;

import java.util.*;

/**
 * This is a helper class to parse strings. 
 * 
 * @author mmaroti@math.u-szeged.hu
 */
public class Parser
{
	/**
	 * Constructs a standard parser
	 */
	public Parser()
	{
	}

	/**
	 * Constructs a parser with the specified bracket types and white spaces
	 */
	public Parser(String openingBrackets, String closingBrackets, String whiteSpaces)
	{
		if( openingBrackets.length() != closingBrackets.length() )
			throw new IllegalArgumentException();
		
		this.openingBrackets = new String[openingBrackets.length()];
		this.closingBrackets = new String[closingBrackets.length()];
		
		for(int i = 0; i < openingBrackets.length(); ++i)
		{
			this.openingBrackets[i] = openingBrackets.substring(i, i+1);
			this.closingBrackets[i] = closingBrackets.substring(i, i+1);
		}

		this.whiteSpaces = whiteSpaces;
	}
	
	String[] openingBrackets = new String[] { "(", "[", "{" };
	String[] closingBrackets = new String[] { ")", "]", "}" };

	/**
	 * Sets a new set of opening and closing brackets. Character sequences
	 * enclosed in these brackets are considered as single objects and
	 * parsing functions will not stop inside and break these groups. 
	 * 
	 * @param openings the new list of opening brackets
	 * @param closings the new list of closing brackets
	 */
	public void setBrackets(String[] openings, String[] closings)
	{
		if( openings.length != closings.length )
			throw new IllegalArgumentException();
	
		openingBrackets = openings;
		closingBrackets = closings;
	}

	/**
	 * Returns the first index of <code>match</code> in the
	 * specified string.
	 * 
	 * @param string the string that is searched
	 * @param match the string to be searched for
	 * @param start the starting position
	 * @return the first matching index that is greater than equal 
	 * to <code>start</code>, or <code>-1</code> if no match is found.
	 */
	public int indexOf(String string, String match, int start)
	{
		for(;;)
		{
			int p = string.indexOf(match, start);
			int i = -1;
			
			for(int k = 0; k < openingBrackets.length; ++k)
			{
				int a = string.indexOf(openingBrackets[k], start);
				if( a >= 0 && a < p )
				{
					p = a;
					i = k;
				}
			}

			if( i < 0 )
				return p;

			start = indexOf(string, closingBrackets[i], p + openingBrackets[i].length());
			if( start < 0 )
				throw new IllegalArgumentException("no matching parenthesis");

			start += closingBrackets[i].length();
		}
	}

	/**
	 * Returns the first index of <code>match</code> in the
	 * specified string.
	 * 
	 * @param string the string that is searched
	 * @param match the string to be searched for
	 * @return the first matching index or <code>-1</code> if no match is found.
	 */
	public int indexOf(String string, String match)
	{
		return indexOf(string, match, 0);
	}
	
	/**
	 * Returns the last index of <code>match</code> in the
	 * specified string.
	 * 
	 * @param string the string that is searched
	 * @param start the starting position
	 * @param match the string to be searched for
	 * @return the last matching index that is smaller than equal 
	 * to <code>start</code>, or <code>-1</code> if no match is found.
	 */
	public int lastIndexOf(String string, int start, String match)
	{
		for(;;)
		{
			int p = string.lastIndexOf(match, start);
			int i = -1;

			for(int k = 0; k < closingBrackets.length; ++k)
			{
				int a = string.lastIndexOf(closingBrackets[k], start);
				if( a >= 0 && a > p )
				{
					p = a;
					i = k;
				}
			}

			if( i < 0 )
				return p;

			start = lastIndexOf(string, p - 1, openingBrackets[i]);
			if( start < 0 )
				throw new IllegalArgumentException("no matching parenthesis");

			--start;
		}
	}

	/**
	 * Returns the last index of <code>match</code> in the
	 * specified string.
	 * 
	 * @param string the string that is searched
	 * @param match the string to be searched for
	 * @return the last matching index, or <code>-1</code> if no match is found.
	 */
	public int lastIndexOf(String string, String match)
	{
		return lastIndexOf(string, string.length() - match.length(), match);
	}

	/**
	 * Parses the specified string into a list of substrings
	 * separated by the specified separator. If the specified
	 * string consists only of white spaces, then an empty list 
	 * is returned.
	 * 
	 * @param string the string to be parsed
	 * @param separator the separator, usually ","
	 * @return the list of separated substrings 
	 */
	public String[] parseList(String string, String separator)
	{
		if( string == null )
			return null;
		
		List<String> elements = new ArrayList<String>();

		if( separator.length() == 0 )
		{
			for(int i = 0; i < string.length(); ++i)
				elements.add(string.substring(i, i+1));
		}
		else
		{
			int start = 0;
			for(;;)
			{
				int n = indexOf(string, separator, start);
				if( n < 0 )
				{
					elements.add(string.substring(start));
					break;
				}
			
				elements.add(string.substring(start, n));
				start = n + separator.length();
			}
		}

		if( elements.size() == 1 && parseBlank(elements.get(0)) )
			return new String[0];
		
		return elements.toArray(new String[elements.size()]);
	}
	
	protected String whiteSpaces = " \t\n";

	/**
	 * Sets the list of whitespace characters.
	 */
	public void setWhiteSpaces(String spaces)
	{
		whiteSpaces = spaces;
	}
	
	/**
	 * Checks if the specified string contains only whitespace characters.
	 */
	public boolean parseBlank(String string)
	{
		int i = string.length();
		while( --i >= 0 )
		{
			if( whiteSpaces.indexOf(string.charAt(i)) < 0 )
				return false;
		}
		return true;
	}
	
	/**
	 * Parses a string for a match of separator and the pair of
	 * substrings before and after the match is returned.
	 * 
	 * @param string the string to be searched
	 * @param separator the string separating the two halves
	 * @return a pair of string if a match was found, or <code>null</code>
	 * otherwise.
	 */
	public String[] parseFirstSeparator(String string, String separator)
	{
		if( string == null )
			return null;
		
		int index = indexOf(string, separator);
		if( index < 0 )
			return null;
		
		return new String[] { string.substring(0,index), string.substring(index + separator.length()) };
	}
	
	/**
	 * Parses a string for a match of separator and the pair of
	 * substrings before and after the match is returned. This version
	 * finds the last match with the given separator.
	 * 
	 * @param string the string to be searched
	 * @param separator the string separating the two halves
	 * @return a pair of string if a match was found, or <code>null</code>
	 * otherwise.
	 */
	public String[] parseLastSeparator(String string, String separator)
	{
		if( string == null )
			return null;
		
		int index = lastIndexOf(string, separator);
		if( index < 0 )
			return null;
		
		return new String[] { string.substring(0,index), string.substring(index + separator.length()) };
	}
	
	/**
	 * Checks if the specified string starts with the given token.
	 * If it does, then the rest of the string is returned, otherwise 
	 * <code>null</code> is returned.
	 * 
	 * @param string the string to be searched
	 * @param token the token that needs match the first token of the string
	 * @return the rest of the string if a match is found, or <code>null</code>
	 * otherwise.
	 */
	public String parseFirstToken(String string, String token)
	{
		if( token.length() == 0 )
			return string;
		
		if( string != null )
		{
			String[] pair = parseFirstSeparator(string, token);
			if( pair != null && parseBlank(pair[0]) )
				return pair[1];
		}

		return null;
	}

	/**
	 * Checks if the specified string ends with the given token.
	 * If it does, then the rest (the part before the token) of the 
	 * string is returned, otherwise <code>null</code> is returned.
	 * 
	 * @param string the string to be searched
	 * @param token the token that needs match the last token of the string
	 * @return the rest of the string if a match is found, or <code>null</code>
	 * otherwise.
	 */
	public String parseLastToken(String string, String token)
	{
		if( token.length() == 0 )
			return string;
		
		if( string != null )
		{
			String[] pair = parseLastSeparator(string, token);
			if( pair != null && parseBlank(pair[1]) )
				return pair[0];
		}

		return null;
	}
	
	/**
	 * Checks if the specified string starts and ends with the given tokens.
	 * If it does, then the inner part is returned which can further be parsed,
	 * otherwise <code>null</code> is returned.
	 * 
	 * @param string the string to be parsed
	 * @param start the token that should match the beginning of the string
	 * @param end the token that should match the end of the string
	 * @return the inner part of the string, or <code>null</code> if there was no match
	 */
	public String parseEnclosingTokens(String string, String start, String end)
	{
		return parseLastToken(parseFirstToken(string, start), end);
	}
}
