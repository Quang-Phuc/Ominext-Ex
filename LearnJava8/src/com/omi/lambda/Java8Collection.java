package com.omi.lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Java8Collection {
	public static void main(String[] args) {
		Java8Collection java8Collection = new Java8Collection();
		System.out.println("Cac phan tu trong mang :");
		java8Collection.addCollection();
		System.out.println("Mang sau khi sap xep:");
		java8Collection.sortCollection();
		
		String FilterName= "A";
		System.out.println("Filter name :"+FilterName);
		System.out.println("KQ :");
		java8Collection.filterCollection(FilterName);
		System.out.println("=================");
		String name ="B";
		System.out.println("Kiem tra ton tai "+ name);
		
		if(java8Collection.idExists(name)==true)
		{
			System.out.println("Co ton tai "+name);
		}
		else
		{
			System.out.println("Khong ton tai "+name);
		}
		
		java8Collection.exToUpperCase();
		java8Collection.editList();
	}
	// Add collection
	List<String> list = Arrays.asList("B", "A", "D", "C","A","a","d");
	List<Integer> listIn =Arrays.asList(1,4,2,5);
	

	/*
	 * View Collecttion
	 */
	public void addCollection() {
		
		//list.forEach(l-> System.out.println(l));
		list.forEach(System.out::println);
	}

	/*
	 * Sort Collecttion
	 */
	public void sortCollection() {
		Collections.sort(list, (String o1, String o2) -> o1.compareTo(o2));
		for (String l : list) {
			System.out.println(l);
		}
	}

	/*
	 * Filter Collecttion
	 */
	public void filterCollection(String nameFilter) {
		int x =0;
		
		List<String> filter = list.stream().filter(l -> nameFilter.equals(l)).collect(Collectors.toList());
		x =x ++;
		
		filter.forEach(System.out::println);
		System.out.println(x);
		
	}
	/*Check isExit Collection */
	boolean idExists(String name)
	{
	return  list.stream().anyMatch(t -> "A".equals(name));
	}
	public void exToUpperCase()
	{
		List<String> listToUpper = list.stream().map(String::toUpperCase).collect(Collectors.toList());
		System.out.println(listToUpper);
	}
	public void editList()
	{
		List<Integer> listIntEd = listIn.stream().map(n-> n*2).collect(Collectors.toList());
		System.out.println(listIntEd);
	}
}
