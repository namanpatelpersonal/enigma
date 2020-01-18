package enigma;

import java.util.HashMap;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Naman Patel
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        String finalCycles = cycles;
        if (!(_cycles.equals(""))) {
            finalCycles = _cycles.replaceAll(" ", "");
            finalCycles = finalCycles.substring(1, finalCycles.length() - 1);
            cycleArrays = finalCycles.split("\\)\\(");
            for (String x : cycleArrays) {
                char[] charCycleArray = x.toCharArray();
                for (int i = 0; i < x.length(); i++) {
                    forPermMap.put(
                            charCycleArray[i], charCycleArray[
                                    (i + 1) % x.length()]);
                }
                for (int i = 0; i < x.length(); i++) {
                    if (i == 0) {
                        backPermMap.put(
                                charCycleArray[0],
                                charCycleArray[x.length() - 1]);
                    } else {
                        backPermMap.put(
                                charCycleArray[i], charCycleArray[i - 1]);
                    }
                }
            }
        }

        for (char ch : _alphabet.charArray) {
            if (!(forPermMap.containsKey(ch))) {
                forPermMap.put(ch, ch);
                backPermMap.put(ch, ch);
            }
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int x = wrap(p);
        char y = permute(_alphabet.toChar(x));
        return _alphabet.toInt(y);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int x = wrap(c);
        char y = invert(_alphabet.toChar(x));
        return _alphabet.toInt(y);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return forPermMap.get(p);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return backPermMap.get(c);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** String version of this permutation. */
    private String _cycles;

    /** String array of the cycles of this permutation. */
    private String[] cycleArrays;

    /** Hashmap of the forward mappings this permutation. */
    private HashMap<Character, Character> forPermMap = new HashMap<>();

    /** Hashmap of the backward mappings this permutation. */
    private HashMap<Character, Character> backPermMap = new HashMap<>();
}

