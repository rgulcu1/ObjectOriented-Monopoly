
import java.util.ArrayList;
import java.util.Scanner;



public class Game {
	public enum intention{
		ADD,REMOVE;
	}
	
    private String playerName;
    private ArrayList<Player> players = new ArrayList<Player>();
    private Scanner scan =new Scanner(System.in);
    //DefaultMap md = new DefaultMap();
 
    private Map board = new Map();
    

    public Game(String playerName, int numOfPlayer) {
        createComputer(numOfPlayer);
        players.add(createPlayer(playerName));
        
    }


    private Player createPlayer(String name) {
        playerName=name;
        return new Player(name);
    }

    private void createComputer(int numberOfPlayer) {
        String[] names = {"Frodo", "Gandalf", "Gollum", "Sauron", "Saruman"};


        for (int i = 0; i < numberOfPlayer-1; i++) {
            players.add(new Player(names[i]));
        }

    }

    private int[] rollDices(){
        return new int[] {(int) (Math.random() * 6 + 1), (int) (Math.random() * 6 + 1)};
    }

    private void move(Player player ,int diceNumber) {
        int prevPosition = player.getCurrentPosition(); //keep previous position
        player.setCurrentPosition((player.getCurrentPosition() +diceNumber) % 40);  //add the dice curren position
        if (player.getCurrentPosition() < prevPosition){
            System.out.print(player.getName()+"passed start point.Received 2000$");
            player.addBalance(2000); //if player pass the start point earn tour money
        }

        SquareEvent(player);
    }

    private void SquareEvent(Player player){
    	int position=player.getCurrentPosition();
    	Square tempSquare = board.getSquare(position);
    	tempSquare.Speak(player);
        if(tempSquare instanceof Purchasable){  // karenın satın alınabilir olup olmadığına bakıyoruz
        	if ( !(((Purchasable) tempSquare).isSold()) ){ //daha önce satılıp satılmadığını kontrol ediyoruz
                buySaleable(player,(Purchasable)tempSquare);
            }else{
                doPayment(player,(Purchasable)tempSquare); //daha önce alınmışsa kira ödemesi yapıyoruz
            }
        }else{
            ((UnSalable)tempSquare).event(player);
        }
    }

    
    private void breakColorSet(Purchasable purchasable) {
    	ArrayList<Purchasable> tempSquares = new ArrayList<Purchasable>(purchasable.getOwner().getPropertys());
    	if( purchasable instanceof Property ) {
    		if( purchasable.getOwner().hasAllColors((Property)purchasable)  ) {
        		for(int i=0;i<tempSquares.size();i++) {
        			if( tempSquares.get(i).getColor().equals( purchasable.getColor() )  ) {
        				purchasable.getOwner().getPropertys().get(i).setRentPrice(tempSquares.get(i).getRentPrice()/2);
        			}
        		}
        	}
    	}
    }
    
    private void arrangePurchasableRents(Purchasable purchasable,intention intention) {
    	ArrayList<Purchasable> tempSquares = new ArrayList<Purchasable>(purchasable.getOwner().getPropertys());
    	double increaseRate;
    	if( intention.ordinal()==0) {
    		increaseRate=2.0;
    	}else {
    		increaseRate=0.5;
    	}
    	if( purchasable instanceof Property ) {
    		if( purchasable.getOwner().hasAllColors((Property)purchasable)  ) {
        		for(int i=0;i<tempSquares.size();i++) {
        			if( tempSquares.get(i).getColor().equals( purchasable.getColor() )  ) {
        				purchasable.getOwner().getPropertys().get(i).setRentPrice((int)(tempSquares.get(i).getRentPrice()*increaseRate));
        			}
        		}
        	}
    	}else if( purchasable instanceof Transportation ) {
    		int howMany=purchasable.getOwner().howManyTransportation();
    		for(int i=0;i<tempSquares.size();i++) {
    			if( tempSquares.get(i) instanceof Transportation ) {
    				purchasable.getOwner().getPropertys().get(i).setRentPrice( 250*howMany);
    			}
    		}
    	}else {
    		if(purchasable.getOwner().hasAllFirm()) {
    			for(int i=0;i<tempSquares.size();i++) {
    				if( tempSquares.get(i) instanceof Firm ) {
    					purchasable.getOwner().getPropertys().get(i).setRentPrice( 100 );
    				}
    			}
    		}
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
            players.remove(players.size()-1);
            maxDice=dices[0]+dices[1];
        }

        System.out.println(players.get(0).getName()+" will be begin!");
    }

