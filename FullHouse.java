/**
 * Models a hand of FullHouse in a Big Two card game
 * @author Darren Chang JR
 */
public class FullHouse extends Hand {

    /**
     * Build a FullHouse with the player and cards
     * @param player player of cards
     * @param cards cards played
     */
    public FullHouse(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Return the top card of this hand
     * @return top card of this hand
     */
    public Card getTopCard() {
        // find triplet, and return the highest of them
        this.sort();
        if (this.getCard(2).getRank() == this.getCard(0).getRank()) return this.getCard(2);
        if (this.getCard(2).getRank() == this.getCard(4).getRank()) return this.getCard(4);
        return null;
    }

    /**
     * Check if this hand beats a specified hand
     * @param hand target hand to beat
     * @return true if this hand beats target false otherwise
     */
    public boolean beats(Hand hand) {
        if (hand.getType() == "FullHouse")
            return (this.getTopCard().compareTo(hand.getTopCard()) > 0);
        
        if (hand.getType() == "Straight" || hand.getType() == "Flush") return true;
        return false;
    }

    /**
     * Check if hand is valid
     * @return true if hand is valid false otherwise
     */
    public boolean isValid() {
        // number of cards
        if (this.size() != 5)
            return false;
        
        // find 2 doubles (0, 1) and (3, 4), and test if middle is part of either double
        this.sort();
        if (this.getCard(0).getRank() == this.getCard(1).getRank() && this.getCard(3).getRank() == this.getCard(4).getRank()) {
            if (this.getCard(2).getRank() == this.getCard(0).getRank() || this.getCard(2).getRank() == this.getCard(4).getRank())
                return true;
        }
        return false;
    }
    
    /**
     * Return the type of hand as a String
     * @return type of hand
     */
    public String getType() { return "FullHouse"; }
}