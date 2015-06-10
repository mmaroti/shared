/**
 *	Copyright (C) Miklos Maroti, 2015
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

package org.mmaroti.stat;

import java.util.*;
import java.io.*;

public class CsvFile {
	public String[] header;
	public ArrayList<String[]> records = new ArrayList<String[]>();

	public void clear() {
		header = null;
		records.clear();
	}

	public void read(char sep, InputStream stream) throws IOException {
		assert sep == ',' || sep == ';';
		String sep2 = "" + sep + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

		clear();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				stream, "UTF-8"));

		String line = reader.readLine();
		if (line == null)
			throw new IllegalArgumentException("No header record");
		header = line.split(sep2);

		while ((line = reader.readLine()) != null) {
			String[] record = line.split(sep2);

			if (record.length != header.length)
				throw new IllegalArgumentException(
						"Record length mismatch at line "
								+ (records.size() + 2));

			for (int i = 0; i < record.length; i++) {
				String s = record[i].trim();
				if (s.startsWith("\"") && s.endsWith("\""))
					s = s.substring(1, s.length() - 1);

				if (s.contains("\"") || s.contains("\n"))
					throw new IllegalStateException(
							"Fields cannot contain quote and newline characters");

				record[i] = s;
			}

			records.add(record);
		}

		System.out.println("Read " + records.size() + " records with "
				+ header.length + " columns");
	}

	public void read(char sep) throws IOException {
		read(sep, System.in);
	}

	public void read(char sep, String filename) throws IOException {
		InputStream stream = new FileInputStream(filename);
		try {
			read(sep, stream);
		} finally {
			stream.close();
		}
	}

	public void write(char sep, OutputStream stream) throws IOException {
		assert sep == ',' || sep == ';';
		String sep2 = "" + sep;

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				stream, "UTF-8"));

		if (header == null)
			throw new IllegalStateException("No header record");

		String[] record = header;
		int index = 0;

		for (;;) {
			boolean c = false;
			for (String s : record) {

				if (s.contains("\"") || s.contains("\n"))
					throw new IllegalStateException(
							"Fields cannot contain quote and newline characters");

				if (c)
					writer.append(sep);
				else
					c = true;

				boolean b = s.contains(sep2);
				if (b)
					writer.append('"');

				writer.append(s);

				if (b)
					writer.append('"');
			}
			writer.newLine();

			if (index < records.size())
				record = records.get(index++);
			else
				break;
		}

		writer.flush();

		System.out.println("Written " + records.size() + " records with "
				+ header.length + " columns");
	}

	public void write(char sep) throws IOException {
		write(sep, System.out);
	}

	public void write(char sep, String filename) throws IOException {
		OutputStream stream = new FileOutputStream(filename);
		try {
			write(sep, stream);
		} finally {
			stream.close();
		}
	}

	public int getColumnId(String column) {
		for (int i = 0; i < header.length; i++)
			if (header[i].equals(column))
				return i;

		throw new IllegalArgumentException("Column not found: " + column);
	}

	public void replace(String column, String old, String value) {
		int id = getColumnId(column);
		for (String[] record : records)
			record[id] = record[id].replace(old, value);
	}

	public void replace(String old, String value) {
		for (int id = 0; id < header.length; id++)
			for (String[] record : records)
				record[id] = record[id].replace(old, value);
	}

	public static void main(String[] args) throws IOException {
		CsvFile file = new CsvFile();
		file.read(';', "/home/mmaroti/shared/gtk.csv");
		file.replace(",", ".");

		for (int i = 1; i <= 32; i++) {
			if (i == 15)
				continue;
			file.replace("S" + i, "0", "");
		}
		
		file.replace("A5", "Nem tudom", "");
		file.replace("A7", "Nem tudom", "");
		file.replace("A9", "Nem tudom", "");
		file.replace("A19", "Nem tudom", "");

		file.write(',', "/home/mmaroti/shared/gtk3.csv");
	}
}
