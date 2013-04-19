/**
 *	Copyright (C) Miklos Maroti, 2007
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

package org.mmaroti.ua.alg;

import org.mmaroti.ua.util.*;

/**
 * This structure captures a term rewrite system over an absolutely free algebra.
 * 
 * @author mmaroti@math.u-szeged.hu
 */
public class RewriteSystem extends TermAlgebra
{
	/**
	 * Constructs a term algebra with the specified list of operation symbols,
	 * and rewriting rules. 
	 */
	public RewriteSystem(TermAlgebra terms, Rule[] rules)
	{
		operations = new Op[terms.getOperations().length];
		
		for(int i = 0; i < operations.length; ++i)
			this.operations[i] = new Op(terms.getOperations()[i].getSymbol());

		this.rules = rules;
	}

	public static class Rule
	{
		/**
		 * Constructs a rule with the specified template and
		 * substitution;
		 */
		public Rule(Term template, Term replacement)
		{
			this.template = template;
			this.replacement = replacement;
		}
		
		protected Term template;
		protected Term replacement;
		
		/**
		 * Returns the textual representation of rules written
		 * as implications.
		 */
		public String toString()
		{
			return template.toString() + " -> " + replacement.toString();
		}

		public int hashCode()
		{
			return template.hashCode() + 7793 * replacement.hashCode();
		}
		
		public boolean equals(Object o)
		{
			if( !(o instanceof Rule) )
				return false;

			Rule r = (Rule)o;

			return template.equals(r.template) && replacement.equals(r.replacement);
		}
	}

	public static Rule parseRule(TermAlgebra terms, String string)
	{
		Parser parser = new Parser();
		String[] pair = parser.parseFirstSeparator(string, "->");

		if( pair != null )
		{
			Term template = Term.parse(terms, pair[0]);
			Term replacement = Term.parse(terms, pair[1]);
		
			if( template != null && replacement != null )
				return new Rule(template, replacement);
		}

		throw new IllegalArgumentException();
	}

	/**
	 * The set of rules of the rewrite system
	 */
	protected Rule[] rules;

	/**
	 * Returns the list of rules of this rewrite system
	 */
	public Rule[] getRules()
	{
		return rules;
	}

	/**
	 * We apply the rewriting rules recursively to simplify 
	 * the term only at the topmost occurrence.
	 */
	protected Term rewriteTopLevel(Term term)
	{
		int count = rules.length;

		if( count == 0 )
			return term;

		for(;;)
		{
			for(int i = 0; i < rules.length; ++i)
			{
				if( --count < 0 )
					return term;
				
				Rule rule = rules[i];
			
				Evaluation endomorphism = createEndomorphism();
				if( endomorphism.extend(rule.template, term) )
				{
					term = (Term)endomorphism.getValue(rule.replacement);
					count = rules.length;
				}
			}
		}
	}
	
	protected class Op extends TermAlgebra.Op
	{
		Op(Symbol symbol)
		{
			super(symbol);
		}

		public Object getValue(Object[] args)
		{
			return rewriteTopLevel((Term)super.getValue(args));
		}
	}

	/**
	 * Returns a string representation of this object.
	 */
	public String toString()
	{
		String s = new String();

		for(int i = 0; i < rules.length; ++i)
			s += rules[i].toString() + "\n";
		
		return s;
	}
}
