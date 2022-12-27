package pd.grupo5.restapi.RMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServidorRMI extends UnicastRemoteObject implements ServerRMIInterface {

    public ArrayList<ClientRMIInterface> clients = new ArrayList<>();

    @Override
    public void registerClient(ClientRMIInterface client) throws RemoteException {
        System.out.println("[ + ] A client has registered in the RMI server");
        clients.add(client);
    }

    public void notifyClients(String mensagem) throws RemoteException {
        int count = 0;
        for (int i = 0; i < clients.size(); i++) {
            try {
                clients.get(i).notifyClient(mensagem);
                count++;
            } catch (RemoteException e) {
                System.out.println("[ - ] A client has disconnected from the RMI server");
                clients.remove(i);
            }
        }
        System.out.println("[ * ] " + count + " client(s) have been notified");
    }

    public ServidorRMI() throws RemoteException {
        try {
            Registry r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            r.rebind(REGISTRY_BIND_NAME, this);
            System.out.println("[ * ] RMI server started");
        } catch (Exception e) {
            System.out.println("[ ! ] An error has occurred while starting the RMI server:");
            System.out.println("      " + e.getMessage());
        }
    }
}
