package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewPasswordReqDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewPasswordReq;
import org.springframework.stereotype.Component;

@Component
public class NewPasswordReqMapper {
    public NewPasswordReqMapper(){}

    public NewPasswordReqDto newPasswordReqtoNewPasswordReqDto(NewPasswordReq newPasswordReq){
        if(newPasswordReq == null){
            return null;
        }
        return NewPasswordReqDto.builder()
            .username(newPasswordReq.getUsername())
            .currentPassword(newPasswordReq.getCurrentPassword())
            .newPassword(newPasswordReq.getNewPassword())
            .build();
    }
    public NewPasswordReq newPasswordReqDtoToNewPasswordReq(NewPasswordReqDto newPasswordReqDto){
        if(newPasswordReqDto == null){
            return null;
        }
        return NewPasswordReq.builder()
            .username(newPasswordReqDto.getUsername())
            .currentPassword(newPasswordReqDto.getCurrentPassword())
            .newPassword(newPasswordReqDto.getNewPassword())
            .build();
    }
}
