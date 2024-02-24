package direction.traffic.simulation.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TrafficSimulationSceneDTO implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("仿真场景id，后台生成")
    private Long id;

    @NotNull(message = "[路段列表]不能为空")
    @NotEmpty(message = "[路段列表]不能为空")
    private List<Road> roadSegments;

    @NotNull(message = "[车辆列表]不能为空")
    @NotEmpty(message = "[车辆列表]不能为空")
    private List<CarFlow> carFlows;


}
