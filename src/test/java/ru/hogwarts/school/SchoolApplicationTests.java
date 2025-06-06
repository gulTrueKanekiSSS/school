package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SchoolApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private StudentController studentController;

	@Autowired
	private FacultyController facultyController;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void contextLoads() throws Exception {
		Assertions.assertThat(studentController).isNotNull();
	}

	@Test
	public void testGetfaculties() throws Exception {
		Assertions
				.assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/faculties", String.class))
				.isNotNull();
	}

	@Test
	public void testGetfaculty() throws Exception {
		Assertions
				.assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/faculty/" + "102", String.class))
				.isNotNull();
	}

	@Test
	public void testPostFaculty() throws Exception {
		Faculty faculty = new Faculty();
		faculty.setName("test");

		Assertions
				.assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/create_faculty", faculty, String.class))
				.isNotNull();
	}

	@Test
	public void testUpdateFaculty() throws Exception {
		Faculty faculty = new Faculty();
		faculty.setName("Initial Name");

		ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
				"http://localhost:" + port + "/create_faculty",
				faculty,
				Faculty.class
		);

		assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
		Faculty createdFaculty = createResponse.getBody();
		assertNotNull(createdFaculty);
		Long id = createdFaculty.getId();

		createdFaculty.setName("Updated Name");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Faculty> updateRequest = new HttpEntity<>(createdFaculty, headers);

		String url = "http://localhost:" + port + "/update_faculty/" + id;
		ResponseEntity<String> updateResponse = restTemplate.exchange(url, HttpMethod.PUT, updateRequest, String.class);

		assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
	}

	@Test
	public void testDeleteFaculty() throws Exception {
		Faculty faculty = new Faculty();
		faculty.setName("Initial Name");

		ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
				"http://localhost:" + port + "/create_faculty",
				faculty,
				Faculty.class
		);

		assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
		Faculty createdFaculty = createResponse.getBody();
		assertNotNull(createdFaculty);
		Long id = createdFaculty.getId();

		createdFaculty.setName("Updated Name");

		restTemplate.delete("http://localhost:" + port + "/delete_faculty/" + id);

		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/faculty/" + id, String.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void testGetStudents(){
		Assertions.assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/students/", String.class))
				.isNotNull();
	}

	@Test
	public void testGetStudent() throws Exception {
		Assertions
				.assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student/" + "102", String.class))
				.isNotNull();
	}

	@Test
	public void testPostStudent() throws Exception {
		StudentDto student = new StudentDto();
		student.setName("test");
		student.setAge(18);
		student.setFacultyId(102L);

		Assertions
				.assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/create_student", student, String.class))
				.isNotNull();
	}

	@Test
	public void testUpdateStudent() throws Exception {
		StudentDto student = new StudentDto();
		student.setName("Initial Name");
		student.setAge(13);
		student.setFacultyId(102L);

		ResponseEntity<StudentDto> createResponse = restTemplate.postForEntity(
				"http://localhost:" + port + "/create_student",
				student,
				StudentDto.class
		);

		assertEquals(HttpStatus.OK, createResponse.getStatusCode());
		StudentDto createdStudent = createResponse.getBody();
		assertNotNull(createdStudent);

		createdStudent.setName("Updated Name");
		createdStudent.setAge(22);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<StudentDto> updateRequest = new HttpEntity<>(createdStudent, headers);

		String url = "http://localhost:" + port + "/update_student/" + 2;
		ResponseEntity<String> updateResponse = restTemplate.exchange(url, HttpMethod.PUT, updateRequest, String.class);

		assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
	}
	@Test
	public void testDeleteStudent() throws Exception {
		StudentDto student = new StudentDto();
		student.setName("Initial Name");
		student.setAge(12);
		student.setFacultyId(102L);

		ResponseEntity<Student> createResponse = restTemplate.postForEntity(
				"http://localhost:" + port + "/create_student",
				student,
				Student.class
		);

		assertEquals(HttpStatus.OK, createResponse.getStatusCode());
		Student createdStudent = createResponse.getBody();
		assertNotNull(createdStudent);
		Long id = createdStudent.getId();

		createdStudent.setName("Updated Name");

		restTemplate.delete("http://localhost:" + port + "/delete_student/" + id);

		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/student/" + id, String.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

}
