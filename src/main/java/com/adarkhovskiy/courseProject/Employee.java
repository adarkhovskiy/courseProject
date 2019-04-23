package com.adarkhovskiy.courseProject;

import org.apache.logging.log4j.Level;

import java.io.Serializable;
import java.sql.Date;
import java.time.format.DateTimeParseException;

public class Employee implements Serializable {
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

    public Employee(String employeeData) throws NumberFormatException, DateTimeParseException, IllegalArgumentException, NullPointerException {
        // Если из файла
        employeeData.replaceAll("\\s+","");
        String[] data = employeeData.split(",");
        if (data.length < 5 || data.length < 6)
            throw new NullPointerException();
        if (data.length == 6) {
            this.id = Integer.valueOf(data[0]);   //  И создаем объект типа Сотрудник с полями: ID
            this.name = data[1];   //  Имя
            this.position = data[2];   //  Должность
            this.dateOfBirth = Date.valueOf(data[3]);  //  Дата рождения
            this.salary = Double.valueOf(data[4]);   //  Зарплата
            this.additionalInfo = data[5]; //  Доп инфо
        }
        // Если инсерт
        if (data.length == 5) {
            this.name = data[0];   //  Имя
            this.position = data[1];   //  Должность
            this.dateOfBirth = Date.valueOf(data[2]);  //  Дата рождения
            this.salary = Double.valueOf(data[3]);   //  Зарплата
            this.additionalInfo = data[4]; //  Доп инфо
        }

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
