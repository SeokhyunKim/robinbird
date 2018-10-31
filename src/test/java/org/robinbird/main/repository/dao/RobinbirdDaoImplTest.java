package org.robinbird.main.repository.dao;

import static org.hamcrest.CoreMatchers.is;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.robinbird.main.model.RobinbirdObject;
import org.robinbird.main.model.Type;

@Ignore
public class RobinbirdDaoImplTest {

    /*private RobinbirdDaoImpl daoImpl =
            (RobinbirdDaoImpl)RobinbirdDaoFactory.createDao("mem:");

    @Test
    public void test_save_and_load_Type() {
        Type type = Type.builder().kind(Type.Kind.REFERENCE).varargs(false).name("test").build();
        long id = daoImpl.save(type);
        RobinbirdObject obj = daoImpl.load(id);
        Assert.assertTrue(obj instanceof Type);
        Assert.assertThat(obj.getName(), is("test"));
    }

    @Test
    public void test_load_Type() {

    }

    @Test
    public void test_loadAll() {
        Type type1 = Type.builder().kind(Type.Kind.REFERENCE).varargs(false).name("test1").build();
        Type type2 = Type.builder().kind(Type.Kind.REFERENCE).varargs(false).name("test2").build();
        Type type3 = Type.builder().kind(Type.Kind.REFERENCE).varargs(false).name("test3").build();
        daoImpl.save(type1);
        daoImpl.save(type2);
        daoImpl.save(type3);
        List<RobinbirdObject> allObjs = daoImpl.loadAll();
        List<String> names = allObjs.stream().map(o -> o.getName()).collect(Collectors.toList());
        Assert.assertTrue(names.contains("test1"));
        Assert.assertTrue(names.contains("test2"));
        Assert.assertTrue(names.contains("test3"));
    }

    @Test
    public void test_getTotalNumber() {
        Type type1 = Type.builder().kind(Type.Kind.REFERENCE).varargs(false).name("test1").build();
        Type type2 = Type.builder().kind(Type.Kind.REFERENCE).varargs(false).name("test2").build();
        Type type3 = Type.builder().kind(Type.Kind.REFERENCE).varargs(false).name("test3").build();
        daoImpl.save(type1);
        daoImpl.save(type2);
        daoImpl.save(type3);
        Assert.assertEquals(daoImpl.getTotalNumber(), 3);
    }

    @Test
    public void test_isExist() {
        Type type1 = Type.builder().kind(Type.Kind.REFERENCE).varargs(false).name("test1").build();
        daoImpl.save(type1);
        Assert.assertTrue(daoImpl.isExist("test1"));
        Assert.assertFalse(daoImpl.isExist("notExisting"));
    }*/


}
