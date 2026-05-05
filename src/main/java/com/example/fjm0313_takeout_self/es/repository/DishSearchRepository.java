package com.example.fjm0313_takeout_self.es.repository;

import com.example.fjm0313_takeout_self.es.DishDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DishSearchRepository extends ElasticsearchRepository<DishDoc, Long> {
}