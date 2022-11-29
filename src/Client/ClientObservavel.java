package Client;

import Client.Model.ClientModel;
import Client.Model.Connection.ClientServerConnection;
import Client.shared.Connecting;


public class ClientObservavel {

    private ClientModel clientModel;
    private ClientServerConnection clientServerConnection;

    public ClientObservavel() {
        clientServerConnection = new ClientServerConnection(this);
        clientModel = new ClientModel(clientServerConnection);

    }
    public ClientModel getClientModel() {
        return clientModel;
    }
}
