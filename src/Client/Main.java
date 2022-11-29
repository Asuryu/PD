package Client;



import   Client.UI.TextUserInterface;

public class Main {

    public static void main(String[] args) {

      /*  String serverAddress;
        int serverPort;
        DatagramSocket mySocket = null;
        DatagramPacket datagramPacket;
        ByteArrayInputStream bin;
        ObjectInputStream oin;

        ByteArrayOutputStream bout;
        ObjectOutputStream oout;*/

      //  HashMap<String, Integer> serverList = new HashMap<String,Integer>();

        if(args.length!=2){
            System.out.println("Usage: java <name> <server address> <server port>");
            return;
        }
        System.out.println("A comecar o cliente....");
        ClientObservavel clientObservavel = new ClientObservavel();
        TextUserInterface userInterface = new TextUserInterface(clientObservavel);
        userInterface.UI();
        /*try
        {
            serverAddress = args[0];
            serverPort = Integer.parseInt(args[1]);
            mySocket = new DatagramSocket();
            mySocket.setSoTimeout(TIMEOUT*1000);
            byte[] buffer = new byte[MAX_PACKET_SIZE];

            mySocket.connect(InetAddress.getByName(serverAddress), serverPort);

            //Serializar a String TIME para um array de bytes encapsulado por bout
            bout = new ByteArrayOutputStream();
            oout = new ObjectOutputStream(bout);
            oout.writeObject(TIMEOUT);
            //Construir um datagrama UDP com o resultado da serialização
            datagramPacket = new DatagramPacket(bout.toByteArray(), bout.size(), InetAddress.getByName(serverAddress), serverPort);
            mySocket.send(datagramPacket);
            datagramPacket = new DatagramPacket(new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);

            mySocket.receive(datagramPacket);
            bin = new ByteArrayInputStream(datagramPacket.getData(), 0, datagramPacket.getLength());
            oin = new ObjectInputStream(bin);


        } catch (UnknownHostException e) {
        System.out.println("Destino desconhecido:\n\t" + e);
        } catch (NumberFormatException e) {
        System.out.println("O porto do servidor deve ser um inteiro positivo.");
        } catch (SocketTimeoutException e) {
        System.out.println("Nao foi recebida qualquer resposta:\n\t" + e);
        } catch (SocketException e) {
        System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t" + e);
        } catch (IOException e) {
        System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
        }*/


    }


}