package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.utills.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    BrandClient brandClient;

    @Autowired
    CategoryClient categoryClient;

    @Autowired
    GoodsClient goodsClient;

    @Autowired
    SpecificationClient specificationClient;

    @Autowired
    GoodsRepository goodsRepository;

    public Goods buildGoods(Spu spu){
        Goods good = new Goods();
        //商品分类集合
        List<Category> categories = categoryClient.getCategoryListByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        List<String> name = categories.stream().map(Category::getName).collect(Collectors.toList());
        //搜索关键字
        String all =spu.getTitle() + StringUtils.join(name," ");
        //商品sku
        List<Sku> skus = goodsClient.querySkuBySpuId(spu.getId());
        //商品价格
        List<Long> priceList = new ArrayList<>();
        for (Sku sku : skus) {
            priceList.add(sku.getPrice());
        }
        //商品详情
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spu.getId());
        //商品通用规格参数
        Map<Long,String> generic = JsonUtils.parseMap(spuDetail.getGeneric_spec(),Long.class,String.class);
        //商品特有规格参数
        Map<Long,List<String>> special = JsonUtils.nativeRead(spuDetail.getSpecial_spec(), new TypeReference<Map<Long, List<String>>>(){});
        //商品规格参数
        Map<String,Object> specs = new HashMap<>();
        List<SpecParam> specParams = specificationClient.querySpecParam(null, spu.getCid3(), true, null);
        for (SpecParam specParam : specParams) {
            String paramName = specParam.getName();
            Object value = "";
            if(specParam.getGeneric()){
                value = generic.get(specParam.getId());
                if (specParam.getNumeric()){
                    //数值类型需要加分段
                    value = this.chooseSegment(value.toString(),specParam);
                }
            }else{
                value = special.get(specParam.getId());
            }
            specs.put(paramName,value);
        }
        good.setAll(all);
        good.setBrandId(spu.getBrandId());
        good.setCid1(spu.getCid1());
        good.setCid2(spu.getCid2());
        good.setCid3(spu.getCid3());
        good.setCreateTime(spu.getCreateTime());
        good.setId(spu.getId());
        good.setPrice(priceList);
        good.setSkus(JsonUtils.serialize(skus));
        good.setSpecs(specs);
        good.setSubTitle(spu.getSubTitle());
        return good;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> search(SearchRequest request){
        String key = request.getKey();
        int page = request.getPage();
        System.out.println("page: " + page);
        int size = request.getSize();
        System.out.println("size: " + size);
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //搜索字段
        builder.withQuery(QueryBuilders.matchQuery("all",key));
        //分页
        builder.withPageable(PageRequest.of(page,size));
        //过滤
        builder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"}, null));
        //搜素
        Page<Goods> result = goodsRepository.search(builder.build());
        //解析结果
        long totalPages = result.getTotalPages();
        long totalElements = result.getTotalElements();
        List<Goods> goodsList = result.getContent();
        return new PageResult<Goods>(totalElements,totalPages,goodsList);
    }

    public void createIndex(Long id){
        Spu spu = this.goodsClient.querySpuById(id);
        Goods goods = this.buildGoods(spu);
        this.goodsRepository.save(goods);
    }

    public void deleteIndex(Long id){
        Goods goods = new Goods();
        goods.setId(id);
        this.goodsRepository.delete(goods);
    }
}
