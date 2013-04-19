/**
 *	Copyright (C) Miklos Maroti, 2004-2005
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

package org.mmaroti.ua.alg;

import org.mmaroti.ua.util.*;
import java.util.*;

/**
 * This structure captures a Hilbert system over an absolutely free algebra.
 * 
 * @author mmaroti@math.u-szeged.hu
 */
public class ProofSystem 
{
	protected TermAlgebra terms;
	
	/**
	 * Returns the underlying term algebra.
	 */
	public TermAlgebra getTermAlgebra()
	{
		return terms;
	}
	
	public static class Rule
	{
		/**
		 * Construct a new rule with the specified parameters.
		 */
		public Rule(Term[] premises, Term conclusion)
		{
			this.premises = premises;
			this.conclusion = conclusion;
		}
		
		/**
		 * Constructs a rule with no premises and with the
		 * specified conclusion.
		 */
		public Rule(Term conclusion)
		{
			this.premises = new Term[0];
			this.conclusion = conclusion;
		}
		
		/**
		 * Constructs a rule with a single premise and 
		 * with the specified conclusion.
		 */
		public Rule(Term premise, Term conclusion)
		{
			this.premises = new Term[] { premise };
			this.conclusion = conclusion;
		}
		
		/**
		 * Constructs a rule with two premises and 
		 * with the specified conclusion.
		 */
		public Rule(Term premise0, Term premise1, 
			Term conclusion)
		{
			this.premises = new Term[] { premise0, premise1 };
			this.conclusion = conclusion;
		}
		
		/**
		 * Constructs a rule with three premises and 
		 * with the specified conclusion.
		 */
		public Rule(Term premise0, Term premise1, 
			Term premise2, Term conclusion)
		{
			this.premises = new Term[] { premise0, premise1, premise2 };
			this.conclusion = conclusion;
		}
		
		protected Term[] premises;
		protected Term conclusion;

		/**
		 * Returns the list of premises of this rule.
		 */
		public Term[] getPremises()
		{
			return premises;
		}

		/**
		 * Returns the number of premises of this rule
		 */
		public int getPremiseCount()
		{
			return premises.length;			
		}

		/**
		 * Returns the conclusion of the rule.
		 */
		public Term getConclusion()
		{
			return conclusion;
		}

		/**
		 * Returns the textual representation of rules written
		 * as implications.
		 */
		public String toString()
		{
			String s = new String();
			
			for(int i = 0; i < premises.length; ++i)
			{
				if( i > 0 )
					s += ", ";
				s += premises[i];
			}
			
			return s + " --> " + conclusion;
		}

		public int hashCode()
		{
			int a = conclusion.hashCode();
			for(int i = 0; i < premises.length; ++i)
				a += premises[i].hashCode();
			return a;
		}
		
		public boolean equals(Object o)
		{
			if( !(o instanceof Rule) )
				return false;
			
			Rule r = (Rule)o;
			if( ! conclusion.equals(r.conclusion) || premises.length != r.premises.length )
				return false;
			
			for(int i = 0; i < premises.length; ++i)
				if( ! premises[i].equals(r.premises[i]) )
					return false;
					
			return true;
		}

		/**
		 * Returns <code>true</code> if the <code>larger</code> term is
		 * larger than the <code>smaller</code> one as defined for reduction rules.
		 */		
		public static boolean isLarger(Term larger, Term smaller)
		{
			if( smaller.getLength() > larger.getLength() )
				return false;
			
			List<Symbol> v = new ArrayList<Symbol>();
			larger.addMyVariablesTo(v);
			
			List<Symbol> w = new ArrayList<Symbol>();
			smaller.addMyVariablesTo(w);
			
			Iterator<Symbol> iter = w.iterator();
			while( iter.hasNext() )
			{
				if( ! v.remove(iter.next()) )
					return false;
			}
			
			return true;
		}
		
