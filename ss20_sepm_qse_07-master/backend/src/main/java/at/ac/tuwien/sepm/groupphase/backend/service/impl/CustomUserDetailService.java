package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewPasswordReq;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.exception.CurrentPasswordNotCorrect;
import at.ac.tuwien.sepm.groupphase.backend.exception.IncorrectTypeException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserLoginRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.OneToOne;
import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserLoginRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailService(UserLoginRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            UserLogin userLogin = findApplicationUserByUsername(username);

            List<GrantedAuthority> grantedAuthorities;
            if (userLogin.isAdmin())
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            else
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");

            return new User(userLogin.getUsername(), userLogin.getPassword(), grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public UserLogin findApplicationUserByUsername(String username) {
        LOGGER.debug("Find application user by username");
        UserLogin userLogin = userRepository.findUserByUsername(username);
        if (userLogin != null) return userLogin;
        throw new NotFoundException(String.format("Could not find the user with the username %s", username));
    }

    @Override
    public UserLogin createNewUser(UserLogin userLogin){
        LOGGER.debug("Crating new user.");
        UserLogin exists = userRepository.findUserByUsername(userLogin.getUsername());
        if(exists==null){
            String encoded = passwordEncoder.encode(userLogin.getPassword());
            userLogin.setPassword(encoded);
            return userRepository.save(userLogin);
        }
        throw new AlreadyExistsException("User with such username already exists.");
    }


    @Override
    public void deleteUser(String userName) {
        userRepository.deleteById(userName);
    }


    @Override
    public void changePassword(NewPasswordReq newPasswordReq) {
        LOGGER.debug("Changing user password.");
        if(passwordEncoder.matches(newPasswordReq.getCurrentPassword(), this.loadUserByUsername(newPasswordReq.getUsername()).getPassword())){
            UserLogin user = findApplicationUserByUsername(newPasswordReq.getUsername());
            String newPassword= passwordEncoder.encode(newPasswordReq.getNewPassword());
            user.setPassword(newPassword);
            userRepository.save(user);
            LOGGER.info("User password changed.");
        }else{
            throw new IncorrectTypeException("Wrong Password");
        }
    }

    @Override
    public void changePasswordByAdmin(NewPasswordReq newPasswordReq) {
        LOGGER.debug("Changing user password.");
        UserLogin user = findApplicationUserByUsername(newPasswordReq.getUsername());
        if(user==null){
            throw new NotFoundException("Can not find user: "+ newPasswordReq.getUsername());
        }
        String newPassword= passwordEncoder.encode(newPasswordReq.getNewPassword());
        user.setPassword(newPassword);
        userRepository.save(user);
        LOGGER.info("User password changed.");
    }
}
