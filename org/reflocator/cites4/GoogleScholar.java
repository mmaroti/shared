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

package org.reflocator.cites4;

import java.io.*;
import java.util.*;
import java.util.prefs.*;
import java.util.regex.*;

import org.jsoup.*;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

/**
 * A class to download data and manage from Google Scholar. This does not do any
 * fancy processing, just focuses on Google Scholar type of data.
 */
public class GoogleScholar extends DataProvider {
	private int maxConnections = 15;

	public static class TooManyConnectionsException extends IOException {
		private static final long serialVersionUID = 51944780478954114L;

		TooManyConnectionsException(String message) {
			super(message);
		}
	}

	private static void addHeader(Connection conn) {
		conn.header("User-Agent", "Mozilla");
		conn.header("Accept", "text/html,text/plain");
		conn.header("Accept-Language", "en-us,en");
		conn.header("Accept-Encoding", "gzip");
		conn.header("Accept-Charset", "utf-8");
	}

	/**
	 * Downloads a webpage from Google Scholar, and handles all necessary
	 * cookies.
	 */
	public Document getDocument(String url) throws IOException {
		if (--maxConnections <= 0)
			throw new TooManyConnectionsException(
					"Too many Google Scholar HTML requests");

		Preferences pref = Preferences.userRoot().node(
				GoogleScholar.class.getName());

		String cookie = pref.get("cookie", "");
		if (!cookie.contains("GSP")) {
			Connection conn = Jsoup
					.connect("http://scholar.google.com/scholar_ncr");
			addHeader(conn);

			conn.get();

			Response resp = conn.response();
			cookie = "PREF=" + resp.cookie("PREF");
			cookie += "; GSP=" + resp.cookie("GSP") + ":CF=4";

			pref.put("cookie", cookie);
		}

		Connection conn = Jsoup.connect(url);
		addHeader(conn);
		conn.header("Cookie", cookie);

		Document doc = conn.get();

		return doc;
	}

	public void clearCookies() {
		Preferences pref = Preferences.userRoot().node(
				GoogleScholar.class.getName());
		pref.remove("cookie");
	}

	private static Pattern citeidPattern = Pattern
			.compile("/scholar\\?cites=([\\d]*)\\&");
	private static Pattern infoidPattern = Pattern.compile("info:([\\w-]*):");
	private static Pattern britidPattern = Pattern
			.compile("direct.bl.uk/research/([0-9/A-Z]*)\\.html");
	private static Pattern doiPattern = Pattern.compile("id=doi:([^&]*)");
	private static Pattern yearPattern = Pattern
			.compile(" ([12][0-9][0-9][0-9])( |$)");

