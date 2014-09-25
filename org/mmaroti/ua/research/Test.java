package org.mmaroti.ua.research;

import org.mmaroti.ua.alg.*;
import org.mmaroti.ua.util.*;

public class Test
{
	public static void main(String[] _)
	{
		Signature signature = new Signature(new Symbol[] 
		{
			new Symbol("w", 2, 0, 0)
		});
		
//		AlgebraBuffer A = new AlgebraBuffer(signature, 7);
//		AlgebraBuffer.Operation f = A.getOperationTable(0);
//		f.setValue(new int[] {1,1,1,1}, 0);

		TermAlgebra A = new TermAlgebra(signature, 100);

//		Algebras.printTo(A, XmlWriter.out);
		
//		ArrayList p = new ArrayList();
//		p.add(A);
//		ProductAlgebra P = new ProductAlgebra(p);
		
		SubAlgebra S = new SubAlgebra(A);
		S.add( A.getVariable(0) );
		S.generate(10);

		Algebras.printTo(S, XmlWriter.out);
	}
}
