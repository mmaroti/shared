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
import javax.swing.border.*;
import java.awt.*;

@SuppressWarnings("serial")
public class QueriesPanel extends JPanel
{
	private JTable table;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField txtHihi;
	
	public QueriesPanel()
	{
		setBorder(null);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JSplitPane splitPane = new JSplitPane();
		splitPane.setBorder(null);
		splitPane.setResizeWeight(1.0);
		add(splitPane);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(new LineBorder(new Color(0, 0, 0), 0));
		splitPane.setLeftComponent(scrollPane);

		String[] columns = { "Serivce", "Query", "Updated" };
		Object[][] data = {
				{ "scholar", "author:maroti m, date=1990", "2011-01-30" },	
				{ "scholar", "author:maroti m, date=1990", "2011-02-30" },	
				{ "scholar", "author:maroti m, date=1990", "2011-03-30" },	
				{ "scholar", "author:maroti m, date=1990", "2011-04-30" },	
				{ "scholar", "author:maroti m, date=1990", "2011-05-30" },	
				{ "scholar", "author:maroti m, date=1990", "2011-06-30" },	
				{ "scholar", "author:maroti m, date=1990", "2011-07-30" },	
				{ "scholar", "author:maroti m, date=1990", "2011-08-30" },	
		};
		
		table = new JTable(data, columns);
		scrollPane.setViewportView(table);
		
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel fieldsPanel = new JPanel();
		fieldsPanel.setBorder(new TitledBorder(null, "Query details", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(fieldsPanel);
		GridBagLayout gbl_fieldsPanel = new GridBagLayout();
		gbl_fieldsPanel.columnWidths = new int[]{0, 0, 0};
		gbl_fieldsPanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_fieldsPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_fieldsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		fieldsPanel.setLayout(gbl_fieldsPanel);
		
		JLabel lblService = new JLabel("Service");
		GridBagConstraints gbc_lblService = new GridBagConstraints();
		gbc_lblService.insets = new Insets(0, 0, 5, 5);
		gbc_lblService.anchor = GridBagConstraints.EAST;
		gbc_lblService.gridx = 0;
		gbc_lblService.gridy = 0;
		fieldsPanel.add(lblService, gbc_lblService);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		fieldsPanel.add(textField, gbc_textField);
		textField.setColumns(10);
		
		JLabel lblQuery = new JLabel("Query");
		GridBagConstraints gbc_lblQuery = new GridBagConstraints();
		gbc_lblQuery.anchor = GridBagConstraints.EAST;
		gbc_lblQuery.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuery.gridx = 0;
		gbc_lblQuery.gridy = 1;
		fieldsPanel.add(lblQuery, gbc_lblQuery);
		
		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 1;
		fieldsPanel.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);
		
		JLabel lblCreated = new JLabel("Created");
		GridBagConstraints gbc_lblCreated = new GridBagConstraints();
		gbc_lblCreated.anchor = GridBagConstraints.EAST;
		gbc_lblCreated.insets = new Insets(0, 0, 5, 5);
		gbc_lblCreated.gridx = 0;
		gbc_lblCreated.gridy = 2;
		fieldsPanel.add(lblCreated, gbc_lblCreated);
		
		txtHihi = new JTextField();
		txtHihi.setEditable(false);
		txtHihi.setText("hihi");
		GridBagConstraints gbc_txtHihi = new GridBagConstraints();
		gbc_txtHihi.insets = new Insets(0, 0, 5, 5);
		gbc_txtHihi.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtHihi.gridx = 1;
		gbc_txtHihi.gridy = 2;
		fieldsPanel.add(txtHihi, gbc_txtHihi);
		txtHihi.setColumns(10);
		
		JPanel buttonsPanel = new JPanel();
		panel.add(buttonsPanel);
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnUpdate = new JButton("Update");
		buttonsPanel.add(btnUpdate);
		
		JButton btnDelete = new JButton("Delete");
		buttonsPanel.add(btnDelete);
		
		JButton btnCreate = new JButton("Create");
		buttonsPanel.add(btnCreate);
	}
}
