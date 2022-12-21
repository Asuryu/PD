package pd.grupo5.restapi;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pd.grupo5.restapi.database.DatabaseManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("AuthorizationFilter");
        DatabaseManager dbmanager = new DatabaseManager();

        // Obtencao do header o token de autenticacao
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        //todo: Verifica se token é válido e devolve o username do user
        switch (dbmanager.checkToken(token)){
            case -1: // Token expired
                filterChain.doFilter(request, response);
                break;
            case 1: // Token valid
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(token, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                break;
            default:
                filterChain.doFilter(request, response);
                DatabaseManager.close();
                break;
        }
    }

}