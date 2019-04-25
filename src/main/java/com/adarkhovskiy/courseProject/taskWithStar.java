package com.adarkhovskiy.courseProject;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

public class taskWithStar {
    private static Statement stmt;
    private static ResultSet rs;
    public static Map<Class, String> converter = new HashMap<Class, String>() {{
        put(String.class, "VARCHAR(100)");  //  Мапа сопоставляющая тип данных из Java  с типом данных из MySQL
        put(int.class, "INT(11)");
        put(double.class, "DECIMAL(10,2)");
        put(java.sql.Date.class, "DATE");
    }};

    private static final Logger taskWithStarLogger = LogManager.getLogger(CourseProject.class.getName());

    //  При необходимости создает и выбирает БД с переданным именем
    public static void useDB(String dbName) {
        ArrayList<String> databases = new ArrayList();  //  Список БД
        StringBuilder sb = new StringBuilder();
        String query = "SHOW DATABASES;";   //  Сотрим существубщие БД
        try {
            stmt = DBRequest.con.createStatement();
            stmt.executeQuery(query);
            rs = stmt.executeQuery(query);
            while (rs.next())
                databases.add(rs.getString(1)); //  Запоминаем существующие БД
        } catch (SQLException e) {
            System.out.println("Ошибка при просмотре баз данных.");
            taskWithStarLogger.log(Level.ERROR, e);
        }

        if (!databases.contains(dbName)) {
            sb.append("CREATE DATABASE `").append(dbName).append("`;");    //  Создаем ее
            try {
                stmt = DBRequest.con.createStatement();
                stmt.execute(sb.toString());
                sb.setLength(0);
            } catch (SQLException e) {
                System.out.println("Ошибка создания базы данных.");
                taskWithStarLogger.log(Level.ERROR, e);
            }
        }
        sb.append("USE `").append(dbName).append("`"); //  И выбираем для работы
        try {
            stmt.execute(sb.toString());
        } catch (SQLException e) {
            System.out.println("Ошибка выбора базы данных.");
            taskWithStarLogger.log(Level.ERROR, e);
        }
    }

    //  Создание таблицы по переданному классу
    public static boolean createTable(Class cls) {
        if (!cls.isAnnotationPresent(XTable.class))
            throw new RuntimeException("@XTable is missed.");

        ArrayList<String> tables = new ArrayList(); //  Список таблиц БД
        String tableName = ((XTable) cls.getAnnotation(XTable.class)).title();
        StringBuilder sb = new StringBuilder();
        sb.append("SHOW TABLES");  //  Смотрим существующие в БД таблицы
        try {
            stmt = DBRequest.con.createStatement();
            rs = stmt.executeQuery(sb.toString());
            sb.setLength(0);
            while (rs.next())
                tables.add(rs.getString(1));    //  И запоминаем их
            if (!tables.contains(tableName)) {
                sb.append("CREATE TABLE ").append(((XTable) cls.getAnnotation(XTable.class)).title());
                sb.append(" (");
                Field[] fields = cls.getDeclaredFields();
                for (Field f : fields) {
                    if (f.isAnnotationPresent(XField.class)) {
                        sb.append(f.getName()).append(" ");
                        sb.append(converter.get(f.getType())).append(", ");
                    }
                }
                sb.setLength(sb.length() - 2);
                for (Field f : fields) {
                    if (f.getAnnotation(XField.class).is_pk()) {
                        sb.append(", PRIMARY KEY (").append(f.getName()).append(")");
                    }
                }
                sb.append(");");
                stmt = DBRequest.con.createStatement();
                stmt.execute(sb.toString());

            }
            return true;
        } catch (SQLException e) {
            System.out.println("Ошибка при создании таблицы.");
            taskWithStarLogger.log(Level.ERROR, e);
            return false;
        }
    }

