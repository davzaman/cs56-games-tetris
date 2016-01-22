package edu.ucsb.cs56.projects.games.tetris;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import sun.audio.*;
import java.io.*;
import java.net.URL;
//import java.applet;


import java.util.Arrays;
import java.util.ArrayList;
import java.awt.Point;

/**
 * @(#)TetrisBoard.java
 *
 *
 * @author
 * @version 1.00 2011/5/8
 */


public class TetrisBoard extends JPanel implements ActionListener {

	private JButton RestartButton;
	private JButton EasyButton;
	private JButton MediumButton;
	private JButton HardButton;

	private JButton RulesButton;
	private JButton PauseButton;
	private JButton MusicButton;
	private JButton StartButton;
	private JButton UnpauseButton;
	private int TIMER_DELAY = 400;
	private JTextArea textArea;

	static JFrame window;
	static JFrame startFrame;
	static JPanel RulePanel;
	static JPanel startPanel;
	static JPanel tetrisPanel;

    Block BlockInControl;
    Color BlockColor;
    int whichType;
    static JLabel statusBar;
    int score = 0;

    private final int MAX_COL = 10;
    private final int MAX_ROW = 24;
    private int[][] board = new int[MAX_ROW][MAX_COL];
    private int[][] color = new int[MAX_ROW][MAX_COL];
    
    Timer timer;
    int timerdelay;
    boolean isFallingFinished = false;
    boolean isStarted = false;
    boolean isPaused = false;
    
    int BlockPosX,BlockPosY;

    private static int WINDOW_X = 335;
    private static int WINDOW_Y = 535;
    
    public TetrisBoard() {
    	for(int row = 0; row < MAX_ROW; row++){
	    for(int col = 0; col<MAX_COL; col++){
		board[row][col] = 0;
		color[row][col] = 0;
	    }
    	}
		this.setFocusable(true);

	MainMenu();

	InGameButtons();

    }

    public void restartGame() {
    	

    	if(statusBar.getText().equals("GAME OVER")) {
    		statusBar.setText("	Restarting Game ...	");
    		score = 0;
    		RestartButton.setText("Restarting...");
    		timer.setDelay(TIMER_DELAY);
    		timer.start();
    		RestartButton.setText("Restart");

    		statusBar.setText("SCORE = 1");

    	}
    	else {
    		
    		if(isPaused) pause();
    		statusBar.setText("	Restarting Game ...	");
    		score = 0;
    		RestartButton.setText("Restarting...");

	    	for(int row = 0; row < MAX_ROW; row++){
	    		for(int col = 0; col<MAX_COL; col++){
	    			board[row][col] = 0;
				color[row][col] = 0;
	    		}
	    	}

	    }

    	
    	
    }

    public void MainMenu() {

    	startFrame = new JFrame();
    	startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	startPanel = new JPanel();

    	startPanel.setBackground(Color.LIGHT_GRAY);
	    startPanel.setLayout(new GridLayout(4,1,0,10));

    	//declare start button
	    StartButton = new JButton();
	    StartButton.setPreferredSize(new Dimension (80, 20));
	    StartButton.setText("Play Tetris");
	    StartButton.addActionListener(new MainMenuButtons());
	    startPanel.add(StartButton);

	    EasyButton = new JButton();
	    EasyButton.setPreferredSize(new Dimension(80,20));
	    EasyButton.setText("Easy");
	    EasyButton.addActionListener(new MainMenuButtons());
	    EasyButton.setVisible(false);
	    startPanel.add(EasyButton);

	    MediumButton = new JButton();
	    MediumButton.setPreferredSize(new Dimension(80,20));
	    MediumButton.setText("Medium");
	    MediumButton.addActionListener(new MainMenuButtons());
	    MediumButton.setVisible(false);
	    startPanel.add(MediumButton);

	    HardButton = new JButton();
	    HardButton.setPreferredSize(new Dimension(80,20));
	    HardButton.setText("Hard");
	    HardButton.addActionListener(new MainMenuButtons());
	    HardButton.setVisible(false);
	    startPanel.add(HardButton);


	    startFrame.getContentPane().add(startPanel);

	    //start screen attributes
	    startFrame.setSize(WINDOW_X, WINDOW_Y);
	    startFrame.setVisible(true);
    	
    }

