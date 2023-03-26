package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.parser.QueryParser;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.List;

public class SelectOperator extends Operator {

    private Operator child;
    private List<ComparisonAtom> selectionCondition;

    private Query query;

    public SelectOperator(Query query) {
        super(query);
//        this.child = child;
        this.query = query;

        this.selectionCondition = setSelectionCondition(query);
    }

//    @Override
//    public Tuple getNextTuple() {
//        Tuple tuple = child.getNextTuple();
//        while (tuple != null) {
//            if (evaluateSelectionCondition(tuple)) {
//                return tuple;
//            }
//            tuple = child.getNextTuple();
//        }
//        return null;
//    }

//    Create function to getNextTuple() given a scan operator:
    public Tuple getNextTuple(ScanOperator scanOperator) {
        Tuple tuple = scanOperator.getNextTuple();
        while (tuple != null) {
            if (evaluateSelectionCondition(tuple)) {
                return tuple;
            }
            tuple = scanOperator.getNextTuple();
        }
        return null;
    }

    private boolean evaluateSelectionCondition(Tuple tuple) {
        for (ComparisonAtom atom : selectionCondition) {
            if (!atom.evaluate(tuple)) {
                return false;
            }
        }
        return true;
    }

    public void checkRightSelectionCondition() {
        for (ComparisonAtom atom : selectionCondition) {
            System.out.println(atom);

//            Get the right term of the comparison atom:
            Term term = atom.getTerm2();
            System.out.println(term);

//            Check if the right term is a variable:
            if (term instanceof Variable) {
                Variable variable = (Variable) term;
                System.out.println(variable);

//                Special case
            }

            else
            {
                System.out.println("Right term is not a variable");
            }



        }
    }



    public List<ComparisonAtom> setSelectionCondition(Query query) {

//       Get body from query and assign to a variable:
        List<Atom> body = query.getBody();

//        Create an empty list to store comparison atoms
        List<ComparisonAtom> selectionCondition = new ArrayList<>();


//        Iterate over body and extract and save all comparison atoms to a list:
        for (Atom atom : body) {
            if (atom instanceof ComparisonAtom) {
                ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
                selectionCondition.add(comparisonAtom);
            }
        }

        return selectionCondition;
    }

//    Function to get relations from query:



    public List<String> getRelationsFromQuery() {
        List<String> relations = new ArrayList<>();
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                relations.add(relationalAtom.getName());
            }
        }
        return relations;
    }

//    Get relational atoms from query:
    public List<RelationalAtom> getRelationalAtomsFromQuery() {
        List<RelationalAtom> relationalAtoms = new ArrayList<>();
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                relationalAtoms.add(relationalAtom);
            }
        }
        return relationalAtoms;
    }

    public void calculate() {
//        IN ALL PRINTS, PRINT THE NAME OF THE VARIABLE TO SEE WHAT IS BEING PRINTED!

//        Print selection condition:
        System.out.println("Selection condition: " + selectionCondition);

//        Get relations from query:
        List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery();
        System.out.println("Relations: " + relationalAtoms);

//        Create a scan operator for each relation in the query and save it to a list:
        List<ScanOperator> scanOperators = new ArrayList<>();
        for (RelationalAtom relationalAtom : relationalAtoms) {
            String relationName = relationalAtom.getName();
            System.out.println("Relation name: " + relationName);
            ScanOperator scanOperator = new ScanOperator(query);
            scanOperators.add(scanOperator);
        }

//        Iterate over atoms in selection condition:
        for (ComparisonAtom comparisonAtom : selectionCondition) {
            System.out.println(comparisonAtom);

//            Get the right term of the comparison atom:
            Variable comparisonVariable = (Variable) comparisonAtom.getTerm1();


//            Initialize counter for the index of the scan operator:
            int scanOperatorIndex = 0;

//            Iterate over relational atoms:
            for (RelationalAtom relationalAtom : relationalAtoms) {
                System.out.println(relationalAtom);

//                Create a list of terms in the relational atom:
                List<Term> relationalAtomTerms = relationalAtom.getTerms();
                System.out.println(relationalAtomTerms);

//                              Get index of the variable in the relational atom by iterating and getting the name of each variable:
                int index = 0;
                for (Term term : relationalAtomTerms) {
                    if (term instanceof Variable) {
                        Variable variable = (Variable) term;
                        System.out.println(variable.getName());
                        if (variable.getName().equals(comparisonVariable.getName())) {
                            System.out.println("Found the index: " + index);
                            break;
                        }
                        index++;
                    }
                }

//                Get the next tuple from the scan operator:
                Tuple tuple = getNextTuple(scanOperators.get(scanOperatorIndex));
                System.out.println(tuple);

//                            Final index is:
                System.out.println("Final index is: " + index);



//                            Get the value of the variable in the tuple:
                System.out.println(tuple.getTuple(index));

//                Increment scan operator index:
                scanOperatorIndex++;



            }

        }

//        Get
    }
    }

