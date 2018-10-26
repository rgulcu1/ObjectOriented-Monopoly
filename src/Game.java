import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {
    private ArrayList<Player> players = new ArrayList<Player>();
    Scanner scan =new Scanner(System.in);

    public Game(String playerName, int numOfPlayer) {
        createComputer(numOfPlayer);
        players.add(createPlayer(playerName));
    }


    private Player createPlayer(String name) {
        return new Player(name);
    }

    private void createComputer(int numberOfPlayer) {
        String[] names = {"Frodo", "Gandalf", "Gollum", "Sauron", "Saruman"};


        for (int i = 0; i < numberOfPlayer-1; i++) {
            players.add(new Player(names[i]));
        }

    }

    private int[] rollDices(){
        int[] dices={(int)(Math.random()*6+1),(int)(Math.random()*6+1)};
        return dices;
    }

    private void move(Player player ,int diceNumber) {
        int prevPosition = player.getCurrentPosition(); //keep previous position
        player.setCurrentPosition((player.getCurrentPosition() +diceNumber) % 40);  //add the dice curren position
        if (player.getCurrentPosition() < prevPosition) player.addBalance(2000); //if player pass the start point earn tour money
        if (player.getCurrentPosition() == 30) { //if player move to jail zone
            player.setPrison(true);
            player.setCurrentPosition(10);
        }


    }

    //teleport user when take luck cards
    private void teleport(Player player,int position) {
        player.setCurrentPosition(position);
    }

    private void decidePlayerOrder(){
        int[] dices;
        int maxDice=0;
        System.out.print("Let's play! These are dice of other players:  ");
        for (int i = 0; i <players.size()-1 ; i++) {
            dices=rollDices();
            System.out.print(players.get(i).getName()+":"+dices[0]+","+dices[1]+"  ");
            if(dices[0]+dices[1]>maxDice){
                players.add(0,players.get(i));
                players.remove(i+1);
                maxDice=dices[0]+dices[1];
            }
        }
        System.out.println();
        System.out.println("Press any button to roll a dice");
        scan.next();
        dices=rollDices();
        System.out.println(players.get(players.size()-1).getName()+":"+dices[0]+","+dices[1]+"  ");

        if(dices[0]+dices[1]>maxDice){
            players.add(0,players.get(players.size()-1));
            players.remove(players.size());
            maxDice=dices[0]+dices[1];
        }

        System.out.println(players.get(0).getName()+" will be begin!");
    }

    public void run(int roundNumber){
        int currentRound=0;
        int dices[];
        int totalDice;
        decidePlayerOrder();
        while(currentRound<=roundNumber){
            for (int i = 0; i <players.size()-1 ; i++) {
                dices=rollDices();
                totalDice=dices[0]+dices[1];
                System.out.print(players.get(i).getName()+" is roll dice :"+dices[0]+","+dices[1]+". Move "+totalDice+" blocks.");
                move(players.get(i),totalDice);
                System.out.println("He is in "+players.get(i).getCurrentPosition()+" now.");
            }

            System.out.println("Press any button to roll a dice");
            scan.next();
            dices=rollDices();
            totalDice=dices[0]+dices[1];
            System.out.print(players.get(players.size()-1).getName()+" is roll dice :"+dices[0]+","+dices[1]+". Move "+totalDice+" blocks.");
            move(players.get(players.size()-1),totalDice);
            System.out.println("He is in "+players.get(players.size()-1).getCurrentPosition()+" now.");


            currentRound++;
            System.out.println("-------------------------------");
        }

    }
}