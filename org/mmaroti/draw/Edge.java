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

public class Edge {
	private Node node1;
	private Node node2;

	public Edge(Node node1, Node node2) {
		this.node1 = node1;
		this.node2 = node2;
	}

	public boolean hasSelectedNode() {
		return node1.isSelected() || node2.isSelected();
	}

	public void draw(Graphics2D graphics) {
		Point p1 = node1.getCenter();
		Point p2 = node2.getCenter();
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.drawLine(p1.x, p1.y, p2.x, p2.y);
	}
}
