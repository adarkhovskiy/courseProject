package com.adarkhovskiy.courseProject;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class CourseProject {
    static final String defaultImportFileName = "import.csv";   //  Имя файла импорта по умолчанию
    static final String defaultExportFileName = "export.csv";   //  Имя файла экспорта по умолчанию
    static final String defaultExtension = ".csv";  //  Расширение по умолчанию

    private static final Logger logger = LogManager.getLogger(CourseProject.class.getName());

    public static void main(String[] args) {
        DBRequest.dbConnect();
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
                    if (DBRequest.insertEmployee(input.substring(input.indexOf(" ") + 1)))  //  Обрезаем по первому пробелу
                        System.out.println("Сотрудник успешно добавлен.");
                }
                if (input.contains("/getAllAvgSalary")) {
                    DBRequest.getAverageSalary();
                }
                if (input.contains("/getAvgSalaryByPos")) {
                    DBRequest.getAverageSalaryByPosition(input.substring(input.lastIndexOf(" ") + 1));
                }
                if (input.contains("/findEmpByPhone")) {
                    DBRequest.findEmployeeByPhone(input.substring(input.lastIndexOf(" ") + 1));
                }
                if (input.contains("/deleteTable")) {
                    DBRequest.deleteTable(input.substring(input.lastIndexOf(" ") + 1));
                }
                if (input.contains("/help")) {
                    System.out.println("Вы можете ввести одну из комманд: " +
                            "\n /exportAll <имя файла> - выгрузка всех данных из БД в указанный файл. При передаче пустого имени файла данные будут записаны в файл по умолчанию;" +
                            "\n /importAll <имя файла> - загрузка всех данных из файла в БД. При передаче пустого имени файла данные будут записаны в файл по умолчанию;" +
                            "\n /addEmployee <имя сотрудника, должность, зарплата, доп. информация> - загрузка всех данных из файла в БД;" +
                            "\n /getAllAvgSalary - вывод в консоль средней зарплаты всех сотрудников;" +
                            "\n /getAvgSalaryByPos <должность> - вывод в консоль средней зарплаты по указанной должности;" +
                            "\n /findEmpByPhone <номер телефона> - вывод в консоль средней зарплаты по указанной должности;" +
                            "\n /deleteTable <имя таблицы> - удаление таблицы по имени. При передаче пустого имени удалит таблицы задания;" +
                            "\n /help - вывод в консоль списка команд;" +
                            "\n /exit - завершение работы.");
                }
                if (input.equals("/exit")) {
                    System.out.println("Goodbye!");
                    DBRequest.dbDisconnect();
                    break;
                }
            }
        } catch (IOException e) {
            logger.log(Level.ERROR, (e));
        } finally {
            DBRequest.dbDisconnect();
        }
    }

    public static String fileNameFormat(String fileName) {
        fileName = fileName.substring(fileName.lastIndexOf(" ")+1);   //
        fileName = fileName.replaceAll("\\s+$", "");   //  Удаляем пробелы из конца строки
        if (!fileName.contains(".txt") && !fileName.contains(".csv")) {   //  Если в имени файла не указано подходящее расширение
            if (fileName.contains("."))  //  или указано некорректное расширение
                fileName.substring(0, fileName.lastIndexOf("."));   //  Обрезаем расширение
            fileName += defaultExtension; //  Указываем расширение по умолчанию
        }
        return fileName;
    }
}