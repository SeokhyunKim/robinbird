package org.robinbird.clustering;

import static org.hamcrest.CoreMatchers.is;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.testing.NullPointerTester;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robinbird.model.AccessLevel;
import org.robinbird.model.Class;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;

public class AgglomerativeClusteringTest {

    @Value
    @Builder
    private static class SimpleClusteringNode {
        @Builder.Default
        private final boolean isClusteringNode = true;
        private final String id;
        private final String name;
        @Builder.Default
        private final double score = 0.0;
        @Builder.Default
        private final List<SimpleClusteringNode> childs = new ArrayList<>();

        public String prettyPrint(int numTabs) {
            StringBuffer sb = new StringBuffer();
            int curTabs =  numTabs;
            while (curTabs-- > 0) {
                sb.append("\t");
            }
            final String tabs = sb.toString();
            sb = new StringBuffer();
            if (childs.isEmpty()) {
                return tabs + name;
            } else {
                sb.append(tabs).append(name).append(" (").append(score).append(")").append(" {\n");
                for (SimpleClusteringNode child : childs) {
                    String childStr = child.prettyPrint(numTabs + 1);
                    sb.append(childStr).append("\n");
                }
                sb.append(tabs).append("}");
            }
            return sb.toString();
        }
    }

    private AgglomerativeClustering agglomerativeClustering;
    private final ClusteringMethodFactory clusteringMethodFactory =
            new ClusteringMethodFactory(new ClusteringNodeFactory());

    @Before
    public void setUp() {
        agglomerativeClustering = (AgglomerativeClustering)clusteringMethodFactory.create(ClusteringMethodType.AGGLOMERATIVE);
    }

    @Test
    public void test_cluster_withTwoNodes() {
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
        List<Component> components = Lists.newArrayList(c1, c2);
        List<ClusteringNode> clusteringNodes =
                agglomerativeClustering.cluster(components, RelationSelectors::getComponentRelations, new double[]{10.0});
        Assert.assertThat(clusteringNodes.size(), is(1));
        Assert.assertThat(getAllNodeNames(clusteringNodes), is(Sets.newHashSet("c1", "c2")));
    }

    @Test
    public void test_cluster_withTwoNodesAndBidirectionalRelation() {
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
        List<Component> components = Lists.newArrayList(c1, c2);
        List<ClusteringNode> clusteringNodes =
                agglomerativeClustering.cluster(components, RelationSelectors::getComponentRelations, new double[]{10.0});
        Assert.assertThat(clusteringNodes.size(), is(1));
        Assert.assertThat(getAllNodeNames(clusteringNodes), is(Sets.newHashSet("c1", "c2")));
    }

    @Test
    public void test_cluster__withTwoNodesAndMultipleRelations() {
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
        c1.setParent(c2);
        c2.addMemberVariable(c1, "c1-var", AccessLevel.PRIVATE);
        List<Component> components = Lists.newArrayList(c1, c2);
        List<ClusteringNode> clusteringNodes =
                agglomerativeClustering.cluster(components, RelationSelectors::getComponentRelations, new double[] {10.0});
        Assert.assertThat(clusteringNodes.size(), is(1));
        Assert.assertThat(getAllNodeNames(clusteringNodes), is(Sets.newHashSet("c1", "c2")));
    }

    @Test
    public void test_cluster_withSomeNodes() {
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

        List<Component> components = Lists.newArrayList(c1, c2, c3, c4, c5);
        List<ClusteringNode> clusteringNodes =
                agglomerativeClustering.cluster(components, RelationSelectors::getComponentRelations, new double[] {10.0});
        Assert.assertThat(clusteringNodes.size(), is(1));
        Assert.assertThat(getAllNodeNames(clusteringNodes), is(Sets.newHashSet("c1", "c2", "c3", "c4", "c5")));

        List<SimpleClusteringNode> simpleClusteringNodes = buildSimpleClusteringNodes(clusteringNodes);
        for (SimpleClusteringNode scn : simpleClusteringNodes) {
            System.out.println(scn.prettyPrint(0));
        }

        clusteringNodes =
                agglomerativeClustering.cluster(components, RelationSelectors::getComponentRelations, new double[] {0.0});
        Assert.assertThat(clusteringNodes.size(), is(5));

        clusteringNodes =
                agglomerativeClustering.cluster(components, RelationSelectors::getComponentRelations, new double[] {5.0});
        Assert.assertThat(clusteringNodes.size(), is(4));

        clusteringNodes =
                agglomerativeClustering.cluster(components, RelationSelectors::getComponentRelations, new double[] {8.0});
        Assert.assertThat(clusteringNodes.size(), is(2));
    }

    private Set<String> getAllNodeNames(Collection<ClusteringNode> nodes) {
        Set<String> names = new HashSet<>();
        for (ClusteringNode node : nodes) {
            names.addAll(getAllNodeNames(node));
        }
        return names;
    }

    private Set<String> getAllNodeNames(ClusteringNode clusteringNode) {
        Set<String> names = new HashSet<>();
        for (Component childNode : clusteringNode.getMemberNodes()) {
            if (childNode.getComponentCategory() == ComponentCategory.CLUSTERING_NODE) {
                names.addAll(getAllNodeNames((ClusteringNode) childNode));
            } else {
                names.add(childNode.getName());
            }
        }
        return names;
    }

    private List<SimpleClusteringNode> buildSimpleClusteringNodes(Collection<ClusteringNode> nodes) {
        List<SimpleClusteringNode> scNodes = new ArrayList<>();
        for (ClusteringNode node : nodes) {
            scNodes.add(buildSimpleClusteringNode(node));
        }
        return scNodes;
    }

    private SimpleClusteringNode buildSimpleClusteringNode(Component node) {
        double score = 0.0;
        if (node.getComponentCategory() == ComponentCategory.CLUSTERING_NODE) {
            score = ((AgglomerativeClusteringNode)node).getScore();
        }
        SimpleClusteringNode scNode = SimpleClusteringNode.builder()
                                                          .id(node.getId())
                                                          .name(node.getName())
                                                          .score(score)
                                                          .isClusteringNode(node.getComponentCategory() == ComponentCategory.CLUSTERING_NODE)
                                                          .build();
        if (node.getComponentCategory() == ComponentCategory.CLUSTERING_NODE) {
            ClusteringNode cn = (ClusteringNode)node;
            for (Component childNode : cn.getMemberNodes()) {
                scNode.getChilds().add(buildSimpleClusteringNode(childNode));
            }
        }
        return scNode;
    }

    @Test
    public void test_nulls() {
        NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicConstructors(AgglomerativeClustering.class);
        tester.testAllPublicStaticMethods(AgglomerativeClustering.class);
        tester.testAllPublicInstanceMethods(agglomerativeClustering);
    }
}
