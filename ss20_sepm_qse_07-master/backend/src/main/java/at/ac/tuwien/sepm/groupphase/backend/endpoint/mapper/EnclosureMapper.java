package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EnclosureDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import org.mapstruct.Mapper;

import java.util.Arrays;

@Mapper
public class EnclosureMapper {

    public EnclosureDto enclosureToEnclosureDto(Enclosure enclosure) {
        if(enclosure == null) {
            return null;
        }

        return EnclosureDto.builder()
            .id(enclosure.getId())
            .name(enclosure.getName())
            .description(enclosure.getDescription())
            .publicInfo(enclosure.getPublicInfo())
            .picture(enclosure.getPicture()==null?null:new String(enclosure.getPicture()))
            .build();

    }

    public Enclosure enclosureDtoToEnclosure(EnclosureDto enclosureDto) {
        if(enclosureDto == null) {
            return null;
        }

        return Enclosure.builder()
            .id(enclosureDto.getId())
            .name(enclosureDto.getName())
            .description(enclosureDto.getDescription())
            .publicInfo(enclosureDto.getPublicInfo())
            .picture(enclosureDto.getPicture()==null?null:enclosureDto.getPicture().getBytes())
            .build();
    }
}
