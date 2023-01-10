package pd.grupo5.restapi.RMI;

import com.nimbusds.jose.shaded.json.JSONObject;
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
        String message = "";
        switch(path){
            case "/api/v1/auth":
                if(request.getMethod().equals("POST")){
                    message = "[POST] New login attempt";
                }
                break;
            case "/api/v1/users":
                if(request.getMethod().equals("GET")){
                    message = "[GET] User list requested";
                } else if (request.getMethod().equals("PUT")){
                    message = "[PUT] User created";
                } else if(request.getMethod().equals("DELETE")){
                    message = "[DELETE] User deleted";
                }
                break;
            case "/api/v1/espetaculos":
                if(request.getMethod().equals("GET")){
                    message = "[GET] Show list requested";
                }
                break;
            case "/api/v1/get_paid_reservations":
                if(request.getMethod().equals("GET")){
                    message = "[GET] Paid reservations requested";
                }
                break;
            case "/api/v1/get_unpaid_reservations":
                if(request.getMethod().equals("GET")){
                    message = "[GET] Unpaid reservations requested";
                }
                break;
            default:
                break;
        }
        servidorRMI.notifyClients(message);

        filterChain.doFilter(request, response);
        DatabaseManager.close();
    }
}
