package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author Naman Patel
 */
class FixedRotor extends Rotor {



    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM. */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
        _name = name;
        _permutation = perm;
        _setting = 0;
    }
    /** Common alphabet of my rotors. */
    private Permutation _permutation;

    /** My current setting. */
    private int _setting;

    /** My name. */
    private String _name;
}
