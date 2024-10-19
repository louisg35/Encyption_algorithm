package assignment2;

import java.util.Random;

public class Deck {
    public static String[] suitsInOrder = {"clubs", "diamonds", "hearts", "spades"};
    public static Random gen = new Random();

    public int numOfCards; // contains the total number of cards in the deck
    public Card head;

    public Deck(int numOfCardsPerSuit, int numOfSuits) {
        //Check if inputs are correct
        if (numOfCardsPerSuit < 1 || numOfCardsPerSuit > 13 || numOfSuits < 1 || numOfSuits > suitsInOrder.length) {
            throw new IllegalArgumentException("Invalid number of cards per suit or suits");
        }
        //Loop through suits
        for (int i = 0; i < numOfSuits; i++) {
            //Loop through num of cards
            for (int rank = 1; rank <= numOfCardsPerSuit; rank++) {

                //New card is the tail of the deck
                addCard(new PlayingCard(suitsInOrder[i], rank));
            }
        }
        //Add jokers
        addCard(new Joker("red"));
        addCard(new Joker("black"));
    }

    public Deck(Deck d) {
        //Make deep copy of each card
        Card current_card = d.head;

        //Loop through deck d to copy cards
        for (int card = 0; card < d.numOfCards; card++) {

            //Make a copy of current card ti add to the deck
            addCard(current_card.getCopy());

            //Move to next card
            current_card = current_card.next;

        }
    }

    public Deck() {
    }

    public void addCard(Card c) {

        if (this.head == null) {
            c.next = c;
            c.prev = c;
            this.head = c;
        } else {
            c.prev = this.head.prev;
            c.next = this.head;

            this.head.prev.next = c;
            this.head.prev = c;

        }
        numOfCards++;
    }

    public void shuffle() {
        // Create an array to store the cards
        Card[] cards = new Card[numOfCards];

        // Populate the array with cards from the deck
        Card current = head;
        for (int i = 0; i < numOfCards; i++) {
            cards[i] = current;
            current = current.next; // Move to the next card
        }

        // Fisher-Yates shuffle
        for (int i = numOfCards - 1; i > 0; i--) {
            int j = gen.nextInt(i + 1);
            Card temp = cards[i];
            cards[i] = cards[j];
            cards[j] = temp;
        }

        // Re-link the cards according to the shuffled array
        for (int i = 0; i < numOfCards; i++) {
            Card nextCard = (i == numOfCards - 1) ? cards[0] : cards[i + 1];
            Card prevCard = (i == 0) ? cards[numOfCards - 1] : cards[i - 1];

            cards[i].next = nextCard;
            cards[i].prev = prevCard;

            if (i == 0) head = cards[i]; // Update the head of the deck
            if (i == numOfCards - 1) head.prev = cards[i]; // Update the tail of the deck
        }
    }

    public Card locateJoker(String color) {
        Card current = head;

        //Loop through deck to find joker
        for (int card = 0; card < this.numOfCards; card++) {

            if (current instanceof Joker && ((Joker) current).getColor().equals(color)) {
                return (Joker) current;
            }
            current = current.next;
        }

        return null; // Joker not found
    }

    public void moveCard(Card c, int p) {
        Card current = c;

        c.prev.next = c.next; // If 'c' is not the head, adjust 'prev's 'next'.
        c.next.prev = c.prev; // If 'c' is not the tail, adjust 'next's 'prev'.


        for (int i = 0; i < p; i++) {
            current = current.next;
        }

        c.next = current.next;
        c.prev = current;
        current.next.prev = c;
        current.next = c;

    }

    public void tripleCut(Card firstCard, Card secondCard) {

        Card tail = head.prev;
        if (tail == secondCard) {
            head = firstCard;
            return;
        }
        if (head == firstCard) {
            head = secondCard.next;
            return;
        }
        Card beforeFirst = firstCard.prev; // Card before the first card
        Card afterSecond = secondCard.next; // Card after the second card

        beforeFirst.next = afterSecond;
        afterSecond.prev = beforeFirst;

        firstCard.prev = tail;
        tail.next = firstCard;

        secondCard.next = head;
        head.prev = secondCard;

        head = afterSecond;
    }


