import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
/**
 * Build a GUI for the Big Two card game and handle all user actions
 * @author Darren Chang JR
 */
public class BigTwoGUI implements CardGameUI {
	private final static int MAX_CARD_NUM = 13;
    private BigTwo game = null;
    private boolean[] selected = new boolean[MAX_CARD_NUM];
	private ArrayList<CardGamePlayer> playerList;
	private ArrayList<Hand> handsOnTable;
    private int activePlayer = -1;
	// GUI related
    private JFrame frame;
    private BigTwoPanel bigTwoPanel;
    private JButton playButton, passButton;
    private JTextArea msgArea, chatArea;
    private JTextField chatInput;
	// if things go wrong the below are probably to be blamed
	private static int textFieldsSize = 40;
	private JMenuBar menuBar;
	private JMenu gameMenu;
	private JMenuItem connectItem, quitItem;
	private JScrollPane msgPane, chatPane;
	private JPanel buttonsPanel, textPanel;
	private static final int xLoc = 50, yLoc = 0, widthCard = 80, heightCard = 110, xSpace = 25, ySpace = 20;
	private boolean globalEnable;
	private BigTwoClient client;
    
	/**
	 * constructor for creating a BigTwoGUI
	 * @param game reference to a BigTwo card game associated with this GUI
	 */
    public BigTwoGUI(BigTwo game) {
		this.game = game;
		this.playerList = game.getPlayerList();
		this.handsOnTable = game.getHandsOnTable();

		client = new BigTwoClient(game, this);
		
		// all the buttons
		playButton = new JButton("Play");
		playButton.addActionListener(new PlayButtonListener());
		
		passButton = new JButton("Pass");
		passButton.addActionListener(new PassButtonListener());

		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
		buttonsPanel.add(playButton);
		buttonsPanel.add(passButton);
		
		// all the texts
		msgArea = new JTextArea(20, textFieldsSize);
		msgArea.setEditable(false);
		msgArea.setLineWrap(true);
		msgArea.setWrapStyleWord(true);
		msgPane = new JScrollPane(msgArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		msgPane.setBorder(BorderFactory.createTitledBorder("Game Messages and Logs"));
		
		chatArea = new JTextArea(20, textFieldsSize);
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		chatArea.setWrapStyleWord(true);
		chatPane = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatPane.setBorder(BorderFactory.createTitledBorder("Lobby Chat"));

		JLabel chatlabel = new JLabel("Chat: ");
		chatlabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		chatInput = new JTextField(textFieldsSize);
		chatInput.addKeyListener(new ChatInputListener());

		textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.setPreferredSize(new Dimension(300, 500));
		textPanel.add(msgPane);
		textPanel.add(chatPane);
		textPanel.add(chatlabel);
		textPanel.add(chatInput);
		
		// all the menu objects
		menuBar = new JMenuBar();
		gameMenu = new JMenu("Game");
		
		connectItem = new JMenuItem("Connect");
		connectItem.addActionListener(new ConnectMenuItemListener());
		
		quitItem = new JMenuItem("Quit");
		quitItem.addActionListener(new QuitMenuItemListener());
		
		gameMenu.add(connectItem);
		gameMenu.add(quitItem);
		menuBar.add(gameMenu);

		// boilerplate: frame and panel
		bigTwoPanel = new BigTwoPanel();
		
		frame = new JFrame("Les Chor Tai Ti");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(new Color(22, 140, 38));
		frame.setJMenuBar(menuBar);
		frame.add(buttonsPanel, BorderLayout.SOUTH);
		frame.add(bigTwoPanel, BorderLayout.CENTER);
		frame.add(textPanel, BorderLayout.EAST);
		frame.pack();
		frame.setVisible(true);
    }

    /**
	 * Sets the index of the active player (i.e., the current player).
	 * @param activePlayer an int value representing the index of the active player
	 */
	public void setActivePlayer(int activePlayer) {
		if (activePlayer < 0 || activePlayer >= playerList.size()) {
			this.activePlayer = -1;
		} else {
			this.activePlayer = activePlayer;
		}
    }

	/**
	 * Repaints the user interface.
	 */
	public void repaint() {
		playerList = game.getPlayerList();
		handsOnTable = game.getHandsOnTable();
		// if frame is null, then do nothing, else remove it and add a new one
		if (frame != null) {
			frame.remove(bigTwoPanel);
			bigTwoPanel = new BigTwoPanel();
			frame.add(bigTwoPanel, BorderLayout.CENTER);
			frame.pack();
			frame.repaint();
		}
	}

	/**
	 * Prints the specified string to the message area of the card game user
	 * interface.
	 * @param msg the string to be printed to the message area of the card game user
	 *            interface
	 */
	public void printMsg(String msg) {
		msgArea.append(msg);
		msgArea.repaint();
		msgArea.setCaretPosition(msgArea.getText().length());
	}

	/**
	 * Clears the message area of the card game user interface.
	 */
	public void clearMsgArea() {
		msgArea.setText("");
		msgArea.repaint();
	}

	/**
	 * Adds the specified string to the end of the chat messages in the chat area
	 * of the card game user interface.
	 * @param msg string to be added to the end of the chat messages in the chat
	 */
	public void appendChat(String msg) {
		if (msg.length() > 0) {
			chatArea.append(msg);
			chatArea.append("\n");
			chatArea.repaint();
		}
	}

	/**
	 * Resets the card game user interface.
	 */
	public void reset() {
		for (int i = 0; i < MAX_CARD_NUM; i++) {
			selected[i] = false;
		}
		clearMsgArea();
		enable();
	}

	/**
	 * Enables user interactions.
	 */
	public void enable() {
		globalEnable = true;
		playButton.setEnabled(globalEnable);
		passButton.setEnabled(globalEnable);
	}

	/**
	 * Disables user interactions.
	 */
	public void disable() {
		globalEnable = false;
		playButton.setEnabled(globalEnable);
		passButton.setEnabled(globalEnable);
	}

	/**
	 * Prompts active player to select cards and make his/her move.
	 */
	public void promptActivePlayer() {
		printMsg(playerList.get(game.getCurrentPlayerIdx()).getName() + "'s turn: ");
		if (activePlayer == game.getCurrentPlayerIdx()) {
			enable();
		} else {
			disable();
		}
	}

	/**
	 * Returns the game's client object.
	 * @return the game's client object
	 */
	public BigTwoClient getClient() {
		return client;
	}

	public void showMessageInDialog(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Game Ends", JOptionPane.PLAIN_MESSAGE);
		client.sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
	}

	/**
	 * Returns an array of indices of the cards selected through the UI.
	 * 
	 * @return an array of indices of the cards selected, or null if no valid cards
	 *         have been selected
	 */
	private int[] getSelected() {
		int[] cardIdx = null;
		int count = 0;
		for (int j = 0; j < selected.length; j++) {
			if (selected[j]) {
				count++;
			}
		}

		if (count != 0) {
			cardIdx = new int[count];
			count = 0;
			for (int j = 0; j < selected.length; j++) {
				if (selected[j]) {
					cardIdx[count] = j;
					count++;
				}
			}
		}
		return cardIdx;
	}

	/**
	 * Resets the list of selected cards to an empty list.
	 */
	private void resetSelected() {
		for (int j = 0; j < selected.length; j++) {
			selected[j] = false;
		}
	}

	/**
	 * Panel containing main gameplay area.
	 * @author Darren Chang JR
	 */
    class BigTwoPanel extends JPanel {
		/**
		 * BigTwoPanel constructor
		 */
		public BigTwoPanel() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setBackground(new Color(0,0,0,0));
			setPreferredSize(new Dimension(1000, 750));
			for (int i = 0; i < game.getNumOfPlayers(); i++) {
				PlayerPanel playerPanel =  new PlayerPanel(i, playerList.get(i).getCardsInHand());
				playerPanel.setBackground(new Color(0,0,0,0));
				add(playerPanel);
			}
			if (!handsOnTable.isEmpty()) {
				HandOnTablePanel handOnTablePanel = new HandOnTablePanel(handsOnTable.get(handsOnTable.size() - 1));
				add(handOnTablePanel);
			} else {
				JLabel label = new JLabel("No cards on the table");
				label.setFont(new Font("Arial", Font.BOLD, 20));
				label.setPreferredSize(new Dimension(1000, 100));
				add(label);
			}
		}
	}
	/**
	 * Panel containing cards on the table.
	 * @author Darren Chang JR
	 */
	class HandOnTablePanel extends JPanel {
		/**
		 * HandOnTablePanel constructor
		 * @param handOnTable the hand on the table
		 */
		public HandOnTablePanel(Hand hand) {
			// decor
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setBackground(new Color(0,0,0,0));
			setBorder(new EmptyBorder(0, 40, 0, 0));
			CardsLying lastHand = new CardsLying(true, hand, false);
			JLabel nameText = new JLabel("Last Hand: " + hand.getPlayer().getName());
			nameText.setFont(new Font("Arial", Font.BOLD, 20));
			add(nameText);
			add(lastHand);
		}
	}

