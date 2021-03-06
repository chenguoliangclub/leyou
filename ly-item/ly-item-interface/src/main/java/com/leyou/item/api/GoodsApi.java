package com.leyou.item.api;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {
    @GetMapping("/spu/page")
    PageResult<Spu> querySpuByPage(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "row",defaultValue = "5") Integer row,
            @RequestParam(value = "saleable",defaultValue = "1") boolean saleable,
            @RequestParam(value = "key",required = false) String key
    );

    @GetMapping("/spu/detail/{id}")
    public SpuDetail querySpuDetailById(@PathVariable("id") Long id);

    @GetMapping("/sku/list")
    public List<Sku> querySkuBySpuId(@RequestParam("id") Long id);

    @GetMapping("/spu/{id}")
    public Spu querySpuById(@PathVariable("id") Long id);
}
