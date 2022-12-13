package com.gousade.entity.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author woxigousade <woxigsd@gmail.com>
 * @date 2022/12/03
 */
@ApiModel(value = "手性碳验证码查询对象")
@Data
public class ChiralCarbonCaptchaQuery implements Serializable {
	/**
	 * 是否包含提示(true时生成的图片中用*号标识答案)
	 */
	@ApiModelProperty(value = "是否包含提示(true时生成的图片中用*号标识答案)")
	private boolean hint;
	@ApiModelProperty(value = "是否包含答案(手性碳所在区域)")
	private boolean answer;

}
