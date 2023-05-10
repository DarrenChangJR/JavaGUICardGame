/**
 * Models a deck of cards in a Big Two card game
 * @author Darren Chang JR
 */
public class BigTwoDeck extends Deck {
    /**
     * Initialises a deck of Big Two cards
     */
    public void initialize() {
        removeAllCards();
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 13; ++j) {
                addCard(new BigTwoCard(i, j));
            }
        }
    }
}
