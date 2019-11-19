
public class Student {
String id;
String checktAt;
String targetDate;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getChecktAt() {
	return checktAt;
}
public void setChecktAt(String checktAt) {
	this.checktAt = checktAt;
}
public String getTargetDate() {
	return targetDate;
}
public void setTargetDate(String targetDate) {
	this.targetDate = targetDate;
}
public Student(String id, String checktAt, String targetDate) {
	super();
	this.id = id;
	this.checktAt = checktAt;
	this.targetDate = targetDate;
}
public Student() {
	super();
	
}
@Override
public String toString() {
	return "Student [id=" + id + ", checktAt=" + checktAt + ", targetDate=" + targetDate + "]";
}

}
