package com.omi.lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class MainLambda {
	public static void main(String[] args) {

		MainLambda mainLambda = new MainLambda();

		System.out.println(mainLambda.isPrime(10));

		mainLambda.view();

		System.out.println(mainLambda.isPrime2(3));
		
		
		mainLambda.ArrayInJava8();

	}

	public boolean isPrime(int number) {
		return number > 1 && IntStream.range(2, number).noneMatch(index -> number % index == 0);
		// Neu number > 1 && number / index ==0 => return true

	}

	public void view() {
		IntStream stream = IntStream.range(4, 9);
		stream.forEach(System.out::println);
	}

	private static boolean isPrime2(int number) {
		IntPredicate isDivisible = index -> number % index == 0;

		return number > 1 && IntStream.range(2, number).noneMatch(isDivisible);
	}

	public void ArrayInJava8()
	{
		  List<String> ls = Arrays.asList("Grapefruit", "Apple", "Durian", "Cherry"); // add values in Array used Java 8
		
		  // sort cac phan tu trong mang
		  Collections.sort(ls, (String o1, String o2) -> o1.compareTo(o2)  );
	 
		  // Duyet phan tu mang
		  for (String l : ls) {
	            System.out.println(l);
	        }
	}

}
