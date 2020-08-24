package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ZooInfo;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ZooInfoRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ZooInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class CustomZooInfoService implements ZooInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ZooInfoRepository zooInfoRepository;

    @Autowired
    public CustomZooInfoService(ZooInfoRepository zooInfoRepository) {
        this.zooInfoRepository = zooInfoRepository;
    }


    @Override
    public ZooInfo findZooInfoById(Long id) {
        LOGGER.debug("Find an ZooInfo by Id.");
        Optional<ZooInfo> zooInfo = zooInfoRepository.findById(id);
        if(zooInfo.isEmpty())
            throw new NotFoundException("Could not find zooInfo in dataBase");
        return zooInfo.get();
    }

    @Override
    public ZooInfo displayZooInfo(){
        LOGGER.debug("Display ZooInfo");

        List<ZooInfo> zooInfo = zooInfoRepository.findAll();
        return zooInfo.get(0);
    }

    @Override
    public ZooInfo editZooInfo(ZooInfo zooInfo) {
         ZooInfo zooInfo1 = findZooInfoById(zooInfo.getId());

        if(zooInfo.getName() != null){
            zooInfo1.setName(zooInfo.getName());
        }
        if(zooInfo.getAddress() != null){
            zooInfo1.setAddress(zooInfo.getAddress());
        }
        if(zooInfo.getPicture() != null){
            zooInfo1.setPicture(zooInfo.getPicture());
        }
        if(zooInfo.getPublicInfo() != null){
            zooInfo1.setPublicInfo(zooInfo.getPublicInfo());
        }
        if(zooInfo.getWorkTimeStart() != null){
            zooInfo1.setWorkTimeStart(zooInfo.getWorkTimeStart());
        }
        if(zooInfo.getWorkTimeEnd() != null){
            zooInfo1.setWorkTimeEnd(zooInfo.getWorkTimeEnd());
        }

        return  zooInfoRepository.save(zooInfo1);

    }

}
