package pd.grupo5.restapi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pd.grupo5.restapi.database.DatabaseManager;

import java.util.ArrayList;

@RestController
public class Espetaculos {

    @GetMapping("espetaculos")
    // optional parameter: ?data_inicio=2020-01-01&data_fim=2020-01-31
    public ArrayList<pd.grupo5.restapi.models.Espetaculos> getEspetaculos(String data_inicio, String data_fim) {
        DatabaseManager dbManager = new DatabaseManager();
        ArrayList<pd.grupo5.restapi.models.Espetaculos> espetaculos = dbManager.getEspetaculos(data_inicio, data_fim);

        if(espetaculos != null || espetaculos.size() > 0){
            return espetaculos;
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No espetaculos found!");
        }
    }

}