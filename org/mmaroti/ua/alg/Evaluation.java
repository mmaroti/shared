/**
 *	Copyright (C) Miklos Maroti, 2000-2004
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

import org.mmaroti.ua.set.*;

/**
 * This class represents a homomorphism from the term algebra to another
 * (or possibly the same) compatible algebra. It allows to work with
 * evaluations and substitutions of terms. A homomorphism is represented
 * as a map of the generators of the term algebra.
 * 
 * @author mmaroti@math.u-szeged.hu
 */
public class Evaluation
{
	public Evaluation(TermAlgebra domain, Algebra coDomain)
	{
		if( ! domain.isCompatible(coDomain) )
			throw new IllegalArgumentException("codomain is not compatible");
		
		this.domain = domain;
		this.coDomain = coDomain;
		map = new Map(Symbol.VARIABLES);
	}
	
	protected TermAlgebra domain;
	
	/**
	 * Returns the domain of the evaluation, which is a term algebra.
	 */
	public TermAlgebra getDomain()
	{
		return domain;
	}
	
	protected Algebra coDomain;
	
	/**
	 * Returns the codomain of the homomorphism
	 */
	public Algebra getCoDomain()
	{
		return coDomain;
	}
	
	/**
	 * Contains the mapping of the generators to elements of the codomain.
	 */
	protected Map map;

	/**
	 * Evaluates the specified term in the target algebra using the 
	 * specified map of generators to elements. This method returns
	 * an element of the target algebra. 
	 */
	public Object getValue(Term term)
	{
		if( term.isVariable() )
		{
			Object image = map.getValue(term.symbol);
			if( image != null )
				return image;
			
			// if this is an endomorphism then the identity entries are not stored
			if( domain == coDomain )
				return term;

			throw new IllegalArgumentException("the image of this variable is not set");
		}
			
		final Term[] subterms = term.subterms;
		Object[] args = new Object[subterms.length];

		for(int i = 0; i < args.length; ++i)
			args[i] = getValue(subterms[i]);

		return coDomain.getOperations()[term.symbol.index].getValue(args);
	}

	/**
	 * Composes the specified homomorphism with this object and
	 * stores the result in this object. The codomain of this object 
	 * must be the same as the domain of the specified homomorphism.
	 */
	public void compose(Evaluation homomorphism)
	{
		if( homomorphism.getDomain() != coDomain )
			throw new IllegalArgumentException("the codomain of this homomorphism" +
				" and the domain of the specified homomorphism are not the same");

		int i = map.getSize();
		while( --i >= 0 )
			map.setValue(i, homomorphism.getValue((Term)map.getValue(i)));
	
		// if this is an endomorphism then the identity entries are not stored
		if( domain == coDomain )
		{
			final Map otherMap = homomorphism.map;
			
			i = otherMap.getSize();
			while( --i >= 0 )
			{
				if( ! map.contains(otherMap.getElement(i)) )
					map.put(otherMap.getElement(i), otherMap.getValue(i));
			}
		}

		// update the codomain		
		coDomain = homomorphism.coDomain;
	}
	
	/**
	 * Builds an endomorphism (represented as a map of the generators to terms)
	 * which maps the <code>source</code> term to the <code>target</code> term.
	 * This endomorphism is extended. The function returns <code>true</code>
	 * if a mapping was found, or <code>false</code> if there exists no extension.
	 */
	public boolean extend(Term source, Term target)
	{
		if( source.isVariable() )
		{
			Object o = map.getValue(source.symbol);
			if( o == null )
			{
				map.put(source.symbol, target);
				return true;
			}

			return coDomain.getUniverse().areEquals(o, target);
		}
	
		if( source.symbol != target.symbol )
			return false;

		for(int i = 0; i < source.subterms.length; ++i)
			if( ! extend(source.subterms[i], target.subterms[i]) )
				return false;
	
		return true;
	}
	
	/**
	 * Sets the image of one of the variables elements.
	 */
	public final void set(Symbol variable, Object image)
	{
		if( image instanceof Symbol )
			throw new IllegalArgumentException("");
		
		if( ! variable.isVariable() )
			throw new IllegalArgumentException("only for variables can the image be specified");
			
		map.put(variable, image);
	}
	
	/**
	 * Returns the image of one of the variables.
	 */
	public final Object get(Symbol variable)
	{
		return map.getValue(variable);
	}

	/**
	 * Clears all assigned values to the variables.
	 * If this homomorphism is an endomorphism, then it resets it
	 * to the identity map.
	 */
	public final void clear()
	{
		map.clear();
	}
	
	/**
	 * Returns the string representation of this evaluation.
	 */
	public String toString()
	{
		String s = "[";

		for(int i = 0; i < map.getSize(); s += ", ", ++i)
			s += map.getElement(i) + " |-> " + map.getValue(i);
					
		return s + "]";
	}
}
