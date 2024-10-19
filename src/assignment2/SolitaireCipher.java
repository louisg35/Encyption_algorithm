package assignment2;
public class SolitaireCipher {
    public Deck key;

    public SolitaireCipher(Deck key) {
        this.key = new Deck(key); // deep copy of the deck
    }

    /*
     * TODO: Generates a keystream of the given size
     */
    public int[] getKeystream(int size) {
        /**** ADD CODE HERE ****/
        int[] keystream = new int[size];

        for (int i = 0; i < size; i++) {
            // Assuming there's a method in the Deck class that generates the next keystream value
            keystream[i] = key.generateNextKeystreamValue();
        }
        return keystream;
    }

    /*
     * TODO: Encodes the input message using the algorithm described in the pdf.
     */
    public String encode(String msg) {
        /**** ADD CODE HERE ****/
        StringBuilder msgEncoded = new StringBuilder();
        // Remove all non-letter characters from the message and convert to uppercase
        String cleanedMsg = msg.replaceAll("[^a-zA-Z]", "").toUpperCase();
        int[] keystream = getKeystream(cleanedMsg.length());
        int keystreamIndex = 0;

        for (char ch : cleanedMsg.toCharArray()) {
            int charValue = ch - 'A';
            int keyValue = keystream[keystreamIndex++];
            int encodedValue = (charValue + keyValue) % 26;
            char encodedChar = (char) ('A' + encodedValue);
            msgEncoded.append(encodedChar);
        }
        return msgEncoded.toString();
    }

    /*
     * TODO: Decodes the input message using the algorithm described in the pdf.
     */
    public String decode(String msg) {
        /**** ADD CODE HERE ****/
        StringBuilder msgDecoded = new StringBuilder();
        // Assume msg only contains letters. If not, consider preprocessing similar to encode.
        int[] keystream = getKeystream(msg.length());
        int keystreamIndex = 0;

        for (char ch : msg.toCharArray()) {
            if (Character.isLetter(ch)) {
                int charValue = ch - 'A';
                int keyValue = keystream[keystreamIndex++];
                int decodedValue = (charValue - keyValue + 26) % 26; // Add 26 to ensure positivity
                char decodedChar = (char) ('A' + decodedValue);
                msgDecoded.append(decodedChar);
            }
        }
        return msgDecoded.toString();
    }
}




