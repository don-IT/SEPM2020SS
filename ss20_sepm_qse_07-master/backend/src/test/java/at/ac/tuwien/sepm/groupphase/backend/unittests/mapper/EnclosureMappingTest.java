package at.ac.tuwien.sepm.groupphase.backend.unittests.mapper;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EnclosureDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EnclosureMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class EnclosureMappingTest implements TestData {

    private final Enclosure enclosure = Enclosure.builder()
        .name(NAME_LION_ENCLOSURE)
        .description(DESCRIPTION_LION_ENCLOSURE)
        .publicInfo(PUBLIC_INFO_LION_ENCLOSURE)
        .picture(PICTURE_LION_ENCLOSURE)
        .build();

    private final EnclosureDto enclosureDto = EnclosureDto.builder()
        .name(NAME_LION_ENCLOSURE)
        .description(DESCRIPTION_LION_ENCLOSURE)
        .publicInfo(PUBLIC_INFO_LION_ENCLOSURE)
        .picture(PICTURE_LION_ENCLOSURE_STRING)
        .build();

    @Autowired
    EnclosureMapper enclosureMapper;

    @Test
    public void givenNothing_whenMapEnclosureToEnclosureDto_thenDtoHasAllProperties() {
        EnclosureDto tempEnclosureDto = enclosureMapper.enclosureToEnclosureDto(enclosure);

        assertAll(
            () -> assertEquals(enclosure.getName(), tempEnclosureDto.getName()),
            () -> assertEquals(enclosure.getDescription(), tempEnclosureDto.getDescription()),
            () -> assertEquals(enclosure.getPublicInfo(), tempEnclosureDto.getPublicInfo()),
            () -> assertEquals(new String(enclosure.getPicture()), tempEnclosureDto.getPicture())
        );
    }

    @Test
    public void givenNothing_whenMapEnclosureDtoToEnclosure_thenEnclosureHasAllProperties() {
        Enclosure tempEnclosure = enclosureMapper.enclosureDtoToEnclosure(enclosureDto);

        assertAll(
            () -> assertEquals(tempEnclosure.getName(), enclosureDto.getName()),
            () -> assertEquals(tempEnclosure.getDescription(), enclosureDto.getDescription()),
            () -> assertEquals(tempEnclosure.getPublicInfo(), enclosureDto.getPublicInfo()),
            () -> assertEquals(new String(tempEnclosure.getPicture()), enclosureDto.getPicture())
        );
    }
}
