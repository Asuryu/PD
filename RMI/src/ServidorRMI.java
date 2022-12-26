import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServidorRMI extends UnicastRemoteObject implements ServerRMIInterface {

    ArrayList<ClientRMIInterface> clients = new ArrayList<>();

    @Override
    public void registerClient(ClientRMIInterface client) throws RemoteException {
        clients.add(client);
        System.out.println("[ * ] Client registered!");
        notifyClients();
    }

    private void notifyClients() throws RemoteException {
        for (ClientRMIInterface client : clients) {
            client.notifyClient("New client registered!");
        }
    }

    public ServidorRMI() throws RemoteException {}

    public static void main(String[] args) throws RemoteException {
        Registry r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        r.rebind(REGISTRY_BIND_NAME, new ServidorRMI());
    }

}
