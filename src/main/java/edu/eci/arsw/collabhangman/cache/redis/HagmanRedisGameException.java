/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.collabhangman.cache.redis;

/**
 *
 * @author laura
 */
public class HagmanRedisGameException extends Exception{

    public HagmanRedisGameException(String message) {
        super(message);
    }

    public HagmanRedisGameException(String message, Throwable cause) {
        super(message, cause);
    }

    public HagmanRedisGameException(Throwable cause) {
        super(cause);
    }
    
}

