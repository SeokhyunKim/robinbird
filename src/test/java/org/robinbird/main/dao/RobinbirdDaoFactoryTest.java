package org.robinbird.main.dao;

import com.google.common.testing.NullPointerTester;
import org.junit.Test;

public class RobinbirdDaoFactoryTest {

    @Test
    public void test_nulls() {
        NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicStaticMethods(RobinbirdDaoFactory.class);
    }
}
