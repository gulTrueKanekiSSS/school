package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class FacultyControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    private Faculty f1;
    private Faculty f2;
    private Student s1;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();

        f1 = new Faculty(null, "Gryffindor");
        f2 = new Faculty(null, "Slytherin");
        facultyRepository.saveAll(List.of(f1, f2));

        // для теста /faculty/{id}/students
        s1 = new Student(null, "Harry", 15);
        s1.setFaculty(f1);
        studentRepository.save(s1);
    }

    @Test
    void getAllFaculties_ReturnsTwo() {
        ResponseEntity<Faculty[]> resp = restTemplate.getForEntity(
                "/faculties", Faculty[].class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty[] arr = resp.getBody();
        assertThat(arr).hasSize(2);
        assertThat(arr)
                .extracting(Faculty::getName)
                .containsExactlyInAnyOrder("Gryffindor", "Slytherin");
    }

    @Test
    void getFacultyById_ReturnsCorrect() {
        Long id = facultyRepository.findAll().get(0).getId();
        ResponseEntity<Faculty> resp = restTemplate.getForEntity(
                "/faculty/{id}", Faculty.class, id);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getName()).isEqualTo(facultyRepository.findById(id).get().getName());
    }

    @Test
    void getFacultyById_NotFound() {
        ResponseEntity<Faculty> resp = restTemplate.getForEntity(
                "/faculty/{id}", Faculty.class, 999L);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createFaculty_ReturnsCreated() {
        Faculty newF = new Faculty(null, "Hufflepuff");
        ResponseEntity<Faculty> resp = restTemplate.postForEntity(
                "/create_faculty", newF, Faculty.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Faculty body = resp.getBody();
        assertThat(body.getId()).isNotNull();
        assertThat(body.getName()).isEqualTo("Hufflepuff");
    }

    @Test
    void updateFaculty_ReturnsUpdated() {
        Faculty toUpdate = facultyRepository.findAll().get(0);
        toUpdate.setName("Ravenclaw");

        HttpEntity<Faculty> ent = new HttpEntity<>(toUpdate);
        ResponseEntity<Faculty> resp = restTemplate.exchange(
                "/update_faculty/{id}", HttpMethod.PUT, ent, Faculty.class, toUpdate.getId());

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getName()).isEqualTo("Ravenclaw");
    }

    @Test
    void updateFaculty_NotFound() {
        Faculty fake = new Faculty(null, "Nope");
        HttpEntity<Faculty> ent = new HttpEntity<>(fake);
        ResponseEntity<Faculty> resp = restTemplate.exchange(
                "/update_faculty/{id}", HttpMethod.PUT, ent, Faculty.class, 999L);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteFaculty_ReturnsOkAndDeletes() {
        Long id = facultyRepository.findAll().get(0).getId();
        ResponseEntity<Void> resp = restTemplate.exchange(
                "/delete_faculty/{id}", HttpMethod.DELETE, null, Void.class, id);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(facultyRepository.existsById(id)).isFalse();
    }

    @Test
    void deleteFaculty_NotFound() {
        ResponseEntity<Void> resp = restTemplate.exchange(
                "/delete_faculty/{id}", HttpMethod.DELETE, null, Void.class, 999L);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    void findByNameLike_ReturnsMatches() {
        String fragment = "gry";
        ResponseEntity<Faculty[]> resp = restTemplate.getForEntity(
                "/faculty/request/{frag}", Faculty[].class, fragment);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty[] arr = resp.getBody();
        assertThat(arr).hasSize(1);
        assertThat(arr[0].getName()).isEqualTo("Gryffindor");
    }

    @Test
    void getStudentsInFaculty_ReturnsStudents() {
        ResponseEntity<Student[]> resp = restTemplate.getForEntity(
                "/faculty/{id}/students", Student[].class, f1.getId());

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student[] arr = resp.getBody();
        assertThat(arr).hasSize(1);
        assertThat(arr[0].getName()).isEqualTo("Harry");
    }

    @Test
    void getStudentsInFaculty_NotFound() {
        ResponseEntity<Student[]> resp = restTemplate.getForEntity(
                "/faculty/{id}/students", Student[].class, 999L);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
