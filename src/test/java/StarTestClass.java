import com.adarkhovskiy.courseProject.*;
import org.junit.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class StarTestClass {

    @BeforeClass
    public static void init() {
        DBRequest.dbConnect();
        taskWithStar.useDB("db2");
    }

    @Test
    public void TestWriteReadStar() {
        FilesIO.deleteFile("out.csv");
        EmployeeStar employeeStar1 = new EmployeeStar(10, "Name1", "Pos1", Date.valueOf("2016-01-02"), 15000.40, "Some text1");
        EmployeeStar employeeStar2 = new EmployeeStar(11, "Name2", "Pos2", Date.valueOf("2000-01-02"), 400.50, "Some text2");
        AdditionalInfoStar addInfoEmp1 = new AdditionalInfoStar(10, "88005553535", "address");
        AdditionalInfoStar addInfoEmp2 = new AdditionalInfoStar(11, "8465454542", "net");
        List<Object> employeeStarList = new ArrayList<>();
        List<Object> addInfoStarList = new ArrayList<>();
        employeeStarList.add(employeeStar1);
        employeeStarList.add(employeeStar2);
        addInfoStarList.add(addInfoEmp1);
        addInfoStarList.add(addInfoEmp2);
        taskWithStar.writeFile("out.csv", employeeStarList);
        taskWithStar.writeFile("out.csv", addInfoStarList);
        List[] resultFromFile = taskWithStar.readFile("out.csv");
        Assert.assertEquals(resultFromFile[0], addInfoStarList);
        Assert.assertEquals(resultFromFile[1], employeeStarList);
    }

    @Test
    public void testInsertSelectStar() {
        EmployeeStar employeeStar = new EmployeeStar(10, "Name1", "Pos1", Date.valueOf("2016-01-02"), 15000.40, "Some text1");
        AdditionalInfoStar addInfoEmp = new AdditionalInfoStar(10, "88005553535", "address");
        taskWithStar.clearTable(EmployeeStar.class);
        taskWithStar.clearTable(AdditionalInfoStar.class);
        taskWithStar.createTable(EmployeeStar.class);
        taskWithStar.createTable(AdditionalInfoStar.class);
        taskWithStar.checkTable(EmployeeStar.class);
        taskWithStar.checkTable(AdditionalInfoStar.class);
        taskWithStar.insertRecord(employeeStar);
        taskWithStar.insertRecord(addInfoEmp);
        EmployeeStar employeeStarSelect = (EmployeeStar) taskWithStar.selectAllObjects(EmployeeStar.class).get(0);
        AdditionalInfoStar addInfoEmpFromSelect = (AdditionalInfoStar) taskWithStar.selectAllObjects(AdditionalInfoStar.class).get(0);
        Assert.assertEquals(employeeStar, employeeStarSelect);
        Assert.assertEquals(addInfoEmp, addInfoEmpFromSelect);
    }


    @Test
    public void fullTestStar() {
        FilesIO.deleteFile("in.csv");
        FilesIO.deleteFile("out.csv");
        EmployeeStar employeeStar1 = new EmployeeStar(10, "Name1", "Pos1", Date.valueOf("2016-01-02"), 15000.40, "Some text1");
        EmployeeStar employeeStar2 = new EmployeeStar(11, "Name2", "Pos2", Date.valueOf("2000-01-02"), 400.50, "Some text2");
        AdditionalInfoStar addInfoEmp1 = new AdditionalInfoStar(10, "88005553535", "address");
        AdditionalInfoStar addInfoEmp2 = new AdditionalInfoStar(11, "8465454542", "net");
        List<Object> employeeStarList = new ArrayList<>();
        List<Object> addInfoStarList = new ArrayList<>();
        employeeStarList.add(employeeStar1);
        employeeStarList.add(employeeStar2);
        addInfoStarList.add(addInfoEmp1);
        addInfoStarList.add(addInfoEmp2);
        taskWithStar.writeFile("in.csv", employeeStarList);
        taskWithStar.writeFile("in.csv", addInfoStarList);
        taskWithStar.clearTable(EmployeeStar.class);
        taskWithStar.clearTable(AdditionalInfoStar.class);
        taskWithStar.createTable(EmployeeStar.class);
        taskWithStar.createTable(AdditionalInfoStar.class);
        taskWithStar.checkTable(EmployeeStar.class);
        taskWithStar.checkTable(AdditionalInfoStar.class);
        taskWithStar.insertAll(taskWithStar.readFile("in.csv"));
        taskWithStar.writeFile("out.csv", taskWithStar.selectAllObjects(EmployeeStar.class));
        taskWithStar.writeFile("out.csv", taskWithStar.selectAllObjects(AdditionalInfoStar.class));
        List[] resultFromFile = taskWithStar.readFile("out.csv");
        Assert.assertEquals(resultFromFile[0], employeeStarList);
        Assert.assertEquals(resultFromFile[1], addInfoStarList);
    }

    @AfterClass
    public static void disconnect() {
        DBRequest.dbDisconnect();
    }
}
