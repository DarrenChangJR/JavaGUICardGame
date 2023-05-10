import java.util.ArrayList;

/**
 * This class implements a CardGame to model a Big Two card game
 * @author Darren Chang JR
 */
public class BigTwo implements CardGame {
    private int numOfPlayers;
    private Deck deck;
    private ArrayList<CardGamePlayer> playerList;
    private ArrayList<Hand> handsOnTable;
    private int currentPlayerIdx;
    private BigTwoGUI ui;
    private BigTwoClient client;

    /**
     * Creates a Big Two card game with 4 players and a BigTwoGUI
     */
    public BigTwo() {
        numOfPlayers = 4;
        playerList = new ArrayList<CardGamePlayer>();
        for (int i = 0; i < numOfPlayers; ++i) {
            playerList.add(new CardGamePlayer());
        }
        handsOnTable = new ArrayList<Hand>();
        currentPlayerIdx = -1;
        ui = new BigTwoGUI(this);
        client = ui.getClient();
        client.connect();
        ui.disable();
    }

    /**
     * Returns the number of players
     * @return number of players
     */
    public int getNumOfPlayers() { return numOfPlayers; }

    /**
     * Returns the deck of cards being used
     * @return deck of cards being used
     */
    public Deck getDeck() { return deck; }

    /**
     * Returns the list of players
     * @return list of players
     */
    public ArrayList<CardGamePlayer> getPlayerList() { return playerList; }

    /**
     * Sets the list of players
     * @param playerList list of players
     */
    public void setPlayerList(ArrayList<CardGamePlayer> playerList) { this.playerList = playerList; }

    /**
     * Returns the list of hands played on the table
     * @return list of hands played on the table
     */
    public ArrayList<Hand> getHandsOnTable() { return handsOnTable; }

    /**
     * Returns the index of the current player
     * @return index of the current player
     */
    public int getCurrentPlayerIdx() { return currentPlayerIdx; }

    /**
     * Sets the index of the current player
     * @param currentPlayerIdx index of the current player
     */
    public void setCurrentPlayerIdx(int currentPlayerIdx) { this.currentPlayerIdx = currentPlayerIdx; }

    
    /** 
     * Starts/retarts the game with a given shuffled deck of cards
     * @param deck shuffled deck of cards
     */
    public void start(Deck deck) {
        this.deck = deck;
        ui.reset();
        handsOnTable.clear();
        
        for (int i = 0; i < numOfPlayers; ++i) {
            playerList.get(i).removeAllCards();
            for (int j = 0; j < 13; ++j) {
                // distribute card to player
                Card currentCard = this.deck.getCard(i*13 + j);
                playerList.get(i).addCard(currentCard);
                // find the player with Three of Diamonds
                if (currentCard.getRank() == 2 && currentCard.getSuit() == 0) {
                    this.currentPlayerIdx = i;
                }
            }
            playerList.get(i).sortCardsInHand();
        }
        ui.promptActivePlayer();
    }

    
    /** 
     * Makes a move by playerIdx using the cards cardIdx
     * @param playerIdx index of player making the move
     * @param cardIdx list of indices of cards
     */
    public void makeMove(int playerIdx, int[] cardIdx) {
        client.sendMessage(new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx));
    }

    
    /** 
     * Checks a move made by a player
     * @param playerIdx index of player making the move
     * @param cardIdx list of indices of cards
     */
    public void checkMove(int playerIdx, int[] cardIdx) {
        
        CardGamePlayer currentPlayer = playerList.get(playerIdx);
        CardList cards = currentPlayer.play(cardIdx);
        
        boolean attemptToPass = cards == null;
        Hand previousHand = (handsOnTable.isEmpty() ? null : handsOnTable.get(handsOnTable.size() - 1));

        if (attemptToPass) {
            if (previousHand == null || previousHand.getPlayer() == currentPlayer) {
                ui.printMsg("Not a legal move!!!\n");
            } else {
                ui.printMsg("{Pass}\n");
                currentPlayerIdx = ++currentPlayerIdx % numOfPlayers;
            }
        } else {
            Hand hypotheticalHand = composeHand(currentPlayer, cards);
        
            // hand is non-composable
            // OR
            // does not beat previous hand (if exists)
            // OR
            // is first hand but 3 Diamond is absent
            if ((hypotheticalHand == null)
                ||
                (previousHand != null && !hypotheticalHand.beats(previousHand) && previousHand.getPlayer() != currentPlayer)
                ||
                (previousHand == null && !hypotheticalHand.contains(new Card(0, 2)))) {
                ui.printMsg("Not a legal move!!!\n");
            } else {
                currentPlayer.removeCards(cards);
                handsOnTable.add(hypotheticalHand);
                String message = String.format("{%s} ", hypotheticalHand.getType());
                ui.printMsg(message);
                cards.print();
                ui.printMsg("\n");
                currentPlayerIdx = ++currentPlayerIdx % numOfPlayers;
            }
        }
        
        if (this.endOfGame()) {
            if (currentPlayerIdx == 0) currentPlayerIdx = 3;
            else currentPlayerIdx = currentPlayerIdx - 1;
            this.showResults();
            ui.disable();
        } else {
            ui.repaint();
            ui.promptActivePlayer();
        }
    }

    
    /** 
     * Checks if a game ends
     * @return boolean true if game ends
     */
    public boolean endOfGame() {
        for (CardGamePlayer player : playerList) {
            if (player.getNumOfCards() == 0) return true;
        }
        return false;
    }

    /**
     * Returns a valid hand from the list of cards, null if no valid hand composable
     * @param player player of the cards
     * @param cards list of cards played
     * @return valid hand from the list of cards, null if no valid hand composable
     */
    public static Hand composeHand(CardGamePlayer player, CardList cards) {
        Hand hypotheticalHand = null;
        
        if (cards.size() == 5) {
            hypotheticalHand = new StraightFlush(player, cards);
            if (hypotheticalHand.isValid()) return hypotheticalHand;

            hypotheticalHand = new Quad(player, cards);
            if (hypotheticalHand.isValid()) return hypotheticalHand;

            hypotheticalHand = new FullHouse(player, cards);
            if (hypotheticalHand.isValid()) return hypotheticalHand;

            hypotheticalHand = new Flush(player, cards);
            if (hypotheticalHand.isValid()) return hypotheticalHand;

            hypotheticalHand = new Straight(player, cards);
            if (hypotheticalHand.isValid()) return hypotheticalHand;
        }

        if (cards.size() == 3) {
            hypotheticalHand = new Triple(player, cards);
            if (hypotheticalHand.isValid()) return hypotheticalHand;
        }

        if (cards.size() == 2) {
            hypotheticalHand = new Pair(player, cards);
            if (hypotheticalHand.isValid()) return hypotheticalHand;
        }

        if(cards.size() == 1) {
            hypotheticalHand = new Single(player, cards);
            if (hypotheticalHand.isValid()) return hypotheticalHand;
        }

        return null;
    }


    private void showResults() {
        String message = "";
        for (int i = 0; i < numOfPlayers; ++i) {
            if (i == currentPlayerIdx)
                message += String.format("%s wins the game.\n", playerList.get(i).getName());
            else
                message += String.format("%s has %d cards in hand.\n", playerList.get(i).getName(), playerList.get(i).getNumOfCards());
        }
        ui.showMessageInDialog(message);
    }

    /**
     * Starts a Big Two card game with a shuffled deck of cards
     * @param args not used
     */
    public static void main(String[] args) {
        new BigTwo();
    }
}
