package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ZooInfo;
import at.ac.tuwien.sepm.groupphase.backend.repository.ZooInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalTime;

@Profile("generateData")
@Component
public class ZooInfoDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ZooInfoRepository zooInfoRepository;

    public ZooInfoDataGenerator(ZooInfoRepository zooInfoRepository) {
        this.zooInfoRepository = zooInfoRepository;
    }

    @PostConstruct
    private void generateZooInfo() {
        if (zooInfoRepository.findAll().size() > 0) {
            LOGGER.debug("ZooInfo already generated");
        } else {
            LOGGER.debug("generating one ZooInfo");

            ZooInfo zooInfo = ZooInfo.builder()
                .name("Our small Zoo")
                .publicInfo("We exist since 1988 and we are happy to offer you having fun and learning about animal world at the same time!")
                .address("Schelleingasse 8")
                .workTimeStart(LocalTime.of(8, 30))
                .workTimeEnd(LocalTime.of(19,45)).build();

            LOGGER.debug("Saving zooInfo");
            zooInfoRepository.save(zooInfo);
        }
    }
}
