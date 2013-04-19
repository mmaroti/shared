/**
 * Copyright (C) Miklos Maroti, 2011
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

package org.mmaroti.cites;

import java.util.*;
import java.io.*;
import jxl.*;
import jxl.write.*;

public class Database
{
	List<Table> tables = new ArrayList<Table>();
	
	public Table getTable(String name)
	{
		for(Table table : tables)
		{
			if( name.equals(table.getName()) )
				return table;
		}
		
		throw new IllegalArgumentException("Table not found");
	};
	
	public void addTable(Table table)
	{
		tables.add(table);
	}

	public void print()
	{
		for(Table table : tables)
			table.print();
	}

	void loadExcel(String filename) throws Exception
	{
		tables.clear();
		
		Workbook workbook = Workbook.getWorkbook(new File(filename));
		for(Sheet sheet : workbook.getSheets())
		{
			Cell[] cells = sheet.getRow(0);
			
			String[] labels = new String[cells.length];
			for(int i = 0; i < labels.length; ++i)
				labels[i] = cells[i].getContents();

			Table table = new Table(sheet.getName(), labels);

			if( cells.length > 0 )
			{
				for(int r = 1; r < sheet.getRows(); ++r)
				{
					Table.Record record = table.createRecord();
			
					for(int i = 0; i < table.labels.length; ++i)
						record.values[i] = sheet.getCell(i, r).getContents();
					
					table.addRecord(record);
				}
			}

			addTable(table);
		}
		workbook.close();
	}

	void saveExcel(String filename) throws Exception
	{
		File file = new File(filename);
		Workbook original = null;
		WritableWorkbook workbook;

		if( file.exists() )
		{
			original = Workbook.getWorkbook(file);
			workbook = Workbook.createWorkbook(file, original);
			original.close();
		}
		else
			workbook = Workbook.createWorkbook(file);
		
		for(int i = 0; i < tables.size(); ++i)
		{
			Table table = tables.get(i);
			WritableSheet sheet;
			
			sheet = workbook.getSheet(table.getName());
			if( sheet == null )
				sheet = workbook.createSheet(table.getName(), workbook.getSheets().length);
				
			while( sheet.getRows() >= 2 )
				sheet.removeRow(1);

			while( sheet.getColumns() > 0 && sheet.getCell(0, 0).getContents().length() == 0 )
				sheet.removeColumn(0);

			String[] labels = table.getLabels();
			int[] indices = new int[labels.length];
			for(int j = 0; j < labels.length; ++j)
			{
				int k;
				for(k = 0; k < sheet.getColumns(); ++k)
					if( labels[j].equals(sheet.getCell(k, 0).getContents()) )
						break;
				
				if( k == sheet.getColumns() )
					sheet.addCell(new Label(k, 0, labels[j]));
				
				indices[j] = k;
			}
			
			List<Table.Record> records = table.getRecords();
			for(int k = 0; k < records.size(); ++k)
			{
				Table.Record record = records.get(k);
				
				for(int j = 0; j < labels.length; ++j)
					sheet.addCell(new Label(indices[j], k+1, record.getValue(j)));
			}
		}
		
		workbook.write();
		workbook.close();
	}
}
