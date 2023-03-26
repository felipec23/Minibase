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






    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) throws IOException {
        // TODO: add your implementation

        try {
            Query query = QueryParser.parse(Paths.get(inputFile));


            //        Create SelectionOperator object, given the scanOperator and the selection condition
            SelectOperator selectionOperator = new SelectOperator(query);





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


        } catch (IOException e) {
            throw new RuntimeException(e);
        }









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
