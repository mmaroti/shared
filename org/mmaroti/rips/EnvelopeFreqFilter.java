/*
 * Created on Aug 8, 2006
 * (C)opyright
 */
package org.mmaroti.rips;

import java.util.*;

/**
 * @author mmaroti
 */
public class EnvelopeFreqFilter
{
	public static double MAX_ENVELOPE_FREQ_ERROR = 2.0;

	public static Data filter(Data data)
	{
		Data filtered = new Data();
		
		filtered.addAllPositions(data.positions);
		filtered.addAllRipsData(filterRips(data.ripsData));
		filtered.addAllRssiData(data.rssiData);
		
		filtered.printStats();
		
		return filtered;
	}

	protected static List filterRips(List ripsData)
	{	
		Map lists = new HashMap();
		Iterator iter = ripsData.iterator();
		while( iter.hasNext() )
		{
			Data.Rips rips = (Data.Rips)iter.next();
			
			String name = rips.scenario + " " + rips.time + " " + rips.master + " " 
				+ rips.assistant + " " + rips.channel + " " + rips.power;

			List list = (List)lists.get(name);
			if( list == null )
			{
				list = new ArrayList();
				lists.put(name, list);
			}
			list.add(rips);
		}

		List output = new ArrayList();
		
		iter = lists.values().iterator();
		while( iter.hasNext() )
		{
			List list = (List)iter.next();
			filterSingle(list, output);
		}
		
		return output;
	}

	protected static void filterSingle(List data, List output)
	{
		if( data.isEmpty() )
			return;
		
		double[] freqs = new double[data.size()];

		for(int i = 0; i < freqs.length; ++i)
			freqs[i] = ((Data.Rips)data.get(i)).envelopeFreq;
		
		Arrays.sort(freqs);
		
		int bestFirst = 1;
		int bestLast = 1;
		int first = 1;
		int last = 1;

		while( first+1 < freqs.length )
		{
			while( last+1 < freqs.length && freqs[last+1]-freqs[first] < MAX_ENVELOPE_FREQ_ERROR )
				++last;
				
			if( last-first > bestLast-bestFirst || (last-first == bestLast-bestFirst 
				&& freqs[last]-freqs[first] < freqs[bestLast]-freqs[bestFirst]) )
			{
				bestLast = last;
				bestFirst = first;
			}
			
			++first;
		}
		
		for(int i = 0; i < freqs.length; ++i)
		{
			Data.Rips rips = (Data.Rips)data.get(i);
			if( rips.envelopeFreq >= freqs[bestFirst] && rips.envelopeFreq <= freqs[bestLast] )
				output.add(rips);
		}
	}
}
