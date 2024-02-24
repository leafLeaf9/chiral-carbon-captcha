package direction.traffic.simulation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CarLocation implements Serializable {
    private String roadId;
    private BigDecimal position;
    private int lane;

    @ApiModelProperty("所在车道纵向偏移，用于变道，默认为0，范围为：-1~1")
    private int laneOffset;
    @ApiModelProperty("变道需要3秒，需要变道时将这个字段赋值为300，每次移动减少该值")
    private int changeLaneNeedTimes;

    /**
     * 这个字段的作用是当前方压车但还不具备变道条件时, 需要减速保持安全距离, 但还想尝试变道时设置为true
     * 下次计算时因为压车在安全距离, 但还是尝试进行变道
     */
    @JsonIgnore
    @ApiModelProperty("是否期望变道")
    private boolean wantChaneLane;

    public CarLocation(String roadId, BigDecimal position, int lane) {
        this.roadId = roadId;
        this.position = position;
        this.lane = lane;
    }
}
