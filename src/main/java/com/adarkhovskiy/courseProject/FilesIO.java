package com.adarkhovskiy.courseProject;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.Date;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FilesIO {
    private static final Logger filesIOLogger = LogManager.getLogger(CourseProject.class.getName());
    static final String separator = ",";

    public static List[] readFile(String fileName) {   //  Чтение файла для экспорта из файла в БД
        List<Employee> employeesList = new ArrayList<>();   //  Список объектов Сотрудник
        List<AdditionalInfo> additionalInfoList = new ArrayList<>();    //  Список объектов Доп инфо
        List[] result = new List[2];
        try (BufferedReader bi = new BufferedReader(new FileReader(fileName))) {
            String line;
            if ((line = bi.readLine()).equals("Employees")) //  Если строка равна ключевому слову Employees
                line = bi.readLine();   //  Читаем следующую строку
            while (line != null && !line.equals("Additional Info")) {   //  И пока не кончится файл или не найдем следующее ключевое слово
                try {
                    employeesList.add(new Employee(line));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Ошибка в данных сотрудника");
                    filesIOLogger.log(Level.ERROR, (e));
                }
                line = bi.readLine();   //  Читаем следующую строку файла
            }
            result[0] = employeesList;
            if (line != null && line.equals("Additional Info")) //  Если строка равна ключевому слову Additional Info
                line = bi.readLine();
            while (line != null) {
                try {
                    additionalInfoList.add(new AdditionalInfo(line));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Ошибка в данных сотрудника");
                    filesIOLogger.log(Level.ERROR, (e));
                }
                line = bi.readLine();
            }
            result[1] = additionalInfoList;
            bi.close(); //  Закрываем поток чтения
            return (new List[]{employeesList, additionalInfoList});    //  Возвращаем результат работы метода добавления всех данных в БД
        } catch (FileNotFoundException e) {
            System.out.println("Файл " + fileName + " не найден.");
            filesIOLogger.log(Level.ERROR, (e));
            return null;
        } catch (IOException e) {
            System.out.println("При чтении из файла " + fileName + " возникла ошибка.");
            filesIOLogger.log(Level.ERROR, (e));
            return null;
        }
    }

    public static boolean writeFile(String fileName, List[] dataArray) {  //  Запись файла для экспорта из БД
        if (dataArray == null)  //  Если из БД ничего не было считано, сразу возвращаем false
            return false;
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)))) {
            if (!(new File(fileName).exists())) //  Создаем файл если его еще нет
                new File((fileName)).createNewFile();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dataArray.length; i++) {
                if (dataArray[i] != null && dataArray[i].get(0) instanceof Employee) {
                    List<Employee> employeesList = new ArrayList<>(dataArray[0]);    //  Список сотрудников
                    bw.write("Employees");
                    bw.newLine();
                    for (Employee employee : employeesList) {   //  Выгружаем данные объекта сотрудник
                        sb.append(employee.getId()).append(separator)
                                .append(employee.getName()).append(separator)
                                .append(employee.getPosition()).append(separator)
                                .append(employee.getDateOfBirth()).append(separator)
                                .append(employee.getSalary()).append(separator)
                                .append(employee.getAdditionalInfo());
                        bw.write(sb.toString());    //  Записываем их
                        bw.newLine();   //  Перенос строки
                        sb.setLength(0);
                    }
                } else if (dataArray[i] != null && dataArray[i].get(0) instanceof AdditionalInfo) {
                    bw.write("Additional Info");
                    bw.newLine();
                    List<AdditionalInfo> additionalInfoList = new ArrayList<>(dataArray[1]);
                    for (AdditionalInfo addInfo : additionalInfoList) { //  Выгружаем данные объекта доп инфо
                        sb.append(addInfo.getAdditionalInfoId() + separator + addInfo.getPhoneNumber() + separator + addInfo.getAddress());
                        bw.write(sb.toString());    //  Записываем их
                        bw.newLine();
                        sb.setLength(0);
                    }
                }
            }
            bw.close(); //  Закрываем поток записи
            return true;
        } catch (IOException e) {
            System.out.println("При записи в файл " + fileName + " возникла ошибка.");
            filesIOLogger.log(Level.ERROR, (e));
            return false;
        }
    }

    public static boolean employeeDataValidate(String[] s) {
        try {
            if (Integer.valueOf(s[0]) < 0 || s[1].length() == 0 || Double.valueOf(s[4]) < 0)
                return false;
            s[2].toString();
            s[5].toString();
            if (Date.valueOf(s[3]).after(new Date(new java.util.Date().getTime()))) {
                System.out.println("Дата рождения сотрудника не может быть больше текущей.");
                return false;
            }
            return true;
        } catch (NumberFormatException | DateTimeParseException e) {
            System.out.println("Ошибка в данных сотрудника");
            filesIOLogger.log(Level.ERROR, (e));
            return false;
        }
    }

    public static boolean deleteFile(String fileName) {
        return (new File(fileName).getAbsoluteFile().delete());
    }
}
