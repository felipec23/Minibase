package ed.inf.adbs.minibase.base;
import ed.inf.adbs.minibase.base.Relation;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
Database Catalog
Description: This class is responsible for parsing the schema.txt file and creating a list of relations.
A Relation object is created for each table in the schema.txt file. The Relation object contains the
name of the table, the list of attributes, and the list of types of the attributes.
In the catalog there's also the path for the different directories used in the database. The instance
can only be created once, and it's a singleton.

@params:
    relations: List of relations in the database
    databaseDir: Path to the database directory
    filesDir: Path to the files directory
    schema: Path to the schema.txt file
    query: Path to the query.txt file
    output: Path to the output.txt file

@returns:
    Catalog: The catalog object

 */
public class Catalog {

    private List<Relation> relations;
    private String databaseDir;

    private File filesDir;

    private static Catalog instance = null;

    private String inputFile;

    private String outputFile;

    private Query query;




    private Catalog() {

    }


    public void init(String databaseDir, String inputFile, String outputFile){
        this.databaseDir = databaseDir;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.relations = new ArrayList<>();
        // Files directory is the database directory + "/files"
        this.filesDir = new File(databaseDir + "/files/");
        this.relations = parseSchema();
        this.query = getParsedQuery();

    }



    // Given a files directory, there should be a list of CSV files and a schema.txt file
    // In the schema.txt file each row is a table
    // First column is the table name, and the rest of the columns are the column names

    // This function parses the query and stores it in the catalog
    public Query getParsedQuery(){
        Query queryParsed = null;
        try {
            queryParsed = QueryParser.parse(Paths.get(inputFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return queryParsed;
    }

    // Get the output file path
    public String getOutputFile() {
        return outputFile;
    }



    // Get the relation object given the name of the relation
    public Relation getRelation(String name){
        for (Relation relation : relations) {
            if (relation.getName().equals(name)) {
                return relation;
            }
        }
        return null;
    }

    // Get the list of relations given a schema txt file
    public List<Relation> parseSchema(){
        List<Relation> relations = new ArrayList<>();
        String schemaFile = databaseDir + "/schema.txt";

        try {
            BufferedReader br = new BufferedReader(new FileReader(schemaFile));
            String line;

            while ((line = br.readLine()) != null) {
                String[] lineArray = line.split(" ");
                String tableName = lineArray[0];
                String filePath = filesDir + "/" + tableName + ".csv";
                List<String> schema = new ArrayList<>();
                for (int i = 1; i < lineArray.length; i++) {
                    schema.add(lineArray[i]);
                }
                Relation relation = new Relation(tableName, schema, filePath);
                relations.add(relation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return relations;
    }

    // Get the catalog instance
    public static Catalog getInstance() {
        if (instance == null) {
            instance = new Catalog();
        }
        return instance;
    }


}
