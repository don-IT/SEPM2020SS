package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewPasswordReq;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the username
     * <p>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param username the username
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * Find a application user based on the email address
     *
     * @param username the username
     * @return a application user
     */
    UserLogin findApplicationUserByUsername(String username);

    /**
     * Create a new user in the database
     *
     * @param userLogin to be saved
     */
    UserLogin createNewUser(UserLogin userLogin);

    /**
     * Delete  user in the database
     *
     * @param username user to be deleted
     */
    void deleteUser(String username);


    /**
     * Editing password that is already in the Database
     *
     * @param newPasswordReq password to be changed
     */
    void changePassword(NewPasswordReq newPasswordReq);

    /**
     * Editing passwrod of employee by admin that is already in the Database
     *
     * @param newPasswordReq password to be changed
     */
    void changePasswordByAdmin(NewPasswordReq newPasswordReq);
}
