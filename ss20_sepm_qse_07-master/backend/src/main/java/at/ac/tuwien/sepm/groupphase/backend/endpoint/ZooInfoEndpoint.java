package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ZooInfoDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserLoginMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ZooInfoMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ZooInfo;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.service.ZooInfoService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/zoo")
public class ZooInfoEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ZooInfoService zooInfoService;
    private final ZooInfoMapper zooInfoMapper;
    private final UserLoginMapper userLoginMapper;
    private final UserService userService;


    public ZooInfoEndpoint(ZooInfoService zooInfoService, ZooInfoMapper zooInfoMapper, UserLoginMapper userLoginMapper, UserService userService) {
        this.zooInfoService = zooInfoService;
        this.zooInfoMapper = zooInfoMapper;
        this.userLoginMapper = userLoginMapper;
        this.userService = userService;
    }


    @PermitAll
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "")
    @ApiOperation(value = "Display zooInfo")
    public ZooInfoDto displayZooInfo(){
        LOGGER.info("Get /api/v1/zoo");
        return zooInfoMapper.zooInfoToZooInfoDto(zooInfoService.displayZooInfo());
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/edit")
    @ApiOperation(value = "Edit zooInfo", authorizations = {@Authorization(value = "apiKey")})
    public ZooInfoDto editZooInfo(@RequestBody @Valid ZooInfoDto zooInfoDto){
        LOGGER.info("PUT /api/v1/zoo/edit body: {}", zooInfoDto);
        ZooInfo zooInfo = zooInfoMapper.zooInfoDtoToZooInfo(zooInfoDto);
        ZooInfo zooInfoEdited = zooInfoService.editZooInfo(zooInfo);
        ZooInfoDto zooInfoEditedRes = zooInfoMapper.zooInfoToZooInfoDto(zooInfoEdited);

        return zooInfoEditedRes;
    }

}
