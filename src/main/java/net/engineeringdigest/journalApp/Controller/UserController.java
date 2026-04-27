package net.engineeringdigest.journalApp.Controller;

// Step 1: User enters username and password during login

// Step 2: Spring Security verifies the credentials
// (checks username + password from database/UserDetailsService)

// Step 3: If credentials are correct,
// Spring creates an Authentication object
// containing username, roles, authorities, etc.

// Step 4: This Authentication object is stored inside SecurityContext

// Step 5: SecurityContext is then stored inside SecurityContextHolder
// so that the currently logged-in user can be accessed anywhere
// in the application during that request/session


import net.engineeringdigest.journalApp.Entity.User;
import net.engineeringdigest.journalApp.repository.UserRepository;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired // injects the UserRepository bean automatically so database operations can be performed without manually creating its object
    private UserRepository userRepository;

    @DeleteMapping // maps this method to handle HTTP DELETE requests for deleting resources
    public ResponseEntity<?> deleteByUserName() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();// authentication object is like a visitor pass given by guard(spring security) after user authentication
        // fetches the currently logged-in user's authentication details from Spring Security context

        userRepository.deleteByUserName(authentication.getName());
        // gets the username of the logged-in user using authentication.getName() and deletes that user from the database

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        // returns HTTP status 204 (No Content), meaning deletion was successful and no response body is needed
    }
    //flow
    // Step 1: User sends request with credentials in Authorization header (for example using HTTP Basic Authentication)
// Step 2: Spring Security filter chain intercepts the request before it reaches the controller
// Step 3: Since .httpBasic() is configured, Spring extracts username and password from the Authorization header
// Step 4: Spring creates a temporary UsernamePasswordAuthenticationToken object with entered credentials
// Step 5: Because auth.userDetailsService(userDetailsService) is configured, Spring calls loadUserByUsername(username)
// Step 6: Custom UserDetailsService fetches username, hashed password, and roles from the database
// Step 7: Because .passwordEncoder(passwordEncoder()) is configured, Spring compares entered raw password with stored hashed password using passwordEncoder.matches()
// Step 8: If password matches, Spring creates a fully authenticated Authentication object
// Step 9: Spring stores this Authentication object inside SecurityContext using SecurityContextHolder.getContext().setAuthentication(auth)
// Step 10: Later in controller, SecurityContextHolder.getContext().getAuthentication() fetches the currently logged-in user's details
// Step 11: authentication.getName() returns the username of the authenticated user (for example: "jay")

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User userInDb = userService.findByUserName(userName);

        userInDb.setUserName(user.getUserName());
        userInDb.setPassword(user.getPassword());
        userService.saveNewUser(userInDb);
        return new ResponseEntity<>(HttpStatus.OK);


    }

}
