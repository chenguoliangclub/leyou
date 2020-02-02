package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import com.sun.javafx.collections.MappingChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class PageService {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private TemplateEngine templateEngine;

    public Map<String,Object> loadModel(Long spuId){
        Map<String,Object> map = new HashMap<>();
        Spu spu = goodsClient.querySpuById(spuId);
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spuId);
        List<Sku> skus = goodsClient.querySkuBySpuId(spuId);
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        List<Category> categories = categoryClient.getCategoryListByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        List<SpecGroup> spec = specificationClient.querySpecsByCid(spu.getCid3());
        //- spu信息
        map.put("spu",spu);
        //- spu的详情
        map.put("spuDetail",spuDetail);
        //- spu下的所有sku
        map.put("sku",skus);
        //- 品牌
        map.put("brand",brand);
        //- 商品三级分类
        map.put("category",categories);
        //- 商品规格参数、规格参数组
        map.put("spec",spec);
        return map;
    }

    public void createHtml(Long spuId){
        //创建上下文
        Context context = new Context();
        //保存数据到上下文
        context.setVariables(loadModel(spuId));
        //新建目标文件
        File file = new File("C:\\Users\\chen\\Desktop\\。。。",spuId + ".html");

        if (file.exists()){
            file.delete();
        }
        try {
            PrintWriter printWriter = new PrintWriter(file, "utf-8");
            templateEngine.process("item",context,printWriter);
        } catch (Exception e) {
            log.error("【静态页面】保存文件错误",e);
        }

    }

    public void deleteHtml(Long spuId) {
        File file = new File("C:\\Users\\chen\\Desktop\\。。。",spuId + ".html");
        if (file.exists()){
            file.delete();
        }
    }
}
