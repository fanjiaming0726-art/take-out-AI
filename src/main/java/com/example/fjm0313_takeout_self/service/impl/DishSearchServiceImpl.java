package com.example.fjm0313_takeout_self.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.fjm0313_takeout_self.entity.Category;
import com.example.fjm0313_takeout_self.entity.Dish;
import com.example.fjm0313_takeout_self.es.DishDoc;
import com.example.fjm0313_takeout_self.es.repository.DishSearchRepository;
import com.example.fjm0313_takeout_self.mapper.CateGoryMapper;
import com.example.fjm0313_takeout_self.mapper.DishMapper;
import com.example.fjm0313_takeout_self.service.DishSearchService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishSearchServiceImpl implements DishSearchService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private CateGoryMapper cateGoryMapper;

    @Autowired
    private DishSearchRepository dishSearchRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Override
    public void saveDishToEs(Long dishId) {
        Dish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            return;
        }

        DishDoc dishDoc = convertToDishDoc(dish);
        dishSearchRepository.save(dishDoc);
    }

    @Override
    public void deleteDishFromEs(Long dishId) {
        dishSearchRepository.deleteById(dishId);
    }

    @Override
    public void rebuildDishIndex() {
        dishSearchRepository.deleteAll();

        List<Dish> dishList = dishMapper.selectList(null);
        List<DishDoc> dishDocList = new ArrayList<>();

        for (Dish dish : dishList) {
            dishDocList.add(convertToDishDoc(dish));
        }

        dishSearchRepository.saveAll(dishDocList);
    }

    @Override
    public List<DishDoc> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .bool(b -> b
                                .should(s -> s.match(m -> m
                                        .field("name")
                                        .query(keyword)
                                ))
                                .should(s -> s.match(m -> m
                                        .field("description")
                                        .query(keyword)
                                ))
                                .should(s -> s.match(m -> m
                                        .field("categoryName")
                                        .query(keyword)
                                ))
                        )
                )
                .withMaxResults(20)
                .build();

        // 将search返回来的结果全部映射成DishDoc，然后通过流，将每一个SearchHit"提纯"，最后汇成一个表
        return elasticsearchOperations.search(query, DishDoc.class)

                // 将返回结果排成一条流水线
                .stream()

                // 让stream里面的每一个数据都进行一次map匹配
                .map(SearchHit::getContent)
                .toList();
    }

    private DishDoc convertToDishDoc(Dish dish) {
        DishDoc dishDoc = new DishDoc();
        BeanUtils.copyProperties(dish, dishDoc);

        if (dish.getCategoryId() != null) {
            Category category = cateGoryMapper.selectById(dish.getCategoryId());
            if (category != null) {
                dishDoc.setCategoryName(category.getName());
            }
        }

        return dishDoc;
    }
}