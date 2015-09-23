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

	public void replaceValue(String column, String oldval, String newval) {
		int id = getColumnId(column);
		for (String[] record : records)
			record[id] = record[id].replace(oldval, newval);
	}

	public void replaceValue(String oldval, String newval) {
		for (int id = 0; id < header.length; id++)
			for (String[] record : records)
				record[id] = record[id].replace(oldval, newval);
	}

	public static String[] append(String[] array, String value) {
		String[] a = new String[array.length + 1];
		System.arraycopy(array, 0, a, 0, array.length);
		a[array.length] = value;
		return a;
	}

	public static String[] insert(String[] array, int pos, String value) {
		String[] a = new String[array.length + 1];
		System.arraycopy(array, 0, a, 0, pos);
		System.arraycopy(array, pos, a, pos + 1, array.length - pos);
		a[pos] = value;
		return a;
	}

	public static String[] remove(String[] array, int pos) {
		assert (array.length > 0);

		String[] a = new String[array.length - 1];
		System.arraycopy(array, 0, a, 0, pos);
		System.arraycopy(array, pos + 1, a, pos, array.length - pos - 1);
		return a;
	}

	public void splitColumn(String column, String newcol, String oldval) {
		int id = getColumnId(column);
		header = insert(header, id + 1, newcol);

		for (int i = 0; i < records.size(); i++) {
			String[] r = records.get(i);
			r = insert(r, id + 1, "");

			if (r[id].equals(oldval)) {
				r[id] = "0";
				r[id + 1] = "1";
			} else if (r[id].length() > 0)
				r[id + 1] = "0";

			records.set(i, r);
		}
	}

	public void removeColumn(String column) {
		int id = getColumnId(column);

		header = remove(header, id);
		for (int i = 0; i < records.size(); i++) {
			String[] r = records.get(i);
			r = remove(r, id);
			records.set(i, r);
		}
	}

	public static void main(String[] args) throws IOException {
		CsvFile file = new CsvFile();
		file.read(';', "/home/mmaroti/shared/gtk.csv");
		file.replaceValue(",", ".");

		for (int i = 1; i <= 32; i++) {
			if (i == 15)
				continue;

			// file.replace("S" + i, "0", "");
			file.splitColumn("S" + i, "S" + i + "n", "0");
		}
		
		file.removeColumn("﻿Ssz");
		file.removeColumn("részsúly");
		file.removeColumn("TEAOR_2");
		file.removeColumn("P5");
		file.removeColumn("P9");
		file.removeColumn("P12");
		file.removeColumn("Emailcím");
		file.removeColumn("megye2");
		file.removeColumn("létszám_kat2");
		file.removeColumn("kor");
		file.removeColumn("vállalk_piaca");
		file.removeColumn("növ_célok");
		file.removeColumn("tervez_merőfelv");
		file.removeColumn("tervez_elbocsátást");
		file.removeColumn("van_tartósan_betöltetlen");
		file.removeColumn("családi_váll");
		file.removeColumn("legmag_isk_végz");
		file.removeColumn("hallotte_szegeden_megepülő_eli");
		file.removeColumn("lesze_hatása_vállalkozására");
		file.removeColumn("egy_millió_befektetése");
		file.removeColumn("megtakarításai_mennyit_tud_vásárolni");
		file.removeColumn("család_felhazsnálná_vállalkozás_vagyonát");
		file.removeColumn("mikor__kezdene_új_beruházásba");
		file.removeColumn("honnan_értesül_pályázati_lehetőségekről");
		file.removeColumn("P9_egyéb");
		file.removeColumn("P9egyéb_név");
		file.removeColumn("P12_egyéb");
		file.removeColumn("P_12egyébnév");
		file.removeColumn("P_5egyéb");
		file.removeColumn("P5_egyéb_név");
		file.removeColumn("súly_azegészhez");
		file.removeColumn("korcsop");

		file.replaceValue("P10", "", "x");
		
		file.write(',', "/home/mmaroti/shared/gtk3.csv");

	}
}
