package pd.grupo5.restapi.RMI;

import org.springframework.web.filter.OncePerRequestFilter;
import pd.grupo5.restapi.database.DatabaseManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RMICallback extends OncePerRequestFilter {

    private ServidorRMI servidorRMI;

    public RMICallback(ServidorRMI servidorRMI) {
        this.servidorRMI = servidorRMI;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println(path);
        // TODO: fazer um switch case (mensagens bonitinhas)
        // request.getMethod(); - podes usar isto para saber se o pedido foi GET, POST, PUT, DELETE
        // Boa sorte :)
        switch(path){
            case "/api/v1/auth": // EXEMPLO
                servidorRMI.notifyClients("Um utilizador fez login");
                break;
            default:
                break;
        }

        filterChain.doFilter(request, response);
        DatabaseManager.close();
    }
}
