//package gameProject;
/*
 * Edward Yao
 * 5/20/22
 * DinoGunner.java
 * GAME PROJECT LETS GOOOO
 */
// final submission

/**
 * DINO GUNNER - A Chrome Dinosaur Game Spinoff
 * 
 * This is a Java-based game inspired by the classic Chrome browser dinosaur game.
 * The game features a dinosaur that can jump over obstacles, shoot enemies,
 * and collect power-ups through a shop system.
 * 
 * MAIN FEATURES:
 * - Endless runner gameplay with increasing difficulty
 * - Multiple weapon types (shotgun, plasma, railgun, minigun, laser)
 * - Power-up system (camouflage, bling, lifesaver, armor, jetpack)
 * - Enemy pterodactyls that drop rocks and bombs
 * - Shop system to buy and equip items
 * - High score tracking and persistence
 * - Sound effects and animations
 * 
 * GAME CONTROLS:
 * - SPACE: Jump
 * - MOUSE CLICK: Shoot weapon
 * - Various buttons for navigation between game panels
 */
import java.awt.*;  
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*; 
import javax.swing.event.*; 
import java.util.Scanner;

/**
 * Main game window class that extends JFrame
 * This is the primary container that holds all game panels and manages the overall game window
 */
public class DinoGunner extends JFrame{
	
	/**
	 * Constructor creates the main game window
	 * Sets up the window properties and creates the main panel that contains all game content
	 */
	public DinoGunner()
	{
		super("DINO GUNNER");
		MainPanel panel = new MainPanel(this);
		setSize(1000, 800);                    // Set window size to 1000x800 pixels
		setDefaultCloseOperation(EXIT_ON_CLOSE); // Close application when window is closed
		setLocation(1200, 100);                // Position window on screen
		setResizable(false);                    // Prevent window resizing
		setContentPane(panel);                  // Add the main panel to the window
		setVisible(true);                       // Make window visible
	}
	
	/**
	 * Main method - entry point of the application
	 * Creates a new instance of the game
	 */
	public static void main(String[] args) 
	{
		DinoGunner code = new DinoGunner();
	}
}

/**
 * MainPanel - The central navigation hub for the entire game
 * 
 * This panel uses CardLayout to manage different game screens:
 * - Home screen with animated elements
 * - Instructions screen
 * - Play screen (game menu and actual gameplay)
 * - High scores screen
 * 
 * The panel acts as a container that switches between different game states
 * using a card-based navigation system.
 */
class MainPanel extends JPanel 
{
	// Navigation buttons for main menu
	JButton play, instructions, scores;
	
	// Image assets for buttons (currently unused)
	Image playI, instructionsI, scoresI;
	
	// Layout spacing elements
	JLabel firstSpacer;
	JLabel[] spacers = new JLabel[4];
	
	// The four main game panels
	HomePanel hPanel = new HomePanel();        // Animated home screen
	InstructionsPanel iPanel = new InstructionsPanel(); // Game instructions
	PlayPanel pPanel;                          // Game menu and gameplay
	ScorePanel sPanel;                         // High scores display
	
	// Layout manager for switching between panels
	CardLayout cardLayout;
	
	// Back buttons to return to home from each panel
	JButton[] backButtons = new JButton[3];
	
	// Reference to main game frame
	DinoGunner frame;
	/**
	 * Constructor receives the main game frame and initializes the panel
	 * @param input Reference to the main DinoGunner frame
	 */
	public MainPanel(DinoGunner input)
	{
		frame = input;
		run();
	}
	
	/**
	 * Paint method - currently empty as this panel only manages other panels
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
	
	/**
	 * Main initialization method that sets up the CardLayout system
	 * Creates all navigation buttons and adds the four main game panels
	 * Sets up action listeners for navigation between different game states
	 */
	public void run()
	{
		cardLayout = new CardLayout();
		setLayout(cardLayout);
		
		//creating three main buttons
		play = new JButton("PLAY");
		instructions = new JButton("INSTRUCTIONS");
		scores = new JButton("HIGH SCORES");
		play.setPreferredSize(new Dimension(200, 70));
		instructions.setPreferredSize(new Dimension(200, 70));
		scores.setPreferredSize(new Dimension(200, 70));
		
		//creating the game name display
		firstSpacer = new JLabel("");
		firstSpacer.setPreferredSize(new Dimension(520, 125));
		
		//creating JLabels to act as spacers
		for(int i=0; i<4; i++)
		{
			spacers[i] = new JLabel("");
			spacers[i].setPreferredSize(new Dimension(1000, 60));
		}
		//creating back buttons to return to the home panel
		for(int i=0; i<3; i++)
		{
			backButtons[i] = new JButton("BACK TO HOME");
			backButtons[i].addActionListener(e -> cardLayout.show(this, "hPanel"));
		}
		
		add(hPanel, "hPanel"); // adding the home panel to the cardLayout panel
		
		//adding elements to their respective panels
		hPanel.add(spacers[0]);
		hPanel.add(firstSpacer);
		hPanel.add(spacers[1]);
		hPanel.add(play);
		hPanel.add(spacers[2]);
		hPanel.add(instructions);
		hPanel.add(spacers[3]);
		hPanel.add(scores);
		
		iPanel.add(backButtons[0]);
		pPanel = new PlayPanel(frame, backButtons[1]);
		sPanel = new ScorePanel(backButtons[2]);
		
		//adding the panels to the cardLayout panel
		add(iPanel, "iPanel");
		add(pPanel, "pPanel");
		add(sPanel, "sPanel");

		cardLayout.show(this, "hPanel"); // home panel is shown by default
		//adding action listeners to the navigation buttons
		instructions.addActionListener(e -> cardLayout.show(this, "iPanel"));
		play.addActionListener(e -> cardLayout.show(this, "pPanel"));
		ScoreListener scorelistener = new ScoreListener(this);
		scores.addActionListener(scorelistener);
		
	}
	/**
	 * ScoreListener - Handles navigation to the high scores panel
	 * 
	 * When the scores button is clicked, this listener:
	 * 1. Switches the view to the scores panel
	 * 2. Updates the scores display with current data
	 */
	class ScoreListener implements ActionListener
	{
		MainPanel panel;
		
		/**
		 * Constructor receives reference to the main panel
		 * @param panelInput Reference to the MainPanel instance
		 */
		public ScoreListener(MainPanel panelInput)
		{
			panel = panelInput;
		}
		
		/**
		 * Action performed when scores button is clicked
		 * Shows the scores panel and refreshes the score data
		 */
		public void actionPerformed(ActionEvent e)
		{
			cardLayout.show(panel, "sPanel");
			sPanel.update();
		}
	}
}
/**
 * HomePanel - The animated home screen of the game
 * 
 * This panel displays an engaging animated scene featuring:
 * - Scrolling clouds in the background
 * - Animated dinosaur running in place
 * - Flying pterodactyl with flapping wings
 * - Cactus obstacle
 * - Firing weapon animation
 * - Game title
 * 
 * The animations are controlled by timers that update positions and states
 * to create a dynamic, engaging home screen.
 */
class HomePanel extends JPanel 
{
	// Background and decorative images
	Image cloud = new ImageIcon("cloud.png").getImage();
	Image ground = new ImageIcon("ground.png").getImage();
	Image title = new ImageIcon("title.png").getImage();
	
	// Animated character and object images
	Image dino1, dino2, ptero1, ptero2, cactus, minigun1, minigun2, armor;
	
	// Animation timers for different elements
	Timer pterotimer, guntimer, cloudTimer;
	
	// Animation state variables
	int pteroX = 850;        // Pterodactyl X position
	int cloudX = 1000;       // Cloud X position for scrolling
	int wingsupdown = 35;    // Wing flapping animation counter
	boolean shooting = true;  // Weapon firing animation state
	/**
	 * Constructor initializes the home panel and starts animations
	 */
	public HomePanel()
	{
		run();
	}
	
