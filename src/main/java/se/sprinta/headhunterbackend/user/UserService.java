package se.sprinta.headhunterbackend.user;

import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.sprinta.headhunterbackend.system.exception.ObjectNotFoundException;
import se.sprinta.headhunterbackend.user.converter.UserToUserDtoViewConverter;
import se.sprinta.headhunterbackend.user.dto.UserDtoView;

import java.util.List;
import java.util.Optional;

/**
 * Business logic for Job
 */

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    public User findUserByEmail(String userEmail) {
        return this.userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new ObjectNotFoundException("user", userEmail));
    }

    public UserDtoView getUserByEmail(String userEmail) {
        return this.userRepository.getUserByEmail(userEmail)
                .orElseThrow(() -> new ObjectNotFoundException("user", userEmail));
    }

    public User save(User newUser) {
        newUser.setPassword(this.passwordEncoder.encode(newUser.getPassword()));
        return this.userRepository.save(newUser);
    }

    /**
     * Updates a user. Certain precautions has to be made for the roles.
     *
     * @param userEmail  Is used to find the User object that we want to update.
     * @param update Holds potentially both username and roles, or at least one of them.
     *               Any double quotes are being removed by the logic.
     * @return The updated User object.
     */

    public User update(String userEmail, User update) {
        User foundUser = this.userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new ObjectNotFoundException("user", userEmail));


        String rolesFixed = update.getRoles().replace("\"", "");
        foundUser.setRoles(rolesFixed);
        return this.userRepository.save(foundUser);
    }


    public void delete(String email) {
        User foundUser = this.userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("user", email));
        this.userRepository.delete(foundUser);
    }

    /**
     * UserDetails is fetched to Spring Security to check authentication.
     *
     * @param email Is used to find the User object that tries to log in.
     * @return UserDetails Here are the credentials that are being matched with the provided username and password by the user when trying to log in.
     * @throws UsernameNotFoundException If the User object doesn't exist, this exception is being thrown.
     */

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetails userDetails = this.userRepository.findUserByEmail(email) // First, we need to find this user from database.
                .map(MyUserPrincipal::new) // If found, wrap the returned user instance in a MyUserPrincipal instance.
                .orElseThrow(() -> new UsernameNotFoundException("email " + email + " is not found")); // Otherwise, throw an exception.
        return userDetails;
    }
}
