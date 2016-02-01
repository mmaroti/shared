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

public class Node {
	private Point center;
	private int radius = 4;
	private boolean selected = false;

	public Node(Point center) {
		this.center = center;
	}

	public void draw(Graphics2D gaphics) {
		gaphics.setColor(Color.BLACK);
		gaphics.fillOval(center.x - radius, center.y - radius, 2 * radius + 1,
				2 * radius + 1);

		if (selected) {
			gaphics.setColor(Color.RED);
			gaphics.drawOval(center.x - radius - 3, center.y - radius - 3,
					2 * radius + 6, 2 * radius + 6);
		}
	}

	public Point getCenter() {
		return center;
	}

	public void move(Point offset) {
		center.x += offset.x;
		center.y += offset.y;
	}

	public boolean contains(Point point) {
		return point.distance(center) <= radius + 1;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