	/**
	 * Paint method draws all animated elements on the home screen
	 * Loads images dynamically and renders them based on animation states
	 * @param g Graphics context for drawing
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		// Load animation frame images
		dino1 = new ImageIcon("dinoRunning1.png").getImage();
		dino2 = new ImageIcon("dinoRunning2.png").getImage();
		ptero1 = new ImageIcon("ptero1.png").getImage();
		ptero2 = new ImageIcon("ptero2.png").getImage();
		cactus = new ImageIcon("cactus4.png").getImage();
		minigun1 = new ImageIcon("minigun.png").getImage();
		minigun2 = new ImageIcon("minigunS.png").getImage();
		armor = new ImageIcon("armor.png").getImage();
		
		g.drawImage(cactus, 700, 500, null); 
		g.drawImage(cloud, cloudX, 50, null);
		for(int i=0; i<2; i++)g.drawImage(ground, i*500, 400, null);
		if(wingsupdown > 0)
		{
			g.drawImage(ptero1, pteroX, 150, null);
			g.drawImage(dino1, -10, 500, null);
		}
		else 
		{
			g.drawImage(ptero2, pteroX, 150, null);
			g.drawImage(dino2, -10, 500, null);
		}
		g.drawImage(armor, -10, 500, null);
		if(shooting)g.drawImage(minigun1, -10, 500, null);
		else g.drawImage(minigun2, -10, 500, null);
		
		g.drawImage(title, 250, 100, 500, 100, null);
	}
	/**
	 * Initializes and starts all animation timers
	 * Creates three separate timers for different animation elements:
	 * - Pterodactyl movement and wing flapping (10ms intervals)
	 * - Gun firing animation (30ms intervals)  
	 * - Cloud scrolling (13ms intervals)
	 */
	public void run()
	{
		PteroMover pteromover = new PteroMover();
		pterotimer = new Timer(10, pteromover);
		pterotimer.start();
		GunShooter gunshooter = new GunShooter();
		guntimer = new Timer(30, gunshooter);
		guntimer.start();
		CloudMover cloudmover = new CloudMover();
		cloudTimer = new Timer(13, cloudmover);
		cloudTimer.start();
	}
	/**
	 * CloudMover - Handles cloud scrolling animation
	 * 
	 * Moves clouds slowly across the sky from right to left
	 * When clouds reach the left edge, they reset to the right side
	 */
	class CloudMover implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			cloudX--;                           // Move cloud left by 1 pixel
			if(cloudX <= -460)cloudX = 1000;   // Reset to right side when off-screen
		}
	}
	/**
	 * GunShooter - Handles weapon firing animation
	 * 
	 * Toggles between two weapon images to create a firing effect
	 * Switches every 30ms to create a rapid firing animation
	 */
	class GunShooter implements ActionListener 
	{
		public void actionPerformed(ActionEvent e)
		{
			shooting = !shooting;  // Toggle between firing and idle states
			repaint();             // Redraw the panel to show animation
		}
	}
	/**
	 * PteroMover - Handles pterodactyl movement and animation
	 * 
	 * Moves the pterodactyl left across the screen at 3 pixels per frame
	 * Controls wing flapping animation by switching images every 35 frames
	 * Resets position when pterodactyl goes off-screen
	 */
	class PteroMover implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			pteroX-=3;                           // Move pterodactyl left
			wingsupdown--;                       // Decrement wing animation counter
			if(wingsupdown == -35)wingsupdown = 35; // Reset wing animation cycle
			if(pteroX < -210) pteroX = 1000;    // Reset to right side when off-screen
			repaint();                           // Redraw to show movement
		}
	}
}
/**
 * InstructionsPanel - Displays game instructions to the player
 * 
 * This panel shows a static image containing all the game rules and controls
 * The back button is positioned in the top right corner using layout spacing
 */
class InstructionsPanel extends JPanel 
{
	// Instructions image loaded from file
	Image instructions = new ImageIcon("instructions.png").getImage();
	Image background;
	
	/**
	 * Constructor initializes the instructions panel
	 */
	public InstructionsPanel()
	{
		run();
	}
	
	/**
	 * Paint method displays the instructions image
	 * @param g Graphics context for drawing
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		g.drawImage(instructions, 50, 50, null);  // Draw instructions at position (50,50)
	}
	
	/**
	 * Sets up the panel layout with proper spacing for the back button
	 * Adds a spacer to position the back button in the top right corner
	 */
	public void run()
	{
		JLabel spacer = new JLabel("");
		spacer.setPreferredSize(new Dimension(750, 1));  // Wide spacer for button positioning
		add(spacer);
	}
}
/**
 * ScorePanel - Displays and manages high scores
 * 
 * This panel reads scores from a text file, sorts them in descending order,
 * and displays the top 10 scores in a formatted text area.
 * 
 * FEATURES:
 * - Reads scores from "scores.txt" file
 * - Sorts scores using bubble sort algorithm
 * - Displays top 10 scores with proper formatting
 * - Updates automatically when opened
 * - Handles file I/O with error checking
 */
class ScorePanel extends JPanel 
{
	// File reading and data storage
	Scanner scan;                    // Scanner for reading score file
	JTextArea scoreboard;            // Text area to display scores
	JButton backButton;              // Button to return to main menu
	
	// Layout and display elements
	JLabel topSpacer, boardSpacer;   // Spacing elements for layout
	Image dino;                      // Dinosaur image (currently unused)
	String scoreString;              // Formatted string of all scores
	Font font;                       // Font for score display
	
	// Score data arrays
	String[] names;                  // Array of player names
	int entries;                     // Number of score entries
	int [] scores;                   // Array of player scores
	/**
	 * Constructor sets up the score panel with a back button
	 * @param back Button to return to main menu
	 */
	public ScorePanel(JButton back)
	{
		backButton = back;
		setBackground(Color.WHITE);  // Set white background
		run();
	}
	
	/**
	 * Paint method - currently empty as this panel uses Swing components
	 * @param g Graphics context (unused)
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
	
	/**
	 * Main initialization method that sets up the score display
	 * Reads scores from file, sorts them, and creates the formatted display
	 */
	public void run()
	{
		scoreboard = new JTextArea();
		scoreboard.setPreferredSize(new Dimension(800, 600));
		topSpacer = new JLabel("");
		topSpacer.setPreferredSize(new Dimension(750, 1));
		boardSpacer = new JLabel("");
		boardSpacer.setPreferredSize(new Dimension(1000, 50));
		entries = countEntries();
		names = new String[entries];
		scores = new int[entries];
		getScores();
		sortScores();
		font = new Font(Font.MONOSPACED, Font.BOLD, 30);
		scoreboard.setFont(font);

		scoreString = "TOP 10 SCORES:\n\n";
		for(int i=0; i<Math.min(entries, 10); i++)
		{
			String spaces = spaceStringMaker(names[i]);
			scoreString += (i+1) + ") " + names[i].trim() + spaces + scores[i] + "\n";
		}
		
		scoreboard.setText(scoreString);
		scoreboard.setEditable(false);
		
		add(topSpacer);
		add(backButton);
		add(boardSpacer);
		add(scoreboard);
	}
	public void update()
	//updates the score textArea
	{
		entries = countEntries();
		getScores();
		sortScores();
		scoreString = "TOP 10 SCORES:\n\n";
		for(int i=0; i<Math.min(entries, 10); i++)
		{
			String spaces = spaceStringMaker(names[i]);
			scoreString += (i+1) + ") " + names[i].trim() + spaces + scores[i] + "\n";
		}
		scoreboard.setText(scoreString);
	}
	public String spaceStringMaker(String name)
	// simply adds creates a string with the right amount of spaces, to space all the scores correctly
	{
		int spaceCount = 35-name.trim().length();
		String spaces = "";
		for(int i=0; i<spaceCount; i++) spaces += ' ';
		return spaces;
	}
	public void sortScores()
	//sorts the scores using bubble sort
	{
		int tempScore;
		String tempString;
		boolean sorted = false;
		while(!sorted) 
		{
			sorted = true;
			for(int i=0; i<scores.length-1; i++) 
			{
				if(scores[i]<scores[i+1]) 
				// this if detects if the array actually isn't sorted (in descending order)
				{
					tempScore = scores[i];
					scores[i] = scores[i+1];
					scores[i+1] = tempScore;
					//the string array will be sorted along with the int array, so the names are still matched with their scores
					tempString = names[i];
					names[i] = names[i+1];
					names[i+1] = tempString;
					
					sorted = false;
				}
				//if the if block isn't entered(meaning the array is sorted) sorted will stay as true, breaking out of the while
			}
		}
	}
	public void getScores()
	// reads the names and scores from the text file, and stores them in string and int arrays
	{
		getScanner();
		names = new String[entries];
		scores = new int[entries];
		for(int i=0; i<entries; i++)
		{
			names[i] = scan.nextLine();
			scores[i] = Integer.parseInt(scan.nextLine());
			//System.out.println(names[i] + " SCORE:" + scores[i]);
		}
	}
	public int countEntries()
	//counts the number of lines in the text file, then returns that divided by two
	//each entry will consume two lines, one for the name and one for the score
	{
		int lines = 0;
		getScanner();
		while(scan.hasNextLine())
		{
			lines++;
			scan.nextLine();
		}
		return lines/2;
	}
	public void getScanner()// sets scan to the start of scores.txt with a try-catch block
	{
		try 
		{
			File scorefile = new File("scores.txt");
			scan = new Scanner(scorefile);
		} 
		catch (FileNotFoundException e) {}
	}
}
/**
 * PlayPanel - The main gameplay hub that manages different game states
 * 
 * This panel uses CardLayout to switch between three main states:
 * 1. MenuPlayPanel - Game menu with play/shop options and dino preview
 * 2. Shop - Weapon and gear purchasing/equipping system
 * 3. GamePlayPanel - Actual gameplay with running, jumping, and combat
 * 
 * The panel acts as a container that coordinates between the menu,
 * shop, and actual gameplay, allowing seamless transitions.
 */
