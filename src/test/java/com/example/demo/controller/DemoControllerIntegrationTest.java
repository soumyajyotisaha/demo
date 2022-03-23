package com.example.demo.controller;

import com.example.demo.entity.Department;
import com.example.demo.repository.DepartmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class DemoControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    DepartmentRepository departmentRepository;

    @BeforeEach
    public void setup() {
        departmentRepository.deleteAll();
    }

    @Test
    public void shouldBeAbleToGetDepartment() throws Exception {
        Department dept = Department.builder()
                .departmentCode("DEPCO")
                .departmentName("DEP name")
                .departmentAddress("Dep address")
                .build();
        departmentRepository.save(dept);
//        departmentRepository.findB

        assert departmentRepository.count() == 1;
        mockMvc.perform(get("/departments").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].departmentName", is("DEP name")))
                .andExpect(jsonPath("$[0].departmentAddress", is("Dep address")))
                .andExpect(jsonPath("$[0].departmentCode", is("DEPCO")));

        assert departmentRepository.count() == 1;
    }

    @Test
    public void shouldBeAbleToCreateDepartment() throws Exception {
        Department dept = Department.builder()
                .departmentCode("DEPCO")
                .departmentName("DEP name")
                .departmentAddress("Dep address")
                .build();
        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(dept);

        assert departmentRepository.count() == 0;
        mockMvc.perform(post("/departments").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.departmentName", is("DEP name")))
                .andExpect(jsonPath("$.departmentAddress", is("Dep address")))
                .andExpect(jsonPath("$.departmentCode", is("DEPCO")));

        assertThat(departmentRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldBeAbleToUpdateDepartment() throws Exception {
        Department dept = Department.builder()
                .departmentCode("DEPCO")
                .departmentName("DEP name")
                .departmentAddress("Dep address")
                .build();
        Department deptAfterSave = departmentRepository.save(dept);
        Department deptUpdate = Department.builder()
                .departmentCode("DEPCO1")
                .departmentName("DEP name2")
                .departmentAddress("Dep address3")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(deptUpdate);

        assertThat(departmentRepository.count()).isEqualTo(1);
        mockMvc.perform(put("/departments/" + deptAfterSave.getDepartmentId()).contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.departmentName", is("DEP name2")))
                .andExpect(jsonPath("$.departmentAddress", is("Dep address3")))
                .andExpect(jsonPath("$.departmentCode", is("DEPCO1")));

        assertThat(departmentRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldBeAbleToDeleteDepartment() throws Exception {
        Department dept = Department.builder()
                .departmentCode("DEPCO")
                .departmentName("DEP name")
                .departmentAddress("Dep address")
                .build();
        Department deptAfterSave = departmentRepository.save(dept);
        assertThat(departmentRepository.count()).isEqualTo(1);

        mockMvc.perform(delete("/departments/" + deptAfterSave.getDepartmentId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Deleted Successfully"));

//        verify(departmentRepository, times(0)).save(any(Department.class));
        assertThat(departmentRepository.count()).isEqualTo(0);
    }
}
