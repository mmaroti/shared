package mmaroti.ua.math;

/**
 *	Copyright (C) 2014 Miklos Maroti
 */

import java.util.*;
import mmaroti.ua.alg.*;
import mmaroti.ua.util.*;

public class ConMeetSemiDistr {

	public static void main(String[] args) {
		Equivalence a = Equivalence.zero(5);
		a.join(0, 3);

		Equivalence b = Equivalence.zero(5);
		b.join(0, 2);
		b.join(3, 4);

		Equivalence c = Equivalence.zero(5);
		c.join(0,1);
		c.join(1,4);

		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
		System.out.println(Equivalence.meet(a, b));
	}
}
