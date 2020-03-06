package jp.drjoy.backend.registration.domain;

import jp.drjoy.backend.registration.domain.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface StudentRepository extends MongoRepository<Student, String> {
    @Query("{'_id': ?0}")
    Student findOne(String id);

    List<Student> findByOrderByNameAsc();

    List<Student> findByOrderByGpaAsc();
}
