/*
 * Created on Aug 7, 2006
 * (C)opyright
 */
package org.mmaroti.rips;

import java.util.*;
import java.io.*;

/**
 * @author mmaroti
 */
public class Data
{
	public static final double CHANNEL_BASE = 430105543;		// herz
	public static final double CHANNEL_SEPARATION = 526628.5;	// herz
	public static final double SPEED_OF_LIGHT  = 299792458;		// meters per seconds

	public Set<String> scenarios = new TreeSet<String>();	// set of scenarios names (string)
	public Set<Integer> nodes = new TreeSet<Integer>();		// set of node ids (integer)
	public Set<Integer> channels = new TreeSet<Integer>();	// set of channel ids (integer)
	public Set<String> powers = new TreeSet<String>();		// set of power setting names (string)
	
	public void clear()
	{
		scenarios.clear();
		nodes.clear();
		channels.clear();
		powers.clear();
		
		positions.clear();
		ripsData.clear();
		rssiData.clear();
	}
	
	public void printStats()
	{
		System.out.println("data with " 
			+ scenarios.size() + " scenarios, "
			+ nodes.size() + " nodes, "
			+ channels.size() + " channels, "
			+ powers.size() + " power settings, "
			+ positions.size() + " position records, "
			+ ripsData.size() + " rips records, "
			+ rssiData.size() + " rssi records."
			);
	}
	
	public void load(String fileBase) throws IOException
	{
		clear();
		
		loadPositions(fileBase + ".pos");
		loadRipsData(fileBase + ".rips");
		loadRssiData(fileBase + ".rssi");
		
		printStats();
	}
	
	public static class Pos
	{
		public String scenario;			// scenario setting
		public int node;				// node ID
		public double x;				// in meters
		public double y;				// in meters
		public double z;				// in meters

		public double getDistance(Pos pos)
		{
			return Math.sqrt((x - pos.x) * (x - pos.x) 
				+ (y - pos.y) * (y - pos.y) + (z - pos.z) * (z - pos.z)); 
		}
	}
	
	public List<Pos> positions = new ArrayList<Pos>();
	
	public void addPosition(Pos pos)
	{
		positions.add(pos);

		nodes.add(pos.node);
		scenarios.add(pos.scenario);
	}
	
	public void addAllPositions(List<Pos> positions)
	{
		Iterator<Pos> iter = positions.iterator();
		while( iter.hasNext() )
			addPosition(iter.next());
	}
	
	public Pos getPosition(String scenario, int nodeID)
	{
		Iterator<Pos> iter = positions.iterator();
		while( iter.hasNext() )
		{
			Pos pos = iter.next();
			if( pos.scenario.equals(scenario) && pos.node == nodeID )
				return pos;
		}
		return null;
	}

	protected void loadPositions(String file) throws IOException
	{
		BufferedReader r = new BufferedReader(new FileReader(file));
		positions.clear();
		for(;;)
		{
			String line = r.readLine();
			if( line == null ) {
				r.close();
				break;
			}
				
			if( line.startsWith("#") )
				continue;
			
			StringTokenizer t = new StringTokenizer(line);
			if( t.countTokens() == 0 )
				continue;

			Pos pos = new Pos();
			pos.scenario = t.nextToken();
			pos.node = Integer.parseInt(t.nextToken());
			pos.x = Double.parseDouble(t.nextToken());
			pos.y = Double.parseDouble(t.nextToken());
			pos.z = Double.parseDouble(t.nextToken());
			addPosition(pos);
			
			if( t.hasMoreTokens() ) {
				r.close();
				throw new IOException("incorrect pos line: " + line);
			}
		}
	}

	public int[] getNodes()
	{
		int[] ns = new int[nodes.size()];
		
		int i = 0;
		Iterator<Integer> iter = nodes.iterator();
		while( i < ns.length )
			ns[i++] = iter.next();
			
		Arrays.sort(ns);
		
		return ns;
	}

	public static double getCarrierFrequency(int channel)
	{
		return CHANNEL_BASE + channel * CHANNEL_SEPARATION;
	}

