package direction.traffic.simulation.service.impl;

import com.github.yitter.idgen.YitIdHelper;
import direction.traffic.simulation.entity.CarFlow;
import direction.traffic.simulation.entity.TrafficSimulationRunDTO;
import direction.traffic.simulation.entity.TrafficSimulationSceneDTO;
import direction.traffic.simulation.service.ITrafficSimulationService;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

@Service
public class TrafficSimulationServiceImpl implements ITrafficSimulationService {
    private final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 100, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new CustomizableThreadFactory("交通流模拟线程-"), new ThreadPoolExecutor.CallerRunsPolicy());

    private final ConcurrentMap<Long, TrafficSimulationThread> map = new ConcurrentHashMap<>();

    @Override
    public String init(TrafficSimulationSceneDTO dto) {
        long id = YitIdHelper.nextId();
        dto.setId(id);
        map.put(id, new TrafficSimulationThread(dto));

        return String.valueOf(id);
    }

    @Override
    public void run(TrafficSimulationRunDTO dto) {
        TrafficSimulationThread thread = Objects.requireNonNull(map.get(dto.getId()),
                "交通流仿真-场景id不存在，请先添加场景再运行。");
        // 判断锁 避免多线程同时对一个场景进行模拟 这里没加redis 所以用内存锁 实际应用可以修改为redisUtils.setnx(id, dto.getMillis())锁校验
        boolean lock = thread.getLock().isLocked();
        if (lock) {
            throw new RuntimeException("场景已经在运行中，请等待运行结束再重新请求。");
        }
        thread.setRun(dto);
        THREAD_POOL.submit(thread);
    }

    @Override
    public List<CarFlow> runFast(TrafficSimulationRunDTO dto) {
        TrafficSimulationThread thread = Objects.requireNonNull(map.get(dto.getId()),
                "交通流仿真-场景id不存在，请先添加场景再运行。");
        // 判断锁 避免多线程同时对一个场景进行模拟 这里没加redis 所以用内存锁 实际应用可以修改为redisUtils.setnx(id, dto.getMillis())锁校验
        boolean lock = thread.getLock().isLocked();
        if (lock) {
            throw new RuntimeException("场景已经在运行中，请等待运行结束再重新请求。");
        }
        dto.setRunFast(true);
        thread.setRun(dto);
        Future<List<CarFlow>> future = THREAD_POOL.submit(thread);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CarFlow> carFlow(Long id) {
        TrafficSimulationThread thread = Objects.requireNonNull(map.get(id),
                "交通流仿真-场景id不存在，请先添加场景再查询。");
        return thread.getResult();
    }
}
