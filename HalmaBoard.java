import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;

/**
 * File: Halma.java
 * @author Christopher Caulfield 
 * @author Calvin Gonzalez
 * 
 * This is a game of Halma, where the objective is to move your pieces into
 * your camp on the other side of the board.  You may move one space in any
 * direction, or you can jump over adjacent pieces as many times are you want.
 */

public class HalmaBoard extends JFrame{
    
    private JPanel board;
    private JButton[][] squares = new JButton [8][8];
    //Menu Items
    private JMenuItem mItemExit;
    private JMenuItem mItemAbout;
    //Board Icons
    private Icon homePiece = new ImageIcon(); //these are the default Icon's
    private Icon awayPiece = new ImageIcon(); //these are the default Icon's
    private Icon empty = new ImageIcon("empty");
    private Icon newHomePiece = new ImageIcon();
    private Icon newAwayPiece = new ImageIcon();
    //Coordinates and Icons for first and second click
    private Icon firstSelectionIcon, secondSelectionIcon;
    private int firstX, firstY, secondX, secondY, prevFirstX, prevFirstY, prevSecondX, prevSecondY; 
    //Useful adaptations of first click
    private int firstSelectionLen;
    private int clickCount=1;
    private String firstSelectionStr;
    //Decide player turn
    private JButton jbEndTurn;
    private int playerTurn;
    private int moveCount = 0;
    //Decide winner
    private boolean hasWon = false;
    private ArrayList<JButton> checkQueenWinner = new ArrayList<JButton>();
    private ArrayList<JButton> checkKingWinner = new ArrayList<JButton>();
    //Total moves in current game
    private int grandTotalMoves = 0;
    //print textArea
    private JTextArea jPrintArea;
    //Change icon variables
    private String stringNewHomePiece;
    private String stringNewAwayPiece;
    //Variable to keep track of an adjacent move
    private boolean movedAdjacent = false;

    public HalmaBoard(){

        setUpGame();  
   
        //JPanel for JButton: End Turn 
        JPanel jpNavigationRight = new JPanel(new GridLayout(0,1));
            JPanel buttonsPanel = new JPanel();               
                jbEndTurn = new JButton("End Turn");
                jbEndTurn.addActionListener( new ActionListener(){
                    public void actionPerformed(ActionEvent ae) {
                        //method that changes the players turn
                        if(prevFirstX<=4 && prevFirstY<=4 && grandTotalMoves<2){
                            changeTurn(1);
                        }
                        else if(prevFirstX<=4 && prevFirstY<=7 && grandTotalMoves<2 ){
                            changeTurn(0);
                        }
                        else{
                            changeTurn(playerTurn);
                        }
 
                    }
                });    
        buttonsPanel.add(jbEndTurn);
        jpNavigationRight.add(buttonsPanel);
        
        //Text Area to print out game dialogue
        JPanel jpText = new JPanel(new GridLayout(0,1));           
            jPrintArea = new JTextArea(5,10);
            jPrintArea.requestFocusInWindow(); 
            jpText.add(jPrintArea);
            JScrollPane scroll = new JScrollPane(jPrintArea);
            jpText.add(scroll);
            jpNavigationRight.add(jpText);
           
        add( jpNavigationRight, BorderLayout.SOUTH );
        
    } // end of constructor Halma
    
    public void setUpGame() {
        createBoard();   
        //places in boardPieces layout
        addFort();
        addPieces();
        addMenu();
        givePieceMoves();
            
        add(board, BorderLayout.CENTER);

    } // end method setUpGame
    
    public void createBoard() {
        //Halma Board Layout - this creates the whole board 
        board = new JPanel(new GridLayout(8,8));
        board.setSize(300,300);
        for(int x=0; x<squares.length; x++){
            for(int y=0; y<squares[x].length; y++){  
                //creates the button
                squares[x][y] = new JButton(x+","+y);
                //makes the color by default black
                squares[x][y].setForeground(Color.BLACK);
                squares[x][y].setOpaque(true);
                //adds the buttons
                board.add(squares[x][y]);
            }
        }
    } //end method createBoard
    
