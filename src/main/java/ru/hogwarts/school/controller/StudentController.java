package ru.hogwarts.school.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public ResponseEntity<StudentResponseDto> updateStudent(@PathVariable Long id, @RequestBody StudentDto newStudentDto){
        try {
            Student updatedStudent = studentService.updateStudent(id, newStudentDto);

            StudentResponseDto response = new StudentResponseDto();
            response.setId(updatedStudent.getId());
            response.setName(updatedStudent.getName());
            response.setAge(updatedStudent.getAge());
            response.setFacultyName(updatedStudent.getFaculty() != null ? updatedStudent.getFaculty().getName() : null);

            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
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

    @GetMapping("amount_students/")
    public int get_amount_students(){
        return studentService.getAll().size();
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id, @RequestParam MultipartFile avatar) throws IOException {
        if (avatar.getSize() > 1024 * 300) {
            return ResponseEntity.badRequest().body("File is too big");
        }

        studentService.uploadAvatar(id, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/avatar/preview")
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable Long id) {
        Avatar avatar = studentService.findAvatar(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @GetMapping(value = "/{id}/avatar")
    public void downloadAvatar(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Avatar avatar = studentService.findAvatar(id);

        Path path = Path.of(avatar.getFilePath());

        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream();) {
            response.setStatus(200);
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            is.transferTo(os);
        }
    }
}

