package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ZooInfoDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ZooInfo;
import org.springframework.stereotype.Component;

@Component
public class ZooInfoMapper {

    public ZooInfo zooInfoDtoToZooInfo(ZooInfoDto zooInfoDto){

        if(zooInfoDto == null){
            return null;
        }

        return ZooInfo.builder()
            .id(zooInfoDto.getId())
            .address(zooInfoDto.getAddress())
            .name(zooInfoDto.getName())
            .picture(zooInfoDto.getPicture()==null?null:zooInfoDto.getPicture().getBytes())
            .publicInfo(zooInfoDto.getPublicInfo())
            .workTimeStart(zooInfoDto.getWorkTimeStart())
            .workTimeEnd(zooInfoDto.getWorkTimeEnd())
            .build();
    }


    public ZooInfoDto zooInfoToZooInfoDto(ZooInfo zooInfo){

        if(zooInfo == null){
            return null;
        }

        return ZooInfoDto.builder()
            .id(zooInfo.getId())
            .address(zooInfo.getAddress())
            .name(zooInfo.getName())
            .picture(zooInfo.getPicture() == null ? null : new String(zooInfo.getPicture()))
            .publicInfo(zooInfo.getPublicInfo())
            .workTimeStart(zooInfo.getWorkTimeStart())
            .workTimeEnd(zooInfo.getWorkTimeEnd())
            .build();
    }
}
