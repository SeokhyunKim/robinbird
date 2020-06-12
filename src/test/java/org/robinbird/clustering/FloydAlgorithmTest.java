package org.robinbird.clustering;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import com.google.common.collect.Lists;
import com.google.common.testing.NullPointerTester;
import java.util.Map;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.robinbird.model.AccessLevel;
import org.robinbird.model.Class;
import org.robinbird.model.ComponentCategory;

public class FloydAlgorithmTest {

    @Test
    public void test_calculateDistances_calculateCorrectDistances_withTwoNodes() {
        /*
        c1  ---->  c2
         */
        Class c1 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c1")
                        .category(ComponentCategory.CLASS)
                        .build();
        Class c2 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c2")
                        .category(ComponentCategory.CLASS)
                        .build();
        c1.addMemberVariable(c2, "c2-var", AccessLevel.PRIVATE);

        Map<String, Map<String, NodeDistance>> dists = FloydAlgorithm.calculateDistances(Lists.newArrayList(c1, c2),
                                                                                         RelationSelectors::getComponentRelations);
        Assert.assertThat(dists.get(c1.getId()).get(c1.getId()), equalTo(NodeDistance.ZERO));
        Assert.assertThat(Double.compare(dists.get(c1.getId()).get(c2.getId()).getDistance(), 1.0), is(0));
        Assert.assertThat(dists.get(c2.getId()).get(c2.getId()), equalTo(NodeDistance.ZERO));
        Assert.assertThat(dists.get(c2.getId()).get(c1.getId()), equalTo(NodeDistance.INFINITE));
    }

    @Test
    public void test_calculateDistances_calculateCorectDistances_withTwoNodesAndBidirectionalRelation() {
        /*
        c1  <---->  c2
         */
        Class c1 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c1")
                        .category(ComponentCategory.CLASS)
                        .build();
        Class c2 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c2")
                        .category(ComponentCategory.CLASS)
                        .build();
        c1.addMemberVariable(c2, "c2-var", AccessLevel.PRIVATE);
        c2.addMemberVariable(c1, "c1-var", AccessLevel.PRIVATE);

        Map<String, Map<String, NodeDistance>> dists = FloydAlgorithm.calculateDistances(Lists.newArrayList(c1, c2),
                                                                                         RelationSelectors::getComponentRelations);
        Assert.assertThat(dists.get(c1.getId()).get(c1.getId()), equalTo(NodeDistance.ZERO));
        Assert.assertThat(Double.compare(dists.get(c1.getId()).get(c2.getId()).getDistance(), 0.5), is(0));
        Assert.assertThat(dists.get(c2.getId()).get(c2.getId()), equalTo(NodeDistance.ZERO));
        Assert.assertThat(Double.compare(dists.get(c2.getId()).get(c1.getId()).getDistance(), 0.5), is(0));
    }

    @Test
    public void test_calculateDistances_calculateCorectDistances_withTwoNodesAndMultipleRelations() {
        /*
        c1  ---->  c2
             |-->  c2
        c2  ---->  c1
         */
        Class c1 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c1")
                        .category(ComponentCategory.CLASS)
                        .build();
        Class c2 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c2")
                        .category(ComponentCategory.CLASS)
                        .build();
        c1.addMemberVariable(c2, "c2-var", AccessLevel.PRIVATE);
        c1.setOwnerComponent(c2);
        c2.addMemberVariable(c1, "c1-var", AccessLevel.PRIVATE);

        Map<String, Map<String, NodeDistance>> dists = FloydAlgorithm.calculateDistances(Lists.newArrayList(c1, c2),
                                                                                         RelationSelectors::getComponentRelations);
        Assert.assertThat(dists.get(c1.getId()).get(c1.getId()), equalTo(NodeDistance.ZERO));
        Assert.assertThat(Double.compare(dists.get(c1.getId()).get(c2.getId()).getDistance(), 2.0 / 3.0), is(0));
        Assert.assertThat(dists.get(c2.getId()).get(c2.getId()), equalTo(NodeDistance.ZERO));
        Assert.assertThat(Double.compare(dists.get(c2.getId()).get(c1.getId()).getDistance(), 1.0 / 3.0), is(0));
    }

