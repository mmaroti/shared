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

package org.mmaroti.compiler;

public class Program
{
	public static abstract class Statement
	{
		public abstract void execute(Executor executor);
	}

	public static class ConstantString extends Statement
	{
		public ConstantString(String value)
		{
			this.value = value;
		}
		
		private String value;
		
		public void execute(Executor executor)
		{
			executor.constant(value);
		}
	}
	
	public static class ConstantInteger extends Statement
	{
		public ConstantInteger(int value)
		{
			this.value = value;
		}
		
		private int value;
		
		public void execute(Executor executor)
		{
			executor.constant(value);
		}
	}
	
}