	/*
	 * Returns a list of records from the Google Scholar URL. This method
	 * automatically follows the NEXT link, so your URL must start at the first
	 * record. Each record has the following fields:
	 *
	 * title: publication title (stable) url: main URL of the publication
	 * (stable, not always present) year: publication year (optional) doi: DOI
	 * identifier (optional) origin: some info about the authors, journal and
	 * publisher (unreliable) cites: number of citations (optional, unstable)
	 * citeid: Google Scholar identifier for the list of citations (optional,
	 * unstable) infoid: Google Scholar identifier for the BiBTeX entry
	 * (optional, unstable) britid: British Library Direct identifier (optional)
	 */
	public Table getRecordsByUrl(String url) throws IOException {
		MemoryTable records = new MemoryTable("scholar", new String[] {
				"title", "url", "year", "doi", "origin", "cites", "citeid",
				"infoid", "britid" });

		Document doc = getDocument(url);

		outer: for (;;) {
			Elements elements = doc.select("div.gs_r");
			for (Element element : elements) {
				Record record = records.createRecord();

				Elements links = element.select(".gs_rt a[href]");
				if (links.size() >= 2)
					throw new IllegalArgumentException(
							"Too many article links in scholar record");
				else if (links.size() == 1) {
					record.setValue("url", links.first().attr("href"));
					record.setValue("title", links.first().text());
				} else {
					String title = element.select(".gs_rt").text();
					if (!title.startsWith("[CITATION]"))
						throw new IllegalArgumentException(
								"Unexpected title format for scholar record");

					title = title.substring(10).trim();
					record.setValue("title", title);
				}

				links = element.select("span.gs_a");
				if (links.size() != 1)
					throw new IllegalArgumentException(
							"No summary line in scholar record");
				else {
					String origin = links.first().text();
					record.setValue("origin", origin);

					Matcher matcher = yearPattern.matcher(origin);
					if (matcher.find())
						record.setValue("year", matcher.group(1));
				}

				record.setValue("cites", "0");

				links = element.select(".gs_fl a[href]");
				for (Element link : links) {
					String text = link.text();

					if (text.startsWith("Cited by ")) {
						Matcher matcher = citeidPattern.matcher(link
								.attr("href"));
						if (!matcher.find())
							throw new IllegalArgumentException(
									"Cites url does not contain the cites field");

						record.setValue("citeid", matcher.group(1));
						record.setValue("cites", text.substring(9));
					}

					else if (text.startsWith("Find it")) {
						Matcher matcher = doiPattern.matcher(link.attr("href"));
						if (matcher.find())
							record.setValue("doi", matcher.group(1));
					}

					else if (text.equals("Import into BibTeX")) {
						Matcher matcher = infoidPattern.matcher(link
								.attr("href"));
						if (!matcher.find())
							throw new IllegalArgumentException(
									"BibTex url does not contain the info field");

						record.setValue("infoid", matcher.group(1));
					}

					else if (text.equals("BL Direct")) {
						Matcher matcher = britidPattern.matcher(link
								.attr("href"));
						if (!matcher.find())
							throw new IllegalArgumentException(
									"BL Direct url is not well formatted");

						record.setValue("britid", matcher.group(1));
					}
				}

				records.addRecord(record);
			}

			elements = doc.select("div.n a[href]");
			for (Element element : elements) {
				String text = element.text();

				if (text.equals("Next")) {
					String href = element.attr("href");
					if (!href.startsWith("/scholar?start"))
						throw new IOException("Unexpected format of next link");

					href = "http://scholar.google.com" + href;
					doc = getDocument(href);

					continue outer;
				}
			}

			// exit if no more Next links
			break;
		}

		// TODO: make sure that the CD counter is incremented properly in the
		// url list
		return records;
	}

	private static Pattern authorPattern = Pattern.compile("[a-zA-Z ]*");

	public Table getRecordsByAuthor(String author, String subject)
			throws IOException {
		String url = "http://scholar.google.com/scholar?start=0&num=100&hl=en&as_sdt=1,5";

		Matcher matcher = authorPattern.matcher(author);
		if (!matcher.matches())
			throw new IllegalArgumentException("Illegal author name");

		url += "&q=author:%22" + author.replace(' ', '+') + "%22";

		if (subject != null && subject.length() > 0)
			url += "&as_subj=" + subject;

		return getRecordsByUrl(url);
	}

	public Table getRecordsByCitesid(String citeid) throws IOException {
		String url = "http://scholar.google.com/scholar?cites=" + citeid
				+ "&as_sdt=2005&sciodt=1,5&hl=en&num=100";

		return getRecordsByUrl(url);
	}

	private static Pattern bibtexTypePattern = Pattern
			.compile("^\\s*@(\\w*)\\{");
	private static Pattern bibtexIdPattern = Pattern
			.compile("\\{([^\\{\\}=,]*),");
	private static Pattern bibtexPropertyPattern = Pattern
			.compile("\\s*(\\w*)=\\{(([^\\{\\}]|\\{([^\\{\\}]|\\{[^\\{\\}]*\\})*\\})*)\\}");

	static protected MemoryTable bibtexTable = new MemoryTable("bibtex",
			new String[] { "infoid", "bibid", "type", "title", "author",
					"journal", "pages", "year", "volume", "number", "issn",
					"book", "isbn", "publisher" });

