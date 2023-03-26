package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.parser.QueryParser;

import javax.xml.validation.Schema;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ScanOperator extends Operator{

    private String relationName;

    private Catalog catalog;

    private BufferedReader reader;

    private String relationPath;

    private Query query;

    private List<String> schema;

    public ScanOperator(String relationName) {
        super(null);
//        this.query = query;
        this.catalog = Catalog.getInstance();
        this.relationName = relationName;
        this.schema = getSchemaOfRelation();
        this.relationPath = getRelationPath();
        this.reader = (BufferedReader) setReader();

    }

    @Override
    public String toString() {
        return "SCAN(" + relationName + ")";
    }

    //    Function to set reader:
    public Reader setReader() {
        try {
            Reader reader = new BufferedReader(new FileReader(relationPath));
            return reader;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //    Function to get relation name:
    public String getRelationName() {
        return relationName;
    }


    //    Function to parse the query and return a list of all possible relation names
    public List<String> extractRelationNames() {

        try{

            System.out.println("Entire query: " + query);
            Head head = query.getHead();
            System.out.println("Head: " + head);
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);

//           Print size of body
            System.out.println("Body size: " + body.size());

//            Get the all the relational atoms in the body
            List<String> relationNames = new ArrayList<>();
            for (Atom atom : body) {
                if (atom instanceof RelationalAtom) {
                    System.out.println("Relational atom: " + atom);
                    String name = ((RelationalAtom) atom).getName();
                    System.out.println("Relational atom name: " + name);
                    relationNames.add(name);
                }
            }

            return relationNames;

        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
            return null;
        }

    }

//    Function to parse the query and return the relation name

    public String extractRelationName() {

//        Query query = null;

        try{

            System.out.println("Entire query: " + query);
            Head head = query.getHead();
            System.out.println("Head: " + head);
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);

//           Print size of body
            System.out.println("Body size: " + body.size());

//            Get the first atom in the body
            Atom atom = body.get(0);
//            If the atom is a relational atom
            if (atom instanceof RelationalAtom){
                System.out.println("Relational atom: " + atom);
                String name = ((RelationalAtom) atom).getName();
                System.out.println("Relational atom name: " + name);

                return name;
            }

            else {
                System.out.println("Not a relational atom");

                return null;
            }



        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
            return null;
        }

    }




    //    Function to get the file path of the relation
    public String getRelationPath() {

//        Get from the catalog the file path of the relation
        Relation r = catalog.getRelation(relationName);

        return r.getFilePath();

    }

    public String getReader() {
        return reader.toString();
    }


    public List<String> getSchemaOfRelation() {
        //            Use the schema to get the data types of the attributes

        Relation relation = catalog.getRelation(relationName);
        List<String> schema = relation.getSchema();

//            Print schema, say that print it
        System.out.println("Schema: " + schema);

        return schema;

    }

    @Override
    public Tuple getNextTuple() {
        String line;
        try {
            line = reader.readLine();
            if (line == null) {
                reader.close();
                return null;
            }


            String[] values = line.split(",");
            Term[] terms = new Term[values.length];


//            Print the values
            System.out.println("Values from CSV: ");
            for (int i = 0; i < values.length; i++) {
                System.out.println(values[i]);
            }

//            Parse the values to the correct data type
            for (int i = 0; i < values.length; i++) {

////                Print i, say that print it
//                System.out.println("i: " + i);
////                Print the data type
//                System.out.println("Data type: " + schema.get(i));
////                Print terms[i]
//                System.out.println("values[i]: " + values[i]);
//                Get correspondant data type
                String type = schema.get(i);


                if (type.equals("int")) {

//                    If it's an integer, create new IntegerConstant object
//                    values[i] = new IntegerConstant(Integer.parseInt(values[i]));
                    terms[i] = new IntegerConstant(Integer.parseInt(values[i].trim()));
                }
                else if (type.equals("string")) {


//                    values[i] = values[i];
//                    Remove the single quotes
//                    values[i] = values[i].trim().replaceAll("'", "");
                    terms[i] = new StringConstant(values[i].trim().replaceAll("'", ""));
                }
            }

//            Print the values
            System.out.println("Values after parsing: ");
            for (int i = 0; i < terms.length; i++) {
                System.out.println(terms[i]);
//                Print the data type
                System.out.println(terms[i].getClass());
            }

//
            Tuple tuple = new Tuple(terms);
            System.out.println("Tuple: " + tuple);
            return tuple;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void reset() {


        try {
            this.reader = new BufferedReader(new FileReader(relationPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    Dump the scan operator
    @Override
    public void dump() {
        reset();
        Tuple tuple = getNextTuple();
        while (tuple != null) {
            System.out.println(tuple);
            tuple = getNextTuple();
        }
    }



}