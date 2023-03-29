package ed.inf.adbs.minibase.base;
import ed.inf.adbs.minibase.base.Relation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Catalog {

    private List<Relation> relations;
    private String databaseDir;

    private File filesDir;

    private static Catalog instance = null;



    private Catalog(String databaseDir) {
        this.databaseDir = databaseDir;
        this.relations = new ArrayList<>();
//        Files directory is the database directory + "/files"
        this.filesDir = new File(databaseDir + "/files/");
        this.relations = parseSchema();
    }


    


//    Given a files directory, there should be a list of CSV files and a schema.txt file
//    The schema.txt file, each row is a table
//    First column is the table name, and the rest of the columns are the column names

//    Open and parse schema.txt file:

//    Create a function to parse the schema.txt file where each line is a relation/table
//    Code:

    public List<Relation> getRelations() {
        return relations;
    }

    public void setRelations(List<Relation> relations) {
        this.relations = relations;
    }

    public String getDatabaseDir() {
        return databaseDir;
    }

    public void setDatabaseDir(String databaseDir) {
        this.databaseDir = databaseDir;
    }

    public File getFilesDir() {
        return filesDir;
    }

    public void setFilesDir(File filesDir) {
        this.filesDir = filesDir;
    }

//    Get the relation given the name of the relation
    public Relation getRelation(String name){
        for (Relation relation : relations) {
            if (relation.getName().equals(name)) {
                return relation;
            }
        }
        return null;
    }


    public List<Relation> parseSchema(){
        List<Relation> relations = new ArrayList<>();
//        Schema.txt file is the database directory + "schema.txt"
        String schemaFile = databaseDir + "/schema.txt";

//        Print out the schema file
        System.out.println(schemaFile);


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

    public static Catalog getInstance() {
        if (instance == null) {
            instance = new Catalog("data/evaluation/db");
        }
        return instance;
    }


}
