package com.wavesenterprise.app.domain;
import java.util.List;

public class Organization {
    private String title;
    private String description;
    private Role role;
    private List<String> employee;

    public Organization() {}

    public Organization(
        String sender,
        String title,
        String description,
        Role role
    ) {
        this.title = title;
        this.description = description;
        this.role = role;
        this.employee = List.of(sender);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Role getRole() {
        return role;
    }

    public List<String> getEmployee() {
        return employee;
    }

    public void setEmployee(List<String> employee) {
        this.employee = employee;
    }

    public void addEmployee(String employeeKey) {
        this.employee.add(employeeKey);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
