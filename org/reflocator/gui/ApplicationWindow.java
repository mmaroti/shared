/**
 *	Copyright (C) Miklos Maroti, 2011
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

package org.reflocator.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.prefs.*;
import org.reflocator.*;
import javax.swing.event.*;

@SuppressWarnings("serial")
public class ApplicationWindow extends JFrame
{
	protected Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
	private static final String PREF_SELECTED_TAB = "tab";
	private static final String PREF_WINDOW_WIDTH = "width";
	private static final String PREF_WINDOW_HEIGHT = "height";

	private JTextField textField;
	private JTabbedPane tabbedPane;

	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				try
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
					ApplicationWindow window = new ApplicationWindow();
					window.setVisible(true);
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}
		});
	}

	public ApplicationWindow()
	{
		initialize();
	}

	private void initialize()
	{
		setTitle("Reference Locator");
		
		if( java.beans.Beans.isDesignTime() )
			setBounds(100, 100, 450, 400);
		else
			setBounds(100, 100, prefs.getInt(PREF_WINDOW_WIDTH, 600), prefs.getInt(PREF_WINDOW_HEIGHT, 400));

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(null);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(null);
		tabbedPane.addTab("Preferences", null, scrollPane, null);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_P);
		
		JPanel preferencesPanel = new PreferencesPanel();
		preferencesPanel.setBorder(null);
		scrollPane.setViewportView(preferencesPanel);

		JPanel queriesPanel = new QueriesPanel();
		tabbedPane.addTab("Queries", null, queriesPanel, null);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_Q);
	
		textField = new JTextField();
		textField.setBorder(null);
		getContentPane().add(textField, BorderLayout.SOUTH);
		textField.setColumns(10);

		tabbedPane.setSelectedIndex(prefs.getInt(PREF_SELECTED_TAB, 0));

		// do this last, so we do not overwrite the preferences during construction
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				prefs.putInt(PREF_SELECTED_TAB, tabbedPane.getSelectedIndex());
			}
		});

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent arg0) {
				prefs.putInt(PREF_WINDOW_WIDTH, ApplicationWindow.this.getWidth());
				prefs.putInt(PREF_WINDOW_HEIGHT, ApplicationWindow.this.getHeight());
			}
		});
	}
	
	public void dispose()
	{
		Database.getInstance().closeDatabase();
		super.dispose();
	}
}
