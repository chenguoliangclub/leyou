package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPage(Integer page,Integer rows,String sortBy,Boolean desc,String key){

        //过滤
        Example example = new Example(Brand.class);
        if(!StringUtils.isBlank(key)){
            example.createCriteria().orLike("name","%"+key+"%").orEqualTo("letter",key.toUpperCase());
        }
        //排序
        if(!StringUtils.isBlank(sortBy)){
            example.setOrderByClause(sortBy + (desc?" DESC":" ASC"));
        }
        //分页
        PageHelper.startPage(page,rows);
        List<Brand> list = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //解析分页结果
        PageInfo<Brand> info = new PageInfo<Brand>(list);
        return new PageResult<Brand>(info.getTotal(),list);
    }

    public List<Brand> queryBrandByCid(Long cid){
        List<Brand> list = brandMapper.queryByCategoryId(cid);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;
    }

    public Brand queryById(Long brandId) {
        Brand brand = brandMapper.selectByPrimaryKey(brandId);
        if(brand==null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    @Transactional
    public void insertBrand(Brand brand, List<Long> cids) {
        //向品牌表插入数据
        int insert = brandMapper.insert(brand);
        if (insert != 1){
            throw new LyException(ExceptionEnum.CREATE_BRAND_ERROR);
        }
        //向品牌分类表插入数据
        cids.forEach(cid->{
            int i = brandMapper.insertBrandCategory(cid, brand.getId());
            if (i != 1){
                throw new LyException(ExceptionEnum.CREATE_BRAND_ERROR);
            }
        });
    }
}
