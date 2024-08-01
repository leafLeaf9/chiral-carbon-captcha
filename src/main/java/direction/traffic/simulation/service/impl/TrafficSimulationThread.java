package direction.traffic.simulation.service.impl;

import direction.traffic.simulation.entity.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

@Setter
@Getter
public class TrafficSimulationThread implements Runnable {
    private final ReentrantLock lock = new ReentrantLock();
    private TrafficSimulationSceneDTO dto;
    private TrafficSimulationRunDTO run;

    private List<Road> roadSegments;

    private Map<String, Road> roadMap;

    private List<CarFlow> carFlows;

    private List<CarFlow> result;

    public TrafficSimulationThread(TrafficSimulationSceneDTO dto) {
        this.dto = dto;
        this.roadSegments = dto.getRoadSegments();
        this.carFlows = dto.getCarFlows();
        roadMap = roadSegments.stream().collect(Collectors.toMap(Road::getId, Function.identity()));
    }

    @Override
    public void run() {
        try {
            lock.lock();
            result = carFlows.stream().map(e -> {
                CarFlow clone = new CarFlow();
                CarLocation movedCarLocation = new CarLocation();
                BeanUtils.copyProperties(e.getLocation(), movedCarLocation);
                BeanUtils.copyProperties(e, clone);
                clone.setLocation(movedCarLocation);
                return clone;
            }).collect(Collectors.toList());
            Long millis = run.getMillis();
            int multipleSpeed = run.getMultipleSpeed();
            boolean runFast = run.isRunFast();
            long times = millis / 10;
            for (int i = 0; i < times; i++) {
                // 通过这个循环控制倍速，如果是2倍，就执行2次再等待10毫秒，只通过加大millis无法模拟真实倍速
                for (int j = 0; j < multipleSpeed; j++) {
                    runOnce();
                    // 一次运行完之后修正变道相关信息
                    getResult().forEach(e -> {
                        e.getLocation().setChangeLaneNeedTimes(Math.max(0, e.getLocation().getChangeLaneNeedTimes() - multipleSpeed));
                        e.getLocation().setLaneOffset(e.getLocation().getChangeLaneNeedTimes() == 0 ? 0 : e.getLocation().getLaneOffset());
                    });
                }
                if (!runFast) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void runOnce() {
        Map<String, List<CarFlow>> groups = carFlows.stream().collect(Collectors.groupingBy(e -> e.getLocation().getRoadId()));
        groups.forEach((k, v) -> {
            Road road = roadMap.get(k);
            v.sort(Comparator.comparing(o -> o.getLocation().getPosition(), Comparator.reverseOrder()));
            Map<Integer, LinkedList<CarFlow>> laneMap = new HashMap<>();
            Map<Integer, LinkedList<CarFlow>> originalMap = new HashMap<>();
            for (int i = 0; i < road.getLaneNum(); i++) {
                laneMap.put(i + 1, new LinkedList<>());
                originalMap.put(i + 1, new LinkedList<>());
            }
            v.forEach(e -> originalMap.get(e.getLocation().getLane()).addLast(e));
            v.forEach(e -> {
                BigDecimal exceptLocation = buildExceptLocation(road, e);
                // 超出路段终点就消失，查询也返回空
                if (exceptLocation.compareTo(road.getEndPosition()) > 0) {
                    return;
                }

                LinkedList<CarFlow> laneCarList = laneMap.get(e.getLocation().getLane());
                // 变道过程中直接插入 不再判断后续逻辑
                if (e.getLocation().getLaneOffset() != 0) {
                    e.getLocation().setPosition(exceptLocation);
                    laneCarList.addLast(e);
                    return;
                }
                // 同车道前方无车直接插入 保存数据
                if (laneCarList.isEmpty()) {
                    e.getLocation().setPosition(exceptLocation);
                    laneCarList.addLast(e);
                    return;
                }
                // 同车道前方有车 需要计算安全距离，如果小于安全距离，则先尝试变道，无法变道则将距离修正到最小安全距离
                CarFlow lastCar = laneCarList.getLast();
                boolean safe = exceptLocation.add(buildCarLength(e.getCarType())).add(BigDecimal.valueOf(0.02))
                        .compareTo(lastCar.getLocation().getPosition().subtract(buildCarLength(e.getCarType()))) < 0;
                if (safe) {
                    laneCarList.addLast(e);
                    return;
                }
                boolean changeLane = tryChangeLane(originalMap, e);
                if (changeLane) {
                    // 3秒除以10毫秒
                    e.getLocation().setChangeLaneNeedTimes(300);
                    e.getLocation().setPosition(exceptLocation);
                    // 变道过程中速度要小于等于前车 否则会超过安全距离
                    e.setRealSpeed(lastCar.getRealSpeed());
                    laneCarList.addLast(e);
                    return;
                }
                // 无法变道 距离修正到安全距离与前车速度保持一致 理解为实际行车时一直和前车保持在最小安全距离即可
                e.getLocation().setPosition(lastCar.getLocation().getPosition().subtract(buildCarLength(lastCar))
                        .subtract(BigDecimal.valueOf(0.02)).subtract(buildCarLength(e)));
                e.setRealSpeed(lastCar.getRealSpeed());
                laneCarList.addLast(e);
            });
            this.setResult(laneMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
        });
    }

    private boolean tryChangeLane(Map<Integer, LinkedList<CarFlow>> originalMap, CarFlow e) {
        int lane = e.getLocation().getLane();
        LinkedList<CarFlow> list1 = originalMap.getOrDefault(lane - 1, new LinkedList<>());
        LinkedList<CarFlow> list2 = originalMap.getOrDefault(lane + 1, new LinkedList<>());
        boolean canChangeLeftLane = checkCanChangeLane(list1, e);
        if (canChangeLeftLane) {
            e.getLocation().setLaneOffset(-1);
            return true;
        }
        boolean canChangeRightLane = checkCanChangeLane(list2, e);
        if (canChangeRightLane) {
            e.getLocation().setLaneOffset(1);
            return true;
        }
        return false;
    }

    private boolean checkCanChangeLane(LinkedList<CarFlow> list, CarFlow e) {
        BigDecimal position = e.getLocation().getPosition();
        // 车辆不多直接allMatch 数量多可以改为取前车最后一个和后车第一个比较
        boolean frontMatch = list.stream().filter(o -> o.getLocation().getPosition().compareTo(position) > 0)
                .allMatch(o -> position.add(buildCarLength(e.getCarType())).add(BigDecimal.valueOf(0.025))
                        .compareTo(o.getLocation().getPosition().subtract(buildCarLength(o.getCarType()))) < 0);
        boolean behindMatch = list.stream().filter(o -> o.getLocation().getPosition().compareTo(position) < 0)
                .allMatch(o -> position.subtract(buildCarLength(e.getCarType())).subtract(BigDecimal.valueOf(0.025))
                        .compareTo(o.getLocation().getPosition().add(buildCarLength(o.getCarType()))) > 0);
        return frontMatch && behindMatch;
    }

    private BigDecimal buildCarLength(CarFlow e) {
        return buildCarLength(e.getCarType());
    }

    private BigDecimal buildCarLength(String carType) {
        if ("car".equals(carType)) {
            return BigDecimal.valueOf(0.0045 / 2);
        } else {
            return BigDecimal.valueOf(0.0072 / 2);
        }
    }

    private BigDecimal buildExceptLocation(Road road, CarFlow car) {
        if (car.getRealSpeed() == null) {
            car.setRealSpeed(car.getExpectSpeed());
        }
        BigDecimal result = car.getLocation().getPosition().add(buildDistance(car));
        List<RoadLimitSpeed> limitSpeeds = road.getLimitSpeeds();
        BigDecimal finalResult = result;
        Optional<RoadLimitSpeed> limitSpeed = limitSpeeds.stream()
                .filter(e -> e.getBeginPosition().compareTo(finalResult) <= 0 && finalResult.compareTo(e.getEndPosition()) <= 0)
                .min(Comparator.comparing(RoadLimitSpeed::getMaxSpeed));
        if (limitSpeed.isPresent()) {
            // 大于限速 需要根据限速值行驶 题目场景中所有车道限速值相同，所以这里不需要考虑变道，直接降速即可
            if (limitSpeed.get().getMaxSpeed().compareTo(car.getRealSpeed()) < 0) {
                car.setRealSpeed(limitSpeed.get().getMaxSpeed());
                result = car.getLocation().getPosition().add(buildDistance(car));
                return result;
            }
        } else {
            return result;
        }
        return result;
    }

    private BigDecimal buildDistance(CarFlow e) {
        return e.getRealSpeed()/*.multiply(BigDecimal.valueOf(10))*/.divide(BigDecimal.valueOf(360000), 3, RoundingMode.HALF_UP);
    }
}
