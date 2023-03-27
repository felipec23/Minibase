package ed.inf.adbs.minibase.base;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SelectionOperator extends Operator{

    private List<ComparisonAtom> comparisonAtoms;

    private String relationName;

    private Operator child;

    public SelectionOperator(List<ComparisonAtom> comparisonAtoms, String relationName) {
        super(null);

        this.comparisonAtoms = comparisonAtoms;
        this.relationName = relationName;
        this.child = createScanOperator(relationName);


    }

    public ScanOperator createScanOperator(String relationName) {
        ScanOperator scanOperator = new ScanOperator(relationName);

        return scanOperator;
    }

    @Override
    public Tuple getNextTuple() {
        Tuple tuple = child.getNextTuple();

        while (tuple != null) {
            if (evaluateSelectionCondition(tuple)) {
                System.out.println("Tuple to return: " + tuple);
                return tuple;
            }

            tuple = child.getNextTuple();
            System.out.println("New tuple from scan: " + tuple);
        }
        System.out.println("No more tuples to return from scan. Thus, no more tuples to return from selection.");
        return null;
    }

   // Function to reset the child's operator:
    @Override
    public void reset() {
        child.reset();
    }


    @Override
    public void dump() {
        reset();
        Tuple tuple = getNextTuple();
        FileWriter pw = null;
        File file = new File("data/evaluation/data.csv");

        if (file.exists()) {
            file.delete();
        }

        while (tuple != null) {
            try {
                pw = new FileWriter("data/evaluation/data.csv" , true);
                pw.append(tuple.toString());
                pw.append("\n");
                pw.flush();
                pw.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            tuple = getNextTuple();
        }
    }

    @Override
    public boolean hasNext() {
        return child.hasNext();
    }


    public boolean evaluateSelectionCondition(Tuple tuple) {

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
