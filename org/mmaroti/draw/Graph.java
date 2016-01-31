/**
 *	Copyright (C) Miklos Maroti, 2016
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

package org.mmaroti.draw;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Graph {
	private List<Node> nodes = new ArrayList<Node>();
	private List<Edge> edges = new ArrayList<Edge>();

	public void draw(Graphics2D g) {
		for (Edge e : edges)
			e.draw(g);

		for (Node n : nodes)
			n.draw(g);
	}

	public void clear() {
		nodes.clear();
		edges.clear();
	}

	public void add(Node node) {
		nodes.add(node);
	}

	public void add(Edge edge) {
		edges.add(edge);
	}

	public void unselectAll() {
		for (Node n : nodes)
			n.setSelected(false);
	}

	public boolean select(Point point) {
		Iterator<Node> i = nodes.iterator();
		while (i.hasNext()) {
			Node n = i.next();
			if (!n.contains(point))
				n.setSelected(false);
			else if (n.contains(point)) {
				n.setSelected(true);
				while (i.hasNext())
					i.next().setSelected(false);
				return true;
			}
		}
		return false;
	}

	public void select(Rectangle rect) {
		for (Node n : nodes)
			n.setSelected(rect.contains(n.getCenter()));
	}

	public void toggle(Point point) {
		for (Node n : nodes) {
			if (n.contains(point))
				n.setSelected(!n.isSelected());
		}
	}

	public List<Node> getSelected() {
		List<Node> list = new ArrayList<Node>();
		for (Node n : nodes)
			if (n.isSelected())
				list.add(n);

		return list;
	}

	public void moveSlected(Point offset) {
		for (Node n : nodes)
			if (n.isSelected())
				n.move(offset);
	}

	public void removeSelected() {
		ListIterator<Edge> i1 = edges.listIterator();
		while (i1.hasNext()) {
			Edge e = i1.next();
			if (e.hasSelectedNode())
				i1.remove();
		}

		ListIterator<Node> i2 = nodes.listIterator();
		while (i1.hasNext()) {
			Node n = i2.next();
			if (n.isSelected())
				i1.remove();
		}
	}
}
