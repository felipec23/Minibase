package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;



/**
 *
 * Minimization of conjunctive queries
 *
 */
public class CQMinimizer {

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

        minimizeCQ(inputFile, outputFile);

//        parsingExample(inputFile);
    }

    /**
     * CQ minimization procedure
     *
     * Assume the body of the query from inputFile has no comparison atoms
     * but could potentially have constants in its relational atoms.
     *
     */

//    Create a function that checks if the string of a term is equal to a head variable
    public static boolean isInHeadVariables(String term, List<Variable> headVariables){
        for (Variable variable : headVariables) {
            if (term.equals(variable.getName())){
                return true;
            }
        }
        return false;
    }

    public static boolean isHeadVariable(Term term, List<Variable> headVariables){
        if (headVariables.contains(term)){
            return true;
        }
        return false;
    }

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


    public static void minimizeCQ(String inputFile, String outputFile) {
        // TODO: add your implementation

        System.out.println("Input file: " + inputFile);
        System.out.println("Output file: " + outputFile);

        Query query = null;
        try {
            query = QueryParser.parse(Paths.get(inputFile));

            System.out.println("Entire query: " + query);
            Head head = query.getHead();
            System.out.println("Head: " + head);
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);

//          Get variables from head
            List<Variable> headVariables = head.getVariables();
            System.out.println("Head variables: " + headVariables);

//            Iterate over head variables
            for (Variable variable : headVariables) {
                System.out.println("Variable: " + variable);

//                Print name of variable
                System.out.println("Variable name: " + variable.getName());

//                Print class of variable
                System.out.println("Variable class: " + variable.getClass());

            }

//            Check if variable

            System.out.println("Head name: " + head.getName());

            // Iterate through the body of the query
            for (Atom atom : body) {

//
                if (atom instanceof RelationalAtom){
                    System.out.println("Relational atom: " + atom);
                }
                else if (atom instanceof ComparisonAtom){
                    System.out.println("Comparison atom: " + atom);
                }
                else {
                    System.out.println("Unknown atom: " + atom);
                }
                System.out.println("Atom: " + atom);
                System.out.println("Atom as string: " + atom.toString());

            }

//          If the atom is a relational atom, get the name of the atom
            for (Atom atom : body) {
                if (atom instanceof RelationalAtom){
                    System.out.println("Relational atom: " + atom);
                    System.out.println("Relational atom name: " + ((RelationalAtom) atom).getName());
                }
            }

//          If the atom is a relational atom, get the terms of the atom
            for (Atom atom : body) {
                if (atom instanceof RelationalAtom){
                    System.out.println("Relational atom: " + atom);

                    System.out.println("Relational atom terms: " + ((RelationalAtom) atom).getTerms());
                }
            }

//            If the atom is a relational atom, get the name of the atom and the terms of the atom
            for (Atom atom : body) {
                if (atom instanceof RelationalAtom){
                    System.out.println("Relational atom: " + atom);
                    System.out.println("Relational atom name: " + ((RelationalAtom) atom).getName());
                    System.out.println("Relational atom terms: " + ((RelationalAtom) atom).getTerms());
                }
            }

//            A constant can only be mapped to itself, though I can map a variable to a constant

//            Iterate over the atoms of the body keeping track of the index
            for (int i = 0; i < body.size(); i++) {
//                Atom atom = body.get(i);
                RelationalAtom atom = (RelationalAtom) body.get(i);

//                PRINT MAIN LOOP
                System.out.println("\tMAIN LOOP: " + atom);


//               Check if the atom is a relational atom
                if (atom instanceof RelationalAtom){
                    System.out.println("Relational atom: " + atom);
                    System.out.println("Relational atom name: " + ((RelationalAtom) atom).getName());



//                    Get terms of atom, assign to a variable
                    List<Term> terms = ((RelationalAtom) atom).getTerms();
                    System.out.println("Relational atom terms: " + terms);


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


                        //                      Create a string to keep track of the matches
                        String matches = "";

//                       Counter for the number of matches
                        int matchCounter = 0;

//                        Integer for saving the possible replacement index
                        int replacementIndex = 0;


                        //   Iterate over the TERMS of the current relational atom, keep track of the index
                        for (int a = 0; a < termsLength; a++) {

                            ////                      Assign term to its corresponding class
                            //                        if (terms.get(a) instanceof Variable){
                            //                            System.out.println("Term is a variable");
                            //                        }
                            //                        else if (terms.get(a) instanceof Constant){
                            //                            System.out.println("Term is a constant");
                            //                        }
                            //                        else {
                            //                            System.out.println("Term is unknown");
                            //                        }

                            Term term = terms.get(a);
                            System.out.println("Term: " + term);

//                                Print type of current atom and other atom
                            System.out.println("Current atom type: " + atom.getClass());
                            System.out.println("Other atom type: " + otherAtom.getClass());



                            //                                Get the term at the current index of the other atom
                            Term otherTerm = otherTerms.get(a);
                            System.out.println("Other term: " + otherTerm);

                            //                                Print the current term class and the other term class
                            System.out.println("Current term class: " + term.getClass());
                            System.out.println("Other term class: " + otherTerm.getClass());

                            //                                    Check if the other term is equal to the current term
                            if (otherTerm.toString().equals(term.toString())) {
                                System.out.println("Other term is equal to current term");

                                //                                    Add the a 1 to the matches string
                                matches += "1";
                                System.out.println("Matches: " + matches);

//                                    Increment the match counter
                                matchCounter++;
                                System.out.println("Match counter: " + matchCounter);

                            } else {
                                System.out.println("Other term is NOT equal to current term");

                                matches += "0";
                                replacementIndex = a;
                                System.out.println("Matches: " + matches);
                            }



                        }

//                        Print the matches string
                        System.out.println("\n\nMatches at END OF THE ATOM: " + matches);

//                        If the count is equal to the length of the terms of the current atom minus 1, then, there might be a match
                        if (matchCounter == termsLength - 1) {
                            System.out.println("There might be a match");

//                        Get the term for each atom in the index of the possible replacement and print them
                            Term term = terms.get(replacementIndex);
                            Term otherTerm = otherTerms.get(replacementIndex);

                            System.out.println("Term: " + term);
                            System.out.println("Other term: " + otherTerm);

//                            Print types of the terms
                            System.out.println("Term type: " + term.getClass());
                            System.out.println("Other term type: " + otherTerm.getClass());

////                            If any of the terms is a variable inside the head, then, can't be a match
//                            if (checkIfIsHead(term, headVariables) || checkIfIsHead(otherTerm, headVariables)) {
//                                System.out.println("Term or other term is in the head, can't be a match");
//                                continue;
//                            }

//                            Check if the term is a variable and the other term is a constant
                            if (term instanceof Variable && otherTerm instanceof Constant) {
                                System.out.println("Term is a variable and other term is a constant");
                                System.out.println("Replacement/mapping: " + term + " for " + otherTerm);

//                                Check if the term that is going to be replaced is inside the head variables
                                if (checkIfIsHead(term, headVariables)) {
                                    System.out.println("Term is in the head, can't be a match");
                                    continue;
                                }

//                                Create a new atom with the replacement
                                RelationalAtom newAtom = new RelationalAtom(atom.getName(), atom.getTerms());
                                System.out.println("New atom: " + newAtom);

//                                Apply the replacement to the new atom
                                newAtom.getTerms().set(replacementIndex, otherTerm);
                                System.out.println("New atom with replacement: " + newAtom);

//                                Replace the atom in the body with the new atom
                                body.set(j, newAtom);
                                System.out.println("New body: " + body);

//                                If two atoms are equal, then, remove the other atom
                                if (atom.toString().equals(newAtom.toString())) {
                                    System.out.println("Atoms are equal, removing other atom");
                                    body.remove(j);
                                    System.out.println("New body: " + body);
                                }
                            }

                            else if (term instanceof Constant && otherTerm instanceof Variable) {
                                System.out.println("Term is a constant and other term is a variable");
                                System.out.println("Replacement/mapping: " + otherTerm + " for " + term);

//                                Check if the term that is going to be replaced is inside the head variables
                                if (checkIfIsHead(otherTerm, headVariables)) {
                                    System.out.println("Other term is in the head, can't be a match");
                                    continue;
                                }

//                                Create a new atom with the replacement
                                RelationalAtom newAtom = new RelationalAtom(otherAtom.getName(), otherAtom.getTerms());
                                System.out.println("New atom: " + newAtom);

//                                Apply the replacement to the new atom
                                newAtom.getTerms().set(replacementIndex, term);
                                System.out.println("New atom with replacement: " + newAtom);

//                                Replace the atom in the body with the new atom
                                body.set(j, newAtom);
                                System.out.println("New body: " + body);

//                                If two atoms are equal, then, remove the other atom
                                if (atom.toString().equals(newAtom.toString())) {
                                    System.out.println("Atoms are equal, removing other atom");
                                    body.remove(j);
                                    System.out.println("New body: " + body);
                                }

                            }

                            else if (term instanceof Variable && otherTerm instanceof Variable) {
                                System.out.println("Term is a variable and other term is a variable");

//                                Try replacing the term with the otherTerm, for that, we check if the term is in the head
                                if (checkIfIsHead(term, headVariables)) {
                                    System.out.println("Term is in the head, can't be replaced");

//                                    Thus, we replace the otherTerm with the term
//                                      Create a new atom with the replacement
                                    RelationalAtom newAtom = new RelationalAtom(otherAtom.getName(), otherAtom.getTerms());
                                    System.out.println("New atom: " + newAtom);


//                                    Apply the replacement to the new atom
                                    newAtom.getTerms().set(replacementIndex, term);
                                    System.out.println("New atom with replacement: " + newAtom);

                                    //                                Replace the atom in the body with the new atom
                                    body.set(j, newAtom);
                                    System.out.println("New body: " + body);

//                                If two atoms are equal, then, remove the other atom
                                    if (atom.toString().equals(newAtom.toString())) {
                                        System.out.println("Atoms are equal, removing other atom");
                                        body.remove(j);
                                        System.out.println("New body: " + body);
                                    }

                                } else if (checkIfIsHead(otherTerm, headVariables)) {
                                    System.out.println("Other term is in the head, can't be replaced");

//                                    Thus, we replace the term with the otherTerm
//                                      Create a new atom with the replacement
                                    RelationalAtom newAtom = new RelationalAtom(atom.getName(), atom.getTerms());
                                    System.out.println("New atom: " + newAtom);

//                                    Apply the replacement to the new atom
                                    newAtom.getTerms().set(replacementIndex, otherTerm);
                                    System.out.println("New atom with replacement: " + newAtom);

                                    //                                Replace the atom in the body with the new atom
                                    body.set(j, newAtom);
                                    System.out.println("New body: " + body);

//                                If two atoms are equal, then, remove the other atom
                                    if (atom.toString().equals(newAtom.toString())) {
                                        System.out.println("Atoms are equal, removing other atom");
                                        body.remove(j);
                                        System.out.println("New body: " + body);
                                    }

                                } else {
                                    System.out.println("Term and other term are not in the head, can be replaced");

//                                    Create a new atom with the replacement

                                }



                            }




                            else {
                                System.out.println("Term is not a variable or other term is not a constant");
                            }



                        }


                    }

                }
            }

            System.out.println("\n\nFINAL BODY: " + body);
//            return new Query(head, body);









        } catch (IOException e) {
            throw new RuntimeException(e);
        }











    }

    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */



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