package com.dashuai.learnin.elasticsearchadvance.service.impl;

import com.dashuai.learnin.elasticsearchadvance.config.EsConfiguration;
import com.dashuai.learnin.elasticsearchadvance.dao.FilmDao;
import com.dashuai.learnin.elasticsearchadvance.model.FilmEntity;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Film service
 * <p/>
 * Created in 2018.12.21
 * <p/>
 *
 * @author Liaozihong
 */
@Service
public class FilmService {

    @Autowired
    private FilmDao filmDao;

    /**
     * The Es config.
     */
    @Autowired
    EsConfiguration esConfig;


    /**
     * Save.
     *
     * @param filmEntity the film entity
     */
    public void save(FilmEntity filmEntity) {
        filmDao.save(filmEntity);
    }

    /**
     * 拼接搜索条件
     *
     * @param name     the name
     * @param director the director
     * @return list
     */
    public List<FilmEntity> search(String name, String director) {
        //使用中文拼音混合搜索，取分数最高的，具体评分规则可参照：
        //  https://blog.csdn.net/paditang/article/details/79098830
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(structureQuery(name))
                .build();
        List<FilmEntity> list = filmDao.search(searchQuery).getContent();
        return list;
    }

    /**
     * 中文、拼音混合搜索
     *
     * @param content the content
     * @return dis max query builder
     */
    public DisMaxQueryBuilder structureQuery(String content) {
        //使用dis_max直接取多个query中，分数最高的那一个query的分数即可
        DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();
        //boost 设置权重,只搜索匹配name和disrector字段
        QueryBuilder ikNameQuery = QueryBuilders.matchQuery("name", content).boost(2f);
        QueryBuilder pinyinNameQuery = QueryBuilders.matchQuery("name.pinyin", content);
        QueryBuilder ikDirectorQuery = QueryBuilders.matchQuery("director", content).boost(2f);
        disMaxQueryBuilder.add(ikNameQuery);
        disMaxQueryBuilder.add(pinyinNameQuery);
        disMaxQueryBuilder.add(ikDirectorQuery);
        return disMaxQueryBuilder;
    }

    /**
     * 拼接搜索条件
     *
     * @param name     the name
     * @param director the director
     * @return list
     */
    public List<FilmEntity> searchHinghlight(String name, String director) {
        List<FilmEntity> list = search(name);
        return list;
    }

    /**
     * <p>高亮查询内容, query的值查询两个字段name, director。当然了你可以配置查询更多个字段或者你可以改成你所需查询的字段</p>
     *
     * @param query the query
     * @return List<FilmEntity>  list
     */
    public List<FilmEntity> search(String query) {
        Client client = esConfig.esTemplate();
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //高亮显示规则
        highlightBuilder.preTags("<span style='color:green'>");
        highlightBuilder.postTags("</span>");
        //指定高亮字段
        highlightBuilder.field("name");
        highlightBuilder.field("name.pinyin");
        highlightBuilder.field("director");
        String[] fileds = {"name", "name.pinyin", "director"};
        QueryBuilder matchQuery = QueryBuilders.multiMatchQuery(query, fileds);
        //搜索数据
        SearchResponse response = client.prepareSearch("film-entity")
                .setQuery(matchQuery)
                .highlighter(highlightBuilder)
                .execute().actionGet();

        SearchHits searchHits = response.getHits();
        System.out.println("记录数-->" + searchHits.getTotalHits());

        List<FilmEntity> list = new ArrayList<>();

        for (SearchHit hit : searchHits) {
            FilmEntity entity = new FilmEntity();
            Map<String, Object> entityMap = hit.getSourceAsMap();
            System.out.println(hit.getHighlightFields());
            //高亮字段
            if (!StringUtils.isEmpty(hit.getHighlightFields().get("name"))) {
                Text[] text = hit.getHighlightFields().get("name").getFragments();
                entity.setName(text[0].toString());
                entity.setDirector(String.valueOf(entityMap.get("director")));
            }
            if (!StringUtils.isEmpty(hit.getHighlightFields().get("name.pinyin"))) {
                Text[] text = hit.getHighlightFields().get("name.pinyin").getFragments();
                entity.setName(text[0].toString());
                entity.setDirector(String.valueOf(entityMap.get("director")));
            }
            if (!StringUtils.isEmpty(hit.getHighlightFields().get("director"))) {
                Text[] text = hit.getHighlightFields().get("director").getFragments();
                entity.setDirector(text[0].toString());
                entity.setName(String.valueOf(entityMap.get("name")));
            }

            //map to object
            if (!CollectionUtils.isEmpty(entityMap)) {
                if (!StringUtils.isEmpty(entityMap.get("id"))) {
                    entity.setId(Long.valueOf(String.valueOf(entityMap.get("id"))));
                }
                if (!StringUtils.isEmpty(entityMap.get("language"))) {
                    entity.setLanguage(String.valueOf(entityMap.get("language")));
                }
            }
            list.add(entity);
        }
        return list;
    }

    /**
     * save
     */
    public void save() {
        FilmEntity filmEntity = new FilmEntity();
        filmEntity.setName("test, you are great bang，好好学习，天天上上");
        filmEntity.setDirector("你才");
        this.save(filmEntity);
    }

    /**
     * 调用 ES 获取 IK 分词后结果
     *
     * @param content      the content
     * @param analyzerType the analyzer type
     * @return analyze result
     */
    public List<AnalyzeResponse.AnalyzeToken> getAnalyzeResult(String content, String analyzerType) {
        AnalyzeRequest analyzeRequest = new AnalyzeRequest("film-entity")
                .text(content)
                .analyzer(Optional.ofNullable(analyzerType).orElse("ik_max_word"));
        List<AnalyzeResponse.AnalyzeToken> tokens = esConfig.esTemplate().admin().indices()
                .analyze(analyzeRequest)
                .actionGet()
                .getTokens();
        return tokens;
    }
}