	/**
	 * Panel that displays the player's avatar and cards in hand.
	 * @author Darren Chang JR
	 */
	class PlayerPanel extends JPanel {
		private final JLabel avatarLabel;
		/**
		 * Constructor for PlayerPanel.
		 * @param i the index of the player
		 * @param cards the cards in hand of the player
		 */
		public PlayerPanel(int i, CardList cards) {
			// layout
			setLayout(new BorderLayout());
			// border
			setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), playerList.get(i).getName()));
			((javax.swing.border.TitledBorder) getBorder()).setTitleFont(new Font("Arial", Font.BOLD, 20));
			setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 30, 0, 10), getBorder()));
			// size
			setPreferredSize(new Dimension(1000, 150));

			// avatar
			Image image = new ImageIcon("./players/" + i + ".png").getImage();
			avatarLabel = new JLabel(new ImageIcon(image.getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
			avatarLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
			add(avatarLabel, BorderLayout.WEST);
			// cards
			JLayeredPane cardsLying = new CardsLying(i == activePlayer, cards, true);
			add(cardsLying, BorderLayout.CENTER);
		}
	}

	/**
	 * JLayeredPane to store and display cards lying on the table
	 * @author Darren Chang JR
	 */
	class CardsLying extends JLayeredPane {
		
		/**
		 * Constructor for CardsLying
		 * @param faceUp whether the cards are face up
		 * @param cards the cards to be displayed
		 * @param clickable whether the cards are clickable
		 */
		public CardsLying(boolean faceUp, CardList cards, boolean clickable) {
			setOpaque(false);
			setBackground(new Color(0,0,0,0));
			setPreferredSize(new Dimension((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), 130));
			for (int i = cards.size() - 1; i >= 0; i--) {
				if (faceUp) {
					CardPanel cardPanel = new CardPanel(i, cards.getCard(i), clickable);
					cardPanel.setBounds(xLoc + xSpace * i, yLoc + ySpace, widthCard, heightCard);
					cardPanel.addMouseListener(cardPanel);
					cardPanel.setBackground(new Color(0,0,0,0));
					add(cardPanel, cards.size() - i);
				} else {
					BackCardPanel backCardPanel = new BackCardPanel();
					backCardPanel.setBounds(xLoc + xSpace * i, yLoc + ySpace, widthCard, heightCard);
					backCardPanel.setBackground(new Color(0,0,0,0));
					add(backCardPanel, cards.size() - i);
				}
			}
		}
	}

	/**
	 * create a card panel of a card's back
	 * @author Darren Chang JR
	 */
	class BackCardPanel extends JPanel {
		private final JLabel backLabel;
		public BackCardPanel() {
			backLabel = new JLabel(new ImageIcon("./cards/b.gif"));
			add(backLabel);
		}
	}
	
	/**
	 * JPanel to store and display a card
	 * @author Darren Chang JR
	 */
	class CardPanel extends JPanel implements MouseListener {
		private final int index;
		private final boolean clickable;
		private boolean lifted = false;
		private String cardName = "";

		/**
		 * Constructor for CardPanel
		 * @param index the index of the card in selected[]
		 * @param card the card to be displayed
		 * @param clickable whether the card is clickable
		 */
		public CardPanel(int index, Card card, boolean clickable) {
			this.index = index;
			this.clickable = clickable;
			switch(card.getRank()) {
				case 0:
					cardName += "a";
					break;
				case 9:
					cardName += "t";
					break;
				case 10:
					cardName += "j";
					break;
				case 11:
					cardName += "q";
					break;
				case 12:
					cardName += "k";
					break;
				default:
					cardName += card.getRank() + 1;
			}

			switch(card.getSuit()) {
				case 0:
					cardName += "d";
					break;
				case 1:
					cardName += "c";
					break;
				case 2:
					cardName += "h";
					break;
				case 3:
					cardName += "s";
					break;
			}
		}

		/**
		 * Paints the card
		 * @param g the Graphics object to be painted
		 */
		@Override
		protected void paintComponent(Graphics g) {
			Image image = new ImageIcon("./cards/" + cardName + ".gif").getImage();
			g.drawImage(image, 3, 4, this);
		}
		
		/**
		 * handle mouse clicks on cardPanel
		 * @param arg0 the event
		 */
		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (clickable && globalEnable) {
				lifted = !lifted;
				selected[index] = lifted;
				if (lifted) setBounds(xLoc + index * xSpace, 0, widthCard, heightCard);
				else setBounds(xLoc + index * xSpace, ySpace, widthCard, heightCard);
				frame.repaint();
			}
		}
	
		/**
		 * not used
		 * @param arg0 the event
		 */
		@Override
		public void mousePressed(MouseEvent arg0) {}
		/**
		 * not used
		 * @param arg0 the event
		 */
		@Override
		public void mouseExited(MouseEvent arg0) {}
		/**
		 * not used
		 * @param arg0 the event
		 */
		@Override
		public void mouseReleased(MouseEvent arg0) {}
		/**
		 * not used
		 * @param arg0 the event
		 */
		@Override
		public void mouseEntered(MouseEvent arg0) {}
	}

	/**
	 * Play button listener
	 * @author Darren Chang JR
	 */
	class PlayButtonListener implements ActionListener {
		/**
		 * handle "Play" button click events
		 * @param arg0 the event
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int[] cardIdx = getSelected();
			if (cardIdx != null) {
				resetSelected();
				game.makeMove(activePlayer, cardIdx);
			}
		}
	}

	/**
	 * Pass button listener
	 * @author Darren Chang JR
	 */
	class PassButtonListener implements ActionListener {
		/**
		 * handle "Pass" button click events
		 * @param arg0 the event
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			resetSelected();
			game.makeMove(activePlayer, null);
		}
	}

	/**
	 * Connect menu item listener
	 * @author Darren Chang JR
	 */
	class ConnectMenuItemListener implements ActionListener {
		/**
		 * handle "Connect" menu item click events
		 * @param arg0 the event
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			client.connect();
		}
	}

	/**
	 * Quit menu item listener
	 * @author Darren Chang JR
	 */
	class QuitMenuItemListener implements ActionListener {
		/**
		 * handle "Quit" menu item click events
		 * @param arg0 the event
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.exit(0);
		}
	}

	/**
	 * Chat input listener
	 * @author Darren Chang JR
	 */
	class ChatInputListener implements KeyListener {
		/**
		 * not used
		 * @param arg0 the event
		 */
		@Override
		public void keyPressed(KeyEvent arg0) {}

		/**
		 * not used
		 * @param arg0 the event
		 */
		@Override
		public void keyReleased(KeyEvent arg0) {}
		
		/**
		 * handle key typed events
		 * @param arg0 the event
		 */
		@Override
		public void keyTyped(KeyEvent event) {
			if (event.getKeyChar() == '\n') {
				String msg = chatInput.getText();
				if (msg.length() > 0) {
					try {
						client.sendMessage(new CardGameMessage(CardGameMessage.MSG, -1, msg));
					} catch (Exception e) {e.printStackTrace();}
					chatInput.setText("");
				}
			}
		}
	}
}
