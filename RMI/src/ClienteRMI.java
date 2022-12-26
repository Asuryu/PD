import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClienteRMI extends UnicastRemoteObject implements ClientRMIInterface {

    public ClienteRMI() throws RemoteException {}

    @Override
    public void notifyClient(String message) throws RemoteException {
        System.out.println("[ * ] Message received: " + message);
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry r = LocateRegistry.getRegistry(null, Registry.REGISTRY_PORT);
        ServerRMIInterface remoteRef = (ServerRMIInterface) r.lookup(ServerRMIInterface.REGISTRY_BIND_NAME);
        remoteRef.registerClient(new ClienteRMI());
    }

}