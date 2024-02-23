package direction.traffic.simulation.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class TrafficSimulationRunDTO implements Serializable {
    @NotNull(message = "[仿真场景id]不能为空")
    @ApiModelProperty("仿真场景id")
    private Long id;

    @NotNull(message = "[仿真毫秒数]不能为空")
    @ApiModelProperty("执行xxx毫秒的仿真")
    private Long millis;

    @ApiModelProperty("倍速")
    private int multipleSpeed = 1;

    @ApiModelProperty("是否快速运行，默认等待真实时间返回，为true时不等待真实时间，直接尽快计算结果")
    private boolean runFast = false;
}
