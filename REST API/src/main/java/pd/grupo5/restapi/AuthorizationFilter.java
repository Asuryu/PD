package pd.grupo5.restapi;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import pd.grupo5.restapi.database.DatabaseManager;
import pd.grupo5.restapi.models.User;

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

        DatabaseManager dbmanager = new DatabaseManager();
        // Obtencao do header o token de autenticacao
        String token = request.getHeader("Authorization");
        System.out.println("Token: " + token);

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        //todo: Verifica se token é válido e devolve o username do user
        int checkToken = dbmanager.checkToken(token);
        switch (checkToken){
            case -1: // Token expired
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                return;
            case 1: // Token valid
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("USER"));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(token, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                break;
            case 2: // Token valid (admin)
                List<GrantedAuthority> authorities2 = new ArrayList<>();
                authorities2.add(new SimpleGrantedAuthority("USER"));
                authorities2.add(new SimpleGrantedAuthority("ADMIN"));
                UsernamePasswordAuthenticationToken authentication2 = new UsernamePasswordAuthenticationToken(token, null, authorities2);
                SecurityContextHolder.getContext().setAuthentication(authentication2);
                break;
            case 0: // Token invalid
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
        }

        filterChain.doFilter(request, response);
        DatabaseManager.close();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // auth and espetaculos are public
        return path.equals("/auth") || path.equals("/espetaculos");
    }
}