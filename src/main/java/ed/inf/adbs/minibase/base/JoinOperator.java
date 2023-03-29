package ed.inf.adbs.minibase.base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
//        this.rightTuple = this.rightChild.getNextTuple();
    }

//    @Override
//    public void open() {
//        System.out.println("Opening join operator");
//        leftChild.open();
//        rightChild.open();
//        leftTuple = leftChild.getNextTuple();
////        rightTuple = rightChild.getNextTuple();
//    }

//    @Override
//    public Tuple getNextTuple() {
//        Tuple mergedTuple;
//
//        while (leftTuple != null) {
//
//
//            // update right
//            if ((rightTuple = rightChild.getNextTuple()) == null) {
//                rightChild.reset();
//                rightTuple = rightChild.getNextTuple();
//                leftTuple = leftChild.getNextTuple();
//            }
//
//            if (checkTuples(leftTuple, rightTuple, joinPredicate)){
//                System.out.println("Tuples match");
//                mergedTuple = this.mergeTuple(leftTuple, rightTuple);
//                System.out.println("Merged tuple: " + mergedTuple);
//                return mergedTuple;
//
//            } else {
//                return mergedTuple = this.mergeTuple(leftTuple, rightTuple);
//
//            }
//        }
//        return null;
//    }


//    @Override
//    public Tuple getNextTuple(){
//        if(leftTuple == null) {
//            return null;
//        }
//
//        Tuple rightTuple = rightChild.getNextTuple();
//        while(rightTuple != null) {
//
//            if (checkTuples(leftTuple, rightTuple, joinPredicate)){
//                System.out.println("Tuples match");
//
//                // Merge the tuples
//                Tuple mergedTuple = this.mergeTuple(leftTuple, rightTuple);
//                System.out.println("Merged tuple: " + mergedTuple);
//                return mergedTuple;
//            }
//
//            rightTuple = rightChild.getNextTuple();
//        }
//        // Reset the right child
//        leftTuple = leftChild.getNextTuple();
//
//        if( leftTuple != null ) {
//            rightChild.reset();
//            return this.getNextTuple();
//        }
//        return null;
//    }

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

        System.out.println("Merged tuple: " + mergedTuple);
        System.out.println("Merged tuple variables: " + mergedTuple.getVariables());

        return mergedTuple;
    }


    @Override
    public List<Term> getTermsOfRelationalAtom() {
        return null;
    }

    @Override
    public List<Term> getTermsOfRelationalAtom(String relationAtomName) {
        return null;
    }

    @Override
    public Tuple getNextTuple(){
        System.out.println("TUPLE1 is: " + leftTuple);

        // Iterate over all the tuples in the first selection operator:
        while (true) {
            System.out.println("Selection operator 1 has next. Tuple 1 is: " + leftTuple);



            // If tuple1 is null, no more tuples in the selection operator:
            if (leftTuple == null) {
                System.out.println("Tuple 1 is null. No more tuples in selection operator 1");
                break;
            }

            // Iterate over all the tuples in the second selection operator:
            while (true) {
                System.out.println("Start of selection operator 2 in join");
                // Get the next tuple:
                Tuple tuple2 = rightChild.getNextTuple();

                // If tuple2 is null, no more tuples in the selection operator:
                if (tuple2 == null) {
                    System.out.println("Tuple 2 is null. No more tuples in selection operator 2");
                    System.out.println("Reseting selection operator 2 for next tuple 1");
                    rightChild.reset();
                    leftTuple = leftChild.getNextTuple();
                    System.out.println("Just updated. Next left tuple is: " + leftTuple);
                    break;
                }

                // Print the tuples:
                System.out.println("Tuple 1 join: " + leftTuple);
                System.out.println("Tuple 2 join: " + tuple2);

                if (checkTuples(leftTuple, tuple2, joinPredicate)) {
                    // Create a new tuple that will be the result of the join:
                    // Creat list of terms:
                    Tuple joinTuple = this.mergeTuple(leftTuple, tuple2);

                    // Print the join tuple:
                    System.out.println("Join tuple: " + joinTuple);

                    System.out.println("Left tuple is END: " + leftTuple);
                    return joinTuple;
                }

//                rightTuple = rightChild.getNextTuple();

                    else {
                        System.out.println("Tuples do not satisfy the join conditions");
                    }


            }


        }

        return null;
    }


    public static boolean checkTuples(Tuple tupleLeft, Tuple tupleRight, List<ComparisonAtom> newJoinConditions){
        //        List to save all the results of the evaluation of the comparison atoms:
        List<Boolean> results = new ArrayList<>();

//        Start counter:
        int i = 0;

        // Iterate over the new join conditions:
        for (ComparisonAtom comparisonAtom : newJoinConditions) {

            // Print the comparison atom:
            System.out.println("Comparison atom: " + comparisonAtom);

            // Print comparison atom indexes:
            System.out.println("Comparison atom indexes: " + comparisonAtom.getIndexes());

            // Get the position of the left term:
            //int leftTermPosition = comparisonAtom.getIndexes().get(0);
            List<Term> variables = tupleLeft.getVariables();

            System.out.println("Variable schema for the joined/left tuple: " + variables);

            // To find 'c' in the joined/accumulative tuple, we need to find the position of 'c' in the first tuple:
            //int leftTermPosition = variables.indexOf(comparisonAtom.getTerm1());

            // We do the search from the end of the list, in case the variable is repeated in the tuple:
            int leftTermPosition = variables.lastIndexOf(comparisonAtom.getTerm1());


            // Get the position of the right term:
            int rightTermPosition = comparisonAtom.getIndexes().get(1);

            // Get the values of the terms:
            Term leftTermValue = tupleLeft.getTuple(leftTermPosition);
            Term rightTermValue = tupleRight.getTuple(rightTermPosition);

//          Create new tuple. Left term is the value of the variable in the tuple, right term is the value of the variable in the comparison atom:
            Tuple tupleToSend = new Tuple(leftTermValue, rightTermValue);
            System.out.println("Tuple to send: " + tupleToSend);

            // Evaluate comparison atom, passing a tuple:
            boolean result = comparisonAtom.evaluate(tupleToSend);
            System.out.println("Result: " + result);

            results.add(result);
            i += 1;

        }

        if (results.contains(false)) {
            System.out.println("False found in results list");
            return false;

        }

        else {
            System.out.println("All atoms in selection conditions are true");
            return true;
        }

    }
//
//    @Override
//    public Tuple getNextTuple() {
//        while (leftTuple != null && rightTuple != null) {
//
//            // Create list of terms from left and right tuples
//            List<Term> terms = new ArrayList<>();
//            terms.add(leftTuple.getTuple(0));
//            terms.add(leftTuple.getTuple(1));
//            // Tuple joinedTuple = New Tuple(terms);
//            Tuple joinedTuple = new Tuple(terms.toArray(new Term[0]));
//
//            if (joinPredicate == null || joinPredicate.evaluate(joinedTuple)) {
//                rightTuple = rightChild.getNextTuple();
//                if (rightTuple == null) {
//                    leftTuple = leftChild.getNextTuple();
//                    rightChild.close();
//                    rightChild.open();
//                    rightTuple = rightChild.getNextTuple();
//                }
//                return joinedTuple;
//            } else {
//                rightTuple = rightChild.getNextTuple();
//                if (rightTuple == null) {
//                    leftTuple = leftChild.getNextTuple();
//                    rightChild.close();
//                    rightChild.open();
//                    rightTuple = rightChild.getNextTuple();
//                }
//            }
//        }
//        return null;
//    }



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