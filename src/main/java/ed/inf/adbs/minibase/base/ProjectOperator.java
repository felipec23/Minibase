package ed.inf.adbs.minibase.base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProjectOperator extends Operator {

    private Operator child;
    private List<Variable> variables;
    //    private Set<List<Term>> buffer;
//    private HashSet<List<Term>> buffer;
    private List<String> buffer;
    private Query query;

    private Catalog catalog;

    public ProjectOperator(Operator child, Query query) {
        super(null);
        this.child = child;
        this.query = query;
        this.variables = getHeadVariables();
//        this.buffer = new HashSet<>();
        this.buffer = new ArrayList<>();
        this.catalog = Catalog.getInstance();
    }

    public ProjectOperator(Query query) {
        super(query);
    }

    @Override
    public List<Term> getTermsOfRelationalAtom() {
        return null;
    }

    @Override
    public List<Term> getTermsOfRelationalAtom(String relationAtomName) {
        return null;
    }

//    @Override
//    public List<String> getSchema() {
//        return variables;
//    }

    //    Function to extract head from query, and get the variables from the head:
    public List<Variable> getHeadVariables() {
        List<Variable> queryHead = new ArrayList<>();
        for (Variable variable : query.getHead().getVariables()) {
            queryHead.add(variable);
        }
        return queryHead;
    }

//    Function to get the schema



    @Override
    public void open() {
        child.open();
    }

//    Function to check if projected tuple is in buffer:


    public boolean tupleIsInBuffer(Set<List<Term>> outerList, List innerList) {
        boolean isSubset = false;
        for (List<Term> list : outerList) {
            if (list.containsAll(innerList)) {
                isSubset = true;
                break;
            }
        }

        return isSubset;
    }

    //    Function to check if buffer has projected tuple:
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



    @Override
    public Tuple getNextTuple() {
        while (true) {
            Tuple tuple = child.getNextTuple();
            System.out.println("Tuple received in Projection: " + tuple);
            if (tuple == null) {
                return null;
            }
//            Create a list of terms:
            List<Term> projectedTuple = new ArrayList<>();

            for (Variable variable : variables) {
//                Print variable as string:
                System.out.println("Variable: " + variable.toString());
//                Get index of variable in the atom:
//                Get relation of child:


                // Read variables from tuple as string:
                List<Term> variablesTuple = tuple.getVariables();
                System.out.println("Variables in tuple: " + variablesTuple);

                // Iterate over variables in tuple:
                Integer index = 0;
                for (Term term : variablesTuple) {
                    System.out.println("Term: " + term);
                    System.out.println("Variable: " + variable);
                    if (term.equals(variable)) {
                        System.out.println("Variable found in tuple");
                        projectedTuple.add(tuple.getTuple(index));
                        break;
                    }

                    index++;
                }


            }


            if (!bufferHasProjectedTuple(buffer, projectedTuple)) {
                System.out.println("Buffer does not contain projected tuple");
//                buffer.add(projectedTuple);
                buffer.add(projectedTuple.toString());
//                System.out.println("Buffer: " + buffer);
//                Create a new tuple:
                Tuple newTuple = new Tuple(projectedTuple.toArray(new Term[projectedTuple.size()]));
                System.out.println("Projection: " + newTuple);
                return newTuple;


            }
        }
    }

    @Override
    public void close() {
        child.close();
        buffer.clear();
    }

    @Override
    public void dump() {
//        reset();
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

}