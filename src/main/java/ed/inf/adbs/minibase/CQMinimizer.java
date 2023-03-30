package ed.inf.adbs.minibase;
import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;
import java.io.FileWriter;
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

        if (args.length != 2) {
            System.err.println("Usage: CQMinimizer input_file output_file");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        // Parse query
        minimizeCQ(inputFile, outputFile);

    }

    /**
     * CQ minimization procedure
     *
     * Assume the body of the query from inputFile has no comparison atoms
     * but could potentially have constants in its relational atoms.
     *
     */


    /*
    Description: This function checks if a term is a head variable.
    @param term: The term to be checked
    @param headVariables: The list of head variables
    @return: True if the term is a head variable, false otherwise
     */
    public static boolean checkIfIsHead(Term term, List<Variable> headVariables){
        for (Variable variable : headVariables) {
            if (term.toString().equals(variable.getName())){
                return true;
            }
        }
        return false;
    }

    /*
    Description: This function implements the CQ minimization algorithm. The approach is the following: we start
    from left to right. We iterate over the atoms that are after the current atom.
    We check that the length between the two atoms being compared is the same. Then,
    we iterate over the terms of the second atom to find the differences between the two atoms' terms.
    If the difference is one, it means there's a potential replacement. Then depending on the type of
    term and on whether the term is a head variable or not, we replace the term in the first atom with
    the term in the second atom. We then remove the second atom from the body of the query (since it's
    redundant). We then return the minimized query. We represent the differences between two atoms
    by creating a match string: "110" means that the first and second terms of the atoms are the same.

    @param inputFile: The input file path
    @param outputFile: The output file path
    @return: The minimized query
     */
    public static String minimizeCQ(Object inputFile, String outputFile) {
        // TODO: add your implementation

        Query query = null;

        // Parse query
        try {
            query = QueryParser.parse(Paths.get(inputFile.toString()));
        } catch (IOException e) {
            //System.out.println("Error parsing query");
        }

        Head head = query.getHead();
        List<Atom> body = query.getBody();

        // Get variables from head
        List<Variable> headVariables = head.getVariables();

        for (int i = 0; i < body.size(); i++) {
            RelationalAtom atom = (RelationalAtom) body.get(i);


            // If atom is not a relational atom, skip it
            if (!(atom instanceof RelationalAtom)) {
                continue;
            }

            // Get terms of atom, assign to a variable
            List<Term> terms = ((RelationalAtom) atom).getTerms();

            // Get length of terms, assign to a variable
            int termsLength = terms.size();

            // Iterate over all the other ATOMS that are after the current atom
            for (int j = i + 1; j < body.size(); j++) {

                // If the other atom is not a relational atom, skip it
                if (!(body.get(j) instanceof RelationalAtom)) {
                    continue;
                }

                RelationalAtom otherAtom = (RelationalAtom) body.get(j);
                List<Term> otherTerms = otherAtom.getTerms();
                int otherTermsLength = otherTerms.size();

                // Get the name of the other atom
                String otherAtomName = otherAtom.getName();

                //  Get the name of the current atom
                String atomName = atom.getName();

                // Check if the name of the other atom is equal to the name of the current atom
                if (!(otherAtomName.equals(atomName))) {
                    continue;
                }

                // Check if the size of the terms of the other atom is equal to the size of the terms of the current atom
                if (!(otherTermsLength == termsLength)) {
                    continue;
                }

                // Create a string to keep track of the matches
                String matches = "";

                // Counter for the number of matches
                int matchCounter = 0;

                // Integer for saving the possible replacement index
                int replacementIndex = 0;


                // Iterate over the TERMS of the current relational atom, keep track of the index
                for (int a = 0; a < termsLength; a++) {

                    Term term = terms.get(a);

                    // Get the term at the current index of the other atom
                    Term otherTerm = otherTerms.get(a);

                    // Check if the other term is equal to the current term
                    if (otherTerm.toString().equals(term.toString())) {

                        // Add a 1 to the matches string
                        matches += "1";

                        // Increment the match counter
                        matchCounter++;

                    } else {

                        matches += "0";
                        replacementIndex = a;

                    }


                }


                //  If the count is equal to the length of the terms of the current atom minus 1, then, there might be a match
                if (matchCounter == termsLength - 1) {

                    // Get the term for each atom in the index of the possible replacement and print them
                    Term term = terms.get(replacementIndex);
                    Term otherTerm = otherTerms.get(replacementIndex);

                    // Check if the term is a variable and the other term is a constant
                    if (term instanceof Variable && otherTerm instanceof Constant) {

                        // Check if the term that is going to be replaced is inside the head variables
                        if (checkIfIsHead(term, headVariables)) {
                            continue;
                        }

                        // Create a new atom with the replacement
                        RelationalAtom newAtom = new RelationalAtom(atom.getName(), atom.getTerms());

                        //  Apply the replacement to the new atom
                        newAtom.getTerms().set(replacementIndex, otherTerm);

                        //  Replace the atom in the body with the new atom
                        body.set(j, newAtom);

                        // If two atoms are equal, then, remove the other atom
                        if (atom.toString().equals(newAtom.toString())) {
                            body.remove(j);
                        }
                    }

                    else if (term instanceof Constant && otherTerm instanceof Variable) {


                        //  Check if the term that is going to be replaced is inside the head variables
                        if (checkIfIsHead(otherTerm, headVariables)) {
                            continue;
                        }

                        // Create a new atom with the replacement
                        RelationalAtom newAtom = new RelationalAtom(otherAtom.getName(), otherAtom.getTerms());


                        // Apply the replacement to the new atom
                        newAtom.getTerms().set(replacementIndex, term);

                        //  Replace the atom in the body with the new atom
                        body.set(j, newAtom);

                        // If two atoms are equal, then, remove the other atom
                        if (atom.toString().equals(newAtom.toString())) {
                            body.remove(j);
                        }

                    }

                    else if (term instanceof Variable && otherTerm instanceof Variable) {

                        // Try replacing the term with the otherTerm, for that, we check if the term is in the head
                        if (checkIfIsHead(term, headVariables)) {

                            //  Thus, we replace the otherTerm with the term
                            // Create a new atom with the replacement
                            RelationalAtom newAtom = new RelationalAtom(otherAtom.getName(), otherAtom.getTerms());

                            // Apply the replacement to the new atom
                            newAtom.getTerms().set(replacementIndex, term);

                            // Replace the atom in the body with the new atom
                            body.set(j, newAtom);

                            // If two atoms are equal, then, remove the other atom
                            if (atom.toString().equals(newAtom.toString())) {
                                body.remove(j);
                            }

                        } else if (checkIfIsHead(otherTerm, headVariables)) {

                            // Thus, we replace the term with the otherTerm
                            // Create a new atom with the replacement
                            RelationalAtom newAtom = new RelationalAtom(atom.getName(), atom.getTerms());

                            //  Apply the replacement to the new atom
                            newAtom.getTerms().set(replacementIndex, otherTerm);

                            // Replace the atom in the body with the new atom
                            body.set(j, newAtom);

                            //  If two atoms are equal, then, remove the other atom
                            if (atom.toString().equals(newAtom.toString())) {
                                body.remove(j);
                            }

                        } else {
                            //System.out.println("Term and other term are not in the head, can be replaced");

                        }

                    }


                    else {
                        //System.out.println("Term is not a variable or other term is not a constant");
                    }



                }


            }


        }

        // Join the body atoms from the list of atoms with a comma
        StringBuilder bodyString = new StringBuilder();
        for (int i = 0; i < body.size(); i++) {
            if (i == body.size() - 1) {
                bodyString.append(body.get(i));
            } else {
                bodyString.append(body.get(i) + ", ");
            }
        }


        // Print the query to screen
        String stringToWrite = head + " :- " + bodyString.toString();

        writeToFile(stringToWrite, outputFile);


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