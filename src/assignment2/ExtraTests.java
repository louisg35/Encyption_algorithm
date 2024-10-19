package assignment2;

import org.junit.jupiter.api.*;
import java.util.Arrays;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class ExtraTests {

    // utils

    public String deckToString(Deck deck) {
        String[] result = new String[deck.numOfCards];
        Deck.Card currentCard = deck.head;
        for (int i = 0; i < result.length; i++) {
            result[i] = currentCard.toString();
            currentCard = currentCard.next;
        }
        return Arrays.toString(result);
    }

    public Deck stringToDeck(String cardList) {
        Deck newDeck = new Deck();
        newDeck.numOfCards = 0;
        String[] cardStrings = cardList.split(" ");
        Deck.Card[] cards = new Deck.Card[cardStrings.length];
        for (int i = 0; i < cardStrings.length; i++) {
            String cardStr = cardStrings[i];
            if (cardStr.equals("RJ")) {
                cards[i] = newDeck.new Joker("red");
            } else if (cardStr.equals("BJ")) {
                cards[i] = newDeck.new Joker("black");
            } else {
                String suit = "";
                int rank = 0;

                suit = switch (cardStr.charAt(cardStr.length() - 1)) {
                    case 'C' -> "clubs";
                    case 'H' -> "hearts";
                    case 'D' -> "diamonds";
                    case 'S' -> "spades";
                    default -> suit;
                };
                String rankStr = cardStr.substring(0, cardStr.length() - 1);
                rank = switch (rankStr) {
                    case "A" -> 1;
                    case "J" -> 11;
                    case "Q" -> 12;
                    case "K" -> 13;
                    default -> Integer.parseInt(rankStr);
                };
                cards[i] = newDeck.new PlayingCard(suit, rank);
            }
        }
        for (Deck.Card card : cards) {
            newDeck.addCard(card);
        }
        return newDeck;
    }

    public boolean[] testLinkage(String expectedFirst, String expectedLast, Deck testDeck) {
        boolean[] result = new boolean[4];

        // Check next pointers
        Deck.Card finalCard = testDeck.head;
        for (int i = 0; i < testDeck.numOfCards - 1; i++) {
            finalCard = finalCard.next;
        }
        result[0] = (expectedLast.equals(finalCard.toString()));

        // Check previous pointers
        Deck.Card firstCard = finalCard;
        for (int i = 0; i < testDeck.numOfCards - 1; i++) {
            firstCard = firstCard.prev;
        }
        result[1] = (expectedFirst.equals(firstCard.toString()));

        // Check that the deck is circular
        result[2] = expectedLast.equals(firstCard.prev.toString());
        result[3] = expectedFirst.equals(finalCard.next.toString());

        return result;
    }

    // tests

    @Test
    @DisplayName("Test deck exception handling")
    public void testDeckExceptionHandling() {
        // Check that numOfCardsPerSuit throws error if not within range [1,13]
        assertThrows(IllegalArgumentException.class,
                () -> {
                    Deck testDeck = new Deck(500, 3);
                }, "should throw an exception if numOfCardsPerSuit is not in range [1,13]");
        assertThrows(IllegalArgumentException.class,
                () -> {
                    Deck testDeck = new Deck(500, Deck.suitsInOrder.length + 1);
                }, "should throw an exception if numOfSuits is not in range [1, suitsInOrder.length]");
    }

    @Test
    @DisplayName("Test deck creation")
    public void testDeckCreation() {
        Deck testDeck = new Deck(4,3);
        String expectedDeck = "[AC, 2C, 3C, 4C, AD, 2D, 3D, 4D, AH, 2H, 3H, 4H, RJ, BJ]";
        assertTrue(deckToString(testDeck).equals(expectedDeck), "deck doesn't match expected");
    }

    @Test
    @DisplayName("Test numOfCards field")
    public void testDeckCreationFields() {
        /* A new deck with 2 suits and 4 cards each should have a total of 10 cards
           (8 suit cards, 2 jokers) */
        Deck testDeck = new Deck(4,2);
        int expectedNumOfCards = 10;
        assertEquals(expectedNumOfCards, testDeck.numOfCards, "numOfCards not initialized properly");
    }

    @Test
    @DisplayName("Test deck linkage")
    public void testDeckLinkage() {
        Deck testDeck = new Deck(4, 3);
        Deck.Card head = testDeck.head;

        boolean[] linkageTest = testLinkage("AC", "BJ", testDeck);
        assertTrue(linkageTest[0], "next fields not working properly");
        assertTrue(linkageTest[1], "previous fields not working");
        assertTrue(linkageTest[2], "firstCard.prev should be the end of the deck");
        assertTrue(linkageTest[3], "finalCard.next should be the head of the deck");

    }

    @Test
    @DisplayName("Test deck copy constructor")
    public void testDeckCopyConstructor() {
        Deck testDeck = new Deck(3,3);
        Deck copyDeck = new Deck(testDeck);
        assertEquals(deckToString(testDeck), deckToString(copyDeck), "copy was not the same as original");
    }

    @Test
    @DisplayName("Test deck copy constructor deep copy")
    public void testDeckCopyConstructorDeepCopy() {
        Deck testDeck = new Deck(3,3);
        Deck copyDeck = new Deck(testDeck);
        // the head of copyDeck should be a different object than the original
        assertFalse(testDeck.head.equals(copyDeck.head), "copy was not a deepcopy");
    }

    @Test
    @DisplayName("Test addCard")
    public void testAddCard() {
        Deck testDeck = new Deck(1,1);
        Deck.Card cardToAdd = new Deck().new PlayingCard("clubs", 4);
        testDeck.addCard(cardToAdd);
        assertEquals("[AC, RJ, BJ, 4C]", deckToString(testDeck), "card was not added" +
                " OR numOfCards field was not updated");
    }

    @Test
    @DisplayName("Test addCard linkage")
    public void testAddCardLinkage() {
        Deck testDeck = new Deck(1,1);
        Deck.Card cardToAdd = new Deck().new PlayingCard("clubs", 4);
        Deck.Card oldTail = testDeck.head.prev;
        testDeck.addCard(cardToAdd);
        assertEquals(cardToAdd, testDeck.head.prev, "head.prev should be the new card");
        assertEquals(testDeck.head, testDeck.head.prev.next, "head.prev.next should be the head");
        assertEquals(oldTail, testDeck.head.prev.prev, "head.prev.prev should be the old tail");
        assertEquals(cardToAdd, testDeck.head.prev.prev.next, "head.prev.prev.next should be the new card");
    }

    @Test
    @DisplayName("Test shuffle")
    public void testShuffle() {
        Deck testDeck = new Deck(5,2);
        Random oldGen = Deck.gen;
        Deck.gen = new Random(10);
        testDeck.shuffle();
        Deck.gen = oldGen;
        String expectedDeck = "[3C, 3D, AD, 5C, BJ, 2C, 2D, 4D, AC, RJ, 4C, 5D]";
        assertEquals(expectedDeck, deckToString(testDeck), "deck didn't shuffle as expected");
    }

    @Test
    @DisplayName("Test shuffle linkage")
    public void testShuffleLinkage() {
        Deck testDeck = new Deck(4,3);
        Random oldGen = Deck.gen;
        Deck.gen = new Random(1);
        testDeck.shuffle();
        Deck.gen = oldGen;

        boolean[] linkageTest = testLinkage("2D", "4H", testDeck);
        assertTrue(linkageTest[0], "next fields not working properly after shuffle");
        assertTrue(linkageTest[1], "previous fields not working properly after shuffle");
        assertTrue(linkageTest[2], "circular link from head.prev to tail broken after shuffle");
        assertTrue(linkageTest[3], "circular link from tail.next to head broken after shuffle");
    }

    @Test
    @DisplayName("Test locateJoker")
    public void testLocateJoker() {
        Deck testDeck = new Deck(1,1);
        Deck.Joker redJoker = (Deck.Joker) testDeck.head.next;
        Deck.Joker blackJoker = (Deck.Joker) testDeck.head.next.next;
        assertEquals(redJoker, testDeck.locateJoker("red"), "didn't find the red joker");
        assertEquals(blackJoker, testDeck.locateJoker("black"), "didn't find the black joker");
    }

    @Test
    @DisplayName("Test locateJoker no joker")
    public void testLocateJokerNoJoker() {
        Deck testDeck = stringToDeck("AC AC AC AC AC");
        assertNull(testDeck.locateJoker("red"));
        assertNull(testDeck.locateJoker("black"));
    }

    @Test
    @DisplayName("Test moveCard non-head case")
    public void testMoveCardNonHead() {
        Deck testDeck = new Deck(2,2);
        testDeck.moveCard(testDeck.head.next, 2);
        String expectedDeck = "[AC, AD, 2D, 2C, RJ, BJ]";
        assertEquals(expectedDeck, deckToString(testDeck), "card did not move down properly");
    }

    @Test
    @DisplayName("Test moveCard non-head case linkage")
    public void testMoveCardNonHeadLinkage() {
        Deck testDeck = new Deck(2,2);
        testDeck.moveCard(testDeck.head.next, 7);

        boolean[] linkageTest = testLinkage("AC", "BJ", testDeck);
        assertTrue(linkageTest[0], "next fields not working properly after moveCardNonHead");
        assertTrue(linkageTest[1], "previous fields not working properly after moveCardNonHead");
        assertTrue(linkageTest[2], "circular link from head.prev to tail broken after moveCardNonHead");
        assertTrue(linkageTest[3], "circular link from tail.next to head broken after moveCardNonHead");
    }

    /* If we call moveCard with a p value greater than the remaining spots below on the list, we need to
       move back to the top, skipping the head, and then move down more.*/

    @Test
    @DisplayName("Test moveCard with large p value")
    public void testMoveCardLargePValue() {
        Deck testDeck = new Deck(2,2);
        testDeck.moveCard(testDeck.head.next, 7);
        String expectedDeck = "[AC, AD, 2D, 2C, RJ, BJ]";
        assertEquals(expectedDeck, deckToString(testDeck), "card did not move down properly (large p value)");
    }

    @Test
    @DisplayName("Test moveCard head case")
    public void testMoveCardHead() {
        Deck testDeck = new Deck(2,2);
        testDeck.moveCard(testDeck.head, 2);
        String expectedDeck = "[AC, 2D, RJ, BJ, 2C, AD]";
        assertEquals(expectedDeck, deckToString(testDeck));
    }

    @Test
    @DisplayName("Test moveCard head case linkage")
    public void testMoveCardHeadLinkage() {
        Deck testDeck = new Deck(2,2);
        testDeck.moveCard(testDeck.head, 2);

        boolean[] linkageTest = testLinkage("AC", "AD", testDeck);
        assertTrue(linkageTest[0], "next fields not working properly after moveCardHead");
        assertTrue(linkageTest[1], "previous fields not working properly after moveCardHead");
        assertTrue(linkageTest[2], "circular link from head.prev to tail broken after moveCardHead");
        assertTrue(linkageTest[3], "circular link from tail.next to head broken after moveCardHead");
    }

    @Test
    @DisplayName("Test tripleCut")
    public void testTripleCut() {
        Deck testDeck = stringToDeck("AC 4C 7C 10C KC 3D 6D 9D QD 3C 6C BJ 9C QC 2D 5D 8D JD 2C RJ 5C 8C JC AD 4D 7D 10D KD");
        testDeck.tripleCut(testDeck.locateJoker("black"), testDeck.locateJoker("red"));
        String expectedDeck = deckToString(stringToDeck("5C 8C JC AD 4D 7D 10D KD BJ 9C QC 2D 5D 8D JD 2C RJ AC 4C 7C 10C KC 3D 6D 9D QD 3C 6C"));
        assertEquals(expectedDeck, deckToString(testDeck), "triple cut didn't work properly");
    }

    @Test
    @DisplayName("Test tripleCut linkage")
    public void testTripleCutLinkage() {
        Deck testDeck = new Deck(2,2);
        testDeck.tripleCut(testDeck.head.next, testDeck.head.next.next.next);

        boolean[] linkageTest = testLinkage("RJ", "AC", testDeck);
        assertTrue(linkageTest[0], "next fields not working properly after tripleCut");
        assertTrue(linkageTest[1], "previous fields not working properly after tripleCut");
        assertTrue(linkageTest[2], "circular link from head.prev to tail broken after tripleCut");
        assertTrue(linkageTest[3], "circular link from tail.next to head broken after tripleCut");
    }

    @Test
    @DisplayName("Test countCut")
    public void testCountCut() {
        Deck testDeck = stringToDeck("5C 8C JC AD 4D 7D 10D KD BJ 9C QC 2D 5D 8D JD 2C RJ AC 4C 7C 10C KC 3D 6D 9D QD 3C 6C");
        testDeck.countCut();
        String expectedDeck = deckToString(stringToDeck("10D KD BJ 9C QC 2D 5D 8D JD 2C RJ AC 4C 7C 10C KC 3D 6D 9D QD 3C 5C 8C JC AD 4D 7D 6C"));
        assertEquals(expectedDeck, deckToString(testDeck), "countCut didn't work properly");
    }

    @Test
    @DisplayName("Test countCut linkage")
    public void testCountCutLinkage() {
        Deck testDeck = new Deck(2,2);
        Deck.PlayingCard cardToAdd = new Deck().new PlayingCard("spades", 1);
        testDeck.addCard(cardToAdd);
        testDeck.countCut();

        boolean[] linkageTest = testLinkage("BJ", "AS", testDeck);
        assertTrue(linkageTest[0], "next fields not working properly after countCut");
        assertTrue(linkageTest[1], "previous fields not working properly after countCut");
        assertTrue(linkageTest[2], "circular link from head.prev to tail broken after tripleCut");
        assertTrue(linkageTest[3], "circular link from tail.next to head broken after tripleCut");
    }

    @Test
    @DisplayName("Test lookUpCard")
    public void testLookUpCard() {
        Deck testDeck = new Deck(5,2);
        Random oldGen = Deck.gen;
        Deck.gen = new Random(10);
        testDeck.shuffle();
        Deck.gen = oldGen;
        String expectedCard = "5C";
        assertEquals(expectedCard, testDeck.lookUpCard().toString(), "didn't find the expected card");
    }

    @Test
    @DisplayName("Test lookUpCard joker")
    public void testLookUpCardJoker() {
        Deck testDeck = stringToDeck("3C AC AD RJ AC AD AC AD");
        assertNull(testDeck.lookUpCard(), "make sure to return null if you find a joker");
    }

    @Test
    @DisplayName("Test generateNextKeystreamValue")
    public void testGenerateNextKeystreamValue() {
        Deck testDeck = stringToDeck("AC 4C 7C 10C KC 3D 6D 9D QD BJ 3C 6C 9C QC 2D 5D 8D JD RJ 2C 5C 8C JC AD 4D 7D 10D KD");
        assertEquals(11, testDeck.generateNextKeystreamValue());
    }

    @Test
    @DisplayName("Test getKeyStream")
    public void testGetKeyStream() {
        Deck keyDeck = stringToDeck("AC 4C 7C 10C KC 3D 6D 9D QD BJ 3C 6C 9C QC 2D 5D 8D JD RJ 2C 5C 8C JC AD 4D 7D 10D KD");
        SolitaireCipher keyGen = new SolitaireCipher(keyDeck);
        int[] keyStream = keyGen.getKeystream(12);
        String expectedKeys = Arrays.toString(new int[]{11, 9, 23, 7, 10, 25, 11, 11, 7, 8, 9, 3});
        assertEquals(expectedKeys, Arrays.toString(keyStream));
    }

    @Test
    @DisplayName("Test encode")
    public void testEncode() {
        Deck keys = stringToDeck("AC 4C 7C 10C KC 3D 6D 9D QD BJ 3C 6C 9C QC 2D 5D 8D JD RJ 2C 5C 8C JC AD 4D 7D 10D KD");
        SolitaireCipher keyGen = new SolitaireCipher(keys);
        String encodedMsg = keyGen.encode("IsthatyouBob");
        String expectedMsg = "TBQOKSJZBJXE";
        assertEquals(expectedMsg, encodedMsg);
    }

    @Test
    @DisplayName("Test decode")
    public void testDecode() {
        Deck keyss = stringToDeck("AC 4C 7C 10C KC 3D 6D 9D QD BJ 3C 6C 9C QC 2D 5D 8D JD RJ 2C 5C 8C JC AD 4D 7D 10D KD");
        SolitaireCipher keyGen = new SolitaireCipher(keyss);
        String decodedMsg = keyGen.decode("TBQOKSJZBJXE");
        String expectedMsg = "ISTHATYOUBOB";
        assertEquals(expectedMsg, decodedMsg);
    }

    @Test
    @DisplayName("Test moving card to original position")
    public void testMoveCardOriginal() {
        Deck testDeck = new Deck(2, 2);
        testDeck.moveCard(testDeck.head.next, 5);
        String expectedDeck = "[AC, 2C, AD, 2D, RJ, BJ]";

        assertEquals(expectedDeck, deckToString(testDeck), "card did not move down properly (back to original position)");

        boolean[] linkageTest = testLinkage("AC", "BJ", testDeck);
        assertTrue(linkageTest[0], "next fields not working properly after moveCardNonHead");
        assertTrue(linkageTest[1], "previous fields not working properly after moveCardNonHead");
        assertTrue(linkageTest[2], "circular link from head.prev to tail broken after moveCardNonHead");
        assertTrue(linkageTest[3], "circular link from tail.next to head broken after moveCardNonHead");

    }

    /*
     * Moving the card only one position down
     */
    @Test
    @DisplayName("Test moving card just one position down")
    public void testMoveCardOne() {
        Deck testDeck = new Deck(2, 2);
        testDeck.moveCard(testDeck.head.next, 1);
        String expectedDeck = "[AC, AD, 2C, 2D, RJ, BJ]";

        assertEquals(expectedDeck, deckToString(testDeck), "card did not move down properly (1 pos down)");
        boolean[] linkageTest = testLinkage("AC", "BJ", testDeck);

        assertTrue(linkageTest[0], "next fields not working properly after moveCardNonHead");
        assertTrue(linkageTest[1], "previous fields not working properly after moveCardNonHead");
        assertTrue(linkageTest[2], "circular link from head.prev to tail broken after moveCardNonHead");
        assertTrue(linkageTest[3], "circular link from tail.next to head broken after moveCardNonHead");

    }


    @Test
    @DisplayName("Test tripleCut with empty section before first card")
    public void testTripleCutEmptyFirst() {
        Deck testDeck = stringToDeck("RJ AC 2C 3D QD BJ 2D 4C");
        testDeck.tripleCut(testDeck.head, testDeck.head.prev.prev.prev);
        String expectedDeck = "[2D, 4C, RJ, AC, 2C, 3D, QD, BJ]";
        assertEquals(expectedDeck, deckToString(testDeck));

        boolean[] linkageTest = testLinkage("2D", "BJ", testDeck);
        assertTrue(linkageTest[0], "next fields not working properly after tripleCut");
        assertTrue(linkageTest[1], "previous fields not working properly after tripleCut");
        assertTrue(linkageTest[2], "circular link from head.prev to tail broken after tripleCut");
        assertTrue(linkageTest[3], "circular link from tail.next to head broken after tripleCut");
    }


    @Test
    @DisplayName("Test tripleCut with empty section after second card")
    public void testTripleCutEmptyEnd() {
        Deck testDeck = stringToDeck("AC 2C RJ 3D QD BJ");
        testDeck.tripleCut(testDeck.head.next.next, testDeck.head.prev);
        String expectedDeck = "[RJ, 3D, QD, BJ, AC, 2C]";
        assertEquals(expectedDeck, deckToString(testDeck));

        boolean[] linkageTest = testLinkage("RJ", "2C", testDeck);
        assertTrue(linkageTest[0], "next fields not working properly after tripleCut");
        assertTrue(linkageTest[1], "previous fields not working properly after tripleCut");
        assertTrue(linkageTest[2], "circular link from head.prev to tail broken after tripleCut");
        assertTrue(linkageTest[3], "circular link from tail.next to head broken after tripleCut");

    }

    @Test
    @DisplayName("Test tripleCut with empty section before and after first card")
    public void testTripleCutEmptyEnds() {
        Deck testDeck = stringToDeck("RJ 3D QD AC 2C BJ");
        testDeck.tripleCut(testDeck.head, testDeck.head.prev);
        String expectedDeck = "[RJ, 3D, QD, AC, 2C, BJ]";
        assertEquals(expectedDeck, deckToString(testDeck));

        boolean[] linkageTest = testLinkage("RJ", "BJ", testDeck);
        assertTrue(linkageTest[0], "next fields not working properly after tripleCut");
        assertTrue(linkageTest[1], "previous fields not working properly after tripleCut");
        assertTrue(linkageTest[2], "circular link from head.prev to tail broken after tripleCut");
        assertTrue(linkageTest[3], "circular link from tail.next to head broken after tripleCut");

    }

    @Test
    @DisplayName("Test generateNextKeystreamValueJokerCase")
    public void testGenerateNextKeystreamValueJokerCase() {
        Deck testDeck = stringToDeck("9C QC 2D 5D 8D JD 2C JC AC 4C 7C 10C KC 3D 6D 9D QD 3C RJ 6C AD 4D 7D BJ 10D KD 5C 8C");
        Deck trueValueDeck = stringToDeck("10D KD BJ 9C QC 2D 5D 8D JD 2C JC AC 4C 7C 10C KC 3D 6D 9D QD 3C 5C 8C RJ AD 4D 7D 6C");
        int trueValue = trueValueDeck.generateNextKeystreamValue();
        assertEquals(trueValue, testDeck.generateNextKeystreamValue());
    }

    @Test
    @DisplayName("Test moveCard when input is the head and p value is large")
    public void testMoveHeadLargePValue() {
        Deck testDeck = new Deck(2, 2);
        testDeck.moveCard(testDeck.head, 8);
        String expectedDeck = "[AC, RJ, BJ, 2C, AD, 2D]";

        assertEquals(expectedDeck, deckToString(testDeck), "cards did not move to bottom properly (head + large p)");
        boolean[] linkageTest = testLinkage("AC", "2D", testDeck);

        assertTrue(linkageTest[0], "next fields not working properly after testMoveHeadLargePValue");
        assertTrue(linkageTest[1], "previous fields not working properly after testMoveHeadLargePValue");
        assertTrue(linkageTest[2], "circular link from head.prev to tail broken after testMoveHeadLargePValue");
        assertTrue(linkageTest[3], "circular link from tail.next to head broken after testMoveHeadLargePValue");
    }

}


