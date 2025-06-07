package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@RestController
public class DataController {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    @Autowired
    public DataController(StudentRepository studentRepository,
                          FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }


    @GetMapping("/students/names-starting-with-a")
    public List<String> getNamesStartingWithA() {
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name != null && name.startsWith("A"))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
    }


    @GetMapping("/students/average-age")
    public double getAverageAge() {
        List<Student> all = studentRepository.findAll();
        OptionalDouble average = all.stream()
                .mapToInt(Student::getAge)
                .average();
        return average.orElse(0.0);
    }

    @GetMapping("/faculties/longest-name")
    public String getLongestFacultyName() {
        return facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
    }


    @GetMapping("/compute-sum")
    public int computeSum() {
        long n = 1_000_000L;
        long longSum = n * (n + 1) / 2; // 500000500000
        return (int) longSum; // приведение к int (будет переполнение так же, как в исходном reduce)
    }

}
