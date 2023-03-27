package ed.inf.adbs.minibase.base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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

//            Print schema relation of child:
            System.out.println("Schema of child: " + child.getSchemaOfRelation());

            for (Variable variable : variables) {
//                Print variable as string:
                System.out.println("Variable: " + variable.toString());
//                Get index of variable in the atom:
//                Get relation of child:
                String relationName = child.getRelationName();

//                Find the relation name in the body of the query:
                for (Atom atom : query.getBody()) {

//                    check if atom is instance of relational atom:
                    if (atom instanceof RelationalAtom) {
                        RelationalAtom relationalAtom = (RelationalAtom) atom;
                        if (relationalAtom.getName().equals(relationName)) {
                            System.out.println("Relation name: " + relationName);
                            System.out.println("Relation name of atom: " + relationalAtom.getName());
                            System.out.println("Terms of a tom: " + relationalAtom.getTerms());


//                            Check if variable is instance of Term class, if so,

//                            Get index of variable in the atom, but as a term:
                            int index = relationalAtom.getTermsAsString().indexOf(variable.toString());
                            System.out.println("Index of variable: " + index);
                            projectedTuple.add(tuple.getTuple(index));
                        }
                    }
                }
//

            }


////           Iterate over all elements in buffer, each element is a string:
//            for (String element : buffer) {
//                System.out.println("Element: " + element);
//                System.out.println("Projected tuple: " + projectedTuple);
//                if (element.equals(projectedTuple.toString())) {
//                    System.out.println("Buffer contains projected tuple");
//                    break;
//                }
//            }


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

}