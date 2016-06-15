import java.awt.AWTException;
import java.awt.Robot;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;


public class Tetriz {
	private static String ttyConfig;
	private AtomicLong time;
	private long sleep = 200;
	private final long SLEEP_ORIGINAL = 200;
	private final int WIDTH = 32, HEIGHT = 45;
	private final int MAGICBOXWIDTH = TetrizBlock.width+6+2;
	private final int MAGICBOXHEIGHT = TetrizBlock.height+4+2;
	private TetrizBlock currentBlock, nextBlock;
	private final Random random = new Random(); 
    private int posX, posY;
    private String[][] magicString;
    private int score, level, levelCounter;
    private int amountOfRowsToLevelUp = 1;

	
	public static void main(String[] args) {
		new Tetriz();
	}
	
	public Tetriz(){
		try {
            setTerminalToCBreak();
            
            time = new AtomicLong(System.currentTimeMillis());

            
            menu();

            
	    }
	    catch (IOException e) {
	            System.err.println("IOException");
	    }
	    catch (InterruptedException e) {
	            System.err.println("InterruptedException");
	    }
	    finally {
	        try {
	            stty( ttyConfig.trim() );
	         }
	         catch (Exception e) {
	             System.err.println("Exception restoring tty config");
	         }
	    }
	}
	
	private static void setTerminalToCBreak() throws IOException, InterruptedException {

        ttyConfig = stty("-g");

        // set the console to be character-buffered instead of line-buffered
        stty("-icanon min 1");

        // disable character echoing
        stty("-echo");
    }

    /**
     *  Execute the stty command with the specified arguments
     *  against the current active terminal.
     */
    private static String stty(final String args)
                    throws IOException, InterruptedException {
        String cmd = "stty " + args + " < /dev/tty";

        return exec(new String[] {
                    "sh",
                    "-c",
                    cmd
                });
    }

    /**
     *  Execute the specified command and return the output
     *  (both stdout and stderr).
     */
    private static String exec(final String[] cmd)
                    throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        Process p = Runtime.getRuntime().exec(cmd);
        int c;
        InputStream in = p.getInputStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        in = p.getErrorStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        p.waitFor();

        String result = new String(bout.toByteArray());
        return result;
    }
    