    @Test
    public void test_calculateDistances_calculateCorectDistances_withSomeNodes() {
        /*
        c1  ---->  c2
             |-->  c3
             |-->  c4
        c3  ---->  c1
             |-->  c5
        c5  -----> c4
         */
        Class c1 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c1")
                        .category(ComponentCategory.CLASS)
                        .build();
        Class c2 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c2")
                        .category(ComponentCategory.CLASS)
                        .build();
        Class c3 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c3")
                        .category(ComponentCategory.CLASS)
                        .build();
        Class c4 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c4")
                        .category(ComponentCategory.CLASS)
                        .build();
        Class c5 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c5")
                        .category(ComponentCategory.CLASS)
                        .build();
        c1.addMemberVariable(c2, "c2-var", AccessLevel.PRIVATE);
        c1.addMemberVariable(c3, "c3-var", AccessLevel.PRIVATE);
        c1.addMemberVariable(c4, "c4-var", AccessLevel.PRIVATE);
        c3.addMemberVariable(c1, "c1-var", AccessLevel.PRIVATE);
        c3.addMemberVariable(c5, "c5-var", AccessLevel.PRIVATE);
        c5.addMemberVariable(c4, "c4-var", AccessLevel.PRIVATE);

        Map<String, Map<String, NodeDistance>> dists = FloydAlgorithm.calculateDistances(Lists.newArrayList(c1, c2, c3, c4, c5),
                                                                                         RelationSelectors::getComponentRelations);
        double nodeDist = 1.0 / 6.0;
        // checking c1
        Assert.assertThat(Double.compare(dists.get(c1.getId()).get(c2.getId()).getDistance(), nodeDist), is(0));
        Assert.assertThat(Double.compare(dists.get(c1.getId()).get(c3.getId()).getDistance(), nodeDist), is(0));
        Assert.assertThat(Double.compare(dists.get(c1.getId()).get(c4.getId()).getDistance(), nodeDist), is(0));
        Assert.assertThat(dists.get(c1.getId()).get(c5.getId()), equalTo(NodeDistance.INFINITE));
        // checking c2
        Assert.assertThat(dists.get(c2.getId()).get(c1.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c2.getId()).get(c3.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c2.getId()).get(c4.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c2.getId()).get(c5.getId()), equalTo(NodeDistance.INFINITE));
        // checking c3
        Assert.assertThat(Double.compare(dists.get(c3.getId()).get(c1.getId()).getDistance(), nodeDist), is(0));
        Assert.assertThat(Double.compare(dists.get(c3.getId()).get(c5.getId()).getDistance(), nodeDist), is(0));
        Assert.assertThat(dists.get(c3.getId()).get(c2.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c3.getId()).get(c4.getId()), equalTo(NodeDistance.INFINITE));
        // checking c4
        Assert.assertThat(dists.get(c4.getId()).get(c1.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c4.getId()).get(c2.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c4.getId()).get(c3.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c4.getId()).get(c5.getId()), equalTo(NodeDistance.INFINITE));
        // checking c5
        Assert.assertThat(Double.compare(dists.get(c5.getId()).get(c4.getId()).getDistance(), nodeDist), is(0));
        Assert.assertThat(dists.get(c5.getId()).get(c1.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c5.getId()).get(c2.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c5.getId()).get(c3.getId()), equalTo(NodeDistance.INFINITE));
    }

