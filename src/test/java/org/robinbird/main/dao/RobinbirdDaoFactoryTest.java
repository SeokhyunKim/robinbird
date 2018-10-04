package org.robinbird.main.dao;

import com.google.common.testing.NullPointerTester;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;
import org.robinbird.main.model.Type;

public class RobinbirdDaoFactoryTest {

    @Test
    public void test_withDbFileName() throws IOException {
        final String curDir = System.getProperty("user.dir");
        System.out.println("cur directory: " + curDir);
        RobinbirdDaoFactory.createDao("test");
        Path dbPath = Paths.get(curDir, "test.h2.db");
        Assert.assertTrue(Files.isRegularFile(dbPath));
        Files.delete(dbPath);
    }

    @Test
    public void test_inMemoryMode() {
        RobinbirdDao dao = RobinbirdDaoFactory.createDao();
        dao.save(Type.builder().name("test").kind(Type.Kind.REFERENCE).build());
        Assert.assertEquals(dao.getTotalNumber(), 1);
    }

    @Test
    public void test_nulls() {
        NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicStaticMethods(RobinbirdDaoFactory.class);
    }
}