	public static class Rips
	{
		public String scenario;			// scenario name
		public String time;				// time when data was downloaded
		public int master;				// node ID
		public int assistant;			// node ID
		public int channel;				// channel ID
		public String power;			// power settings
		public int receiver;			// node ID
		public double absolutePhase;
		public double envelopeFreq;
	
		public double getCarrierFrequency()
		{
			return Data.getCarrierFrequency(channel);
		}
	}

	public List<Rips> ripsData = new ArrayList<Rips>();

	public void addRipsData(Rips rips)
	{
		ripsData.add(rips);

		nodes.add(new Integer(rips.master));
		nodes.add(new Integer(rips.assistant));
		nodes.add(new Integer(rips.receiver));
		scenarios.add(rips.scenario);
		channels.add(new Integer(rips.channel));
		powers.add(rips.power);
	}
	
	public void addAllRipsData(List<Rips> ripsData)
	{
		Iterator<Rips> iter = ripsData.iterator();
		while( iter.hasNext() )
			addRipsData(iter.next());
	}
	
	protected void loadRipsData(String file) throws IOException
	{
		BufferedReader r = new BufferedReader(new FileReader(file));
		ripsData.clear();
		for(;;)
		{
			String line = r.readLine();
			if( line == null ) {
				r.close();
				break;
			}
				
			if( line.startsWith("#") )
				continue;
			
			StringTokenizer t = new StringTokenizer(line);
			if( t.countTokens() == 0 )
				continue;

			Rips rips = new Rips();
			rips.scenario = t.nextToken();
			rips.time = t.nextToken();
			rips.master = Integer.parseInt(t.nextToken());
			rips.assistant = Integer.parseInt(t.nextToken());
			rips.channel = Integer.parseInt(t.nextToken());
			rips.power = t.nextToken();
			rips.receiver = Integer.parseInt(t.nextToken());
			rips.absolutePhase = Double.parseDouble(t.nextToken());
			rips.envelopeFreq = Double.parseDouble(t.nextToken());
			addRipsData(rips);
			
			if( t.hasMoreTokens() ) {
				r.close();
				throw new IOException("incorrect rips line: " + line);
			}
		}
	}
	
	public static class Rssi
	{
		public String scenario;			// scenario setting
		public String time;				// time the data was downloaded
		public int sender;				// node ID
		public int channel;				// channel ID
		public String power;			// power settings
		public int receiver;			// node ID
		public int rssi;				// measured rssi value 
	}

	public List<Rssi> rssiData = new ArrayList<Rssi>();

	public void addRssiData(Rssi rssi)
	{
		rssiData.add(rssi);
			
		nodes.add(new Integer(rssi.sender));
		nodes.add(new Integer(rssi.receiver));
		scenarios.add(rssi.scenario);
		channels.add(new Integer(rssi.channel));
		powers.add(rssi.power);
	}
	
	public void addAllRssiData(List<Rssi> rssiData)
	{
		Iterator<Rssi> iter = rssiData.iterator();
		while( iter.hasNext() )
			addRssiData(iter.next());
	}
	
	protected void loadRssiData(String file) throws IOException
	{
		BufferedReader r = new BufferedReader(new FileReader(file));
		rssiData.clear();
		for(;;)
		{
			String line = r.readLine();
			if( line == null ) {
				r.close();
				break;
			}
				
			if( line.startsWith("#") )
				continue;
			
			StringTokenizer t = new StringTokenizer(line);
			if( t.countTokens() == 0 )
				continue;

			Rssi rssi = new Rssi();
			rssi.scenario = t.nextToken();
			rssi.time = t.nextToken();
			rssi.sender = Integer.parseInt(t.nextToken());
			rssi.channel = Integer.parseInt(t.nextToken());
			rssi.power = t.nextToken();
			rssi.receiver = Integer.parseInt(t.nextToken());
			rssi.rssi = Integer.parseInt(t.nextToken());
			addRssiData(rssi);

			if( t.hasMoreTokens() ) {
				r.close();
				throw new IOException("incorrect rssi line: " + line);
			}
		}
	}
}
