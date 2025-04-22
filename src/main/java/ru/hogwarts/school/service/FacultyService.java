package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    @Autowired
    FacultyService(FacultyRepository facultyRepository){
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty){
        return facultyRepository.save(faculty);
    }

    public List<Faculty> getAll(){
        List<Faculty> faculties = facultyRepository.findAll();
        System.out.println("Найдено факультетов: " + faculties.size());
        return faculties;
    }

    public Optional<Faculty> findById(Long id){
        return facultyRepository.findById(id);
    }

    public void deleteById(Long id){
        facultyRepository.deleteById(id);
    }

    public Faculty updateById(Long id, Faculty faculty){
        return facultyRepository.findById(id)
                .map(existingFaculty -> {
                    existingFaculty.setName(faculty.getName());
                    return facultyRepository.save(existingFaculty);
                })
                .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + id));
    }
}
