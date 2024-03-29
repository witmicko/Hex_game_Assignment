package controllers;

import play.*;
import play.db.jpa.Blob;
import play.mvc.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.*;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import models.*;
import play.*;


import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        Hex hexGame = new Hex(11, 11);
        hexGame.save();
        render(hexGame);
    }

    public static void newGame() {
        Hex hexGame = new Hex(11, 11);
        hexGame.save();
        renderTemplate("Application/index.html", hexGame);
    }

    public static void takeTurn(int x, int y, long id) {
        Hex hexGame = Hex.findById(id);
        hexGame.takeTurn(x, y);
        hexGame._save();
        if(hexGame.getCurrentPlayer()==2){
            if(hexGame.isWinner()){
                hexGame._delete();
                index();
            }
            x = (int)(Math.random()*11);
            y = (int)(Math.random()*11);
            takeTurn(x,y,id);
        }
        hexGame.save();
        renderTemplate("Application/index.html", hexGame);
    }

    public static void removeAll(){
        List<Hex>list = Hex.findAll();
        for(Hex x:list){
            System.out.println(x.id);
            x.delete();
        }
        index();
    }



}