    //  Добавление в таблицу переданного объекта
    public static boolean insertRecord(Object object) {
        Class cls = object.getClass();
        if (!cls.isAnnotationPresent(XTable.class))
            throw new RuntimeException("@XTable is missed.");

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(((XTable) cls.getAnnotation(XTable.class)).title());
        sb.append(" (");
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(XField.class)) {
                sb.append(f.getName()).append(", ");
            }
        }
        sb.setLength(sb.length() - 2);
        sb.append(") VALUES (");
        for (Field f : fields) {
            if (f.isAnnotationPresent(XField.class)) {
                sb.append("?, ");
            }
        }
        sb.setLength(sb.length() - 2);
        sb.append(");");
        try (PreparedStatement ps = DBRequest.con.prepareStatement(sb.toString())) {
            int c = 1;
            for (Field f : fields) {
                if (f.isAnnotationPresent(XField.class)) {
                    ps.setObject(c, f.get(object));
                    c++;
                }
            }
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (IllegalAccessException e) {
            System.out.println("Ошибка в данных записи.");
            taskWithStarLogger.log(Level.ERROR, e);
            return false;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Ошибка при добавлении записи: запись с таким id уже существует.");
            taskWithStarLogger.log(Level.ERROR, e);
            return false;
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении записи.");
            taskWithStarLogger.log(Level.ERROR, e);
            return false;
        }
    }

    //  Удаление переданного объекта
    public static boolean deleteRecord(Object object) {
        Class cls = object.getClass();
        if (!cls.isAnnotationPresent(XTable.class))
            throw new RuntimeException("@XTable is missed.");
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append(((XTable) cls.getAnnotation(XTable.class)).title());
        sb.append(" WHERE ");
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            if (f.getAnnotation(XField.class).is_pk()) {
                sb.append(f.getName()).append(" = ");
                try {
                    sb.append(f.get(object)).append(";");
                } catch (IllegalAccessException e) {
                    System.out.println("Ошибка в данных объекта при удалении записи.");
                    taskWithStarLogger.log(Level.ERROR, e);
                }
            }
        }
        try {
            stmt = DBRequest.con.createStatement();
            stmt.executeUpdate(sb.toString());
            System.out.println("Объект успешно удален.");
            return true;
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении записи.");
            taskWithStarLogger.log(Level.ERROR, e);
            return false;
        }

    }

    public static boolean insertAll(List[] objectsList) {
        if (objectsList == null)    //  Если список пустой
            return false;
        for (int i = 0; i < objectsList.length; i++) {
            if (objectsList[i] != null) {
                for (Object obj : objectsList[i]) {
                    insertRecord(obj);
                }
            }
        }
        return true;
    }

    public static List<Object> selectAllObjects(Class cls) {
        if (!cls.isAnnotationPresent(XTable.class))
            throw new RuntimeException("@XTable is missed.");
        try {
            stmt = DBRequest.con.createStatement();
            Field[] fields = cls.getDeclaredFields();
            Class[] types = new Class[fields.length];
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isAnnotationPresent(XField.class)) {
                    types[i] = fields[i].getType();
                }
            }
            Constructor cons = cls.getConstructor(types);
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * FROM ").append(((XTable) cls.getAnnotation(XTable.class)).title());
            sb.append(";");
            List<Object> result = new ArrayList<>();
            List<Object> objects = new ArrayList<>();
            rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String columnContent = rs.getString(i);
                    if (fields[i - 1].getType().isAssignableFrom(int.class) || fields[i - 1].getType().isAssignableFrom(Integer.class))
                        result.add(Integer.valueOf(columnContent));
                    else if (fields[i - 1].getType().isAssignableFrom(String.class))
                        result.add(columnContent);
                    else if (fields[i - 1].getType().isAssignableFrom(java.sql.Date.class))
                        result.add(java.sql.Date.valueOf(columnContent));
                    else if (fields[i - 1].getType().isAssignableFrom(double.class) || fields[i - 1].getType().isAssignableFrom(Double.class))
                        result.add(Double.valueOf(columnContent));
                }
                objects.add(cons.newInstance(result.toArray()));
                result.clear();
            }
            return objects;
        } catch (SQLException e) {
            System.out.println("Ошибка при выполнении запроса.");
            taskWithStarLogger.log(Level.ERROR, e);
        } catch (NoSuchMethodException e) {
            System.out.println("Ошибка при поиске конструктора объекта.");
            taskWithStarLogger.log(Level.ERROR, e);
        } catch (IllegalAccessException e) {
            System.out.println("Ошибка в данных класса.");
            taskWithStarLogger.log(Level.ERROR, e);
        } catch (InstantiationException e) {
            System.out.println("Ошибка при создании объекта.");
            taskWithStarLogger.log(Level.ERROR, e);
        } catch (InvocationTargetException e) {
            System.out.println("Ошибка при создании объекта.");
            taskWithStarLogger.log(Level.ERROR, e);
        }
        return null;
    }


    public static boolean clearTable(Class cls) {
        if (!cls.isAnnotationPresent(XTable.class))
            throw new RuntimeException("@XTable is missed.");
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append(((XTable) cls.getAnnotation(XTable.class)).title()).append(";");
        try {
            stmt = DBRequest.con.createStatement();
            stmt.executeUpdate(sb.toString());
            System.out.println("Таблица " + ((XTable) cls.getAnnotation(XTable.class)).title() + " успешно очищена.");
            return true;
        } catch (SQLException e) {
            System.out.println("Ошибка при очистке таблицы.");
            taskWithStarLogger.log(Level.ERROR, e);
            return false;
        }
    }

    public static boolean checkTable(Class cls) {
        if (!cls.isAnnotationPresent(XTable.class))
            throw new RuntimeException("@XTable is missed.");

        try {
            stmt = DBRequest.con.createStatement();
            rs = stmt.executeQuery("DESCRIBE " + ((XTable) cls.getAnnotation(XTable.class)).title());
            List<String> al = new ArrayList<>();
            while (rs.next()) {
                al.add(rs.getString(2));
            }
            Field[] fields = cls.getDeclaredFields();
            for (int i = 0; i < al.size(); i++) {
                if (!al.get(i).toUpperCase().equals(converter.get(fields[i].getType()))) {
                    return false;
                }
            }
            System.out.println("Таблица " + ((XTable) cls.getAnnotation(XTable.class)).title() + "соответствует разметке класса");
            return true;
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке таблицы.");
            taskWithStarLogger.log(Level.ERROR, e);
            return false;
        }
    }

    public static void writeFile(String fileName, List<Object> objectsList) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)))) {
            if (!new File(fileName).exists())    //  Если файл существует
                new File((fileName)).createNewFile();   //  И создаем новый

            Class cls = objectsList.get(0).getClass();  //  Запоминаем класс сущности
            StringBuilder sb = new StringBuilder();
            Field[] fields = cls.getDeclaredFields();
            bw.write(cls.getName().substring(cls.getName().lastIndexOf(".") + 1));  //  Записываем класс сущности без указания пакета
            bw.newLine();
            for (int i = 0; i < objectsList.size(); i++) {
                for (Field f : fields) {
                    if (f.isAnnotationPresent(XField.class)) {
                        sb.append(f.get(objectsList.get(i)).toString()).append(", ");
                    }
                }
                sb.setLength(sb.length() - 2);  //  Удаляем последнюю запятую и пробел
                bw.write(sb.toString());
                sb.setLength(0);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при записи файла.");
            taskWithStarLogger.log(Level.ERROR, e);
        } catch (IllegalAccessException e) {
            System.out.println("Ошибка при записи файла: ошибка в данных объекта.");
            taskWithStarLogger.log(Level.ERROR, e);
        }
    }

    public static List[] readFile(String fileName) {
        String className = taskWithStar.class.getPackage().toString();
        className = className.substring(className.indexOf(" ") + 1) + ".";  //  Берем имя пакета
        List<Object> objectData = new ArrayList<>();    //  Список данных одной сущности
        List<Object> objects = new ArrayList<>();   //  Список сущностей одного типа
        List<List> resultAsList = new ArrayList<>();    //  Список из списков сущностей
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            String columnContent;   //  Содержимое столбца
            int entityCount = 0;    //  Количество считанных из файла сущностей
            while (line != null) {
                Class cls = Class.forName(className + line);    //  Добавляем имя пакета к имени класса для создания класса
                Field[] fields = cls.getDeclaredFields();
                Class[] types = new Class[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].isAnnotationPresent(XField.class)) {
                        types[i] = fields[i].getType();
                    }
                }
                Constructor constructor = cls.getConstructor(types);    //  Берем конструктор, содержащий подходящие поля
                entityCount++;
                line = br.readLine();
                while (line != null && line.contains(",")) { //  Пока в строке больше одного эл-та ( с учетом того что сущность задается не менее чем двумя параметрами)
                    String[] data = line.split(", ");
                    for (int i = 0; i < data.length; i++) {
                        columnContent = data[i];
                        if (fields[i].getType().isAssignableFrom(int.class) || fields[i].getType().isAssignableFrom(Integer.class))
                            objectData.add(Integer.valueOf(columnContent));
                        else if (fields[i].getType().isAssignableFrom(String.class))
                            objectData.add(columnContent);
                        else if (fields[i].getType().isAssignableFrom(java.sql.Date.class))
                            objectData.add(java.sql.Date.valueOf(columnContent));
                        else if (fields[i].getType().isAssignableFrom(double.class) || fields[i].getType().isAssignableFrom(Double.class))
                            objectData.add(Double.valueOf(columnContent));
                    }
                    objects.add(constructor.newInstance(objectData.toArray()));
                    objectData.clear();
                    line = br.readLine();
                }
                resultAsList.add(new ArrayList(objects));
                objects.clear();
            }
            br.close();
            List[] result = new List[entityCount];  //  Преобразование списка сущностей в массив
            for (int i = 0; i < entityCount; i++) {
                result[i] = resultAsList.get(i);
            }
            return result;
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла.");
            taskWithStarLogger.log(Level.ERROR, e);
        } catch (ClassNotFoundException e) {
            System.out.println("Ошибка при создании объекта.");
            taskWithStarLogger.log(Level.ERROR, e);
        } catch (NoSuchMethodException e) {
            System.out.println("Ошибка при создании объекта: ошибка конструктора.");
            taskWithStarLogger.log(Level.ERROR, e);
        } catch (IllegalAccessException e) {
            System.out.println("Ошибка при создании объекта.");
            taskWithStarLogger.log(Level.ERROR, e);
        } catch (InstantiationException e) {
            System.out.println("Ошибка при создании объекта.");
            taskWithStarLogger.log(Level.ERROR, e);
        } catch (InvocationTargetException e) {
            System.out.println("Ошибка при создании объекта.");
            taskWithStarLogger.log(Level.ERROR, e);
        }
        return null;
    }
}