    private void RoundLastPlayer(){
        int dices[];
        int totalDice;
        for (int i = 0; i <players.size()-1 ; i++) {
            dices=rollDices();
            totalDice=dices[0]+dices[1];
            System.out.print(players.get(i).getName()+" is roll dice :"+dices[0]+","+dices[1]+". Move "+totalDice+" blocks.");
            move(players.get(i),totalDice);
            System.out.println(players.get(i).getName()+"'s Balance: $"+players.get(i).getBalance());
            players.get(i).speak();

        }

        System.out.println("Press any button to roll a dice");
        scan.next();
        dices=rollDices();
        totalDice=dices[0]+dices[1];
        System.out.print(players.get(players.size()-1).getName()+" is roll dice :"+dices[0]+","+dices[1]+". Move "+totalDice+" blocks.");
        move(players.get(players.size()-1),totalDice);
        System.out.println(players.get(players.size()-1).getName()+"'s Balance: $"+players.get(players.size()-1).getBalance());
        players.get(players.size()-1).speak();

    }

    private void RoundFirstPlayer(){
        int dices[];
        int totalDice;

        System.out.println("Press any button to roll a dice");
        scan.next();
        dices=rollDices();
        totalDice=dices[0]+dices[1];
        System.out.print(players.get(0).getName()+" is roll dice :"+dices[0]+","+dices[1]+". Move "+totalDice+" blocks.");
        move(players.get(0),totalDice);
        System.out.println(players.get(0).getName()+"'s Balance: $"+players.get(0).getBalance());
        players.get(0).speak();

        for (int i = 1; i <players.size() ; i++) {
            dices=rollDices();
            totalDice=dices[0]+dices[1];
            System.out.print(players.get(i).getName()+" is roll dice :"+dices[0]+","+dices[1]+". Move "+totalDice+" blocks.");
            move(players.get(i),totalDice);
            System.out.println(players.get(i).getName()+"'s Balance: $"+players.get(i).getBalance());
            players.get(i).speak();
        }
    }

    private void buySaleable(Player player,Purchasable saleable){
        String choice="";
        if(player.getBalance()>=saleable.getPurchasePrice()){
            if(player.getName().equals(playerName)){
                System.out.println("Dou u want to "+saleable.getName()+"("+saleable.getPurchasePrice()+"$)"+"(y/n)");
                 choice=scan.next();
            }
        if(!choice.equals("n")){
            player.reduceBalance(saleable.getPurchasePrice());
            player.addProperty(saleable);
            saleable.setOwner(player);
            saleable.setSold(true);
            System.out.print(player.getName()+" is purchase "+saleable.getName()+ "("+saleable.getPurchasePrice()+ "$)"+ ".");
            intention intent = intention.ADD;
            arrangePurchasableRents(saleable,intent);
        }
        }else{
            System.out.println(player.getName()+"cant buy "+saleable.getName()+" Its expensive.");
        }

    }

    private void sellSaleable(Player player,Purchasable saleable){
    	arrangePurchasableRents(saleable,intention.REMOVE);
        player.addBalance(saleable.getHypothecPrice());
        player.removeProperty(saleable);
        saleable.setSold(false);
    }

    private void doPayment(Player player,Purchasable saleable){
        player.reduceBalance(saleable.getRentPrice());
        saleable.getOwner().addBalance(saleable.getRentPrice());
    }

    private void checkBalances(){
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).getBalance()<0){
                hypoteseSaleables(players.get(i));
            }
        }
    }

    private void hypoteseSaleables(Player player){
        while(player.getBalance()<0){
            if(player.getPropertys().size()>0){
                if(!player.getName().equals(playerName)){
                    sellSaleable(player,player.getPropertys().get(0));
                }else{
                    System.out.println("which property you want to sell ?");
                    for (int i = 0; i <player.getPropertys().size() ; i++) {
                        System.out.println(i+"-"+player.getPropertys().get(i).getName()+"("+player.getPropertys().get(i).getHypothecPrice()+"$)");
                    }
                    sellSaleable(player,player.getPropertys().get(scan.nextInt()));
                }

            }else{
                System.out.println(player.getName()+ " go bankrupt.");
                players.remove(player);
                break;
            }

        }
    }


    public void run(int roundNumber){
        boolean checkPlayer=false;
        int currentRound=0;
        if(players.size()>1) decidePlayerOrder();
        if(players.get(0).getName().equals(playerName)) checkPlayer=true;

        while(currentRound<=roundNumber){
            checkBalances();
            if(checkPlayer){
                RoundFirstPlayer();
            }
            else{
                RoundLastPlayer();
            }


            currentRound++;
            System.out.println("-------------------------------");
        }

    }

}