    private class MainMenuButtons implements ActionListener{

    	public void actionPerformed(ActionEvent e) {
    		if(e.getSource() == StartButton) {
    			StartButton.setVisible(false);
    			EasyButton.setVisible(true);
    			MediumButton.setVisible(true);
    			HardButton.setVisible(true);
    		}
    		else if(e.getSource() == EasyButton)
		   	{

		   		TIMER_DELAY = 1000;

    			startFrame.setVisible(false);
    			startPanel.setVisible(false);
    			window.setVisible(true);
    			window.setSize(WINDOW_X, WINDOW_Y);
    			beginGame();
		   	} 
		   	else if(e.getSource() == MediumButton)
		   	{
		   		TIMER_DELAY = 400;
    			startFrame.setVisible(false);
    			startPanel.setVisible(false);
    			window.setVisible(true);
    			window.setSize(WINDOW_X, WINDOW_Y);
    			beginGame();
    		}
    		else if(e.getSource() == HardButton)
		   	{

		   		TIMER_DELAY = 80;

    			startFrame.setVisible(false);
    			startPanel.setVisible(false);
    			window.setVisible(true);
    			window.setSize(WINDOW_X, WINDOW_Y);
    			beginGame();

		   	}  
    	}


    }



    public void InGameButtons() {

	   RulePanel =  new JPanel();
	   
	   RulePanel.setBackground(Color.LIGHT_GRAY);
	   RulePanel.setLayout(new GridLayout(5,1,0,10));
	   
	   RestartButton = new JButton();
	   RestartButton.setFocusable(false);
	   RestartButton.setSize(1,1);
	   RestartButton.setText("Restart");
	   RestartButton.addActionListener(new SideButtons());
	   RulePanel.add(RestartButton);

	   PauseButton = new JButton();
	   PauseButton.setFocusable(false);
	   PauseButton.setPreferredSize(new Dimension(40,40));
	   PauseButton.setText("Pause");
	   PauseButton.addActionListener(new SideButtons());
	   RulePanel.add(PauseButton);

	   RulesButton = new JButton();
	   RulesButton.setFocusable(false);
	   RulesButton.setPreferredSize(new Dimension(40,40));
	   RulesButton.setText("Rules\n");
	   RulesButton.addActionListener(new SideButtons());
	   RulePanel.add(RulesButton);

	   UnpauseButton = new JButton();
	   UnpauseButton.setFocusable(false);
	   UnpauseButton.setText("Resume Game");
	   UnpauseButton.addActionListener(new SideButtons());
	   RulePanel.add(UnpauseButton);
	   UnpauseButton.setVisible(false);


	   MusicButton = new JButton();
	   MusicButton.setFocusable(false);
	   MusicButton.setText("Music on/off");
	   MusicButton.addActionListener(new SideButtons());
	   RulePanel.add(MusicButton);

	   

    }

    private class SideButtons implements ActionListener{

    	public void actionPerformed(ActionEvent e) {

    		if(e.getSource() == RulesButton)
		   	{
		   		
		   		if(!isPaused) pause();
		   		String text;
		   		text = "	RULES\n\n\nThis game is very similar\nto the classic game of tetris.\n\n" +
		   		"The Controls are as Follows:\n\n" +
		   		"Left Arrow: Move Block Left\n" +
		   		"Right Arrow: Move Block Right\n" +
		   		"Up Arrow: Rotate Block\n" + 
		   		"Down Arrow: Soft Drop\n" +
		   		"Space Bar: Hard Drop\n" + 
		   		"p: Pause Game\n" + 
		   		"\n\nHave Fun !";
		   		textArea = new JTextArea(text);
		   		textArea.setEditable(false);
		   		window.add(textArea);
		   		window.revalidate();
		   		tetrisPanel.setVisible(false);
		   		textArea.setVisible(true);
		   		//textArea.toFront();

		   		UnpauseButton.setVisible(true);
		   		RestartButton.setVisible(false);
		   		PauseButton.setVisible(false);
		   		RulesButton.setVisible(false);
		   		
		   	}
		   	else if(e.getSource() == UnpauseButton)
		   	{
		   		pause();
		   		UnpauseButton.setVisible(false);
		   		RestartButton.setVisible(true);
		   		PauseButton.setVisible(true);
		   		RulesButton.setVisible(true);
		   		window.remove(textArea);
		   		tetrisPanel.setVisible(true);
		   		tetrisPanel.requestFocus();

		   	} 
		   	else if(e.getSource() == PauseButton)
		   	{
		   		pause();
		   		
		   	}
		   	else if (e.getSource() == MusicButton){ 

		   		playMusic();


		   	}
		   	else if (e.getSource() == RestartButton) {
				restartGame();
			}


    	}

    }



