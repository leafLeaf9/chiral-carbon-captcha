package direction.traffic.simulation.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class Road implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;


    @Schema(description = "路段名称")
    private String name;

    private BigDecimal beginPosition;

    private BigDecimal endPosition;

    private int laneNum = 1;

    private List<RoadLimitSpeed> limitSpeeds;
}
