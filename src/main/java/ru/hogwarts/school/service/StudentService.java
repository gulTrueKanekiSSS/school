package ru.hogwarts.school.service;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;


    @Autowired
    StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository){
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    public List<Student> getAll(){
        return studentRepository.findAll();
    }

    public Optional<Student> getById(Long id){
        return studentRepository.findById(id);
    }

    public Student createStudent(StudentDto dto){
        Faculty faculty = facultyRepository.findById(dto.getFacultyId())
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        Student student = new Student();
        student.setName(dto.getName());
        student.setAge(dto.getAge());
        student.setFaculty(faculty);

        return studentRepository.save(student);
    }


    public void deleteStudentById(Long id){
        studentRepository.deleteById(id);
    }

    public Student updateStudent(Long id, Student newStudent){
        return  studentRepository.findById(id)
                .map(existingStudent -> {
                    existingStudent.setName(newStudent.getName());
                    existingStudent.setAge(newStudent.getAge());
                    return studentRepository.save(existingStudent);
                })
                .orElseThrow(() -> new RuntimeException("Student with id: " + id + " not found"));
    }

    public Collection<Student> getStudentsByAgeBetween(int min_age, int max_age){
        return studentRepository.findByAgeBetween(min_age, max_age);
    }
}
