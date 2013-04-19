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

package org.mmaroti.cites4;

import java.util.*;
import java.io.*;
import jxl.*;
import jxl.write.*;

/**
 * A database backed by an Microsoft Excel file. Each sheet contains a
 * table. The first row in the sheet defines the column (key) names.
 * The subsequent rows are the values. You can format and rearrange the
 * columns, those formating will not be lost when you save your data.
 */
public class ExcelDatabase
{
	String filename;
	List<MemoryTable> tables = new ArrayList<MemoryTable>();

	public void open(String filename) throws Exception
	{
		this.filename = null;
		tables.clear();
		
		Workbook workbook = Workbook.getWorkbook(new File(filename));

		for(Sheet sheet : workbook.getSheets())
		{
			Cell[] cells = sheet.getRow(0);
			String[] keys = new String[cells.length];
			for(int i = 0; i < keys.length; ++i)
				keys[i] = cells[i].getContents();

			Table table = addTable(sheet.getName(), keys);

			if( cells.length > 0 )
			{
				for(int j = 1; j < sheet.getRows(); ++j)
				{
					Record record = table.createRecord();
			
					for(int i = 0; i < keys.length; ++i)
						record.setValue(keys[i], sheet.getCell(i, j).getContents());
					
					table.addRecord(record);
				}
			}
		}

		workbook.close();
		this.filename = filename;
	}

	public void save() throws Exception
	{
		if( filename != null )
			save(filename);
		
		filename = null;
	}
	
	public void save(String filename) throws Exception
	{
		File file = new File(filename);
		Workbook original = null;
		WritableWorkbook workbook;

		if( file.exists() )
		{
			File backup = new File(filename + ".bak");
			
			if( backup.exists() && ! backup.delete() )
				throw new IOException("Could not delete file: " + backup.getName());
			
			if( ! file.renameTo(backup) )
				throw new IOException("Could not rename file: " + file.getName());

			original = Workbook.getWorkbook(backup);
			workbook = Workbook.createWorkbook(file, original);
			original.close();
		}
		else
			workbook = Workbook.createWorkbook(file);
		
		for(Table table : tables)
		{
			System.out.println("Saving " + table.getName() + " with " + table.getSize() + " records");
			
			WritableSheet sheet;
			
			sheet = workbook.getSheet(table.getName());
			if( sheet == null )
				sheet = workbook.createSheet(table.getName(), workbook.getSheets().length);
				
			while( sheet.getRows() >= 2 )
				sheet.removeRow(1);
			
			String[] keys = table.getKeys();
			int[] indices = new int[keys.length];
			for(int j = 0; j < keys.length; ++j)
			{
				int i;
				for(i = 0; i < sheet.getColumns(); ++i)
					if( keys[j].equals(sheet.getCell(i, 0).getContents()) )
						break;
				
				if( i == sheet.getColumns() )
					sheet.addCell(new Label(i, 0, keys[j]));
				
				indices[j] = i;
			}
			
			Iterator<Record> iterator = table.iterator();
			int j = 1;
			while( iterator.hasNext() )
			{
				Record record = iterator.next();

				for(int i = 0; i < keys.length; ++i)
					sheet.addCell(new Label(indices[i], j, record.getValue(keys[i])));
				
				++j;
			}
		}
		
		workbook.write();
		workbook.close();
	}

	public void print()
	{
		for(MemoryTable table : tables)
			table.print();
	}

	public Collection<Table> getTables()
	{
		Collection<Table> collection = new ArrayList<Table>();
		collection.addAll(tables);
		return collection;
	}
	
	public Table getTable(String name)
	{
		for(MemoryTable table : tables)
		{
			if( name.equals(table.getName()) )
				return table;
		}

		throw new IllegalArgumentException("Table not found");
	};
	
	public Table addTable(String name, String[] keys)
	{
		MemoryTable table = new MemoryTable(name, keys);
		tables.add(table);

		return table;
	}
	
	public void removeTable(String name)
	{
		tables.remove(getTable(name));
	}
}
