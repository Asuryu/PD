package Client.shared;

import java.io.Serializable;
import java.util.LinkedList;

import Client.shared.IpPort;

public class RequestToConnect implements Serializable, Answers {
    public final LinkedList<IpPort> servers;
    /**
     * Returns the port for the tcp or an error:<br/>
     * <b>ERROR_USER_INFO_NOT_MATCH</b><br/>
     * or<br/>
     * <b>ERROR_SERVER_FULL</b><br/>
     */
    public final int TcpPort;

    public RequestToConnect(int TcpPort, LinkedList<IpPort> servers) {
        this.TcpPort = TcpPort;
        this.servers = servers;
    }

    public RequestToConnect() {
        this.TcpPort = -1;
        this.servers = null;
    }


}
