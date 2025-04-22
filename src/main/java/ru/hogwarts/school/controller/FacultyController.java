package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

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
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id){
        try{
            facultyService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
}
