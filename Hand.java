/**
 * Models an abstract Hand used in a Big Two card game
 * @author Darren Chang JR
 */
public abstract class Hand extends CardList {
    private final CardGamePlayer player;

    /**
     * Build a Hand with the player and cards
     * @param player player of cards
     * @param cards cards played
     */
    public Hand(CardGamePlayer player, CardList cards) {
        this.player = player;
        for (int i = 0; i < cards.size(); ++i) {
            addCard(cards.getCard(i));
        }
        this.sort();
    }

    /**
     * Return the player of this hand
     * @return player of this hand
     */
    public CardGamePlayer getPlayer() { return player; }

    /**
     * Return the top card of this hand
     * @return top card of this hand
     */
    public Card getTopCard() {
        if (!this.isEmpty()) {
            this.sort();
            return (this.getCard(this.size() - 1));
        }
        return null;
    }

    /**
     * Check if this hand beats a specified hand
     * @param hand target hand to beat
     * @return true if this hand beats target false otherwise
     */
    public boolean beats(Hand hand) {        
        if (hand == null || !this.isValid() || !hand.isValid() || this.getType() != hand.getType())
            return false;
        return (this.getTopCard().compareTo(hand.getTopCard()) > 0);
    }

    /**
     * Check if hand is valid
     * @return true if hand is valid false otherwise
     */
    public abstract boolean isValid();

    /**
     * Return the type of hand as a String
     * @return type of hand
     */
    public abstract String getType();
}