class PlayPanel extends JPanel
{
	// Navigation and control buttons
	JButton play, quit, shop, back;
	
	// The three main game panels
	GamePlayPanel gPP;              // Actual gameplay panel
	MenuPlayPanel mPP;              // Game menu panel
	JLabel menuSpacer1, menuSpacer2, menuSpacer3;  // Layout spacing
	
	// Shop system and layout management
	Shop sPP;                       // Shop panel for buying/equipping items
	CardLayout cardlayout;          // Layout manager for switching between panels
	DinoGunner frame;               // Reference to main game frame
	JButton backButton;             // Button to return to main menu
	/**
	 * Constructor sets up the play panel with CardLayout
	 * @param input Reference to main game frame
	 * @param button Back button from main menu
	 */
	public PlayPanel(DinoGunner input, JButton button)
	{
		setBackground(Color.YELLOW);           // Set yellow background
		cardlayout = new CardLayout();        // Use CardLayout for panel switching
		setLayout(cardlayout);
		frame = input;
		backButton = button;
		run();
	}
	
	/**
	 * Paint method - empty as this panel only contains other panels
	 * @param g Graphics context (unused)
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
	
	/**
	 * Main initialization method that creates all three game panels
	 * Sets up the menu, shop, and gameplay panels with proper navigation
	 */
	public void run()
	{
		sPP = new Shop();
		add(sPP, "sPP");
		mPP = new MenuPlayPanel(sPP);
		add(mPP, "mPP");
		gPP = new GamePlayPanel(frame, sPP);
		add(gPP, "gPP");
		play = new JButton("RUN!");
		quit = new JButton("QUIT");
		shop = new JButton("SHOP");
		back = new JButton("BACK");
		menuSpacer1 = new JLabel("");
		menuSpacer1.setPreferredSize(new Dimension(750, 1));
		menuSpacer2 = new JLabel("");
		menuSpacer2.setPreferredSize(new Dimension(1000, 300));
		menuSpacer3 = new JLabel("");
		menuSpacer3.setPreferredSize(new Dimension(1000, 60));

		play.setPreferredSize(new Dimension(200, 60));
		shop.setPreferredSize(new Dimension(200, 60));
		StopListener stopListener = new StopListener(this);
		quit.addActionListener(stopListener);
		shopToMenuListener toMenu = new shopToMenuListener(this);
		back.addActionListener(toMenu);
		shop.addActionListener(e -> cardlayout.show(this, "sPP"));
		PlayListener playlistener = new PlayListener(this);
		play.addActionListener(playlistener);
		gPP.add(quit);
		sPP.topPanel.add(back);
		mPP.add(menuSpacer1);
		mPP.add(backButton);
		mPP.add(menuSpacer2);
		mPP.add(play);
		mPP.add(menuSpacer3);
		mPP.add(shop);
		cardlayout.show(this, "mPP");
	}
	class shopToMenuListener implements ActionListener
	{
		PlayPanel panel;
		public shopToMenuListener(PlayPanel input)
		//receives the cardlayout panel
		{
			panel = input;
		}
		public void actionPerformed(ActionEvent e)
		//shows menu panel, and repaint it 
		{
			cardlayout.show(panel, "mPP");
			mPP.repaint();
		}
	}
	class PlayListener implements ActionListener// PlayListener calls the gameplay panel when the start button is pressed, starting the game loop
	{
		PlayPanel panel;
		public PlayListener(PlayPanel input)
		//receives cardlayout panel
		{
			panel = input;
		}
		public void actionPerformed(ActionEvent e) 
		//shows gameplay panel and starts gameplay
		{
			cardlayout.show(panel, "gPP");
			gPP.run();
		}
	}
	class StopListener implements ActionListener// PlayListener calls the gameplay panel when the start button is pressed, starting the game loop
	{
		PlayPanel panel;
		public StopListener(PlayPanel input)
		//recieves the cardlayout panel
		{
			panel = input;
		}
		public void actionPerformed(ActionEvent e) 
		//shows the menu and closes the gameplay panel
		{
			cardlayout.show(panel, "mPP");
			gPP.close();
		}
	}
}
class MenuPlayPanel extends JPanel
// holds the play and shop buttons, as well as back to home button. 
//shows a preview of your dino, before it goes to run.
{
	Shop shop;
	Image ground = new ImageIcon("ground.png").getImage();
	Image dino = new ImageIcon("dino.png").getImage();

	//same image arrays as dino. 
	Image[] weaponImages = {
			new ImageIcon("shotgun.png").getImage(),
			new ImageIcon("plasma.png").getImage(),
			new ImageIcon("railgun.png").getImage(),
			new ImageIcon("minigun.png").getImage(),
			new ImageIcon("laser.png").getImage()
	};
	Image[] gearImages = {
			new ImageIcon("camouflage.png").getImage(),
			new ImageIcon("bling.png").getImage(),
			new ImageIcon("lifesaver.png").getImage(),
			new ImageIcon("armor.png").getImage(),
			new ImageIcon("jets.png").getImage()
	};
	
	public MenuPlayPanel(Shop shopInput)
	//constructor recieves the panel that it is contained in
	{
		setBackground(Color.WHITE);
		shop = shopInput;
	}
	public void paintComponent(Graphics g)
	//shows a preview of the dino, before you start the game
	{
		super.paintComponent(g);
		for(int i=0; i<2; i++)g.drawImage(ground, i*500, 400, null);
		g.drawImage(dino, 50, 595, dino.getWidth(null)/2, dino.getHeight(null)/2, null);
		if(shop.equippedGear != -1)g.drawImage(gearImages[shop.equippedGear-5], 50, 595, dino.getWidth(null)/2, dino.getHeight(null)/2, null);
		if(shop.equippedWeapon != -1)g.drawImage(weaponImages[shop.equippedWeapon], 50, 595, dino.getWidth(null)/2, dino.getHeight(null)/2, null);
	}
}
/**
 * GamePlayPanel - The core gameplay engine where the actual game happens
 * 
 * This panel implements the main game loop and handles:
 * - Player character (dinosaur) movement and controls
 * - Obstacle generation and movement (cacti, pterodactyls)
 * - Combat system (shooting, enemy attacks)
 * - Collision detection and damage
 * - Score tracking and game over conditions
 * - Visual rendering of all game elements
 * 
 * The panel runs at 100 FPS (10ms timer) and manages all game state updates.
 * It implements multiple listeners for keyboard, mouse, and timer events.
 */
class GamePlayPanel extends JPanel implements ActionListener, MouseListener, KeyListener 
{
	// Game objects and characters
	Cactus cact;                    // Obstacle that ends game on collision
	Dino dino;                      // Player character
	DinoProjectile[] projectiles;   // Array of player's fired projectiles
	PteroRock[] rocks;              // Rocks dropped by pterodactyls
	PteroBomb[] bombs;              // Bombs dropped by pterodactyls
	Ptero[] pteros;                 // Array of enemy pterodactyls
	
	// Game system components
	Timer gameTimer;                 // Main game loop timer (10ms intervals)
	DinoGunner frame;                // Reference to main game frame
	Shop shop;                       // Shop system for equipped items
	
	// Visual elements
	Image ground;                    // Scrolling ground texture
	Image cloud;                     // Scrolling cloud texture

	// UI elements for score saving
	JButton saveButton;              // Button to save high score
	JLabel centerSpacer, fieldSpacer; // Layout spacing elements
	JLabel nameLabel;                // Label for name input
	JTextField nameField;            // Text field for player name
	
	// Game state and animation variables
	int groundstart = 0;             // Ground scrolling position
	int cloudX = 1000;               // Cloud scrolling position
	boolean gameOver = false;        // Game over state flag
	int runningSpeed = 9;            // Base movement speed
	double pteroSpawn;               // Pterodactyl spawn timer
	double initialSpawnRate;         // Initial spawn rate for enemies
	
	// Game statistics and progression
	double score;                    // Current score (increases over time)
	int kills;                       // Number of enemies defeated
	int fuel;                        // Jetpack fuel (if equipped)
	int earnedCredits;               // Credits earned from gameplay
	int maxFuel = 1500;              // Maximum fuel capacity
	
