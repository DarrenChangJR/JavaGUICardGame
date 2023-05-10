/**
 * Models a hand of StraightFlush in a Big Two card game
 * @author Darren Chang JR
 */
public class StraightFlush extends Hand {

    /**
     * Build a StraightFlush with the player and cards
     * @param player player of cards
     * @param cards cards played
     */
    public StraightFlush(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if this hand beats a specified hand
     * @param hand target hand to beat
     * @return true if this hand beats target false otherwise
     */
    public boolean beats(Hand hand) {
        if (hand.getType() == "StraightFlush")
            return (this.getTopCard().compareTo(hand.getTopCard()) > 0);
        
        if (hand.getType() == "Straight" || hand.getType() == "Flush" || hand.getType() == "FullHouse" || hand.getType() == "Quad") return true;
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
        
        this.sort();
        int firstRank;
        int secondRank;
        int suit = this.getCard(4).getSuit();
        for (int i = 0; i < 4; ++i) {
            // preparing ranks
            firstRank = this.getCard(i).getRank();
            secondRank = this.getCard(i + 1).getRank();
            if (firstRank < 2) firstRank += 13;
            if (secondRank < 2) secondRank += 13;
            // check if suits are uniform
            // AND
            // all cards in sorted order must be one apart from front and back in Big Two order
            if (this.getCard(i).getSuit() != suit || firstRank + 1 != secondRank)
                return false;
        }
        return true;
    }
    
    /**
     * Return the type of hand as a String
     * @return type of hand
     */
    public String getType() { return "StraightFlush"; }
}