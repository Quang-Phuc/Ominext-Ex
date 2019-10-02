package com.omi.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class Java8Stream implements HelloLambda{
	public static void main(String[] args) {
		Java8Stream stream8 = new Java8Stream();
		stream8.ExStream();
	}
	List<String> list = Arrays.asList("a","b","ab","bc");
	
	public void ExStream()
	{
		list.stream().filter(l -> "abd".equals(l)).forEach(System.out::println);
	}
	
	
	@Override
	public String sayHello3(String name, String password) {
		/* HelloLambda helloLambda = (String a,String b) -> name + password; */
		int number=10	;
		 IntPredicate isDivisible = index -> number % index == 0;
		return null;
		
	}
	public int Sumnumber()
	{/*
		 * IntStream.range(3, 10).sum
		 */
		return 10;
	}

}
