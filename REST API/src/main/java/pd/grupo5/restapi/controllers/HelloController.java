package pd.grupo5.restapi.controllers;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pd.grupo5.restapi.database.DatabaseManager;
import pd.grupo5.restapi.models.User;

@RestController
public class HelloController {

    @GetMapping("hello")
    public String hello() {
        return "Hello World!";
    }
}