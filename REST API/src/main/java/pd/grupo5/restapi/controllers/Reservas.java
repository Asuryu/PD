package pd.grupo5.restapi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pd.grupo5.restapi.database.DatabaseManager;
import pd.grupo5.restapi.models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;

@RestController
public class Reservas {

    @GetMapping("get_paid_reservations")
    public ArrayList<pd.grupo5.restapi.models.Reservas> getPaidReservations() {
        DatabaseManager dbManager = new DatabaseManager();
        ArrayList<pd.grupo5.restapi.models.Reservas> paidReservations = dbManager.getPaidReservations();

        if(paidReservations != null || paidReservations.size() > 0){
            return paidReservations;
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No paid reservations found!");
        }
    }

    @GetMapping("get_unpaid_reservations")
    public ArrayList<pd.grupo5.restapi.models.Reservas> getUnpaidReservations() {
        DatabaseManager dbManager = new DatabaseManager();
        ArrayList<pd.grupo5.restapi.models.Reservas> unpaidReservations = dbManager.getUnpaidReservations();

        if(unpaidReservations != null || unpaidReservations.size() > 0){
            return unpaidReservations;
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No unpaid reservations found!");
        }
    }
}