package direction.traffic.simulation.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CarFlow implements Serializable {
    @ApiModelProperty("车辆id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private BigDecimal expectSpeed;
    private BigDecimal realSpeed;

    @ApiModelProperty("车辆类型，car客车 truck货车 客车：长4.5m，宽1.7米 货车：长7.2m，宽2.3米")
    private String carType;

    private CarLocation location;
}