		/**
		 * Returns <code>true</code> if this rule is a reduction rule.
		 */
		public boolean isReduction()
		{
			for(int i = 0; i < premises.length; ++i)
				if( ! isLarger(conclusion, premises[i]) )
					return false;
			
			return true;
		}
		
		/**
		 * Performs the specified endomorphism on each term of this rule,
		 * and returns the new rule.
		 */
		public Rule evaluate(Evaluation endomorphism)
		{
			Term[] p = premises.clone();
			for(int i = 0; i< p.length; ++i)
				p[i] = (Term)endomorphism.getValue(p[i]);
			
			return new Rule(p, (Term)endomorphism.getValue(conclusion));
		}
		
		/**
		 * Returns the variables in the order of their 
		 * appearance with multiple occurrences.
		 */
		public List<Symbol> getVariableList()
		{
			ArrayList<Symbol> list = new ArrayList<Symbol>();

			conclusion.addMyVariablesTo(list);
			for(int i = 0; i < premises.length; ++i)
				premises[i].addMyVariablesTo(list);
			
			return list;
		}
		
		/**
		 * Returns the set of variables of this rule.
		 */
		public Set<Symbol> getVariableSet()
		{
			HashSet<Symbol> set = new HashSet<Symbol>();

			conclusion.addMyVariablesTo(set);
			for(int i = 0; i < premises.length; ++i)
				premises[i].addMyVariablesTo(set);
			
			return set;
		}
	}

	/**
	 * Apply the rule to a specific inequality. If the rule cannot 
	 * be applied then <code>null</code> is returned, otherwise
	 * there is an endomorphism that maps the conclusion of this rule
	 * to the specified root. In this case the list of premises,
	 * after mapped by the endomorphism, is returned.
	 */
	public Term[] applyRule(Rule rule, Term root)
	{
		Evaluation endomorphism = terms.createEndomorphism();
		if( ! endomorphism.extend(rule.conclusion, root) )
			return null;

		Term[] children = new Term[rule.premises.length];
		for(int i = 0; i < rule.premises.length; ++i)
			children[i] = (Term)endomorphism.getValue(rule.premises[i]);
	
		return children;
	}

	/**
	 * Returns the normalized form of the rule
	 */		
	public Rule normalize(Rule rule)
	{
		Evaluation automorphism =	
			terms.renameVariables(rule.getVariableList(), new ArrayList<Symbol>());

		return rule.evaluate(automorphism);
	}
	
	/**
	 * The set of rules of the Hilbert system
	 */
	protected Rule[] rules;

	/**
	 * Returns the list of rules of this Hilbert system
	 */
	public Rule[] getRules()
	{
		return rules;
	}

	/**
	 * Returns a string representation of this object.
	 */
	public String toString()
	{
		String s = new String();

		for(int i = 0; i < rules.length; ++i)
		{
			s += rules[i].toString();
			
			String a = new String();
			if( rules[i].isReduction() )
				a += (a.length() == 0 ? "(" : ", ") + "reduction";
			if( isReversible(rules[i]) )
				a += (a.length() == 0 ? "(" : ", ") + "reversible";
			
			if( a.length() != 0 )
				s += " " + a + ")";

			s += "\n";
		}
		
		return s;
	}
	
	/**
	 * This method returns <code>true</code> if the provided
	 * inequality is derivable using the set of rules, 
	 * <code>false</code> otherwise.
	 */
	public boolean isDerivable(Term root)
	{
		Derivation derivation = new Derivation();
		return (derivation.isDerivable(root, 1) == 0);
	}
	
	/**
	 * This method returns <code>true</code> if there exists
	 * a partial derivation tree whose leaves are some of the
	 * premises and root is the conclusion of the rule.
	 * If the rule is derivable, then that implication is true
	 * in the absolutely free algebra. However, the implication
	 * can still be true even if the rule is not derivable.
	 */
	public boolean isDerivable(Rule rule)
	{
		Derivation derivation = new Derivation();
		derivation.addPremises(rule.premises);
		return (derivation.isDerivable(rule.conclusion, 1) == 0);
	}

