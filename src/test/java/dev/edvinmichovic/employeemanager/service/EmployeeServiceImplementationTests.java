package dev.edvinmichovic.employeemanager.service;

import dev.edvinmichovic.employeemanager.exception.UserNotFoundException;
import dev.edvinmichovic.employeemanager.model.Employee;
import dev.edvinmichovic.employeemanager.repository.EmployeeRepository;
import dev.edvinmichovic.employeemanager.service.implementation.EmployeeServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplementationTests {

    @Mock
    private EmployeeRepository employeeRepository;
    
    @InjectMocks
    private EmployeeServiceImplementation employeeServiceImplementation;

    private Employee employee;

    @BeforeEach
    public void setup() {
        employee = Employee.builder()
                .id(1L)
                .name("John Smith")
                .email("john.smith@company.com")
                .jobTitle("Team Lead")
                .build();
    }

    @DisplayName("JUnit test for addEmployee method")
    @Test
    public void givenEmployeeObject_whenSaveEmployee_thenReturnEmployeeObject() {
        given(employeeRepository.save(employee)).willReturn(employee);

        Employee savedEmployee = employeeServiceImplementation.addEmployee(employee);

        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getEmployeeCode()).isNotNull();
        verify(employeeRepository, times(1)).save(employee);
    }

    @DisplayName("Junit test for findAllEmployees - list of employees returned")
    @Test
    public void givenEmployeeList_whenFindAllEmployees_theReturnEmployeeList() {
        Employee employee2 = Employee.builder()
                .id(2L)
                .name("Sam Smith")
                .email("sam.smith@company.com")
                .jobTitle("QA Engineer")
                .build();

        given(employeeRepository.findAll()).willReturn(List.of(employee, employee2));

        List<Employee> employeeList = employeeServiceImplementation.findAllEmployees();

        assertThat(employeeList).isNotEmpty();
        assertThat(employeeList.size()).isEqualTo(2);
        assertThat(employeeList.get(0).getName()).isEqualTo("John Smith");
        assertThat(employeeList.get(1).getName()).isEqualTo("Sam Smith");
    }

    @DisplayName("Junit test for findAllEmployees - empty list returned (negative)")
    @Test
    public void givenEmptyEmployeeList_whenFindAllEmployees_thenReturnEmptyList() {
        given(employeeRepository.findAll()).willReturn(Collections.emptyList());

        List<Employee> employeeList = employeeServiceImplementation.findAllEmployees();

        assertThat(employeeList).isEmpty();
        assertThat(employeeList.size()).isEqualTo(0);
    }

    @DisplayName("JUnit test for updateEmployee method")
    @Test
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee(){
        given(employeeRepository.save(employee)).willReturn(employee);
        employee.setEmail("updatedEmail@gmail.com");
        employee.setName("Updated Name");

        Employee updatedEmployee = employeeServiceImplementation.updateEmployee(employee);

        // then - verify the output
        assertThat(updatedEmployee.getEmail()).isEqualTo("updatedEmail@gmail.com");
        assertThat(updatedEmployee.getName()).isEqualTo("Updated Name");
    }

    @DisplayName("Junit test for findEmployeeById - employee should be returned with proper ID")
    @Test
    public void givenCorrectId_whenEmployeeById_thenEmployeeReturned() {
        when(employeeRepository.findEmployeeById(1L)).thenReturn(Optional.of(employee));

        Employee foundEmployee = employeeServiceImplementation.findEmployeeById(1L);

        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee).isEqualTo(employee);
    }
    @DisplayName("Junit test for findEmployeeById - exception should be thrown when Employee not found (negative)")
    @Test
    public void givenIncorrectId_whenEmployeeById_thenThrowsException() {
        when(employeeRepository.findEmployeeById(2L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> employeeServiceImplementation.findEmployeeById(2L));

        assertThat(exception.getMessage()).isEqualTo("User by id " + 2L + " was not found.");
    }

    @DisplayName("Junit test for deleteEmployee method")
    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenNothing() {
        willDoNothing().given(employeeRepository).deleteById(1L);

        employeeServiceImplementation.deleteEmployee(1L);

        verify(employeeRepository, times(1)).deleteById(1L);
    }
}
