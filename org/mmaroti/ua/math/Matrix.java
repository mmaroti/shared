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

package org.mmaroti.ua.math;

import java.util.*;

/**
 * This is the general class for finite dimensional vector spaces. 
 */
public class Matrix
{
	/**
	 * The field of the elements
	 */
	protected Field field;
	
	/**
	 * Returns the underlying field of the matrix.
	 */
	public Field getField()
	{
		return field;
	}
	
	/**
	 * The number of row.
	 */
	protected int rows;
	
	/**
	 * Returns the number of rows in this matrix.
	 */
	public int getRows()
	{
		return rows;
	}
	
	/**
	 * The number of columns.
	 */
	protected int columns;

	/**
	 * Returns the number of columns in this matrix.
	 */
	public int getColumns()
	{
		return columns;
	}

	/**
	 * The vector of entries.
	 */
	protected int[] entries;
	
	/**
	 * Constructs an empty matrix.
	 */
	public Matrix(Field field, int rows, int columns)
	{
		if( rows < 0 || columns < 0 )
			throw new IllegalArgumentException("the matrix cannot have negative number of columns or rows");

		this.field = field;
		this.rows = rows;
		this.columns = columns;
		
		entries = new int[rows * columns];
		if( field.zero() != 0 )
			Arrays.fill(entries, field.zero());
	}

	/**
	 * Creates a copy of the given matrix
	 */
	public Matrix(Matrix m)
	{
		this.field = m.field;
		this.rows = m.rows;
		this.columns = m.columns;
		this.entries = m.entries.clone();
	}

	/**
	 * Creates a unit matrix with the given dimension.
	 */
	public static Matrix unit(Field field, int dim)
	{
		Matrix m = new Matrix(field, dim, dim);
		
		int a = field.zero();
		for(int i = 0; i < dim; ++i)
			for(int j = 0; j < dim; ++j)
				m.set(i, j, a);
		
		a = field.unit();
		for(int i = 0; i < dim; ++i)
			m.set(i, i, a);

		return m;
	}

	/**
	 * Sets all entries to zero
	 */
	public void clear()
	{
		int zero = field.zero();

		for(int i = 0; i < entries.length; ++i)
			entries[i] = zero;
	}
	
	/**
	 * Returns the index of the element in
	 * the i-th row and j-th column.
	 */
	public int get(int i, int j)
	{
		return entries[i * columns + j];	
	}
	
	public Object getElem(int i, int j)
	{
		return field.getElement(get(i,j));
	}
	
	/**
	 * Replaces the element in the i-th row
	 * and j-th column with a new one.
	 */
	public void set(int i, int j, int elem)
	{
		entries[i * columns + j] = elem;
	}
	
	public void setElem(int i, int j, Object elem)
	{
		set(i, j, field.getIndex(elem));
	}
	
	/**
	 * Adds the specified element to each of the entries
	 * in the matrix.
	 * @param elem the index of the element from the 
	 * underlying field
	 */
	public void add(int elem)
	{
		int i = entries.length;
		while( --i >= 0 )
			entries[i] = field.sum(entries[i], elem);
	}
	
	/**
	 * Multiplies all entries in the matrix
	 * with the specified element.
	 * @param elem the index of the element from the 
	 * underlying field
	 */
	public void multiply(int elem)
	{
		int i = entries.length;
		while( --i >= 0 )
			entries[i] = field.product(entries[i], elem);
	}

	/**
	 * Multiplies each element of the given row
	 * with the specified element.
	 * @param row the index of the row starting from zero.
	 * @param elem the index of the element from the
	 * underlying field.
	 */
	public void multiplyRow(int row, int elem)
	{
		int s = row * columns;
		int i = s + columns;
		while( --i >= s )
			entries[i] = field.product(entries[i], elem);
	}

	/**
	 * Adds a constant multiple of a source row to the target row.
	 * @param source the source row that is added to the target
	 * @param target the target row that is modified
	 * @param the multiplier by which each source element is multiplied 
	 * with before added to the target entry.
	 */
	public void combineRows(int source, int target, int multiplier)
	{
		int s = source * columns + columns;
		int t = target * columns;
		int i = t + columns;
		while( --i >= t )
			entries[i] = field.sum(entries[i], 
				field.product(entries[--s], multiplier));
	}

