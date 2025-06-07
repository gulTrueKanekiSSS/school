package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);


    @Autowired
    FacultyService(FacultyRepository facultyRepository){
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty){
        logger.info("Создаем факультет");
        return facultyRepository.save(faculty);
    }

    public List<Faculty> getAll(){
        logger.info("Работа метода getAll");
        List<Faculty> faculties = facultyRepository.findAll();
        System.out.println("Найдено факультетов: " + faculties.size());
        return faculties;
    }

    public Optional<Faculty> findById(Long id){
        logger.info("Работа метода findById");
        return facultyRepository.findById(id);
    }

    public void deleteById(Long id){
        logger.info("Работа метода deleteById");
        if (!facultyRepository.existsById(id)) {
            throw new EntityNotFoundException("Faculty not found");
        }
        facultyRepository.deleteById(id);
    }

    public Faculty updateById(Long id, Faculty faculty){
        logger.info("Работа метода updateById");
        return facultyRepository.findById(id)
                .map(existingFaculty -> {
                    existingFaculty.setName(faculty.getName());
                    return facultyRepository.save(existingFaculty);
                })
                .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + id));

    }

    public Collection<Faculty> findByNameLike(String request){
        logger.info("Работа метода findByNameLike");
        return facultyRepository.findByNameContainsIgnoreCase(request);
    }

}
