package com.omi.lambda.ex;

public class Student {
	private int id;
	private String name;
	private String passWord;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", passWord=" + passWord + "]";
	}
	public Student(int id, String name, String passWord) {
		super();
		this.id = id;
		this.name = name;
		this.passWord = passWord;
	}
	public Student() {
		super();
	}
	

}
