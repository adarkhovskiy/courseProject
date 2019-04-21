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
        Assert.assertEquals(list[0], FilesIO.readFile(fileName)[0]);
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
