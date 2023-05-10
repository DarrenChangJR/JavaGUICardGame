/**
 * Models a hand of Triple in a Big Two card game
 * @author Darren Chang JR
 */
public class Triple extends Hand {

    /**
     * Build a Triple with the player and cards
     * @param player player of cards
     * @param cards cards played
     */
    public Triple(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if hand is valid
     * @return true if hand is valid false otherwise
     */
    public boolean isValid() {
        // number of cards
        if (this.size() != 3)
            return false;
        
        // same rank of cards
        int card0 = this.getCard(0).getRank();
        for (int i = 1; i < this.size(); ++i) {
            if (this.getCard(i).getRank() != card0)
                return false;
        }
        return true;
    }
    
    /**
     * Return the type of hand as a String
     * @return type of hand
     */
    public String getType() { return "Triple"; }
}