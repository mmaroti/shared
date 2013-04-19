/*
 * Created on December 2, 2006
 * (C)opyright
 */
package org.mmaroti.rips.mp;

import java.util.*;
import java.awt.*;
import java.awt.color.*;
import java.awt.event.*;

import org.mmaroti.rips.*;

/**
 * @author mmaroti
 */
public class Canvas extends java.awt.Canvas
{
	/**
	 *  just to make java 5 happy
	 */ 
	static final long serialVersionUID = 0; 
	
	public Room room;
	
	/**
	 * The size of one evaluation (a rectangle) in pixels
	 */
	public int resolution = 3;

	/**
	 * Which graph should be displayed:
	 * 	r	rips
	 * 	m	master rssi
	 * 	a	assistant rssi
	 */
	public char displaySelection = 'r';

	public void setDefaultCenter()
	{
		center.x = 0.5 * (room.east.coord + room.west.coord);
		center.y = 0.5 * (room.south.coord + room.north.coord);
		center.z = 0.5 * (room.bottom.coord + room.top.coord);
		
		double d1 = room.east.coord - room.west.coord;
		double d2 = room.north.coord - room.south.coord;
		
		diagonal = Math.round(10.0 * Math.sqrt(d1*d1 + d2*d2)) * 0.1;
	}
	
