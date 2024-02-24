package direction.traffic.simulation.service;

import direction.traffic.simulation.entity.CarFlow;
import direction.traffic.simulation.entity.TrafficSimulationRunDTO;
import direction.traffic.simulation.entity.TrafficSimulationSceneDTO;

import java.util.List;

public interface ITrafficSimulationService {
    String init(TrafficSimulationSceneDTO dto);

    /**
     * 真实时间运行，不返回最后结果，实时通过查询接口查询最新状态
     *
     * @param dto
     */
    void run(TrafficSimulationRunDTO dto);

    List<CarFlow> runFast(TrafficSimulationRunDTO dto);

    List<CarFlow> carFlow(Long id);
}
