package mmaroti.ua.alg;

/**
 *	Copyright (C) 2000 Miklos Maroti
 */

import java.util.Arrays;
import mmaroti.ua.util.Arrays2;

public class FunctionBuffer implements Function {
	private int size;
	private int arity;
	private int buffer[];

	@Override
	public int size() {
		return size;
	}

	@Override
	public int arity() {
		return arity;
	}

	public int[] buffer() {
		return buffer;
	}

	public void fill(int b) {
		Arrays.fill(buffer, b);
	}

	public static int power(int base, int exp) {
		int a = 1;

		while (--exp >= 0)
			a *= base;

		return a;
	}

	public FunctionBuffer(int size, int arity) {
		if (size <= 0 || arity < 0)
			throw new IllegalArgumentException();

		this.size = size;
		this.arity = arity;

		buffer = new int[power(size, arity)];
	}

	public FunctionBuffer(int size, int arity, int[] buffer) {
		if (size <= 0 || arity < 0 || power(size, arity) != buffer.length)
			throw new IllegalArgumentException();

		this.size = size;
		this.arity = arity;
		this.buffer = buffer;
	}

	public FunctionBuffer(Function function) {
		if (function.size() <= 0 || function.arity() < 0)
			throw new IllegalArgumentException();

		size = function.size();
		arity = function.arity();

		buffer = new int[power(size, arity)];

		int[] args = new int[arity];
		int i;

		for (i = 0; i < arity; ++i)
			args[i] = 0;

		int index = 0;
		do {
			buffer[index++] = function.value(args);

			i = arity;
			while (--i >= 0 && ++args[i] >= size)
				args[i] = 0;

		} while (i >= 0);
	}

	@Override
	public Object clone() {
		return new FunctionBuffer(size, arity, buffer.clone());
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof FunctionBuffer))
			return false;

		FunctionBuffer f = (FunctionBuffer) o;

		return size == f.size && Arrays.equals(buffer, f.buffer);
	}

	@Override
	public int hashCode() {
		return Arrays2.hashCode(buffer);
	}

	public int index() {
		if (arity != 0)
			throw new IllegalArgumentException();

		return 0;
	}

	public int index(int args[]) {
		if (arity != args.length)
			throw new IllegalArgumentException();

		int a = 0;
		for (int i = 0; i < arity; ++i) {
			if (args[i] < 0 || args[i] >= size)
				throw new IllegalArgumentException();

			a *= size;
			a += args[i];
		}

		return a;
	}

	public int index(int a0) {
		if (arity != 1 || a0 < 0 || size <= a0)
			throw new IllegalArgumentException();

		return a0;
	}

	public int index(int a0, int a1) {
		if (arity != 2 || a0 < 0 || size <= a0 || a1 < 0 || size <= a1)
			throw new IllegalArgumentException();

		return a0 * size + a1;
	}

	public int index(int a0, int a1, int a2) {
		if (arity != 3 || a0 < 0 || size <= a0 || a1 < 0 || size <= a1
				|| a2 < 0 || size <= a2)
			throw new IllegalArgumentException();

		return (a0 * size + a1) * size + a2;
	}

	public void setByIndex(int index, int b) {
		buffer[index] = b;
	}

	public int valueByIndex(int index) {
		return buffer[index];
	}

	public void set(int b) {
		buffer[index()] = b;
	}

	public void set(int a0, int b) {
		buffer[index(a0)] = b;
	}

	public void set(int a0, int a1, int b) {
		buffer[index(a0, a1)] = b;
	}

	public void set(int a0, int a1, int a2, int b) {
		buffer[index(a0, a1, a2)] = b;
	}

	public void set(int args[], int b) {
		buffer[index(args)] = b;
	}

	public int value() {
		return buffer[index()];
	}

	public int value(int a0) {
		return buffer[index(a0)];
	}

	public int value(int a0, int a1) {
		return buffer[index(a0, a1)];
	}

	public int value(int a0, int a1, int a2) {
		return buffer[index(a0, a1, a2)];
	}

	@Override
	public int value(int args[]) {
		return buffer[index(args)];
	}
}
