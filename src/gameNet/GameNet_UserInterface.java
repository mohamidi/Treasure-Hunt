package gameNet;



/**
 * Your GUI MUST implement this interface. 
 */
public interface GameNet_UserInterface
{
	/**
	 * When the "process" method returns a non-null MyGameOutput, the information will then be sent to the
	 * receivedMessage methods of all of the user interfaces. 
	 * @param objectReceived - information received 
	 */
	public void receivedMessage(Object objectReceived);
	/**
	 * When the Networked game has been put together, the user interface is 
	 * informed with a call to startUserInterface.  In this call the GamePlayer class is passed in.  
	 * The GamePlayer address should be saved since it provides the mechanism for sending data to the game. 
	 * @param gamePlayer - contains the players name, and the glue information needed to connect the user 
	 * interface to the game.  
	 */
	public void startUserInterface (GamePlayer gamePlayer);
}