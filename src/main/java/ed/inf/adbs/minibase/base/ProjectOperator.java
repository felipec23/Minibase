package ed.inf.adbs.minibase.base;
import java.util.ArrayList;
import java.util.List;

/**
 * This class receives the tuples from child and matches the tuple with the query variables.
 * @child the child operator
 * @query: the query parsed from the input file. Used to get the head variables.
 * @variables: the variables in the head of the query.
 * @buffer: the buffer to store the tuples that have already been projected.
 * @catalog: the catalog of the database.
 * @return: the projected tuples.
 *
 */
public class ProjectOperator extends Operator {

    private Operator child;
    private List<Variable> variables;
    private List<String> buffer;
    private Query query;
    private Catalog catalog;

    public ProjectOperator(Operator child, Query query) {
        super(null);
        this.child = child;
        this.query = query;
        this.variables = getHeadVariables();
        this.buffer = new ArrayList<>();
        this.catalog = Catalog.getInstance();
    }

    @Override
    public List<Term> getTermsOfRelationalAtom() {
        return null;
    }


    /**
     * Function to extract head from query, and get the variables from the head.
     * @return list of variables in the query head.
     */
    public List<Variable> getHeadVariables() {
        List<Variable> queryHead = new ArrayList<>();
        for (Variable variable : query.getHead().getVariables()) {
            queryHead.add(variable);
        }
        return queryHead;
    }


    @Override
    public void open() {
        child.open();
    }





    /**
     * Function to check if buffer has projected tuple:
     * @param buffer
     * @param projectedTuple
     * @return boolean indicates whether the tuple is already in the buffer.
     */
    public boolean bufferHasProjectedTuple(List<String> buffer, List<Term> projectedTuple) {
        boolean hasTuple = false;
        for (String element : buffer) {
            if (element.equals(projectedTuple.toString())) {
                hasTuple = true;
                break;
            }
        }
        return hasTuple;
    }


    /**
     * This calls the next tuple from the child operator and projects the tuple. We know which value corresponds
     * to which variable given that we store alongside the tuple, the variables that are present in the tuple.
     * This allows an easy match between the variable and the value. At the end, we check if the projected tuple
     * is already in the buffer. If it is, we discard it. If it is not, we add it to the buffer and return it.
     * @return tuple
     */
    @Override
    public Tuple getNextTuple() {
        while (true) {
            Tuple tuple = child.getNextTuple();
            if (tuple == null) {
                return null;
            }
//            Create a list of terms:
            List<Term> projectedTuple = new ArrayList<>();

            for (Variable variable : variables) {

                // Read variables from tuple as string:
                List<String> variablesTupleString = tuple.getVariablesAsListOfStrings();

                // Iterate over variables in tuple:
                Integer index = 0;
                for (String term : variablesTupleString) {
                    if (term.equals(variable.toString())) {
                        projectedTuple.add(tuple.getTuple(index));
                        break;
                    }

                    index++;
                }

            }


            if (!bufferHasProjectedTuple(buffer, projectedTuple)) {

                buffer.add(projectedTuple.toString());

                // Create a new tuple:
                Tuple newTuple = new Tuple(projectedTuple.toArray(new Term[projectedTuple.size()]));
                return newTuple;

            }
        }
    }

    @Override
    public void close() {
        child.close();
        buffer.clear();
    }



}