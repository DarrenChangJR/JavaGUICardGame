/**
 * Models a hand of Single in a Big Two card game
 * @author Darren Chang JR
 */
public class Single extends Hand {

    /**
     * Build a Single with the player and cards
     * @param player player of cards
     * @param cards cards played
     */
    public Single(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }
    
    /**
     * Check if hand is valid
     * @return true if hand is valid false otherwise
     */
    public boolean isValid() {
        // number of cards
        if (this.size() != 1)
            return false;
        
        return true;
    }

    /**
     * Return the type of hand as a String
     * @return type of hand
     */
    public String getType() { return "Single"; }
}
