package ed.inf.adbs.minibase.base;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Operator to perform the comparison between atoms.
 */
public class SelectionOperator extends Operator{

    private List<ComparisonAtom> comparisonAtoms;

    private String relationName;

    private Operator child;

    public SelectionOperator(Operator child, List<ComparisonAtom> comparisonAtoms, String relationName) {
        super(null);

        this.comparisonAtoms = comparisonAtoms;
        this.relationName = relationName;
        this.child = child;


    }


    @Override
    public List<Term> getTermsOfRelationalAtom() {
        return null;
    }


    /**
     * Returns the next tuple of the child
     * @return tuple
     */
    @Override
    public Tuple getNextTuple() {
        Tuple t = null;
        while((t = child.getNextTuple())!=null){

            // Check whether tuple t satisfy the condition
            if (evaluateSelectionCondition(t)){

                return t;
            }
        }
        return null;
    }

   // Function to reset the child's operator:
    @Override
    public void reset() {
        child.reset();
    }


    @Override
    public boolean hasNext() {
        return child.hasNext();
    }

    /**
     * This function iterates over all the possible conditions that apply to a Tuple. We fetch the
     * position of each element referenced in a comparison atom. From here, we call the comparison
     * operator.
     *
     * @param tuple
     * @return boolean
     */
    public boolean evaluateSelectionCondition(Tuple tuple) {

        // List to save all the results of the evaluation of the comparison atoms:
        List<Boolean> results = new ArrayList<>();

        // Start counter:
        int i = 0;
        // Iterate over atoms in selection condition:
        for (ComparisonAtom comparisonAtom : comparisonAtoms) {

            // Get all relations names in the comparison atom:
            List<String> relations = comparisonAtom.getRelationsNames();

            // Find index of the relation in the tuple:
            int temp = relations.indexOf(relationName);

            // Get indexes list of the comparison atom:
            List<Integer> indexes = comparisonAtom.getIndexes();
            int current = indexes.get(temp);

            // Get the value of the variable in the tuple:
            Term term1 = tuple.getTuple(current);

            Term toCompare = comparisonAtom.getTerm2();

            // Create new tuple. Left term is the value of the variable in the tuple,
            // right term is the value of the variable in the comparison atom:
            Tuple tupleToSend = new Tuple(term1, toCompare);

            // Evaluate comparison atom, passing a tuple:
            boolean result = comparisonAtom.evaluate(tupleToSend);

            results.add(result);
            i += 1;

        }

        if (results.contains(false)) {
            return false;

        }

        else {
            return true;
        }

    }
}
