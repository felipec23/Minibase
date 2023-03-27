package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Import Catalog class created:


/**
 * In-memory database system
 *
 */
public class Minibase {

    public static void main(String[] args) {

        if (args.length != 3) {
            System.err.println("Usage: Minibase database_dir input_file output_file");
            return;
        }

        String databaseDir = args[0];
        String inputFile = args[1];
        String outputFile = args[2];

        try {
            evaluateCQ(databaseDir, inputFile, outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        parsingExample(inputFile);
    }






    public static List<String> getRelationsFromQuery(Query query) {
        List<String> relations = new ArrayList<>();
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                relations.add(relationalAtom.getName());
            }
        }
        return relations;
    }

    public static List<RelationalAtom> getRelationalAtomsFromQuery(Query query) {
        List<RelationalAtom> relationalAtoms = new ArrayList<>();
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                relationalAtoms.add(relationalAtom);
            }
        }
        return relationalAtoms;
    }


    public static String getQueryType(Query query) {

//        Only scan: if there are not comparisons atoms and if all terms in head are variables:
//        Iterate over the body of the query:

        boolean hasComparisonAtoms = false;
//        Save each relational atom in a list:

        List<String> relationalVariables = new ArrayList<>();
        for (Atom atom : query.getBody()) {
            if (atom instanceof ComparisonAtom) {
                hasComparisonAtoms = true;
            }

//            Get the terms of the relational atom:
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                List<Term> terms = relationalAtom.getTerms();
                for (Term term : terms) {
                    relationalVariables.add(term.toString());
                }
            }
        }


//        Iterate over the head of the query:
        boolean allHeadTermsAreVariables = true;

//        Iterate over the terms in the head of the query:
        Head head = query.getHead();

        List<String> headVariables = new ArrayList<>();
        for (Term term : head.getVariables()) {
            headVariables.add(term.toString());
            if (!(term instanceof Variable)) {
                allHeadTermsAreVariables = false;
            }
        }


        if (!hasComparisonAtoms && allHeadTermsAreVariables) {

            System.out.println("Head variables: " + headVariables);
            System.out.println("Relational variables: " + relationalVariables);

//            Check if the head is in the same order as the body:
            if (headVariables.equals(relationalVariables)) {
//                Only scan:
                return "scan";
            }
            else {
//                Scan, but change the order of the columns:
                return "projection";
            }

        }

        else if (hasComparisonAtoms && allHeadTermsAreVariables) {

//            Get number of relational atoms in body
            List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);
            int numberOfRelationalAtoms = relationalAtoms.size();

            if (numberOfRelationalAtoms == 1) {
//                Only one relational atom in body:

                if (headVariables.equals(relationalVariables)) {
//                    Only selection:
                    return "selection";
                }
                else {
//                    Selection and projection:
                    return "selection_projection";
                }

            }
            else {
//                More than one relational atom in body:
                return "join";
            }

        }

//        else if (hasComparisonAtoms && !allHeadTermsAreVariables) {



//        }
        else {
            return "other";
        }


    }


//    function to do only scan
    public static void runScan(Query query){
        //            Iterate over each relational atom and get the relation name
        List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);

        for (RelationalAtom relationalAtom : relationalAtoms) {
            String relationName = relationalAtom.getName();
            System.out.println("Current relation name: " + relationName);

            //        Create ScanOperator object, given the query and the relation name
            ScanOperator scanOperator = new ScanOperator(relationName);

            System.out.println("Before dump:");
            scanOperator.dump();

        }
    }

    public static void runSelection(Query query) {
        //            Iterate over each relational atom and get the relation name
        List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);

        for (RelationalAtom relationalAtom : relationalAtoms) {
            String relationName = relationalAtom.getName();
            System.out.println("Current relation name: " + relationName);

            //        Create SelectionOperator object, given the query and the relation name
            SelectOperator selectionOperator = new SelectOperator(query, relationName);

            System.out.println("Selection operator dump:");
            selectionOperator.dump();
        }
    }


    public static void runProjection(Query query) {
        //            Iterate over each relational atom and get the relation name
        List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);

        for (RelationalAtom relationalAtom : relationalAtoms) {
            String relationName = relationalAtom.getName();
            System.out.println("Current relation name: " + relationName);

//            For this case, the child is a scan operator:
            ScanOperator scanOperator = new ScanOperator(relationName);

            //        Create ProjectionOperator object, given the query and the relation name
            ProjectOperator projectionOperator = new ProjectOperator(scanOperator, query);

            System.out.println("Projection operator dump:");
            projectionOperator.dump();
        }
    }

    public static void runSelectionProjection(Query query) {
        //            Iterate over each relational atom and get the relation name
        List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);

        for (RelationalAtom relationalAtom : relationalAtoms) {
            String relationName = relationalAtom.getName();
            System.out.println("Current relation name: " + relationName);


            //        Create SelectionOperator object, given the query and the relation name
            SelectOperator selectionOperator = new SelectOperator(query, relationName);

            //        Create ProjectionOperator object, given the query and the relation name
            ProjectOperator projectionOperator = new ProjectOperator(selectionOperator, query);

            System.out.println("Selection projection operator dump:");
            projectionOperator.dump();
        }

    }

