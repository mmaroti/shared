package mmaroti.ua.alg;

/**
 * Copyright (C) 2000 Miklos Maroti
 */

public class Functions {
	protected static int[] args0 = new int[0];

	public static int value(Function f) {
		return f.value(args0);
	}

	protected static int[] args1 = new int[1];

	public static int value(Function f, int a0) {
		args1[0] = a0;
		return f.value(args1);
	}

	protected static int[] args2 = new int[2];

	public static int value(Function f, int a0, int a1) {
		args2[0] = a0;
		args2[1] = a1;
		return f.value(args2);
	}

	protected static int[] args3 = new int[3];

	public static int value(Function f, int a0, int a1, int a2) {
		args3[0] = a0;
		args3[1] = a1;
		args3[2] = a2;
		return f.value(args3);
	}

	public static class Unary {
		protected Function f;
		protected int[] args = new int[1];

		public int value(int a) {
			args[0] = a;
			return f.value(args);
		}

		public int size() {
			return f.size();
		}

		public Unary(Function f) {
			if (f.arity() != 1)
				throw new IllegalArgumentException();

			this.f = f;
		}
	}

	public static class Binary {
		protected Function f;
		protected int[] args = new int[2];

		public int value(int a, int b) {
			args[0] = a;
			args[1] = b;
			return f.value(args);
		}

		public int size() {
			return f.size();
		}

		public Binary(Function f) {
			if (f.arity() != 2)
				throw new IllegalArgumentException();

			this.f = f;
		}
	}

	public static class Ternary {
		protected Function f;
		protected int[] args = new int[3];

		public int value(int a, int b, int c) {
			args[0] = a;
			args[1] = b;
			args[2] = c;
			return f.value(args);
		}

		public int size() {
			return f.size();
		}

		public Ternary(Function f) {
			if (f.arity() != 3)
				throw new IllegalArgumentException();

			this.f = f;
		}
	}
}