	/**
	 * This class is used to calculate with derivation trees.
	 */
	protected class Derivation
	{
		/**
		 * Contains a map from the visited terms to integers.
		 * Zero value means that the term is derivable,
		 * <code>Integer.MAX_VALUE</code> means that it is not,
		 * and values in between represents loop-backs to lower levels.
		 */
		public HashMap<Term,Integer> cache = new HashMap<Term,Integer>();
		
		/**
		 * Adds each of the premises as derivable statements.
		 */
		public void addPremises(Term[] premises)
		{
			int i = premises.length;
			while( --i >= 0 )
				cache.put(premises[i], new Integer(0));
		}
		
		/**
		 * Tests if the given term <code>root</code> is derivable.
		 * <code>0</code> is returned if the term is derivable,
		 * <code>Integer.MAX_VALUE</code> if the term is not derivable,
		 * and some value in between if the term is partially derivable,
		 * but there is a loop to terms higher up in the tree. The return
		 * value is always between <code>0</code> and <code>level-1</code>
		 * or <code>Integer.MAX_VALUE</code>. The value of <code>level</code>
		 * is the depth of the branch of the derivation tree.
		 */
		public int isDerivable(Term root, int level)
		{
			// check first the cache
			Integer o = cache.get(root);
			if( o != null )
				return o.intValue();

			// update the cache to avoid infinite cycles
			cache.put(root, new Integer(level));

			int a = Integer.MAX_VALUE;	// disjunction over all rules
			outer: for(int i = 0; i < rules.length; ++i)
			{
				Term[] subs = applyRule(rules[i], root);
				if( subs == null )	// not applicable
					continue;
				
				int b = 0;	// conjunction over children
				for(int j = 0; j < subs.length; ++j)
				{
					int c = isDerivable(subs[j], level + 1);
					if( b < c )
					{
						b = c;
						if( b == Integer.MAX_VALUE )	// this rule cannot work
							continue outer;
					}
				}
				
				if( a > b )
				{
					a = b;
					if( a == 0 ) // we found a derivation
					{
						cache.put(root, new Integer(0));
						return 0;
					}
				}
			}

			/* if all choices lead to loops to this or lower terms, 
			 * then this root cannot be derived
			 */ 
			if( level <= a )
			{
				cache.put(root, new Integer(Integer.MAX_VALUE));
				return Integer.MAX_VALUE;
			}

			/* this term has some derivation to terms higher up in the tree, 
			 * in which case we do not know if it is derivable or not
			 */
			cache.remove(root);
			return a;
		}
	}

	/**
	 * Constructs a Hilbert system on the specified absolutely free algebra.
	 * with the list of rules.
	 */
	public ProofSystem(TermAlgebra terms, Rule[] rules)
	{
		this.terms = terms;
		this.rules = rules;
	}

	/**
	 * Returns <code>true</code> if each of the rules are reduction rules.
	 */
	public boolean isDecidable()
	{
		for(int i = 0; i < rules.length; ++i)
			if( ! rules[i].isReduction() )
				return false;
		
		return true;
	}

	/**
	 * Checks if the given rule is reversible, i.e., it has a single
	 * premise, and the premise is derivable from the conclusion.
	 */
	public boolean isReversible(Rule rule)
	{
		if( rule.getPremiseCount() != 1 )
			return false;
		
		return isDerivable(new Rule(rule.conclusion, rule.premises[0]));
	}
	