private void menu() throws IOException, InterruptedException{
    	
    	int play = 0;
    	
    	printMenu(play);
    	
    	while(true){
    		
    		if ( System.in.available() != 0 ) {
                int c = System.in.read();
                
                switch (c) {
				case 68: //VÄNSTERPIL
					play = 1;
					printMenu(play);
					break;
					
				case 67: //HÖGER PIL
					play = 0;
					printMenu(play);
					break;
				case 0x1B:
					printMenu(play);
					break;
				}
                
                if(c == 10){ //ENTER
					if(play == 1){
						level = 1;
						play = newGame();
						printMenu(play);
					} else if(play == 2){
						play--;
						printMenu(play);
					} else {
						break;
					}
                }
    		}
    	}
    	
    	System.out.println("******* QUITING *******");
    	
    	
    }

	private void printMenu(int play) throws IOException, InterruptedException {
		for (int i = 0; i < 45; i++) {
			System.out.println("");
		}
		
		if(play == 1){ //highlighta play
			
			System.out.println("********************************************");
			System.out.println("********************************************");
			System.out.println("**                                        **");
			System.out.println("**                                        **"); //--
			System.out.println("**  XXXXXXXXXXXX            .----------.  **");
			System.out.println("**  X          X            |          |  **");
			System.out.println("**  X   PLAY   X            |   EXIT   |  **");
			System.out.println("**  X          X            |          |  **");
			System.out.println("**  XXXXXXXXXXXX            `----------´  **");
			System.out.println("**                                        **"); //---
			System.out.println("**                                        **");
			System.out.println("********************************************");
			System.out.println("********************************************");
			
		} else if(play == 0) { //highlighta exit
			
			System.out.println("********************************************");
			System.out.println("********************************************");
			System.out.println("**                                        **");
			System.out.println("**                                        **"); //--
			System.out.println("**  .----------.            XXXXXXXXXXXX  **");
			System.out.println("**  |          |            X          X  **");
			System.out.println("**  |   PLAY   |            X   EXIT   X  **");
			System.out.println("**  |          |            X          X  **");
			System.out.println("**  `----------´            XXXXXXXXXXXX  **");
			System.out.println("**                                        **"); //---
			System.out.println("**                                        **");
			System.out.println("********************************************");
			System.out.println("********************************************");
			
		} else {
			System.out.println("********************************************");
			System.out.println("********************************************");
			System.out.println("**                                        **");
			System.out.println("**               GAME  OVER               **"); //--
			System.out.println("**                                        **");
			String rad = "**        SCORE: ";
		    rad += score;
		    for(int i = 0; i < 25-String.valueOf(score).length(); ++i){
		    	rad += " ";
		    }
		    rad += "**";
			System.out.println(rad);
		    String rad2 = "**        LEVEL: ";
		    rad2 += level;
		    for(int i = 0; i < 25-String.valueOf(level).length(); ++i){
		    	rad2 += " ";
		    }
		    rad2 += "**";
			System.out.println(rad2);
			System.out.println("**                                        **");
			System.out.println("**          PRESS ENTER FOR MENU          **");
			System.out.println("**                                        **"); //---
			System.out.println("**                                        **");
			System.out.println("********************************************");
			System.out.println("********************************************");
		}
	}
	
	private int newGame() throws IOException, InterruptedException{
        posX = WIDTH/2;
        posY = 0;
        generateMap();
        
        score = level = levelCounter = 0;
        sleep = SLEEP_ORIGINAL;
        
        
		nextBlock    = TetrizBlock.generateNewBlock(random());
		magicString = magicBox();
		currentBlock = TetrizBlock.generateNewBlock(random());
        while (true) {
        	

            if ( System.in.available() != 0 ) {
                    int c = System.in.read();
                    if ( c == 0x1B ) { // esc knapp
                    	return 1;
                    }
                    
                    // HÄR KÖRS LITE TETRIS
                    
                    switch (c) {
					case 'a': // flytta vänster
						posX-=3;
						if(collided()){
							posX+=3;
						}
						break;
						
					case 's': // tvinga ner
						posY+=3;
						if(collided()){
							posY-=3;
						}
						break;
					case 'd': // flytta höger
						posX+=3;
						if(collided()){
							posX-=3;
						}
						break;
					case 'k': // rotera moturs
						currentBlock.rotate(false);
						if(collided()){
							currentBlock.rotate(true);
						}
						break;
					case 'l': // rotera medurs
						currentBlock.rotate(true);
						if(collided()){
							currentBlock.rotate(false);
						}
						break;
					case 'w':
						for(int x = 0; x < WIDTH; ++x){
							for(int y = 0; y < HEIGHT; ++y){
								statMapBackup[y][x] = statMap[y][x];
							}
						}
						break;
					case 'b':
						for(int x = 0; x < WIDTH; ++x){
							for(int y = 0; y < HEIGHT; ++y){
								statMap[y][x] = statMapBackup[y][x];
							}
						}
						break;
					}
                    // SÅ
            }
            
           
            
            
            
            if(System.currentTimeMillis() - time.get() > sleep){
            	
            	if(isLost()){
            		return 2;
            	}
            	posY++;
            	if(collided()){// Touch down
            		posY--;
            		for(int y = 0; y < TetrizBlock.height; y++){
            			for(int x = 0; x < TetrizBlock.width; x++){
            				if(posX+x < WIDTH){
                				if((posY+y < statMap.length) && 
                				   (posX+x < statMap.length) && 
                				   (posX+x >= 0) && 
                				   statMap[posY+y][posX+x].equals(" ")){
                					statMap[posY+y][posX+x] = currentBlock.string[y][x];
                				}
            				}
            			}
            		}
        			  generateNewCurrentBlock();
        			  posX = WIDTH/2-TetrizBlock.width/2;
        			  posY = 0;
        		  } else {
        			  posY--;
        		  }
            	deleteRows();
              updateMap();
            	print();
            	time.set(System.currentTimeMillis());
            	posY++;
            }
            
            //här är ett spel slut. så.
   
            
            //lite så

        } // end while
    }
	
	private boolean collided(){
		for(int y = 0; y < TetrizBlock.height; y++){
			for(int x = 0; x < TetrizBlock.width; x++){
				if(!currentBlock.string[y][x].equals(" ")){
					if((y+posY >= 0) && (x+posX >= 0) && !statMap[y+posY][x+posX].equals(" ")){
						return true;
					}
				}
			}
		}
		return false;
	}

	private String[][] statMap;
	private String[][] statMapBackup;
	private String[][] map;
	
	private void generateMap(){
		statMapBackup = new String[HEIGHT][WIDTH];
		statMap       = new String[HEIGHT][WIDTH];
		map           = new String[HEIGHT][WIDTH];
		for(int y = 0; y < HEIGHT; ++y){
			for(int x = 0; x < WIDTH; ++x){
				if(y == HEIGHT-1){
					statMap[y][x] = "=";
				} else if(x == 0 || x == WIDTH-1){
					statMap[y][x] = "|";
				} else {
					statMap[y][x] = " ";					
				}
				
			}
		}
	}
	
	
	private void updateMap() {
		int y2 = 0;
		for(int y = 0; y < HEIGHT; ++y){
			for(int x = 0; x < WIDTH; ++x){
				map[y][x] = statMap[y][x];
				// Om vi är vid positionen där vi ska skriva ut block, skriv ut det
				if(y >= posY && y < (posY+TetrizBlock.height) &&  posX <= x && x < posX+TetrizBlock.width){
					for(int i = 0, i2 = posX < 0 ? 2 : 0; i2 < TetrizBlock.width; ++i, ++i2){
						if(i+x+1 < WIDTH && statMap[y][i+x].equals(" ")){
							map[y][i+x] = currentBlock.string[y2][i2];	
						}
					}
					y2++;
					
					x += TetrizBlock.width;
				}
			}
			
		}
		
	}
	private boolean isLost(){
		return collided() && posY < 2;
	}
	
	private void deleteRows(){
		int amountOfRows = 0;
		for(int y = 0; y < HEIGHT-1; y++){
			for(int x = 0; x < WIDTH; ++x){
				if(statMap[y][x].equals(" ")){
					break;
				}else if(x == WIDTH-1){
					rotate(y);
					amountOfRows++;
				}
			}
		}
		score += getScore(amountOfRows/2);
		checkLevel(amountOfRows/2);
	}
	
	private void checkLevel(int amountOfRows){
		levelCounter += amountOfRows;
		if(levelCounter >= amountOfRowsToLevelUp){
			level++;
			sleep = (long) (SLEEP_ORIGINAL * Math.exp(-0.1*level)) + 20;
			levelCounter-= amountOfRowsToLevelUp;
		}
	}
	
	private int getScore(int amountOfRows){
	 	switch (amountOfRows) {
		case 1:
			return 40 * (level + 1);
		case 2:
			return 100 * (level + 1);
		case 3:
			return 300 * (level + 1);
		case 4:
			return 1200 * (level + 1);
		default:
			return 0;
		}
	}
	
	private void rotate(int row){
		for(int i = row; i > 0 ; --i){
			statMap[i] = statMap[i-1]; 
		}
		for(int x = 0; x < WIDTH; ++x){
			if(x == 0 || x == WIDTH-1){
				statMap[0][x] = "|";
			} else {
				statMap[0][x] = " ";					
			}
		}
			
	}
	
	private int random(){
		return random.nextInt(TetrizBlock.max()/4*100)/100;
	}

	private void generateNewCurrentBlock() {
		currentBlock = new TetrizBlock(nextBlock);
		nextBlock = TetrizBlock.generateNewBlock(random());
		magicString = magicBox();
	}
	
	
	private void print(String msg){
		try{
			System.out.print(msg == null ? " " : msg);
		} catch (NullPointerException e){
			e.printStackTrace();
			System.out.print("?");
		}
	}
	
	
	private String[][] magicBox(){
		String[][] magicString = new String[MAGICBOXHEIGHT][MAGICBOXWIDTH];
		
		int y2 = 0;
		for(int y = 0; y < MAGICBOXHEIGHT; ++y){
			for(int x = 0; x < MAGICBOXWIDTH; ++x){
				//------------
				if(y == 0 && (x == 0 || x == MAGICBOXWIDTH-1)){
					magicString[y][x] = ".";
				} else if(y == MAGICBOXHEIGHT-1 && x == 0){
					magicString[y][x] = "`";
				} else if(y == MAGICBOXHEIGHT-1 && x == MAGICBOXWIDTH-1){
					magicString[y][x] = "´";
				} else if(y == 0 || y == MAGICBOXHEIGHT-1){
					magicString[y][x] = "-";
				} else if(x == 0 || x == MAGICBOXWIDTH-1){
						magicString[y][x] = "|";
				} else if(y > 2 && y < MAGICBOXHEIGHT-3 && x == 5){
						//här kan vi skriva lite
						for(int x2 = 0; x2 < TetrizBlock.width; ++x2){
							magicString[y][x2+x] = nextBlock.string[y2][x2];
						}
						y2++;
						x += TetrizBlock.width;
				} else {
					magicString[y][x] = " ";
				}
				//------------
			} // x loopen slut
		} //y loopen slut
		
		return magicString;
	}
	
	private void print(){
		System.out.flush();
		
		//Disable terminal-haxxande
		try {
            stty( ttyConfig.trim() );
         }
         catch (Exception e) {
             System.err.println("Exception restoring tty config");
         }
		
		//skicka clear kommando
		try {
			Runtime.getRuntime().exec(new String[] {"clear"});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//enablea terminal-haxxande
		try {
			setTerminalToCBreak();
		} catch (IOException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			exec(new String[] {"clear"});
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		for (int i = 0; i < 25; i++) {
//			System.out.println("");
//		}
		
		int y2 = 0;
		for(int y = 0; y < HEIGHT; ++y){
			for(int x = 0; x < WIDTH; ++x){
				print(map[y][x]);
				
				if(y > 10 && y < 11+MAGICBOXHEIGHT && x == WIDTH-1){
					for(int x2 = 0; x2 < MAGICBOXWIDTH; ++x2){
						print(magicString[y2][x2]);
					}
					y2++;
				} else if(y == 13+MAGICBOXHEIGHT && x == WIDTH-1){
					print("    SCORE: " + score);
				} else if(y == 14+MAGICBOXHEIGHT && x == WIDTH-1){
					print("    LEVEL: " + level);
				}
				
			}
			print("\n");
		}
		
		
	}

}
