package com.project.dao;

import com.project.entities.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Repository("emlpoyeeDAO")
public class JDBCEmployeeDAO implements EmployeeDAO {
//https://docs.spring.io/spring/docs/current/spring-framework-reference/html/jdbc.html
    @Autowired
    private Environment environment;
   // private DriverManagerDataSource dataSource;

    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
        return dataSource;
    }

    @Override
    public void addEmployee(Employee employee) {
        String query = "INSERT INTO EMPLOYEE (NAME, ROLE) VALUES(?,?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource());
        Object[] args = new Object[]{employee.getName(), employee.getRole()};
        int out = jdbcTemplate.update(query, args);
        if (out != 0) {
            System.out.println("Employee saved with id : " + employee.getId());
        }
    }

    @Override
    public void removeEmployee(Integer employeeId) {
        String query = "DELETE FROM EMPLOYEE WHERE ID = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource());
        Object[] args = new Object[]{employeeId};
        int out = jdbcTemplate.update(query, args);
        if (out != 0) {
            System.out.println("Employee with id : " + employeeId + "deleted successfully");
        }

    }

    @Override
    public Employee getEmployee(final Integer employeeId) {
        String query = "SELECT ID, NAME, ROLE FROM EMPLOYEE WHERE ID = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource());
        Object[] args = new Object[]{employeeId};
        Employee emp = jdbcTemplate.queryForObject(query, new Object[]{employeeId}, new RowMapper<Employee>() {
            @Override
            public Employee mapRow(ResultSet resultSet, int i) throws SQLException {
                Employee employee = new Employee();
                employee.setId(resultSet.getInt("id"));
                return employee;
            }
        });
        return emp;
    }

    @Override
    public List<Employee> listEmployees() {
        String query = "SELECT * FROM EMPLOYEE";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource());
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
        List<Employee> employees = new ArrayList<Employee>();
        for (Map row : rows) {
            Employee employee = new Employee();
            Integer id = (Integer) row.get("ID");
            employee.setId(id);
            employee.setName((String) row.get("NAME"));
            employee.setRole((String) row.get("ROLE"));
            employees.add(employee);
        }
        return employees;
    }

}