	/**
	 * Refines an implication by exploring the possibility that the specified
	 * rule was applied at the specified premise of the implication.
	 * 
	 * @param implication the implication whose variables represent terms,
	 * 	premises are treated assumed to be derived and we need to verify that
	 *  its conclusion is also derivable.
	 * @param index the index of the premise of the <code>implication</code>
	 * 	we want to extend
	 * @param rule the assumed topmost rule of the derivation tree of the
	 * 	specified premise of the implication
	 * @return the most general implication that is obtained from <code>implication</code>
	 *  by using the <code>rule</code> at the specified premise, or <code>null</code>
	 *  if that rule could not have been applied at that premise.
	 */
	public Rule refine(Rule implication, int index, Rule rule)
	{
		Evaluation endomorphism = 
			terms.renameVariables(rule.getVariableSet(), implication.getVariableSet());

		rule = rule.evaluate(endomorphism);

		endomorphism = terms.findCommonExtension(implication.premises[index], rule.conclusion);
		if( endomorphism == null )
			return null;
		
		Term[] premises = 
			new Term[implication.getPremiseCount() - 1 + rule.getPremiseCount()];
		
		for(int i = 0; i < index; ++i)
			premises[i] = (Term)endomorphism.getValue(implication.premises[i]);
		
		for(int i = 0; i < rule.getPremiseCount(); ++i)
			premises[index + i] = (Term)endomorphism.getValue(rule.premises[i]);
			
		for(int i = index + 1; i < implication.getPremiseCount(); ++i)
			premises[i - 1 + rule.getPremiseCount()] = 
				(Term)endomorphism.getValue(implication.premises[i]);

		return new Rule(premises, (Term)endomorphism.getValue(implication.conclusion));	
	}

	/**
	 * Returns all refinements of depth one.
	 */
	public List<Rule> refine(Rule implication)
	{
		ArrayList<Rule> list = new ArrayList<Rule>();
		list.add(implication);
		
		int index = implication.getPremiseCount();
		while( --index >= 0 )
		{
			ArrayList<Rule> refinements = new ArrayList<Rule>();
			
			Iterator<Rule> iter = list.iterator();
			while( iter.hasNext() )
			{
				implication = iter.next();
				for(int i = 0; i < rules.length; ++i)
				{
					Rule refinement = refine(implication, index, rules[i]);
					if( refinement != null && ! isDerivable(refinement) )
						refinements.add(normalize(refinement));
				}
			}
			
			list = refinements;
		}
		
		return list;
	}

	/**
	 * Returns all refinements of depth <code>depth</code>.
	 */
	public List<Rule> refine(List<Rule> implications, int depth)
	{
		while( --depth >= 0 )
		{
			ArrayList<Rule> list = new ArrayList<Rule>();

			Iterator<Rule> iter = implications.iterator();
			while( iter.hasNext() )
				list.addAll(refine(iter.next()));
		
			implications = list;
		}
		
		return implications;
	}

	/**
	 * Prints out all refinements of the given implication
	 * of the specified depth.
	 */
	public void printRefinements(Rule implication, int depth)
	{
		implication = normalize(implication);
		System.out.println("refinements of " + implication);

		List<Rule> list = new ArrayList<Rule>();
		list.add(implication);
		
		list = refine(list, depth);
		
		Iterator<Rule> iter = list.iterator();
		while( iter.hasNext() )
		{
			Rule rule = iter.next();
			if( rule.conclusion.getLength() == 3)
				System.out.println(rule);
		}

		System.out.println("end of refinements");
	}

	/**
	 * Prints all counter examples of the implication where
	 * all the premises are proved using less than <code>steps</code>
	 * many rules.
	 */
	public void printCounterExamples(Rule implication, int steps)
	{
		System.out.println("counter-examples of " + implication + " of steps " + steps);
		printCounterExamplesInternal(implication, steps);
		System.out.println("end of counter-examples");
	}

	/**
	 * Prints all counter examples of the implication where
	 * all the premises are proved using less than <code>steps</code>
	 * many rules.
	 */
	protected void printCounterExamplesInternal(Rule implication, int steps)
	{
		int i = implication.getPremiseCount();

		// cannot be derived in this many steps
		if( i > steps )
			return;
			
		// complete derivation
		if( i == 0 )
		{
			if( ! isDerivable(implication.getConclusion()) )
				System.out.println(implication.getConclusion());
			
			return;
		}

		// decrease the number of steps and scan further
		--steps;
		for(i = 0; i < rules.length; ++i)
		{
			Rule r = refine(implication, 0, rules[i]);
			if( r != null )
				printCounterExamplesInternal(r, steps);
		}
	}