    public void playMusic() {

		try{
    		File song = new File("tetrisSong.wav");
    		AudioInputStream aStream = AudioSystem.getAudioInputStream(this.getClass().getResource("tetrisSong.wav"));
    		//Clip clip = AudioSystem.getClip();
    		//clip.open(aStream);
    		//clip.start();
			}	 catch (Exception ex) { System.out.println("sorry couldn't open audio");}

	}
/*
    	InputStream in = new FileInputStream("tetrisSong.mp3");
    	AudioStream as = new AudioStream(in);
    	AudioPlayer.player.start(as);

    	String bip = "tetrisSong.mp3";
    	Media hit = new Media(bip);
    	MediaPlayer mediaPlayer = new MediaPlayer(hit);
    	mediaPlayer.play();
	try {
			java.applet.AudioClip clip = java.applet.Applet.newAudioClip(new java.net.URL(“file://c:/tetrisSong.wav"));
			clip.play();
		} catch (java.net.MalformedURLException murle) {
		System.out.println(murle);
		}
    		//File song = new File(
    		//URL url = this.getClass().getClassLoader().getResource("tetrisSong.mp3");
            //AudioInputStream aStream = AudioSystem.getAudioInputStream(url);

    		
    		//AudioInputStream aStream = AudioSystem.getAudioInputStream("/cs/student/marshallnaito/cs56/cs56-games-tetris/src/edu/ucsb/cs56/projects/games/tetris/tetrisSong.mp3");
    		//AudioInputStream aStream = AudioSystem.getAudioInputStream(song);
    		//AudioInputStream aStream = AudioSystem.getAudioInputStream(song);
    		//AudioInputStream aStream = AudioSystem.getAudioInputStream(new File("~/cs/student/marshallnaito/cs56/cs56-games-tetris/src/edu/ucsb/cs56/projects/games/tetris/tetrisSong.wav").getAbsoluteFile());
    		
  //   	Clip clip;
  //   	AudioInputStream audio;

  //   	try{
	 //    audio = AudioSystem.getAudioInputStream(new File("tetrisSong.mp3"));
		// } catch(IOException e) {
		// 	System.out.println("file not found");
		// }

		// try {
  //     	clip = AudioSystem.getClip();
  //     	} catch(LineUnavailableException e) {
  //     		System.out.println("line unavailable");
  //     	}

  //     	try{
  //     	clip.open(audio);
  //     	} catch(UnsupportedAudioFileException e) {
  //     		System.out.println("sound file not found");
  //     	}

  //     	clip.start();
      	 */

    public void beginGame() {
	for(int row = 0; row < MAX_ROW; row++){
	    for(int col = 0; col<MAX_COL; col++){
		board[row][col] = 0;
		color[row][col] = 0;
	    }
    	}
	this.setFocusable(true);
	RulePanel.setVisible(true);
	window.add(this);
	window.revalidate();
	window.repaint();

	BlockColor = Color.BLACK;
	Type1 y = new Type1();
	this.putBlock(y);
	timerdelay = TIMER_DELAY;
	timer = new Timer(timerdelay,this);
	timer.start();

	//this.setPreferredSize(new Dimension(205,460));
	this.setBackground(Color.WHITE);


	//if(this.canMoveDown() == true) 
	addKeyListener(new TAdapter());
    }
    
