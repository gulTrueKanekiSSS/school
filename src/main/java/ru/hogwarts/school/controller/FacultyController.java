package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class FacultyController {
    private final FacultyService facultyService;

    @Autowired
    FacultyController(FacultyService facultySerivce){
        this.facultyService = facultySerivce;
    }

    @GetMapping("/faculty/{id}")
    public ResponseEntity<Faculty> getFacultyById(@PathVariable Long id){
        try {
            Faculty faculty = facultyService.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
            return ResponseEntity.ok(faculty);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/faculties")
    public ResponseEntity<List<Faculty>> getFaculty(){
        List<Faculty> faculties = facultyService.getAll();
        return ResponseEntity.ok(faculties);
    }

    @PostMapping("/create_faculty")
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty){
        Faculty created = facultyService.createFaculty(faculty);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update_faculty/{id}")
    public ResponseEntity<Faculty> updateFaculty(@PathVariable Long id, @RequestBody Faculty updatedFaculty){
        try{
            Faculty faculty = facultyService.updateById(id, updatedFaculty);
            return ResponseEntity.ok(faculty);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete_faculty/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        try {
            facultyService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/faculty/request/{request}")
    public ResponseEntity<Collection<Faculty>> findByNameLike(@PathVariable String request){
        try {
            Collection<Faculty> faculties = facultyService.findByNameLike(request);
            return ResponseEntity.ok(faculties);
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/faculty/{id}/students")
    public ResponseEntity<Collection<Student>> getStudentsInFaculty(@PathVariable Long id) {
        Optional<Faculty> facultyOpt = facultyService.findById(id);
        if (facultyOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Faculty faculty = facultyOpt.get();
        List<Student> students = (List<Student>) faculty.getStudents();
        return ResponseEntity.ok(students != null ? students : Collections.emptyList());
    }

}
