package com.omi.lambda.ex;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ActionStudent {
	Student student = new Student(1,"Nguyen Van A","123");
	Student student2 = new Student(2,"Nguyen Van C","123");
	Student student3 = new Student(3,"Nguyen Van E","123");
	Student student4 = new Student(4,"Nguyen Van D","123");
	Student[] student5 = {student,student2,student3,student4};
	
	List<Student> listStudent = Arrays.asList(student5);
	public static void main(String[] args) {
		ActionStudent actionStudent = new ActionStudent();
		actionStudent.viewStudent();
		System.out.println("======Sap xep=======");
		actionStudent.sortStudent();
		System.out.println("=============");
		actionStudent.filterStudent("Nguyen Van A");
		System.out.println("=============");
		
		if(actionStudent.idExists("Nguyen Van aA"))
			System.out.println("Co ton tai");
		else
		System.out.println("Khong ton tai");
		
		System.out.println("=============");
		actionStudent.editListStuden();
	}
	public void viewStudent()
	{
		listStudent.stream().forEach(System.out::println);
	}
	
	
	public void sortStudent()
	{
		
		listStudent.sort((Student s1,Student s2)-> s1.getName().compareTo(s2.getName()));
		listStudent.forEach(System.out::println);
	}
	
	public void filterStudent(String name)
	{
		List<Student> lStudentFi  =listStudent.stream().filter(stu ->name.equals(stu.getName())).collect(Collectors.toList());
		lStudentFi.forEach(System.out::println);
	}
	public boolean idExists(String name)
	{
		return listStudent.stream().anyMatch(t -> name.equals(t.getName()));
	}
	public void editListStuden()
	{
		List<Integer> listStudentId =listStudent.stream().map(n-> n.getId()*2).collect(Collectors.toList());
		System.out.println(listStudentId);
		
	}
	

}
