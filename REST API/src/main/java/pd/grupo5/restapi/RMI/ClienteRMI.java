package pd.grupo5.restapi.RMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClienteRMI extends UnicastRemoteObject implements ClientRMIInterface {

    public ClienteRMI() throws RemoteException {}

    @Override
    public void notifyClient(String message) throws RemoteException {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.println( "[" + formatter.format(date) + "] " + message);
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        try{
            Registry r = LocateRegistry.getRegistry(null, Registry.REGISTRY_PORT);
            ServerRMIInterface remoteRef = (ServerRMIInterface) r.lookup(ServerRMIInterface.REGISTRY_BIND_NAME);
            remoteRef.registerClient(new ClienteRMI());
            System.out.println("[ * ] RMI client started");
        } catch (Exception e) {
            System.out.println("[ ! ] An error has occurred while connecting to the RMI server:");
            System.out.println("      " + e.getMessage());
        }
    }

}