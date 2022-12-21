package pd.grupo5.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
            http.csrf().disable()
                    .addFilterAfter(new AuthorizationFilter(),
                            UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/auth").permitAll() // Permitir users sem login de fazer pedidos POST com este URI
                    .anyRequest().authenticated().and() // Obrigar aos users estarem autenticados para outro pedido qualquer
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Todos os pedidos tem que ter a informação necesária: stateless
                    .and().exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        }
    }

}
