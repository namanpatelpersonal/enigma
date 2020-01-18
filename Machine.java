package enigma;

import java.util.HashMap;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Naman Patel
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numrotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numrotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        HashMap<String, Rotor> allRotors = new HashMap<String, Rotor>();
        for (Rotor x : _allRotors) {
            allRotors.put(x.name(), x);
        }
        myRotors = new Rotor[rotors.length];
        for (int i = 0; i < rotors.length; i++) {
            if (allRotors.containsKey(rotors[i])) {
                myRotors[i] = allRotors.get(rotors[i]);
            } else {
                throw new EnigmaException("misnamed rotors");
            }
        }
        for (int i = 0; i < rotors.length; i++) {
            if (i == 0) {
                if (!myRotors[0].reflecting()) {
                    throw new EnigmaException("reflector in wrong place");
                }
            } else {
                if (myRotors[i].reflecting()) {
                    throw new EnigmaException("reflector in wrong place");
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != myRotors.length - 1) {
            throw new EnigmaException("not valid settings");
        }
        char[] settingChar = setting.toCharArray();
        for (int i = 1; i <= myRotors.length - 1; i++) {
            myRotors[i].set(settingChar[i - 1]);
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        int lastRotor = numRotors() - 1;
        boolean[] toAdvance = new boolean[numRotors()];
        toAdvance[0] = false;
        toAdvance[lastRotor] = true;
        for (int i = lastRotor; i > 0; i--) {
            if (myRotors[i].atNotch() && myRotors[i - 1].rotates()) {
                toAdvance[i] = true;
                toAdvance[i - 1] = true;
            }
        }
        for (int i = 0; i < toAdvance.length; i++) {
            if (toAdvance[i]) {
                myRotors[i].advance();
            }
        }
        for (boolean x : toAdvance) {
            x = false;
        }
        int updatedInt = _plugboard.permute(c);
        for (int i = numRotors() - 1; i >= 0; i -= 1) {
            updatedInt = myRotors[i].convertForward(updatedInt);
        }
        for (int i = 1; i < myRotors.length; i += 1) {
            updatedInt = myRotors[i].convertBackward(updatedInt);
        }
        updatedInt = _plugboard.permute(updatedInt);
        return updatedInt;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        msg = msg.replaceAll(" ", "");
        char[] charArray = new char[msg.length()];
        for (int i = 0; i < msg.length(); i++) {
            char updatedChar = _alphabet.toChar(
                    convert(_alphabet.toInt(msg.charAt(i))));
            charArray[i] = updatedChar;
        }
        return String.valueOf(charArray);
    }


    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** My total number of rotors. */
    protected int _numrotors;

    /** My total number of pawls. */
    private final int _pawls;

    /** Collection of all possible rotors. */
    protected final Collection<Rotor> _allRotors;

    /** Rotor array of my rotors. */
    private Rotor[] myRotors;

    /** The permutation of my plugboard specifications. */
    private Permutation _plugboard;
}
