package GUI;
import java.awt.*;
import java.awt.event.*;

import java.awt.image.*;
//imageio

import javax.swing.*;


import Elements.*;
import Threads.*;

public class GameGUI extends JPanel implements KeyListener{
    private JFrame mainFrame;
    // private Puzzle puzzle;
    public int VISIBILITY_RADIUS = 2; // Defina o raio de visão do jogador aqui
    

    //SCREEN SETTINGS
    final int originalTileSize=16;
    final int scale=4;
    public final int TILE_SIZE = originalTileSize *scale;
    final int maxScreenCol=16;
    final int maxScreenRow=10;
    public final int SCREEN_WIDTH = TILE_SIZE * maxScreenCol;
    public final int SCREEN_HEIGHT = TILE_SIZE * maxScreenRow;

    //world settings
    public final int maxWorldCol=66;
    public final int maxWorldRow=30;
    public final int worldWidth = TILE_SIZE * maxWorldCol;
    public final int worldHeight = TILE_SIZE * maxWorldRow;

    public int id=0;
    public TileManager tileManager;
    private Direction lastDirection;
    private int idIterator = 0;
    //Threads 
    private ThreadManager threadManager;
    public Player player;
    private int toolsFound=0;
    private boolean firstTime = true;
    private boolean firstTimeAxed = true;   


