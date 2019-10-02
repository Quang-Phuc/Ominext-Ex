package com.omi.lambda;

public class LambdaExpressionExample1 {
public static void main(String[] args) {
		/*
		 * Drawable drawable = (name,password) -> { System.out.println("A"); };
		 * drawable.draw("A","B");
		 */
	Drawable drawable =(a,b)->
	{
		return a +b;
	};
	System.out.println();
}
}
