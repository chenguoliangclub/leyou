package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    public PageResult<Spu> querySpuByPage(Integer page, Integer row, boolean saleable, String key) {

        //关键词过滤
        Example example = new Example(Spu.class);
        if(!StringUtils.isBlank(key)){
            example.createCriteria().andLike("tile","%"+key+"%");
        }
        //上下架过滤

        example.createCriteria().andEqualTo("saleable",saleable);

        //默认排序
        example.setOrderByClause("last_update_time DESC");
        //分页
        PageHelper.startPage(1,5);
        //查询
        List<Spu> list = this.spuMapper.selectByExample(example);
        PageInfo<Spu> info = new PageInfo<Spu>(list,5);
        //判断
        if(CollectionUtils.isEmpty(list)){
            throw  new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //解析分类和品牌名称
        loadCategoryAndBrand(list);
        //返回
        return new PageResult<>(info.getTotal(),list);
    }

    private void loadCategoryAndBrand(List<Spu> list){
        for (Spu spu:list){
            //解析分类
            List<String> names = categoryService.queryByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names,"/"));
            //解析品牌
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
        }
    }

    public SpuDetail querySpuDetailById(Long id) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);
        if(spuDetail==null){
            new LyException(ExceptionEnum.SPUDETAIL_NOT_FUND);
        }
        return spuDetail;
    }

    public List<Sku> querySkuBySpuId(Long spuId) {
        // 查询sku
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(record);
        if(CollectionUtils.isEmpty(skus)){
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }
        for (Sku sku : skus) {
            // 同时查询出库存
            sku.setStock(this.stockMapper.selectByPrimaryKey(sku.getId()).getStock());
        }
        return skus;
    }
}
