/**
 *	Copyright (C) Miklos Maroti, 2003-2006
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

package org.mmaroti.stocks;

import java.text.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class Histogram {

	public static class Robot {
		static final double COMMISSION = 8.0;

		double money = 10000.0;
		double stocks;
		double commissions = 0;

		void print(String prefix) {
			prefix += "\t" + DECIMAL_FORMAT.format(stocks);
		}

		void buy(Sale sale) {
			double price = (sale.low + sale.high) / 2.0;
			stocks += money / price;
			money = 0;
			commissions += COMMISSION;

			String s = DATE_FORMAT.format(new Date(sale.time * 1000));
			s += " buying at " + DECIMAL_FORMAT.format(price);
			s += "\t" + DECIMAL_FORMAT.format(stocks * price - commissions);
			System.out.println(s);
		}

		void sell(Sale sale) {
			double price = (sale.low + sale.high) / 2.0;
			money += stocks * price;
			stocks = 0;
			commissions += COMMISSION;

			String s = DATE_FORMAT.format(new Date(sale.time * 1000));
			s += " selling at " + DECIMAL_FORMAT.format(price);
			s += "\t" + DECIMAL_FORMAT.format(money - commissions);
			System.out.println(s);
		}

		final static int NOTHING = 0;
		final static int HOLDING = 1;
		int state;

		void process(Sale sale) {
			double price = (sale.low + sale.high) / 2.0;

			double modulo = price - 10.0 * (int) (price / 10.0);
			assert (0 <= modulo && modulo < 10.0);

			if (state == NOTHING && 1.1 <= modulo && modulo <= 2) {
				buy(sale);
				state = HOLDING;
			} else if (state == HOLDING && 8 <= modulo && modulo <= 8.9) {
				sell(sale);
				state = NOTHING;
			}
		}

		void process(List<Sale> sales) {
			for (Sale sale : sales)
				process(sale);
		}
	};

	private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00#");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy.MM.dd HH:mm");

	public static List<Bucket> normalize(List<Bucket> buckets, double modulo) {
		double step = buckets.get(1).low - buckets.get(0).low;
		int count = (int) Math.round(modulo / step);
		assert ((modulo / step) - count < 0.0001);

		ArrayList<Bucket> normalized = new ArrayList<Bucket>();

		for (int i = 0; i < count; ++i) {
			Bucket bucket = new Bucket();
			bucket.low = i * step;
			normalized.add(bucket);
		}

		for (Bucket bucket : buckets) {
			int index = (int) (Math.round(bucket.low / step) % count);
			normalized.get(index).volume += bucket.volume;
		}

		return normalized;
	}

	public static class Bucket {
		double low, volume;

		public String toString() {
			return DECIMAL_FORMAT.format(low) + "\t"
					+ DECIMAL_FORMAT.format(volume);
		}
	}

	public static void printBuckets(List<Bucket> buckets) {
		for (Bucket bucket : buckets)
			System.out.println(bucket);
	}

	public static List<Bucket> histogram(List<Sale> sales, double step) {
		if (sales == null || sales.size() <= 0)
			throw new IllegalArgumentException();

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for (Sale sale : sales) {
			if (min > sale.low)
				min = sale.low;
			if (max < sale.high)
				max = sale.high;
		}

		ArrayList<Bucket> buckets = new ArrayList<Bucket>();

		double start = Math.floor(min / step) * step;

		for (double low = start; low < max + step; low += step) {
			Bucket bucket = new Bucket();
			bucket.low = low;
			buckets.add(bucket);
		}

		for (Sale sale : sales) {
			int beg = (int) Math.floor((sale.low - start) / step);
			int end = (int) Math.ceil((sale.high - start) / step);

			assert (buckets.get(beg).low <= sale.low);
			assert (buckets.get(end - 1).low <= sale.high);
			assert (buckets.get(end).low > sale.high);

			double amount = sale.volume / (end - beg);
			for (int i = beg; i < end; ++i) {
				buckets.get(i).volume += amount;
			}
		}

		if (buckets.get(buckets.size() - 1).volume == 0.0)
			buckets.remove(buckets.size() - 1);

		return buckets;
	}

	public static class Sale {
		long time;
		double open, low, high, close, volume;

		public String toString() {
			String s = "";

			s += DATE_FORMAT.format(new Date(time * 1000));
			s += "\t" + DECIMAL_FORMAT.format(open);
			s += "\t" + DECIMAL_FORMAT.format(low);
			s += "\t" + DECIMAL_FORMAT.format(high);
			s += "\t" + DECIMAL_FORMAT.format(close);
			s += "\t" + DECIMAL_FORMAT.format(volume);

			return s;
		}
	}

	public static void printSales(List<Sale> sales) {
		for (Sale sale : sales)
			System.out.println(sale);
	}

	public static List<Sale> readGoogle(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				stream, "UTF-8"));

		String[] columns = null;

		for (;;) {
			String line = reader.readLine();
			if (line == null)
				throw new IOException("TIMEZONE_OFFSET line not found");
			else if (line.startsWith("TIMEZONE_OFFSET="))
				break;
			else if (line.startsWith("COLUMNS="))
				columns = line.substring(8).split(",");
		}

		if (columns == null)
			throw new IOException("COLUMNS line not found");

		ArrayList<Sale> sales = new ArrayList<Sale>();
		long baseTime = 0;

		for (;;) {
			String line = reader.readLine();
			if (line == null)
				break;

			String[] values = line.split(",");
			if (values.length != columns.length) {
				throw new IOException("incorrect number of columns");
			}

			Sale sale = new Sale();

			for (int i = 0; i < columns.length; ++i) {
				if (columns[i].equals("DATE")) {
					if (values[i].startsWith("a")) {
						baseTime = Long.parseLong(values[i].substring(1));
						sale.time = baseTime;
					} else
						sale.time = baseTime + 60 * Integer.parseInt(values[i]);
				} else if (columns[i].equals("OPEN"))
					sale.open = Double.parseDouble(values[i]);
				else if (columns[i].equals("CLOSE"))
					sale.close = Double.parseDouble(values[i]);
				else if (columns[i].equals("HIGH"))
					sale.high = Double.parseDouble(values[i]);
				else if (columns[i].equals("LOW"))
					sale.low = Double.parseDouble(values[i]);
				else if (columns[i].equals("VOLUME"))
					sale.volume = Double.parseDouble(values[i]);
			}

			sales.add(sale);
		}

		reader.close();

		return sales;
	};

	public static List<Sale> readGoogle(String filename) throws IOException {
		InputStream stream;
		if (filename.startsWith("http://")) {
			URL url = new URL(filename);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			stream = conn.getInputStream();
		} else
			stream = new FileInputStream(filename);

		return readGoogle(stream);
	}

	public static void main(String[] args) throws IOException {
		List<Sale> sales = readGoogle("/home/mmaroti/aapl.txt");
		// List<Sale> sales =
		// readGoogle("http://www.google.com/finance/getprices?q=AAPL&x=NASDAQ&i=60&p=10d&f=d,o,c,h,l,v");

		List<Bucket> buckets = histogram(sales, 0.01);
		buckets = normalize(buckets, 10.0);
//		printBuckets(buckets);
		
		Robot robot = new Robot();
		robot.process(sales);
	}
}
