package ed.inf.adbs.minibase.base;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

public abstract class Operator {
    public Operator(Query query) {
    }


    public abstract List<Term> getTermsOfRelationalAtom();


    public Tuple getNextTuple() {
        return null;
    }

    public void reset() {
    }

    public void open() {
    }

    public void close() {
    }

    /**
     * This method is used to dump the tuples of all the operators, except the SumOperator.
     * This is called when running "root.dump()". The output file is fetched from the catalog.
     */
    public void dump() {

        Tuple tuple = getNextTuple();
        try {
            String outputFileName = this.getCatalog().getOutputFile();
            FileWriter pw = new FileWriter(outputFileName, true);

            while (tuple != null) {

                // Write tuple to file:
                pw.write(tuple.toString());
                pw.write("\n");

                // Get next tuple
                tuple = getNextTuple();

            }

            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public List<String> getSchemaOfRelation() {
        return null;
    }

    public Catalog getCatalog() {
        return Catalog.getInstance();
    }

    public boolean hasNext() {
        return false;
    }
}
