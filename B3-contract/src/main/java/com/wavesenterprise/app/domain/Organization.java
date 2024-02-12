package com.wavesenterprise.app.domain;
import java.util.List;

public class Organization {
    private final String title;
    private String description;
    private final UserRole role;
    private List<String> employee;

    public Organization() {
        this.title = null;
        this.role = null;
    }

    public Organization(
        String sender,
        String title,
        String description,
        UserRole role
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

    public UserRole getRole() {
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
}