    public void actionPerformed(ActionEvent e) {

    

   	



	if (isFallingFinished) {
	    isFallingFinished = false;
	    int randomNumber = (int)(Math.random() * 7) + 1;
	    switch(randomNumber){
	    case 1: Type1 a = new Type1();
		whichType = 1;
		this.putBlock(a);
		break;
	    case 2: Type2 b = new Type2();
		whichType = 2;
		this.putBlock(b);
		break;
	    case 3: Type3 c = new Type3();
		whichType = 3;
		this.putBlock(c);
		break;
	    case 4: Type4 d = new Type4();
		whichType = 4;
		this.putBlock(d);
		break;
	    case 5: Type5 h = new Type5();
		whichType = 5;
		this.putBlock(h);
		break;
	    case 6: Type6 f = new Type6();
		whichType = 6;
		this.putBlock(f);
		break;
	    case 7: Type7 g = new Type7();
		whichType = 7;
		this.putBlock(g);
		break;
	    }
	    if(timerdelay > 200){
		double x = .1*timerdelay;
		timerdelay = timerdelay - (int)x;
	    }
	    timer.setDelay(timerdelay);
	}
	else 
	    {
		this.moveDown();
		this.deleteRows();
		this.deleteRows();
		this.deleteRows();
		this.deleteRows();
	    }
	this.repaint();

    }
    
    public int getBlockPosX(){
    	return this.BlockPosX;
    }

    public int getBlockPosY(){
    	return this.BlockPosY;
    }

    public int getRowCol(int r, int c){
    	return board[r][c];
    }

    public void clearBoard(){
    	
    	for(int row = 0; row < MAX_ROW; row++){
    		for(int col = 0; col<MAX_COL; col++){
    			board[row][col] = 0;
			color[row][col] = 0;
    		}
    	}
    	
    	timer.stop();
    	statusBar.setText("GAME OVER");
    	RestartButton.setText("Play Again");
    }

    public void putBlock(Block block){
	score++;
	statusBar.setText("SCORE = " + String.valueOf(score));
	RestartButton.setText("Restart");
	//int [][] theBlock = block.getBlock();
	//int k = (int)(Math.random() * MAX_COL);

	BlockPosX = 5;
	BlockPosY = 0;
	int posX = 5;
	int posY = 0;
	BlockInControl = block;

	int x = 0;
	for(int i=0;i<MAX_COL;i++){
	    if(board[posY+1][i] == 1)
	        x = 1;
	}
	if(x==1){
	    this.clearBoard();
	    score = 0;
	}


	for(int r=0;r<4;r++){
	    for(int c=0;c<4;c++){
		if(block.getRowCol(r,c) == 1)
		    {
			board[posY][posX]=1;

			switch(whichType){
			case 1: color[posY][posX] = 1;
			    break;
			case 2: color[posY][posX] = 2;
			    break;
			case 3: color[posY][posX] = 3;
			    break;
			case 4: color[posY][posX] = 4;
			    break;
			case 5: color[posY][posX] = 5;
			    break;
			case 6: color[posY][posX] = 6;
			    break;
			case 7: color[posY][posX] = 7;
			    break;
			}
		    }

		posX++;
	    }
	    posY++;
	    posX-=4;
	}
	
    }


    public boolean canMoveRight(){

    	int [][] temp = BlockInControl.getBlock();
    	int tempPosX = BlockPosX;
    	int	tempPosY = BlockPosY;

    	for(int r = 0; r <4; r++){
    		for(int c = 0; c < 4;c++){
    			if(temp[r][c] == 1){
    				if(c ==3){
    					if(tempPosX+1 > MAX_COL-1)
    						return false;
    					if(board[tempPosY][tempPosX+1]==1)
    						return false;
    				}
    				else{
    					if(tempPosX+1 > MAX_COL-1)
    						return false;
    					if(temp[r][c+1] == 0){
    						if(board[tempPosY][tempPosX+1]==1)
    						return false;
    					}
    				}
    			}
    			tempPosX++;
    		}
    		tempPosX = BlockPosX;
    		tempPosY++;
    	}
		return true;
    }

