package pd.grupo5.restapi.controllers;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pd.grupo5.restapi.database.DatabaseManager;
import pd.grupo5.restapi.models.User;

@RestController
public class Authentication {

    @PostMapping("auth")
    public User login(@RequestBody User user) {
        DatabaseManager dbManager = new DatabaseManager();
        String sha256hex = DigestUtils.sha256Hex(user.getUsername() + System.currentTimeMillis());
        if(dbManager.loginUser(user.getUsername(), user.getPassword(), sha256hex)){
            user.setToken(sha256hex);
        }
        else{
            user.setToken("User not registered!");
        }

        return user;
    }
}