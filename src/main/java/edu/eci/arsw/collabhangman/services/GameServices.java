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
//NOTA: los comentarios no llevan acentos para evitar conflictos de codificacion
package edu.eci.arsw.collabhangman.services;

import edu.eci.arsw.collabhangman.cache.redis.GameStateRedisCache;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.eci.arsw.collabhangman.model.game.entities.User;
import edu.eci.arsw.collabhangman.persistence.PersistenceException;
import edu.eci.arsw.collabhangman.persistence.UsersRepository;
import edu.eci.arsw.collabhangman.persistence.WordsRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

  
/**
 *
 * @author hcadavid
 */
@Service
public class GameServices {
   
    //cache con los datos volatiles del juego
    @Autowired
    GameStateRedisCache cache;
    
    //repositorios (capa de persistencia)
    @Autowired
    UsersRepository usersRepository;
    
    @Autowired
    WordsRepository wordsRepository;

   
    private final Random random;

    public GameServices() {
        random=new Random(System.currentTimeMillis());
    }
    
    public User loadUserData(int userid) throws GameServicesException{
        return usersRepository.findById(userid);
    }
    
    
    public Set<User> getAllUsers(){
        return new HashSet<>();
    }
    
    /**
     * Crea un nuevo juego, con una palabra creada al azar
     * @param gameid
     * @throws GameCreationException 
     * @throws edu.eci.arsw.collabhangman.services.GameServicesException 
     */
    public void createGame(int gameid) throws GameCreationException, GameServicesException{
       List<String> words=wordsRepository.loadAllWords();
       String word=words.get(random.nextInt(words.size()));       
       cache.createGame(gameid, word);
    }

    /**
     * Selecciona una nueva letra para la palabra secreta del juego identificado
     * con 'gameid'.
     * @param gameid identificador del juego
     * @param letter letra elegida por el usuario
     * @return la nueva palabra, mostrando la letra elegida. Por ejemplo, si 
     * la palabra es 'felicidad', y en el primer intento del juego se elige
     * la letra 'd', el metodo retornara:  '______d_d'. Si en un siguiente intento
     * se elige la letra 'i', el metodo retornara: '___i_id_d'
     * @throws GameServicesException si el identificador dado no corresponde
     * a una partida existente.
     */
    public String addLetterToGame(int gameid,char letter) throws GameServicesException{
        return cache.getGame(gameid).addLetter(letter);
    }
    
    /**
     * Retorna la palabra que esta siendo adivinada actualmente en el juego
     * identificado con 'gameid', en su estado actual (es decir, ocultando 
     * las letras aun no descubiertas)
     * @param gameid
     * @return la palabra en su estado actual, ocultando los caracteres
     * no descubiertos.
     * @throws GameServicesException si el identificador dado no corresponde
     * a una partida existente.
     */
    public String getCurrentGuessedWord(int gameid) throws GameServicesException{
        return cache.getGame(gameid).getCurrentGuessedWord();
    }
    
    /**
     * Permite realizar un intento de adivinar la palabra secreta.
     * @param playerName el nombre del jugador que realiza el intento.
     * @param gameid el identificador del juego de la partida
     * @param word la palabra que el usuario su  
     * @pos Si el intento es exitoso, el estado del juego almacenado
     *      en el  cache se actualiza internamente: el nombre del ganador,
     *      y el estado del juego (finalizado).
     * @return true si la palabra fue adivinada, false d.l.c.
     * @throws GameServicesException si el identificador dado no corresponde
     * a una partida existente.
     */
    public boolean guessWord(String playerName,int gameid,String word) throws GameServicesException{
        return cache.getGame(gameid).tryWord(playerName,word);
    }
    
    
    /**
     * Indica si el juego del identificador dado ya ha sido finalizado
     * @param gameid
     * @return true si el juego termino, false d.l.c.
     * @throws GameServicesException si el identificador dado no corresponde
     * a una partida existente.
     */
    public boolean isGameFinished(int gameid) throws GameServicesException{
        return cache.getGame(gameid).gameFinished();
    }
    
    /**
     * Consulta el nombre del jugador declarado como ganador
     * @pre isGameFinished==true
     * @param gameid
     * @return el nombre del jugador ganador.
     * @throws GameServicesException si el identificador dado no corresponde
     * a una partida existente.
     */
    public String getGameWinner(int gameid) throws GameServicesException{
        return cache.getGame(gameid).getWinnerName();
    }
    public List<User> score(int score){
        return usersRepository.findByScore(score);
    }
  
    
}
