package GUI;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import Threads.CharacterThread;
import Threads.ThreadManager;
import Elements.LumberJack;
import Elements.Snake;

public class TileManager {
    private Map<String, BufferedImage> tileImages;
    private GameGUI game;
    private char[][] mapTileChar;
    private ThreadManager threadManager;


    public TileManager(GameGUI game, ThreadManager threadManager) {
        this.game = game;
        mapTileChar = new char[game.maxWorldRow][game.maxWorldCol];
        this.threadManager = threadManager;
        tileImages = new HashMap<>();
        createThreads();
        try {
            BufferedImage treeImage = resizeImage("/assets/tree.png", 16, 16);
            BufferedImage axeImage = resizeImage("/assets/axe.png", 16, 16);
            BufferedImage lampImage = resizeImage("/assets/lamp.png", 16, 16);
            BufferedImage houseImage = resizeImage("/assets/house.png", 16, 16);
            BufferedImage carImage = resizeImage("/assets/car_crash.png", 16, 16);
            BufferedImage lakeImage = resizeImage("/assets/water.png", 16, 16);
            BufferedImage bridgeImage = resizeImage("/assets/bridge_lake.png", 16, 16);
            BufferedImage lumberjackImage = resizeImage("/assets/lumberjack.png", 16, 16);
            BufferedImage playerImage = resizeImage("/assets/player.png", 16, 16);
            BufferedImage earthImage = resizeImage("/assets/earth.png", 16, 16);
            BufferedImage witchTentImage = resizeImage("/assets/witch_tent.png", 16, 16);
            BufferedImage snakeImage = resizeImage("/assets/snake.png", 16, 16);
            BufferedImage mapImage = resizeImage("/assets/map.png", 16, 16);
            BufferedImage heartImage = resizeImage("/assets/heart.png", 10, 10);

            tileImages.put("hrt",heartImage);
            tileImages.put("#", treeImage);
            tileImages.put("T", treeImage);
            tileImages.put("A", axeImage);
            tileImages.put("L", lampImage);
            tileImages.put("M", mapImage);
            tileImages.put("H", houseImage);
            tileImages.put("C", carImage);
            tileImages.put("W", lakeImage);
            tileImages.put("B", bridgeImage);
            tileImages.put("X", lumberjackImage);
            tileImages.put("P", playerImage);
            tileImages.put(".", earthImage);
            tileImages.put("Z", witchTentImage);
            tileImages.put("S", snakeImage);
        } catch (IOException e) {
            System.out.println("Erro ao carregar imagens dos tiles" + e);
        }
        loadMap("world.txt");
    }

    public void getObjectOfTheGround(int x,int y){
        mapTileChar[y][x] = '.';
        game.repaint();
    }

    private BufferedImage resizeImage(String imagePath, int width, int height) throws IOException {
        BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream(imagePath));
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    public void addTileImage(String symbol, BufferedImage image) {
        tileImages.put(symbol, image);
    }

    public BufferedImage getTileImage(String symbol) {
        return tileImages.get(symbol);
    }

    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
    
            int numRows = 0;
            int numCols = 0;
    
            String line;
            while ((line = br.readLine()) != null) {
                numRows++;
                numCols = Math.max(numCols, line.length());
            }
    
            br.close();
            is = getClass().getResourceAsStream(filePath);
            br = new BufferedReader(new InputStreamReader(is));
    
            mapTileChar = new char[numRows][numCols];
    
            int row = 0;
            
            while ((line = br.readLine()) != null) {
                for (int col = 0; col < line.length(); col++) {
                    mapTileChar[row][col] = line.charAt(col);
                }
                row++;
            }
    
            br.close();
        } catch (Exception e) {
            System.out.println("Erro ao carregar o mapa: " + e);
        }
    }

    public void draw(Graphics g) {
        int playerX = game.player.getX() / game.TILE_SIZE;
        int playerY = game.player.getY() / game.TILE_SIZE;
    
        for (int worldRow = 0; worldRow < game.maxWorldRow; worldRow++) {
            for (int worldCol = 0; worldCol < game.maxWorldCol; worldCol++) {
                char tile = threadManager.isCharacterAtPosition(worldCol, worldRow)!=' ' ? threadManager.isCharacterAtPosition(worldCol, worldRow) : mapTileChar[worldRow][worldCol];
    
                int worldX = worldCol * game.TILE_SIZE;
                int worldY = worldRow * game.TILE_SIZE;
    
                int screenX = worldX - game.player.worldX + game.player.screenX;
                int screenY = worldY - game.player.worldY + game.player.screenY;
    
                BufferedImage tileImage;
                int distance = Math.abs(playerX - worldCol) + Math.abs(playerY - worldRow);
                if (distance <= game.VISIBILITY_RADIUS) {
                    tileImage = getTileImage(String.valueOf(tile));
                    BufferedImage backgroundTileImage = getTileImage("."); // Imagem do terreno de fundo
                    g.drawImage(backgroundTileImage, screenX, screenY, game.TILE_SIZE, game.TILE_SIZE, null);
                } else {
                    tileImage = getTileImage("Fog");
                }
                
                g.drawImage(tileImage, screenX, screenY, game.TILE_SIZE, game.TILE_SIZE, null);
                
            }
        }
    }


    public char getTile(int x, int y) {
        return mapTileChar[y][x];
    }

    public boolean isWalkable(int x, int y) {
        if (x >= 0 && y < game.maxWorldRow && y >= 0 && x < game.maxWorldCol) {
            return mapTileChar[y][x] == '.';
        } else {
            System.out.println("Out of bounds");
            return false;
        }
    }

    public void createThreads(){
        Snake snake = new Snake(5, 6, 1, game);
        CharacterThread snakeThread = new CharacterThread(game, snake, game.id++,this);
        threadManager.addThread(snakeThread);
        Snake snake2 = new Snake(5, 6, 2, game);
        CharacterThread snakeThread2 = new CharacterThread(game, snake2, game.id++,this);
        threadManager.addThread(snakeThread2);
        LumberJack lumberJack = new LumberJack(5, 6, 3, game);
        CharacterThread lumberJackThread = new CharacterThread(game, lumberJack, game.id++,this);
        threadManager.addThread(lumberJackThread);
        threadManager.startAllThreads();
    }

    public boolean foundATree(int x, int y){
        return mapTileChar[y][x] == 'T';
    }

    public void removeTree(int x, int y){
        mapTileChar[y][x] = '.';
    }

    
}
