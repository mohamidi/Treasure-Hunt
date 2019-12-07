package treasure_hunt;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gameNet.GameNet_UserInterface;
import gameNet.GamePlayer;
// New Import for Events

@SuppressWarnings("serial")
public class MyUserInterface extends JFrame 
implements   ActionListener, GameNet_UserInterface
{     
    private MyGame myGame=null;
    private GamePlayer  myGamePlayer=null;
    private String myName="";
    private MyGameInput myGameInput = new MyGameInput();

    private JButton[][] buttonArr = new JButton[10][10];
    private JButton resetButton = new JButton("Reset");
    private JLabel messageLabel = new JLabel("");
    private JLabel nameLabel = new JLabel("");
    private AudioInputStream audioInputStream;
    private Clip clip;
    private JFrame finish = new JFrame();
	private JLabel backgroundLose = new JLabel(new ImageIcon("Webp.net-resizeimage.jpg"));
    private JLabel backgroundWin = new JLabel(new ImageIcon("Webp.net-resizeimage.png"));
    private Termination closeMonitor = new Termination();
    
    public MyUserInterface()
    {
        super("Treasure Hunt");   
    }

    public void startUserInterface (GamePlayer gamePlayer) {    
        myGamePlayer = gamePlayer; 
        myName=gamePlayer.getPlayerName();
        myGameInput.setName(myName);

        addWindowListener(this.closeMonitor);
        screenLayout();
        setVisible(true); 

        sendMessage(MyGameInput.JOIN_GAME);    
    } 

    private void sendMessage(int command)
    {
        myGameInput.command = command;
        myGamePlayer.sendMessage(myGameInput);
    }

    private void sendMessageSelection(int row, int col)
    {
        myGameInput.row = row;
        myGameInput.col =col;
        sendMessage(MyGameInput.SELECT_SQUARE);
    }

    public void actionPerformed(ActionEvent e)
    {	
        for (int row=0; row < 10; row++)
        {
            for (int col=0; col < 10; col++)
            {
                if (e.getSource() == buttonArr[row][col])
                { 	
                   	
                    if (myGame != null && myGame.checkAvailability(myName, row, col))
                    {
                        sendMessageSelection(row, col);    
                    }

                }
            }
        }
        if (e.getSource() == resetButton)
        {
            sendMessage(MyGameInput.RESETTING);  
            sendMessage(MyGameInput.RESETMUSIC);
        }
    }

    public void receivedMessage(Object ob)
    {
        MyGameOutput myGameOutput = (MyGameOutput)ob;
        myGame = myGameOutput.myGame;

        String msg= myGame.getStatus(myName);
        String turnMsg = myGame.getTurnInfo(myName);
        String extendedName =  myGame.getExtendedName(myName);
        
        nameLabel.setText(extendedName);   
        messageLabel.setText(turnMsg + " ------- " + msg); 

        for (int row=0; row < 10; row++)
        {
            for (int col=0; col < 10; col++)
            {
                String label = myGame.getButtonLabel(row, col);
                
                if(label == "WHITE")
                	buttonArr[row][col].setBackground(Color.WHITE);
                else if(label == "BLACK")
                	buttonArr[row][col].setBackground(Color.BLACK);
                else if(label == "null")
                    buttonArr[row][col].setBackground(null);
            }
        }
        
    	boolean reset = myGame.reset();      
    	
        if(reset)
        {
        	stopSound("Overwatch-Victory-Theme-POTG-Theme-8-bit-remix.wav");
        	stopSound("Game-Over-8-Bit-Music.wav");
        	playSound("88GLAM-Bali-feat-NAV-Instrumental.wav");
        }
        if ( myGame.gameInProgress())
        {
        	finish.setVisible(false);
            resetButton.setVisible(false);
        }
        else
        {
        	stopSound("88GLAM-Bali-feat-NAV-Instrumental.wav");
        	if(msg.equals("Congratulations! You Found It !!"))
        	{
        		finish.add(backgroundWin);
        		playSound("Overwatch-Victory-Theme-POTG-Theme-8-bit-remix.wav");
        	}
        	else if(msg.equals("Sorry, You Lose"))
        	{
        		finish.add(backgroundLose);
        		playSound("Game-Over-8-Bit-Music.wav");
        	}
        	finish.setVisible(true);
            resetButton.setVisible(true);
        }
    }

    private void screenLayout()
    {
        setLayout(new BorderLayout());   
        setSize(500,500);     
        
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.LIGHT_GRAY);
        centerPanel.setLayout(new GridLayout(10,10));
        
        Font myFont = new Font("Palatino", Font.BOLD, 15);
        
        for (int i=0; i < 10; i++)
        {
            for (int j=0; j < 10; j++)
            {
                JButton b =new JButton(" ");
                b.addActionListener(this);
                centerPanel.add(b);
                buttonArr[i][j] = b;
            }
        }
        
        add (centerPanel, BorderLayout.CENTER);

        JPanel northPanel = new JPanel();
        northPanel.setBackground(Color.darkGray);
        northPanel.setLayout(new GridLayout(0,1));
        
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(myFont);
        
        northPanel.add(nameLabel);

        JPanel topPanel = new JPanel();     
        topPanel.setBackground(Color.darkGray);
        topPanel.setLayout(new FlowLayout());
        
        topPanel.add(resetButton);
        resetButton.addActionListener(this);
        
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(myFont);
        
        topPanel.add(messageLabel);
        
        northPanel.add(topPanel);

        add(northPanel, BorderLayout.NORTH);   
        playSound("88GLAM-Bali-feat-NAV-Instrumental.wav");

    	finish.setLayout(new BorderLayout());
    	finish.setSize(450,250);
    	finish.setResizable(false);
    	
    	JPanel bottomFinishPanel = new JPanel();    
    	bottomFinishPanel.setLayout(new FlowLayout());
    	bottomFinishPanel.add(resetButton);
    	
    	finish.add(bottomFinishPanel, BorderLayout.SOUTH);
    }

    public boolean playSound(String fileName)
    {
    	try 
    	{
    		System.out.println("Playing: "+ fileName);
    		audioInputStream = AudioSystem.getAudioInputStream(
    				new File(fileName));
    		clip = AudioSystem.getClip();
    		clip.open(audioInputStream);
    		clip.start();
    	} 
    	catch(Exception ex) 
    	{
    		System.out.println("Error with playing sound."+ex);
    		ex.printStackTrace();
    	}
    	return true;		
    }
    
    public void stopSound(String fileName)
    {
    	clip.stop();
    }
        
    //*******************************************************
    // Inner Class
    //*******************************************************

    class Termination extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            sendMessage(MyGameInput.DISCONNECTING);
            myGamePlayer.doneWithGame();
            System.exit(0);
        }
    }
}

