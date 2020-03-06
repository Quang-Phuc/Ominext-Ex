package jp.drjoy.backend.registration.domain.model;

import jp.drjoy.core.autogen.grpc.registration.StudentResponse;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Student")
public class Student {
    @Id
    private String id;

    private String name;

    private int age;

    private String address;

    private float gpa;

    public Student(String name, int age, String address, float gpa) {

        this.name = name;
        this.age = age;
        this.address = address;
        this.gpa = gpa;
    }


    public Student(StudentResponse studentResponse) {
        this.name = studentResponse.getName();
        this.age = studentResponse.getAge();
        this.address = studentResponse.getAddress();
        this.gpa = studentResponse.getGpa();
    }

    public StudentResponse asStudentResponse() {
        return  StudentResponse.newBuilder().setName(this.name).setAge(this.age).setGpa(this.gpa).build();
    }

}
