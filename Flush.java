/**
 * Models a hand of Flush in a Big Two card game
 * @author Darren Chang JR
 */
public class Flush extends Hand {

    /**
     * Build a Flush with the player and cards
     * @param player player of cards
     * @param cards cards played
     */
    public Flush(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if this hand beats a specified hand
     * @param hand target hand to beat
     * @return true if this hand beats target false otherwise
     */
    public boolean beats(Hand hand) {
        // for 2 flushes, if both have same suit, hand with higher ranked top card wins
        // else hand with higher suit wins
        if (hand.getType() == "Flush") {
            if (this.getTopCard().getSuit() == hand.getTopCard().getSuit()) {
                return (this.getTopCard().compareTo(hand.getTopCard()) > 0);
            }
            return (this.getTopCard().getSuit() > hand.getTopCard().getSuit());
        }
        
        // for other types, this hand wins if target is a straight, loses otherwise
        if (hand.getType() == "Straight") return true;
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
        
        // all cards must have the same suit
        int suit = this.getCard(0).getSuit();
        for (int i = 1; i < 5; ++i) {
            if (this.getCard(i).getSuit() != suit)
                return false;
        }
        return true;
    }
    
    /**
     * Return the type of hand as a String
     * @return type of hand
     */
    public String getType() { return "Flush"; }
}