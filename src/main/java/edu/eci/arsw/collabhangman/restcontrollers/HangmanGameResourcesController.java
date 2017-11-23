/*
 * Copyright (C) 2016 Pivotal Software, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.arsw.collabhangman.restcontrollers;

import com.mongodb.util.JSON;
import edu.eci.arsw.collabhangman.model.game.entities.HangmanLetterAttempt;
import edu.eci.arsw.collabhangman.model.game.entities.HangmanWordAttempt;
import edu.eci.arsw.collabhangman.services.GameServicesException;
import edu.eci.arsw.collabhangman.services.GameServices;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hcadavid
 */
@RestController
@RequestMapping("/hangmangames")
public class HangmanGameResourcesController {
    
    @Autowired
    GameServices gameServices;
    
    @Autowired
    SimpMessagingTemplate msmt;
    
    @RequestMapping(method = RequestMethod.GET)
    public String test() {
        return "Ok";
    }
    
    @RequestMapping(path = "/{gameid}/currentword", method = RequestMethod.GET)
    public ResponseEntity<?> getCurrentWord(@PathVariable Integer gameid) {
        try {
            return new ResponseEntity<>(gameServices.getCurrentGuessedWord(gameid), HttpStatus.ACCEPTED);
        } catch (GameServicesException ex) {
            return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @RequestMapping(path = "/{gameid}/letterattempts", method = RequestMethod.POST)
    public ResponseEntity<?> tryLetterInGame(@PathVariable Integer gameid, @RequestBody HangmanLetterAttempt hga) {
        try {
            
            String tmp = gameServices.addLetterToGame(gameid, hga.getLetter());
            LOG.log(Level.INFO, "Getting letter from client {0}:{1}", new Object[]{hga.getUsername(), hga.getLetter()});
            msmt.convertAndSend("/topic/wupdate." + gameid, tmp);
            boolean win = gameServices.guessWord(hga.getUsername(), gameid, tmp);
            LOG.log(Level.INFO, "Getting word from client {0}:{1}", new Object[]{hga.getUsername(), tmp});
            if (win) {
                msmt.convertAndSend("/topic/winner." + gameid, hga.getUsername());
                msmt.convertAndSend("/topic/wupdate." + gameid, tmp);
            }
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (GameServicesException ex) {
            Logger.getLogger(HangmanGameResourcesController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("No existe el juego", HttpStatus.FORBIDDEN);
        }
    }
    private static final Logger LOG = Logger.getLogger(HangmanGameResourcesController.class.getName());
    
    @RequestMapping(path = "/{gameid}/wordattempts", method = RequestMethod.POST)
    public ResponseEntity<?> attemptAWord(@PathVariable Integer gameid, @RequestBody HangmanWordAttempt hwa){
        try {
            
            boolean win = gameServices.guessWord(hwa.getUsername(), gameid, hwa.getWord());
            LOG.log(Level.INFO, "Getting word from client {0}:{1}", new Object[]{hwa.getUsername(), hwa.getWord()});
            if (win) {
                msmt.convertAndSend("/topic/winner." + gameid, hwa.getUsername());
                msmt.convertAndSend("/topic/wupdate." + gameid, hwa.getWord());
            }
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (GameServicesException ex) {
            Logger.getLogger(HangmanGameResourcesController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("No existe el juego", HttpStatus.FORBIDDEN);
        }
    }
    
}
