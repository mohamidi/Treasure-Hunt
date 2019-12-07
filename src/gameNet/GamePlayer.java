package gameNet;

import java.io.*;
import java.net.*;



/**
 * 
 * 
 *
 * The <b>GamePlayer</b> class manages the socket connection with the GameControl.
 * <p>
 * When the GUI wants to send a message to the GameControl, the <b>sendMessage</b> 
 * routine is called to write the message object to an ObjectOutputStream.
 * <p>
 * When the GameControl wants to send a message to the GUI,  the 
 * GamePlayer Thread will read the object from the ObjectInputStream. 
 * The GamePlayer class passes it along to the GUI by calling the 
 * <b>receivedMessage</b> method. 
 * 
 */

@SuppressWarnings("serial")
public class GamePlayer  extends Thread
    implements Serializable
{
    GameNet_ConnectionBrokenInterface gameConnectionBrokenObj = null;
    private String playerName;
    private GameControl gameControl=null;
    
    Socket gameSocket = null;
    ObjectInputStream socketInput=null;    
    ObjectOutputStream socketOutput = null;
	GameNet_UserInterface userInterface=null;

    boolean socketAlive = true;
   

    /**
     * The GamePlayer constructor needs the name of the player
     * and the GameControl class to connect to. 
     * 
     */
    GamePlayer(String playerName, GameControl game, 
    		GameNet_UserInterface r)
    {
        this.playerName = playerName;
        this.gameControl = game;
        userInterface = r;
        
        
        // There could be a little bit of a race condition
        // in the next 2 statements ... can be solved with a little
        // more logic. 
        
        joinGame();  
    }
    /**
     * getPlayerName returns the name of the player 
     * associated with this connection. 
     * 
     */
    public String getPlayerName()
    {
    	return playerName;
    }
    
    /**
     * If you want your GUI to find out when a GameControl connection
     * is broken, your GUI must implement the Interface_ConnectionBroken
     * interface.  Use setGameConnectionBroken to let GamePlayer know
     * about this interface.  
     * @param gcb - an instance of the Interface_ConnectionBroken interface 
     * to be called when the Socket disconnects. 
     */
    public void setGameConnectionBroken(GameNet_ConnectionBrokenInterface gcb)
    {
        gameConnectionBrokenObj = gcb;
    }
    
    /**     
     * The Thread that is underneath the GamePlayer
     * class is responsible for reading the 
     * ObjectOutputStream from the GameControl.   Every MyGameOutput
     * read from the ObjectOutputStream is passed on
     * by calling the receivedMessage routine.  Your Networked Game should not be 
     * accessing the run routine directly.  It has to be public to properly run
     * as a thread. 
     */  
  public void run()
  {
     Object outputFromSocket;
     try{
     // Read from Socket and write to Screen
        while ((outputFromSocket = socketInput.readObject()) != null) // Read from Socket
        {   
            receivedMessage(outputFromSocket);
        }
     } 
     catch (ClassNotFoundException e)
     { 
            System.out.println("GamePlayer.run Class Not Found Exception: " + e);
            e.printStackTrace(System.out);
     }
     catch (IOException e)
     {
            System.out.println("GamePlayer.run Exception: " +e);
     }
     // It's easier for the socket reader to detect that the socket
     // is gone.  We need to set a flag so that the socket writer
     // will know that it's time to give up.  
     
     socketAlive = false;
     if (gameConnectionBrokenObj != null)
        gameConnectionBrokenObj.gameConnectionBroken();
     System.out.println("GamePlayer.run Thread terminating ");

  }
  
  /**
   * GameIsAlive can be called to find out if the GamePlayer socket is
   * still connected. 
   * @return  true if socket is still alive
   */
  public boolean GameIsAlive()
  {
    return socketAlive;
  }

  /**  
   * joinGame will open a socket to the GameControl and 
   * construct an ObjectOutputStream and ObjectInputStream from the socket. 
    
   */
   void joinGame()
    {
        ObjectOutputStream tempSocketOutput = null;
        if (gameControl==null)
        	throw new RuntimeException ("joinGame called on a null gameControl");

        try 
        {
            gameSocket = new Socket(
            		gameControl.getIpAddress(), 
                    gameControl.getPortNum());
            if (gameSocket == null) 
                throw new RuntimeException("joinGame gameSocket null error");
                      
            // Create in/out classes associated with the Open Socket
            tempSocketOutput = new ObjectOutputStream(gameSocket.getOutputStream());
            
            socketInput = new ObjectInputStream( gameSocket.getInputStream());
            if (socketInput == null) 
                throw new RuntimeException("joinGame socketInput null error");

            
                        
            // Put in a pause to allow some time to get the server
            // side thread up.
            // It turns out that a sendMessage is likely to be sent
            // immediately when this call returns.
           
            try
            {   
                Thread.sleep(500);// Sleep for 1/2 second
            }catch (InterruptedException e){}   
            
            // Start up a Thread to read from the socket and write 
            //   the contents to the screen
            
            this.start();
            try
            {   
                Thread.sleep(500);// Sleep for 1/2 second
            }catch (InterruptedException e){}   
         
        }
        catch (UnknownHostException e) 
        {
            System.out.println("GamePlayer.joinGame Cant find host: "+ e);
        } 
        catch (IOException e) 
        {
            System.out.println("GamePlayer.joinGame IOException: " +e);
            e.printStackTrace(System.out);
        }    
        socketOutput = tempSocketOutput;  

    }
    
   /**
    * exitGame is called when you want to terminate the connection to the GameControl
    *
    */
    void exitGame()
    {
        exitGame(null);
    }
    /**
     * Call this version of exitGame if you want to pass one last object 
     * to the gameControl and then terminate the connection to the GameControl. 
     * @param ob Object to pass to the GameControl before terminating
     */
    void exitGame(Object ob)
    {
        if (ob != null) sendMessage(ob);
        
        System.out.println("GamePlayer.exitGame " + playerName);
        try{
                if (socketOutput != null) socketOutput.close(); // Close output stream side of the socket
                if (socketInput != null)socketInput.close();  // Close input stream side of socket
                if (gameSocket != null)gameSocket.close();  // Close the socket
            }
        catch (IOException e){}
    } 
    
    /*
     * Some useful routines contained in GamePlayer
    void sendMessage(MyGameInput); // Send to GameControl
    boolean GameIsAlive(); //Tests if socket is alive
    */
    public void doneWithGame()
    {
    	exitGame(); // Our GamePlayer object disconnects from the gameControl
        gameControl.endGame(); // If we own the server, it will shutdown 
    }
    
    /**
     * sendMessage provides a way to send an object to the GameControl. 
     * @param objectToSend to pass to the GameControl. 
     */
    public void sendMessage(Object objectToSend)
    {
     try{
            if (socketOutput != null)
            {
                socketOutput.writeObject(objectToSend); // Write to socket
                socketOutput.reset();
            }
         }
      catch (IOException e)
      {
           System.out.println("GamePlayer.sendMessage Exception: " + e);
           e.printStackTrace(System.out);
      }
    } 
    
    /**
     * To use GamePlayer, you must create a class which extends GamePlayer and
     * overrides receivedMessage.  The receivedMessage is the routine that is 
     * called to receive any messages from the GameControl. 
     * @param objectReceived  Object received from the GameControl
     */
    
    protected void receivedMessage(Object objectReceived)
    {
    	userInterface.receivedMessage(objectReceived);       
    }
    
}








    
    
