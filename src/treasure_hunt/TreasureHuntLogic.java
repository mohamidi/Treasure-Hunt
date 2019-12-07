package treasure_hunt;
import java.io.Serializable;
import java.util.Random;

public class TreasureHuntLogic implements Serializable{
    
    private static final long serialVersionUID = 1L;

        // The game field 10 x 10 will be filled with -1 (AVAILABLE) to start with
        // There after, entries will receive either a the player's index (0 or 1). 
        private static final int AVAILABLE = -1;

        // GameControl Status values
        private static final int GAME_TIE = 2;
        private static final int GAME_IN_PROGRESS=-1;

        // Note that gameWinner will take on the values of:
        // GAME_TIE
        // 0 if the first player is the Winner
        // 1 if the second player is the Winner
        private int gameWinner = GAME_IN_PROGRESS;
        
        private int[][] board = new int[10][10]; 

        private int nextTurn = 0;
        private int startingTurn = 1;
        
        // Random game elements
        private Random random = new Random() ;
        private int randRow = random.nextInt(10);
        private int randCol = random.nextInt(10);
        private int randCol1 = random.nextInt(10);
        private int randRow1 = random.nextInt(10);
        private int randCol2 = random.nextInt(10);
        private int randRow2 = random.nextInt(10);
        private int randCol3 = random.nextInt(10);
        private int randRow3 = random.nextInt(10);

        // Used to allow the User interface to fill in appropriate label for each cell
        public String getButtonLabel(int row, int col)
        {
            int value = board[row][col];     

            if (value == AVAILABLE) 
                return "null";
            if (value == 0)
            {
                return "BLACK";  
            }
            else
            {
            	return "WHITE";
            }      
        }
        
        // Used to find out if the game is complete
        public boolean gameInProgress()
        {
            if (gameWinner == GAME_IN_PROGRESS)
                return true;
            else
                return false;
        }
        
        // Used to figure out if the player has won, lost or tied
        public String getGameStatus(int yourIndex)
        {
            if (yourIndex == gameWinner)
                return "Congratulations! You Found It !!";
            if (gameWinner >= 0 && gameWinner <=1)
                return "Sorry, You Lose";
            if (gameWinner == GAME_TIE)
                return "It's a Tie";
            return "Game in Progress";
        }
        
        // New Game
        void clearBoard()
        {
            gameWinner = GAME_IN_PROGRESS;
            // Alternate who starts if multiple games are played
            if (startingTurn != 0)
                startingTurn = 0;
            else
                startingTurn = 1;

            nextTurn = startingTurn;

            for (int i=0; i < 10; i++)
            {
                for (int j=0; j < 10; j ++)
                {
                    board[i][j] = AVAILABLE;
                    getButtonLabel(i,j);
                }
            }

            // Choosing new random numbers after game reset
            randRow = random.nextInt(10);
            randCol = random.nextInt(10);
            randCol1 = random.nextInt(10);
            randRow1 = random.nextInt(10);
            randCol2 = random.nextInt(10);
            randRow2 = random.nextInt(10);
            randCol3 = random.nextInt(10);
            randRow3 = random.nextInt(10);
        }
        
        // Find out if it is your turn
        public boolean isTurn(int clientIndex)
        {
            if (clientIndex == nextTurn)
                return true;
            else 
                return false;
        }
        
        // Can you select this button
        public boolean checkAvailability(int clientIndex, int row,  int col)
        {
            if (!isTurn(clientIndex))
            {
                return false;
            }
            if (row < 0 || row > 9 || col < 0 || col > 9)
            {
                return false;
            }
            if (board[row][col] == AVAILABLE)
                return true;
            else
                return false;
            
        }
        
        // makeSelection allows a player to make a selection if possible.
        // Selection not allowed if it is a bad selection.
        // If it is an OK selection, then the "Turn" is switched and the 
        // gameWinner status is updated
        
        public boolean makeSelection(int clientIndex, int row, int col)
        {

            if (!isTurn(clientIndex))
            {
                return false;
            }
            if (row < 0 || row > 9 || col < 0 || col > 9)
            {
                return false;
            }
            if (board[row][col] != AVAILABLE)
            {
                return false;
            }
            // an OK selection
            board[row][col]= nextTurn;
            
            // change the turn
            nextTurn = (nextTurn +1) % 2;
            
            // update the game status
            gameWinner = scoreGame();
            return true;
        }        

        // Returns the index of whoever lands on the treasure
        public int scoreGame()
        {   	
	        int treasure = board[randRow][randCol];
	        int treasure1 = board[randRow1][randCol1];
	        int treasure2 = board[randRow2][randCol2];
	        int treasure3 = board[randRow3][randCol3];

	        for(int i=0; i < 10; i++)
	        {
		        for (int j=0; j < 10; j++)
		        {        	
		        	int index = board[i][j];        	
		        	
		        	if (index == treasure && index != AVAILABLE || 
		        			index == treasure1 && index != AVAILABLE ||
		        					index == treasure2 && index != AVAILABLE ||
		        							index == treasure3 && index != AVAILABLE)
		        	{
		        		return index;
		        	}
		        }
	        }	
        	return GAME_IN_PROGRESS;
        }
}