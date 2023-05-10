import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 * Class responsible for establishing a connection to the Big Two game server and handling the communications with the game server
 * @author Darren Chang JR
 */
public class BigTwoClient implements NetworkGame {
	private BigTwo game;
	private BigTwoGUI gui;
	private Socket sock;
	private ObjectOutputStream oos;
	private int playerID;
	private String playerName;
	private String serverIP = "127.0.0.1";
	private int serverPort = 2396;
	private ObjectInputStream ois;

	/**
	 * Creates a Big Two client
	 * @param game Big Two game
	 * @param gui Big Two GUI
	 */
	public BigTwoClient(BigTwo game, BigTwoGUI gui) {
		this.game = game;
		this.gui = gui;
		
		do {
			playerName = JOptionPane.showInputDialog(null, "Enter your name: ", "Big Two", JOptionPane.PLAIN_MESSAGE);
			if (playerName == null) {
				System.exit(0);
			}
		}
		while (playerName.equals(""));
	}

    /**
	 * Returns the playerID (index) of the local player.
	 * @return the playerID (index) of the local player
	 */
    @Override
    public int getPlayerID() { return playerID; }

    /**
	 * Sets the playerID (index) of the local player.
	 * @param playerID the playerID (index) of the local player.
	 */
	public void setPlayerID(int playerID) { this.playerID = playerID; }

	/**
	 * Returns the name of the local player.
	 * @return the name of the local player
	 */
	public String getPlayerName() { return playerName; }

	/**
	 * Sets the name of the local player.
	 * @param playerName the name of the local player
	 */
	public void setPlayerName(String playerName) { this.playerName = playerName; }

	/**
	 * Returns the IP address of the server.
	 * @return the IP address of the server
	 */
	public String getServerIP() { return serverIP; }

	/**
	 * Sets the IP address of the server.
	 * @param serverIP the IP address of the server
	 */
	public void setServerIP(String serverIP) { this.serverIP = serverIP; }

	/**
	 * Returns the TCP port of the server.
	 * @return the TCP port of the server
	 */
	public int getServerPort() { return serverPort; }

	/**
	 * Sets the TCP port of the server
	 * @param serverPort the TCP port of the server
	 */
	public void setServerPort(int serverPort) { this.serverPort = serverPort; }

	/**
	 * Makes a network connection to the server.
	 */
	public void connect() {
		if (sock != null)
			return;
		try {
			sock = new Socket(serverIP, serverPort);
			oos = new ObjectOutputStream(sock.getOutputStream());
			ois = new ObjectInputStream(sock.getInputStream());
			Thread handlerThread = new Thread(new ServerHandler());
			handlerThread.start();
		} catch (Exception e) { e.printStackTrace(); }
	}

	/**
	 * Parses the specified message received from the server.
	 * @param message the specified message received from the server
	 */
	public synchronized void parseMessage(GameMessage message){
		switch (message.getType()) {
			
			case CardGameMessage.PLAYER_LIST:

				playerID = message.getPlayerID();
				game.getPlayerList().get(playerID).setName(playerName);
				String[] names = (String[]) message.getData();
				for (int i = 0; i < game.getNumOfPlayers(); i++) {
					if (playerID != i)
						game.getPlayerList().get(i).setName((names[i] == null)? "" : names[i]);
					else
						gui.setActivePlayer(playerID);
				}
				sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, playerName));
				gui.repaint();
				break;

			case CardGameMessage.JOIN:
				game.getPlayerList().get(message.getPlayerID()).setName((String) message.getData());
				if (message.getPlayerID() == playerID)
					sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
				gui.repaint();
				break;

			case CardGameMessage.FULL:
				gui.printMsg("The server is full. Please try again later.\n");
				sock = null;
				break;

			case CardGameMessage.QUIT:
				CardGamePlayer player = game.getPlayerList().get(message.getPlayerID());
				gui.printMsg(player.getName() + " has left the game.\n");
				player.setName("");
				sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
				if (game.getCurrentPlayerIdx() != -1) {
					gui.disable();
					game.setCurrentPlayerIdx(-1);
					sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
				}
				break;

			case CardGameMessage.READY:
				gui.printMsg("Player " + message.getPlayerID() + " is ready.\n");
				gui.repaint();
				break;
			
			case CardGameMessage.START:
				game.start((BigTwoDeck) message.getData());
				gui.repaint();
				break;

			case CardGameMessage.MOVE:
				game.checkMove(message.getPlayerID(), (int[]) message.getData());
				break;

			case CardGameMessage.MSG:
				gui.appendChat((String) message.getData());
				break;
		}
	}

	/**
	 * Sends the specified message to the server.
	 * @param message the specified message to be sent the server
	 */
	public synchronized void sendMessage(GameMessage message) {
		try {
			oos.writeObject(message);
		} catch (Exception e) { e.printStackTrace(); }
	}

	/**
	 * Inner class that implements run job for threading
	 * @author Darren Chang JR
	 */
	class ServerHandler implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					GameMessage message = (GameMessage) ois.readObject();
					parseMessage(message);
				}
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
}
