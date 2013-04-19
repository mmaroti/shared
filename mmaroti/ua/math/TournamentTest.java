package mmaroti.ua.math;

/**
 *	Copyright (C) 2001 Miklos Maroti
 */

import mmaroti.ua.alg.*;
import mmaroti.ua.io.*;

public class TournamentTest
{
	static void main(String[] args)
	{
		int[] partial = {
			0, 1, 0,-1, 1, 0,-1, 
			0, 0, 1, 0,-1, 1, 0,
			0, 0, 0, 1, 0,-1, 1,
			0, 0, 0, 0, 1, 0,-1,
			0, 0, 0, 0, 0, 1, 0,
			0, 0, 0, 0, 0, 0, 1,
			0, 0, 0, 0, 0, 0, 0 };
			
		Tournament tour = Tournaments.freeAlgebraOver(partial);

		UaWriter.out.print(tour);
		UaWriter.out.print(((FreeAlgebra)tour.alg()).generators());

		UaWriter.out.print(tour.covers());
	}

	public static void main2(String[] _)
	{
		int[] partial = {
			0, 1,-1,-1,
			0, 0,-1,-1,
			0, 0, 0, 1,
			0, 0, 0, 0 };
		
		Tournament free = Tournaments.freeAlgebraOver(partial);
		System.out.println(free.size());
//		UaWriter.out.print(free);
	}

	static void main3(String[] args)
	{
		int[] graph = {
			0, 1, 0,
			0, 0, 1,
			0, 0, 0 };
			
		Algebra cycle = Tournaments.createTournament(
			new FunctionBuffer(3, 2, graph));

		FreeAlgebra free = new FreeAlgebra(cycle, 4);
		System.out.println(free.size());
	}
}