	public GamePlayPanel(DinoGunner input, Shop shopInput)// does NOT call run, run is called by the start button in PlayPanel
	{
		setBackground(Color.WHITE);
		ground = new ImageIcon("ground.png").getImage();
		cloud = new ImageIcon("cloud.png").getImage();
		frame = input;
		score = 0;
		rocks = new PteroRock[25];
		bombs = new PteroBomb[5];
		gameTimer = new Timer(10, this);//game timer, each frame is 10 milliseconds long
		centerSpacer = new JLabel("");
		centerSpacer.setPreferredSize(new Dimension(1000, 360));
		fieldSpacer = new JLabel("");
		fieldSpacer.setPreferredSize(new Dimension(120, 1));
		nameLabel = new JLabel("ENTER YOUR NAME TO SAVE SCORE", JLabel.CENTER);
		nameLabel.setPreferredSize(new Dimension(1000, 20));
		nameField = new JTextField();
		nameField.setPreferredSize(new Dimension(230, 25));
		saveButton = new JButton("SAVE SCORE");
		SaveListener scoresaver = new SaveListener();
		saveButton.addActionListener(scoresaver);
		shop = shopInput;
	}
	public void paintComponent(Graphics g)// drawing the game elements. this includes hitboxes for debugging and game over elements 
	{
		super.paintComponent(g);
		
		//The rectangles display the hitboxes of collidable game elements, this is temporary and will not be in the actual game
		/*g.setColor(Color.GREEN);
		g.drawRect(cact.x, (int)(cact.y+(cact.height*0.3)), cact.width, (int)(cact.height*0.7));
		g.setColor(Color.BLUE);
		g.drawRect(dino.x+15, dino.y, dino.width-45, dino.height);
		g.setColor(Color.RED);
		*/
		
		for(int i=0; i<3; i++) g.drawImage(ground, (500*i)+groundstart, 400, null);
		g.drawImage(cloud, cloudX, 50, cloud.getWidth(null)/2, cloud.getHeight(null)/2, null);
		g.drawImage(cact.picture, cact.x, cact.y, cact.width, cact.height, null);
		g.drawImage(dino.picture, dino.x, dino.y, dino.width, dino.height, null);
		
		for(int i=0; i<1000; i++) {
			if(projectiles[i] != null) 
			g.drawImage(projectiles[i].picture, projectiles[i].x, projectiles[i].y, null);
		}
		
		if(gameOver)
		{
			g.drawImage(dino.dead, dino.x, dino.y, dino.width, dino.height, null);
		}

		if(dino.gearID != 2 || dino.hasSaver == true)g.drawImage(dino.gear, dino.x, dino.y, dino.width, dino.height, null);
		g.drawImage(dino.weapon, dino.x, dino.y, dino.width, dino.height, null);
		
		g.setColor(Color.ORANGE);
		for(int i=0; i<pteros.length; i++)
		{
			if(pteros[i] != null)
			{
				//g.drawRect(pteros[i].x+10, pteros[i].y+25, pteros[i].width-20, (int)(pteros[i].height*0.55));
				
				g.drawImage(pteros[i].picture, pteros[i].x, pteros[i].y, pteros[i].width, pteros[i].height, null);
				
			}
		}
		g.setColor(Color.CYAN);
		for(int i=0; i<rocks.length; i++)
		{
			if(rocks[i] != null)
			{
				//g.drawRect(rocks[i].x, rocks[i].y, rocks[i].width, rocks[i].height);
				
				g.drawImage(rocks[i].picture, rocks[i].x, rocks[i].y, rocks[i].width, rocks[i].height, null);
			}
		}
		g.setColor(Color.MAGENTA);
		for(int i=0; i<bombs.length; i++)
		{
			if(bombs[i] != null)
			{
				//g.drawRect(bombs[i].x, bombs[i].y, bombs[i].width, bombs[i].height);
				
				g.drawImage(bombs[i].picture, bombs[i].x, bombs[i].y, bombs[i].width, bombs[i].height, null);
			}
		}
		
		if(gameOver)
		{
			g.setColor(Color.WHITE);
			g.fillRect(385, 385, 350, 100);
			g.setColor(Color.BLACK);
			g.drawString("DINOCOINS EARNED: " + earnedCredits, 389, 400);
		}
		
		g.setColor(Color.BLACK);// dislpays the score and kills
		g.drawString("SCORE: " + (int)score + "\t\t\tKILLS: " + kills, 10, 30);
		
		
		//health bar
		g.drawString("HEALTH: " + Math.max(0, (int)dino.health), 10, 70);//this chunk of code is temporary; testing out jet pack and displaying the fuel bar.
		if(dino.health > 0)// if fuel is 0, cannot divide by zero.
		{
			double percentage = (double)100/dino.health;
			g.setColor(Color.RED);
		    ((Graphics2D)g).setStroke(new BasicStroke(2));
			g.fillRect(10,  75, (int)(200/percentage), 15);
		}
		g.setColor(Color.BLACK);
	    ((Graphics2D)g).setStroke(new BasicStroke(5));
		g.drawRect(10,  75,  200,  15);
		
		if(shop.equippedGear == 9)//drawing fuel bar
		{
			g.drawString("FUEL: ", 10, 130);//this chunk of code is temporary; testing out jet pack and displaying the fuel bar.
			if(fuel > 0)// if fuel is 0, cannot divide by zero.
			{
				double percentage = (double)maxFuel/fuel;
				g.setColor(Color.ORANGE);
			    ((Graphics2D)g).setStroke(new BasicStroke(2));
				g.fillRect(10,  135, (int)(200/percentage), 15);
			}
			g.setColor(Color.BLACK);
		    ((Graphics2D)g).setStroke(new BasicStroke(5));
			g.drawRect(10,  135,  200,  15);
			if(fuel <= 0){
				g.setColor(Color.RED);
				g.drawString("OUT OF FUEL", 50, 130);
			}
		}
	}
	public void run()// sets listeners to this and creates new dino and its projectiles. 
	//also creates new cactus and pteros, as well as starting the game timer
	//ALSO removes the score-saving system from the panel
	{
		addMouseListener(this);
		addKeyListener(this);
		grabFocus();
		remove(centerSpacer);
		remove(nameLabel);
		remove(fieldSpacer);
		remove(nameField);
		remove(saveButton);
		saveButton.setText("SAVE SCORE");
		saveButton.setEnabled(true);
		nameField.setEnabled(true);
		nameField.setText("");
		cact = new Cactus();
		pteros = new Ptero[10];
		dino = new Dino(cact, pteros, this, shop.equippedWeapon, shop.equippedGear-5);
		projectiles = new DinoProjectile[1000];
		rocks = new PteroRock[100];
		bombs = new PteroBomb[5];
		gameOver = false;
		score = 0;
		kills = 0;
		earnedCredits = 0;
		fuel = maxFuel;
		initialSpawnRate = -10;
		pteroSpawn = initialSpawnRate;
		groundstart = 0;
		cloudX = 1000;
		gameTimer.start();
	}
	class SaveListener implements ActionListener
	//operation of the save score button
	{
		public void actionPerformed(ActionEvent e)
		{
			if(nameField.getText().length() == 0)
			// there is no name entered, tells user to enter a name
			{
				saveButton.setText("PLEASE ENTER A NAME");
			}
			else if(nameField.getText().length() >= 26)
			// name has been entered, but is too long to fit
			{
				saveButton.setText("NAME TOO LONG (MAX 26)");
				nameField.setText("");
			}
			else
			// this means that the user entered a valid , and the score should be saved.
			{
				try 
				{
					//the score will be saved, and the button updated to make sure the score isn't saved multiple times
					printMethod(nameField.getText(), (int)score);
					saveButton.setText("SCORE SAVED!");
					//the field and button are disabled to make sure the score isn't saved more than once
					saveButton.setEnabled(false);
					nameField.setEnabled(false);
				} 
				catch (IOException e2) 
				{
					System.out.println("wtf");
				}
			}
		}

	}
	public void printMethod(String name, int score) throws IOException
	//uses filewriter to append to the score file, storing the score that the user just got
	{
		File outfile = new File("scores.txt");
		PrintWriter writer;
		try
		{
			writer = new PrintWriter(new FileWriter(outfile, true));
			writer.println(name);
			writer.println(score);
			writer.close();
		}
		catch(FileNotFoundException e)
		{
			System.err.println("uh oh");
		}
	}
	public void stop()// stops the game and updates shop, called when the dino dies
	//also adds the score-saving system to the panel, allowing users to save their scores
	{
		add(centerSpacer);
		add(nameLabel);
		add(fieldSpacer);
		add(nameField);
		add(saveButton);
		earnedCredits += score/10;
		earnedCredits += kills*20;
		if(dino.gearID == 1)earnedCredits *= 1.5;
		shop.credits += earnedCredits;
		shop.update();
		gameTimer.stop();
		dino.isJumping = false;
		gameOver = true;
	}
	public void close()// similar to stop(), but only stops the game.
	{
		gameTimer.stop();
		dino.isJumping = false;
		gameOver = true;
	}
	public void shoot()//this method shoots the dino's projectiles
	{
		for(int i=0; i<1000; i++)
		{
			if(projectiles[i] == null)
			{
				projectiles[i] = new DinoProjectile(dino.weaponID, dino.y, this, pteros);
				break;
			}
		}
	}
	public void shootRock(int x, int y)// this method shoots the ptero's rocks. the Rock array belongs to this panel,
	// so that even after a ptero dies, the rock is still able to fall and hit things.
	{
		for(int i=0; i<rocks.length; i++) 
		{
			if(rocks[i]== null) 
			{
				int distance = x - dino.x;
				distance += (int)((Math.random()*401)-200);
				int height = 680 - y;
				height /= 12;
				if(dino.gearID == 0)distance = (int)(Math.random()*500);
				rocks[i] = new PteroRock(this, dino, x, y, distance/height);
				break;
			}
		}
	}
	public void shootBomb(int x, int y)// this method shoots a bomb from the ptero. same logic as shootRock.
	{
		for(int i=0; i<bombs.length; i++) 
		{
			if(bombs[i]== null) 
			{
				bombs[i] = new PteroBomb(this, dino, x, y);
				break;
			}
		}
	}
	public void remove(PteroRock input) //overloaded remove methods remove rocks, bombs, dinoprojectiles.
	{
		for(int i=0; i<rocks.length; i++) 
		{
			if(rocks[i]==input) 
			{
				rocks[i] = null;
				break;
			}
		}
	}
	public void remove(PteroBomb input)  //overloaded remove methods remove rocks, bombs, dinoprojectiles.
	{
		for(int i=0; i<bombs.length; i++) 
		{
			if(bombs[i]==input) 
			{
				bombs[i] = null;
				break;
			}
		}
	}
	public void remove(DinoProjectile input)  //overloaded remove methods remove rocks, bombs, dinoprojectiles.
	{
		for(int i=0; i<projectiles.length; i++) 
		{
			if(projectiles[i]==input) 
			{
				projectiles[i] = null;
				break;
			}
		}
	}
	public void spawnPtero() // spawns a ptero 
	{
		for(int i=0; i<1000; i++)
		{
			if(pteros[i] == null)
			{
				pteros[i] = new Ptero(this, dino);
				break;
			}
		}
	}
	public void removePtero(Ptero input)// removes a ptero, when it dies or goes off the screen.
	{
		for(int i=0; i<pteros.length; i++) 
		{
			if(pteros[i]==input) 
			{
				if(pteros[i].health <= 0)kills++;
				pteros[i] = null;
				break;
			}
		}
	}
	public void actionPerformed(ActionEvent e)// this us runned every game tick, or 10 milliseconds.
	{
		score += runningSpeed/50.0;
		dino.drawShooting--;
		cact.move(runningSpeed);//calling the move methods of game elements
		dino.move();
		for(int i=0; i<pteros.length; i++)
		{
			if(pteros[i] != null)
			pteros[i].move();
		}
		for(int i=0; i<rocks.length; i++)
		{
			if(rocks[i] != null)
			rocks[i].move();
		}
		for(int i=0; i<bombs.length; i++)
		{
			if(bombs[i] != null)
			bombs[i].move();
		}
		if(Math.random() < pteroSpawn)
		{
			spawnPtero();
			pteroSpawn = initialSpawnRate;
		}
		else pteroSpawn += 0.1;
		for(int i=0; i<1000; i++) if(projectiles[i] != null) projectiles[i].move();
		groundstart -=runningSpeed;//moves the ground, to create the illusion of the dino running.
		cloudX -= 1;
		if(groundstart <= -500)groundstart +=  500;
		if(cloudX <= -200)cloudX = 1000+(int)(Math.random()*300);
		
		if(dino.gearID != 4);if(dino.isJumping && dino.y == 595) dino.velocity = 29;//this is normal jumping
		
		if(dino.gearID == 4)// this is with jetpack
		{
			if(dino.isJumping && dino.y == 595 && fuel <= 0) dino.velocity = 29;//when out of fuel, dino can fall back to default jumping.
			if(dino.isJumping && fuel > 0){ 
				if(dino.y == 595) dino.velocity = 10;//if the dino starts on the ground, it jumps, giving it an initial boost
				fuel--;
				dino.velocity += 2; 
				dino.velocity = Math.min(dino.velocity, 12);
			}
		}
		repaint();
	}
	public void keyPressed(KeyEvent e) {//if space is pressed, jumping is set to true
		int code = e.getKeyCode();
		if(code==KeyEvent.VK_SPACE)dino.isJumping = true;
	}
	public void keyReleased(KeyEvent e) {// if space is released, jumping is set to false

		int code = e.getKeyCode();
		if(code==KeyEvent.VK_SPACE)dino.isJumping = false;
	}
	public void mousePressed(MouseEvent e) { //mousePressed and mouseReleased methods will be used for shooting, still WIP
		dino.isShooting = true;
	}
	public void mouseReleased(MouseEvent e) {
		dino.isShooting = false;
	}
	// the other listener methods are not used in gameplay.
	public void keyTyped(KeyEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
/**
 * Shop - The in-game store for purchasing and equipping weapons and gear
 * 
 * This panel manages the economy system where players can:
 * - Buy weapons and gear using earned credits
 * - Equip/unequip purchased items
 * - See visual previews of all available items
 * - Manage their inventory and equipment
 * 
 * The shop displays items in a 5x2 grid layout with:
 * - 5 weapons (shotgun, plasma, railgun, minigun, laser)
 * - 5 gear items (camouflage, bling, lifesaver, armor, jets)
 * 
 * Each item has a price, description image, and can be bought/equipped.
 */
class Shop extends JPanel
{
	// Economy system
	int credits = 200;               // Starting credits for new players
	
	// UI layout components
	JPanel buyPanel;                 // Panel containing all shop items
	JPanel topPanel;                 // Top panel showing credits and coin icon
	JLabel creditsDisplay;           // Label showing current credit amount
	JLabel dinoCoins;                // Icon representing the currency
	
	// Layout managers
	BorderLayout borderLayout = new BorderLayout();
	GridLayout gridLayout = new GridLayout(5, 2, 5, 5);  // 5 rows, 2 columns with spacing
	
	// Shop item management
	JButton[] buyButtons = new JButton[10];     // Buttons for all 10 items
	ButtonListener[] listeners = new ButtonListener[10];  // Action listeners for buttons
	boolean[] isOwned = new boolean[10];        // Tracks which items are owned
	
	// Equipment state
	int equippedWeapon = -1;         // Currently equipped weapon (-1 = none)
	int equippedGear = -1;          // Currently equipped gear (-1 = none)
	
	// giant image arrays to store all weapons, gear, and their descriptions.
	Image[] weaponImages = 
		{
				new ImageIcon("shotgunIcon.png").getImage(),
				new ImageIcon("plasmaIcon.png").getImage(),
				new ImageIcon("railgunIcon.png").getImage(),
				new ImageIcon("minigunIcon.png").getImage(),
				new ImageIcon("laserIcon.png").getImage()
		};
	Image[] gearImages = 
		{
				new ImageIcon("camoIcon.png").getImage(),
				new ImageIcon("blingIcon.png").getImage(),
				new ImageIcon("lifesaverIcon.png").getImage(),
				new ImageIcon("armorIcon.png").getImage(),
				new ImageIcon("jetsIcon.png").getImage()
		};
	Image[] descripionImages =
		{
				new ImageIcon("shotgunD.png").getImage(),
				new ImageIcon("plasmaD.png").getImage(),
				new ImageIcon("railgunD.png").getImage(),
				new ImageIcon("minigunD.png").getImage(),
				new ImageIcon("laserD.png").getImage(),
				new ImageIcon("camoD.png").getImage(),
				new ImageIcon("blingD.png").getImage(),
				new ImageIcon("lifesaverD.png").getImage(),
				new ImageIcon("armorD.png").getImage(),
				new ImageIcon("jetsD.png").getImage(),
		};
	
	JLabel[] imageLabels = new JLabel[10];
	JLabel[] descriptions = new JLabel[10];
	int[] weaponPrices = {200, 200, 350, 350, 1000};
	int[] gearPrices = {150, 150, 250, 400, 400};
	JPanel[] itemPanel =  new JPanel[10];
	public Shop()// constructor makes the shop
	{
		topPanel = new JPanel();
		topPanel.setBackground(Color.GRAY);
		creditsDisplay = new JLabel(""+ credits);
		ImageIcon coinIcon = new ImageIcon("dinoCoins.png");
		dinoCoins = new JLabel();
		dinoCoins.setIcon(coinIcon);
		topPanel.add(creditsDisplay);
		topPanel.add(dinoCoins);
		setLayout(borderLayout);
		add(topPanel, BorderLayout.NORTH);
		buyPanel = new JPanel();
		buyPanel.setLayout(gridLayout);
		for(int i=0; i<5; i++)// This for loop makes all the weapons in the shop
		{
			ImageIcon icon = new ImageIcon(weaponImages[i]);//These 3 lines of code set the image of the imageLabel to the shop image
			imageLabels[i] = new JLabel();
			imageLabels[i].setIcon(icon);
			
			icon = new ImageIcon(descripionImages[i]);//These 3 lines of code add the image with description text.
			descriptions[i] = new JLabel();
			descriptions[i].setIcon(icon);
			
			buyButtons[i] = new JButton("BUY: "+weaponPrices[i]);//creating new buy button, and creating a listener for that button.
			if(weaponPrices[i] > credits && !isOwned[i])buyButtons[i].setForeground(Color.RED);
			listeners[i] = new ButtonListener(i, weaponPrices[i]);
			buyButtons[i].addActionListener(listeners[i]);
			
			itemPanel[i] = new JPanel();//adding everything to itemPanel
			itemPanel[i].setLayout(new BorderLayout());
			itemPanel[i].add(buyButtons[i], BorderLayout.WEST);
			itemPanel[i].add(imageLabels[i], BorderLayout.CENTER);
			itemPanel[i].add(descriptions[i], BorderLayout.EAST);
			itemPanel[i].setBackground(Color.white);
		}
		for(int i=0; i<5; i++)
		{
			ImageIcon icon = new ImageIcon(gearImages[i]);//These 3 lines of code set the image of the imageLabel to the shop image
			imageLabels[i+5] = new JLabel();
			imageLabels[i+5].setIcon(icon);
			
			icon = new ImageIcon(descripionImages[i+5]);//These 3 lines of code add the image with description text.
			descriptions[i+5] = new JLabel();
			descriptions[i+5].setIcon(icon);
			
			buyButtons[i+5] = new JButton("BUY: "+gearPrices[i]);//creating new buy button, and creating a listener for that button.
			if(gearPrices[i] > credits && !isOwned[i+5])buyButtons[i+5].setForeground(Color.RED);
			listeners[i+5] = new ButtonListener(i+5, gearPrices[i]);
			buyButtons[i+5].addActionListener(listeners[i+5]);
			
			itemPanel[i+5] = new JPanel();//adding everything to item Panel
			itemPanel[i+5].setLayout(new BorderLayout());
			itemPanel[i+5].add(buyButtons[i+5], BorderLayout.WEST);
			itemPanel[i+5].add(imageLabels[i+5], BorderLayout.CENTER);
			itemPanel[i+5].add(descriptions[i+5], BorderLayout.EAST);
			itemPanel[i+5].setBackground(Color.white);
		}
		for(int i=0; i<5; i++)// this loop adds the 10 itemPanels to their correct spots in the shop grid layout
		{
			buyPanel.add(itemPanel[i]);
			buyPanel.add(itemPanel[i+5]);
			buyButtons[i].setPreferredSize(new Dimension(100, 10));
			buyButtons[i+5].setPreferredSize(new Dimension(100, 10));
			buyButtons[i].setOpaque(true);
			buyButtons[i+5].setOpaque(true);
			isOwned[i] = false;
			isOwned[i+5] = false;
		}
		add(buyPanel, BorderLayout.CENTER);
	}
	public void paintComponent()//nothing
	{
		super.paintComponent(getGraphics());
	}
	public void update()
	//update() updates the look of the shop, changing the credits count and the red text, which shows if you can afford something
	{
		creditsDisplay.setText("" + credits);
		for(int i=0; i<5; i++)
		{
			buyButtons[i].setForeground(Color.BLACK);
			buyButtons[i+5].setForeground(Color.BLACK);
			if(weaponPrices[i] > credits && !isOwned[i])buyButtons[i].setForeground(Color.RED);
			if(gearPrices[i] > credits && !isOwned[i+5])buyButtons[i+5].setForeground(Color.RED);
		}
	}
	class ButtonListener implements ActionListener// ActionListener will make buttons take credits and equip stuff, or unequip stuff.
	{
		int shopID;
		int price;
		public ButtonListener(int input, int cost)//constructor gets shopID (to know what is being bought) and price (to know how many credits to remove)
		{
			shopID = input;
			price = cost;
		}
		public void actionPerformed(ActionEvent e)// Based on the status of the button that is pressed, 
		//it will behave differently.
		{
			if(!isOwned[shopID] && credits >= price)// if the item is not owned, it will
			//be bought and equipped, unequipping the currently equipped item. Only works if the player
			//has enough credits.
			{
				credits -= price;
				isOwned[shopID] = true;
				buyButtons[shopID].setText("EQUIPPED");
				buyButtons[shopID].setBackground(Color.green);
				if(shopID <= 4)
				{
					if( equippedWeapon != -1) { buyButtons[equippedWeapon].setText("OWNED");
					buyButtons[equippedWeapon].setBackground(Color.yellow);}
					equippedWeapon = shopID;
				}
				else if(shopID > 4)
				{
					if( equippedGear != -1) { buyButtons[equippedGear].setText("OWNED");
					buyButtons[equippedGear].setBackground(Color.yellow);}
					equippedGear = shopID;
				}
			}
			else if(isOwned[shopID]) // if the item is already owned, two things can happen:
			// if the item is not equipped, it will equip the item and unequip the currently equipped item
			// if the item is equipped, it will unequip the item and dino will not have any item.
			{
				if(shopID <= 4)
				{
					if(equippedWeapon != shopID)
					{
						buyButtons[shopID].setText("EQUIPPED");
						buyButtons[shopID].setBackground(Color.green);
						if(equippedWeapon != -1) { buyButtons[equippedWeapon].setText("OWNED");
						buyButtons[equippedWeapon].setBackground(Color.yellow);}
						equippedWeapon = shopID;
					}
					else if(equippedWeapon == shopID)
					{
						buyButtons[shopID].setText("OWNED");
						buyButtons[shopID].setBackground(Color.yellow);
						equippedWeapon = -1;
					}
				}
				else if(shopID > 4)
				{
					if(equippedGear != shopID)
					{
						buyButtons[shopID].setText("EQUIPPED");
						buyButtons[shopID].setBackground(Color.green);
						if(equippedGear != -1) { buyButtons[equippedGear].setText("OWNED");
						buyButtons[equippedGear].setBackground(Color.yellow);}
						equippedGear = shopID;
					}
					else if(equippedGear == shopID)
					{
						buyButtons[shopID].setText("OWNED");
						buyButtons[shopID].setBackground(Color.yellow);
						equippedGear = -1;
					}
				}
			}
			//changing the credits display
			update();
		}
	}
}
/**
 * Cactus - The primary obstacle that ends the game on collision
 * 
 * Cacti are the main obstacles that the dinosaur must jump over.
 * They spawn randomly from the right side of the screen and move left.
 * Each cactus has a random appearance (6 different cactus images).
 * 
 * COLLISION BEHAVIOR:
 * - Instant game over when dinosaur touches any part of the cactus
 * - Collision detection uses a reduced hitbox (70% of height) for fairness
 * - Respawns with random delay and random appearance when off-screen
 */
class Cactus
{
	// Physical properties
	int height, width, x, y;
	
	// Visual variety - 6 different cactus appearances
	Image[] cacti = {
			new ImageIcon("cactus1.png").getImage(),
			new ImageIcon("cactus2.png").getImage(),
			new ImageIcon("cactus3.png").getImage(),
			new ImageIcon("cactus4.png").getImage(),
			new ImageIcon("cactus5.png").getImage(),
			new ImageIcon("cactus6.png").getImage() 
	};
	Image picture;                   // Currently displayed cactus image
	public Cactus()// sets the cactus's picture to a random picture from the array, and sets the location so that all cacti are at the same y-level
	{
		picture = cacti[(int)(Math.random()*6)];
		height = picture.getHeight(null)/2;
		width = picture.getWidth(null)/2;
		x = 1000;
		y = 710-height;
	}
	public void move(int amount)// move is called every game tick, it moves the cactus left 8 pixels, to match the speed of the ground.
	{
		x-=amount;
		if(x < 0-width)// when the cactus leaves the screen, it does not immediately go back to the start, instead there is a random delay.
		{
			respawn();
		}
	}
	public void respawn()
	{
		//the cactus changes each time it reappears, giving the illusion of them being different cacti.
		x = 1000 + (int)(Math.random() * 2000);
		picture = cacti[(int)(Math.random()*6)];
		height = picture.getHeight(null)/2;
		width = picture.getWidth(null)/2;
		y = 710-height;
	}
}
class Ptero// Ptero will be the destroyable obstacle. It drops rocks and explosives, which deal damage to your dino.
{
	int height, width, x, y, velocity;
	Image image1 = new ImageIcon("ptero1.png").getImage();
	Image image2 = new ImageIcon("ptero2.png").getImage();
	Image picture;
	int flyingInt = 30;
	int health = 100;
	int damage = 20;
	double rockFireChance;
	double bombFireChance;
	double initialRockChance;
	Dino enemy;
	GamePlayPanel gPP;
	public Ptero(GamePlayPanel input, Dino enemyInput)
	//2 parameters: the ptero needs to know the gameplaypanel so it can be removed, and it also needs to know if the dino is using camo.
	{
		picture = image1;
		gPP = input;
		enemy = enemyInput;
		initialRockChance = -5;
		if(enemy.gearID == 0)initialRockChance = -8.5;
		rockFireChance = initialRockChance;
		bombFireChance = 0.005;
		width = image1.getWidth(null)/2;
		height = image1.getHeight(null)/2;
		velocity = (int)(Math.random()*3)+4;
		x = 1000;
		y = (int)(Math.random()*300) + 150;
	}
	public void move()// called every game tick, moves the pterodactyl and changes its images.
	{
		// these 4 lines of code
		if(health <= 0)gPP.removePtero(this);
		flyingInt--;
		if(flyingInt == -30) flyingInt = 30;
		picture = image1;
		if(flyingInt > 0)picture = image2;
		if(Math.random() <= rockFireChance) {gPP.shootRock(x+20, y); rockFireChance = initialRockChance;}
		if(x <= 250 && Math.random() <= bombFireChance)gPP.shootBomb(x+20, y);
		rockFireChance += 0.05;
		x -= velocity;if(x < 0-width)// when the ptero leaves the screen, it gets despawned
		{
			gPP.removePtero(this);
		}
	}
}
/**
 * Dino - The player character that the user controls
 * 
 * The dinosaur is the main character that can:
 * - Jump over obstacles using SPACE key
 * - Shoot weapons using mouse clicks
 * - Equip different weapons and gear from the shop
 * - Take damage from enemy attacks
 * - Use special abilities based on equipped gear
 * 
 * MOVEMENT SYSTEM:
 * - Gravity-based jumping with adjustable velocity
 * - Running animation with two alternating frames
 * - Jetpack boost when jets gear is equipped
 * 
 * COMBAT SYSTEM:
 * - Multiple weapon types with different firing rates
 * - Sound effects for shooting and taking damage
 * - Visual feedback for different weapon states
 */
class Dino
{
	// Movement and action states
	boolean isJumping = false;       // Whether the dino is currently jumping
	boolean isShooting = false;      // Whether the dino is currently shooting
	int runningInt = 8;              // Counter for running animation frames
	
	// Visual representation images
	Image jumping, running1, running2;  // Movement animation frames
	Image picture;                      // Currently displayed image
	Image gear;                         // Currently equipped gear image
	Image gear1, gear2, gear3;         // Different gear states (idle, active, special)
	Image weapon;                       // Currently equipped weapon image
	Image weapon1, weapon2;             // Weapon states (idle, firing)
	Image dead;                         // Death animation image
	
	// Audio system
	Clip shotSound;                     // Sound clip for weapon firing
	AudioInputStream sound;             // Audio stream for sound loading
	
	int weaponID, gearID;
	int height, width, x, y;
	double health;
	boolean hasSaver;
	int velocity = 0;
	
	long time;
	long shotTime;
	int reload = 1000;
	int drawShooting;
	
	Cactus cactus;
	Ptero[] pteros;
	GamePlayPanel gPP;
	
	int[] reloadTimes = {1000, 250, 1500, 60, 90};
	//more gigantic image arrays
	Image[] weaponImages = {
			new ImageIcon("shotgun.png").getImage(),
			new ImageIcon("plasma.png").getImage(),
			new ImageIcon("railgun.png").getImage(),
			new ImageIcon("minigun.png").getImage(),
			new ImageIcon("laser.png").getImage()
	};
	Image[] weaponShootingImages = {
			new ImageIcon("shotgunS.png").getImage(),
			new ImageIcon("plasmaS.png").getImage(),
			new ImageIcon("railgunS.png").getImage(),
			new ImageIcon("minigunS.png").getImage(),
			new ImageIcon("laserS.png").getImage()
	};
	Image[] gearImages = {
			new ImageIcon("camouflage.png").getImage(),
			new ImageIcon("bling.png").getImage(),
			new ImageIcon("lifesaver.png").getImage(),
			new ImageIcon("armor.png").getImage(),
			new ImageIcon("jets.png").getImage()
	};
	Image[] gearImages1 = {
			new ImageIcon("camouflage.png").getImage(),
			new ImageIcon("bling.png").getImage(),
			new ImageIcon("nothing.png").getImage(),
			new ImageIcon("armor.png").getImage(),
			new ImageIcon("jetsFired1.png").getImage()
	};
	Image[] gearImages2 = {
			new ImageIcon("camouflage.png").getImage(),
			new ImageIcon("bling.png").getImage(),
			new ImageIcon("nothing.png").getImage(),
			new ImageIcon("armor.png").getImage(),
			new ImageIcon("jetsFired2.png").getImage()
	};
	
	//sound file array
	File[] shotSounds = {
			new File("shotgun.wav").getAbsoluteFile(), 
			new File("plasma.wav").getAbsoluteFile(), 
			new File("railgun.wav").getAbsoluteFile(), 
			new File("minigun.wav").getAbsoluteFile(), 
			new File("laser.wav").getAbsoluteFile()
	};
	
	//empty image used to display unequipped items 
	Image empty = new ImageIcon("empty.png").getImage();
	
	
	public Dino(Cactus enemy1, Ptero[] pterosInput, GamePlayPanel panel, int weaponInput, int gearInput)
	// sets the GamePlayPanel as its "parent panel", so it can call stop() and get information.
	{
		jumping = new ImageIcon("dino.png").getImage();
		running1 = new ImageIcon("dinoRunning1.png").getImage();
		running2 = new ImageIcon("dinoRunning2.png").getImage();
		dead = new ImageIcon("dead.png").getImage();

		weaponID = weaponInput;
		gearID = gearInput;
		//setting all the images to empty by default.
		weapon1 = empty;
		weapon2 = empty;
		gear1 = empty;
		gear2 = empty;
		gear3 = empty;
		gear = empty;
		if(gearID == 2)hasSaver = true;
		health = 100;
		
		drawShooting = 1;
		time = System.currentTimeMillis();
		
		//if things are actually equipped, it will then update the images.
		if(weaponInput >= 0)
		{
			weapon1 = weaponImages[weaponInput];
			weapon2 = weaponShootingImages[weaponInput];
		}
		if(gearInput >= 0)
		{
			gear1 = gearImages[gearInput];
			gear2 = gearImages1[gearInput];
			gear3 = gearImages2[gearInput];
		}
		//setting more values
		y = 595;
		x = 50;
		height = jumping.getHeight(null)/2;
		width = jumping.getWidth(null)/2;
		picture = running1; 
		cactus = enemy1;
		pteros = pterosInput;
		gPP = panel;
	}
	public void move() {// called every game tick, dino simply moves based on its values.
		runningInt--;
		if(runningInt == -8)runningInt = 8;//runningInt makes it so that every 8 frames, the dino switches to the other running picture
		y = Math.min(y-velocity, 595);//moving the dinosaur based on its velocity
		velocity--;//velocity decreases
		if(y >= 595 && velocity < -8)velocity = -8;
		if(y < 50) 
		{
			y  = 50;
			velocity--;
		}
		
		time = System.currentTimeMillis();
		
		//determining the pictures the dino sould be using
		gear = gear1;
		weapon = weapon1;
		if(isShooting) shoot();
		if(drawShooting >= 1) {
			weapon = weapon2;
		}
		if(runningInt > 0)
		{
			picture = running1;
			if(isJumping)gear = gear2;
		}
		else 
		{
			picture = running2;
			if(isJumping)gear = gear3;
		}
		if(gPP.fuel <= 0)gear = gear1;
		if(y < 595) picture = jumping;
		
		//hit detection, seeing if dino collided with cactus
		if((x+15)+(width-45) > cactus.x && x+15 < cactus.x+cactus.width && y+height > cactus.y+cactus.height*0.3 && y < cactus.y+cactus.height*0.3) 
		{
			health = 0;
			if(hasSaver)cactus.respawn();
		}
		//hit detection, seeing if dino collided with ptero
		for(int i=0; i<pteros.length; i++)
		if(pteros[i] != null)
		if((x+15)+(width-45) > pteros[i].x+10 && x+15 < (pteros[i].x+10)+(pteros[i].width-20) && y+height > pteros[i].y+25 && y < (pteros[i].y+25)+(int)(pteros[i].height*0.55)) 
		{
			//colliding with a ptero will hurt, but it will also kill the ptero
			damage(pteros[i].damage);
			if(health > 0)pteros[i].health -= 100;
		}
		if(health <= 0)
		{
			if(hasSaver)
			{
				health = 25;
				hasSaver = false;
			}
			else gPP.stop();
		}
	}
	public void damage(int damage) {
		if(gearID == 3)damage /= 3.0;
		health -= damage;
		playHitSound();
	}
	public void shoot() {// this method is work in progress
		//its purpose is to figure out which type of shooting animation a weapon should use.
		if(weaponID != -1)
		if(time - shotTime >= reloadTimes[weaponID])// this if loop sees if the weapon is reloaded yet. 
		{
			shotTime = System.currentTimeMillis();
			playShootingSound();
			if(weaponID == 0 || weaponID == 1 || weaponID == 2)// shotgun, plasma gun, and railgun all show their firing pictures for a very brief moment.
			{
				drawShooting = 10;
				gPP.shoot();
				if(weaponID == 0)for(int i=0; i<8; i++) gPP.shoot();// shotgun fires 9 projectiles
			}
			if(weaponID == 3)// minigun is unique, it needs to flicker on and off
			{
				drawShooting = 4;
				gPP.shoot();
			}
			if(weaponID == 4)//laser shoots very fast 
			{
				drawShooting = 5;
				gPP.shoot();
			}
		}
	}
	public void playShootingSound()
	//uses a try-catch block to play the sound of the weapons firing
	{
		try
		{
			sound = AudioSystem.getAudioInputStream(shotSounds[weaponID]);
			shotSound = AudioSystem.getClip();
			shotSound.open(sound);
			shotSound.start();
		}
		catch(Exception e)
		{
			System.out.println("you didn't find the file and \nthis is entirely your fault and \nnow the world is ending");
		}
	}
	public void playHitSound()
	//uses a try-catch block to play the sound of the weapons firing
	{
		try
		{
			File hitSound = new File("hit.wav").getAbsoluteFile();
			sound = AudioSystem.getAudioInputStream(hitSound);
			shotSound = AudioSystem.getClip();
			shotSound.open(sound);
			shotSound.start();
		}
		catch(Exception e)
		{
			System.out.println("you didn't find the file and \nthis is entirely your fault and \nnow the world is ending");
		}
	}
}
class DinoProjectile// projectiles that weapons shoot.
{
	Image[] images = {
			new ImageIcon("shotgunP.png").getImage(),
			new ImageIcon("plasmaPL.png").getImage(),
			new ImageIcon("railgunP.png").getImage(),
			new ImageIcon("minigunP.png").getImage(),
			new ImageIcon("laserP.png").getImage()
	};
	int[] damages = {20, 150, 420, 34, 100};
	int damage;
	Image picture;
	GamePlayPanel gPP;
	Ptero[] pteros;
	int weaponID;
	int x, y, width, height;
	int[] velocities = {20, 15, 0, 25, 50};
	int[] xVariation = {9, 1, 1, 5, 1};
	int[] spreadFactors = {5, 1, 1, 3, 1};
	int[] startingX = {80, 125, 155, 120, 55};
	int[] startingY = {30, 25, 20, 30, 10};
	int xV, yV;
	long shotTime = System.currentTimeMillis();
	int[] despawnTimes = {1500, 1500, 100, 1500, 1000};
	public DinoProjectile(int type, int yInput, GamePlayPanel owningPanel, Ptero[] targets)
	// constructor recieves a ton of information from gameplaypanel, determining how it will behave, because
	//each weapon's projectiles act slightly differently.
	{
		weaponID = type;
		picture = images[weaponID];
		y = yInput + startingY[weaponID];
		x = startingX[weaponID];
		width = picture.getWidth(null);
		height = picture.getHeight(null);
		damage = damages[weaponID];
		// making the projectiles have random x and y velocities based on the values
		xV = velocities[weaponID] + ((int)(Math.random()*xVariation[weaponID])-(xVariation[weaponID]/2));
		yV = ((int)(Math.random()*spreadFactors[weaponID])-(spreadFactors[weaponID]/2));
		gPP = owningPanel;
		pteros = targets;
		
	}
	public void move()// moves the projectile based on its values
	{
		if(System.currentTimeMillis() - shotTime >= despawnTimes[weaponID]) gPP.remove(this);
		for(int i=0; i<pteros.length; i++)
		{
			if(pteros[i] != null)
			{
				//This chunk of code is for collision logic.
				//first if checks if it is a railgun, as railgun behaves very different
				//railgun is unique in two ways: 
				//its hitbox is much larger than the image, so it needs to be calculated differently
				//it does not disappear after collision
				if(weaponID == 2)if(x+width > pteros[i].x+10 && x < (pteros[i].x+10)+(pteros[i].width-20) && y+(1.5*height) > pteros[i].y+25 && y-(0.5*height) < (pteros[i].y+25)+(int)(pteros[i].height*0.55))
				{
					pteros[i].health -= damage;
				}
				if(weaponID != 2)if(x+width > pteros[i].x+10 && x < (pteros[i].x+10)+(pteros[i].width-20) && y+height > pteros[i].y+25 && y < (pteros[i].y+25)+(int)(pteros[i].height*0.55))
				{
					pteros[i].health -= damage;
					gPP.remove(this);
				}
				
				
				//plasma needs to decay, updating both damage and image.
				if(weaponID == 1)
				{
					if(x >= 350)
					{
						picture = new ImageIcon("plasmaPM.png").getImage();
						damage  = 80;
					}
					if(x >= 700)
					{
						picture = new ImageIcon("plasmaPS.png").getImage();
						damage = 40;
					}
				}
			}
		}
		x += xV;
		y += yV;
	}
}
class PteroRock // standard projectile that the ptero drops
{
	int x, y, width, height, xV, yV;
	int damage = 6;
	int rocks = 1;
	Image picture = new ImageIcon("rock.png").getImage();
	GamePlayPanel owner;
	Dino target;
	public PteroRock(GamePlayPanel input, Dino targetInput, int xInput, int yInput, int velocity)
	//needs to know the gameplaypanel for removal, its target, as well as the location it spawns at and the velocity.
	{
		owner = input;
		target = targetInput;
		yV = 12;
		xV = velocity;
		x = xInput;
		y = yInput;
		width = (int)(picture.getWidth(null)*1.5);
		height = (int)(picture.getHeight(null)*1.5);
	}
	public void move()
	//moves the rock based on its velocities, and checks if it has hit the dino
	{
		x -= xV;
		y += yV;
		if(y >= 680) owner.remove(this);
		//hit detection, seeing if the rock hit the dino
		if((target.x)+(target.width) > x && target.x < x+width && target.y+height > y && target.y < y+height)
		{
			target.damage(damage);
			owner.remove(this);
		}
	}
}
class PteroBomb // explosive projectile that the ptero drops
{
	int x, y, width, height, yV;
	int eWidth = 180, eHeight = 110;
	int damage = 35;
	int explosionTime;
	boolean shouldExplode;
	boolean exploded;
	Image picture;
	Image bombImage = new ImageIcon("bombP.png").getImage();
	Image explosionImage =  new ImageIcon("explosion.png").getImage();
	GamePlayPanel owner;
	Dino target;
	public PteroBomb(GamePlayPanel input, Dino targetInput, int xInput, int yInput)
	//almost the same as pteroRock parameters, but it doesn't need velocity, as bombs are dropped, not thrown
	{
		owner = input;
		target = targetInput;
		explosionTime = 10;
		picture = bombImage;
		shouldExplode = false;
		exploded = false;
		yV = 8;
		x = xInput;
		y = yInput;
		width = picture.getWidth(null)/4;
		height = picture.getHeight(null)/4;
	}
	public void move()
	//move calls fall() if the bomb hasn't exploded yet
	//move calls exlplode() if the bomb touches the ground
	//move calls drawExploded(); after the bomb explodes, to draw the explosion image for enough time to see.
	{
		if(exploded) drawExploded();
	    else if(shouldExplode) explode();
		else fall();
	}
	public void fall()
	//falls, then checks if it has touched the ground.
	{
		y += yV;
		if(y >= 680) shouldExplode = true;
	}
	public void explode()
	//updates image and dimentions, and checks if it hit a dino.
	{
		picture = explosionImage;
		x -= 60;
		y -= 120;
		width = explosionImage.getWidth(null);
		height = explosionImage.getHeight(null);
		if((target.x)+(target.width) > x && target.x < x+width && target.y+height > y && target.y < y+height)
		{
			target.damage(damage);
		}
		playExplosionSound();
		exploded = true;
		shouldExplode = false;
	}
	public void drawExploded()
	//draws the explosion after the damage is dealt
	{
		explosionTime--;
		if(explosionTime <= 0) owner.remove(this);
	}
	public void playExplosionSound()
	//uses a try-catch block to play the explosion sound
	{
		Clip shotSound;
		AudioInputStream sound;
		try
		{
			File soundFile = new File("explosion.wav").getAbsoluteFile();
			sound = AudioSystem.getAudioInputStream(soundFile);
			shotSound = AudioSystem.getClip();
			shotSound.open(sound);
			shotSound.start();
		}
		catch(Exception e)
		{
			System.out.println("you didn't find the file and \nthis is entirely your fault and \nnow the world is ending");
		}
	}
}