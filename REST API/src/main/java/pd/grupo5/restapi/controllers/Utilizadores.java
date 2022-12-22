package pd.grupo5.restapi.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Utilizadores {

    @GetMapping("users")
    public String getUserList() {
        return "Fixe";
    }

    @PostMapping("users")
    public String addUser() {
        return "Fixe";
    }

    @DeleteMapping("users/{id}")
    public String getUser() {
        return "Fixe";
    }

}
