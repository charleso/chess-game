/*
*	chessGame.java	1.0 26/7/2000
*	chessGame.java 1.1  4/9/2000 (Introduced Check)
*
*	Copyright (c) 2000 Charles Plunkett O'Farrell
*/

import java.awt.*;
import java.applet.*;
import java.awt.event.*;

public class chessGame extends Applet 
implements MouseListener
{
	Square gridRect[][],clickedRect,tempRect;
	boolean white_turn,whiteChessCheck,blackChessCheck,checkMethod;
	int clickX,clickY,size,count;
	
	String temp_string_piece;
	boolean temp_empty;
	boolean temp_white_color;
	Image temp_piece;
	
	Graphics gChess;
	Image buffer;
	
	String stringPieces[][] =
	{ 
		{"br","bn","bb","bq","bk","bb","bn","br"},
		{"bp","bp","bp","bp","bp","bp","bp","bp"},
		{" "," "," "," "," "," "," "," "},
		{" "," "," "," "," "," "," "," "},
		{" "," "," "," "," "," "," "," "},
		{" "," "," "," "," "," "," "," "},
		{"wp","wp","wp","wp","wp","wp","wp","wp"},
		{"wr","wn","wb","wq","wk","wb","wn","wr"},
	};
	
	public void init(){
		setBackground(Color.white);
		white_turn = true;	
		size = 35;
		gridRect = new Square[8][8];
		clickedRect = new Square();
		clickedRect = null;
		for(int i=0;i<=7;i++){
			for (int j=0;j<=7;j++){
				
				gridRect[j][i] = new Square();
				gridRect[j][i].setBounds(25+j*size,25+i*size,size,size);
			
				gridRect[j][i].setPos(j,i);
				
				gridRect[j][i].setPiece(stringPieces[i][j]);
				
			}
		}
		this.addMouseListener(this);
		
		buffer = createImage(this.getSize().width,this.getSize().height);
		gChess = buffer.getGraphics();
	}
	
	public void mousePressed(MouseEvent e){
		clickX = e.getX();
		clickY = e.getY();
		repaint();
	}
	
	public void paint(Graphics g){
		gChess.setColor(this.getBackground());
		gChess.fillRect(0,0,this.getSize().width,this.getSize().height);
		gChess.setColor(Color.black);
		
		count = 0;
		for(int i=0;i<=7;i++){
			for (int j=0;j<=7;j++){
				if(white_turn)gridRect[j][i].whitejumpedPawn = false;
				if(!white_turn)gridRect[j][i].blackjumpedPawn = false;
			}
		}
		
		for(int i=0;i<=7;i++){
			for (int j=0;j<=7;j++){	
				gridRect[j][i].drawRectangle(gChess);

				if (gridRect[j][i].contains(clickX,clickY)){
					if(gridRect[j][i].isTurn()){
						clickedRect = gridRect[j][i];
					}
					else{
						if(clickedRect == null);
						else{
							if(gridRect[j][i].isMovePossible()){
								tempRect = clickedRect;
								gridRect[j][i].movePiece();
								clickedRect = gridRect[j][i];
								check();
								/*
								if((whiteChessCheck && !white_turn) || (blackChessCheck && white_turn)){
									if(checkMate()) gChess.drawString("CHECKMATE!!!",350,100);
								}
								else
								*///Checkmate - disabled
								if((whiteChessCheck && white_turn) || (blackChessCheck && !white_turn)){
									gChess.drawString("CHECK!!!",350,100);
									clickedRect = tempRect;
								 	gridRect[j][i].undoMove();
								}
								else{
									gridRect[j][i].firstMove = false;
									clickedRect = null;
									white_turn = !white_turn;
								}
							}
							else{
								gChess.drawString("Invalid Move",350,50);
								//play(getCodeBase(),"stuff/ding.au");
							}
						}
					}
				}
			}
					
			gChess.drawString(""+(char)(65+i),i*35+37,15);
			gChess.drawString("" + (i+1),10,i*35+45);
		}
		
		if(clickedRect != null)clickedRect.drawSelection(gChess);
		if(white_turn) gChess.drawString("White turn",350,25);
		else gChess.drawString("Black turn",350,25);
		
		if(whiteChessCheck) gChess.drawString("White is in check!!!",350,75);
		if(blackChessCheck) gChess.drawString("Black is in check!!!",350,75);
		
		//Draws the pieces on the board
		for(int i=0;i<=7;i++){
			for (int j=0;j<=7;j++){
				gChess.drawImage(gridRect[j][i].piece,gridRect[j][i].x,gridRect[j][i].y,size,size,this);
			}
		}
		clickX=0;clickY=0;
		
		g.drawImage(buffer,0,0,this);
	}
	
	public class Square extends Rectangle{
		Image piece;
		boolean white_color,empty,whitejumpedPawn,blackjumpedPawn;
		boolean whiteCheck, blackCheck, firstMove=true;
		int intXPos, intYPos;
		String string_piece;
		
		//Sets the int cooridante values of the square
		public void setPos(int x, int y){
			intXPos = x;
			intYPos = y;
		}
		
		//Draws the rectangle on the screen
		public void drawRectangle(Graphics g){
			if (count%2!=0) g.fillRect(x,y,width,height);
			g.drawRect(x,y,width,height);
			if(intXPos==7) count++;
			count++;
		}
		
		//Sets up the piece initially
		public void setPiece(String string){
			string_piece = string;
			piece = getImage(getDocumentBase(),"Stuff/"+ string_piece+".gif");
			if(string_piece==" ")empty=true;
			else empty=false;
			if(string_piece.charAt(0)=='w')white_color=true;
			if(string_piece.charAt(0)=='b')white_color=false;
		}
		
		//Determines whether the user has clicked on a piece
		//that is allowed to move
		public boolean isTurn(){
			if(!empty){
				if(white_turn && white_color)return true;
				else
				if(!white_turn && !white_color)return true;
			}
			return false;
		}
		
		//Determines if the move is possible by checking the
		//move requirements of the chess piece selected
		public boolean isMovePossible(){
			char charPiece = clickedRect.string_piece.charAt(1);
			boolean test;
			switch(charPiece){
				case 'r':
				test = isRook();
				break;
	
				case 'n':
				test = isKnight();
				break;
				
				case 'b':
				test = isBishop();
				break;
				
				case 'q':
				test = isQueen();
				break;
				
				case 'k':
				test = isKing();
				break;
				
				case 'p':
				test = isPawn();
				break;
				
				default:
				test = false;
				break;
			}
			return test;
		}
		
		//Moves the required pieces
		public void movePiece(){
			
			temp_string_piece = string_piece;
			temp_empty = empty;
			temp_white_color = white_color;
			temp_piece = piece;
			
			string_piece = clickedRect.string_piece;
			empty = clickedRect.empty;
			white_color = clickedRect.white_color;
			piece = getImage(getDocumentBase(),"Stuff/"+ clickedRect.string_piece+".gif");
		
			clickedRect.string_piece = " ";
			clickedRect.empty = true;
			clickedRect.piece = getImage(getDocumentBase(),".gif");
		
		}
		
		public void undoMove(){
			clickedRect.string_piece = string_piece;
			clickedRect.empty = empty;
			clickedRect.white_color = white_color;
			clickedRect.piece = getImage(getDocumentBase(),"Stuff/"+ string_piece+".gif");
			
			string_piece = temp_string_piece;
			empty = temp_empty;
			white_color = temp_white_color;
			piece = temp_piece;
		}
		
		//Draws the selected rectangle
		public void drawSelection(Graphics g){
			g.setColor(Color.pink);
			g.fillRect(x,y,width,height);
			g.setColor(Color.black);
		}
		
		//Checks whether the move can be made by the Rook
		public boolean isRook(){
			if(intYPos == clickedRect.intYPos)return checkHorizontal();
			else
			if(intXPos == clickedRect.intXPos)return checkVertical();
			else return false;
				
		}
		
		//Checks to see if there is any piece between
		//the piece and the potential move horizontally
		public boolean checkHorizontal(){
			if(intXPos<clickedRect.intXPos){
				for(int i=intXPos+1;i<=clickedRect.intXPos-1;i++){
					if (!gridRect[i][intYPos].empty){
						return false;
					}
				}
			}
			else{
				for(int i=clickedRect.intXPos+1;i<=intXPos-1;i++){
					if (!gridRect[i][intYPos].empty){
						return false;
					}
				}
			}
			return true;
		}
		
		//Checks to see if there is any piece between
		//the piece and the potential move vertically
		public boolean checkVertical(){
			if (intYPos<clickedRect.intYPos){
				for(int i=intYPos+1;i<=clickedRect.intYPos-1;i++){
					if (!gridRect[intXPos][i].empty){
						return false;
					}
				}
			}
			else{
				for(int i=clickedRect.intYPos+1;i<=intYPos-1;i++){
					if (!gridRect[intXPos][i].empty){
						return false;
					}
				}
			}
			return true;
		}
		
		//Checks whether the move can be made by the Knight
		public boolean isKnight(){
			if(intXPos==clickedRect.intXPos+2){
				if(intYPos==clickedRect.intYPos+1)return true;
				else
				if(intYPos==clickedRect.intYPos-1)return true;
			}
			else
			if(intXPos==clickedRect.intXPos-2){
				if(intYPos==clickedRect.intYPos+1)return true;
				else
				if(intYPos==clickedRect.intYPos-1)return true;
			}
			else
			if(intXPos==clickedRect.intXPos+1){
				if(intYPos==clickedRect.intYPos+2)return true;
				else
				if(intYPos==clickedRect.intYPos-2)return true;
			}
			else
			if(intXPos==clickedRect.intXPos-1){
				if(intYPos==clickedRect.intYPos+2)return true;
				else
				if(intYPos==clickedRect.intYPos-2)return true;
			}
			return false;
		}
		
		//Checks whether the move can be made by the Bishop
		public boolean isBishop(){
			if(intXPos<clickedRect.intXPos){
				if(intYPos<clickedRect.intYPos)return diagonalMove(-1,-1,clickedRect.intXPos,intXPos);
				else
				if(intYPos>clickedRect.intYPos)return diagonalMove(-1,1,clickedRect.intXPos,intXPos);
			}
			else
			if(intXPos>clickedRect.intXPos){
				if(intYPos<clickedRect.intYPos)return diagonalMove(1,-1,intXPos,clickedRect.intXPos);
				else
				if(intYPos>clickedRect.intYPos)return diagonalMove(1,1,intXPos,clickedRect.intXPos);
			}
			return false;
		}
		
		//Checks to see if there is any piece between
		//the piece and the potential move diagonally
		public boolean diagonalMove(int m,int n,int pos1,int pos2){
			for(int i=1;i<=pos1 - pos2;i++){
				if(intXPos==clickedRect.intXPos+i*m && intYPos==clickedRect.intYPos+i*n) return true;
				else
				if(clickedRect.intXPos+i*m<0 || clickedRect.intXPos+i*m >7 ||
					clickedRect.intYPos+i*n<0 || clickedRect.intYPos+i*n >7) return false;
				else
				if(!gridRect[clickedRect.intXPos+i*m][clickedRect.intYPos+i*n].empty)return false;

			}
			return false;
		}
		
		//Checks whether the move can be made by the Queen
		public boolean isQueen(){
			if(isBishop() || isRook()) return true;
			else return false;
		}
		
		//Checks whether the move can be made by the Pawn
		public boolean isPawn(){
			if(clickedRect.white_color){
				if((clickedRect.intXPos==intXPos+1 || clickedRect.intXPos==intXPos-1) &&
					(clickedRect.intYPos-1==intYPos) && (!empty || blackjumpedPawn) ){
						if(blackjumpedPawn && !checkMethod){
							gridRect[intXPos][3].string_piece = " ";
							gridRect[intXPos][3].empty = true;
							gridRect[intXPos][3].piece = getImage(getDocumentBase(),".gif");
						}	
						return true;
					}
				else{
					if(clickedRect.intYPos==6 && intYPos==4){
						if(clickedRect.intXPos==intXPos && empty){
							gridRect[intXPos][5].whitejumpedPawn = true;
							return true;
						}	
					}
					else{ 
						if(clickedRect.intXPos==intXPos && intYPos==clickedRect.intYPos-1 && empty){
							if(intYPos==0  && !checkMethod){
								clickedRect.piece = getImage(getDocumentBase(),"wq.gif");
								clickedRect.string_piece="wq";
							}
						return true;
						}
					}
				}
			}
			else{
				if(!clickedRect.white_color){
					if((clickedRect.intXPos==intXPos+1 || clickedRect.intXPos==intXPos-1) &&
						(clickedRect.intYPos+1==intYPos) && (!empty || whitejumpedPawn)){
							if(whitejumpedPawn && !checkMethod){
								gridRect[intXPos][4].string_piece = " ";
								gridRect[intXPos][4].empty = true;
								gridRect[intXPos][4].piece = getImage(getDocumentBase(),".gif");
							}
							return true;
						}
					else{
						if(clickedRect.intYPos==1 && intYPos==3){
							if(clickedRect.intXPos==intXPos && empty){
								gridRect[intXPos][2].blackjumpedPawn=true;
								return true;
							}
						}
						else{ 
							if(clickedRect.intXPos==intXPos && intYPos==clickedRect.intYPos+1 && empty){ 
								if(intYPos==7 && !checkMethod){
									clickedRect.piece = getImage(getDocumentBase(),"bq.gif");
									clickedRect.string_piece="bq";
								}
								return true;
							}
						}
					}
				}
			}
			return false;
		}
		
		//Checks whether the move can be made by the King
		public boolean isKing(){
			if(clickedRect.intXPos==intXPos+1 || clickedRect.intXPos==intXPos-1){
				if(clickedRect.intYPos==intYPos+1 || clickedRect.intYPos==intYPos-1 ||
				clickedRect.intYPos==intYPos) return true;
			}
			else
				if((clickedRect.intYPos==intYPos+1 || clickedRect.intYPos==intYPos-1) &&
				clickedRect.intXPos==intXPos) return true;
			else
				if(gridRect[4][7].firstMove && intXPos==6 && intYPos==7 && gridRect[7][7].string_piece=="wr" &&
					!gridRect[5][7].blackCheck && !gridRect[6][7].blackCheck &&
					gridRect[5][7].empty && gridRect[6][7].empty && 
					!checkMethod){
					tempRect = clickedRect;
					clickedRect = gridRect[7][7];
					gridRect[5][7].movePiece();
					clickedRect = tempRect;
					return true;
				}
			else
				if(gridRect[4][7].firstMove && intXPos==2 && intYPos==7 && gridRect[0][7].string_piece=="wr" &&
					!gridRect[1][7].blackCheck && !gridRect[2][7].blackCheck && !gridRect[3][7].blackCheck &&
					gridRect[1][7].empty && gridRect[2][7].empty && gridRect[3][7].empty &&
					!checkMethod){
					tempRect = clickedRect;
					clickedRect = gridRect[0][7];
					gridRect[3][7].movePiece();
					clickedRect = tempRect;
					return true;
				}
			else
				if(gridRect[4][0].firstMove && intXPos==6 && intYPos==0 && gridRect[7][0].string_piece=="br" &&
					!gridRect[5][0].whiteCheck && !gridRect[6][0].whiteCheck &&
					gridRect[5][0].empty && gridRect[6][0].empty && 
					!checkMethod){
					tempRect = clickedRect;
					clickedRect = gridRect[7][0];
					gridRect[5][0].movePiece();
					clickedRect = tempRect;
					return true;
				}
			else
				if(gridRect[4][0].firstMove && intXPos==2 && intYPos==0 && gridRect[0][0].string_piece=="br" &&
					!gridRect[1][0].whiteCheck && !gridRect[2][0].whiteCheck && !gridRect[3][0].whiteCheck &&
					gridRect[1][0].empty && gridRect[2][0].empty && gridRect[3][0].empty &&
					!checkMethod){
					tempRect = clickedRect;
					clickedRect = gridRect[0][0];
					gridRect[3][0].movePiece();
					clickedRect = tempRect;
					return true;
				}
			return false;
		}
			
		public void checkCheck(){
			for(int i=0;i<=7;i++){
				for (int j=0;j<=7;j++){
					if(gridRect[j][i].isMovePossible()){
						if(gridRect[j][i].empty){
							if(white_color) gridRect[j][i].whiteCheck = true;
							else gridRect[j][i].blackCheck = true;
						}
						else{
							if(gridRect[j][i].string_piece == "wk" && !white_color){
								whiteChessCheck = true;
								gridRect[j][i].firstMove = false;
							}
							if(gridRect[j][i].string_piece == "bk" && white_color){
								blackChessCheck = true;
								gridRect[j][i].firstMove = false;
							}
						}
					}
				}
			}
		}
		
		public boolean outOfCheck(){
			Square tempRect2 = new Square();
			for(int i=0;i<=7;i++){
				for (int j=0;j<=7;j++){	
					if(gridRect[j][i].isMovePossible()){
						tempRect2 = clickedRect;
						gridRect[j][i].movePiece();
						clickedRect = gridRect[j][i];
						if(!isCheck()){
							clickedRect = tempRect;
							gridRect[j][i].undoMove();
							return true;
						}
						clickedRect = tempRect2;
						gridRect[j][i].undoMove();
					}
				}
			}
			return false;
		}
	}
	
	public void check(){
		checkMethod = true;
		whiteChessCheck = false;
		blackChessCheck = false;
		for(int i=0;i<=7;i++){
			for (int j=0;j<=7;j++){
				gridRect[j][i].whiteCheck = false;
				gridRect[j][i].blackCheck = false;
			}
		}
		for(int i=0;i<=7;i++){
			for (int j=0;j<=7;j++){
				if(!gridRect[j][i].empty){
					clickedRect = gridRect[j][i];
					gridRect[j][i].checkCheck();
				}
			}
		}
		checkMethod = false;
	}
	
	public boolean isCheck(){
		check();
		if((whiteChessCheck && !white_turn)||
			(blackChessCheck && white_turn)) return true;
		return false;
	}
	
	public boolean checkMate(){
		for(int i=0;i<=7;i++){
			for (int j=0;j<=7;j++){
				if(!gridRect[j][i].empty){	
					if(gridRect[j][i].string_piece.charAt(1) == 'k') ;
					else
					if((gridRect[j][i].white_color && white_turn) ||
						(!gridRect[j][i].white_color && !white_turn)){
						clickedRect = gridRect[j][i];
						if(gridRect[j][i].outOfCheck()) return false;
					}
				}
			}
		}
		return true;
	}
	
	public void update(Graphics g){
		paint(g);
	}
	
	public void mouseEntered(MouseEvent e){;}
	public void mouseExited(MouseEvent e){;}
	public void mouseReleased(MouseEvent e){;}
	public void mouseClicked(MouseEvent e){;}
	
}	