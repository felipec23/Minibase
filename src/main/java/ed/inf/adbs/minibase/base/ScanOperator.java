package ed.inf.adbs.minibase.base;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  Operator to scan a file. It uses relTermsToDelete for optimization purposes. In this way, it's only scanning
 *  the variables that will actually be used at some time in the process.
 */
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


    /**
     * Function to get the file path of the relation
     * @return file path
     */
    public String getRelationPath() {

        // Get from the catalog the file path of the relation
        Relation r = catalog.getRelation(relationName);

        return r.getFilePath();

    }


    /**
     * This function returns the schema of a given relation
     * @return list of data types
     */

    @Override
    public List<String> getSchemaOfRelation() {
        // Use the schema to get the data types of the attributes

        Relation relation = catalog.getRelation(relationName);
        List<String> schema = relation.getSchema();

        return schema;

    }

    /**
     * This function identifies the positions that should be ignored when reading the .csv files. These positions
     * are given by another variable called relTermsToDelete, which it's part of the optimization process.
     * @param queryOriginal
     * @return list of indexes to not scan
     */
    public List<Integer> getIndexesToIgnore(Query queryOriginal){

        // Get the terms of the original relational atom
        List<Term> terms = getOriginalTermsOfAtom(queryOriginal);

        // Save indexes of terms equal to relTermsToDelete
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < terms.size(); i++) {
            if (relTermsToDelete.contains(terms.get(i).toString())) {
                indexes.add(i);
            }
        }

        return indexes;
    }

    /**
     * This functions fetches from the original query the terms of a given atom
     * @param originalQuery
     * @return list of terms
     */

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


    /**
     * The following function returns the simplified version of an atom, given the possible terms that can be
     * deleted.
     * @param queryOriginal
     * @return list of terms of atom
     */
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

        // System.out.println("Terms after deleting terms: " + terms);

        return terms;
    }

    @Override
    public List<Term> getTermsOfRelationalAtom() {
        return null;
    }

    /**
     * First, we open the reader and get the line. We then split by comma and iterate over the columns of the
     * .csv file. Nevertheless, we skip those indexes that are not needed in any part of the process and that
     * are part of the optimization step. Finally, we parse the string according to the schema, which we have
     * defined for the specific relation we're in.
     * @return
     */
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

            // Final size of the terms array
            int finalSize = values.length - indexesToIgnore.size();

            Term[] terms = new Term[finalSize];

            // Parse the values to the correct data type
            int counter = 0;
            for (int i = 0; i < values.length; i++) {

                if (indexesToIgnore.contains(i)) {
                    continue;
                }
                String type = schema.get(i);

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

            return tuple;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * HasNext function to check if there is a next tuple in the relation.
     * @return boolean indicating if there's a next line.
      */

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