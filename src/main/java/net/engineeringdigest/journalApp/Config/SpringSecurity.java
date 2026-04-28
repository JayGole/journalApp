package net.engineeringdigest.journalApp.Config;

import net.engineeringdigest.journalApp.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration //Marks this as a configuration class
@EnableWebSecurity //for spring security features
public class SpringSecurity extends WebSecurityConfigurerAdapter {//Allows me to customize security behavior.I extend WebSecurityConfigurerAdapter to override default security configurations.


    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    @Override
    /*This method does not return anything.

    It only defines security rules.*/
    //spring automatically calls configure method(used for customisation) during startup
    protected void configure(HttpSecurity http) throws Exception{ //this method provides a way to configure how requests are secured. It defines how matching should be done and what security actions should be app;ied.
        http
                .authorizeRequests()//start authorising requests
                    .antMatchers("/journal/**","/user/**").authenticated() //any endpoint containing /journal/** is authenticated
                    .antMatchers("/admin/**").hasRole("ADMIN") //admin wali jo api endpoints hai woh un users se authenticate hongi, jinka role = "admin"
                    .anyRequest().permitAll() //any other request should be permissible without authentication
                .and() // phir se http pe chale gaye
                .httpBasic(); //without .httpBasic spring will forward user to default login page

        //to stop spring from creating session by default
        http.sessionManagement() // starts configuration for how Spring Security should manage user sessions
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // tells Spring not to create or use HTTP sessions; every request must carry authentication info again (ideal for REST APIs)
                .and() // ends session configuration and returns to main HttpSecurity configuration
                .csrf() // starts CSRF (Cross-Site Request Forgery) protection configuration
                .disable(); // disables CSRF protection, commonly done for stateless REST APIs tested via Postman where CSRF tokens are not needed //by default this is enabled so we had to disable it (cross site request forgery)
    }

    @Override // overrides Spring Security's default authentication configuration method from WebSecurityConfigurerAdapter
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService) // tells Spring to use the custom UserDetailsService implementation to load username, password, and roles from the database during authentication
                .passwordEncoder(passwordEncoder()); // tells Spring to use the configured PasswordEncoder (like BCrypt) to securely compare the raw password entered by the user with the hashed password stored in the database. in few words, checks password entered by user which is converted in base64 and then check with passwrod stored in database.
       /* passwordEncoder() = brings the lock machine
        .passwordEncoder(...) = gives that machine to the security guard*/
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); //converts password to hash
    }
}
