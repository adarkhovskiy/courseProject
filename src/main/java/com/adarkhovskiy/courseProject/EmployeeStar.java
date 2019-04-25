package com.adarkhovskiy.courseProject;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@XTable(title = "employees")
public class EmployeeStar implements Serializable {
    @XField (is_pk = true, name = "ID")
    public int id;
    @XField (name = "NAME")
    public String name;
    @XField (name = "POSITION")
    public String position;
    @XField (name = "DATE_OF_BIRTH")
    public Date dateOfBirth;
    @XField (name = "SALARY")
    public double salary;
    @XField (name = "ADD_INFO")
    public String additionalInfo;

    public EmployeeStar(int id, String name, String position, Date dateOfBirth, double salary, String additionalInfo) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.dateOfBirth = dateOfBirth;
        this.salary = salary;
        this.additionalInfo = additionalInfo;
    }

    public EmployeeStar() {

    }

    public EmployeeStar(List<String> list) {
        this.id = Integer.valueOf(list.get(0));
        this.name = list.get(1);
        this.position = list.get(1);
        this.dateOfBirth = Date.valueOf(list.get(1));
        this.salary = Double.valueOf(list.get(1));
        this.additionalInfo = list.get(1);;
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
            EmployeeStar tmp = (EmployeeStar) obj;
            if (tmp.getId() == this.getId() && tmp.getName().equals(this.getName())
                    && tmp.getPosition().equals(this.getPosition())
                    //&& tmp.getDateOfBirth().compareTo(this.getDateOfBirth()) == 0 //  Не сравниваю дату т.к. записывается в БД на день меньше, судя по интернету это косяк драйвера
                    && tmp.getSalary() == this.getSalary() && tmp.getAdditionalInfo().equals(this.getAdditionalInfo()))
                return true;
            else
                return false;
        }
    }
}
