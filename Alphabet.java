package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Naman Patel
 */
class Alphabet {
    /**
     * A new alphabet containing CHARS.  Character number #k has index
     * K (numbering from 0). No character may be duplicated.
     */
    Alphabet(String chars) {
        _chars = chars;
        String[] stringArray = chars.split("");
        charArray = _chars.toCharArray();
    }

    /**
     * A default alphabet of all upper-case characters.
     */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /**
     * Returns the size of the alphabet.
     */
    int size() {
        return _chars.length();
    }

    /**
     * Returns true if CH is in this alphabet.
     */
    boolean contains(char ch) {
        return _chars.contains(String.valueOf(ch));
    }

    /**
     * Returns character number INDEX in the alphabet, where
     * 0 <= INDEX < size().
     */
    char toChar(int index) {
        if (0 > index || this.size() <= index) {
            throw new EnigmaException("illegal argument error");
        } else {
            return charArray[index];
        }
    }

    /** Returns the index of character CH, which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        for (int i = 0; i < _chars.length(); i++) {
            if (ch == _chars.charAt(i)) {
                return i;
            }
        }
        throw new EnigmaException("character not found");
    }

    /** String of characters in my alphabet. */
    private final String _chars;

    /** Char array of characters in my alphabet. */
    protected char[] charArray;

}
