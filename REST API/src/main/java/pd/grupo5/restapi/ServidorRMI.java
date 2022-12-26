package pd.grupo5.restapi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServidorRMI extends UnicastRemoteObject implements ServerRMIInterface {

    public ArrayList<ClientRMIInterface> clients = new ArrayList<>();

    @Override
    public void registerClient(ClientRMIInterface client) throws RemoteException {
        clients.add(client);
    }

    public void notifyClients(String mensagem) throws RemoteException {
        for (ClientRMIInterface client : clients) {
            client.notifyClient(mensagem);
        }
    }

    public ServidorRMI() throws RemoteException {
        Registry r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        r.rebind(REGISTRY_BIND_NAME, this);
        System.out.println("[ * ] Servidor RMI iniciado");
    }

}
