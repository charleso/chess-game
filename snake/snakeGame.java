/*
	Charles O'Farrell
	11 July 2000
*/

import java.awt.*;
import java.applet.*;
import java.awt.event.*;

public class snakeGame extends Applet
implements Runnable, KeyListener
{
	Thread snakeThread;
	Square square[][];
	int size, squareSize, snakeNumber;
	char keyPress = 'l',key = 'r',lastPress;
	boolean eatNibble, gameOver=true;
	int nibbleX=0,nibbleY=0;
	
	char leftMove = 'h';
	char rightMove = 'k';
	char upMove = 'u';
	char downMove = 'j';
	
	Graphics gSnake;
	Image buffer;
	
	public void init(){
		size = 9;
		squareSize = 55;
		snakeNumber=0;
		square = new Square[squareSize][squareSize];
		buffer = createImage(this.getSize().width,this.getSize().height);
		gSnake = buffer.getGraphics();
		
		for(int i=0;i<squareSize;i++){
			for(int j=0;j<squareSize;j++){
				square[j][i] = new Square();
				square[j][i].setBounds(25+j*size,75+i*size,size,size);
				beginSquares(j,i);
			}
		}
		randomNibble();
		this.addKeyListener(this);
	}
	
	public void start(){
		if(gameOver){
		}
		else{
			if(snakeThread == null){
				snakeThread = new Thread(this);
			}
			snakeThread.start();
		}
	}
	
	public void run(){
		while (Thread.currentThread() == snakeThread){
			repaint();
			try{
				Thread.sleep(60);
			}	
			catch(InterruptedException e){}
		}
	}
	
	public void stop(){
			snakeThread = null;
	}
	
	public void startSnake(int pos,int j,int i){
		square[j][i].snake=true;
		square[j][i].position=pos;
	}
	
	public void keyPressed(KeyEvent e){
		key = e.getKeyChar();
		if(keyPress == 'p'){
			if(snakeThread == null){
				snakeThread = new Thread(this);
			}
			snakeThread.start();
		}
		if(key == 'p'){
			lastPress = keyPress;
			keyPress = 'p';
		}
		if(key == upMove && keyPress != 'd') keyPress = 'u';
		if(key == downMove && keyPress != 'u') keyPress = 'd';
		if(key == leftMove && keyPress != 'r') keyPress = 'l';
		if(key == rightMove && keyPress != 'l') keyPress = 'r';
		
		if(gameOver) tryAgain();
	}
	
	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e){}
	
	public void paint(Graphics g){
		gSnake.setColor(getBackground());
		gSnake.fillRect(0,0,this.getSize().width,this.getSize().height);
		gSnake.setColor(Color.black);
		
		gSnake.drawRect(25,75,size*squareSize,size*squareSize);
		
		if(keyPress == 'p'){
			gSnake.drawString("Paused",(squareSize*size)/2+25-2*9,250);
			snakeThread = null;
		}
		else{
			positionChange();
			if(inSquare()){
				if(!directionMove()){
					gameOver(gSnake);
				}
				else{
					if(eatNibble){
						snakeNumber+=2;
						do{
							randomNibble();
						}
						while(square[nibbleX][nibbleY].nibble &&
						square[nibbleX][nibbleY].snake);
						eatNibble = false;			
					}
					removeTail();
				}
			}
			else gameOver(gSnake);
		}
		for(int i=0;i<squareSize;i++){
			for(int j=0;j<squareSize;j++){
				if(square[j][i].snake) square[j][i].drawRectangle(gSnake); 
			}
		}
		
		
		
		gSnake.drawString("@",nibbleX*size+25,(nibbleY+1)*size+75);
		gSnake.drawString("Score: " + (snakeNumber-3)*10,25,25);
		
		g.drawImage(buffer,0,0,this);
	}
	
	public class Square extends Rectangle{
		boolean snake,nibble;
		int position;
		
		public void drawRectangle(Graphics g){
			g.fillRect(x,y,width,height);	//Variables used by Rectangle
		}
	}
	
	public boolean directionMove(){
		boolean move = true;
		switch(keyPress){
			case'u':
			move = moveSnake(0,-1);
			break;
			
			case'd':
			move = moveSnake(0,1);
			break;
			
			case'l':
			move = moveSnake(-1,0);
			break;
			
			case'r':
			move = moveSnake(1,0);
			break;
		}
		return move;
	}
	
	public boolean moveSnake(int x, int y){
		for(int i=0;i<squareSize;i++){
			for(int j=0;j<squareSize;j++){
				if(square[j][i].position==2){
					if(square[j+x][i+y].snake) return false;
					else{
						if(square[j+x][i+y].nibble)eatNibble=true;
						square[j+x][i+y].snake=true;
						square[j+x][i+y].position=1;
						return true;
					}
				}
			}
		}
		return true;
	}
	
	public void removeTail(){
		for(int i=0;i<squareSize;i++){
			for(int j=0;j<squareSize;j++){
				if(square[j][i].position==snakeNumber+1){
					square[j][i].snake=false;
					square[j][i].position = 0;
					break;
				}
			}
		}
	}
	
	public boolean inSquare(){
		for(int i=0;i<squareSize;i++){
			for(int j=0;j<squareSize;j++){
				if(square[j][i].position == 2){
					switch(keyPress){
						case'u':
						if(i==0) return false;
						break;
						
						case'd':
						if(i==squareSize-1) return false;
						break;
								
						case'l':
						if(j==0) return false;
						break;
					
						case'r':
						if(j==squareSize-1) return false;
						break;
					}
				}
			}
		}
		return true;
	}
	
	public void gameOver(Graphics g){
		g.drawString("Game Over",(squareSize*size)/2+25-3*9,250);
		g.drawString("Press any key to continue",210,300);
		gameOver = true;
		snakeThread = null;
	}
	
	public void positionChange(){
		for(int i=0;i<squareSize;i++){
			for(int j=0;j<squareSize;j++){
				if(square[j][i].snake) square[j][i].position++;
			}
		}
	}
	
	public void randomNibble(){
		square[nibbleX][nibbleY].nibble = false;
		nibbleX = (int)(Math.random()*squareSize);
		nibbleY = (int)(Math.random()*squareSize);
		square[nibbleX][nibbleY].nibble = true;
	}
	
	public void tryAgain(){
		gameOver = false;
		snakeNumber=0;
		for(int i=0;i<squareSize;i++){
			for(int j=0;j<squareSize;j++){
				square[j][i].snake = false;
				square[j][i].position = 0;
				beginSquares(j,i);
			}
		}
		randomNibble();
		keyPress = 'l';
		if(snakeThread == null){
			snakeThread = new Thread(this);
		}
		snakeThread.start();
	}
	
	public void beginSquares(int j, int i){
		if(j==squareSize/2 && i==squareSize/2){
			snakeNumber++;
			startSnake(snakeNumber,j,i);
		}
		if(j==squareSize/2+1 && i==squareSize/2){
			snakeNumber++;
			startSnake(snakeNumber,j,i);
		}
		if(j==squareSize/2+2 && i==squareSize/2){
			snakeNumber++;
			startSnake(snakeNumber,j,i);
		}
	}
	
	public void update(Graphics g){
		paint(g);
	}
}