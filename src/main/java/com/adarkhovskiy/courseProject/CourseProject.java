package com.adarkhovskiy.courseProject;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.IllegalFormatException;
import java.util.List;

public class CourseProject {
    static final String defaultImportFileName = "import.csv";   //  Имя файла импорта по умолчанию
    static final String defaultExportFileName = "export.csv";   //  Имя файла экспорта по умолчанию
    static final String defaultExtension = ".csv";  //  Расширение по умолчанию

    private static final Logger courseProjectLogger = LogManager.getLogger(CourseProject.class.getName());

    public static void main(String[] args) {
        if (!DBRequest.dbConnect()) {
            System.out.println("Программа будет завершена.");
            return;
        }
        System.out.println("Соединение с базой данных установлено. Для вывода описания доступных функций введите /help .");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input;
        String fileName;
        try {
            while (true) {
                input = br.readLine().replaceAll("\\s+", " ");  //  Удаляем дублирующие пробелы

                if (input.contains("/exportAll")) {
                    if (input.equals("/exportAll") || input.equals("/exportAll ")) {    //  Если имя файла не указано, берем имя по умолчанию
                        fileName = defaultExportFileName;
                    } else {
                        fileName = fileNameFormat(input);
                    }
                    if (FilesIO.writeFile(fileName, DBRequest.selectAll())) //  Передаем имя в метод
                        System.out.println("Экспорт в файл выполнен успешно.");
                }

                if (input.contains("/importAll")) {
                    if (input.equals("/importAll") || input.equals("/importAll ")) {    //  Если имя файла не указано, берем имя по умолчанию
                        fileName = defaultImportFileName;
                    } else {
                        fileName = fileNameFormat(input);
                    }
                    if (DBRequest.insertAll(FilesIO.readFile(fileName))) //  Передаем имя в метод
                        System.out.println("Импорт из файла выполнен успешно.");
                }

                if (input.contains("/addEmployee")) {
                    try {
                        if (DBRequest.insertEmployee(new Employee(input.substring(input.indexOf(" ") + 1))))  //  Обрезаем по первому пробелу
                            System.out.println("Сотрудник успешно добавлен.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка в данных сотрудника");
                    } catch (NullPointerException e) {
                        System.out.println("Неверное количество аргументов (должно быть 5 либо 6).");
                    }
                }
                if (input.contains("/getAllAvgSalary")) {
                    System.out.println("Средняя зарплата всех сотрудников: " + DBRequest.getAverageSalary());
                }
                if (input.contains("/getAvgSalaryByPos")) {
                    if (input.equals("/getAvgSalaryByPos"))
                        System.out.println("Укажите должность.");
                    else {
                        String position = input.substring(input.lastIndexOf(" ") + 1);
                        System.out.println("Средняя зарплата сотрудников на должности " + position + ": " + DBRequest.getAverageSalaryByPosition(position));
                    }
                }
                if (input.contains("/findEmpByPhone")) {
                    if (input.equals("/findEmpByPhone"))
                        System.out.println("Укажите номер телефона.");
                    else {
                        String phone = input.substring(input.lastIndexOf(" ") + 1);
                        System.out.println("По номеру телефона '" + phone + "' найдены следующие сотрудники: ");
                        List<String> result = DBRequest.findEmployeeByPhone(phone);
                        for (int i = 0; i < result.size(); i+=2) {
                            System.out.println(result.get(i) + " " + result.get(i+1));
                        }
                    }
                }
                if (input.contains("/deleteTable")) {
                    String tableName = "";
                    if (!input.equals("/deleteTable"))
                        tableName = input.substring(input.lastIndexOf(" ") + 1);
                    DBRequest.deleteTable(tableName);
                }
                if (input.contains("/help")) {
                    System.out.println("Вы можете ввести одну из комманд: " +
                            "\n /exportAll <имя файла> - выгрузка всех данных из БД в указанный файл. При вызове без имени файла данные будут записаны в файл по умолчанию;" +
                            "\n /importAll <имя файла> - загрузка всех данных из файла в БД. При вызове без имени файла данные будут считаны из файла по умолчанию;" +
                            "\n /addEmployee <id (опционально), имя сотрудника, должность, зарплата, доп. информация> - загрузка всех данных из файла в БД;" +
                            "\n /getAllAvgSalary - вывод в консоль средней зарплаты всех сотрудников;" +
                            "\n /getAvgSalaryByPos <должность> - вывод в консоль средней зарплаты по указанной должности;" +
                            "\n /findEmpByPhone <номер телефона> - вывод в консоль средней зарплаты по указанной должности;" +
                            "\n /deleteTable <имя таблицы> - удаление таблицы по имени. При вызове без имени таблицы удалит обе таблицы задания;" +
                            "\n /help - вывод в консоль списка команд;" +
                            "\n /exit - завершение работы.");
                }
                if (input.equals("/exit")) {
                    DBRequest.dbDisconnect();
                    break;
                }
            }
        } catch (IOException e) {
            courseProjectLogger.log(Level.ERROR, (e));
        } finally {
            DBRequest.dbDisconnect();
        }
    }

    public static String fileNameFormat(String fileName) {
        fileName = fileName.substring(fileName.lastIndexOf(" ") + 1);   //
        fileName = fileName.replaceAll("\\s+$", "");   //  Удаляем пробелы из конца строки
        if (!fileName.contains(".txt") && !fileName.contains(".csv")) {   //  Если в имени файла не указано подходящее расширение
            if (fileName.contains("."))  //  или указано некорректное расширение
                fileName.substring(0, fileName.lastIndexOf("."));   //  Обрезаем расширение
            fileName += defaultExtension; //  Указываем расширение по умолчанию
        }
        return fileName;
    }
}
