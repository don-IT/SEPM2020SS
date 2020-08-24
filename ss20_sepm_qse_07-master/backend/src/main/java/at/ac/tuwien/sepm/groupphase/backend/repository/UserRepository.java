package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

//TODO: replace this class with a correct ApplicationUser JPARepository implementation
@Repository
public class UserRepository {

    private final ApplicationUser user;
    private final ApplicationUser admin;

    @Autowired
    public UserRepository(PasswordEncoder passwordEncoder) {
        user = new ApplicationUser("user", passwordEncoder.encode("password"), false);
        admin = new ApplicationUser("admin", passwordEncoder.encode("password"), true);
    }

    public ApplicationUser findUserByUsername(String username) {
        if (username.equals(user.getUsername())) return user;
        if (username.equals(admin.getUsername())) return admin;
        return null; // In this case null is returned to fake Repository behavior
    }


}