	/**
	 * Adds a constant multiple of a source column to the target column.
	 * @param source the source column that is added to the target
	 * @param target the target column that is modified
	 * @param the multiplier by which each source element is multiplied 
	 * with before added to the target entry.
	 */
	public void combineColumns(int source, int target, int multiplier)
	{
		for(int i = 0; i < rows; ++i)
			set(i, target, field.sum(get(i,target), field.product(get(i,source), multiplier)));
	}

	/**
	 * Exchanges two rows in the matrix.
	 */
	public void exchangeRows(int first, int second)
	{
		first *= columns;
		second *= columns;

		int i = columns;
		while( --i >= 0 )
		{
			int t = entries[first];
			entries[first++] = entries[second];
			entries[second++] = t;
		}
	}

	/**
	 * Adds multiples of the given row to the other rows
	 * such that the only nonzero entry in the given column
	 * is at the specified position, and that entry is the unit. 
	 * The matrix must have a nonzero element at the given position.
	 * 
	 * @param row the row of the position containing the nonzero element
	 * @param column the column of the position containing the nonzero element
	 * @throws IllegalArgumentException if zero is at the given position
	 */
	public void normalizeColumn(int row, int column)
	{
		int a = get(row, column);
		if( a == field.zero() )
			throw new IllegalArgumentException("must be nonzero");

		multiplyRow(row, field.inverse(a));
		
		for(int i = 0; i < rows; ++i)
			if( i != row )
				combineRows(row, i, field.negative(get(i, column)));
	}

	/**
	 * Adds multiples of the given row to the other rows
	 * such that the only nonzero entry in the given column
	 * is at the specified position. 
	 * The matrix must have a nonzero element at the given position.
	 * 
	 * @param row the row of the position containing the nonzero element
	 * @param column the column of the position containing the nonzero element
	 * @throws IllegalArgumentException if zero is at the given position
	 */
	public void preNormalizeColumn(int row, int column)
	{
		int a = get(row, column);
		if( a == field.zero() )
			throw new IllegalArgumentException("must be nonzero");

		a = field.inverse(a);
		
		for(int i = 0; i < rows; ++i)
			if( i != row )
				combineRows(row, i, field.negative(field.product(get(i, column),a)));
	}
	
	/**
	 * Transforms the given matrix to row echelon format.
	 */
	public void doGaussElimination()
	{
		int r = 0;
		for(int c = 0; c < columns; ++c)
		{
			int i = r;
			for(; i < rows; ++i)
				if( get(i,c) != field.zero() )
					break;
			
			if( i >= rows )
				continue;
			
			if( r != i )
			{
				exchangeRows(r,i);
				multiplyRow(i, field.negative(field.unit()));
			}
		
			preNormalizeColumn(r, c);
			
			++r;
		}
	}
	
	/**
	 * Transforms the given matrix to reduced row echelon format.
	 */
	public void doGaussJordanElimination()
	{
		int r = 0;
		for(int c = 0; c < columns; ++c)
		{
			int i = r;
			for(; i < rows; ++i)
				if( get(i,c) != field.zero() )
					break;
			
			if( i >= rows )
				continue;
			
			if( r != i )
			{
				exchangeRows(r,i);
				multiplyRow(i, field.negative(field.unit()));
			}
		
			normalizeColumn(r, c);
			
			++r;
		}
	}

	/**
	 * Prints the elements in the matrix
	 */
	public void print()
	{
		for(int i = 0; i < rows; ++i)
		{
			for(int j = 0; j < columns; ++j)
				System.out.print( field.toString(field.getElement(get(i,j))) + " ");
			
			System.out.println();
		}
	}
	
	/**
	 * Prints the elements in the matrix
	 */
	public String toLatexString()
	{
		String s = "\\begin{pmatrix}";
		
		for(int i = 0; i < rows; ++i)
		{
			if( i != 0 )
				s += " \\\\";
			
			for(int j = 0; j < columns; ++j)
			{
				if( j != 0 )
					s += " &";
				
				s += " " + field.toString(field.getElement(get(i,j)));
			}
		}
		
		return s + " \\end{pmatrix}";
	}
	
