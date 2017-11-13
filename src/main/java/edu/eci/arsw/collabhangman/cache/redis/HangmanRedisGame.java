/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.collabhangman.cache.redis;

import edu.eci.arsw.collabhangman.model.game.HangmanGame;
import edu.eci.arsw.collabhangman.services.GameCreationException;
import edu.eci.arsw.collabhangman.services.GameServicesException;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 *
 * @author laura
 */
public class HangmanRedisGame extends HangmanGame{
    private final String id;
    private final StringRedisTemplate template; 
    public HangmanRedisGame(int id,String word, StringRedisTemplate tmpl) {
        super(word);
        template=tmpl;
        this.id=String.valueOf(id);
        template.opsForHash().put(this.id,"word",word);
        template.opsForHash().put(this.id,"guessedword",new String(super.guessedWord));
        template.opsForHash().put(this.id,"winner","");
        template.opsForHash().put(this.id,"gamestatus",false);
    }
    
    public HangmanRedisGame(int id,StringRedisTemplate tmpl) {
        super("");
        this.id=String.valueOf(id);
        template=tmpl;
        
        
    }

  
    /**
     * @pre gameFinished==false
     * @param l new letter
     * @return the secret word with all the characters 'l' revealed
     */
    @Override
    public String addLetter(char l){  
        String guessedWor=(String)template.opsForHash().get(id, "guessedword");
        String w=(String)template.opsForHash().get(id, "word");
        char[] guessedWordchar = guessedWor.toCharArray();
        for (int i=0;i<w.length();i++){
            if (w.charAt(i)==l){
                guessedWordchar[i]=l;
            }            
        }    
        String value=new String(guessedWordchar);
        template.opsForHash().put(id,"guessedWord",value);
        return value;
    }
    
    @Override
    public synchronized boolean tryWord(String playerName,String s){
        String w=(String)template.opsForHash().get(id, "word");
        if (s.toLowerCase().equals(w)){
            template.opsForHash().put(id,"winner",playerName);
            template.opsForHash().put(id,"gameFinished",true);
            template.opsForHash().put(id,"guessedword",w);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean gameFinished(){
        return (boolean)template.opsForHash().get(id, "gameFinished");
    }
    
    /**
     * @pre gameFinished=true;
     * @return winner's name
     */
    @Override
    public String getWinnerName(){
        return (String)template.opsForHash().get(id, "winner");
    }
    
    @Override
    public String getCurrentGuessedWord(){
        return (String)template.opsForHash().get(id, "guessedword");
    }    
    
}
