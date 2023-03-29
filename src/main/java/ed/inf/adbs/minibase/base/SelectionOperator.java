package ed.inf.adbs.minibase.base;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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



//    @Override
//    public Tuple getNextTuple() {
//        Tuple tuple = child.getNextTuple();
//
//        while (tuple != null) {
//            if (evaluateSelectionCondition(tuple)) {
//                System.out.println("Tuple to return: " + tuple);
//                return tuple;
//            }
//
//            tuple = child.getNextTuple();
//            System.out.println("New tuple from scan: " + tuple);
//        }
//        System.out.println("No more tuples to return from scan. Thus, no more tuples to return from selection.");
//        return null;
//    }

    @Override
    public List<Term> getTermsOfRelationalAtom() {
        return null;
    }

    @Override
    public List<Term> getTermsOfRelationalAtom(String relationAtomName) {
        return null;
    }

    @Override
    public Tuple getNextTuple() {
        Tuple t = null;
        while((t = child.getNextTuple())!=null){
            //check whether tuple t satisfy the condition
            if (evaluateSelectionCondition(t)){

                // Get the schema of the relation:
                //List<Term> termsInAtom = child.getTermsOfRelationalAtom();

                //System.out.println("Terms/variables in order in atom: " + termsInAtom);
                //t.setVariables(termsInAtom);

                System.out.println("VariablesTuple: " + t.getVariables());

                System.out.println("Passing from selection operator as accepted: " + t);
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


    public boolean evaluateSelectionCondition(Tuple tuple) {

        System.out.println("Evaluating selection condition");
        // Print comparison atoms:
        System.out.println("Comparison atoms: " + comparisonAtoms);

        // Print tuple:
        System.out.println("Tuple being evaluated: " + tuple);

//        List to save all the results of the evaluation of the comparison atoms:
        List<Boolean> results = new ArrayList<>();

//        Start counter:
        int i = 0;
        //        Iterate over atoms in selection condition:
        for (ComparisonAtom comparisonAtom : comparisonAtoms) {
            System.out.println("Current comparison atom: " + comparisonAtom);

            // Get all relations names in the comparison atom:
            List<String> relations = comparisonAtom.getRelationsNames();
            System.out.println("Relations in comparison atom: " + relations);

            // Find index of the relation in the tuple:
            int temp = relations.indexOf(relationName);

            // Get indexes list of the comparison atom:
            List<Integer> indexes = comparisonAtom.getIndexes();
            System.out.println("Indexes of the comparison atom: " + indexes);
            int current = indexes.get(temp);

            // Print index of the relation in the tuple:
            System.out.println("Index of the relation in the tuple: " + current);

            // Get the value of the variable in the tuple:
            Term term1 = tuple.getTuple(current);
            System.out.println("Value of variable in tuple: " + term1);

            Term toCompare = comparisonAtom.getTerm2();
            System.out.println("Variable to compare: " + toCompare);

//          Create new tuple. Left term is the value of the variable in the tuple, right term is the value of the variable in the comparison atom:
            Tuple tupleToSend = new Tuple(term1, toCompare);
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
}
