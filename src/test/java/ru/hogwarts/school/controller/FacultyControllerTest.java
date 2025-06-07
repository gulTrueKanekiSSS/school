package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getFacultyById_ShouldReturnFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Gryffindor");
        Mockito.when(facultyService.findById(1L)).thenReturn(Optional.of(faculty));

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"));
    }

    @Test
    void getFacultyById_ShouldReturnNotFound() throws Exception {
        Mockito.when(facultyService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllFaculties_ShouldReturnList() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "Gryffindor"),
                new Faculty(2L, "Slytherin")
        );
        Mockito.when(facultyService.getAll()).thenReturn(faculties);

        mockMvc.perform(get("/faculties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void createFaculty_ShouldReturnCreatedFaculty() throws Exception {
        Faculty faculty = new Faculty(null, "Hufflepuff");
        Faculty savedFaculty = new Faculty(1L, "Hufflepuff");

        Mockito.when(facultyService.createFaculty(Mockito.any(Faculty.class)))
                .thenReturn(savedFaculty);

        mockMvc.perform(post("/create_faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Hufflepuff"));
    }

    @Test
    void updateFaculty_ShouldReturnUpdatedFaculty() throws Exception {
        Faculty updated = new Faculty(1L, "Ravenclaw");

        Mockito.when(facultyService.updateById(Mockito.eq(1L), Mockito.any(Faculty.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/update_faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ravenclaw"));
    }

    @Test
    void updateFaculty_ShouldReturnNotFound() throws Exception {
        Mockito.when(facultyService.updateById(Mockito.eq(1L), Mockito.any()))
                .thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(put("/update_faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Faculty(null, "Invalid"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFaculty_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/delete_faculty/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFaculty_ShouldReturnNotFound() throws Exception {
        Mockito.doThrow(new RuntimeException("Not found"))
                .when(facultyService).deleteById(1L);

        mockMvc.perform(delete("/delete_faculty/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByNameLike_ShouldReturnMatchingFaculties() throws Exception {
        List<Faculty> faculties = List.of(new Faculty(1L, "Slytherin"));
        Mockito.when(facultyService.findByNameLike("sly"))
                .thenReturn(faculties);

        mockMvc.perform(get("/faculty/request/sly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Slytherin"));
    }

    @Test
    void getStudentsInFaculty_ShouldReturnStudentList() throws Exception {
        Student student = new Student(1L, "Harry", 15);
        Faculty faculty = new Faculty(1L, "Gryffindor");
        faculty.setId(1L);
        faculty.getStudents().add(student);

        Mockito.when(facultyService.findById(1L))
                .thenReturn(Optional.of(faculty));

        mockMvc.perform(get("/faculty/1/students"))
                .andExpect(status().isOk());
    }

    @Test
    void getStudentsInFaculty_ShouldReturnNotFound() throws Exception {
        Mockito.when(facultyService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/faculty/1/students"))
                .andExpect(status().isNotFound());
    }
}