    @Test
    public void test_calculateDistances_calculateCorrectDistances_withSomeNodes_withMultipleEdgesToSameNode() {
        /*
        c1  ---->  c2
             |-->  c3
             |-->  c4
        c3  ---->  c1
            ---->  c1
             |-->  c5
        c5  -----> c3
            -----> c3
            -----> c4
            -----> c4
         */
        Class c1 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c1")
                        .category(ComponentCategory.CLASS)
                        .build();
        Class c2 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c2")
                        .category(ComponentCategory.CLASS)
                        .build();
        Class c3 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c3")
                        .category(ComponentCategory.CLASS)
                        .build();
        Class c4 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c4")
                        .category(ComponentCategory.CLASS)
                        .build();
        Class c5 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c5")
                        .category(ComponentCategory.CLASS)
                        .build();
        c1.addMemberVariable(c2, "c2-var", AccessLevel.PRIVATE);
        c1.addMemberVariable(c3, "c3-var", AccessLevel.PRIVATE);
        c1.addMemberVariable(c4, "c4-var", AccessLevel.PRIVATE);
        c3.addMemberVariable(c1, "c1-var-1", AccessLevel.PRIVATE);
        c3.addMemberVariable(c1, "c1-var-2", AccessLevel.PRIVATE);
        c3.addMemberVariable(c5, "c5-var", AccessLevel.PRIVATE);
        c5.addMemberVariable(c3, "c3-var-1", AccessLevel.PRIVATE);
        c5.addMemberVariable(c3, "c3-var-2", AccessLevel.PRIVATE);
        c5.addMemberVariable(c4, "c4-var-1", AccessLevel.PRIVATE);
        c5.addMemberVariable(c4, "c4-var-2", AccessLevel.PRIVATE);

        Map<String, Map<String, NodeDistance>> dists = FloydAlgorithm.calculateDistances(Lists.newArrayList(c1, c2, c3, c4, c5),
                                                                                         RelationSelectors::getComponentRelations);

        // checking c1
        Assert.assertThat(Double.compare(dists.get(c1.getId()).get(c2.getId()).getDistance(), 0.1), is(0));
        Assert.assertThat(Double.compare(dists.get(c1.getId()).get(c3.getId()).getDistance(), 0.1), is(0));
        Assert.assertThat(Double.compare(dists.get(c1.getId()).get(c4.getId()).getDistance(), 0.1), is(0));
        Assert.assertThat(dists.get(c1.getId()).get(c5.getId()), equalTo(NodeDistance.INFINITE));
        // checking c2
        Assert.assertThat(dists.get(c2.getId()).get(c1.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c2.getId()).get(c3.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c2.getId()).get(c4.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c2.getId()).get(c5.getId()), equalTo(NodeDistance.INFINITE));
        // checking c3
        Assert.assertThat(Double.compare(dists.get(c3.getId()).get(c1.getId()).getDistance(), 0.2), is(0));
        Assert.assertThat(Double.compare(dists.get(c3.getId()).get(c5.getId()).getDistance(), 0.1), is(0));
        Assert.assertThat(dists.get(c3.getId()).get(c2.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c3.getId()).get(c4.getId()), equalTo(NodeDistance.INFINITE));
        // checking c4
        Assert.assertThat(dists.get(c4.getId()).get(c1.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c4.getId()).get(c2.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c4.getId()).get(c3.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c4.getId()).get(c5.getId()), equalTo(NodeDistance.INFINITE));
        // checking c5
        Assert.assertThat(Double.compare(dists.get(c5.getId()).get(c3.getId()).getDistance(), 0.2), is(0));
        Assert.assertThat(Double.compare(dists.get(c5.getId()).get(c4.getId()).getDistance(), 0.2), is(0));
        Assert.assertThat(dists.get(c5.getId()).get(c1.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c5.getId()).get(c2.getId()), equalTo(NodeDistance.INFINITE));
    }

    @Test
    public void test_calculateDistances_calculateCorectDistances_withRelationsToSelf() {
        /*
        c1  ---->  c1
             |-->  c2
             |-->  c3
        c3  ---->  c3
             |-->  c4
         */
        Class c1 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c1")
                        .category(ComponentCategory.CLASS)
                        .build();
        Class c2 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c2")
                        .category(ComponentCategory.CLASS)
                        .build();
        Class c3 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c3")
                        .category(ComponentCategory.CLASS)
                        .build();
        Class c4 = Class.builder()
                        .id(UUID.randomUUID().toString())
                        .name("c4")
                        .category(ComponentCategory.CLASS)
                        .build();
        c1.addMemberVariable(c1, "c1-var", AccessLevel.PRIVATE);
        c1.addMemberVariable(c2, "c2-var", AccessLevel.PRIVATE);
        c1.addMemberVariable(c3, "c3-var", AccessLevel.PRIVATE);
        c3.addMemberVariable(c3, "c3-var", AccessLevel.PRIVATE);
        c3.addMemberVariable(c4, "c4-var", AccessLevel.PRIVATE);

        Map<String, Map<String, NodeDistance>> dists = FloydAlgorithm.calculateDistances(Lists.newArrayList(c1, c2, c3, c4),
                                                                                         RelationSelectors::getComponentRelations);
        // checking c1
        Assert.assertThat(dists.get(c1.getId()).get(c1.getId()), equalTo(NodeDistance.ZERO));
        Assert.assertThat(Double.compare(dists.get(c1.getId()).get(c2.getId()).getDistance(), 1.0/3.0), is(0));
        Assert.assertThat(Double.compare(dists.get(c1.getId()).get(c3.getId()).getDistance(), 1.0/3.0), is(0));
        Assert.assertThat(dists.get(c1.getId()).get(c4.getId()), equalTo(NodeDistance.INFINITE));
        // checking c3
        Assert.assertThat(dists.get(c3.getId()).get(c1.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c3.getId()).get(c2.getId()), equalTo(NodeDistance.INFINITE));
        Assert.assertThat(dists.get(c3.getId()).get(c3.getId()), equalTo(NodeDistance.ZERO));
        Assert.assertThat(Double.compare(dists.get(c3.getId()).get(c4.getId()).getDistance(), 1.0/3.0), is(0));
    }

    @Test
    public void test_nulls() {
        NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicStaticMethods(FloydAlgorithm.class);
    }
}
