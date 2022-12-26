import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRMIInterface extends Remote {
    String REGISTRY_BIND_NAME = "REST_API_RMI_Service";
    void notifyClient(String message) throws RemoteException;
}
