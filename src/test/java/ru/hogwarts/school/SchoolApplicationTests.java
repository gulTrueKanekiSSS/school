package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
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
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

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

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	FacultyRepository facultyRepository;

	@BeforeEach
	void cleanDb() {
		studentRepository.deleteAll();
		facultyRepository.deleteAll();
	}

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
	void testGetFaculty() {
		Faculty saved = facultyRepository.save(new Faculty(null, "TestFaculty"));
		Long id = saved.getId();

		ResponseEntity<Faculty> resp = restTemplate.getForEntity(
				"http://localhost:" + port + "/faculty/{id}",
				Faculty.class,
				id
		);

		assertEquals(HttpStatus.OK, resp.getStatusCode());
		Faculty body = resp.getBody();
		assertNotNull(body, "Тело ответа должно быть не null");
		assertEquals(id, body.getId());
		assertEquals("TestFaculty", body.getName());
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
		Student saved = studentRepository.save(new Student(null, "TestName", 99));
		Long id = saved.getId();
		String url = "http://localhost:" + port + "/student/{id}";
		ResponseEntity<Student> response = restTemplate.getForEntity(url, Student.class, id);

		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		Student body = response.getBody();
		Assertions.assertThat(body).isNotNull();
		Assertions.assertThat(body.getId()).isEqualTo(id);
		Assertions.assertThat(body.getName()).isEqualTo("TestName");
		Assertions.assertThat(body.getAge()).isEqualTo(99);
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
	void testUpdateStudent() {
		Faculty savedFaculty = facultyRepository
				.save(new Faculty(null, "Test Faculty"));

		StudentDto dto = new StudentDto();
		dto.setName("Initial Name");
		dto.setAge(13);
		dto.setFacultyId(savedFaculty.getId());

		ResponseEntity<StudentResponseDto> createResp = restTemplate
				.postForEntity(
						"http://localhost:" + port + "/create_student",
						dto,
						StudentResponseDto.class
				);
		assertEquals(HttpStatus.OK, createResp.getStatusCode());
		StudentResponseDto created = createResp.getBody();
		assertNotNull(created);
		Long studentId = created.getId();

		StudentDto updateDto = new StudentDto();
		updateDto.setName("Updated Name");
		updateDto.setAge(22);
		updateDto.setFacultyId(savedFaculty.getId());

		HttpEntity<StudentDto> request = new HttpEntity<>(updateDto);
		String url = "http://localhost:" + port + "/update_student/{id}";

		ResponseEntity<StudentResponseDto> updateResp = restTemplate
				.exchange(
						url,
						HttpMethod.PUT,
						request,
						StudentResponseDto.class,
						studentId
				);
		assertEquals(HttpStatus.OK, updateResp.getStatusCode());
		StudentResponseDto updated = updateResp.getBody();
		assertNotNull(updated);

		assertEquals(studentId, updated.getId());
		assertEquals("Updated Name", updated.getName());
		assertEquals(22, updated.getAge());
		assertEquals("Test Faculty", updated.getFacultyName());
	}

	@Test
	void testDeleteStudent() throws Exception {
		// 1) Сохраняем факультет
		Faculty savedFaculty = facultyRepository.save(new Faculty(null, "TestDept"));

		// 2) Подготавливаем DTO для создания
		StudentDto createDto = new StudentDto();
		createDto.setName("Initial Name");
		createDto.setAge(12);
		createDto.setFacultyId(savedFaculty.getId());

		// 3) Создаём студента через контроллер и читаем ответ как StudentResponseDto
		ResponseEntity<StudentResponseDto> createResponse = restTemplate.postForEntity(
				"http://localhost:" + port + "/create_student",
				createDto,
				StudentResponseDto.class
		);
		assertEquals(HttpStatus.OK, createResponse.getStatusCode());
		StudentResponseDto created = createResponse.getBody();
		assertNotNull(created);
		Long studentId = created.getId();

		// 4) Удаляем по id
		restTemplate.delete("http://localhost:" + port + "/delete_student/{id}", studentId);

		// 5) Проверяем, что GET /student/{id} теперь возвращает 404
		ResponseEntity<String> getResponse = restTemplate.getForEntity(
				"http://localhost:" + port + "/student/{id}",
				String.class,
				studentId
		);
		assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
	}
}
