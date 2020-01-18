package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Naman Patel
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _name = name;
        _permutation = perm;
        _notches = notches;
    }

    /** A string that tells where my notches are.
     * @return hgh*/
    String getnotches() {
        return _notches;
    }

    @Override
    boolean atNotch() {
        char[] notchArray = _notches.replaceAll(" ", "").toCharArray();
        for (char ch : notchArray) {
            if (setting() == alphabet().toInt(ch)) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        set(_permutation.wrap(setting() + 1));
    }

    @Override
    boolean rotates() {
        return true;
    }

    /** My permutation. */
    private Permutation _permutation;

    /** My current setting. */
    private int _setting;

    /** My name. */
    private String _name;

    /** A string of my notches. */
    private String _notches;

}
