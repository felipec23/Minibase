package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Minimizer {


    public static void main(String[] args) {

        System.out.println("Args: " + args.length);

        // Print out the arguments

//        data/minimization/input/query1.txt data/minimization/output/query1.txt


        System.out.println("Args: " + args[0] + " " + args[1]);

        if (args.length != 2) {
            System.err.println("Usage: CQMinimizer input_file output_file");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        helperMinimizer(inputFile, outputFile);

//        parsingExample(inputFile);
    }

    /**
     * CQ minimization procedure
     *
     * Assume the body of the query from inputFile has no comparison atoms
     * but could potentially have constants in its relational atoms.
     *
     */


    public static boolean checkIfIsHead(Term term, List<Variable> headVariables){
        for (Variable variable : headVariables) {
            if (term.toString().equals(variable.getName())){
                return true;
            }
        }
        return false;
    }

//   Test the function, call it

//    Function to


    public static String helperMinimizer(String inputFile, String outputFile) {
        // TODO: add your implementation

        System.out.println("Input file: " + inputFile);
        System.out.println("Output file: " + outputFile);

        Query query = null;
        try {
            query = QueryParser.parse(Paths.get(inputFile));

        } catch (IOException e) {
            System.out.println("Error parsing query");
        }

        System.out.println("Entire query: " + query);
        Head head = query.getHead();
        System.out.println("Head: " + head);
        List<Atom> body = query.getBody();
        System.out.println("Body: " + body);

//          Get variables from head
        List<Variable> headVariables = head.getVariables();
        System.out.println("Head variables: " + headVariables);

//            A constant can only be mapped to itself, though I can map a variable to a constant

//            Iterate over the atoms of the body keeping track of the index
        for (int i = 0; i < body.size(); i++) {
//                Atom atom = body.get(i);
            RelationalAtom atom = (RelationalAtom) body.get(i);

//                PRINT MAIN LOOP
            System.out.println("\tMAIN LOOP: " + atom);

            // If atom is not a relational atom, skip it
            if (!(atom instanceof RelationalAtom)) {
                System.out.println("SKIP. Atom is not a relational atom");
                continue;
            }



//                    Get terms of atom, assign to a variable
            List<Term> terms = ((RelationalAtom) atom).getTerms();
            System.out.println("Relational atom terms: " + terms);

            // Check if any of the terms is a constant
            boolean isConstant = false;
            int constantPosition = -1;

            // Create variable for the constant
            Constant constant = null;
            for (Term term : terms) {
                if (term instanceof Constant) {
                    isConstant = true;
                    constantPosition = terms.indexOf(term);
                    constant = (Constant) term;
                    break;
                }
            }

            // If there is NOT constant, skip the atom
            if (!isConstant) {
                System.out.println("SKIP. Atom does not contain a constant");
                continue;
            }



            //                    Get length of terms, assign to a variable
            int termsLength = terms.size();
            System.out.println("Relational atom terms length: " + termsLength);

//

//                  Iterate over all the other ATOMS that are after the current atom
            for (int j = i + 1; j < body.size(); j++) {


//                      If the other atom is not a relational atom, skip it
                if (!(body.get(j) instanceof RelationalAtom)) {
                    System.out.println("Other atom is not a relational atom, skipping");
                    continue;
                }

                RelationalAtom otherAtom = (RelationalAtom) body.get(j);
                List<Term> otherTerms = otherAtom.getTerms();
                int otherTermsLength = otherTerms.size();

//                        Get the name of the other atom
                String otherAtomName = otherAtom.getName();

//                        Get the name of the current atom
                String atomName = atom.getName();

//                        Check if the name of the other atom is equal to the name of the current atom
                if (!(otherAtomName.equals(atomName))) {
                    System.out.println("SKIP. Other atom name is NOT equal to current atom name");
                    continue;
                }

                System.out.println("Other atom: " + otherAtom);
                System.out.println("Other atom name: " + otherAtom.getName());
                System.out.println("Other atom length: " + otherTermsLength);
                System.out.println("Current atom length: " + termsLength);
                System.out.println("Other atom terms: " + otherTerms);

                //                                  Check if the size of the terms of the other atom is equal to the size of the terms of the current atom
                if (!(otherTermsLength == termsLength)) {
                    System.out.println("SKIP. Other atom terms size is NOT equal to current atom terms size");
                    continue;
                }


                // In the other atom, I will replace the variable with the constant (on that index)

                List<Term> newOtherTerms = new ArrayList<>();

                Term replacedTerm = otherTerms.get(constantPosition);

                // If replacedTerm is a variable, check if it is in the head
                if (replacedTerm instanceof Variable) {
                    if (checkIfIsHead(replacedTerm, headVariables)) {
                        System.out.println("SKIP. Potential replacement term is a variable that is in the head");
                        continue;
                    }
                }


                System.out.println("Replaced term: " + replacedTerm);
                for (int k = 0; k < otherTermsLength; k++) {
                    if (k == constantPosition) {
                        newOtherTerms.add(terms.get(k));
                    } else {
                        newOtherTerms.add(otherTerms.get(k));
                    }
                }

                // Apply the potential replacement in the current atom:
                List<Term> newTerms = new ArrayList<>();
                for (int k = 0; k < termsLength; k++) {
                    if (k == constantPosition) {
                        newTerms.add(constant);
                    } else {
                        newTerms.add(terms.get(k));
                    }
                }

                // Apply the potential replacement in all the next atoms:
                for (int k = j + 1; k < body.size(); k++) {
                    RelationalAtom nextAtom = (RelationalAtom) body.get(k);
                    List<Term> nextTerms = nextAtom.getTerms();
                    List<Term> newNextTerms = new ArrayList<>();
                    for (int l = 0; l < nextTerms.size(); l++) {
                        if (nextTerms.get(l).equals(replacedTerm)) {
                            newNextTerms.add(constant);
                        } else {
                            newNextTerms.add(nextTerms.get(l));
                        }
                    }
                    RelationalAtom newTempAtom = new RelationalAtom(atomName, newTerms);
                    body.set(k, newTempAtom);
                }

                // Print both:
                System.out.println("New terms: " + newTerms);
                System.out.println("New other terms: " + newOtherTerms);

                // Compare newTerms and newOtherTerms, keep track of the differences:
                int differences = 0;
                for (int k = 0; k < termsLength; k++) {
                    if (newTerms.get(k).equals(newOtherTerms.get(k))) {
                        differences++;
                    }
                }

                // If differences > 1, skip the atom
                if (differences > 1) {
                    System.out.println("SKIP. Differences > 1");
                    continue;
                }

                // If differences == 1, replace the current atom with the other atom
                if (differences == 1) {

                    System.out.println("REPLACE. Doing both replacements.");

                    // Create a new atom with the new terms
                    RelationalAtom newAtom = new RelationalAtom(atomName, newTerms);

                    // Replace the current atom with the new atom
                    body.set(i, newAtom);

                    // Create a new atom with the new other terms\
                    RelationalAtom newOtherAtom = new RelationalAtom(otherAtomName, newOtherTerms);

                    // Replace the other atom with the new other atom
                    body.set(j, newOtherAtom);

                    // Print the new body
                    System.out.println("New body: " + body);



                }







            }


        }

        System.out.println("\n\nFINAL BODY: " + body);
//            return new Query(head, body);
        // Join the body atoms from the list of atoms with a comma
        StringBuilder bodyString = new StringBuilder();
        for (int i = 0; i < body.size(); i++) {
            if (i == body.size() - 1) {
                bodyString.append(body.get(i));
            } else {
                bodyString.append(body.get(i) + ", ");
            }
        }

        System.out.println("BODY STRING: " + bodyString);



        // Print the query to screen
        String stringToWrite = head + " :- " + bodyString.toString();

        return stringToWrite;



    }

    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static void writeToFile(String bodyString, String outputFileName) {
        try {
            FileWriter pw = new FileWriter(outputFileName, true);

            if (!bodyString.equals("")) {
                pw.write(bodyString);

            }

            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parsingExample(String filename) {

        try {
//            Query query = QueryParser.parse(Paths.get(filename));
            // Query query = QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w)");
            // Query query = QueryParser.parse("Q(x) :- R(x, 'z'), S(4, z, w)");
            Query query = QueryParser.parse(Paths.get(filename));

            System.out.println("Entire query: " + query);
            Head head = query.getHead();
            System.out.println("Head: " + head);
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);
        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

}
