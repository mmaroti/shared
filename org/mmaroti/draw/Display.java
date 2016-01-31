/**
 *	Copyright (C) John B. Matthews, Miklos Maroti, 2016
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
import java.awt.event.*;
import javax.swing.*;
import java.util.List;

@SuppressWarnings("serial")
public class Display extends JComponent {
	private static final int WIDE = 640;
	private static final int HIGH = 480;

	private Graph graph = new Graph();
	private ControlPanel control = new ControlPanel();
	private Point mousePt = new Point(WIDE / 2, HIGH / 2);
	private Rectangle mouseRect = new Rectangle();
	private boolean selecting = false;

	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Graph Display");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Display display = new Display();
				frame.add(display.control, BorderLayout.NORTH);
				frame.add(new JScrollPane(display), BorderLayout.CENTER);
				frame.getRootPane().setDefaultButton(
						display.control.defaultButton);
				frame.pack();
				frame.setLocationByPlatform(true);
				frame.setVisible(true);
			}
		});
	}

	public Display() {
		this.setOpaque(true);
		this.addMouseListener(new MouseHandler());
		this.addMouseMotionListener(new MouseMotionHandler());
		ToolTipManager.sharedInstance().registerComponent(this);

		for (int x = 10; x <= 300; x += 10)
			for (int y = 10; y <= 300; y += 10)
				graph.add(new Node(new Point(x, y)));
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDE, HIGH);
	}

	private static Color BACKGROUND_COLOR = new Color(0x00F0F0FF);

	@Override
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, getWidth(), getHeight());

		graph.draw(g);

		if (selecting) {
			g.setColor(Color.DARK_GRAY);
			g.drawRect(mouseRect.x, mouseRect.y, mouseRect.width,
					mouseRect.height);
		}
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		return "hihi " + event.getX() + " " + event.getY();
	}

	private class MouseHandler extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent event) {
			System.out.println("released "
					+ MouseEvent.getModifiersExText(event.getModifiersEx()));

			if (selecting) {
				selecting = false;
				repaint();
			}

			if (event.isPopupTrigger())
				showPopup(event);
		}

		@Override
		public void mousePressed(MouseEvent event) {
			System.out.println("pressed "
					+ MouseEvent.getModifiersExText(event.getModifiersEx()));

			mousePt = event.getPoint(); // drag starts here
			if (event.isPopupTrigger())
				showPopup(event);
		}

		private void showPopup(MouseEvent e) {
			control.popup.show(e.getComponent(), e.getX(), e.getY());
		}

		@Override
		public void mouseClicked(MouseEvent event) {
			System.out.println("clicked "
					+ MouseEvent.getModifiersExText(event.getModifiersEx())
					+ " " + event.getClickCount());

			Point p = event.getPoint();
			if (event.getClickCount() == 1) {
				int m = event.getModifiers();
				if (m == (MouseEvent.BUTTON1_MASK | MouseEvent.SHIFT_MASK))
					graph.toggle(p);
				else if (m == MouseEvent.BUTTON1_MASK) {
					graph.select(p);
					selecting = false;
				}
			}
		}
	}

	private class MouseMotionHandler extends MouseMotionAdapter {
		Point delta = new Point();

		@Override
		public void mouseDragged(MouseEvent event) {
			System.out.println("dragged "
					+ MouseEvent.getModifiersExText(event.getModifiersEx()));

			if (selecting) {
				mouseRect.setBounds(Math.min(mousePt.x, event.getX()),
						Math.min(mousePt.y, event.getY()),
						Math.abs(mousePt.x - event.getX()),
						Math.abs(mousePt.y - event.getY()));
				graph.select(mouseRect);
			} else {
				delta.setLocation(event.getX() - mousePt.x, event.getY()
						- mousePt.y);
				graph.moveSlected(delta);
				mousePt = event.getPoint();
			}
			repaint();
		}
	}

	private class ControlPanel extends JToolBar {
		private Action addNode = new AddNodeAction("Add");
		private Action delete = new DeleteAction("Delete");
		private Action clearAll = new ClearAllAction("Clear");
		private Action connect = new ConnectAction("Connect");

		private JButton defaultButton = new JButton(addNode);
		private JPopupMenu popup = new JPopupMenu();

		ControlPanel() {
			this.setLayout(new FlowLayout(FlowLayout.LEFT));
			this.setBackground(Color.LIGHT_GRAY);
			this.setFloatable(false);

			this.add(defaultButton);
			this.add(new JButton(delete));
			this.add(new JButton(clearAll));
			this.add(new JButton(connect));

			popup.add(new JMenuItem(addNode));
			popup.add(new JMenuItem(delete));
			popup.add(new JMenuItem(connect));
		}
	}

	private class AddNodeAction extends AbstractAction {
		public AddNodeAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			graph.unselectAll();
			Node n = new Node(mousePt.getLocation());
			mousePt.x += 10;
			mousePt.y += 10;
			n.setSelected(true);
			graph.add(n);
			repaint();
		}
	}

	private class ClearAllAction extends AbstractAction {
		public ClearAllAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			graph.clear();
			repaint();
		}
	}

	private class ConnectAction extends AbstractAction {
		public ConnectAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			List<Node> list = graph.getSelected();
			if (list.size() > 1) {
				for (int i = 0; i < list.size() - 1; ++i) {
					Node n1 = list.get(i);
					Node n2 = list.get(i + 1);
					graph.add(new Edge(n1, n2));
				}
			}
			repaint();
		}
	}

	private class DeleteAction extends AbstractAction {
		public DeleteAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			graph.removeSelected();
			repaint();
		}
	}
}
