package com.eport.daemon.rule.storage.database.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 规则存储表
 *
 * @author alaia
 * @date 2025-10-28 16:43:06
 */
@Data
@TableName("rule_database_load")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "规则存储表")
public class RuleDatabaseLoadEntity extends Model<RuleDatabaseLoadEntity> {


	/**
	* 主键
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    private Long id;

	/**
	* 规则主键
	*/
    @Schema(description="规则主键")
    private String ruleSetKey;

	/**
	* 规则内容
	*/
    @Schema(description="规则内容")
    private String ruleContent;
 
	/**
	* ruleDatabaseKey
	*/
    @Schema(description="ruleDatabaseKey")
    private String ruleDatabaseKey;

	/**
	* 是否被base64加密(true为1, false为0)
	*/
    @Schema(description="是否被base64加密(true为1, false为0)")
    private Boolean base64;

	/**
	* 备注信息
	*/
    @Schema(description="备注信息")
    private String remarks;

	/**
	* 是否启用(true为1, false为0)
	*/
    @Schema(description="是否启用(true为1, false为0)")
    private Boolean hasEnable;

	/**
	* 创建人
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建人")
    private String createBy;

	/**
	* 创建时间
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建时间")
    private LocalDateTime createTime;

	/**
	* 修改人
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="修改人")
    private String updateBy;

	/**
	* 更新时间
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="更新时间")
    private LocalDateTime updateTime;

	/**
	* 删除标志
	*/
    @TableLogic
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="删除标志")
    private String delFlag;
}