package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

import static enigma.EnigmaException.*;


/** Enigma simulator.
 *  @author Naman Patel
 */


public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */


    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }



/** Check ARGS and open the necessary files (see comment on main). */


    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }



    /** Return a Scanner reading from the file named NAME. */


    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }



    /** Return a PrintStream writing to the file named NAME. */


    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }



    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */

    private void process() {
        Machine myMachine = readConfig();
        int group = 0;
        int entryPoint = 1;

        String nextItem = _input.nextLine();
        while (entryPoint == 1) {
            String rotorLine = nextItem;
            if (!rotorLine.startsWith("*")) {
                throw new EnigmaException(
                        "invalid input file, invalid rotor settings");
            }
            ArrayList<String> allLines = new ArrayList<>();
            allLines.add(rotorLine);
            if (!_input.hasNextLine()) {
                throw new EnigmaException(
                        "invalid input file, no message line");
            } else {
                while (_input.hasNextLine()) {
                    nextItem = _input.nextLine();
                    if (nextItem.startsWith("*")) {
                        entryPoint = 1;
                        break;
                    }

                    nextItem = nextItem.replace(" ", "");
                    allLines.add(nextItem);
                    entryPoint = 0;
                }
            }
            settingLineGroup.put(group, allLines);
            group = group + 1;
        }

        for (Map.Entry<Integer, ArrayList<String>>
                groupEntry : settingLineGroup.entrySet()) {
            ArrayList<String> lines = groupEntry.getValue();
            String rotorSettings = lines.get(0);
            setUp(myMachine, rotorSettings);
            for (int i = 1; i < lines.size(); i++) {
                StringBuilder toreturn = new StringBuilder();
                for (int j = 0; j < lines.get(i).length(); j++) {
                    String currentLine = lines.get(i);
                    char currentChar = currentLine.charAt(j);
                    int converted = myMachine.convert(
                            _alphabet.toInt(currentChar));
                    toreturn.append(_alphabet.toChar(converted));
                }
                if (toreturn.toString().equals("")) {
                    _output.println();
                }
                printMessageLine(toreturn.toString());
            }
        }

    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */

    private Machine readConfig() {
        try {
            String myAlphabet = _config.next();
            if (myAlphabet.contains("*") || myAlphabet.contains("(")
                    || myAlphabet.contains(")")) {
                throw new EnigmaException(
                        "incompatible configuration file, invalid alphabet");
            }
            _alphabet = new Alphabet(myAlphabet);
            if (_config.hasNextInt()) {
                mainnumRotors = _config.nextInt();
            } else {
                throw new EnigmaException(
                        "incompatible configuration file, invalid numRotors");
            }
            if (_config.hasNextInt()) {
                numPawls = _config.nextInt();
            } else {
                throw new EnigmaException(
                        "incompatible configuration file, invalid numPawls");
            }
            boolean x = true;
            while (_config.hasNext()) {
                if (x) {
                    rotorName = _config.next();
                    x = false;
                } else {
                    rotorName = oneCycle;
                }
                typeAndnotch = _config.next();
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, mainnumRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }


    /** Return a rotor, reading its description from _config. */

    private Rotor readRotor() {
        try {
            oneCycle = _config.next();
            String y = "";
            String complete = "";
            while (oneCycle.startsWith("(")) {
                complete = complete + " " + oneCycle;
                if (!_config.hasNext()) {
                    y = complete;
                    break;
                } else {
                    oneCycle = _config.next();
                }
                y = complete;
            }
            String x = y.substring(1);
            Permutation myPermutation = new Permutation(x, _alphabet);
            if (typeAndnotch.startsWith("N")) {
                return new FixedRotor(rotorName, myPermutation);
            } else if (typeAndnotch.startsWith("M")) {
                return new MovingRotor(rotorName, myPermutation,
                        typeAndnotch.substring(1));
            } else {
                return new Reflector(rotorName, myPermutation);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner rotorLine = new Scanner(settings);
        String skipAsterisk = rotorLine.next();
        String[] rotors = new String[mainnumRotors];

        String newItem = rotorLine.next();
        for (int i = 0; i < mainnumRotors; i++) {
            for (Rotor current : M._allRotors) {
                if (current.name().equals(newItem)) {
                    rotors[i] = newItem;
                }
            }
            newItem = rotorLine.next();
        }
        String initialSettings = newItem;
        String plugboard = "";
        while (rotorLine.hasNext()) {
            newItem = rotorLine.next();
            plugboard = plugboard + newItem + " ";
        }
        Permutation plugboardPerm = new Permutation(plugboard, _alphabet);
        M.insertRotors(rotors);
        M.setRotors(initialSettings);
        M.setPlugboard(plugboardPerm);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i += 5) {
            if (msg.length() - i <= 5) {
                _output.println(msg.substring(i, i + msg.length() - i));
            } else {
                _output.print(msg.substring(i, i + 5) + " ");
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Collection of all rotors. */
    private Collection<Rotor> allRotors = new ArrayList<>();

    /** String of rotor type and notch position. */
    private String typeAndnotch;

    /** String of next cycle to be added. */
    private String oneCycle;

    /** Total number of my pawls. */
    private int numPawls;

    /** Total number of rotors in my machine. */
    private int mainnumRotors;

    /** Name of the rotor being constructed. */
    private String rotorName;

    /** Hashmap of rotor setting and text inputs mapped to group of text. */
    private HashMap<Integer, ArrayList<String>> settingLineGroup =
            new HashMap<Integer, ArrayList<String>>();
}
