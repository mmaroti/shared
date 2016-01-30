package mmaroti.ua.alg;

/**
 * Copyright (C) 2000 Miklos Maroti
 */

public interface Function {
	public int arity();

	public int size();

	public int value(int[] args);
}
