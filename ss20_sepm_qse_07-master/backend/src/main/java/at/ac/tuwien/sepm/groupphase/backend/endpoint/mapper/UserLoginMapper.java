package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EmployeeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserLoginMapper {

    UserLogin userLoginDtoToUserLogin(UserLoginDto userLoginDto);

    UserLogin eployeeDtoToUserLogin(EmployeeDto employeeDto);

    UserLoginDto userLoginToUserLoginDto(UserLogin applicationUserByUsername);

    UserLogin userDtoToUserLogin (UserLoginDto userLoginDto);
}