    /*
     * TODO: Performs a count cut on the deck. Note that if the value of the
     * bottom card is equal to a multiple of the number of cards in the deck,
     * then the method should not do anything. This method runs in O(n).
     */
    public void countCut() {
        /**** ADD CODE HERE ****/
        Card bottomCard = this.head.prev;
        int count = bottomCard.getValue();

        if ((count % (this.numOfCards - 1)) == 0) {
            return;
        }
        Card newBottom = this.head.prev;
        for (int i = 0; i < count; i++) {
            newBottom = newBottom.next;
        }

        Card prevHead = this.head;
        this.head = newBottom.next;

        bottomCard.next = this.head;
        this.head.prev = bottomCard;

        bottomCard.prev.next = prevHead;
        prevHead.prev = bottomCard.prev;

        bottomCard.prev = newBottom;
        newBottom.next = bottomCard;
    }



    /*
     * TODO: Returns the card that can be found by looking at the value of the
     * card on the top of the deck, and counting down that many cards. If the
     * card found is a Joker, then the method returns null, otherwise it returns
     * the Card found. This method runs in O(n).
     */
    public Card lookUpCard() {
        /**** ADD CODE HERE ****/
        // Check if the deck is empty
        if (head == null) {
            return null;
        }

        // Get the value from the top card
        int steps = this.head.getValue();
        Card current = this.head;

        for (int i = 0; i < steps; i++) {
            current = current.next;
        }

        // Check if the card is a Joker
        if (current instanceof Joker) {
            return null;
        }
        return current; // Return the card
        }

    /*
     * TODO: Uses the Solitaire algorithm to generate one value for the keystream
     * using this deck. This method runs in O(n).
     */
    public int generateNextKeystreamValue() {
        /**** ADD CODE HERE ****/
        // Step 1: Move the red joker down by one position
        moveCard(locateJoker("red"), 1);

        // Step 2: Move the black joker down by two positions
        moveCard(locateJoker("black"), 2);

        // Step 3: Perform a triple cut

        Card current = head;

        for (int i = 1; i < numOfCards; i++) {
            if (current instanceof Joker) {
                break;
            }
            //Update current card
            current = current.next;
        }
        if (((Joker) current).getColor().equals("red")) {
            tripleCut(locateJoker("red"), locateJoker("black"));
        }
        if (((Joker) current).getColor().equals("black")) {
            tripleCut(locateJoker("black"), locateJoker("red"));
        }

        // Step 4 : Perform a count cut
        countCut();

        int keystream_val = 0;

        Card keystream_card = lookUpCard();

        if (keystream_card instanceof Joker || keystream_card == null) {
            return generateNextKeystreamValue();

        } else {
            keystream_val = keystream_card.getValue();
        }
        return keystream_val;
    }



    public abstract class Card {
        public Card next;
        public Card prev;

        public abstract Card getCopy();

        public abstract int getValue();

    }

    public class PlayingCard extends Card {
        public String suit;
        public int rank;

        public PlayingCard(String s, int r) {
            this.suit = s.toLowerCase();
            this.rank = r;
        }

        public String toString() {
            String info = "";
            if (this.rank == 1) {
                //info += "Ace";
                info += "A";
            } else if (this.rank > 10) {
                String[] cards = {"Jack", "Queen", "King"};
                //info += cards[this.rank - 11];
                info += cards[this.rank - 11].charAt(0);
            } else {
                info += this.rank;
            }
            //info += " of " + this.suit;
            info = (info + this.suit.charAt(0)).toUpperCase();
            return info;
        }

        public PlayingCard getCopy() {
            return new PlayingCard(this.suit, this.rank);
        }

        public int getValue() {
            int i;
            for (i = 0; i < suitsInOrder.length; i++) {
                if (this.suit.equals(suitsInOrder[i]))
                    break;
            }

            return this.rank + 13 * i;
        }

    }

    public class Joker extends Card {
        public String redOrBlack;

        public Joker(String c) {
            if (!c.equalsIgnoreCase("red") && !c.equalsIgnoreCase("black"))
                throw new IllegalArgumentException("Jokers can only be red or black");

            this.redOrBlack = c.toLowerCase();
        }

        public String toString() {
            //return this.redOrBlack + " Joker";
            return (this.redOrBlack.charAt(0) + "J").toUpperCase();
        }

        public Joker getCopy() {
            return new Joker(this.redOrBlack);
        }

        public int getValue() {
            return numOfCards - 1;
        }

        public String getColor() {
            return this.redOrBlack;
        }
    }
}