//
//
//    private void
//
//
////          Print comparison atoms from body:
//        for (Atom atom : body) {
//            if (atom instanceof ComparisonAtom) {
//
////                    Assign comparison atom to a variable:
//                ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
//                System.out.println(atom);
////                    Print terms and operator:
//                System.out.println(comparisonAtom.getTerm1());
//                System.out.println(comparisonAtom.getTerm2());
//                System.out.println(comparisonAtom.getOp());
//
//                Variable comparisonVariable = (Variable) comparisonAtom.getTerm1();
//                System.out.println("comparisonVariable" + comparisonVariable);
//
////                    Get comparison variable name:
//
//                System.out.println(comparisonAtom);
//
////                    Iterate over all relational atoms:
//                for (Atom atom2 : body) {
//                    if (atom2 instanceof RelationalAtom) {
//                        RelationalAtom relationalAtom = (RelationalAtom) atom2;
//                        System.out.println(relationalAtom);
//                        System.out.println(relationalAtom.getTerms());
//
////                            Get atom name (relation):
//                        String atomName = relationalAtom.getName();
////                            Print atom name, say variable name:
//                        System.out.println("atomName: " + atomName);
//
////                            Get terms:
//                        List<Term> relationalAtomTerms = relationalAtom.getTerms();
//                        System.out.println("relationalAtomTerms: " + relationalAtomTerms);
//
////                            Print type of each element in the list:
//                        for (Term term : relationalAtomTerms) {
//                            System.out.println(term.getClass());
//                        }
//
////                            Print the type of the comparison variable:
//                        System.out.println(comparisonVariable.getClass());
//
//
////                            Get index of the variable in the relational atom by iterating and getting the name of each variable:
//                        int index = 0;
//                        for (Term term : relationalAtomTerms) {
//                            if (term instanceof Variable) {
//                                Variable variable = (Variable) term;
//                                System.out.println(variable.getName());
//                                if (variable.getName().equals(comparisonVariable.getName())) {
//                                    System.out.println("Found the index: " + index);
//                                    break;
//                                }
//                                index++;
//                            }
//                        }
//
////                            Final index is:
//                        System.out.println("Final index is: " + index);
//
//
//
////                            Get the value of the variable in the tuple:
//                        System.out.println(tupleHere.getTuple(index));
//
////                            Create new tuple. Left term is the value of the variable in the tuple, right term is the value of the variable in the comparison atom:
//                        Tuple tuple = new Tuple(tupleHere.getTuple(index), comparisonAtom.getTerm2());
//                        System.out.println("Tuple to send: " + tuple);
//
//                        //                    Evaluate comparison atom, passing a tuple:
//                        System.out.println(comparisonAtom.evaluate(tuple));



//                    }
//                }



//            }
















//
////    getNextTuple()
////method will grab the next tuple from its child
////operator and then apply the selection predicate to it. If the tuple satisfies the predicate, it will be returned. If not, the method will return the next tuple from the child operator until it finds a tuple that satisfies the predicate or until it reaches the end of the child operator’s tuples.
//
////    Constructor:
//    private Operator child;
//    private List<ComparisonAtom> selectionCondition;
//
//    public SelectOperator(Operator child, List<ComparisonAtom> selectionCondition) {
//        super(child, selectionCondition);
////        super(child, predicate);
//        this.child = child;
//        this.predicate = predicate;
//    }
//


//    private boolean evaluateSelectionCondition(Tuple tuple) {
//        for (ComparisonAtom atom : selectionCondition) {
//            if (!atom.evaluate(tuple)) {
//                return false;
//            }
//        }
//        return true;
//    }

//    Function to get next tuple:



