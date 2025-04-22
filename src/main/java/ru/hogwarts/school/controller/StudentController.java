package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
public class StudentController {
    private final StudentService studentService;

    @Autowired
    StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @GetMapping("students/")
    public ResponseEntity<List<Student>> getAllStudents(){
        List<Student> students = studentService.getAll();
        return ResponseEntity.ok(students);
    }

    @GetMapping("student/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id){
        try{
            Student student = studentService.getById(id).orElseThrow(() -> new RuntimeException("Student with id: " + id
                    + " not found"));
            return ResponseEntity.ok(student);
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create_student")
    public ResponseEntity<Student> createStudent(@RequestBody Student newStudent){
        Student student = studentService.createStudent(newStudent);
        return ResponseEntity.ok(student);
    }

    @PutMapping("/update_student/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student newStudent){
        try{
            Student student = studentService.updateStudent(id, newStudent);
            return ResponseEntity.ok(student);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete_student/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id){
        try {
            studentService.deleteStudentById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
