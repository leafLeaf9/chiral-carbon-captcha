package direction.traffic.simulation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoadLimitSpeed implements Serializable {
    private BigDecimal beginPosition;

    private BigDecimal endPosition;
    private BigDecimal maxSpeed;
}
