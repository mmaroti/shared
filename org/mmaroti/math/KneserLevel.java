package org.mmaroti.math;

public class KneserLevel
{
	public int size;
	public int level;

	public int points[];
	private int count;
	
	public KneserLevel(int size, int level)
	{
		if( size < 0 || level < 0 || level > size )
			throw new IllegalArgumentException();
			
		this.size = size;
		this.level = level;
		
		long a = 1;
		long b = 1;
		
		for(int i = 1; i <= level; ++i)
		{
			a *= (size + 1 - i);
			b *= i;
		}
		
		int c = (int)(a / b);
		points = new int[c];
		
		addPoints(0, 0, -1);
	}
	
	public String pointToString(int point)
	{
		StringBuffer s = new StringBuffer(size);

		for(int i = 0; i < size; ++i)
			s.append((point & (1<<i)) != 0 ? '1' : '0');
		
		return s.toString();
	}
	
	private void addPoints(int point, int elements, int last)
	{
		if( elements == level )
			points[count++] = point;
		else
		{
			int end = size - (level - elements);
			++elements;

			while( ++last <= end )
				addPoints(point | (1 << last), elements, last);
		}
	}
	
	public static void main(String[] args)
	{
		KneserLevel level = new KneserLevel(9*2 + 5, 9);

//		for(int i = 0; i < level.points.length; ++i)
//			System.out.println(level.pointToString(level.points[i]));
		
		System.out.println(level.points.length + " points");
	}
}
