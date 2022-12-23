package pd.grupo5.restapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@SpringBootApplication
public class RestApiApplication {

    public static void main(String[] args) {
            SpringApplication.run(RestApiApplication.class, args);
        }

    @EnableWebSecurity
    @Configuration
    class WebSecurityConfig extends WebSecurityConfigurerAdapter
    {

        @Override
        protected void configure(HttpSecurity http) throws Exception
        {
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/users/*").hasAuthority("ADMIN")
                    .antMatchers(HttpMethod.GET, "/users").hasAuthority("ADMIN")
                    .antMatchers(HttpMethod.POST, "/auth").permitAll()
                    .antMatchers(HttpMethod.GET, "/espetaculos").permitAll()
                    .anyRequest().authenticated().and()
                    .addFilterBefore(new AuthorizationFilter(), BasicAuthenticationFilter.class)
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and().exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        }
    }
}
