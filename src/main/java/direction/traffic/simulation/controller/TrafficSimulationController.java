package direction.traffic.simulation.controller;

import com.gousade.common.ResponseResult;
import direction.traffic.simulation.entity.TrafficSimulationRunDTO;
import direction.traffic.simulation.entity.TrafficSimulationSceneDTO;
import direction.traffic.simulation.service.ITrafficSimulationService;
import io.swagger.annotations.ApiOperation;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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

    @ApiOperation(value = "执行仿真")
    @PostMapping("/run")
    public ResponseResult run(@Valid @RequestBody TrafficSimulationRunDTO dto) {
        service.run(dto);
        return ResponseResult.renderSuccess();
    }
}
