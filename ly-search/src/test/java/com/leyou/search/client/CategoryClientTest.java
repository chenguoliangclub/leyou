package com.leyou.search.client;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.Spu;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SearchService searchService;

    @Autowired
    GoodsClient goodsClient;

    @Autowired
    GoodsRepository goodsRepository;
    @Test
    public void getCategoryListByIds() {
        List<Category> categories = categoryClient.getCategoryListByIds(Arrays.asList(1L, 2L, 3L));
        Assert.assertEquals(3,categories.size());
        for (Category category : categories) {
            System.out.println("category: " + category);
        }
    }

    @Test
    public void loadData(){
        //查询spu
        PageResult<Spu> pageResult = goodsClient.querySpuByPage(1, 100, true, null);
        //获取商品
        List<Goods> goodsList = pageResult.getItems().stream().map(searchService::buildGoods).collect(Collectors.toList());
        //放进索引库
        goodsRepository.saveAll(goodsList);
    }
}