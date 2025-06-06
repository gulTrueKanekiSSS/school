package ru.hogwarts.school;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private StudentRepository studentRepository;

    @Test
    void getStudentById_ReturnsStudent() throws Exception {
        Student student = new Student(1L, "Harry", 15);
        Mockito.when(studentService.getById(1L))
                .thenReturn(Optional.of(student));

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Harry"));
    }

    @Test
    void createStudent_ReturnsStudentResponseDto() throws Exception {
        StudentDto dto = new StudentDto();
        dto.setName("Hermione");
        dto.setAge(14);
        dto.setFacultyId(2L);

        Faculty faculty = new Faculty(2L, "Gryffindor");

        Student savedStudent = new Student(10L, "Hermione", 14);
        savedStudent.setFaculty(faculty);

        Mockito.when(studentService.createStudent(any())).thenReturn(savedStudent);

        mockMvc.perform(post("/create_student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hermione"))
                .andExpect(jsonPath("$.facultyName").value("Gryffindor"));
    }

    @Test
    void updateStudent_ReturnsUpdatedStudent() throws Exception {
        StudentDto dto = new StudentDto();
        dto.setName("Ron");
        dto.setAge(15);

        Student updated = new Student(1L, "Ron", 15);
        updated.setFaculty(new Faculty(2L, "Gryffindor"));

        Mockito.when(studentService.updateStudent(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/update_student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ron"))
                .andExpect(jsonPath("$.facultyName").value("Gryffindor"));
    }

    @Test
    void deleteStudent_ReturnsOk() throws Exception {
        Mockito.doNothing().when(studentService).deleteStudentById(1L);

        mockMvc.perform(delete("/delete_student/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllStudents_ReturnsList() throws Exception {
        Student student1 = new Student(1L, "Harry", 15);
        Student student2 = new Student(2L, "Hermione", 14);
        Mockito.when(studentService.getAll()).thenReturn(List.of(student1, student2));

        mockMvc.perform(get("/students/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void findByAgeBetween_ReturnsOk() throws Exception {
        Mockito.when(studentService.getStudentsByAgeBetween(10, 20)).thenReturn(List.of());

        mockMvc.perform(get("/students/get_by_age_between/")
                        .param("minAge", "10")
                        .param("maxAge", "20"))
                .andExpect(status().isOk());
    }

    @Test
    void getStudentFacultyById_ReturnsFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Hufflepuff");
        Student student = new Student(1L, "Cedric", 17);
        student.setFaculty(faculty);

        Mockito.when(studentService.getById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hufflepuff"));
    }
}
