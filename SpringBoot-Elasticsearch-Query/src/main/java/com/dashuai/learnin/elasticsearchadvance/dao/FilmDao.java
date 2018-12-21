package com.dashuai.learnin.elasticsearchadvance.dao;

import com.dashuai.learnin.elasticsearchadvance.model.FilmEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * The interface Film dao.
 *
 * @author Liaozihong
 */
public interface FilmDao extends ElasticsearchRepository<FilmEntity, Long> {

}