/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.collabhangman.restcontrollers;

import edu.eci.arsw.collabhangman.services.GameServices;
import edu.eci.arsw.collabhangman.services.GameServicesException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hcadavid
 */
@RestController
@RequestMapping("/users")
public class UsersResourceController {

    @Autowired
    GameServices gameServices;

    @Autowired
    SimpMessagingTemplate msmt;

    @RequestMapping(path = "/{userid}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable String userid) {
        try {
            int id = Integer.parseInt(userid);
            return new ResponseEntity<>(gameServices.loadUserData(id), HttpStatus.OK);
        } catch (NumberFormatException ex) {
            Logger.getLogger(UsersResourceController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Please insert a valid user ID", HttpStatus.BAD_REQUEST);
        } catch (GameServicesException ex) {
            Logger.getLogger(UsersResourceController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("There was an exception loading the user's data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(method = RequestMethod.GET, path = "/scores/{score}")
    public ResponseEntity<?> findByScore(@PathVariable int score) {
        return new ResponseEntity<>(gameServices.score(score), HttpStatus.ACCEPTED);
    }
}