	public Canvas(Room environment)
	{
		room = environment;
		
		ColorSpace space = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		float[] comp = new float[1];

		colors = new Color[256];
		for(int i = 0; i < colors.length; ++i)
		{
			comp[0] = ((float)i) / colors.length;
			colors[i] = new Color(space, comp, 1);
		}
	
		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent event)
			{
				center = getPosition(event.getPoint());
				repaint();				
			}
		});

		setFocusable(true);

		addKeyListener(new KeyAdapter()
		{
			public void keyTyped(KeyEvent event)
			{
				if( event.getKeyChar() == '+' )
					diagonal /= 1.5;
				else if( event.getKeyChar() == '-' )
					diagonal *= 1.5;
				else if( event.getKeyChar() == '<' )
					center.z -= 0.1;
				else if( event.getKeyChar() == '>' )
					center.z += 0.1;
				else if( event.getKeyChar() == '[' )
				{
					room.master.setFrequency(room.master.getFrequency() - 1000000);
					room.assistant.setFrequency(room.assistant.getFrequency() - 1000000);
				}
				else if( event.getKeyChar() == ']' )
				{
					room.master.setFrequency(room.master.getFrequency() + 1000000);
					room.assistant.setFrequency(room.assistant.getFrequency() + 1000000);
				}
				else if( event.getKeyChar() == 'h' )
					resolution = 1;
				else if( event.getKeyChar() == 'l' )
					resolution = 3;
				else if( event.getKeyChar() == 'r' )
					displaySelection = 'r';
				else if( event.getKeyChar() == 'm' )
					displaySelection = 'm';
				else if( event.getKeyChar() == 'n' )
					displaySelection = 'n';
				else if( event.getKeyChar() == 'b' )
					displaySelection = 'b';
				else if( event.getKeyChar() == 'a' )
					displaySelection = 'a';
				else if( event.getKeyChar() >= '0' && event.getKeyChar() <= '9' )
					Canvas.this.room.loadSetup(event.getKeyChar() - '0');
				else
					return;

				repaint();
			}
		});
	}

	/**
	 * Contains a greyscale color scale
	 */	
	public Color[] colors;
	
	/**
	 * The dimension of the screen
	 */
	public Dimension dimension;

	/**
	 * Contains the location corresponding to the center
	 */	
	public Position center = new Position(0.0, 0.0, 0.0);
	
	/**
	 * The length of the diagonal in meters
	 */
	public double diagonal = 40.0;

	/**
	 * Converts a real location to a screen coordinate
	 */
	public Point getPoint(Position position)
	{
		double screen = Math.sqrt(dimension.width * dimension.width + dimension.height * dimension.height); 

		Point point = new Point();
		point.x = (int)((position.x - center.x) * screen / diagonal + 0.5 * dimension.width);
		point.y = (int)((center.y - position.y) * screen / diagonal + 0.5 * dimension.height);
		
		return point;
	}
	
	/**
	 * Converts a screen coordinate to a real location, 
	 */
	public Position getPosition(Point point)
	{
		double screen = Math.sqrt(dimension.width * dimension.width + dimension.height * dimension.height); 

		Position position = new Position();
		position.x = (point.x - 0.5 * dimension.width) * diagonal / screen + center.x;
		position.y = center.y - (point.y - 0.5 * dimension.height) * diagonal / screen;
		position.z = center.z;
		
		return position;
	}

	public Color getRipsColor(ComplexNumber signal)
	{
		double rssiDiff = RadioModel.getStrength(signal.getAbsoluteValue());
		if( rssiDiff < -RadioModel.RIPS_MAX_DB || rssiDiff > RadioModel.RIPS_MAX_DB )
			return Color.GREEN;
		else
			return colors[(int)(colors.length / RadioModel.TWO_PI * signal.getComplexArgument())];
	}

	public void paintRips(Graphics graphics)
	{
		Point point = new Point();
		for(point.x = 0; point.x < dimension.width; point.x += resolution)
			for(point.y = 0; point.y < dimension.height; point.y += resolution)
			{
				Position pos = getPosition(point);
				ComplexNumber signal = room.getInterferenceSignal(pos);
				graphics.setColor(getRipsColor(signal));
				graphics.fillRect(point.x, point.y, resolution, resolution);
			}
	}

	public void paintNormalVector(Graphics graphics)
	{
		graphics.setColor(Color.BLACK);
		
		Point point = new Point();
		for(point.x = 0; point.x < dimension.width; point.x += 3 * resolution)
			for(point.y = 0; point.y < dimension.height; point.y += 3 * resolution)
			{
				Position pos = getPosition(point);
				ComplexNumber signal = room.getInterferenceVector(pos);
				graphics.drawLine(point.x, point.y, point.x + (int)(5 * signal.y), point.y + (int)(5 * signal.x));
			}
	}

	public void paintAveragedNormalVector(Graphics graphics)
	{
		graphics.setColor(Color.BLACK);
		
		Point point = new Point();
		for(point.x = 0; point.x < dimension.width; point.x += 3 * resolution)
			for(point.y = 0; point.y < dimension.height; point.y += 3 * resolution)
			{
				Position pos = getPosition(point);
				ComplexNumber signal = room.getAveragedInterferenceVector(pos);
				graphics.drawLine(point.x, point.y, point.x + (int)(5 * signal.y), point.y + (int)(5 * signal.x));
			}
	}

	public void paintComponents(Graphics graphics)
	{
		graphics.setColor(Color.RED);
		
		Iterator iter = room.components.iterator();
		while( iter.hasNext() )
		{
			PathComponent component = (PathComponent)iter.next();
			Point pos = getPoint(component.position);
			
//			graphics.fillOval(pos.x-3 , pos.y-3, 7, 7);
			graphics.fillRect(pos.x-1 , pos.y-9, 3, 19);
			graphics.fillRect(pos.x-9 , pos.y-1, 19, 3);
		}
	}

	public static final double MIN_RSSI = -30.0;
	public static final double MAX_RSSI = 1.0;

	public Color getRssiColor(ComplexNumber signal)
	{
		double rssi = RadioModel.getStrength(signal.getAbsoluteValue());
		
		int color = colors.length - (int)(colors.length * (rssi - MIN_RSSI) / (MAX_RSSI - MIN_RSSI));
		if( color < 0 )
			color = 0;
		if( color >= colors.length )
			color = colors.length - 1;
		
		return colors[color];
	}

	HashMap rssiResults = new HashMap();

	public void paintRssi(Graphics graphics, boolean master)
	{
		Point point = new Point();
		for(point.x = 0; point.x < dimension.width; point.x += resolution)
			for(point.y = 0; point.y < dimension.height; point.y += resolution)
			{
				Position pos = getPosition(point);
				ComplexNumber signal = master ? room.getMasterSignal(pos) : room.getAssistantSignal(pos);
				graphics.setColor(getRssiColor(signal));
				graphics.fillRect(point.x, point.y, resolution, resolution);
			}
	}

	public void paintRoom(Graphics graphics)
	{
		graphics.setColor(Color.BLACK);

		Point p1 = getPoint(new Position(room.west.coord, room.south.coord, room.bottom.coord));
		Point p2 = getPoint(new Position(room.east.coord, room.north.coord, room.top.coord));
		
		graphics.drawRect(p1.x, p2.y, p2.x - p1.x, p1.y - p2.y);
	}

	public void paint(Graphics graphics)
	{
		dimension = getSize();

		frame.setTitle("RIPS Simulator [" + room.identifier 
				+ "] center=" + center 
				+ ", diag=" + (Math.round(diagonal * 100.0) * 0.01) 
				+ ", res=" + resolution 
				+ ", freq=" + Math.round(room.master.getFrequency()/1000000) + "MHz, "
				+ (displaySelection == 'r' ? "rips" : 
					displaySelection == 'm' ? "master" : 
					displaySelection == 'a' ? "assistant" :
					displaySelection == 'n' ? "normal" :
					displaySelection == 'b' ? "averaged normal" :
					"unknown"));
		
		if( displaySelection == 'r' )
			paintRips(graphics);
		else if( displaySelection == 'm' )
			paintRssi(graphics, true);
		else if( displaySelection == 'a' )
			paintRssi(graphics, false);
		else if( displaySelection == 'n' )
			paintNormalVector(graphics);
		else if( displaySelection == 'b' )
			paintAveragedNormalVector(graphics);
		
		paintComponents(graphics);
		paintRoom(graphics);
	}
	
	public Frame frame = null; 

	public void display()
	{
		if( frame != null )
			return; 

		setSize(100*(int)(room.east.coord - room.west.coord), 
				100*(int)(room.north.coord - room.south.coord));
			
		frame = new Frame();
		frame.add(this);
		frame.setTitle("RIPS simulator");
		
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				frame.dispose();
			}
		});
		
		addKeyListener(new KeyAdapter()
		{
			public void keyTyped(KeyEvent event)
			{
				if( event.getKeyChar() == 'q' )
					frame.dispose();
			}
		});

		frame.pack();
		frame.setVisible(true);
	}
}
