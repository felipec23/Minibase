package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

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

        // parsingExample(inputFile);
    }

    /**
     * Evaluates a CQ from a file and writes the result to a file.
     *
     * @param databaseDir
     * @param inputFile
     * @param outputFile
     * @throws IOException
     */

    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) throws IOException {
        // TODO: add your implementation

        // Create a new instance of the Catalog class:
        Catalog catalog = Catalog.getInstance();
        catalog.init(databaseDir, inputFile, outputFile);

        Query query = null;
        // Parse the query:
        try {
            // Parse the query:
            query = QueryParser.parse(Paths.get(inputFile));

        }

        catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Create a new instance of the QueryPlanner class:
        QueryPlanner queryPlanner = new QueryPlanner();
        Operator root = queryPlanner.plan(query);

        // Write query result to file:
        root.dump();


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
