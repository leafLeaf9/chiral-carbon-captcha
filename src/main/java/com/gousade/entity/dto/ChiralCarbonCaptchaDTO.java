package com.gousade.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author woxigousade <woxigsd@gmail.com>
 * @date 2022/12/03
 */
@ApiModel(value = "手性碳验证码对象")
@Data
@Builder
public class ChiralCarbonCaptchaDTO implements Serializable {
	@ApiModelProperty(value = "验证码答案区域")
	List<String> regions;
	@ApiModelProperty(value = "分子id, 0代表未找到cid")
	private Long cid;
	@ApiModelProperty(value = "验证码图片base64")
	private String base64;
}
