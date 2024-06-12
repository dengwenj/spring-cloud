package com.itheima.mp.domain.dto;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Data
@ApiModel(description = "分页返回的数据")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {
    @ApiModelProperty("总条数")
    private Long total;

    @ApiModelProperty("总页数")
    private Long pages;

    @ApiModelProperty("集合")
    private List<T> list;

    public static <PO, VO> PageDTO<VO> of(Page<PO> page, Class<VO> voClass) {
        // 总条数
        long total = page.getTotal();
        // 总页数
        long pages = page.getPages();
        // 当前页数据
        List<PO> records = page.getRecords();

        PageDTO<VO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(total);
        pageDTO.setPages(pages);
        if (records == null) {
            pageDTO.setList(Collections.emptyList());
            return pageDTO;
        }
        pageDTO.setList(BeanUtil.copyToList(records, voClass));

        return pageDTO;
    }

    // Function<PO, VO> PO 是参数，VO 是返回值，也可以自己写个 function 接口，然后接口.方法调用
    public static <PO, VO> PageDTO<VO> of(Page<PO> page, Function<PO, VO> callback) {
        // 总条数
        long total = page.getTotal();
        // 总页数
        long pages = page.getPages();
        // 当前页数据
        List<PO> records = page.getRecords();

        PageDTO<VO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(total);
        pageDTO.setPages(pages);
        if (records == null) {
            pageDTO.setList(Collections.emptyList());
            return pageDTO;
        }

        List<VO> list1 = records.stream().map(callback).toList();
        pageDTO.setList(list1);

        return pageDTO;
    }
}
