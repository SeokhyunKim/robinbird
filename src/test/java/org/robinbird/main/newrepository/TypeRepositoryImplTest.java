package org.robinbird.main.newrepository;

import static org.hamcrest.CoreMatchers.is;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.robinbird.main.newmodel.Instance;
import org.robinbird.main.newmodel.Relation;
import org.robinbird.main.newmodel.RelationCategory;
import org.robinbird.main.newmodel.Type;
import org.robinbird.main.newmodel.TypeCategory;
import org.robinbird.main.newrepository.dao.TypeDao;
import org.robinbird.main.newrepository.dao.TypeDaoFactory;

import com.google.common.collect.ImmutableList;

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
        final Optional<Type> loaded3 = repository.getType(t.getId());
        Assert.assertFalse(loaded3.isPresent());
    }
    
    @Test
    public void test_registerType_deleteTypeWithName() {
        final Type t = repository.registerType(TypeCategory.CLASS, "test");
        Assert.assertThat(t.getCategory(), is(TypeCategory.CLASS));
        Assert.assertThat(t.getName(), is("test"));
        
        repository.deleteType(t.getName());
        final Optional<Type> loaded = repository.getType(t.getName());
        Assert.assertFalse(loaded.isPresent());
    }
    
    @Test
    public void test_updateType_doNothing_withPrimitiveType() {
    	final TypeDao dao = Mockito.mock(TypeDao.class);
    	repository = new TypeRepositoryImpl(dao);
    	
    	final Type t = Type.builder()
    			           .category(TypeCategory.PRIMITIVE)
    			           .id(0)
    			           .name("testPrimitive").build();
        
        final Type t2 = Type.builder()
        		            .category(TypeCategory.CLASS)
        		            .id(0).name("test").build();
        final Instance i = Instance.builder()
        						   .type(t2)
        						   .name("testInstance")
        						   .build();
        final Relation r = Relation.builder()
        		                   .category(RelationCategory.ASSOCIATION)
        		                   .type(t2)
        		                   .build();
        
        final Type populated = t.populate(ImmutableList.of(t2), ImmutableList.of(i), ImmutableList.of(r));
        Assert.assertNull(populated.getCompositionTypes());
        Assert.assertNull(populated.getInstances());
        Assert.assertNull(populated.getRelations());
        
        repository.updateType(populated);
        
        Mockito.verifyZeroInteractions(dao);
    }
    
    @Test
    public void test_updateType_populateType() {
        final Type t = repository.registerType(TypeCategory.CLASS, "test");
        final Type t2 = repository.registerType(TypeCategory.CLASS, "testType");
        final Instance i = Instance.builder() 
        		   .type(t2)
				   .name("testInstance")
				   .build();
		final Relation r = Relation.builder()
		                .category(RelationCategory.ASSOCIATION)
		                .type(t2)
		                .build();
		final Type populated = t.populate(ImmutableList.of(t2), ImmutableList.of(i), ImmutableList.of(r));
		repository.updateType(populated);
		
		final Type loaded = repository.getType(populated.getId()).get();
		final Type populated2 = repository.populateType(loaded);
		Assert.assertThat(populated2.getCompositionTypes().size(), is(1));
		Assert.assertThat(populated2.getCompositionTypes().get(0), is(t2));
		Assert.assertThat(populated2.getInstances().size(), is(1));
		Assert.assertThat(populated2.getInstances().get(0), is(i));
		Assert.assertThat(populated2.getRelations().size(), is(1));
		Assert.assertThat(populated2.getRelations().get(0), is(r));
    }

}
