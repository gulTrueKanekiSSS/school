package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
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
    public ResponseEntity<StudentResponseDto> createStudent(@RequestBody StudentDto dto){
        Student student = studentService.createStudent(dto);

        StudentResponseDto response = new StudentResponseDto();
        response.setId(student.getId());
        response.setName(student.getName());
        response.setAge(student.getAge());
        response.setFacultyName(student.getFaculty().getName());

        return ResponseEntity.ok(response);
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

    @GetMapping("/students/get_by_age_between/")
    public ResponseEntity<Collection<Student>>findByAgeBetween(@RequestParam("minAge") int minAge,
                                                               @RequestParam("maxAge") int maxAge){
        studentService.getStudentsByAgeBetween(minAge, maxAge);
        return ResponseEntity.ok().build();
    }


    @GetMapping("student/{id}/faculty")
    public ResponseEntity<Faculty> getStudentFacultyById(@PathVariable Long id){
        try{
            return studentService.getById(id)
                    .map(student -> ResponseEntity.ok(student.getFaculty()))
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
}
