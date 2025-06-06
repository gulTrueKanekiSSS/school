package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class StudentService {

    @Value("${avatars.dir.path}")
    private String avatarsDir;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AvatarRepository avatarRepository;


    @Autowired
    StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository, AvatarRepository avatarRepository){
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.avatarRepository = avatarRepository;
    }

    public List<Student> getAll(){
        logger.info("запущен метод getAll");
        return studentRepository.findAll();
    }

    public Optional<Student> getById(Long id){
        logger.info("Запущен метод getById с айди:" + id);
        return studentRepository.findById(id);
    }

    public Student createStudent(StudentDto dto){
        logger.info("Создается студент" + dto);
        Faculty faculty = facultyRepository.findById(dto.getFacultyId())
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        Student student = new Student();
        student.setName(dto.getName());
        student.setAge(dto.getAge());
        student.setFaculty(faculty);

        return studentRepository.save(student);
    }


    public void deleteStudentById(Long id){
        logger.info("Удаляем студента с айди: " + id);
        studentRepository.deleteById(id);
    }

    public Student updateStudent(Long id, StudentDto newStudent){
        logger.info("Редактируем студента");
        return  studentRepository.findById(id)
                .map(existingStudent -> {
                    existingStudent.setName(newStudent.getName());
                    existingStudent.setAge(newStudent.getAge());
                    return studentRepository.save(existingStudent);
                })
                .orElseThrow(() -> new RuntimeException("Student with id: " + id + " not found"));
    }

    public Collection<Student> getStudentsByAgeBetween(int min_age, int max_age){
        logger.info("Ищем студента в диапазоне от " + min_age + "лет, до " + max_age + " лет");
        return studentRepository.findByAgeBetween(min_age, max_age);
    }

    public int getAmountStudents(){
        logger.info("Считаем количество студентов");
        return studentRepository.getAmountStudents();
    }

    public int getAvgAgeStudents(){
        logger.info("Считаем средний возраст студентов");
        return studentRepository.getAvgAge();
    }

    public Avatar findAvatar(long studentId) {
        logger.info("Ищем аватар");
        return avatarRepository.findByStudentId(studentId).orElseThrow();
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Загружаем аватарку студенту");
        Optional<Student> student = getById(studentId);

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElseGet(Avatar::new);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        avatarRepository.save(avatar);
    }

    private String getExtension(String fileName) {
        logger.info("Работает метод getExtensions");
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}

