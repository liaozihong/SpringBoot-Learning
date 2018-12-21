## SpringBoot集成Elasticsearch 进阶，实现[中文、拼音、繁简体转换]搜索
### Elasticsearch 分词 
分词分为读时分词和写时分词。  
读时分词发生在用户查询时，ES 会即时地对用户输入的关键词进行分词，分词结果只存在内存中，当查询结束时，分词结果也会随即消失。而写时分词发生在文档写入时，ES 会对文档进行分词后，将结果存入倒排索引，该部分最终会以文件的形式存储于磁盘上，不会因查询结束或者 ES 重启而丢失。  
写时分词器需要在 mapping 中指定，而且一经指定就不能再修改，若要修改必须新建索引。    

分词一般在ES中有分词器处理。英文为Analyzer,它决定了分词的规则，Es默认自带了很多分词器，如：  
Standard、english、Keyword、Whitespace等等。默认的分词器为Standard，通过它们各自的功能可组合
成你想要的分词规则。分词器具体详情可查看官网：[分词器](https://www.elastic.co/guide/en/elasticsearch/reference/5.5/analysis-standard-analyzer.html)
另外，在常用的中文分词器、拼音分词器、繁简体转换插件。国内用的就多的分别是：  
[elasticsearch-analysis-ik](https://github.com/medcl/elasticsearch-analysis-ik)  
[elasticsearch-analysis-pinyin](https://github.com/medcl/elasticsearch-analysis-pinyin)  
[elasticsearch-analysis-stconvert](https://github.com/medcl/elasticsearch-analysis-stconvert)
可在以上链接找到自己对于的elasticsearch版本安装插件。  
这里提供一个我自己封装的elasticsearch 5.5.0 的Docker镜像，里面在官方镜像的基础上加入了以上三个个插件，链接：  
[liaodashuai/elasticsearch:1.0.2](https://cloud.docker.com/u/liaodashuai/repository/docker/liaodashuai/elasticsearch)  

简单了解至此，下面用SpringBoot 集成  
实现效果：  
```
打造匹配搜索和高亮搜索API  
使用中文、拼音和繁简体都能搜索到    
扩展另外众多的搜索方式，简单使用测试用例实现
```
### 集成SpringBoot 实现高亮显示、拼音搜索
1. 导入jar包,springboot 2.0.4只支持5.X版本的Es,注意版本对应，避免坑。  
```text
    compile group: 'org.springframework.boot', name: 'spring-boot-starter-data-elasticsearch', version: '2.0.6.RELEASE'
    compile 'org.elasticsearch.client:x-pack-transport:5.5.0'
```
2. 配置连接Es
```java
@Configuration
public class EsConfiguration {

    private Client esClient;

    /**
     * Transport client transport client.
     * 如果配置X-PACK ,则需要在此处配置用户信息
     *
     * @return the transport client
     */
    @Bean
    public Client transportClient() {
        TransportClient client = null;
        try {
            client = new PreBuiltXPackTransportClient(Settings.builder()
                    //嗅探集群状态
//                    .put("client.transport.sniff", true)
                    .put("cluster.name", "docker-cluster")
                    //如果有配置xpack插件，需要配置登录
                    .put("xpack.security.user", "elastic:changeme")
                    .build())
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("120.79.58.138"), 9300));
        } catch (UnknownHostException e) {
            log.error("elasticsearch 连接失败 !");
        }
        return client;
    }

    /**
     * 避免TransportClient每次使用创建和释放
     */
    public Client esTemplate() {
        if (StringUtils.isEmpty(esClient) || StringUtils.isEmpty(esClient.admin())) {
            esClient = transportClient();
            return esClient;
        }
        return esClient;
    }
}
```
3. 配置实体Mapping
```java
@Document(indexName = "film-entity", type = "film")
@Setting(settingPath = "/json/film-setting.json")
@Mapping(mappingPath = "/json/film-mapping.json")
public class FilmEntity {

    @Id
    private Long id;
//    @Field(type = FieldType.Text, searchAnalyzer = "ik_max_word", analyzer = "ik_smart")
    private String name;
    private String nameOri;
    private String publishDate;
    private String type;
    private String language;
    private String fileDuration;
    private String director;
//    @Field(type = FieldType.Date)
    private Date created ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameOri() {
        return nameOri;
    }

    public void setNameOri(String nameOri) {
        this.nameOri = nameOri;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFileDuration() {
        return fileDuration;
    }

    public void setFileDuration(String fileDuration) {
        this.fileDuration = fileDuration;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FilmEntity [id=" + id + ", name=" + name + ", director=" + director + "]";
    }
}
```
上面的Model有必要解释一下，SpringBoot 有为我们提供多种方式设置mapping，你可以按喜好选择使用，我选择
的使用@Mapping注解配置，使用es原生的方式进行设置，虽然有点小麻烦，但是更加直观了，也不仅限于java，也可以直接用curl或es控制台创建。  
film-mapping.json  
```json
{
  "film": {
    "_all": {
      "enabled": true
    },
    "properties": {
      "id": {
        "type": "integer"
      },
      "name": {
        "type": "text",
        "analyzer": "ikSearchAnalyzer",
        "search_analyzer": "ikSearchAnalyzer",
        "fields": {
          "pinyin": {
            "type": "text",
            "analyzer": "pinyinSimpleIndexAnalyzer",
            "search_analyzer": "pinyinSimpleIndexAnalyzer"
          }
        }
      },
      "nameOri": {
        "type": "text"
      },
      "publishDate": {
        "type": "text"
      },
      "type": {
        "type": "text"
      },
      "language": {
        "type": "text"
      },
      "fileDuration": {
        "type": "text"
      },
      "director": {
        "type": "text",
        "index": "true",
        "analyzer": "ikSearchAnalyzer"
      },
      "created": {
        "type": "date",
        "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
      }
    }
  }
}
```
另外，除了@Mapping，SpringBoot还为我们提供了另一强大的注解@Setting，该注解可以让我们为当前索引设置一些相关属性,相当于
elasticsearch中的settings配置，例如：  
film-setting.json   
```json
{
  "index": {
    "analysis": {
      "filter": {
        "edge_ngram_filter": {
          "type": "edge_ngram",
          "min_gram": 1,
          "max_gram": 50
        },
        "pinyin_simple_filter": {
          "type": "pinyin",
          "first_letter": "prefix",
          "padding_char": " ",
          "limit_first_letter_length": 50,
          "lowercase": true
        }
      },
      "char_filter": {
        "tsconvert": {
          "type": "stconvert",
          "convert_type": "t2s"
        }
      },
      "analyzer": {
        "ikSearchAnalyzer": {
          "type": "custom",
          "tokenizer": "ik_max_word",
          "char_filter": [
            "tsconvert"
          ]
        },
        "pinyinSimpleIndexAnalyzer": {
          "tokenizer": "keyword",
          "filter": [
            "pinyin_simple_filter",
            "edge_ngram_filter",
            "lowercase"
          ]
        }
      }
    }
  }
}
```
上面的JSON作用是创建两个分析器名为ikSearchAnalyzer，pinyinSimpleIndexAnalyzer,前者使用ik中文分词器加繁体转简体char_filter过滤，使得引用此分词器的字段在设置时，将会自动对中文进行分词和繁简体转换。
 pinyinSimpleIndexAnalyzer 使用pinyin分词器，并进行edge_ngram 过滤，大写转小写过滤。  
上述设置完后，启动应用，打开head插件，也可以使用google扩展，elasticsearch-head。  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fye85xx8cyj30yq0jgac7.jpg)  

创建好索引后，便可开始测试查询了。  
使用SpringBoot提供的ElasticsearchRepository<T,ID>构建简单查询，当然它也是有局限的，一些较复杂的查询，只能通过
SearchResponse 自定义设置。  
首先我们实现简单的普通查询,可以配合Repository，继承ElasticsearchRepository<T,ID>,简单的CRUD都提供了。  
```java
public interface FilmDao extends ElasticsearchRepository<FilmEntity, Long> {

}
```
先创建几条测试数据：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fyeadga1ajj315208vq3q.jpg)
service类,构建查询
```java
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
```  
输入拼音搜索“ceshi”可看到对应结果，当然中文也是可以的：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fyebeh7pwrj30dg0bswet.jpg)  
输入简体字搜索"测试",可看到对应结果
![](https://ws1.sinaimg.cn/large/006mOQRagy1fyebetm5ayj30dd0b2jrp.jpg)  
service类，构建高亮查询
```java
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
```
上面配置了高亮搜索字段[name,name.pinyin,director],也就是说匹配到这三个字段的高亮结果，则会加上自定义的
高亮显示规则:  
```
<span style='color:green'>...</span>   
```
输入拼音搜索“ceshi”可看到对应结果，当然中文也是可以的：    
![](https://ws1.sinaimg.cn/large/006mOQRagy1fyeb7oo7m2j30gh0arq3a.jpg)    
输入简体字搜索"测试",可看到对应结果   
![](https://ws1.sinaimg.cn/large/006mOQRagy1fyeba68lauj30ex0atglx.jpg)   
输入繁体字搜索"認爲",可看到对应结果，由于pinyin分词器影响还会取到小王。    
![](https://ws1.sinaimg.cn/large/006mOQRagy1fyebdatz65j30g60g7aan.jpg)    

实际上有搜索到有多个高亮结果的，这里只取第一个演示查看。  

大家肯定很好奇这分词到底是怎么分的，为此我专门提供一个接口，可以查看我们输入的搜索内容是怎样被分词的。  
api测试：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fyd9hqecqoj316c0i63zp.jpg)  
结果如下：  
```json
{
  "result": [
    {
      "term": "xiao",
      "startOffset": 0,
      "endOffset": 2,
      "position": 0,
      "positionLength": 1,
      "attributes": null,
      "type": "CN_WORD",
      "fragment": false
    },
    {
      "term": "xm",
      "startOffset": 0,
      "endOffset": 2,
      "position": 0,
      "positionLength": 1,
      "attributes": null,
      "type": "CN_WORD",
      "fragment": false
    },
    {
      "term": "ming",
      "startOffset": 0,
      "endOffset": 2,
      "position": 1,
      "positionLength": 1,
      "attributes": null,
      "type": "CN_WORD",
      "fragment": false
    }
  ],
  "msg": "",
  "code": 200,
  "is_success": true
}
```
可以看到，我们的分词器已经生效。  

以上示例源码以上传至GitHub：https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Elasticsearch-Query    

参考链接： 
[Elasticsearch 分词检索](https://elasticsearch.cn/article/771)  
[Java API 5.5.0](https://www.elastic.co/guide/en/elasticsearch/client/java-api/5.5/_bucket_aggregations.html)  
[Elasticsearch 结合SpringBoot 高亮显示查询](https://blog.csdn.net/Hello_Ray/article/details/82153042)  