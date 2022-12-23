package pd.grupo5.restapi.controllers;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pd.grupo5.restapi.database.DatabaseManager;
import pd.grupo5.restapi.models.User;

@RestController
public class Authentication {

    @PostMapping("auth")
    public User login(@RequestBody User user) {
        DatabaseManager dbManager = new DatabaseManager();
        String sha256hex = DigestUtils.sha256Hex(user.getUsername() + System.currentTimeMillis());
        int result = dbManager.loginUser(user.getUsername(), user.getPassword(), sha256hex);
        if(result > 0){
            user.setToken(sha256hex);
            user.setId(result);
        }
        else{
            user.setToken("User not registered!");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Could not login!");
        }
        return user;
    }
}