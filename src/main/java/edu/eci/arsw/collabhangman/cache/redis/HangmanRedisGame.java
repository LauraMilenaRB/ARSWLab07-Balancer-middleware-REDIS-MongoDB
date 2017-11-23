/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.collabhangman.cache.redis;

import edu.eci.arsw.collabhangman.model.game.HangmanGame;
import java.util.Collections;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import static reactor.bus.selector.Selectors.T;

/**
 *
 * @author laura
 */
public class HangmanRedisGame extends HangmanGame{
    private String id;
    private StringRedisTemplate template; 
    public HangmanRedisGame(int id,String word, StringRedisTemplate tmpl) {
        super(word);
        template=tmpl;
        this.id="gameid:"+String.valueOf(id);
        template.opsForHash().put(this.id,"word",word);
        template.opsForHash().put(this.id,"guessedword",new String(super.guessedWord));
        template.opsForHash().put(this.id,"winner","");
        template.opsForHash().put(this.id,"gamestatus",false);
    }
    
    public HangmanRedisGame(int id,StringRedisTemplate tmpl) {
        super("");
        this.id="gameid:"+String.valueOf(id);
        template=tmpl;
        
        
    }

  
    /**
     * @pre gameFinished==false
     * @param l new letter
     * @return the secret word with all the characters 'l' revealed
     */
    @Override
    public String addLetter(char l){  
        /*
        String guessedWor=(String)template.opsForHash().get(id, "guessedword");
        String w=(String)template.opsForHash().get(id, "word");
        char[] guessedWordchar = guessedWor.toCharArray();
        System.out.println(w);
        for (int i=0;i<w.length();i++){
            if (w.charAt(i)==l){
                guessedWordchar[i]=l;
            }            
        }    
        String value=new String(guessedWordchar);
        System.out.println(value);
        template.opsForHash().put(id,"guessedword",value);
        System.out.println((String)template.opsForHash().get(id, "guessedword"));
        return (String)template.opsForHash().get(id, "guessedword");*/
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("src/main/resources/scriptsLUA/addLetter.lua")));
        redisScript.setResultType(String.class);
        String resp=template.execute(new SessionCallback<String>() {
            @SuppressWarnings("unchecked")
            @Override
            public <K, V> String execute(final RedisOperations<K, V> op) throws DataAccessException {
                op.watch((K)id);
                op.multi();
                String res= op.execute(redisScript, Collections.singletonList((K) id), String.valueOf(l));
                op.exec();
                return res;
            }
        });
        return resp;
    }
    
    @Override
    public synchronized boolean tryWord(String playerName,String s){
        String w=(String)template.opsForHash().get(id, "word");
        if (s.toLowerCase().equals(w)){
            template.opsForHash().put(id,"winner",playerName);
            template.opsForHash().put(id,"gamestatus",true);
            template.opsForHash().put(id,"guessedword",s.toLowerCase());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean gameFinished(){
        String bol=(String)template.opsForHash().get(id, "gamestatus");
        return Boolean.valueOf(bol);
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
