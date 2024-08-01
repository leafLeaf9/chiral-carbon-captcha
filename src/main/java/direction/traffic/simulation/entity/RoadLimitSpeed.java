package direction.traffic.simulation.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RoadLimitSpeed implements Serializable {
    private BigDecimal beginPosition;

    private BigDecimal endPosition;
    private BigDecimal maxSpeed;
}
