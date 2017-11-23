/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.collabhangman.persistence.stub;

import edu.eci.arsw.collabhangman.model.game.entities.User;
import edu.eci.arsw.collabhangman.persistence.PersistenceException;
import edu.eci.arsw.collabhangman.persistence.UsersRepository;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 *
 * @author hcadavid
 */
/*@Service*/
public class UsersRepositoryStub /*implements UserRepository*/{

    private static Map<Integer,User> usersdb;
    
    static{
        usersdb=new ConcurrentHashMap<>();
        usersdb.put(112233, new User(112233,"Maria Perez","http://www.your3dsource.com/images/facepic1.jpeg"));
        usersdb.put(223344, new User(223344,"Luis Rodriguez","http://www.your3dsource.com/images/facepic4.jpeg"));
        usersdb.put(334455, new User(334455,"Pedro Rodriguez","http://www.your3dsource.com/images/facepic5.jpeg"));
    }

    public User getUserByID(Integer id) throws PersistenceException {
        if (!usersdb.containsKey(id)){
            throw new PersistenceException("User not found:"+id);
        }
        else{
            return usersdb.get(id);
        }
    }

    public Set<User> getAllUsers() {
        return new LinkedHashSet<>(usersdb.values());
    }
    

    
}
