
package org.mmaroti.math;

import java.awt.*;
import javax.swing.*;
import java.util.*;

class RandomWalk extends Component
{
	private static final long serialVersionUID = 1L;

	Random random = new Random();

	Point currentPoint = new Point(0, 0);
	LinkedList<Point> points = new LinkedList<Point>();	// does not includes currentPoint

	public void step()
	{
		if( ! points.contains(currentPoint) )
			points.add((Point)currentPoint.clone());

		switch( random.nextInt(4) )
		{
		case 0:
			currentPoint.translate(1, 0);
			break;

		case 1:
			currentPoint.translate(0, 1);
			break;

		case 2:
			currentPoint.translate(-1, 0);
			break;

		default:
			currentPoint.translate(0, -1);
		}
	}

	public void paint(Graphics graphics)
	{
		int cx = getWidth() / 2;
		int cy = getHeight() / 2;

		graphics.drawRect(currentPoint.x + cx, currentPoint.y + cy, 1, 1);

		Iterator<Point> iter = points.iterator();
		while( iter.hasNext() )
		{
			Point point = (Point)iter.next();
			graphics.drawRect(point.x + cx, point.y + cy, 1, 1);
		}
	}

	void run()
	{
		for(;;)
		{
			for(int i = 0; i < 100; ++i)
				step();

			Thread.yield();
		}
	}

	public static void main(String[] args) 
	{
		JFrame frame = new JFrame("Random Walk");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		RandomWalk walk = new RandomWalk();
	
		frame.getContentPane().add(walk);
		frame.getContentPane().validate();

		frame.setSize(208, 233);
		frame.setVisible(true);

		walk.run();
	}
}
