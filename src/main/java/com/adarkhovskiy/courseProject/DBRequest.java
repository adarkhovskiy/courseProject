package com.adarkhovskiy.courseProject;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DBRequest {
    private static final String url = "jdbc:mysql://localhost:3306/?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
    private static final String user = "root";
    private static final String password = "1234";
    private static final String dbName = "course_project";
    private static final String employeesTable = "employees";
    private static final String additionalInfoTable = "additional_info";
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    private static final Logger logger = LogManager.getLogger(CourseProject.class.getName());

    public static boolean dbConnect() {
        try {
            logger.log(Level.INFO, ("Try to connection to server " + url + " as " + user + "..."));
            con = DriverManager.getConnection(url, user, password);
            con.isValid(5000);
            stmt = con.createStatement();
            logger.log(Level.INFO, ("Connection done. Checking database and tables..."));
        } catch (CommunicationsException e) {
            System.out.println("Не удается подключиться к базе данных. Проверьте, что сервер базы данных запущен.");
            logger.log(Level.ERROR, (e));
            return false;
        } catch (SQLNonTransientConnectionException e) {
            System.out.println("Не удается подключиться к базе данных. Проверьте данные подключения.");
            logger.log(Level.ERROR, (e));
            return false;
        } catch (SQLException e) {
            logger.log(Level.ERROR, (e));
            return false;
        }
        if (dbInit()) {
            logger.log(Level.INFO, ("DB and tables are ok."));
        }
        return true;
    }

    public static boolean dbDisconnect() {
        logger.log(Level.INFO, ("Try to close connection..."));
        try {
            stmt.close();
        } catch (SQLException e) {
            logger.log(Level.ERROR, (e));
            return false;
        }

        try {
            rs.close();
        } catch (SQLException e) {
            logger.log(Level.ERROR, (e));
            return false;
        }
        try {
            con.close();
        } catch (SQLException e) {
            logger.log(Level.ERROR, (e));
            return false;
        }
        logger.log(Level.INFO, ("Connection closed."));
        return true;
    }

    public static boolean dbInit() {    //  Инициализации базы данных
        ArrayList<String> databases = new ArrayList();  //  Список БД
        ArrayList<String> tables = new ArrayList(); //  Список таблиц БД
        logger.log(Level.INFO, ("Checking databases..."));
        try {
            String query = "SHOW DATABASES;";   //  Сотрим существубщие БД
            rs = stmt.executeQuery(query);
            try {
                while (rs.next())
                    databases.add(rs.getString(1)); //  Запоминаем существующие БД
            } catch (SQLException e) {
                logger.log(Level.ERROR, (e));
            }
            if (!databases.contains(dbName)) {  //  Если нужной нам не существует
                logger.log(Level.INFO, ("Database " + dbName + " is not exist. Creating database..."));
                query = "CREATE DATABASE `" + dbName + "`;";    //  Создаем ее
                stmt.execute(query);
            }
            query = "USE `" + dbName + "`"; //  И выбираем для работы
            stmt.execute(query);
            query = "SHOW TABLES";  //  Смотрим существующие в БД таблицы
            rs = stmt.executeQuery(query);
            try {
                while (rs.next())
                    tables.add(rs.getString(1));    //  И запоминаем их
            } catch (SQLException e) {
                logger.log(Level.ERROR, (e));
            }
            if (!tables.contains(employeesTable)) { //  Если нет таблицы сотрудников - создаем ее
                logger.log(Level.INFO, ("Table " + employeesTable + " is not exists. Creating.."));
                System.out.println("Creating table " + employeesTable + "...");
                query = "CREATE TABLE `" + employeesTable + "` (employee_id INT(10) AUTO_INCREMENT NOT NULL, " +
                        "name VARCHAR(30) NOT NULL, " +
                        "position VARCHAR(30) NOT NULL, " +
                        "date_of_birth DATE NOT NULL, " +
                        "salary DECIMAL(10,2) NOT NULL, " +
                        "additional_info VARCHAR(100) NOT NULL, " +
                        "PRIMARY KEY (employee_id));";
                stmt.execute(query);
            }

            if (!tables.contains(additionalInfoTable)) {    //  Если нет таблицы доп информации о сотрудниках - создаем ее
                logger.log(Level.INFO, ("Table " + additionalInfoTable + " is not exists. Creating..."));
                query = "CREATE TABLE `" + additionalInfoTable + "` (additional_info_id INT(10) NOT NULL, " +
                        "phone VARCHAR(12) NOT NULL, " +
                        "address VARCHAR(100) NOT NULL, " +
                        "PRIMARY KEY (additional_info_id), " +
                        "FOREIGN KEY (additional_info_id) REFERENCES employees(employee_id));";
                stmt.execute(query);
            }
            return true;    //  Все создано и готово
        } catch (SQLException e) {
            logger.log(Level.ERROR, (e));
            return false;
        }
    }

    public static List[] selectAll() {  //  Экспорт. Выборка всех данных из двух таблиц, создание объектов на их основе и заполнение соответствубщих двух списков
        List<Employee> employeesList = new ArrayList<>();
        List<AdditionalInfo> additionalInfoList = new ArrayList<>();
        List[] result = new List[2];
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            Statement stmt = con.createStatement();
            String query = "SELECT * FROM `" + dbName + "`.`" + employeesTable + "`;";
            rs = stmt.executeQuery(query);
            try {
                while (rs.next()) {
                    employeesList.add(new Employee(rs.getInt(1),    //  ID
                            rs.getString(2),    //  Имя
                            rs.getString(3),    //  Должность
                            rs.getDate(4),   //  Дата рождения
                            rs.getDouble(5),    //  Зарплата
                            rs.getString(6)));  //  Доп инфо
                }
            } catch (SQLException e) {
                logger.log(Level.ERROR, (e));
            }
            result[0] = employeesList;
            query = "SELECT * FROM `" + dbName + "`.`" + additionalInfoTable + "`;";
            rs = stmt.executeQuery(query);
            try {
                while (rs.next())
                    additionalInfoList.add(new AdditionalInfo(rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3)));
            } catch (SQLException e) {
                logger.log(Level.ERROR, (e));
            }
            result[1] = additionalInfoList;
            if (result[0].equals(new ArrayList()) && result[1].equals(new ArrayList())) {
                System.out.println("В таблицах " + employeesTable + " и " + additionalInfoTable + "не найдено записей.");
                return null;
            }
            return (result); //  Возвращаем массив списков - список работников и список доп информации
        } catch (SQLException e) {
            System.out.println("При выполнении запроса возникла ошибка.");
            logger.log(Level.ERROR, (e));
            return null;
        }
    }

    public static boolean insertAll(List[] list) {  //  Импорт. Добавление всех объектов из списков
        if (list == null)    //  Если список пустой
            return false;
        try {
            if (list[0] != null) {
                List<Employee> employeesList = new ArrayList<>(list[0]);
                for (Employee employee : employeesList) {   //  Если в спике только сотурдники
                    String query = "INSERT INTO `" + dbName + "`.`" + employeesTable + "` VALUES (" +
                            "\'" + employee.getId() + "\', " +
                            "\'" + employee.getName() + "\', " +
                            "\'" + employee.getPosition() + "\', " +
                            "\'" + dateFormat.format(employee.getDateOfBirth()) + "\', " +
                            "\'" + employee.getSalary() + "\', " +
                            "\'" + employee.getAdditionalInfo() + "\');";
                    stmt.executeUpdate(query);
                }
            }
            if (list[1] != null) {  //  Если в списке и сотрудники и доп информация
                List<AdditionalInfo> additionalInfoList = new ArrayList<>(list[1]);
                for (AdditionalInfo additionalInfo : additionalInfoList) {
                    String query = "INSERT INTO `" + dbName + "`.`" + additionalInfoTable + "` VALUES (" +
                            "\'" + additionalInfo.getAdditionalInfoId() + "\', " +
                            "\'" + additionalInfo.getPhoneNumber() + "\', " +
                            "\'" + additionalInfo.getAddress() + "\');";
                    stmt.executeUpdate(query);
                }
            }
            System.out.println("Записи успешно добавлены.");
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Запись с таким ID уже существует.");
            return false;
        } catch (SQLException e) {
            System.out.println("При выполнении добавления сотрудников из файла возникла ошибка.");
            logger.log(Level.ERROR, (e));
            return false;
        }
    }

    public static boolean insertEmployee(String data) { //  Добавление сотрудника
        String[] employeeData = data.split(", ");
        if (!FilesIO.employeeDataValidate(employeeData))
            return false;
        if (employeeData.length != 5) { //  Проверяем наличие всех параметров
            System.out.println("Неверное количество параметров (должно быть 4 - имя, должность, зарплата, доп. информация).");
            return false;
        }
        try {
            String query = "INSERT INTO `" + dbName + "`.`" + employeesTable + "` (`name`, `position`, `date_of_birth`, `salary`, `additional_info`)" +
                    "VALUES (" +
                    "\'" + employeeData[0] + "\', " +   //  Имя
                    "\'" + employeeData[1] + "\', " +   //  Должность
                    "\'" + employeeData[2] + "\', " +   //  Дата рождения
                    "\'" + employeeData[3] + "\', " +   //  Зарплата
                    "\'" + employeeData[4] + "\');";    //  Доп инфо
            stmt.executeUpdate(query);
            System.out.println("Сотрудник " + employeeData[0] + " успешно добавлен.");
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {  //  Ошибка нарушения уникальности - Primary Key
            System.out.println("Запись с таким ID уже существует.");
            return false;
        } catch (SQLException e) {
            System.out.println("При выполнении запроса возникла ошибка.");
            logger.log(Level.ERROR, (e));
            return false;
        }
    }

    public static void getAverageSalary() { //  Вычисление средней зарплаты всех сотрудников
        String query = "SELECT AVG(salary) FROM `" + dbName + "`.`" + employeesTable + "`;";
        try {
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                System.out.println("Средняя зарплата всех сотрудников: " + rs.getDouble(1));
            }
        } catch (SQLException e) {
            System.out.println("При выполнении запроса возникла ошибка.");
            logger.log(Level.ERROR, (e));
        }
    }

    public static void getAverageSalaryByPosition(String position) {    //  Вычисление средней зарплаты сотрудников одной должности
        String query = "SELECT AVG(salary) FROM `" + dbName + "`.`" + employeesTable + "` WHERE position = '" + position + "';";
        System.out.println(query);
        try {
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                System.out.println("Средняя зарплата сотрудников на должности " + position + ": " + rs.getDouble(1));
            }
        } catch (SQLException e) {
            System.out.println("При выполнении запроса возникла ошибка.");
            logger.log(Level.ERROR, (e));
        }
    }

    public static void findEmployeeByPhone(String phone) {  //  Поиск сотрудника по номеру телефона - возвращаем ID и имя
        String query = "SELECT * FROM `" + dbName + "`.`" + employeesTable + "` WHERE employee_id in (" +
                "SELECT additional_info_id FROM `" + dbName + "`.`" + additionalInfoTable + "` WHERE phone = '" + phone + "');";
        try {
            rs = stmt.executeQuery(query);
            System.out.println("По номеру телефона '" + phone + "' найдены следующие сотрудники: ");
            while (rs.next()) {
                System.out.print(rs.getInt(1) + " " + rs.getString(2) + "\n");
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, (e));
        }
    }


    /*------------------------*/
    public static boolean deleteTable(String tableName) {
        if (!tableName.equals("")) {
            ArrayList<String> tables = new ArrayList(); //  Список таблиц БД
            try {
                String query = "SHOW TABLES";  //  Смотрим существующие в БД таблицы
                rs = stmt.executeQuery(query);
                while (rs.next())
                    tables.add(rs.getString(1));    //  И запоминаем их
                if (!tables.contains(tableName)) {
                    System.out.println("Таблица '" + tableName + "' отсутствует в БД.");
                    return false;
                }
                query = "DELETE FROM `" + dbName + "`.`" + tableName + "`;";
                stmt.executeUpdate(query);
                System.out.println("Таблица '" + tableName + "' успешно удалена.");
                return true;
            } catch (SQLException e) {
                System.out.println("При удалении таблицы  '" + tableName + "' возникла ошибка.");
                logger.log(Level.ERROR, (e));
                return false;
            }

        } else {
            try {
                String query = "DELETE FROM `" + dbName + "`.`" + additionalInfoTable + "`;";
                stmt.executeUpdate(query);
                query = "DELETE FROM `" + dbName + "`.`" + employeesTable + "`;";
                stmt.executeUpdate(query);
                System.out.println("Таблицы '" + employeesTable + "' и '" + additionalInfoTable + "' успешно удалены.");
                return true;
            } catch (SQLException e) {
                System.out.println("При удалении таблиц '" + employeesTable + "' и '" + additionalInfoTable + "' возникла ошибка.");
                logger.log(Level.ERROR, (e));
                return false;
            }
        }
    }
}
