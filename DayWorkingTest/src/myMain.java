import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class myMain {
	public static void main(String[] args) {
		myMain myMain = new myMain();
		myMain.listStudents();

	}
	public void listStudents()
	{
		Student student11 = new Student("3", "11h", "20191025");
		Student student1 = new Student("1", "8h", "20191023");
		Student student2 = new Student("1", "9h", "20191025");
		Student student12 = new Student("1", "10h", "20191025");
		Student student13 = new Student("2", "11h", "20191025");
		Student student14 = new Student("2", "17h", "20191025");
		Student student21 = new Student("1", "17h", "20191025");

	String stringa= "20191025";
	int a =stringa.trim().length();
	System.out.println(a);
		
		  List<Student> students =
		  Arrays.asList(student11,student1,student2,student12,student13,student14); List<Student>
		  students2=
		  students.stream().filter(x->stringa.equalsIgnoreCase(x.getTargetDate())).
		  collect(Collectors.toList()); students2 = students2.stream()
		  .sorted(Comparator.comparing(Student::getId)).collect(Collectors.toList());
		  students2.forEach(i->System.out.println(i));
		 
		 
	}

}

