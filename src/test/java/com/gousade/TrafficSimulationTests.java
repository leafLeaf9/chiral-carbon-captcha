package com.gousade;

import direction.traffic.simulation.entity.*;
import direction.traffic.simulation.service.ITrafficSimulationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static direction.traffic.simulation.entity.CarFlow.CAR;
import static direction.traffic.simulation.entity.CarFlow.TRUCK;

/**
 * 测试类包名需要在主程序包名下，否则自动注入有点问题
 */
@Slf4j
@SpringBootTest
public class TrafficSimulationTests {
    @Autowired
    private ITrafficSimulationService service;

    private List<Road> roadSegments;

    @BeforeEach
    public void setUp() {
        Road roadA = Road.builder().id("001").name("路段A").beginPosition(BigDecimal.valueOf(1.100))
                .endPosition(BigDecimal.valueOf(50.000))
                .laneNum(1)
                .limitSpeeds(Arrays.asList(new RoadLimitSpeed(BigDecimal.valueOf(1.100), BigDecimal.valueOf(50.000), BigDecimal.valueOf(120)),
                        new RoadLimitSpeed(BigDecimal.valueOf(23.500), BigDecimal.valueOf(25.500), BigDecimal.valueOf(80))))
                .build();
        Road roadB = Road.builder().id("002").name("路段B").beginPosition(BigDecimal.valueOf(50.000))
                .endPosition(BigDecimal.valueOf(100.000))
                .laneNum(2)
                .limitSpeeds(Arrays.asList(new RoadLimitSpeed(BigDecimal.valueOf(50), BigDecimal.valueOf(100), BigDecimal.valueOf(120)),
                        new RoadLimitSpeed(BigDecimal.valueOf(80.500), BigDecimal.valueOf(85.500), BigDecimal.valueOf(80))))
                .build();
        roadSegments = Arrays.asList(roadA, roadB);
    }

    @Test
    public void test1() {
        CarFlow car1 = new CarFlow(1L, BigDecimal.valueOf(110), null, CAR, new CarLocation("001", BigDecimal.valueOf(2.452), 1));
        Map<Long, CarFlow> map = run(Collections.singletonList(car1), 2L * 60 * 1000);
        CarFlow car = map.get(1L);
        // 由于每次计算距离时并不是整数 会四舍五入，所以这里无法用Assert去断言一个确定结果值
        log.info("小客车1分钟后位置{}, 速度{}, 车道{}", car.getLocation().getPosition(), car.getRealSpeed(), car.getLocation().getLane());
        // 结果应该近似为6.119
    }

    public Map<Long, CarFlow> run(List<CarFlow> carFlows, Long millis) {
        TrafficSimulationSceneDTO dto = new TrafficSimulationSceneDTO(null, roadSegments, carFlows);
        String id = service.init(dto);
        TrafficSimulationRunDTO runDTO = new TrafficSimulationRunDTO();
        runDTO.setId(Long.valueOf(id));
        runDTO.setMillis(millis);
        return service.runFast(runDTO).stream().collect(Collectors.toMap(CarFlow::getId, Function.identity()));
    }


    @Test
    public void test2() {
        CarFlow car1 = new CarFlow(1L, BigDecimal.valueOf(110), null, CAR, new CarLocation("001", BigDecimal.valueOf(22.500), 1));
        Map<Long, CarFlow> map = run(Collections.singletonList(car1), 2L * 60 * 1000);
        CarFlow car = map.get(1L);
        log.info("小客车1分钟后位置{}, 速度{}, 车道{}", car.getLocation().getPosition(), car.getRealSpeed(), car.getLocation().getLane());
    }

    @Test
    public void test3() {
        CarFlow car1 = new CarFlow(1L, BigDecimal.valueOf(75), null, TRUCK, new CarLocation("001", BigDecimal.valueOf(2.700), 1));
        CarFlow car2 = new CarFlow(2L, BigDecimal.valueOf(110), null, CAR, new CarLocation("001", BigDecimal.valueOf(2.000), 1));
        Map<Long, CarFlow> map = run(Arrays.asList(car1, car2), 1L * 60 * 1000);
        CarFlow car = map.get(2L);
        log.info("小客车1分钟后位置{}, 速度{}, 车道{}", car.getLocation().getPosition(), car.getRealSpeed(), car.getLocation().getLane());
        Map<Long, CarFlow> map2 = run(Arrays.asList(car1, car2), 2L * 60 * 1000);
        CarFlow c2 = map2.get(2L);
        log.info("小客车2分钟后位置{}, 速度{}, 车道{}", c2.getLocation().getPosition(), c2.getRealSpeed(), c2.getLocation().getLane());
    }

    @Test
    public void test4() {
        CarFlow car1 = new CarFlow(1L, BigDecimal.valueOf(75), null, TRUCK, new CarLocation("002", BigDecimal.valueOf(52.700), 1));
        CarFlow car2 = new CarFlow(2L, BigDecimal.valueOf(110), null, CAR, new CarLocation("002", BigDecimal.valueOf(52.000), 1));
        Map<Long, CarFlow> map = run(Arrays.asList(car1, car2), 1L * 60 * 1000);
        CarFlow car = map.get(2L);
        log.info("小客车1分钟后位置{}, 速度{}, 车道{}", car.getLocation().getPosition(), car.getRealSpeed(), car.getLocation().getLane());
        Map<Long, CarFlow> map2 = run(Arrays.asList(car1, car2), 2L * 60 * 1000);
        CarFlow c2 = map2.get(2L);
        log.info("小客车2分钟后位置{}, 速度{}, 车道{}", c2.getLocation().getPosition(), c2.getRealSpeed(), c2.getLocation().getLane());
    }

    @Test
    public void test5() {
        CarFlow car1 = new CarFlow(1L, BigDecimal.valueOf(110), null, CAR, new CarLocation("002", BigDecimal.valueOf(51.100), 2));
        CarFlow car2 = new CarFlow(2L, BigDecimal.valueOf(110), null, CAR, new CarLocation("002", BigDecimal.valueOf(51.071), 1));
        CarFlow car3 = new CarFlow(3L, BigDecimal.valueOf(110), null, CAR, new CarLocation("002", BigDecimal.valueOf(51.115), 1));
        CarFlow car4 = new CarFlow(4L, BigDecimal.valueOf(110), null, CAR, new CarLocation("002", BigDecimal.valueOf(51.000), 1));
        CarFlow car5 = new CarFlow(5L, BigDecimal.valueOf(75), null, TRUCK, new CarLocation("002", BigDecimal.valueOf(51.700), 2));
        Map<Long, CarFlow> map = run(Arrays.asList(car1, car2, car3, car4, car5), 1L * 60 * 1000);
        CarFlow car = map.get(1L);
        log.info("小客车1分钟后位置{}, 速度{}, 车道{}", car.getLocation().getPosition(), car.getRealSpeed(), car.getLocation().getLane());
        Map<Long, CarFlow> map2 = run(Arrays.asList(car1, car2, car3, car4, car5), 2L * 60 * 1000);
        CarFlow c2 = map2.get(1L);
        log.info("小客车2分钟后位置{}, 速度{}, 车道{}", c2.getLocation().getPosition(), c2.getRealSpeed(), c2.getLocation().getLane());
    }
}
