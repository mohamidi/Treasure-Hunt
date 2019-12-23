package treasure_hunt;
import gameNet.*;
import treasure_hunt.MyGame;
import treasure_hunt.MyUserInterface;

public class MyMain extends GameCreator
{   
	public GameNet_CoreGame createGame()
	{
		return new MyGame();
	}

	public static void main(String[] args) 
	{   
		MyMain myMain = new MyMain(); 
		GameNet_UserInterface  myUserInterface = new MyUserInterface();
		myMain.enterGame(myUserInterface); 
	}
}
