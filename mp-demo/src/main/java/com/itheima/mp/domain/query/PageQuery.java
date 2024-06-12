package com.itheima.mp.domain.query;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PageQuery {
    @ApiModelProperty("页码")
    private Integer pageNo = 1;

    @ApiModelProperty("每页条数")
    private Integer pageSize = 5;

    @ApiModelProperty("排序字段")
    private String sortBy;

    @ApiModelProperty("是否升序")
    private Boolean isAsc = true;

    public <T> Page<T> toMpPage(OrderItem... orderItems) {
        // 1、构建 page
        Page<T> page = Page.of(pageNo, pageSize);

        // 排序条件
        if (sortBy != null) {
            // 按条件排序
            page.addOrder(new OrderItem(sortBy, isAsc));
        } else if (orderItems != null) {
            // 默认排序
            page.addOrder(orderItems);
        }

        return page;
    }

    public <T> Page<T> toMpPage(String defaultSortBy, Boolean defaultAsc) {
        return toMpPage(new OrderItem(defaultSortBy, defaultAsc));
    }

    public <T> Page<T> toMpPageDefaultSortByUpdateTime() {
        return toMpPage(new OrderItem("update_time", false));
    }

    public <T> Page<T> toMpPageDefaultSortByCreateTime() {
        return toMpPage(new OrderItem("create_time", false));
    }
}
