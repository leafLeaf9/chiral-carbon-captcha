package com.gousade.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author woxigousade <woxigsd@gmail.com>
 * @date 2020/9/1/0001 20:35
 * response common result class
 */
@ApiModel(description = "公共响应类")
@Data
public class ResponseResult implements Serializable {

    @ApiModelProperty(value = "响应状态")
    private Boolean status;

    @ApiModelProperty(value = "响应码")
    private Integer code;

    @ApiModelProperty(value = "响应消息")
    private String message;

    @ApiModelProperty(value = "响应数据")
    private Map<String, Object> data = new HashMap<>();

    // 把构造方法私有
    private ResponseResult() {
    }

    public static ResponseResult renderSuccess() {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setStatus(true);
        responseResult.setCode(StatusCode.SUCCESS);
        responseResult.setMessage("操作成功");
        return responseResult;
    }

    public static ResponseResult renderError() {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setStatus(false);
        responseResult.setCode(StatusCode.ERROR);
        responseResult.setMessage("操作失败");
        return responseResult;
    }

    public static ResponseResult renderBadRequest() {
        return renderError().code(HttpStatus.BAD_REQUEST.value());
    }

    public static ResponseResult renderBoolean(Boolean b) {
        if (b) {
            return renderSuccess();
        } else {
            return renderError();
        }
    }

    public ResponseResult status(Boolean success) {
        this.setStatus(success);
        return this;
    }

    public ResponseResult message(String message) {
        this.setMessage(message);
        return this;
    }

    public ResponseResult code(Integer code) {
        this.setCode(code);
        return this;
    }

    public ResponseResult data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public ResponseResult data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }
}