    public void givePieceMoves() {
        //Iterate through every piece in board
        for(int x=0; x<squares.length; x++){
            for(int y=0; y<squares.length; y++){
                //System.out.println(x + " " + y); Print layout of board
                //Add action listener to every piece in the board
                squares[x][y].addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent ae) {
                        if(clickCount == 1 ){ //save information about first click
                            
                               String selectionText = ((JButton) ae.getSource()).getText();
                               firstSelectionIcon = ((JButton) ae.getSource()).getIcon();
                               String[] arr = selectionText.split(","); 
                               firstX = Integer.parseInt(arr[0]);
                               firstY = Integer.parseInt(arr[1]);

                               firstSelectionStr = firstSelectionIcon.toString();
                               firstSelectionLen = firstSelectionStr.length();
                               
                               if(!firstSelectionIcon.equals(empty)) { 
                                   jPrintArea.append(String.format("You have selected %s at %d, %d%n", firstSelectionStr.substring(0, firstSelectionLen - 4), firstX, firstY));
                               }
                               else {
                                   jPrintArea.append(String.format("You have selected an empty spot.%n"));
                               }

                              clickCount++;
                        }
                        else{ // save information about second click
                            String selectionText = ((JButton) ae.getSource()).getText();
                            secondSelectionIcon = ((JButton) ae.getSource()).getIcon();
                            String[] arr = selectionText.split(","); 
                            secondX = Integer.parseInt(arr[0]);
                            secondY = Integer.parseInt(arr[1]); 
                                    
                            //validation - if validates do movePiece
                            if( validateMove()) {
                                movePiece();
                                prevSecondX = secondX;
                                prevSecondY = secondY;
                                prevFirstX = firstX;
                                prevFirstY = firstY;
                                moveCount++;
                                grandTotalMoves++;
                                isWinner(); //checks if there is a winner                                
                            }
                            clickCount--;
                        }   
                    }
                }); // end of actionListener
            }
        }  
    } //end method givePieceMoves
    


    public boolean validateMove() {      
        //is the first move the same as the previous first piece move?
        if( moveCount>0) {
            if(firstX != prevSecondX && firstY != prevSecondY){ 
               jPrintArea.append(String.format("You have already moved a piece to %d, %d%n",prevSecondX,prevSecondY ));
               return false;
            }
            if(secondY == prevFirstY && secondX == prevFirstX){
               jPrintArea.append(String.format("You you cannot move back to %d, %d%n",prevSecondX,prevSecondY ));
               return false;
            }
            
            
        }
        
        //Print appropriate message for valid moves
        if( !firstSelectionIcon.equals(empty)  && secondSelectionIcon.equals(empty)  ) { //move to adjacent spot
            if( Math.abs(secondX - firstX) <= 1  && Math.abs(secondY - firstY) <= 1 ) {            
                if(moveCount==0){
                  jPrintArea.append(String.format("You have moved to %d, %d%n", secondX, secondY));
                  movedAdjacent = true;
                  return true;        
                }
                else{
                  jPrintArea.append(String.format("You cannot move here%n"));
                  return false;
                }
            }
            else if( (Math.abs(secondX - firstX) == 0 || Math.abs(secondX - firstX) == 2)  && 
                        (Math.abs(secondY - firstY) == 0 || Math.abs(secondY - firstY) == 2) ) { //jumping pieces
                if( checkJumpedPiece() ) { //if there is a piece being jumped over
                    if(movedAdjacent==true ){
                        jPrintArea.append(String.format("You cannot move here%n"));
                        return false;
                    }
                    else{
                        jPrintArea.append(String.format("You have moved to %d, %d%n", secondX, secondY));
                        movedAdjacent = false;
                        return true;
                    }
                }
            }
        }
        //Print appropriate message for invalid moves
        if( firstSelectionIcon.equals(empty) ) {
            jPrintArea.append(String.format("Empty spot deselected.%n"));
            return false;
        }
        else if( secondSelectionIcon.equals(firstSelectionIcon) ) {
            jPrintArea.append(String.format(firstSelectionStr.substring(0, firstSelectionLen - 4) + " has been deselected%n" ));
            return false;
        }
        else {
            jPrintArea.append(String.format("You cannot move here%n"));
            return false;
        }
    } //end method validateMove
    
    //checks if there is a winner -- shows a message that there is a winner  
    public void isWinner() {
        String message = null; //will be updated depending on who wins
        int pieceCounter = 0;
        boolean isWinner = false;
        checkKingWinner.clear();
        checkQueenWinner.clear();
         
        for(int x=0; x<8; x++){
            for(int y=0; y<8; y++){
                //check the top left corner!
                if( (x+y) <= 3 ){
                    if(!squares[x][y].getIcon().equals(empty)){
                    checkKingWinner.add(squares[x][y]);
                        if(checkKingWinner.size() == 10) {
                            for(JButton jb: checkKingWinner) {
                                if( jb.getIcon().equals(homePiece) ) {
                                message =  "Player One has won the game";
                                disableOpponent( -1 ); //disables all the pieces
                                JOptionPane.showMessageDialog(null, message );
                                }
                            }
                        }
                    }
                }   
                //check the bottom right corner!
                else if( (x+y) >= 11 ){ 
                    if(!squares[x][y].getIcon().equals(empty)){
                    checkQueenWinner.add(squares[x][y]);
                        if(checkQueenWinner.size() == 10) {
                            for(JButton jb: checkQueenWinner) {
                                if( jb.getIcon().equals(awayPiece) ) {
                                message =  "Player Two has won the game";
                                disableOpponent( -1 ); //disables all the pieces
                                JOptionPane.showMessageDialog(null, message );
                                }
                            }
                        }
                    }
                }
            }
        }    
    } //end method isWinner
      
    /*  
        Change the other players turn
        player represents playerTurn
    */
    public int changeTurn( int player ) {
        if(player == 0) {
            jPrintArea.append(String.format("Player 1 has ended their turn.%n"));
            disableOpponent(player);
            player++;
            movedAdjacent = false;
            moveCount = 0;
            return playerTurn = player;
        }
        else {
            jPrintArea.append(String.format("Player 2 has ended their turn.%n"));
            disableOpponent(player--);
            movedAdjacent = false;
            moveCount = 0;
            return playerTurn = player;
        }
    } //end method changeTurn
    
    /*
        Disable opponents pieces during your turn
        @param int player represents int playerTurn
    */
    public void disableOpponent( int player ) {
        if(player == 0) {
            for(int x=0; x<8; x++){
                for(int y=0; y<8; y++){
                    //target the top left corner!
                    if(squares[x][y].getIcon().equals(homePiece)) {
                        squares[x][y].setEnabled(false);
                    }
                    else if(squares[x][y].getIcon().equals(awayPiece)) {
                        squares[x][y].setEnabled(true);
                    }
                }
            }
        }
        else if(player>0){
            for(int x=0; x<8; x++){
                for(int y=0; y<8; y++){
                    //target the top left corner!
                    if(squares[x][y].getIcon().equals(homePiece)) {
                        squares[x][y].setEnabled(true);
                    }
                    else if(squares[x][y].getIcon().equals(awayPiece)) {
                        squares[x][y].setEnabled(false);
                    }
                }
            }
        }
        else if(player == -1){
            for(int x=0; x<8; x++){
                for(int y=0; y<8; y++){
                    if( !squares[x][y].getIcon().equals(empty) ) {
                        squares[x][y].setEnabled(false);
                    }
                }
            }
        }
        else if(player == -2){ //makes so all is enabled
            for(int x=0; x<8; x++){
                for(int y=0; y<8; y++){
                    squares[x][y].setEnabled(true);
                }
            }
        }
    } //end method disableOpponent

    public boolean checkJumpedPiece() {
        //Calculate position of spot being jumped over
        int averageX = Math.abs( (secondX + firstX)/2 );
        int averageY = Math.abs( (secondY + firstY)/2 );
        //If the spot contains a piece, it is a valid move
        if( !squares[averageX][averageY].getIcon().equals(empty) ) {
            return true;
        } 
        return false;  
    } //end method checkJumpedPiece


    //This is a different color - for where the pieces will belong 
    //The orignal color of black will be changed to green
    public void addFort(){   
        for(int x=0; x<8; x++){
            for(int y=0; y<8; y++){
                //designing the top left corner!
                if((x+y)<= 3){
                    squares[x][y].setBackground(Color.ORANGE);
                }   
                //designing the bottom right corner!
                if((x+y)>= 11){ 
                    squares[x][y].setBackground(Color.ORANGE);
                }
            }
        }  
    }

    public void addPieces() {     
        homePiece = new ImageIcon("king.png"); //these are the default icons
        awayPiece = new ImageIcon("queen.png"); //these are the default icons
        for(int x=0; x<8; x++){
            for(int y=0; y<8; y++){
                //designing the top left corner!
                if((x+y)<= 3){
                    squares[x][y].setIcon(awayPiece);                  
                }                
                //designing the bottom right corner!
                else if((x+y)>= 11){
                    squares[x][y].setIcon(homePiece);
                }
                else{
                    squares[x][y].setIcon(empty);
                }
            }
        }  
    } //end method addPieces
    
    //mutator that changes the piece design
     public void setPieces() {       
        for(int x=0; x<8; x++){
            for(int y=0; y<8; y++){
                if(squares[x][y].getIcon().equals(homePiece)){ 
                   squares[x][y].setIcon(newHomePiece);  
                }
                else if(squares[x][y].getIcon().equals(awayPiece)){ 
                   squares[x][y].setIcon(newAwayPiece);
                }              
            }
        } 
        homePiece = newHomePiece;
        awayPiece = newAwayPiece;
    } //end method setPieces

    //basically it moves the pieces and refreshes layout
    public void movePiece(){  
        squares[secondX][secondY].setIcon(firstSelectionIcon);
        squares[firstX][firstY].setIcon(empty);
    } //end method movePiece

    public void addMenu(){
        //Create and set JMenu Bar - Options: File, Help
        JMenuBar mBar = new JMenuBar();
        setJMenuBar( mBar );
         
        //Create and add menu File
        JMenu mFile = new JMenu("File");
        mBar.add( mFile );
            //Add menu item Exit
            mItemExit = new JMenuItem("Exit");
            mFile.add(mItemExit);
            mItemExit.addActionListener( new ActionListener() {
                //adding action listener for exit
                public void	actionPerformed(ActionEvent ae) {            
                    System.exit(0);
                }
            });
            
        //Game options - restart
        JMenu mGame = new JMenu("Game");
        mBar.add( mGame );
            //option of King/Queen Layout
            JMenuItem mReset = new JMenuItem("New Game");
            mGame.add(mReset); 
         
        //Tells user who made this game 
        mReset.addActionListener(new ActionListener() {
		    public void	actionPerformed(ActionEvent ae) {
                addFort();
                addPieces();
                addMenu();
                disableOpponent(-2);
                prevFirstX = 99;
                prevFirstY = 99;
                prevSecondY = 99;
                prevSecondX = 99;
                clickCount=1;
                playerTurn = -5;
                moveCount = 0;
                playerTurn = 0;
                hasWon = false;
                grandTotalMoves = 0;
                movedAdjacent = false;
                jPrintArea.setText(null);
            }
		});
         
        //Create and add menu Help
        JMenu mHelp = new JMenu("Help");
        mBar.add( mHelp );
            //Add menu item Help
            mItemAbout = new JMenuItem("About");
            mHelp.add(mItemAbout); 
         
        //Tells user who made this game 
        mItemAbout.addActionListener(new ActionListener() {
		    public void	actionPerformed(ActionEvent ae) {
               String message = "Halma \n by: Christopher Caulfield and Calvin Gonzalez";
               JOptionPane.showMessageDialog(null, message );
            }
		});
        
        //Menu for modifiable icons
        JMenu mModify = new JMenu("Change Piece Design");
        mBar.add( mModify );
            //option of King/Queen Layout
            JMenuItem mKingQueen = new JMenuItem("King/Queen");
            mModify.add(mKingQueen); 
         
        //Tells user who made this game 
        mKingQueen.addActionListener(new ActionListener()	{
		    public void	actionPerformed(ActionEvent ae) {
               newHomePiece = new ImageIcon("king.png"); //king
               newAwayPiece = new ImageIcon( "queen.png"); //queen
               setPieces();
            }
		});
         
            //option of Pikachu/charmander Layout
            JMenuItem mPokemon = new JMenuItem("Pokemon");
            mModify.add(mPokemon); 
         
        //Tells user who made this game 
        mPokemon.addActionListener(new ActionListener()	{
			public void	actionPerformed(ActionEvent ae) {
               newHomePiece = new ImageIcon("charmander.png"); //chiz
               newAwayPiece = new ImageIcon( "pikachu.png"); //pik
               setPieces();
           }
		});
         
            //option of RIT logos
            JMenuItem mRIT = new JMenuItem("RIT Theme");
            mModify.add(mRIT); 
         
         //Tells user who made this game 
         mRIT.addActionListener(new ActionListener() {
			public void	actionPerformed(ActionEvent ae) { 
               newHomePiece = new ImageIcon("tiger.png"); //Tiger logo
               newAwayPiece = new ImageIcon( "ritLogo.png"); //RIT Logo
               setPieces();
            }
		});
         
            //option of Basketball/Football Layout
            JMenuItem mBalls = new JMenuItem("Sports Balls");
            mModify.add(mBalls); 
         
        //Allows user to change icons to sports balls
        mBalls.addActionListener(new ActionListener() {
			public void	actionPerformed(ActionEvent ae) {  
               newHomePiece = new ImageIcon("basketball.png"); //basketball
               newAwayPiece = new ImageIcon( "football.png"); //football
               setPieces();
            }
        });    
    }
 

    //This is the main that calls the HalmaBoard that contains the 
    //majority of the design. 
    //adds some extra detail to the HalmaBoard
    public static void main(String[] args) {
        //displays everything in HalmaBoard()
        HalmaBoard jf = new HalmaBoard();
        //main layout
        jf.setTitle("Halma"); 
        jf.setVisible(true);
        jf.pack();
        jf.setSize(500,600); 
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //closes frame
        jf.setLocationRelativeTo(null);
        jf.setVisible(true); //makes HalmaBoard visible
        }
    }








