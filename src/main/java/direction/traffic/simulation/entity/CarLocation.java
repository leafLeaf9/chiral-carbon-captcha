package direction.traffic.simulation.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CarLocation implements Serializable {
    private String roadId;
    private BigDecimal position;
    private int lane;

    @ApiModelProperty("所在车道纵向偏移，用于变道，默认为0，范围为：-1~1")
    private int laneOffset;
    @ApiModelProperty("变道需要3秒，需要变道时将这个字段赋值为300，每次移动减少该值")
    private int changeLaneNeedTimes;
}
