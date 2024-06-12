package com.itheima.mp.domain.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PageQuery {
    @ApiModelProperty("页码")
    private Integer pageNo;

    @ApiModelProperty("每页条数")
    private Integer pageSize;

    @ApiModelProperty("排序字段")
    private String sortBy;

    @ApiModelProperty("是否升序")
    private Boolean isAsc;
}
