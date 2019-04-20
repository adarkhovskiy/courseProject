package com.adarkhovskiy.courseProject;

import java.sql.Date;

public class Employee {
    private int id;
    private String name;
    private String position;
    private Date dateOfBirth;
    private double salary;
    private String additionalInfo;

    public Employee() {
    }

    public Employee(int id, String name, String position, Date dateOfBirth, double salary, String additionalInfo) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.dateOfBirth = dateOfBirth;
        this.salary = salary;
        this.additionalInfo = additionalInfo;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public double getSalary() {
        return salary;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!(getClass() == obj.getClass()))
            return false;
        else {
            Employee tmp = (Employee) obj;
            if (tmp.getId() == this.getId() && tmp.getName().equals(this.getName())
                    && tmp.getPosition().equals(this.getPosition()) && tmp.getDateOfBirth().compareTo(this.getDateOfBirth()) == 0
                    && tmp.getSalary() == this.getSalary() && tmp.getAdditionalInfo().equals(this.getAdditionalInfo()))
                return true;
            else
                return false;
        }
    }
}
