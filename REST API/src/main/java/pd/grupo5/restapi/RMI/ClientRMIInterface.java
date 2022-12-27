package pd.grupo5.restapi.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRMIInterface extends Remote {
    void notifyClient(String message) throws RemoteException;
}
