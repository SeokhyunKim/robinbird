package org.robinbird.main.newrepository;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Assert;
import org.junit.Test;
import org.robinbird.main.newmodel.Type;
import org.robinbird.main.newmodel.TypeCategory;
import org.robinbird.main.newrepository.dao.TypeDao;
import org.robinbird.main.newrepository.dao.TypeDaoFactory;

public class TypeRepositoryImplTest {

    private TypeDao dao = TypeDaoFactory.createDao("mem:");

    private TypeRepository repository = new TypeRepositoryImpl(dao);

    @Test
    public void test_registerType_getType_deleteType() {
        final Type t = repository.registerType(TypeCategory.CLASS, "test");
        Assert.assertThat(t.getCategory(), is(TypeCategory.CLASS));
        Assert.assertThat(t.getName(), is("test"));

        final Type loaded1 = repository.getType(t.getId()).get();
        final Type loaded2 = repository.getType("test").get();
        Assert.assertThat(t, is(loaded1));
        Assert.assertThat(t, is(loaded2));

        repository.deleteType(t.getId());
        final Type loaded3 = repository.getType(t.getId()).get();
        Assert.assertNull(loaded3);
    }

}
