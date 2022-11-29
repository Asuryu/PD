package Client.Model.Connection;

import Client.ClientObservavel;
import Client.shared.Connecting;
import Client.shared.IpPort;
import Client.shared.RequestToConnect;
import Client.shared.Utils;


import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static Client.shared.Utils.objectToBytes;


public class ClientServerConnection extends Thread{
    private final List<IpPort> servers = Collections.synchronizedList(new LinkedList<>());
    private int retries = 0;
    private int tries = 0;
    private RequestToConnect resposta = null;
    private Connecting request = null;
    private InetAddress serverAdd = null;
    private Socket socket = null;
    private InputStream isTCP = null;
    private OutputStream osTCP = null;
    private ClientObservavel clientObservavel;

    // timeout para conectar com o server(em segundos)
    private final int TIMEOUT = 1;

    public ClientServerConnection(ClientObservavel clientObservavel) {
        this.clientObservavel = clientObservavel;
    }

    //Seleciona o proximo ip/port para o servidor a ligar

    private IpPort getIpPort() {
        if (servers.size() == 0) {
            if (retries <= 3) {
                return new IpPort(Utils.Consts.SERVER_ADDRESS, Utils.Consts.UDP_CLIENT_REQUEST_PORT);
            } else {
                throw new Error("Não existem servidores online contactaveis.");
            }
        } else {
            if (retries < 2) {
                tries++;
                retries = 0;
            }
            if (tries >= servers.size())
                throw new Error("Não existem servidores online contactaveis.  if (tries >= servers.size())");
            return new IpPort(servers.get(tries).ip, servers.get(tries).port);
        }
    }

    private InetAddress connectUdp(Connecting request) {
        try {
            IpPort ipPort = getIpPort();
            var requestBytes = objectToBytes(request);
            DatagramSocket socket = new DatagramSocket();
            assert requestBytes != null;
            DatagramPacket packet = new DatagramPacket(
                    requestBytes, requestBytes.length,
                    InetAddress.getByName(Utils.Consts.SERVER_ADDRESS),
                    Utils.Consts.UDP_CLIENT_REQUEST_PORT
            );
            socket.setSoTimeout(TIMEOUT * 1000);
            //send packet
            socket.send(packet);

            //receive packet
            byte[] buffer = new byte[Utils.Consts.MAX_SIZE_PER_PACKET];
            packet.setData(buffer, 0, buffer.length);
            socket.receive(packet);
            resposta = (RequestToConnect) Utils.bytesToObject(packet.getData());
            retries = 0;
            tries = 0;
            return packet.getAddress();
        } catch (SocketTimeoutException | SocketException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        ++retries;
        return null;
    }
    private void connectTcp(InetAddress ip) throws Exception {
        socket = new Socket(ip, resposta.TcpPort);
        isTCP = socket.getInputStream();
        osTCP = socket.getOutputStream();
    }
    
    public int connectToServer(Connecting request) {
        this.request = request;
        while (true) {//condição para que o servidor esteja sempre a conectar
            try {
                while ((serverAdd = connectUdp(request)) == null) ;
            } catch (Error e) {
                //Caso ocorra erro a ligar a um server ele cancela a conexao.
                interrupt();
                e.printStackTrace();
                return resposta.TcpPort;
            }
            if (Utils.Consts.DEBUG)
                System.out.println("Recebido resposta de : " + serverAdd + "; port:" + resposta.TcpPort + "; servers: " + resposta.servers);

            if (0 < resposta.TcpPort) {
                try {
                    connectTcp(serverAdd);
                    start();
                    return resposta.TcpPort;
                } catch (Exception e) {
                    break;
                }
            }
        }
        return 0;
    }
}
