package se.sprinta.headhunterbackend.user;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import se.sprinta.headhunterbackend.system.Result;
import se.sprinta.headhunterbackend.system.StatusCode;
import se.sprinta.headhunterbackend.user.converter.UserDtoFormToUserConverter;
import se.sprinta.headhunterbackend.user.converter.UserToUserDtoViewConverter;
import se.sprinta.headhunterbackend.user.dto.UserDtoForm;
import se.sprinta.headhunterbackend.user.dto.UserDtoView;

import java.util.List;

/**
 * Backend API endpoints for User.
 */

@RestController
@RequestMapping("${api.endpoint.base-url-users}")
@CrossOrigin("http://localhost:3000")
public class UserController {

    private final UserService userService;
    private final UserToUserDtoViewConverter userToUserDtoViewConverter;
    private final UserDtoFormToUserConverter userDtoFormToUserConverter;

    public UserController(UserService userService, UserToUserDtoViewConverter userToUserDtoViewConverter, UserDtoFormToUserConverter userDtoFormToUserConverter) {
        this.userService = userService;
        this.userToUserDtoViewConverter = userToUserDtoViewConverter;
        this.userDtoFormToUserConverter = userDtoFormToUserConverter;
    }

    @GetMapping("/findAll")
    public Result findAllUsers() {
        List<User> foundUsers = this.userService.findAll();
        List<UserDtoView> foundUserDtos = foundUsers.stream()
                .map(this.userToUserDtoViewConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Find All User Success", foundUserDtos);
    }

    @GetMapping("/findUser/{email}")
    public Result findUserByEmail(@PathVariable String email) {
        User foundUser = this.userService.findByUserEmail(email);
        UserDtoView foundUserDto = this.userToUserDtoViewConverter.convert(foundUser);
        return new Result(true, StatusCode.SUCCESS, "Find One User Success", foundUserDto);
    }

    @PostMapping("/register")
    public Result registerUser(@Valid @RequestBody User user) {
        user.setRoles("user");
        User addedUser = this.userService.save(user);
        UserDtoView addedUserDto = this.userToUserDtoViewConverter.convert(addedUser);
        return new Result(true, StatusCode.SUCCESS, "Add User Success", addedUserDto);
    }

    @PostMapping("/addUser")
    public Result addUser(@Valid @RequestBody User user) {
        User addedUser = this.userService.save(user);
        UserDtoView addedUserDto = this.userToUserDtoViewConverter.convert(addedUser);
        return new Result(true, StatusCode.SUCCESS, "Add Success", addedUserDto);
    }

    @PutMapping("/update/{email}")
    public Result updateUser(@PathVariable String email, @RequestBody UserDtoForm userDtoForm) {
        User update = this.userDtoFormToUserConverter.convert(userDtoForm);
        User user = this.userService.update(email, update);
        UserDtoView updatedUserDto = this.userToUserDtoViewConverter.convert(user);
        return new Result(true, StatusCode.SUCCESS, "Update User Success", updatedUserDto);
    }

    @DeleteMapping("/delete/{email}")
    public Result deleteUser(@PathVariable String email) {
        this.userService.delete(email);
        return new Result(true, StatusCode.SUCCESS, "Delete User Success");
    }
}