	public boolean canMoveLeft(){

		int [][] temp = BlockInControl.getBlock();
    	int tempPosX = BlockPosX;
    	int	tempPosY = BlockPosY;

    	for(int r = 0; r <4; r++){
    		for(int c = 0; c < 4;c++){
    			if(temp[r][c] == 1){
    				if(c ==0){
    					if(tempPosX-1 < 0)
    						return false;
    					if(board[tempPosY][tempPosX-1]==1)
    						return false;
    				}
    				else{
    					if(tempPosX-1 < 0)
    						return false;
    					if(temp[r][c-1] == 0){
    						if(board[tempPosY][tempPosX-1]==1)
    							return false;
    					}
    				}
    			}
    			tempPosX++;
    		}
    		tempPosX = BlockPosX;
    		tempPosY++;
    	}
		return true;
	}
	public boolean canMoveDown(){
	    int [][] temp = BlockInControl.getBlock();
    	int tempPosX = BlockPosX;
    	int	tempPosY = BlockPosY;

    	for(int r = 0; r <4; r++){
    		for(int c = 0; c < 4;c++){
    			if(temp[r][c] == 1){
    				if(r ==3){
    					if(tempPosY+1 > MAX_ROW-1)
    						return false;
    					if(board[tempPosY+1][tempPosX]==1)
    						return false;
    				}
    				else{
    					if(tempPosY+1 > MAX_ROW-1)
    						return false;
    					if(temp[r+1][c] == 0){
    						if(board[tempPosY+1][tempPosX]==1)
    							return false;
    					}
    				}
    			}
    			tempPosX++;
    		}
    		tempPosX = BlockPosX;
    		tempPosY++;
    	}

		return true;
	}

	public void moveRight(){
		if(canMoveRight()){
			int[][] temp = BlockInControl.getBlock();
			int tempPosX = BlockPosX;
			int tempPosY = BlockPosY;
			ArrayList <Point> CoordinatesToRight = new ArrayList <Point>();

			for(int r=0; r<4; r++){
				for(int c=0;c<4;c++){
					if(temp[r][c] == 1 && getRowCol(tempPosY,tempPosX)==1){
						CoordinatesToRight.add(new Point(tempPosX,tempPosY));
						board[tempPosY][tempPosX]=0;
						color[tempPosY][tempPosX]=0;
					}
					tempPosX++;
				}
				tempPosY++;
				tempPosX-=4;
			}

			for(Point p : CoordinatesToRight){
				board[(int)p.getY()][(int)p.getX()+1]=1;
				color[(int)p.getY()][(int)p.getX()+1]= whichType;
			}

			CoordinatesToRight.clear();
			CoordinatesToRight = null;
			BlockPosX++;
		}
	}

	public void moveLeft(){
		if(canMoveLeft()){
			int[][] temp = BlockInControl.getBlock();
			int tempPosX = BlockPosX;
			int tempPosY = BlockPosY;
			ArrayList <Point> CoordinatesToLeft = new ArrayList <Point>();

			for(int r=0; r<4; r++){
				for(int c=0;c<4;c++){
					if(temp[r][c] == 1 && getRowCol(tempPosY,tempPosX)==1){
						CoordinatesToLeft.add(new Point(tempPosX,tempPosY));
						board[tempPosY][tempPosX]=0;
						color[tempPosY][tempPosX]=0;
					}
					tempPosX++;
				}
				tempPosY++;
				tempPosX-=4;
			}

			for(Point p : CoordinatesToLeft){
				board[(int)p.getY()][(int)p.getX()-1]=1;
				color[(int)p.getY()][(int)p.getX()-1]=whichType;
			}

			CoordinatesToLeft.clear();
			CoordinatesToLeft = null;
			BlockPosX--;

		}

	}

	public void moveDown(){

		if(canMoveDown()){
			int[][] temp = BlockInControl.getBlock();
			int tempPosX = BlockPosX;
			int tempPosY = BlockPosY;
			ArrayList <Point> CoordinatesToDown = new ArrayList <Point>();

			for(int r=0; r<4; r++){
				for(int c=0;c<4;c++){
					if(temp[r][c] == 1 && getRowCol(tempPosY,tempPosX)==1){
						CoordinatesToDown.add(new Point(tempPosX,tempPosY));
						board[tempPosY][tempPosX]=0;
						color[tempPosY][tempPosX]=0;
					}
					tempPosX++;
				}
				tempPosY++;
				tempPosX-=4;
			}

			for(Point p : CoordinatesToDown){
				board[(int)p.getY()+1][(int)p.getX()]=1;
				color[(int)p.getY()+1][(int)p.getX()]=whichType;
			}

			CoordinatesToDown.clear();
			CoordinatesToDown = null;
			BlockPosY++;
		}
		else
		    isFallingFinished=true;

	}

