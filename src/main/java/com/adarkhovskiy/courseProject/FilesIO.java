package com.adarkhovskiy.courseProject;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FilesIO {
    private static final Logger logger = LogManager.getLogger(CourseProject.class.getName());
    static final String separator = ",";

    public static List[] readFile(String fileName) {   //  Чтение файла для экспорта из файла в БД
        List<Employee> employeesList = new ArrayList<>();   //  Список объектов Сотрудник
        List<AdditionalInfo> additionalInfoList = new ArrayList<>();    //  Список объектов Доп инфо
        List[] result = new List[2];
        try {
            BufferedReader bi = new BufferedReader(new FileReader(fileName));
            String line;
            String[] lineArray;
            if ((line = bi.readLine()).equals("Employees")) //  Если строка равна ключевому слову Employees
                line = bi.readLine();   //  Читаем следующую строку
            while (line != null && !line.equals("Additional Info")) {   //  И пока не кончится файл или не найдем следующее ключевое слово
                lineArray = line.split(separator);  //  Делим по запятым считанную строку
                if (!employeeDataValidate(lineArray))
                    return null;
                try {
                    employeesList.add(new Employee(Integer.valueOf(lineArray[0]),   //  И создаем объект типа Сотрудник с полями: ID
                            lineArray[1],   //  Имя
                            lineArray[2],   //  Должность
                            Date.valueOf(lineArray[3]),  //  Дата рождения
                            Double.valueOf(lineArray[4]),   //  Зарплата
                            lineArray[5])); //  Доп инфо
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    logger.log(Level.ERROR, (e));
                    bi.close();
                    throw e;
                }
                line = bi.readLine();   //  Читаем следующую строку файла
            }
            result[0] = employeesList;
            if (line != null && line.equals("Additional Info")) //  Если строка равна ключевому слову Additional Info
                line = bi.readLine();
            while (line != null) {
                lineArray = line.split(separator);  //  Аналогично, но для объектов типа Доп инфо
                try {
                    additionalInfoList.add(new AdditionalInfo(Integer.valueOf(lineArray[0]),
                            lineArray[1],
                            lineArray[2]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    bi.close();
                    throw e;
                }
                line = bi.readLine();
            }
            result[1] = additionalInfoList;
            bi.close(); //  Закрываем поток чтения
            return (new List[]{employeesList, additionalInfoList});    //  Возвращаем результат работы метода добавления всех данных в БД
        } catch (IOException e) {
            logger.log(Level.ERROR, (e));
            return null;
        }
    }

    public static boolean writeFile(String fileName, List[] dataArray) {  //  Запись файла для экспорта из БД

        if (dataArray == null)   //  Если из БД ничего не было считано, сразу возвращаем false
            return false;
        List<Employee> employeesList = new ArrayList<>(dataArray[0]);    //  Список сотрудников
        try {
            if (!(new File(fileName).exists())) //  Создаем файл если его еще нет
                new File((fileName)).createNewFile();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
            StringBuilder sb = new StringBuilder();
            bw.write("Employees");
            bw.newLine();
            for (Employee employee : employeesList) {   //  Выгружаем данные объекта сотрудник
                sb.append(employee.getId() + separator
                        + employee.getName() + separator
                        + employee.getPosition() + separator
                        + employee.getDateOfBirth() + separator
                        + employee.getSalary() + separator
                        + employee.getAdditionalInfo());
                bw.write(sb.toString());    //  Записываем их
                bw.newLine();   //  Перенос строки
                sb.setLength(0);
            }
            if (dataArray[1] != null) {
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
            bw.close(); //  Закрываем поток записи
            return true;
        } catch (IOException e) {
            logger.log(Level.ERROR, (e));
            return false;
        }
    }

    public static boolean employeeDataValidate(String[] s) {
        try {
            if(Integer.valueOf(s[0]) < 0 || s[1].length() == 0|| Double.valueOf(s[4]) < 0)
                return false;
            s[2].toString();
            new SimpleDateFormat("yyyy-MM-dd").parse(s[3]);
            s[5].toString();
            return true;
        } catch (NumberFormatException | DateTimeParseException | ParseException e) {
            System.out.println("Ошибка в данных сотрудника");
            return false;
        }
    }
}