//    Create a function to get the comparison atoms from the query:
    public static List<ComparisonAtom> getComparisonAtomsFromQuery(Query query) {
        List<ComparisonAtom> comparisonAtoms = new ArrayList<>();
        for (Atom atom : query.getBody()) {
            if (atom instanceof ComparisonAtom) {
                ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
                comparisonAtoms.add(comparisonAtom);
            }
        }
        return comparisonAtoms;
    }

    public static List<ComparisonAtom> detectImplicitConditions(List<RelationalAtom> relationalAtoms ) {

        // Detect implicit conditions, that is, if there is a relational atom with a constant:
        // Save the relational atoms with constants in a list:
        List<ComparisonAtom> newComparisonAtoms = new ArrayList<>();
        for (RelationalAtom relationalAtom : relationalAtoms) {
            // Create index for keeping track of the position of the constant:
            int index = 0;
            for (Term term : relationalAtom.getTerms()) {
                if (term instanceof Constant) {
                    System.out.println("Implicit condition detected: " + relationalAtom + " = " + term);

                    // Save as a comparison atom:
                    ComparisonAtom comparisonAtom = new ComparisonAtom(term, term, ComparisonOperator.EQ);

                    // Set type and relation name and index:
                    comparisonAtom.setType("implicit");
                    comparisonAtom.setRelationName(relationalAtom.getName());
                    comparisonAtom.setIndex(index);

                    // Print the comparison atom:
                    System.out.println("Comparison atom: " + comparisonAtom);

                    // Add to the list of new comparison atoms:
                    newComparisonAtoms.add(comparisonAtom);
                }
                index++;
            }
        }

        return newComparisonAtoms;
    }

    public static void detectEquijoinConditions(List<RelationalAtom> relationalAtoms){
        // Find if two relational atoms have the same variable:
        // If so, then it is an equi-join condition:

        // Save the relational atoms with constants in a list:

        // Iterate over each relational atom:
        for (int i = 0; i < relationalAtoms.size(); i++) {
            RelationalAtom relationalAtom1 = relationalAtoms.get(i);
            for (int j = i + 1; j < relationalAtoms.size(); j++) {
                RelationalAtom relationalAtom2 = relationalAtoms.get(j);

                // Iterate over each term of the first relational atom:
                for (Term term1 : relationalAtom1.getTerms()) {
                    // Iterate over each term of the second relational atom:
                    for (Term term2 : relationalAtom2.getTerms()) {
                        if (term1.equals(term2)) {
                            System.out.println("Equi-join condition detected: " + relationalAtom1 + " = " + relationalAtom2);
                        }
                    }
                }
            }
        }

    }


    public static void analyzeConditions(Query query){
        // Get relational atoms from query:
        List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);

        // Detect implicit conditions, that is, if there is a relational atom with a constant:
        List<ComparisonAtom> relationalAtomsWithConstants = detectImplicitConditions(relationalAtoms);

        // Detect equi-join conditions:
        detectEquijoinConditions(relationalAtoms);

        // Get comparison atoms from query:
        List<ComparisonAtom> comparisonAtoms = getComparisonAtomsFromQuery(query);




    }





    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) throws IOException {
        // TODO: add your implementation

        try {
            Query query = QueryParser.parse(Paths.get(inputFile));

//            Print query:
            System.out.println("Query parsed: " + query);

            //  Get type of query:
            String queryType = getQueryType(query);
            System.out.println("Query type: " + queryType);

            if (queryType.equals("scan")) {
                runScan(query);
            }

            else if (queryType.equals("projection")) {
                runProjection(query);
            }

            else if (queryType.equals("selection")) {
                runSelection(query);
            }

            else if (queryType.equals("join")) {
                analyzeConditions(query);
            }

            else if (queryType.equals("selection_projection")) {
                runSelectionProjection(query);
            }


//
////            Get list of relational atoms from query
//            List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);
//
////            Iterate over each relational atom and get the relation name
//            for (RelationalAtom relationalAtom : relationalAtoms) {
//                String relationName = relationalAtom.getName();
//                System.out.println("Current relation name: " + relationName);
//
//                //        Create SelectionOperator object, given the query and the relation name
//                SelectOperator selectionOperator = new SelectOperator(query, relationName);
//
//                System.out.println("Selection operator: " + selectionOperator);
//
////                selectionOperator.getNextTuple();
//
//
////              Testing the projection operator:
//                ProjectOperator projectionOperator = new ProjectOperator(query, relationName);
//                System.out.println("Projection operator: " + projectionOperator);
//
////////                Iterate 8 times:
////                for (int i = 0; i < 8; i++) {
////                    Tuple tuple = selectionOperator.getNextTuple();
////                    System.out.println("TupleResult: " + tuple);
////                }
//
//            }




        }

         catch (IOException e) {
            throw new RuntimeException(e);
        }








////        Print scanOperator relationName, relationPath and reader
//            System.out.println(scanOperator.getRelationName());
//            System.out.println(scanOperator.getRelationPath());
//            System.out.println(scanOperator.getReader());
//
//            //        Try to use the get next tuple function:
//            Tuple tupleHere = scanOperator.getNextTuple();
//            System.out.println("tupleHere: " + tupleHere);
//            Tuple tuple2 = scanOperator.getNextTuple();
//            System.out.println(tuple2);
////       Reset the reader:
//            scanOperator.reset();
//
//            System.out.println("Reset the reader");
//            Tuple tuple3 = scanOperator.getNextTuple();
//            System.out.println(tuple3);
////        Dump operator:
////        Print dumping
//            System.out.println("Dumping");
//            scanOperator.dump();












//        Prinit tuple elements:
//        System.out.println(tuple.toList());

//        Create ProjectOperator object, given the scanOperator and the schema






    }

    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static void parsingExample(String filename) {
        try {
            Query query = QueryParser.parse(Paths.get(filename));
            // Query query = QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w), z < w");
            // Query query = QueryParser.parse("Q(SUM(x * 2 * x)) :- R(x, 'z'), S(4, z, w), 4 < 'test string' ");

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