	public void drop(){
		while(canMoveDown())
			moveDown();
	}

    public void deleteRows(){
	int nodelete;
	int rowtobedeleted = 0;
    
	for(int row = 0; row<MAX_ROW; row++){
	    nodelete = 0;
	    for(int col = 0; col <MAX_COL; col++){
		if(board[row][col] == 0)
		    nodelete = 1;
	    }
	    if(nodelete == 0){
		rowtobedeleted = row;
	    }
	}
	if(rowtobedeleted != 0){
	    for(int row = rowtobedeleted; row > 1; row--)
		for (int col = 0; col < MAX_COL; col++){
			board[row][col] = board[row-1][col];
		    }
	}
	for (int col = 0; col < MAX_COL; col++)
	    board[0][col] = 0;
	if(rowtobedeleted != 0){
	    score = score + 10;
	    statusBar.setText("SCORE = " + String.valueOf(score));
	}
    }

    public Color getColor(int x){
	switch(x){
	case 1: BlockColor = Color.BLACK;
	    break;
	case 2: BlockColor = Color.GREEN;
	    break;
	case 3: BlockColor =  Color.BLUE;
	    break;
	case 4: BlockColor =  Color.ORANGE;
	    break;
	case 5: BlockColor =  Color.MAGENTA;
	    break;
	case 6: BlockColor =  Color.BLUE;
	    break;
	case 7: BlockColor =  Color.RED;
	    break;

	}
	return BlockColor;
    }




    public void paint(Graphics gr)
    {
	super.paint(gr);
	for(int row = 0; row<MAX_ROW; row++){
	    for(int col = 0; col <MAX_COL; col++){
		if(board[row][col] == 1){
		    gr.setColor(getColor(color[row][col]));
		    gr.fillRect(20*col,20*row,20,20);
		}
		else{
		    gr.setColor(Color.WHITE);
		    gr.fillRect(20*col,20*row,20,20);
		}

	    }
	}
    }

    private void pause()
    {	
	isPaused = !isPaused;
	if (isPaused) {
	    timer.stop();
	    PauseButton.setText("Resume");
        statusBar.setText("GAME PAUSED");
	} else {
	    timer.start();
	    PauseButton.setText("Pause");
	    statusBar.setText("SCORE = " + String.valueOf(score));
	}
	repaint();
    }
    
    
    class TAdapter extends KeyAdapter {
         public void keyPressed(KeyEvent e) {

             int keycode = e.getKeyCode();
	     
             if (keycode == 'p' || keycode == 'P') {
                 pause();
                 return;
             }
	     
             if (isPaused)
                 return;
	     
         	switch (keycode) {
		     	case KeyEvent.VK_UP:
			 	{ 
			 		BlockInControl.rotate();
			 		break;
			 	}
				case KeyEvent.VK_DOWN:
				{
					timer.setDelay(TIMER_DELAY/6); break;
				}
	            case KeyEvent.VK_LEFT:
	            {
	            	moveLeft();
	            	break;
	            }
	            case KeyEvent.VK_RIGHT:
	            {
	                moveRight();
	                break;
	            }
	            case KeyEvent.VK_SPACE:
	            { 
	                drop();
	                break;
	            }
	          
            }

         }
		 

}


   

	public static void main(String [] args){

	    
	    window = new JFrame("TETRIS");
	    
	    window.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	    statusBar = new JLabel("A Fun Game of Classic Tetris");
	    window.add(BorderLayout.SOUTH, statusBar);
	    TetrisBoard b = new TetrisBoard();
	    tetrisPanel = b;
	    window.add(tetrisPanel);
	    window.add(BorderLayout.EAST, RulePanel);
	    RulePanel.setVisible(false);

	    window.add(startPanel);
		
	   
	    window.setSize(WINDOW_X, WINDOW_Y);
	    window.setVisible(true);
	    
	}

    }