	/**
	 * Derives a new statement using a list of already derived statements
	 * and the provided rule. This command is capable of deriving statements
	 * using the cut rule.
	 * 
	 * @param rule the topmost rule of the derivation tree
	 * @param statements The list of derived statements we use at the
	 * 	second level in the derivation tree.
	 * @param args the indices of the derived statements that we use for
	 * 	each premise of the rule. This list must have the same length as
	 * 	the number of premises.
	 * @return the newly derived rule whose premises are common generalizations
	 *  of the premises of the simple implication and the derivable terms.
	 */
	public Rule derive(Rule rule, List<Term> statements, int[] args)
	{
		int i = rule.getPremiseCount();
		while( --i >= 0 )
		{
			Term statement = statements.get(args[i]);
			statement = terms.renameVariables(statement, rule.getVariableSet());
			
			Evaluation e = terms.findCommonExtension(rule.premises[i], statement);
			if( e == null )
				return null;
						
			rule = rule.evaluate(e);
		}
		
		return rule;
	}

	/**
	 * Lists all derivable terms in increasing complexity.
	 */
	public List<Term> getSmallDerivableTerms(int count)
	{
		List<Term> statements = new ArrayList<Term>();
		if( count <= 0 )
			return statements;
		
		// add trivial derivable terms
		for(int i = 0; i < rules.length; ++i)
			if( rules[i].getPremiseCount() == 0 )
			{
				statements.add(rules[i].getConclusion());
				if( --count <= 0 )
					return statements;
			}

		if( statements.size() == 0 )
			throw new IllegalArgumentException("there are no terminating rules");

		int level = 0;
		for(;;)
		{
			++level;
			for(int i = 0; i < rules.length; ++i)
			{
				if( rules[i].getPremiseCount() == 0 )
					continue;

				Argument args = new SphereArgument(rules[i].getPremiseCount(), level);	
				if( args.reset() ) do
				{
					Rule statement = derive(rules[i], statements, args.vector);
					if( statement != null && ! terms.isSpecialization(statement.conclusion, statements) )
					{
						statements.add(statement.conclusion);
						if( --count <= 0 )
								return statements;		
					}
				} while( args.next() );
			}
		}
	}
	
	/**
	 * Prints all counter-examples of the given implication. It uses each
	 * derivable statements from the list as premises and then form newly
	 * derived terms using the specified rule. If this term is not derivable
	 * using the standard rules, then it is a counter-example.
	 * 
	 * @param rule the implication whose elimination we want to check
	 * @param statements a list of derivable statements using the standard rules.
	 * @param all if this is <code>true</code> then all matches are returned,\
	 *  not just counter examples.
	 */
	public void printCounterExamples(Rule rule, List<Term> statements, boolean all)
	{
		System.out.println("counter-examples of " + rule);
		
		for(int level = 1; level <= statements.size(); ++level)
		{
			Argument args = new SphereArgument(rule.getPremiseCount(), level);
			if( args.reset() ) do
			{
				Rule statement = derive(rule, statements, args.vector);
				if( statement != null && (all || ! isDerivable(statement.conclusion)) )
					System.out.println(statement); 
			} while( args.next() );
		}
		
		System.out.println("end of counter-examples");
	}

	/**
	 * Prints all statements that are extensions of the given term.
	 */	
	public void printExamples(Term term, Collection<Term> statements)
	{
		System.out.println("examples of " + term);
		
		Iterator<Term> iter = statements.iterator();
		while( iter.hasNext() )
		{
			Term statement = iter.next();
			if( terms.createExtension(term, statement) != null )
				System.out.println(statement);
		}
		
		System.out.println("end of examples");
	}
}
