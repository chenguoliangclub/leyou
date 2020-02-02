package com.leyou.item.web;


import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Specification;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;
    @GetMapping("/groups/{cid}")
    ResponseEntity<List<SpecGroup>> querySpecificationByCid(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(specificationService.querySpecificationByCid(cid));
    }

    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParam(
            @RequestParam(value="gid", required = false) Long gid,
            @RequestParam(value="cid", required = false) Long cid,
            @RequestParam(value="searching", required = false) Boolean searching,
            @RequestParam(value="generic", required = false) Boolean generic
    ){
        return ResponseEntity.ok(specificationService.querySpecParams(gid, cid, searching, generic));
    }

    // 查询规格参数组，及组内参数
    @GetMapping("{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecsByCid(@PathVariable("cid") Long cid){
         return ResponseEntity.ok(specificationService.querySpecsByCid(cid));
    }
}
