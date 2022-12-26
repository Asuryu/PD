import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRMIInterface extends Remote {
    String REGISTRY_BIND_NAME = "REST_API_RMI_Service";

    void registerClient(ClientRMIInterface client) throws RemoteException;
}
