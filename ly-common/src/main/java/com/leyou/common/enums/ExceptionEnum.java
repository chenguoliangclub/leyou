package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
    BRAND_NOT_FOUND(404 , "品牌不存在"),
    CATEGORY_NOT_FOUND(404,"商品分类未查到"),
    UPLOAD_FILE_ERROR(500,"文件上传失败"),
    INVALID_FILE_TYPE(400,"无效文件类型"),
    GOODS_NOT_FOUND(404,"商品未查询到"),
    SPEC_GROUP_NOT_FOUND(404,"商品规格组未查到"),
    SPEC_PARAMS_NOT_FOUND(404,"商品规格参数未查到"),
    SPUDETAIL_NOT_FUND(404,"商品详细信息未查到"),
    SKU_NOT_FOUND(404,"SKU未查到")
    ;
    private int code;
    private String msg;
}
