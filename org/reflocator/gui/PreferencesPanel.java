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

import javax.swing.*;
import javax.swing.GroupLayout.*;
import javax.swing.LayoutStyle.*;
import java.awt.event.*;
import java.io.*;
import java.util.prefs.*;
import org.reflocator.*;

@SuppressWarnings("serial")
public class PreferencesPanel extends JPanel
{
	private Database database = Database.getInstance();

	protected Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
	private static final String PREF_CONNECTION = "connection"; 
	
	private JButton openButton;
	private JButton closeButton;
	private JTextArea connectionField;

	public PreferencesPanel()
	{
		JLabel databaseLabel = new JLabel("Database");
		
		JSeparator databaseSeparator = new JSeparator();

		JLabel connectionLabel = new JLabel("Connection:");
		
		connectionField = new JTextArea(1, 10);
		connectionField.setEditable(false);
		
		openButton = new JButton("Open...");
		openButton.setMnemonic(KeyEvent.VK_O);
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new javax.swing.filechooser.FileFilter()
				{
					public boolean accept(File f)
					{
						return f.isDirectory() || f.getName().endsWith(".h2.db");
					}

					public String getDescription()
					{
						return "H2 Database (.h2.db)";
					}
				});
				chooser.setAcceptAllFileFilterUsed(false);
				int returnVal = chooser.showOpenDialog(PreferencesPanel.this);
				if( returnVal == JFileChooser.APPROVE_OPTION )
				{
					String selectedFile = chooser.getSelectedFile().getAbsolutePath();
					if( ! selectedFile.endsWith(".h2.db") )
						selectedFile += ".h2.db";
						
					database.openDatabase(selectedFile);
					if( database.isOpened() )
						connectionField.setText(selectedFile);
						
					updateDatabaseFlags();
				}
			}
		});

		closeButton = new JButton("Close");
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				database.closeDatabase();

				if( ! database.isOpened() )
					connectionField.setText("");
				
				updateDatabaseFlags();
			}
		});

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.CENTER)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(databaseLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(databaseSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(connectionLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(connectionField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(openButton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(closeButton))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
					.addComponent(databaseLabel)
					.addComponent(databaseSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
					.addComponent(connectionLabel)
					.addComponent(connectionField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
					.addComponent(openButton)
					.addComponent(closeButton))
		);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		setLayout(groupLayout);

		String path = prefs.get(PREF_CONNECTION, null);
		if( path != null &&  ! java.beans.Beans.isDesignTime() )
		{
			if( path.length() > 0 )
			{
				database.openDatabase(path);
				connectionField.setText(database.isOpened() ? path : "");
			}
		}
		
		updateDatabaseFlags();
	}

	public void updateDatabaseFlags()
	{
		if( database.isOpened() )
		{
			openButton.setEnabled(false);
			closeButton.setEnabled(true);
			prefs.put(PREF_CONNECTION, connectionField.getText());
		}
		else
		{
			openButton.setEnabled(true);
			closeButton.setEnabled(false);
			prefs.remove(PREF_CONNECTION);
		}
	}
}
