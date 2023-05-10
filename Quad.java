/**
 * Models a hand of Quad in a Big Two card game
 * @author Darren Chang JR
 */
public class Quad extends Hand {

    /**
     * Build a Quad with the player and cards
     * @param player player of cards
     * @param cards cards played
     */
    public Quad(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Return the top card of this hand
     * @return top card of this hand
     */
    public Card getTopCard() {
        // find single (not part of quad), and return the highest while avoiding the single
        this.sort();
        if (this.getCard(0).getRank() != this.getCard(1).getRank()) return this.getCard(4);
        if (this.getCard(3).getRank() != this.getCard(4).getRank()) return this.getCard(3);
        return null;
    }

    /**
     * Check if this hand beats a specified hand
     * @param hand target hand to beat
     * @return true if this hand beats target false otherwise
     */
    public boolean beats(Hand hand) {
        if (hand.getType() == "Quad")
            return (this.getTopCard().compareTo(hand.getTopCard()) > 0);
        
        if (hand.getType() == "Straight" || hand.getType() == "Flush" || hand.getType() == "FullHouse") return true;
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
        
        // offset is 0 if expecting single to be the last card, 1 if first
        this.sort();
        int offset = ((this.getCard(0).getRank() == this.getCard(1).getRank()) ? 0 : 1);
        for (int i = offset; i < offset + 3; i++) {
            if (this.getCard(i).getRank() != this.getCard(i + 1).getRank())
                return false;
        }
        return true;
    }
    
    /**
     * Return the type of hand as a String
     * @return type of hand
     */
    public String getType() { return "Quad"; }
}