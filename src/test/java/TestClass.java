import com.adarkhovskiy.courseProject.AdditionalInfo;
import com.adarkhovskiy.courseProject.DBRequest;
import com.adarkhovskiy.courseProject.Employee;
import com.adarkhovskiy.courseProject.FilesIO;
import org.junit.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TestClass {

    @BeforeClass
    public static void init() {
        DBRequest.dbConnect();
    }

    @Test
    public void testWriteAndRead() {
        String fileName = "testIn.csv";
        Employee emp = new Employee(98, "Name", "Position", Date.valueOf("2016-01-01"), 6543.21, "Some text");
        Employee emp2 = new Employee(99, "Name2", "Position2", Date.valueOf("2016-01-01"), 4443.21, "Some text2");
        List<Employee> empList = new ArrayList<>();
        empList.add(emp);
        empList.add(emp2);
        List[] list = new List[2];
        list[0] = empList;
        FilesIO.writeFile(fileName, list);  //  Записали в исходный файл
        Employee e = (Employee)FilesIO.readFile(fileName)[0].get(1);
        Assert.assertEquals(list[0], FilesIO.readFile(fileName)[0]);
    }

    @Test
    public void phoneTest() {
        DBRequest.deleteTable("");
        Employee emp = new Employee(98, "Name", "Position", Date.valueOf("2016-01-01"), 6543.21, "Some text");
        AdditionalInfo addInfoEmp1 = new AdditionalInfo(98, "88005553535", "Some Info employee 1");
        DBRequest.insertEmployee(emp);    //  Считали из исходного файла и загрузили в БД
        DBRequest.insertAddInfo(addInfoEmp1);
        List<String> expectedList = new ArrayList<>();
        expectedList.add("98");
        expectedList.add("Name");
        Assert.assertEquals(expectedList, DBRequest.findEmployeeByPhone("88005553535"));
    }

    @Test
    public void avgSalaryTest() {
        DBRequest.deleteTable("");
        Employee emp1 = new Employee(98, "Name", "Position", Date.valueOf("2016-01-01"), 15000.40, "Some text");
        Employee emp2 = new Employee(99, "Name2", "Position2", Date.valueOf("2016-01-01"), 5000.40, "Some text2");
        DBRequest.insertEmployee(emp1);    //  Считали из исходного файла и загрузили в БД
        DBRequest.insertEmployee(emp2);    //  Считали из исходного файла и загрузили в БД
        Assert.assertSame(0, Double.compare(10000.40, DBRequest.getAverageSalary()));
    }

    @Test
    public void avgSalaryByPosTest() {
        String position = "Position2";
        DBRequest.deleteTable("");
        Employee emp1 = new Employee(98, "Name", "Position", Date.valueOf("2016-01-01"), 1, "Some text");
        Employee emp2 = new Employee(99, "Name2", position, Date.valueOf("2016-01-01"), 3000.50, "Some text2");
        Employee emp3 = new Employee(100, "Name2", position, Date.valueOf("2016-01-01"), 7000.50, "Some text2");
        DBRequest.insertEmployee(emp1);    //  Считали из исходного файла и загрузили в БД
        DBRequest.insertEmployee(emp2);    //  Считали из исходного файла и загрузили в БД
        DBRequest.insertEmployee(emp3);    //  Считали из исходного файла и загрузили в БД
        Assert.assertSame(0, Double.compare(5000.50, DBRequest.getAverageSalaryByPosition(position)));
    }

    @Test
    public void fullTest() {
        DBRequest.deleteTable("");
        String fileName = "testIn.csv";
        String fileName2 = "testOut.csv";
        Employee emp = new Employee(98, "Name", "Position", Date.valueOf("2016-01-01"), 6543.21, "Some text");
        Employee emp2 = new Employee(99, "Name2", "Position2", Date.valueOf("2016-01-01"), 4443.21, "Some text2");
        AdditionalInfo addInfoEmp1 = new AdditionalInfo(98, "88005553535", "Some Info employee 1");
        AdditionalInfo addInfoEmp2 = new AdditionalInfo(99, "88002222222", "Some Info employee 2");
        List<Employee> empList = new ArrayList<>();
        List<AdditionalInfo> addInfoList = new ArrayList<>();
        empList.add(emp);
        empList.add(emp2);
        addInfoList.add(addInfoEmp1);
        addInfoList.add(addInfoEmp2);
        List[] list = new List[2];
        list[0] = empList;
        list[1] = addInfoList;
        FilesIO.writeFile(fileName, list);  //  Записали в исходный файл
        DBRequest.insertAll(FilesIO.readFile(fileName));    //  Считали из исходного файла и загрузили в БД
        FilesIO.writeFile(fileName2, DBRequest.selectAll()); //  Считали из БД и выгрузили в выходной файл
        Assert.assertEquals(list[0], FilesIO.readFile(fileName2)[0]);
        Assert.assertEquals(list[1], FilesIO.readFile(fileName2)[1]);
    }

    @Test
    public void testDelete() {
        DBRequest.deleteTable("");
        List[] resultList = DBRequest.selectAll();
        Assert.assertNull(resultList);
    }

    @AfterClass
    public static void disconnect() {
        DBRequest.dbDisconnect();
    }
}
