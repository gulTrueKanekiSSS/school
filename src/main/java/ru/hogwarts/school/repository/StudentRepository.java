package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    public Collection<Student> findByAgeBetween(int min_age, int max_age);

    @Query(value = "SELECT COUNT(*) FROM student", nativeQuery = true)
    int getAmountStudents();

    @Query(value = "SELECT AVG(age) FROM student", nativeQuery = true)
    int getAvgAge();

    @Query(
            value = "SELECT * FROM students ORDER BY id DESC LIMIT 5",
            nativeQuery = true
    )
    List<Student> findLastFiveStudents();

}
