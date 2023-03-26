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




    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) throws IOException {
        // TODO: add your implementation

        try {
            Query query = QueryParser.parse(Paths.get(inputFile));

//            Print query:
            System.out.println("Query parsed: " + query);

            //        Create ScanOperator object, given the query
            ScanOperator scanOperator = new ScanOperator("R");
            System.out.println(scanOperator.getRelationName());
            System.out.println(scanOperator.getRelationPath());
            System.out.println(scanOperator.getReader());

            //        Try to use the get next tuple function:
            Tuple tupleHere = scanOperator.getNextTuple();

//            Get list of relational atoms from query
            List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);

//            Iterate over each relational atom and get the relation name
            for (RelationalAtom relationalAtom : relationalAtoms) {
                String relationName = relationalAtom.getName();
                System.out.println("Current relation name: " + relationName);

                //        Create SelectionOperator object, given the query and the relation name
                SelectOperator selectionOperator = new SelectOperator(query, relationName);

                System.out.println("Selection operator: " + selectionOperator);

//                selectionOperator.getNextTuple();

////                Iterate 8 times:
                for (int i = 0; i < 8; i++) {
                    Tuple tuple = selectionOperator.getNextTuple();
                    System.out.println("TupleResult: " + tuple);
                }




            }


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
