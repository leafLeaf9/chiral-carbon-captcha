package com.gousade.config;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class IdConfig {

    @PostConstruct
    public void init() {
        IdGeneratorOptions options = new IdGeneratorOptions((short) 1);
        YitIdHelper.setIdGenerator(options);
    }
}
