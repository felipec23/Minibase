package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScanOperator extends Operator{

    private String relationName;

    private Catalog catalog;

    private BufferedReader reader;

    private String relationPath;

    private Query query;

    private List<String> schema;

    private List<String> relTermsToDelete;

    private List<Integer> indexesToIgnore;


    public ScanOperator(String relationName, List<String> relTermsToDelete) {
        super(null);
        this.relTermsToDelete = relTermsToDelete;
        this.catalog = Catalog.getInstance();
        this.query = catalog.getParsedQuery();
        this.relationName = relationName;
        this.schema = getSchemaOfRelation();
        this.relationPath = getRelationPath();
        this.reader = (BufferedReader) setReader();
        this.indexesToIgnore = getIndexesToIgnore(query);


        System.out.println("Here, ORIGINAL QUERY: " + query);
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

    @Override
    public String getRelationName() {
        return relationName;
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

    @Override
    public List<String> getSchemaOfRelation() {
        //            Use the schema to get the data types of the attributes

        Relation relation = catalog.getRelation(relationName);
        List<String> schema = relation.getSchema();

        System.out.println("Original schema: " + schema);


        return schema;

    }

    public List<Integer> getIndexesToIgnore(Query queryOriginal){
        // Get the terms of the original relational atom
        List<Term> terms = getOriginalTermsOfAtom(queryOriginal);

        System.out.println("Original query: " + queryOriginal);

        System.out.println("Original terms of relational atom: " + terms);

        System.out.println("Terms to delete: " + relTermsToDelete);

        // Save indexes of terms equal to relTermsToDelete
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < terms.size(); i++) {
            if (relTermsToDelete.contains(terms.get(i).toString())) {
                indexes.add(i);
            }
        }

        return indexes;
    }


    public List<Term> getOriginalTermsOfAtom(Query originalQuery) {

        // Iterate over relational atoms in the body
        // If the name of the relational atom is the same as the relation name
        // Get the terms of the relational atom

        List<Term> terms = new ArrayList<>();
        for (Atom atom : originalQuery.getBody()) {
            if (atom instanceof RelationalAtom) {
                if (((RelationalAtom) atom).getName().equals(relationName)) {
                    terms = ((RelationalAtom) atom).getTerms();
                }
            }

        }

        return terms;

    }


//    @Override
//    public List<Term> getTermsOfRelationalAtom(Query queryOriginal) {
//
//        // Iterate over relational atoms in the body
//        // If the name of the relational atom is the same as the relation name
//        // Get the terms of the relational atom
//
//        List<Term> terms = new ArrayList<>();
//        for (Atom atom : queryOriginal.getBody()) {
//            if (atom instanceof RelationalAtom) {
//                if (((RelationalAtom) atom).getName().equals(relationName)) {
//                    terms = ((RelationalAtom) atom).getTerms();
//                }
//            }
//
//        }
//
//        return terms;
//
//    }

    public List<Term> getNewSimpleTermsOfRelAtom(Query queryOriginal) {
        List<Term> terms = new ArrayList<>();
        for (Atom atom : queryOriginal.getBody()) {
            if (atom instanceof RelationalAtom) {
                if (((RelationalAtom) atom).getName().equals(relationName)) {
                    terms = ((RelationalAtom) atom).getTerms();

                    // Iterate over the terms, if string of term is in relTermsToDelete, remove it
                    for (int i = terms.size() - 1; i >= 0; i--) {
                        if (relTermsToDelete.contains(terms.get(i).toString())) {
                            terms.remove(i);
                        }
                    }

                }
            }

        }

        System.out.println("Terms after deleting terms: " + terms);

        return terms;
    }

    @Override
    public List<Term> getTermsOfRelationalAtom() {
        return null;
    }

    @Override
    public Tuple getNextTuple() {
        String line;
        try {


            line = reader.readLine();
            System.out.println("FROM SCAN OPERATOR");
            System.out.println("Reader: " + reader);
            System.out.println("LINE: " + line);
            if (line == null) {
                reader.close();
                return null;
            }


            String[] values = line.split(",");


            // Get indexes of terms to ignore

            System.out.println("Indexes to ignore: " + indexesToIgnore);

            // Final size of the terms array
            int finalSize = values.length - indexesToIgnore.size();

            Term[] terms = new Term[finalSize];

            // Parse the values to the correct data type
            int counter = 0;
            for (int i = 0; i < values.length; i++) {

                System.out.println("iaca: " + i);
                if (indexesToIgnore.contains(i)) {
                    continue;
                }
                String type = schema.get(i);
                System.out.println("Type: " + type);

                if (type.equals("int")) {

                    terms[counter] = new IntegerConstant(Integer.parseInt(values[i].trim()));
                }
                else if (type.equals("string")) {

                    terms[counter] = new StringConstant(values[i].trim().replaceAll("'", ""));
                }

                counter++;
            }

            Tuple tuple = new Tuple(terms);
            // Get variables for the tuple
            tuple.setVariables(getNewSimpleTermsOfRelAtom(query));
            System.out.println("Tuple to send from scan: " + tuple);
            System.out.println("Tuple variables: " + tuple.getVariables());
            return tuple;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



//    @Override
//    public Tuple getNextTuple() {
//        String line;
//        try {
//
//
//            line = reader.readLine();
//            System.out.println("FROM SCAN OPERATOR");
//            System.out.println("Reader: " + reader);
//            System.out.println("LINE: " + line);
//            if (line == null) {
//                reader.close();
//                return null;
//            }
//
//
//            String[] values = line.split(",");
//            Term[] terms = new Term[values.length];
//
//
////            Print the values
////            System.out.println("Values from CSV: ");
////            for (int i = 0; i < values.length; i++) {
////                System.out.println(values[i]);
////            }
//
////            Parse the values to the correct data type
//            for (int i = 0; i < values.length; i++) {
//
//////                Print i, say that print it
////                System.out.println("i: " + i);
//////                Print the data type
////                System.out.println("Data type: " + schema.get(i));
//////                Print terms[i]
////                System.out.println("values[i]: " + values[i]);
////                Get correspondant data type
//                String type = schema.get(i);
//
//
//                if (type.equals("int")) {
//
////                    If it's an integer, create new IntegerConstant object
////                    values[i] = new IntegerConstant(Integer.parseInt(values[i]));
//                    terms[i] = new IntegerConstant(Integer.parseInt(values[i].trim()));
//                }
//                else if (type.equals("string")) {
//
//
////                    values[i] = values[i];
////                    Remove the single quotes
////                    values[i] = values[i].trim().replaceAll("'", "");
//                    terms[i] = new StringConstant(values[i].trim().replaceAll("'", ""));
//                }
//            }
//
////            Print the values
////            System.out.println("Values after parsing: ");
////            for (int i = 0; i < terms.length; i++) {
////                System.out.println(terms[i]);
//////                Print the data type
////                System.out.println(terms[i].getClass());
////            }
//
////
//            Tuple tuple = new Tuple(terms);
//            // Get variables for the tuple
//
//            List<Term> relAtomTerms = getTermsOfRelationalAtom();
//            System.out.println("Relational atom terms: " + relAtomTerms);
//
//            // Get the variables from the head
//            List<String> headTerms = extractAllTerms();
//            System.out.println("ALL terms: " + headTerms);
//
//            // Find the intersection of the two lists
//            List<Term> intersection = new ArrayList<>();
//
//            // Array to store the new list of terms for the minimal tuple
//            List<Term> newTupleTerms = new ArrayList<>();
//
//            int index = 0;
//            for (Term t : relAtomTerms) {
//                if (headTerms.contains(t.toString())) {
//                    intersection.add(t);
//
//                    // Add the term in common to the new tuple
//                    newTupleTerms.add(terms[index]);
//                }
//
//                index++;
//            }
//
//            Term[] myArray = newTupleTerms.toArray(new Term[newTupleTerms.size()]);
//
//            // Create new tuple with the intersection
//            Tuple newTuple = new Tuple(myArray);
//
//            System.out.println("Intersection: " + intersection);
//
//            // Remove the terms from the tuple that are not in the intersection
//
//
//
//            newTuple.setVariables(intersection);
//            System.out.println("Tuple to send from scan: " + tuple);
//            System.out.println("Tuple variables: " + tuple.getVariables());
//
//            System.out.println("New tuple to send from scan: " + newTuple);
//            System.out.println("New tuple variables: " + newTuple.getVariables());
//            return newTuple;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    // HasNext function to check if there is a next tuple in the relation:
    @Override
    public boolean hasNext() {
        String line;
        try {
            line = reader.readLine();
            if (line == null) {
                reader.close();
                return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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






}