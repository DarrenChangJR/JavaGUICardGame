/**
 * Models a card used in a Big Two card game
 * @author Darren Chang JR
 */
public class BigTwoCard extends Card {
    /**
     * Build a card with the given suit and rank
     * @param suit suit of card
     * @param rank rank of card
     */
    public BigTwoCard(int suit, int rank) {
        super(suit, rank);
    }

    
    /** 
     * Compares the order of this card with the given card
     * @param card card to be compared to
     * @return int negative integer, zero, or a positive integer when this card is less than, equal to, or greater than the specified card.
     */
    public int compareTo(Card card) {
        int thisBigTwoRank = this.rank, cardBigTwoRank = card.getRank();

        if (thisBigTwoRank < 2) thisBigTwoRank += 13;
        if (cardBigTwoRank < 2) cardBigTwoRank += 13;

        if (thisBigTwoRank > cardBigTwoRank) return 1;
        else if (thisBigTwoRank < cardBigTwoRank) return -1;
        else if (this.suit > card.getSuit()) return 1;
        else if (this.suit < card.getSuit()) return -1;
        else return 0;
    }
}
