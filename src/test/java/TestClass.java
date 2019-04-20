import com.adarkhovskiy.courseProject.DBRequest;
import com.adarkhovskiy.courseProject.Employee;
import com.adarkhovskiy.courseProject.FilesIO;
import org.junit.*;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TestClass {

    @Before
    public void init() {
        DBRequest.dbConnect();
    }

    @Test
    public void fullTest() {
        Date d1 = Date.valueOf("2016-01-01");
        String fileName = "testIn.csv";
        String fileName2 = "testIn2.csv";
        Employee emp = new Employee(98, "Name", "Position", d1, 6543.21, "Some text");
        Employee emp2 = new Employee(99, "Name2", "Position2", d1, 4443.21, "Some text2");
        List<Employee> empList = new ArrayList<>();
        empList.add(emp);
        empList.add(emp2);
        List[] list = new List[2];
        list[0] = empList;
        FilesIO.writeFile(fileName, list);  //  Записали в исходный файл
        DBRequest.insertAll(FilesIO.readFile(fileName));    //  Считали из исходного файла и загрузили в БД
        FilesIO.writeFile(fileName2, DBRequest.selectAll()); //  Считали из БД и выгрузили в выходной файл
        Assert.assertEquals(list[0],FilesIO.readFile(fileName2)[0]);

    }

    public void testDelete() throws ParseException, SQLException {
        DBRequest.deleteTable("");
    }

    @After
    public void disconnect() {
        DBRequest.dbDisconnect();
    }
}
