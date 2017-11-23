/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.collabhangman.cache.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author laura
 */
@Service
public class GameStateRedisCache {
    @Autowired
    private StringRedisTemplate template;
    
    public void createGame(int id,String word) throws HagmanRedisGameException{
        
    };

    public HangmanRedisGame getGame(int gameid) throws HagmanRedisGameException{
        return new HangmanRedisGame(gameid,template);
    };
}
