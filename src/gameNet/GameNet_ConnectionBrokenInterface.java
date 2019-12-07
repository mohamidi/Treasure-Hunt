package gameNet;

/**
 * Your GUI might want to implement this interface if wants to know when the 
 * socket connection is broken. Since this is optional, to register the 
 * desire for this information, call the GamePlayer method:
 * setGameConnectionBroken(Interface_ConnectionBroken gcb).  Whenever, a socket connection is
 * broken a call will be made on the gameConnectionBroken method. 
 */
public interface GameNet_ConnectionBrokenInterface
{
    public void gameConnectionBroken();
}