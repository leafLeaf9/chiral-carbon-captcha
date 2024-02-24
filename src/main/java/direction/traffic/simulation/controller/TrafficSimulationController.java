package direction.traffic.simulation.controller;

import com.gousade.common.ResponseResult;
import direction.traffic.simulation.entity.CarFlow;
import direction.traffic.simulation.entity.TrafficSimulationRunDTO;
import direction.traffic.simulation.entity.TrafficSimulationSceneDTO;
import direction.traffic.simulation.service.ITrafficSimulationService;
import io.swagger.annotations.ApiOperation;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/trafficSimulation")
public class TrafficSimulationController {

    @Setter(onMethod = @__({@Autowired}))
    private ITrafficSimulationService service;

    @ApiOperation(value = "场景初始化")
    @PostMapping("/init")
    public ResponseResult init(@Valid @RequestBody TrafficSimulationSceneDTO dto) {
        String id = service.init(dto);
        return ResponseResult.renderSuccess().data("data", id);
    }

    @ApiOperation(value = "执行仿真, 真实时间")
    @PostMapping("/run")
    public ResponseResult run(@Valid @RequestBody TrafficSimulationRunDTO dto) {
        service.run(dto);
        return ResponseResult.renderSuccess();
    }

    @ApiOperation(value = "执行仿真, 快速模拟返回结果")
    @PostMapping("/runFast")
    public ResponseResult runFast(@Valid @RequestBody TrafficSimulationRunDTO dto) {
        List<CarFlow> result = service.runFast(dto);
        return ResponseResult.renderSuccess().data("data", result);
    }

    @ApiOperation(value = "执行仿真")
    @PostMapping("/carFlow/{id}")
    public ResponseResult carFlow(@PathVariable("id") Long id) {
        List<CarFlow> result = service.carFlow(id);
        return ResponseResult.renderSuccess().data("data", result);
    }
}
