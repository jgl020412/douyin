package com.atjgl.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 有关vlog的事务对象类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VlogBO {

    private String id;
    @NotBlank(message = "用户号不能为空")
    private String vlogerId;
    @NotBlank(message = "路径不能为空")
    private String url;
    private String cover;
    @NotBlank(message = "标题不能为空")
    private String title;
    private Integer width;
    private Integer height;
    private Integer likeCounts;
    private Integer commentsCounts;
    private Integer isPrivate;
    private Date createdTime;
    private Date updatedTime;
}