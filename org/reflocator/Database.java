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

package org.reflocator;

import java.sql.*;

public class Database
{
	protected static Database instance = null;

	public static Database getInstance()
	{
		if( instance == null )
			instance = new Database();
		
		return instance;
	}
	
	protected Database()
	{
	}
	
	protected Connection connection = null;
	protected Statement statement = null;

	public void openDatabase(String databaseFile)
	{
		if( connection != null )
			closeDatabase();

		assert( connection == null && statement == null );
		
		final String extension = ".h2.db";
		if( ! databaseFile.endsWith(extension) )
		{
			System.out.println("Bad file extension " + databaseFile);
			return;
		}
		else
			databaseFile = databaseFile.substring(0, databaseFile.length() - extension.length());

		try
		{
			Class.forName("org.h2.Driver");
			connection = DriverManager.getConnection("jdbc:h2:" + databaseFile, "reflocator", "");
			statement = connection.createStatement();

			statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS refloc");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS refloc.queries ("
					+ "id INTEGER AUTO_INCREMENT PRIMARY KEY,"
					+ "service VARCHAR(20),"
					+ "query VARCHAR(100),"
					+ "created DATE,"
					+ "updated DATE)");
			
			System.out.println("database is openend");
		}
		catch(Exception exception)
		{
			System.out.println(exception.getMessage());
			closeDatabase();
		}
	}
	
	public void closeDatabase()
	{
		if( connection != null )
		{
			try
			{
				if( statement != null )
				{
					statement.executeUpdate("DROP SCHEMA IF EXISTS REFLOC");

					statement.close();
				}
				
				connection.close();
				System.out.println("database is closed");
			}
			catch(Exception exception)
			{
				System.out.println(exception.getMessage());
			}
			finally
			{
				statement = null;
				connection = null;
			}
		}
		else
			assert( statement == null );
	}
	
	public boolean isOpened()
	{
		return connection != null;
	}

	protected ResultSet executeQuery(String query) throws SQLException
	{
		ResultSet result = statement.executeQuery(query);
		
		System.out.println(query);

		System.out.print("ROW");
		ResultSetMetaData meta = result.getMetaData();
		int columns = meta.getColumnCount();
		for(int i = 1; i <= columns; ++i)
			System.out.print("\t" + meta.getColumnName(i));
		System.out.println();

		while( result.next() )
		{
			System.out.print(result.getRow());
			for(int i = 1; i <= columns; ++i)
				System.out.print("\t" + result.getObject(i));
			System.out.println();
		}

		System.out.println("END\n");
		
		return result;
	}
	
	protected int executeUpdate(String update) throws SQLException
	{
		System.out.println(update);

		int result = statement.executeUpdate(update);
		System.out.println("result: " + result + "\n");

		return result;
	}
}
