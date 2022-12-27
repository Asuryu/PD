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
        switch(path){
            case "/api/v1/auth":
                if(request.getMethod().equals("POST")){
                    // Tomás, is this possible? It isn't working
                    // String user = request.getParameter("username");
                    // servidorRMI.notifyClients("[!] New login attempt: " + user);
                    servidorRMI.notifyClients("[!] New login attempt");
                }
                break;
            case "/api/v1/users":
                if(request.getMethod().equals("GET")){
                    servidorRMI.notifyClients("[!] User list requested");
                } else if (request.getMethod().equals("POST")){
                    servidorRMI.notifyClients("[!] User created");
                }
                break;
            case "/api/v1/users/{id}":
                //Method that deletes a user
                if(request.getMethod().equals("DELETE")){
                    servidorRMI.notifyClients("[!] User deleted");
                }
                break;
            case "/api/v1/espetaculos":
                if(request.getMethod().equals("GET")){
                    servidorRMI.notifyClients("[!] Espetaculo list requested");
                }
                break;
            case "/api/v1/get_paid_reservations":
                if(request.getMethod().equals("GET")){
                    servidorRMI.notifyClients("[!] Paid reservations requested");
                }
                break;
            case "/api/v1/get_unpaid_reservations":
                if(request.getMethod().equals("GET")){
                    servidorRMI.notifyClients("[!] Unpaid reservations requested");
                }
                break;
            default:
                break;
        }

        filterChain.doFilter(request, response);
        DatabaseManager.close();
    }
}
