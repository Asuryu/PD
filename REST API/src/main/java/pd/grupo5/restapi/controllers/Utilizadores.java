package pd.grupo5.restapi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pd.grupo5.restapi.database.DatabaseManager;
import pd.grupo5.restapi.models.Espetaculos;
import pd.grupo5.restapi.models.Utilizador;

import java.util.ArrayList;

@RestController
public class Utilizadores {

    @GetMapping("users")
    public ArrayList<Utilizador> getUserList() {
        DatabaseManager dbManager = new DatabaseManager();
        ArrayList<Utilizador> utilizadores = dbManager.getRegisteredUsers();

        if(utilizadores != null || utilizadores.size() > 0){
            return utilizadores;
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No users found!");
        }
    }

    static class JsonUserObject {
        public String username;
        public String nome;
        public String password;
        public boolean isAdmin;

        public JsonUserObject(String username, String nome, String password, boolean isAdmin) {
            this.username = username;
            this.nome = nome;
            this.password = password;
            this.isAdmin = isAdmin;
        }

    }

    @PutMapping("users")
    // receive 3 parameters: username (string), nome (string), password (string) and admin (boolean)
    public Utilizador addUser(@RequestBody JsonUserObject user) {
        DatabaseManager dbManager = new DatabaseManager();
        boolean result = dbManager.addUser(user.username, user.nome, user.password, user.isAdmin);

        if(result){
            throw new ResponseStatusException(HttpStatus.OK, "User successfully added!");
        }
        else{
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "User already exists!");
        }

    }

    @DeleteMapping("users/{id}")
    public String deleteUser(@PathVariable int id) {
        DatabaseManager dbManager = new DatabaseManager();
        boolean result = dbManager.deleteUser(id);
        if(result){
            throw new ResponseStatusException(HttpStatus.OK, "User successfully deleted!");
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
    }
}
