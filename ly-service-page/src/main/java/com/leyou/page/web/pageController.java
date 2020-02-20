package com.leyou.page.web;

import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import com.leyou.page.pojo.user;
import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
public class pageController {

    @Autowired
    private PageService pageService;

    @RequestMapping("item/{spuId}.html")
    public String getPage(@PathVariable Long spuId, Model model){
        model.addAllAttributes(pageService.loadModel(spuId));
        return spuId + "item";
    }
}
