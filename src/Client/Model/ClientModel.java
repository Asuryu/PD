package Client.Model;

import Client.shared.Connecting;
import Client.Model.Connection.ClientServerConnection;
import Client.shared.User;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientModel {
    public  List<User> listUsers = null;
    public Integer myID = null;
    public Connecting request = null;
    public ClientServerConnection csc = null;

    public Connecting getRequest(){return request;}
    public void setRequest(){this.request= request;}

    public ClientModel(ClientServerConnection csc) {

        this.csc = csc;
        this.listUsers = new CopyOnWriteArrayList<>();

    }

    public int registOrLogin(Connecting c) {
        return csc.connectToServer(c);
    }

    public int getUserIdByName(String name){
        for (var i : listUsers)
            if(i.getNome().equals(name))
                return i.getId();
        return -1;
    }
    public int getMyId(){
        if(myID == null)
        {
            for (var i : listUsers)
                if (i.getUsername() == request.getUsername())
                    myID = i.getId();
        }
        return myID;
    }

}

