/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.collabhangman.model.game.entities;

/**
 *
 * @author laura
 */
public class Score {
    private int puntaje;
    private String fecha;

    public Score(int val, String date) {
        this.puntaje = val;
        this.fecha = date;
    }
    public Score() {
    }
    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

  
    
    

}
