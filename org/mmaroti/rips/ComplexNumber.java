/*
 * Created on July 1, 2005
 */
package org.mmaroti.rips;

/**
 * @author mmaroti@gmail.com
 */
public class ComplexNumber 
{
	public double x;
	public double y;

	public ComplexNumber(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public ComplexNumber(ComplexNumber z)
	{
		this.x = z.x;
		this.y = z.y;
	}

	static public ComplexNumber createTrigForm(double absoluteValue, double complexArgument)
	{
		return new ComplexNumber(absoluteValue * Math.cos(complexArgument), 
			absoluteValue * Math.sin(complexArgument));
	}

	public void set(ComplexNumber z)
	{
		x = z.x;
		y = z.y;
	}
	
	public void set(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public double getRealPart()
	{
		return x;
	}
	
	public double getImaginaryPart()
	{
		return y;
	}
	
	public double getAbsoluteValue()
	{
		return Math.sqrt(x*x + y*y);
	}
	
	/**
	 * Returns the complex argument in radians, which is always a number
	 * between <code>0</code> and <code>2PI</code>.
	 */
	public double getComplexArgument()
	{
		if( y == 0.0 )
		{
			if( x >= 0.0 )
				return 0.0;
			else
				return Math.PI;
		}
		if( y > 0.0 )
			return Math.PI * 0.5 - Math.atan(x/y);
		else
			return Math.PI * 1.5 - Math.atan(x/y);
	}
	
	public void add(ComplexNumber z)
	{
		this.x += z.x;
		this.y += z.y;
	}

	public void addTrigForm(double absoluteValue, double complexArgument)
	{
		x += absoluteValue * Math.cos(complexArgument);
		y += absoluteValue * Math.sin(complexArgument);
	}

	public static ComplexNumber sum(ComplexNumber z1, ComplexNumber z2)
	{
		return new ComplexNumber(z1.x + z2.x, z1.y + z2.y);
	}
	
	public void multiply(double zx, double zy)
	{
		double t = x*zx - y*zy;
		y = x*zy + y*zx;
		x = t;
	}

	public void multiply(ComplexNumber z)
	{
		double t = x*z.x - y*z.y;
		y = x*z.y + y*z.x;
		x = t;
	}
	
	public void multiply(double r)
	{
		x *= r;
		y *= r;
	}
	
	public static ComplexNumber product(ComplexNumber z1, ComplexNumber z2)
	{
		return new ComplexNumber(z1.x*z2.x - z1.y*z2.y, z1.x*z2.y + z1.y*z2.x);
	}
	
	public void conjugate()
	{
		y = -y;
	}
	
	public static ComplexNumber conjugate(ComplexNumber c)
	{
		return new ComplexNumber(c.x,-c.y);
	}
	
	public void reciproc()
	{
		double r2 = x*x + y*y;
		x = x/r2;
		y = -y/r2; 
	}
	
	public static ComplexNumber reciproc(ComplexNumber c)
	{
		double r2 = c.x*c.x + c.y*c.y;
		return new ComplexNumber(c.x/r2, -c.y/r2);
	}
	
	public void divide(ComplexNumber c)
	{
		double r2 = c.x*c.x + c.y*c.y;
		multiply(c.x/r2, -c.y/r2);
	}
	
	public void exponent()
	{
		double t = Math.exp(x);
		x = t * Math.cos(y);
		y = t * Math.sin(y);
	}
	
	public static ComplexNumber exponent(ComplexNumber c)
	{
		double t = Math.exp(c.x);
		return new ComplexNumber(t * Math.cos(c.y), t * Math.sin(c.y));
	}

	public static ComplexNumber unitCircle(double argument)
	{
		return new ComplexNumber(Math.cos(argument), Math.sin(argument));
	}
	
	public String toString()
	{
		return Double.toString(x) + " + " + Double.toString(y) + "i";
	}
}
