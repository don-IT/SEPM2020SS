package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EmployeeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserLoginMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final UserLoginMapper userLoginMapper;

    @Autowired
    public AdminEndpoint(UserService userService, UserLoginMapper userLoginMapper) {
        this.userService = userService;
        this.userLoginMapper = userLoginMapper;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "")
    @ApiOperation(value = "Create new admin", authorizations = {@Authorization(value = "apiKey")})
    public void createAdmin(@RequestBody @Valid UserLoginDto adminDto){
        LOGGER.info("POST /api/v1/admin body: {}",adminDto);
        UserLogin admin = userLoginMapper.userDtoToUserLogin(adminDto);
        admin.setAdmin(true);
        userService.createNewUser(admin);
    }
}
