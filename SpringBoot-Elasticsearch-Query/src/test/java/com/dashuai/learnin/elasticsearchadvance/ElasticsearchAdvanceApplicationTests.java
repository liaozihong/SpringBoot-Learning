package com.dashuai.learnin.elasticsearchadvance;

import com.dashuai.learnin.elasticsearchadvance.config.EsConfiguration;
import com.dashuai.learning.utils.json.JSONParseUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchAdvanceApplicationTests {

    @Autowired
    EsConfiguration esConfiguration;
    private String index;
    private Client client;

    @Before
    public void init() {
        index = "film-entity";
        client = esConfiguration.esTemplate();
    }

    /**
     * 普通查询
     */
    @Test
    public void testSearch() {
        SearchRequestBuilder searchQuery = client.prepareSearch(index)    // 在prepareSearch()的参数为索引库列表，意为要从哪些索引库中进行查询
                .setSearchType(SearchType.DEFAULT)  // 设置查询类型，有QUERY_AND_FETCH  QUERY_THEN_FETCH  DFS_QUERY_AND_FETCH  DFS_QUERY_THEN_FETCH
                .setQuery(QueryBuilders.matchQuery("name", "小"));
        // 如果上面不加查询条件，则会查询所有
        showResult(searchQuery.get());
    }

    /**
     * 精确查询
     */
    @Test
    public void testSearch1() {
        SearchRequestBuilder searchQuery = client.prepareSearch(index)    // 在prepareSearch()的参数为索引库列表，意为要从哪些索引库中进行查询
                .setSearchType(SearchType.DEFAULT)  // 设置查询类型，有QUERY_AND_FETCH  QUERY_THEN_FETCH  DFS_QUERY_AND_FETCH  DFS_QUERY_THEN_FETCH
                .setQuery(QueryBuilders.termQuery("name", "小明"));
        // 如果上面不加查询条件，则会查询所有
        showResult(searchQuery.get());
    }

    /**
     * 2.模糊查询
     * prefixQuery
     */
    @Test
    public void testSearch2() {
        SearchResponse response = client.prepareSearch(index).setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.prefixQuery("name", "小"))
                .get();
        showResult(response);
    }

    /**
     * 3.分页查询
     * 查询索引库bank中
     * 年龄在(25, 35]之间的数据信息
     * <p>
     * 分页算法：
     * 查询的第几页，每一页显示几条
     * 每页显示10条记录
     * <p>
     * 查询第4页的内容
     * setFrom(30=(4-1)*size)
     * setSize(10)
     * 所以第N页的起始位置：(N - 1) * pageSize
     */
    @Test
    public void testSearch3() {
        // 注意QUERY_THEN_FETCH和注意QUERY_AND_FETCH返回的记录数不一样，前者默认10条，后者是50条（5个分片）
        SearchResponse response = client.prepareSearch(index).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.rangeQuery("id").gt(0).lte(2))
                // 下面setFrom和setSize用于设置查询结果进行分页
                .setFrom(0)
                .setSize(1)
                .get();
        showResult(response);
    }

    /**
     * 4.高亮显示查询
     * 获取数据，
     * 查询apache，不仅在author拥有，也可以在url，在name中也可能拥有
     * author or url   --->booleanQuery中的should操作
     * 如果是and的类型--->booleanQuery中的must操作
     * 如果是not的类型--->booleanQuery中的mustNot操作
     * 使用的match操作，其实就是使用要查询的keyword和对应字段进行完整匹配，是否相等，相等返回
     */
    @Test
    public void testSearch4() {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //高亮显示规则
        highlightBuilder.preTags("<span style='color:green'>");
        highlightBuilder.postTags("</span>");
        //指定高亮字段
        highlightBuilder.field("name");
        highlightBuilder.field("director");
        String[] fileds = {"name", "director"};
        QueryBuilder matchQuery = QueryBuilders.multiMatchQuery("小", fileds);
        SearchResponse response = client.prepareSearch(index).setSearchType(SearchType.DEFAULT)
                .setQuery(matchQuery)
                .highlighter(highlightBuilder)
                .execute().actionGet();
        SearchHits searchHits = response.getHits();
        float maxScore = searchHits.getMaxScore();  // 查询结果中的最大文档得分
        System.out.println("maxScore: " + maxScore);
        long totalHits = searchHits.getTotalHits(); // 查询结果记录条数
        System.out.println("totalHits: " + totalHits);
        SearchHit[] hits = searchHits.getHits();    // 查询结果
        System.out.println("当前返回结果记录条数：" + hits.length);
        for (SearchHit hit : searchHits) {
            System.out.println("========================================================");
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            for (Map.Entry<String, HighlightField> me : highlightFields.entrySet()) {
                System.out.println("--------------------------------------");
                String key = me.getKey();
                HighlightField highlightField = me.getValue();
                String name = highlightField.getName();
                System.out.println("key: " + key + ", name: " + name);
                Text[] texts = highlightField.fragments();
                String value = "";
                for (Text text : texts) {
                    value += text.toString();
                }
                System.out.println("value: " + value);
            }
        }
    }

    /**
     * 5.排序查询
     * 对结果集进行排序
     * balance（收入）由高到低
     */
    @Test
    public void testSearch5() {
        // 注意QUERY_THEN_FETCH和注意QUERY_AND_FETCH返回的记录数不一样，前者默认10条，后者是50条（5个分片）
        SearchResponse response = client.prepareSearch(index).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.rangeQuery("id").gt(0).lte(3))
                .addSort("id", SortOrder.DESC)
                // 下面setFrom和setSize用于设置查询结果进行分页
                .setFrom(0)
                .setSize(5)
                .get();
        showResult(response);
    }

    /**
     * 6.聚合查询：计算平均值
     */
    @Test
    public void testSearch6() {
        // 注意QUERY_THEN_FETCH和注意QUERY_AND_FETCH返回的记录数不一样，前者默认10条，后者是50条（5个分片）
        SearchResponse response = client.prepareSearch(index).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.rangeQuery("id").gt(0).lte(3))
                /*
                            select avg(age) as avg_name from person;
                            那么这里的avg("balance")--->就是返回结果avg_name这个别名
                         */
                .addAggregation(AggregationBuilders.avg("avg_id").field("id"))
                .addAggregation(AggregationBuilders.max("max").field("id"))
                .get();
        //        System.out.println(response);
    /*
            response中包含的Aggregations
                "aggregations" : {
                    "max" : {
                      "value" : 49741.0
                    },
                    "avg_balance" : {
                      "value" : 25142.137373737372
                    }
                  }
                  则一个aggregation为：
                  {
                      "value" : 49741.0
                    }
         */
        Aggregations aggregations = response.getAggregations();
        List<Aggregation> aggregationList = aggregations.asList();
        for (Aggregation aggregation : aggregationList) {
            System.out.println("========================================");
            String name = aggregation.getName();
            // Map<String, Object> map = aggregation.getMetaData();
            System.out.println("name: " + name);
            Object obj = aggregation.getType();
            System.out.println(JSONParseUtils.object2JsonString(aggregation));
        }
    /*Aggregation avgBalance = aggregations.get("avg_balance");
        Object obj = avgBalance.getProperty("value");
        System.out.println(obj);*/
    }

    public void showResult(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        float maxScore = searchHits.getMaxScore();  // 查询结果中的最大文档得分
        System.out.println("maxScore: " + maxScore);
        long totalHits = searchHits.getTotalHits(); // 查询结果记录条数
        System.out.println("totalHits: " + totalHits);
        SearchHit[] hits = searchHits.getHits();    // 查询结果
        System.out.println("当前返回结果记录条数：" + hits.length);
        for (SearchHit hit : hits) {
            long version = hit.getVersion();
            String id = hit.getId();
            String index = hit.getIndex();
            String type = hit.getType();
            float score = hit.getScore();
            System.out.println("===================================================");
            String source = hit.getSourceAsString();
            System.out.println("version: " + version);
            System.out.println("id: " + id);
            System.out.println("index: " + index);
            System.out.println("type: " + type);
            System.out.println("score: " + score);
            System.out.println("source: " + source);
        }
    }
}

