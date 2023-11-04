package dev.edvinmichovic.employeemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import dev.edvinmichovic.employeemanager.EmployeemanagerApplication;
import dev.edvinmichovic.employeemanager.exception.UserNotFoundException;
import dev.edvinmichovic.employeemanager.model.Employee;
import dev.edvinmichovic.employeemanager.repository.EmployeeRepository;
import org.junit.Test;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = EmployeemanagerApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@Transactional
public class EmployeeControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeTransaction
    public void setup() {
        employee = Employee.builder()
                .id(1L)
                .name("John Smith")
                .email("john.smith@company.com")
                .jobTitle("Team Lead")
                .employeeCode("Test")
                .build();

        employeeRepository.save(employee);
    }

    @Order(1)
    @DisplayName("Integration Test #1 - check getAllEmployees endpoint")
    @Test
    public void givenEmployees_whenGetEmployees_thenStatus200() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/employee/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("John Smith"));
    }

    @Order(2)
    @DisplayName("Integration Test #2 - check getEmployeeById endpoint - positive scenario (ID of existing emp. provided)")
    @Test
    public void givenCorrectEmployeeId_whenGetEmployeeById_thenStatus200() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/employee/find/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John Smith"));
    }

    @Order(3)
    @DisplayName("Integration Test #3 - check getEmployeeById endpoint exception handling")
    @Test
    public void givenIncorrectEmployeeId_whenGetEmployeeById_thenExceptionThrown() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/employee/find/{id}", 2L))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User by id " + 2L + " was not found.", result.getResolvedException().getMessage()));
    }

    @Order(4)
    @DisplayName("Integration Test #4 - check addEmployee endpoint")
    @Test
    public void givenProperEmployeeBody_whenAddEmployee_thenEmployeeSaved() throws Exception{
        // Building an employee object
        Employee anotherEmployee = Employee.builder()
                .id(2L)
                .name("Kate Smith")
                .email("kate.smith@company.com")
                .jobTitle("JavaScript Developer")
                .build();

        // Convert employee object to JSON
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(anotherEmployee);

        // POST Request to the Endpoint
        mvc.perform(MockMvcRequestBuilders.post("/employee/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Kate Smith"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("kate.smith@company.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobTitle").value("JavaScript Developer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employeeCode").isNotEmpty());
    }

    @Order(5)
    @DisplayName("Integration Test #5 - check updateEmployee endpoint")
    @Test
    public void givenProperEmployeeBody_whenUpdateEmployee_thenEmployeeUpdated() throws Exception{
        // Modifying employee object
        employee.setName("Eric Watson");
        employee.setEmail("eric.watson@company.com");
        employee.setJobTitle("QA Manager");
        employee.setImageUrl("testdomain.com/testimage.png");

        // Convert employee object to JSON
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(employee);

        // PUT Request to the Endpoint
        mvc.perform(MockMvcRequestBuilders.put("/employee/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Eric Watson"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("eric.watson@company.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobTitle").value("QA Manager"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.imageUrl").value("testdomain.com/testimage.png"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employeeCode").value("Test"));
    }

    @Order(6)
    @DisplayName("Integration Test #6 - check deleteEmployee endpoint - correct ID provided")
    @Test
    public void givenProperEmployeeId_whenDeleteEmployee_thenStatus200() throws Exception{
        mvc.perform(MockMvcRequestBuilders.delete("/employee/delete/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Order(7)
    @DisplayName("Integration Test #7 - check deleteEmployee endpoint - incorrect ID provided")
    @Test
    public void givenImproperEmployeeId_whenDeleteEmployee_thenStatus200() throws Exception{
        mvc.perform(MockMvcRequestBuilders.delete("/employee/delete/{id}", 2L))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }



}
