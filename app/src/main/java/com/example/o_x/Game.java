package com.example.o_x;

public class Game {

   String player1Id, player2Id,player1sign,player2sign;

   public Game(){
       
   }
   public Game(String player1Id,String player2Id,String player1sign,String player2sign){
       this.player1Id = player1Id;
       this.player2Id = player2Id;
       this.player1sign = player1sign;
       this.player2sign = player2sign;
   }

    public String getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(String player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer1sign() {
        return player1sign;
    }
    
    public void setPlayer1sign(String player1sign) {
        this.player1sign = player1sign;
    }

    public String getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(String player2Id) {
        this.player2Id = player2Id;
    }

    public String getPlayer2sign() {
        return player2sign;
    }

    public void setPlayer2sign(String player2sign) {
        this.player2sign = player2sign;
    }
}