    public GameGUI() {
        
        try{
            initializeWindow();
        showIntroductionDialog();
        this.player = new Player(1*TILE_SIZE, 2*TILE_SIZE,idIterator++,this);
        this.lastDirection= Direction.NONE;
        this.threadManager = new ThreadManager();
        this.tileManager = new TileManager(this,this.threadManager);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void showIntroductionDialog() {
        IntroductionDialog introDialog = new IntroductionDialog(mainFrame);
        introDialog.setVisible(true);   
     }
    private void initializeWindow() {
        this.mainFrame = new JFrame("Maze Solver"); 
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(this);
        mainFrame.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        mainFrame.addKeyListener(this);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        mainFrame.pack();
    }

    public void draw(Graphics g, BufferedImage image, int x, int y) {
        g.drawImage(image, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(toolsFound==5 && firstTime){
            JOptionPane.showMessageDialog(mainFrame, "You found all the tools! Go back to the car to fix it and escape!");
            firstTime = false;
        } 
        super.paintComponent(g);
        tileManager.draw(g);
        player.draw(g);
        drawTab(g);
        drawToolsFound(g);
        
    }


    public void drawTab(Graphics g){
        int x = 10;
        int y = 35;
        for(int i = 0; i<player.getVidas();i++){
            g.drawImage(tileManager.getTileImage("9"), x, y, TILE_SIZE, TILE_SIZE, null);
            x+=TILE_SIZE;
        }

    }

    public void drawToolsFound(Graphics g) {
        g.setColor(Color.RED); 
        g.setFont(new Font("Arial", Font.BOLD, 30)); 
        g.drawString("Tools Found: " + toolsFound, 15, 25);
    }

    

    @Override
    public void keyPressed(KeyEvent e) {
        int x = player.getX();
        int y = player.getY();
        x = x / TILE_SIZE;
        y = y / TILE_SIZE;
    
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                this.lastDirection = Direction.UP;
                if (tileManager.isWalkable(x, y - 1)) {
                    player.setY((y - 1)*TILE_SIZE);
                }
                break;
            case KeyEvent.VK_DOWN:
                this.lastDirection = Direction.DOWN;

                if (tileManager.isWalkable(x, y + 1)) {
                    player.setY((y + 1)*TILE_SIZE);
                }
                break;
            case KeyEvent.VK_LEFT:
                this.lastDirection = Direction.LEFT;

                if (tileManager.isWalkable(x - 1, y)) {
                    player.setX((x - 1)*TILE_SIZE);
                }
                break;
            case KeyEvent.VK_RIGHT:
                this.lastDirection = Direction.RIGHT;
                if (tileManager.isWalkable(x + 1, y)) {
                    player.setX((x + 1)*TILE_SIZE);
                }
                break;
            case KeyEvent.VK_E:
                if(isAdjacentToALamp()){
                    this.VISIBILITY_RADIUS+=2;
                }
                if( isAdjacentToAAxe()){
                    if(firstTimeAxed){
                        //aparecer uma mensagem 
                        firstTimeAxed = false;
                        JOptionPane.showMessageDialog(mainFrame, "You found an axe! Press 'Space' to throw it. You can chop down trees or kill snakes with it.");
                    }
                    player.setHasAxe(8);

                }
                if(isAdjacentToHeart()){
                    player.addVida();
                }
                if(isAdjacentToCar()){
                    if(toolsFound==5){
                        JOptionPane.showMessageDialog(mainFrame, "You fixed the car and escaped! Congratulations!");
                        System.exit(0);
                    }else{
                        JOptionPane.showMessageDialog(mainFrame, "You need to find all the tools to fix the car and escape!");
                    }
                }
                if(isAdjacentToTool()){
                    toolsFound++;
                }
                break;

            case KeyEvent.VK_SPACE:
                if(player.hasAxe()>0){
                    startThrowable();
                    player.throwAxe();
                }
                break;
        }
    
        repaint();
    }

    public boolean isAdjacentToCar(){
        int playerX = player.getX() / TILE_SIZE;
        int playerY = player.getY() / TILE_SIZE;
        for(int i = -1; i<=1;i++){
            for(int j = -1; j<=1;j++){
                if(tileManager.getTile(playerX+i,playerY+j) == 'C'){
                    return true;
                }
            }
        }
        return false;
    }

    

    public void startThrowable() {
        if (lastDirection != Direction.NONE) {
            int x = player.getX();
            int y = player.getY();
            player.throwAxe();
            ThrowableThread throwableThread = new ThrowableThread(this, x/TILE_SIZE, y/TILE_SIZE, lastDirection, id++,"A",this.tileManager);
            threadManager.addThread(throwableThread);
            throwableThread.start();
        }
    }

    private boolean isAdjacentToTool(){
        int playerX = player.getX() / TILE_SIZE;
        int playerY = player.getY() / TILE_SIZE;
        for(int i = -1; i<=1;i++){
            for(int j = -1; j<=1;j++){
                if(tileManager.getTile(playerX+i,playerY+j) == '1'||
                tileManager.getTile(playerX+i,playerY+j) == '2'||
                tileManager.getTile(playerX+i,playerY+j) == '3'||
                tileManager.getTile(playerX+i,playerY+j) == '4'||
                tileManager.getTile(playerX+i,playerY+j) == '5'
                ){
                    tileManager.getObjectOfTheGround(playerX+i,playerY+j);
                    return true;
                }
            }
        }
        return false;
    }

    

    private boolean isAdjacentToHeart(){
        int playerX = player.getX() / TILE_SIZE;
        int playerY = player.getY() / TILE_SIZE;
        for(int i = -1; i<=1;i++){
            for(int j = -1; j<=1;j++){
                if(tileManager.getTile(playerX+i,playerY+j) == '9' ){
                    tileManager.getObjectOfTheGround(playerX+i,playerY+j);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAdjacentToALamp(){
        int playerX = player.getX() / TILE_SIZE;
        int playerY = player.getY() / TILE_SIZE;
        for(int i = -1; i<=1;i++){
            for(int j = -1; j<=1;j++){
                if(tileManager.getTile(playerX+i,playerY+j) == 'L'){
                    VISIBILITY_RADIUS+=1;
                    tileManager.getObjectOfTheGround(playerX+i,playerY+j);
                    return true;
                }
            }
        }
        return false;
    }



    private boolean isAdjacentToAAxe(){
        int playerX = player.getX() / TILE_SIZE;
        int playerY = player.getY() / TILE_SIZE;
        for(int i = -1; i<=1;i++){
            for(int j = -1; j<=1;j++){
                if(tileManager.getTile(playerX+i,playerY+j) == 'A'){
                    tileManager.getObjectOfTheGround(playerX+i,playerY+j);
                    return true;
                }
            }
        }
        return false;

    }
  

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public ThreadManager getThreadManager() {
        return threadManager;
    }


    public Player getPlayer() {
        return player;
    }
}