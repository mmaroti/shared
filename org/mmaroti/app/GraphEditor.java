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

package org.mmaroti.app;

import java.util.List;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import org.mmaroti.ua.math.*;

public class GraphEditor extends java.awt.Canvas
{
	Frame frame; 

	public static class Vertex
	{
		int x;
		int y;
		String name;
		
		Point getPoint()
		{
			return new Point(x,y);
		}
		
		Vertex(int x, int y, String name)
		{
			this.x = x;
			this.y = y;
			this.name = name;
		}
	}
	
	List<Vertex> vertices = new ArrayList<Vertex>();

	public static class Edge
	{	
		Vertex source;
		Vertex target;
		
		Edge(Vertex s, Vertex t)
		{
			source = s;
			target = t;
		}
	}

	List<Edge> edges = new ArrayList<Edge>();
	
	static final int VERTEX_RADIUS = 7;
	static final int SELECTION_RADIUS = VERTEX_RADIUS;
	static final int TEXT_OFFSET_X = -3;
	static final int TEXT_OFFSET_Y = 4;
	static final int LINE_CLEARENCE = VERTEX_RADIUS + 1;
	static final int ARROW_LENGTH = 10;
	static final int ARROW_WIDTH = 3;
	
	public void paint(Graphics graphics)
	{
		graphics.setColor(Color.BLACK);

		Iterator<Vertex> iter1 = vertices.iterator();
		while( iter1.hasNext() )
		{
			Vertex vertex = iter1.next();
			Point point = toScreen(vertex.getPoint());
			
			graphics.drawOval(point.x - VERTEX_RADIUS, point.y - VERTEX_RADIUS, 
				2*VERTEX_RADIUS, 2*VERTEX_RADIUS);
			
			graphics.drawString(vertex.name, point.x + TEXT_OFFSET_X, point.y + TEXT_OFFSET_Y);
		}
		
		Iterator<Edge> iter2 = edges.iterator();
		while( iter2.hasNext() )
		{
			Edge edge = iter2.next();
			Point point1 = toScreen(edge.source.getPoint());
			Point point2 = toScreen(edge.target.getPoint());
			
			double d = point1.distance(point2);
			if( d < 1.0 )
				d = 1.0;

			double nx = (point2.x - point1.x) / d;
			double ny = (point2.y - point1.y) / d;
			
			Point s1 = new Point(
					(int)(point1.x + LINE_CLEARENCE * nx),
					(int)(point1.y + LINE_CLEARENCE * ny));

			Point e1 = new Point(
					(int)(point2.x - LINE_CLEARENCE * nx),
					(int)(point2.y - LINE_CLEARENCE * ny));

			Point e2 = new Point(
					(int)(point2.x - (LINE_CLEARENCE + ARROW_LENGTH) * nx + ARROW_WIDTH * ny), 
					(int)(point2.y - (LINE_CLEARENCE + ARROW_LENGTH) * ny - ARROW_WIDTH * nx));

			Point e3 = new Point(
					(int)(point2.x - (LINE_CLEARENCE + ARROW_LENGTH) * nx - ARROW_WIDTH * ny), 
					(int)(point2.y - (LINE_CLEARENCE + ARROW_LENGTH) * ny + ARROW_WIDTH * nx));

			graphics.drawLine(s1.x, s1.y, e1.x, e1.y);
			graphics.fillPolygon(new int[] { e1.x, e2.x, e3.x }, new int[] { e1.y, e2.y, e3.y }, 3);
		}
	}
	
	public Vertex selectedVertex;
	
	public class MouseEventHandler extends MouseAdapter
	{
		public void mousePressed(MouseEvent event)
		{
			Point click = event.getPoint();
			
			Iterator<Vertex> iter = vertices.iterator();
			while( iter.hasNext() )
			{
				Vertex vertex = iter.next();
				Point point = toScreen(new Point(vertex.x, vertex.y));
				
				if( point.distance(click) <= SELECTION_RADIUS )
					selectedVertex = vertex;
			}
		}
		
		public void mouseReleased(MouseEvent event)
		{
			if( selectedVertex == null )
				return;
			
			Point point = toCoord(event.getPoint());
			selectedVertex.x = point.x;
			selectedVertex.y = point.y;

			repaint();
			
			selectedVertex = null;
		}
	}
	
	Point toScreen(Point point)
	{
		return new Point(point.x, point.y);
	}

	Point toCoord(Point point)
	{
		return new Point(point.x, point.y);
	}

	void createGraph()
	{
//		Graph graph = new Graph('x' - 'a' + 1);
//		String edgeString = "ab ae ai ak an ao ap aq aw bg bm bo ce cg ck cr cs da dc de dg dh dj dk dm dn dq dr ds dt dv dx ek eo ew fb fc fg fi fm fo fs ft fu fw fx gk ha hb he hf hi hk hl hn ho hp hq hr hu hv hw ib ig im io is iw ix ja je jg jk jm jn jq js jx ko lb lc ld lf lg li lj lm lo lp ls lt lu lw lx mg mk mq nb ne ni nk no nq nw og pb pg pi pj pm po ps pw px qb qk qo re rk ro ru rw se sg sk tc te tg tk tm tn tq tr ts tv tx uc ug uo us uw vb ve vf vi vk vn vo vq vr vu vw wg wo ws xe xg xk xm xn xq xs";

		Graph graph = new Graph('r' - 'a' + 1);
		String edgeString = "ae ag ak am be bi bm bn cg ci cm cp da db de dg di dk dl dm dn do dp dr em fa fc fe fg fi fk fl fm fn fp fq fr gm hb hc he hg hi hk hm hn ho hp hq hr im ja jb jc jd je jf jg jh ji jk jl jm jn jo jp jq jr ke kg km la le lg li lk lm ln lp lr ne ni nm ob oe og oi ok om on op or pg pi pm qc qe qg qi qk qm qn qp qr re rg ri rk rm rn rp";

		for(int i = 0; 3*i < edgeString.length(); ++i)
		{
			int a = edgeString.charAt(3*i) - 'a';
			int b = edgeString.charAt(3*i + 1) - 'a';
			graph.setEdge(a, b, true);
		}

		Graph closure = graph.transitiveColsure();
		
		for(int i = 0; i < graph.getSize(); ++i)
		{
			vertices.add(new Vertex(100 + 20 * i, 
					100, new String(new char[]{(char)('a' + i)})));
		}
		
		for(int i = 0; i < graph.getSize(); ++i)
			for(int j = 0; j < graph.getSize(); ++j)
			{
				if( i == j || ! graph.hasEdge(i, j))
					continue;

				if( closure.isSameBlock(i, j) || closure.isCoverBlock(i, j))
					edges.add(new Edge(vertices.get(i),vertices.get(j)));
			}
	}
	
	public GraphEditor()
	{
		createGraph();
		
		frame = new Frame();
		frame.add(this);
		frame.setTitle("Graph editor");
		
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

		addMouseListener(new MouseEventHandler());

		setSize(800, 500);
		frame.pack();
		frame.setVisible(true);
	}

	static final long serialVersionUID = 1;

	public static void main(String[] args)
	{
		new GraphEditor();
	}
}
