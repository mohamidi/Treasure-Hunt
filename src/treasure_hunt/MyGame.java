package treasure_hunt;
import java.io.Serializable;
import java.util.ArrayList;

import gameNet.GameNet_CoreGame;

public class MyGame extends GameNet_CoreGame implements Serializable
{
    private static final long serialVersionUID = 1L;
    private ArrayList<String> currPlayers = new ArrayList<String>();
    private TreasureHuntLogic treasureLogic = new TreasureHuntLogic();
    private boolean reset;

    public MyGame()
    {
    	treasureLogic.clearBoard();
    }
    
    // Process commands from each of the game players
    public Object process(Object inputs)
    {
        MyGameInput myGameInput = (MyGameInput)inputs;
        
        // Note that this routine will add the player if he isn't currently in the 
        // game and there is room (i.e. < 2 players)
        int clientIndex = getClientIndex(myGameInput.myName);
        
        if (clientIndex < 0)
        {
            System.out.println("Already have 2 players");
            return null; // Ignore input
        }
        reset = false;
        switch (myGameInput.command)
        {
        case MyGameInput.JOIN_GAME:
            break;
        case MyGameInput.SELECT_SQUARE:
            treasureLogic.makeSelection(clientIndex, myGameInput.row, myGameInput.col);
            break;
        case MyGameInput.DISCONNECTING:
            currPlayers.remove(myGameInput.myName);        
            break;
        case MyGameInput.RESETTING:
            treasureLogic.clearBoard();
            break;
        case MyGameInput.RESETMUSIC:
        	reset = true;
        	break;      	
        default: /* ignore */
        }
        
        // Send game back to all clients
        MyGameOutput myGameOutput = new MyGameOutput(this);
        return myGameOutput;
    }
    
    // Get the proper label for each button in the 10x10 grid
    public String getButtonLabel(int row, int col)
    {
        return treasureLogic.getButtonLabel(row, col);
    }
    
    // returns your name with either " -- BLACK" or " -- WHITE" appended
    public String getExtendedName(String myName)
    {
         String myMarker =(( getYourIndex(myName)==0)? "BLACK" : "WHITE" );
         return myName + " --  " + myMarker;
    }
    
    // Returns True if the Game is still going
    public boolean gameInProgress()
    {
        return treasureLogic.gameInProgress();
    }
    
    // Returns True if the music is reset
    public boolean reset()
    {
    	return reset;
    }
    
    // Returns whether you won, lost, or the Game is in progress
    public String getStatus(String myName)
    {
        int index = getYourIndex(myName);
        return treasureLogic.getGameStatus(index);
    }
    
    // returns whether you can select a given 10x10 grid based on the current turn.
    public boolean checkAvailability(String myName, int row, int col)
    {
        int index = getYourIndex(myName);
        return treasureLogic.checkAvailability(index,  row,  col);
    }
    
    // Returns Information about whose turn it is
    public String getTurnInfo(String myName)
    {
        if (!gameInProgress())
            return " Game Over ";
        
        int index = getYourIndex(myName);
        if (treasureLogic.isTurn(index))
            return "Your Turn";
        String otherClient = otherPlayerName(myName);
        return otherClient+"'s Turn";
    }

    // If you have already connected, then this will return your index (0 or 1). 
    // If you are new and we currently have less than 2 players then you are added
    // to the game and your index is returned (0 or 1)
    // If we already have 2 players, then this will return -1
    private int getClientIndex(String name)
    {
        // The following will return -1 if the name can't be found
        int retval = currPlayers.indexOf(name);
        
        if (retval < 0 && currPlayers.size() < 2)
        {
            retval = currPlayers.size();
            currPlayers.add(name);
            if (currPlayers.size() == 2)
            {
                // Game ready to go.
            }
        }
        return retval;
    }
    
    // If you are already in the game, your index will be returned (0 or 1)
    // Otherwise -1 is returned ... you are never added with this routine.
   
    
    // This returns the other Player's name if he exists.  A null is returned if he doesn't exist.
    private String otherPlayerName(String yourName)
    {
        if (currPlayers.size() < 2)
            return null;
        if (yourName.equals(currPlayers.get(0)))
            return currPlayers.get(1);
        else
            return currPlayers.get(0);
    }
    
    private int getYourIndex(String name)
    {
        return currPlayers.indexOf(name);
    }
}