	/**
	 * Returns the transpose of this matrix
	 */
	public Matrix transpose()
	{
		Matrix m = new Matrix(field, columns, rows);

		for(int i = 0; i < rows; ++i)
			for(int j = 0; j < columns; ++j)
				m.set(j,i,get(i,j));

		return m;
	}

	/**
	 * Returns the product of this matrix with the given matrix
	 */
	public Matrix product(Matrix m)
	{
		if( columns != m.rows )
			throw new IllegalArgumentException("the dimensions of two matrices are not compatible");
		
		if( ! field.equals(m.field) )
			throw new IllegalArgumentException("the underlying fields of the two matrices are not the same");

		Matrix n = new Matrix(field, rows, m.columns);
		for(int i = 0; i < rows; ++i)
			for(int j = 0; j < m.columns; ++j)
			{
				int a = field.zero();
				
				for(int k = 0; k < columns; ++k)
					a = field.sum(a, field.product(get(i,k), m.get(k,j)));
					
				n.set(i,j,a);
			}
		
		return n;
	}
	
	/**
	 * Returns the scalar product of this matrix
	 */
	public Matrix product(int scalar)
	{
		Matrix m = new Matrix(this);
		m.multiply(scalar);
		return  m;
	}
	
	/**
	 * Returns the sum of this matrix with the given matrix
	 */
	public Matrix sum(Matrix m)
	{
		if( rows != m.rows || columns != m.columns )
			throw new IllegalArgumentException("the dimensions of two matrices are not compatible");
		
		if( ! field.equals(m.field) )
			throw new IllegalArgumentException("the underlying fields of the two matrices are not the same");
			
		Matrix n = new Matrix(field, rows, m.columns);
		for(int i = 0; i < rows; ++i)
			for(int j = 0; j < m.columns; ++j)
				n.set(i, j, field.sum(get(i,j), m.get(i,j)));
		
		return n;
	}
	
	/**
	 * Returns the concatenation of this matrix with the give one, 
	 * that is putting them next to each other;
	 */
	public Matrix concatenate(Matrix m)
	{
		if( rows != m.rows )
			throw new IllegalArgumentException("the two matrices must have the same number of rows");
		
		if( ! field.equals(m.field) )
			throw new IllegalArgumentException("the underlying fields of the two matrices are not the same");

		Matrix n = new Matrix(field, rows, columns + m.columns);
		
		for(int r = 0; r < rows; ++r)
		{
			for(int c = 0; c < columns; ++c)
				n.set(r, c, get(r,c));

			for(int c = 0; c < m.columns; ++c)
				n.set(r, columns + c, m.get(r,c));
		}
		
		return n;
	}
	
	/**
	 * Returns the submatrix of this matrix starting at the
	 * (rowStart,columnStart) element up to and including the
	 * (rowEnd-1,columnEnd-1) element. 
	 */
	public Matrix subMatrix(int rowStart, int rowEnd, int columnStart, int columnEnd)
	{
		Matrix n = new Matrix(field, rowEnd-rowStart, columnEnd-columnStart);
		
		for(int i = 0; i < n.rows; ++i)
			for(int j = 0; j < n.columns; ++j)
				n.set(i,j, get(rowStart+i, columnStart+j));
		
		return n;
	}

	/**
	 * Returns the inverse of the given matrix, or <code>null</code>
	 * if the matrix has no inverse.
	 */
	public Matrix inverse()
	{
		if( rows != columns )
			throw new IllegalArgumentException("only a square matrix can have an inverse");
		
		Matrix m = concatenate(Matrix.unit(field, rows));
		
		m.doGaussJordanElimination();
		
		for(int i = 0; i < rows; ++i)
			if( m.get(i, i) != field.unit() )
				return null;
		
		return m.subMatrix(0, rows, rows, rows+rows);
	}
	
	public boolean equals(Object o)
	{
		Matrix m = (Matrix)o;
		
		if( m.field != field || rows != m.rows || columns != m.columns )
			throw new IllegalArgumentException();

		for(int i = 0; i < rows; ++i)
			for(int j = 0; j < columns; ++j)
				if( get(i,j) != m.get(i,j) )
					return false;

		return true;
	}
	
	public int hashCode()
	{
		return Arrays.hashCode(entries);
	}
}
