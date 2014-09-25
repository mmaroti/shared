/**
 *	Copyright (C) Miklos Maroti, 2000-2004
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

/**
 * This class represents a homomorphism from the term algebra to another (or
 * possibly the same) compatible algebra. It allows to work with evaluations and
 * substitutions of terms. A homomorphism is represented as a map of the
 * generators of the term algebra.
 * 
 * @author mmaroti@math.u-szeged.hu
 */
public class Evaluation {
	public Evaluation(TermAlgebra domain, Algebra codomain) {
		if (!domain.isCompatible(codomain))
			throw new IllegalArgumentException("codomain is not compatible");

		this.domain = domain;
		map = new Object[domain.getVariableCount()];

		this.codomain = codomain;
		this.codomainops = codomain.getOperations();

	}

	protected final TermAlgebra domain;

	/**
	 * Returns the domain of the evaluation, which is a term algebra.
	 */
	public TermAlgebra getDomain() {
		return domain;
	}

	protected Algebra codomain;
	protected Operation[] codomainops;

	/**
	 * Returns the codomain of the homomorphism
	 */
	public Algebra getCoDomain() {
		return codomain;
	}

	/**
	 * Contains the mapping of the generators to elements of the codomain.
	 */
	protected final Object[] map;

	/**
	 * Evaluates the specified term in the target algebra using the specified
	 * map of generators to elements. This method returns an element of the
	 * target algebra.
	 */
	public Object getValue(TermAlgebra.Term term) {
		if (term.isVariable()) {
			Object image = map[term.getVariableIndex()];
			if (image != null)
				return image;

			throw new IllegalArgumentException(
					"the image of a variable is not set");
		} else {
			final TermAlgebra.Term[] subterms = term.subterms;
			Object[] args = new Object[subterms.length];

			for (int i = 0; i < args.length; ++i)
				args[i] = getValue(subterms[i]);

			return codomainops[term.getSymbolIndex()].getValue(args);
		}
	}

	/**
	 * Composes the specified homomorphism with this object and stores the
	 * result in this object. The codomain of this object must be the same as
	 * the domain of the specified homomorphism.
	 */
	public void compose(Evaluation homomorphism) {
		if (codomain != homomorphism.getDomain())
			throw new IllegalArgumentException(
					"the codomain does not match the domain of other homomorphism");

		for (int i = 0; i < map.length; ++i)
			map[i] = homomorphism.getValue((TermAlgebra.Term) map[i]);

		// update the codomain
		codomain = homomorphism.codomain;
		codomainops = codomain.getOperations();
	}

	/**
	 * Builds an endomorphism (represented as a map of the generators to terms)
	 * which maps the <code>source</code> term to the <code>target</code> term.
	 * This endomorphism is extended. The function returns <code>true</code> if
	 * a mapping was found, or <code>false</code> if there exists no extension.
	 */
	public boolean extend(TermAlgebra.Term source, TermAlgebra.Term target) {
		assert (domain == codomain);

		if (source.isVariable()) {
			int i = source.getVariableIndex();
			if (map[i] == null) {
				map[i] = target;
				return true;
			}

			return codomain.areEquals(map[i], target);
		}

		if (target.isVariable()
				|| source.getSymbolIndex() != target.getSymbolIndex())
			return false;

		for (int i = 0; i < source.subterms.length; ++i)
			if (!extend(source.subterms[i], target.subterms[i]))
				return false;

		return true;
	}

	/**
	 * Sets the image of one of the variables elements.
	 */
	public final void set(int index, Object image) {
		map[index] = image;
	}

	/**
	 * Returns the image of one of the variables.
	 */
	public final Object get(int index) {
		return map[index];
	}

	/**
	 * Clears all assigned values to the variables.
	 */
	public final void clear() {
		for (int i = 0; i < map.length; ++i)
			map[i] = null;
	}

	/**
	 * Returns the string representation of this evaluation.
	 */
	public String toString() {
		String s = "[";

		for (int i = 0; i < map.length; ++i) {
			if (map[i] != null) {
				if (s.length() > 1)
					s += ", ";

				s += "x" + i + " = " + codomain.toString(map[i]);
			}
		}

		return s + "]";
	}
}
