package org.robinbird.test;

public class TestClass {

	String name;

	public static <T> int testFn(int a, TestClass2 tc, float b) {
		String abc = "test";
		int c = a*b;
		tc.testFn();
		return c;
	}

	public void fn1() {}

	public void fn2(float fs, int a) {}

	public String fn3(int a, char c, String... strs) {
		return "merong";
	}

}