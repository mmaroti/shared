/**
 *	Copyright (C) Miklos Maroti, 2005
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

package org.mmaroti.ua.research;

import org.mmaroti.ua.alg.*;

public class ResiduatedLattices
{
	static public TermAlgebra terms;
	static public TermAlgebra.Term[] vars;

	static public Operation product;
	static public Operation under;
	static public Operation over;
	static public Operation join;
	static public Operation meet;
	static public Operation unit;
	static public Operation leq;

	static public ProofSystem.Rule identityRule;
	static public ProofSystem.Rule productOrderRule;
	static public ProofSystem.Rule overOrderRule;
	static public ProofSystem.Rule underOrderRule;
	static public ProofSystem.Rule residuationRule1;
	static public ProofSystem.Rule residuationRule2;
	static public ProofSystem.Rule residuationRule3;
	static public ProofSystem.Rule residuationRule4;
	static public ProofSystem.Rule leftJoinRule;
	static public ProofSystem.Rule rightJoinRule1;
	static public ProofSystem.Rule rightJoinRule2;
	static public ProofSystem.Rule leftMeetRule1;
	static public ProofSystem.Rule leftMeetRule2;
	static public ProofSystem.Rule rightMeetRule;
	static public ProofSystem.Rule unitRule1;
	static public ProofSystem.Rule unitRule2;
	static public ProofSystem.Rule unitRule3;
	static public ProofSystem.Rule unitRule4;
	static public ProofSystem.Rule commutativityRule;
	static public ProofSystem.Rule associativityRule1;
	static public ProofSystem.Rule associativityRule2;
	static public ProofSystem.Rule cancellationRule1;
	static public ProofSystem.Rule cancellationRule2;
	static public ProofSystem.Rule cancellationRule3;
	static public ProofSystem.Rule cancellationRule4;

	static public ProofSystem.Rule cutRule;
	static public ProofSystem.Rule meetSemiDistributivity;
	static public ProofSystem.Rule joinSemiDistributivity;
	static public ProofSystem.Rule leftCancellativity;
	static public ProofSystem.Rule rightCancellativity;
	static public ProofSystem.Rule meetDistributivity;
	static public ProofSystem.Rule joinDistributivity;
	static public ProofSystem.Rule modularity;
	
	static public ProofSystem system;

	static void construct()
	{
		Signature signature = new Signature(new Symbol[]
        {
			new Symbol("*", 2, 20, Symbol.INFIX | Symbol.ASSOCIATIVE),
			new Symbol("\\", 2, 20, Symbol.INFIX),
			new Symbol("/", 2, 20, Symbol.INFIX),
			new Symbol(" \\vee ", 2, 10, Symbol.INFIX | Symbol.ASSOCIATIVE),
			new Symbol(" \\wedge ", 2, 10, Symbol.INFIX | Symbol.ASSOCIATIVE),
			new Symbol("1", 0, Integer.MAX_VALUE, Symbol.INFIX),
			new Symbol(" <= ", 2, 0, Symbol.INFIX),
        });
		
		terms = new TermAlgebra(signature, 5);
		
		TermAlgebra.Term[] vars = new TermAlgebra.Term[] {
				terms.getVariable(0),
				terms.getVariable(1),
				terms.getVariable(2),
				terms.getVariable(3),
				terms.getVariable(4),
		};
		
		for(int i = 0; i < vars.length; ++i)
			vars[i] = terms.getVariable(i);

		product = terms.getOperations()[0];
		under = terms.getOperations()[1];
		over = terms.getOperations()[2];
		join = terms.getOperations()[3];
		meet = terms.getOperations()[4];
		unit = terms.getOperations()[5];
		leq = terms.getOperations()[6];

		identityRule = new ProofSystem.Rule(
			(Term)leq.getValue(vars[0],vars[0]));

		productOrderRule = new ProofSystem.Rule(
			(Term)leq.getValue(vars[0],vars[1]),
			(Term)leq.getValue(vars[2],vars[3]),
			(Term)leq.getValue(product.getValue(vars[0],vars[2]),product.getValue(vars[1],vars[3])));
		
		overOrderRule = new ProofSystem.Rule(
			(Term)leq.getValue(vars[0],vars[1]),
			(Term)leq.getValue(vars[3],vars[2]),
			(Term)leq.getValue(over.getValue(vars[0],vars[2]),over.getValue(vars[1],vars[3])));
		
		underOrderRule = new ProofSystem.Rule(
			(Term)leq.getValue(vars[1],vars[0]),
			(Term)leq.getValue(vars[2],vars[3]),
			(Term)leq.getValue(under.getValue(vars[0],vars[2]),under.getValue(vars[1],vars[3])));
		
		residuationRule1 = new ProofSystem.Rule(
			(Term)leq.getValue(product.getValue(vars[0],vars[1]),vars[2]),
			(Term)leq.getValue(vars[1],under.getValue(vars[0],vars[2])));
		
		residuationRule2 = new ProofSystem.Rule(
			(Term)leq.getValue(product.getValue(vars[0],vars[1]),vars[2]),
			(Term)leq.getValue(vars[0],over.getValue(vars[2],vars[1])));
		
		residuationRule3 = new ProofSystem.Rule(
			(Term)leq.getValue(vars[0],over.getValue(vars[2],vars[1])),
			(Term)leq.getValue(product.getValue(vars[0],vars[1]),vars[2]));
		
		residuationRule4 = new ProofSystem.Rule(
			(Term)leq.getValue(vars[1],under.getValue(vars[0],vars[2])),
			(Term)leq.getValue(product.getValue(vars[0],vars[1]),vars[2]));
		
		leftJoinRule = new ProofSystem.Rule(
			(Term)leq.getValue(vars[0],vars[2]),
			(Term)leq.getValue(vars[1],vars[2]),
			(Term)leq.getValue(join.getValue(vars[0],vars[1]),vars[2]));
		
		rightJoinRule1 = new ProofSystem.Rule(
			(Term)leq.getValue(vars[0],vars[1]),
			(Term)leq.getValue(vars[0],join.getValue(vars[1],vars[2])));
		
		rightJoinRule2 = new ProofSystem.Rule(
			(Term)leq.getValue(vars[0],vars[2]),
			(Term)leq.getValue(vars[0],join.getValue(vars[1],vars[2])));
		
		leftMeetRule1 = new ProofSystem.Rule(
			(Term)leq.getValue(vars[0],vars[2]),
			(Term)leq.getValue(meet.getValue(vars[0],vars[1]),vars[2]));
		
		leftMeetRule2 = new ProofSystem.Rule(
			(Term)leq.getValue(vars[1],vars[2]),
			(Term)leq.getValue(meet.getValue(vars[0],vars[1]),vars[2]));
		
		rightMeetRule = new ProofSystem.Rule(
			(Term)leq.getValue(vars[0],vars[1]),
			(Term)leq.getValue(vars[0],vars[2]),
			(Term)leq.getValue(vars[0],meet.getValue(vars[1],vars[2])));
		
		unitRule1 = new ProofSystem.Rule(
			(Term)leq.getValue(vars[0], unit.getConstantElement()),
			(Term)leq.getValue(vars[1], vars[2]),
			(Term)leq.getValue(product.getValue(vars[0],vars[1]),vars[2]));
		
		unitRule2 = new ProofSystem.Rule(
			(Term)leq.getValue(vars[1], unit.getConstantElement()),
			(Term)leq.getValue(vars[0], vars[2]),
			(Term)leq.getValue(product.getValue(vars[0],vars[1]),vars[2]));
		
		unitRule3 = new ProofSystem.Rule(
			(Term)leq.getValue(unit.getConstantElement(), vars[1]),
			(Term)leq.getValue(vars[0], vars[2]),
			(Term)leq.getValue(vars[0], product.getValue(vars[1],vars[2])));
		
		unitRule4 = new ProofSystem.Rule(
			(Term)leq.getValue(unit.getConstantElement(), vars[2]),
			(Term)leq.getValue(vars[0], vars[1]),
			(Term)leq.getValue(vars[0], product.getValue(vars[1],vars[2])));
		
		commutativityRule = new ProofSystem.Rule(
			(Term)leq.getValue(product.getValue(vars[0],vars[1]),vars[2]),
			(Term)leq.getValue(product.getValue(vars[1],vars[0]),vars[2]));

		associativityRule1 = new ProofSystem.Rule(
			(Term)leq.getValue(product.getValue(vars[0],vars[1]),under.getValue(vars[2],vars[3])),
			(Term)leq.getValue(product.getValue(vars[2],vars[0]),over.getValue(vars[3],vars[1])));
		
		associativityRule2 = new ProofSystem.Rule(
			(Term)leq.getValue(product.getValue(vars[2],vars[0]),over.getValue(vars[3],vars[1])),
			(Term)leq.getValue(product.getValue(vars[0],vars[1]),under.getValue(vars[2],vars[3])));
		
		cancellationRule1 = new ProofSystem.Rule(
			(Term)leq.getValue(vars[0],product.getValue(vars[1],vars[2])),
			(Term)leq.getValue(under.getValue(vars[1],vars[0]),vars[2]));
		
		cancellationRule2 = new ProofSystem.Rule(
			(Term)leq.getValue(vars[0],product.getValue(vars[1],vars[2])),
			(Term)leq.getValue(over.getValue(vars[0],vars[2]),vars[1]));
		
		cancellationRule3 = new ProofSystem.Rule(
			(Term)leq.getValue(vars[1],product.getValue(join.getValue(vars[0],vars[2]),vars[4])),
			(Term)leq.getValue(vars[3],product.getValue(join.getValue(vars[0],vars[2]),vars[4])),
			(Term)leq.getValue(meet.getValue(under.getValue(vars[0],vars[1]),under.getValue(vars[2],vars[3])),vars[4]));
		
		cancellationRule4 = new ProofSystem.Rule(
			(Term)leq.getValue(vars[1],product.getValue(vars[4],join.getValue(vars[0],vars[2]))),
			(Term)leq.getValue(vars[3],product.getValue(vars[4],join.getValue(vars[0],vars[2]))),
			(Term)leq.getValue(meet.getValue(over.getValue(vars[1],vars[0]),over.getValue(vars[3],vars[2])),vars[4]));
		
		cutRule = new ProofSystem.Rule(
			(Term)leq.getValue(vars[0],vars[1]),
			(Term)leq.getValue(vars[1],vars[2]), 
			(Term)leq.getValue(vars[0],vars[2]));

		meetSemiDistributivity = new ProofSystem.Rule(
			(Term)leq.getValue(meet.getValue(vars[0],vars[1]),vars[2]),
			(Term)leq.getValue(meet.getValue(vars[0],vars[2]),vars[1]),
			(Term)leq.getValue(meet.getValue(vars[0],join.getValue(vars[1],vars[2])),vars[1]));

		joinSemiDistributivity = new ProofSystem.Rule(
			(Term)leq.getValue(vars[1],join.getValue(vars[0],vars[2])),
			(Term)leq.getValue(vars[2],join.getValue(vars[0],vars[1])),
			(Term)leq.getValue(vars[1],join.getValue(vars[0],meet.getValue(vars[1],vars[2]))));

		leftCancellativity = new ProofSystem.Rule(
			(Term)leq.getValue(product.getValue(vars[0],vars[1]),product.getValue(vars[0],vars[2])),
			(Term)leq.getValue(vars[1],vars[2]));

		rightCancellativity = new ProofSystem.Rule(
			(Term)leq.getValue(product.getValue(vars[1],vars[0]),product.getValue(vars[2],vars[0])),
			(Term)leq.getValue(vars[1],vars[2]));

		meetDistributivity = new ProofSystem.Rule(
			(Term)leq.getValue(meet.getValue(vars[0],vars[1]),vars[3]),
			(Term)leq.getValue(meet.getValue(vars[0],vars[2]),vars[3]),
			(Term)leq.getValue(meet.getValue(vars[0],join.getValue(vars[1],vars[2])),vars[3]));
	
		joinDistributivity = new ProofSystem.Rule(
			(Term)leq.getValue(vars[3],join.getValue(vars[0],vars[1])),
			(Term)leq.getValue(vars[3],join.getValue(vars[0],vars[2])),
			(Term)leq.getValue(vars[3],join.getValue(vars[0],meet.getValue(vars[1],vars[2]))));
	
		modularity = new ProofSystem.Rule(
			(Term)leq.getValue(vars[2],vars[0]),
			(Term)leq.getValue(meet.getValue(vars[0],vars[1]),vars[3]),
			(Term)leq.getValue(vars[2],vars[3]),
			(Term)leq.getValue(meet.getValue(vars[0],join.getValue(vars[1],vars[2])),vars[3]));
	
		system = new ProofSystem(terms, new ProofSystem.Rule[] 
		{
			identityRule,
//			productOrderRule, overOrderRule, underOrderRule,
//			residuationRule1, residuationRule2, residuationRule3, residuationRule4,
			leftJoinRule, rightJoinRule1, rightJoinRule2, leftMeetRule1, leftMeetRule2, rightMeetRule,
//			unitRule1, unitRule2, unitRule3, unitRule4, 
//			commutativityRule, 
//			associativityRule1, associativityRule2, 
//			cancellationRule1, cancellationRule2,
//			cancellationRule3, cancellationRule4,
//			meetDistributivity, joinDistributivity,
//			modularity,
		});
	}
	
	static public void main(String[] args)
	{
		construct();
		System.out.println("rules");
		System.out.print(system);
		System.out.println("end of rules");

//		system.printCounterExamples(leftCancellativity, 7);
//		system.printCounterExamples(cutRule, 8);
//		system.printCounterExamples(cutRule, 9);
//		system.printCounterExamples(meetSemiDistributivity, 8);
		system.printCounterExamples(modularity, 6);
		
//		HilbertSystem.Rule test = new HilbertSystem.Rule(
//			(TermAlgebra.Term)leq.get(product.get(vars[0],vars[2]),product.get(join.get(vars[0],vars[1]),vars[3])),
//			(TermAlgebra.Term)leq.get(vars[2],vars[3]));

//		system.printCounterExamples(test, 2);
		
//		exploreCancellativity();

//		TermAlgebra.Term mask = (TermAlgebra.Term)leq.get(product.get(vars[0],vars[1]),
//			join.get(vars[2],vars[3]));

//		List statements = system.getSmallDerivableTerms(30000);
//		system.printExamples(mask, statements);

//		system.printRefinements(leftCancellativity, 1);
//		system.printRefinements(leftCancellativity, 2);
//		system.printRefinements(leftCancellativity, 3);
	}
	
	static void exploreCancellativity()
	{
		ProofSystem.Rule rule = leftCancellativity;
		System.out.println(rule);
		
		rule = system.refine(rule, 0, residuationRule3);
		System.out.println(rule);
		
		rule = system.refine(rule, 0, leftJoinRule);
		System.out.println(rule);
		
		rule = system.refine(rule, 0, overOrderRule);
		System.out.println(rule);
		
		rule = system.refine(rule, 0, productOrderRule);
		System.out.println(rule);
		
		rule = system.refine(rule, 0, rightJoinRule2);
		System.out.println(rule);
		
		rule = system.refine(rule, 0, identityRule);
		System.out.println(rule);
		
		rule = system.refine(rule, 0, identityRule);
		System.out.println(rule);
		
		rule = system.refine(rule, 0, identityRule);
		System.out.println(rule);
		
		rule = system.refine(rule, 0, residuationRule2);
		System.out.println(rule);
		
		rule = system.refine(rule, 0, residuationRule4);
		System.out.println(rule);
		
		rule = system.refine(rule, 0, underOrderRule);
		System.out.println(rule);
		
		rule = system.refine(rule, 0, identityRule);
		System.out.println(rule);
		
		rule = system.refine(rule, 0, productOrderRule);
		System.out.println(rule);

		rule = system.refine(rule, 0, rightJoinRule1);
		System.out.println(rule);

		rule = system.refine(rule, 0, residuationRule2);
		System.out.println(rule);

		rule = system.refine(rule, 0, productOrderRule);
		System.out.println(rule);

		rule = system.refine(rule, 0, identityRule);
		System.out.println(rule);

		rule = system.refine(rule, 1, identityRule);
		System.out.println(rule);
	}
}