	public Record getBibTexRecord(String infoid) throws IOException {
		Record record = bibtexTable.createRecord();
		record.setValue("infoid", infoid);

		Document doc = getDocument("http://scholar.google.com/scholar.bib?q=info:"
				+ infoid + ":scholar.google.com/&output=citation&hl=en");
		String bibtex = doc.body().text();

		Matcher matcher = bibtexTypePattern.matcher(bibtex);
		if (!matcher.find())
			throw new IllegalArgumentException(
					"Incorrect bibtex publication type");
		record.setValue("type", matcher.group(1));

		matcher = bibtexIdPattern.matcher(bibtex);
		if (!matcher.find())
			throw new IllegalArgumentException("Incorrect bibtex identifier: "
					+ bibtex);

		String bibid = matcher.group(1);
		bibid = Conversion.removeAccents(bibid);
		bibid = bibid.replace(' ', '_');
		record.setValue("bibid", bibid);

		matcher = bibtexPropertyPattern.matcher(bibtex);
		while (matcher.find()) {
			String label = matcher.group(1);
			String value = matcher.group(2);

			if (label.equals("title")) {
				if (value.startsWith("{") && value.endsWith("}"))
					value = value.substring(1, value.length() - 1);
			} else if (label.equals("author"))
				value = Conversion.removeTex(value);
			else if (label.equals("booktitle"))
				label = "book";

			if (record.getTable().hasKey(label))
				record.setValue(label, value);
		}

		return record;
	}

	@Override
	public int compare(Record a, Record b) {
		if (a.getValue("infoid").length() >= 10
				&& a.getValue("infoid").equals(b.getValue("infoid")))
			return DataProvider.IDENTICAL;

		if (a.getValue("citeid").length() >= 15
				&& a.getValue("citeid").equals(b.getValue("citeid")))
			return DataProvider.IDENTICAL;

		if (a.getValue("url").length() >= 15
				&& a.getValue("url").equals(b.getValue("url")))
			return DataProvider.IDENTICAL;

		if (a.getValue("title").length() >= 15
				&& a.getValue("title").equals(b.getValue("title"))
				&& a.getValue("origin").length() >= 10
				&& a.getValue("origin").equals(b.getValue("origin")))
			return DataProvider.IDENTICAL;

		if (a.getValue("doi").length() >= 10
				&& a.getValue("doi").equals(b.getValue("doi")))
			return DataProvider.EQUIVALENT;

		if (a.getValue("britid").length() >= 10
				&& a.getValue("britid").equals(b.getValue("britid")))
			return DataProvider.EQUIVALENT;

		if (a.getValue("title").length() >= 10
				&& getLevenshteinDistance(a.getValue("title").toUpperCase(), b
						.getValue("title").toUpperCase()) <= 5)
			return DataProvider.SIMILAR;

		return DataProvider.DIFFERENT;
	}

	@Override
	public Table getPartialRecords(String query) throws IOException {
		Table table;

		Map<String, String> options = getOptions(query);

		if (options.containsKey("author"))
			table = getRecordsByAuthor(options.get("author"),
					options.get("subject"));
		else
			throw new IllegalArgumentException("Illegal google query: " + query);

		return table;
	}

	protected static MemoryTable fullTable = new MemoryTable("scholar-full",
			new String[] { "title", "url", "year", "doi", "origin", "cites",
					"citeid", "infoid", "britid", "bibid", "type", "author",
					"journal", "pages", "volume", "number", "issn", "book",
					"isbn", "publisher" });

	@Override
	public Record getFullRecord(Record partialRecord) throws IOException {
		Record fullRecord = fullTable.createRecord();
		fullRecord.setValues(partialRecord);

		if (!partialRecord.getValue("infoid").equals("")) {
			Record bibtexRecord = getBibTexRecord(partialRecord
					.getValue("infoid"));
			fullRecord.setValues(bibtexRecord);
		}

		// this is a temporary object, we do not add it to the memory table
		return fullRecord;
	}

	public static void main(String[] args) throws Exception {
		GoogleScholar scholar = new GoogleScholar();
		Record record = scholar.getBibTexRecord("t618yHuMGq4J");
		System.out.println(record);
	}
}
