/**
 *	Copyright (C) Miklos Maroti, 2005
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

package org.mmaroti.ua.math;

import org.mmaroti.ua.set.*;
import java.math.BigInteger;

/**
 * This class represents the ring of integers.
 */
public class Rationals extends Universe implements Field
{
	private SubUniverse rationals = new SubUniverse(new ProductUniverse(Objects.INSTANCE,2));

	public int getIndex(Object a)
	{
		return rationals.getIndex(rationals.xor(a));
	}
	
	public Object getElement(int a)
	{
		if( a < 0 || a >= rationals.getSize() )
			throw new IllegalArgumentException("this index was never assigned to an" +				" integer from this class");
		
		return rationals.getElement(a);
	}
	
	public int getSize()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Constructs the field of rationals.
	 */
	public Rationals()
	{
	}

	public int sum(int a, int b)
	{
		return getIndex(sum(getElement(a), getElement(b)));
	}

	public int negative(int a)
	{
		return getIndex(negative(getElement(a)));
	}

	public int zero()
	{
		return getIndex(zeroElement());
	}

	public int product(int a, int b)
	{
		return getIndex(product(getElement(a), getElement(b)));
	}

	public int unit()
	{
		return getIndex(unitElement());
	}

	public int inverse(int a)
	{
		return getIndex(inverse(getElement(a)));
	}

	protected BigInteger[] simplify(BigInteger numerator, BigInteger denominator)
	{
		if( numerator.equals(BigInteger.ZERO) )
			return new BigInteger[] { BigInteger.ZERO, BigInteger.ONE };

		BigInteger gcd = numerator.gcd(denominator);
		if( denominator.signum() < 0 )
			gcd = gcd.negate();
			
		return new BigInteger[] { numerator.divide(gcd), denominator.divide(gcd) };
	}
	
	public Object sum(Object a, Object b)
	{
		BigInteger[] as = (BigInteger[])a;
		BigInteger[] bs = (BigInteger[])b;
		
		return simplify(as[0].multiply(bs[1]).add(as[1].multiply(bs[0])), as[1].multiply(bs[1]));
	}

	public Object negative(Object a)
	{
		BigInteger[] as = (BigInteger[])a;
		return  new BigInteger[] { as[0].negate(), as[1] };
	}

	public Object zeroElement()
	{
		return new BigInteger[] { BigInteger.ZERO, BigInteger.ONE };
	}

	public Object product(Object a, Object b)
	{
		BigInteger[] as = (BigInteger[])a;
		BigInteger[] bs = (BigInteger[])b;
		
		return simplify(as[0].multiply(bs[0]), as[1].multiply(bs[1]));
	}

	public Object unitElement()
	{
		return new BigInteger[] { BigInteger.ONE, BigInteger.ONE };
	}

	public Object inverse(Object a)
	{
		BigInteger[] as = (BigInteger[])a;

		if( as[0].signum() == 0 )
			throw new IllegalArgumentException();
		
		return simplify(as[1], as[0]);
	}
	
	public int character()
	{
		return 0;
	}
	
	public int signum(Object a)
	{
		BigInteger[] as = (BigInteger[])a;
		
		return as[0].signum();
	}
	
	public boolean areEquals(Object a, Object b)
	{
		BigInteger[] as = (BigInteger[])a;
		BigInteger[] bs = (BigInteger[])b;

		return as[0].equals(bs[0]) && as[1].equals(bs[1]); 
	}

	public int hashCode(Object a)
	{
		BigInteger[] as = (BigInteger[])a;
		
		return as[0].hashCode() + as[1].hashCode();
	}

	public String toString(Object a)
	{
		return toString(a, '/');
	}

	/**
	 * Converts the rational to a string in the following formats:
	 * 	'/' : -1/7				1/7
	 *  't' : \\frac{-1}{7}		\\frac{1}{7}
	 *  'n' : -\\frac{1}{7}		\\frac{1}{7}
	 *  'p' : -\\frac{1}{7}		+\\frac{1}{7}
	 */
	public String toString(Object a, char style)
	{
		BigInteger[] as = (BigInteger[])a;

		if( as[1].equals(BigInteger.ONE) )
			return (style == 'p' && as[0].signum() > 0 ? "+" : "") + as[0].toString();
	
		if( style == '/' )
			return as[0].toString() + "/" + as[1].toString();
		else if( style == 't' || ((style == 'n' || style == 'p') && as[0].signum() >= 0) )
			return (style == 'p' ? "+" : "") + "\\frac{" + as[0].toString() + "}{" + as[1].toString() + "}"; 
		else if( style == 'n' || style == 'p' )
			return "-\\frac{" + as[0].negate().toString() + "}{" + as[1].toString() + "}"; 
		else
			throw new IllegalStateException();
	}

	public Object parse(String string)
	{
		string = string.trim();
	
		int i = string.indexOf('/');
		if( i < 0 )
			return new BigInteger[] { new BigInteger(string), BigInteger.ONE };
		
		BigInteger numerator = new BigInteger(string.substring(0,i));
		BigInteger denominator = new BigInteger(string.substring(i+1)); 

		if( denominator.signum() == 0 )
			throw new IllegalArgumentException();
		
		return simplify(numerator, denominator);
	}
}
