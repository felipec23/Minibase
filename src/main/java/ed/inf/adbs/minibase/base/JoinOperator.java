package ed.inf.adbs.minibase.base;
import java.util.ArrayList;
import java.util.List;


/**
 * Join operator is used to join two tuples. It takes two operators as input and a list of comparison.
 */
public class JoinOperator extends Operator {

    private Operator leftChild;
    private Operator rightChild;
    private List<ComparisonAtom> joinPredicate;

    private Tuple leftTuple;
    private Tuple rightTuple;

    public JoinOperator(Operator leftChild, Operator rightChild, List<ComparisonAtom> joinPredicate) {
        super(null);
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.joinPredicate = joinPredicate;
        this.leftTuple = this.leftChild.getNextTuple();
    }

    /**
     * All the elements are extracted from the list of terms. Then, the new tuple is created
     * and the variables are set. The variables here means the variables that are present in the tuple.
     * For example, if the tuple is (5, 3, 2), then the variables are (A, B, C). In that way, we keep
     * track of the variables that are present in each tuple.
     * @param leftTuple the tuple from the left child (operator)
     * @param rightTuple the tuple from the right child (operator)
     * @return the merged tuple from the left and right child
     *
     */
    private Tuple mergeTuple(Tuple leftTuple, Tuple rightTuple) {
        List<Term> terms = new ArrayList<>();
        for (Term term : leftTuple.getTuple()) {
            terms.add(term);
        }
        for (Term term : rightTuple.getTuple()) {
            terms.add(term);
        }

        Tuple mergedTuple = new Tuple(terms.toArray(new Term[0]));

        // Set the variables of the merged tuple
        List<Term> variables = new ArrayList<>();
        variables.addAll(leftTuple.getVariables());
        variables.addAll(rightTuple.getVariables());
        mergedTuple.setVariables(variables);

        return mergedTuple;
    }


    @Override
    public List<Term> getTermsOfRelationalAtom() {
        return null;
    }

    /**
     * For the GetNextTuple() method: we first initialize the left tuple to the first tuple in the left child.
     * Then, we iterate over all the tuples in the left child. For each tuple in the left child, we iterate
     * over all the tuples in the right child. If the join predicate is satisfied, we merge the two tuples
     * and return the merged tuple. If the join predicate is not satisfied, we move on to the next tuple
     * in the right child. If we reach the end of the right child, we reset the right child and move on
     * to the next tuple in the left child.
     *
     */
    @Override
    public Tuple getNextTuple(){

        // Iterate over all the tuples in the first selection operator:
        while (true) {

            // If tuple1 is null, no more tuples in the selection operator:
            if (leftTuple == null) {
                break;
            }

            // Iterate over all the tuples in the second selection operator:
            while (true) {
                // Get the next tuple:
                Tuple tuple2 = rightChild.getNextTuple();

                // If tuple2 is null, no more tuples in the selection operator:
                if (tuple2 == null) {
                    rightChild.reset();
                    leftTuple = leftChild.getNextTuple();
                    break;
                }

                if (checkTuples(leftTuple, tuple2, joinPredicate)) {
                    // Create a new tuple that will be the result of the join:
                    // Creat list of terms:
                    Tuple joinTuple = this.mergeTuple(leftTuple, tuple2);

                    return joinTuple;
                }


                    else {
                        //System.out.println("Tuples do not satisfy the join conditions");
                    }
            }

        }

        return null;
    }

    /**
     * This method checks if the tuples satisfy the join conditions. It takes as input the two tuples
     * and the list of join conditions. It returns true if the tuples satisfy all the join conditions, false
     * otherwise.
     * @param tupleLeft the left tuple
     * @param tupleRight the right tuple
     * @param newJoinConditions the list of join conditions
     * @return true if the tuples satisfy all the join conditions, false otherwise
     */
    public static boolean checkTuples(Tuple tupleLeft, Tuple tupleRight, List<ComparisonAtom> newJoinConditions){

        // List to save all the results of the evaluation of the comparison atoms:
        List<Boolean> results = new ArrayList<>();

        // Start counter:
        int i = 0;

        // If newJoinConditions is empty, return true:
        if (newJoinConditions.isEmpty()) {
            return true;
        }

        // Iterate over the new join conditions:
        for (ComparisonAtom comparisonAtom : newJoinConditions) {

            // Get the position of the left term:
            List<String> variables = tupleLeft.getVariablesAsListOfStrings();

            // To find 'c' in the joined/accumulative tuple, we need to find the position of 'c' in the first tuple:
            // We do the search from the end of the list, in case the variable is repeated in the tuple:
            int leftTermPosition = variables.lastIndexOf(comparisonAtom.getTerm1().toString());

            // Get the position of the right term:
            int rightTermPosition = comparisonAtom.getIndexes().get(1);

            // Get the values of the terms:
            Term leftTermValue = tupleLeft.getTuple(leftTermPosition);
            Term rightTermValue = tupleRight.getTuple(rightTermPosition);

            // Create new tuple. Left term is the value of the variable in the tuple, right term is the value of the variable in the comparison atom:
            Tuple tupleToSend = new Tuple(leftTermValue, rightTermValue);

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


    @Override
    public void close() {
        leftChild.close();
        rightChild.close();
    }

    @Override
    public void reset() {
        leftChild.reset();
        rightChild.reset();
    }



}