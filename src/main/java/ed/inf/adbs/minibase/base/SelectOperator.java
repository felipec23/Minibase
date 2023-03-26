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

    private String relationName;

    private RelationalAtom relationalAtom;

//    List of integers to store the index of the columns that are used in the selection condition
    private List<Integer> indexes;

    public SelectOperator(Query query, String relationName) {
        super(null);
        this.query = query;
        this.selectionCondition = setSelectionCondition(query);
        this.relationName = relationName;
        this.relationalAtom = extractRelationalAtom(relationName);
        this.child = createScanOperator(relationName);
        this.indexes = getIndexes();

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
        return null;
    }


    public boolean evaluateSelectionCondition(Tuple tuple) {

//        List to save all the results of the evaluation of the comparison atoms:
        List<Boolean> results = new ArrayList<>();

//        Start counter:
        int i = 0;
        //        Iterate over atoms in selection condition:
        for (ComparisonAtom comparisonAtom : selectionCondition) {
            System.out.println("Current comparison atom: " + comparisonAtom);

            Integer current = indexes.get(i);

            //          Get the value of the variable in the tuple:
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

    public static List<ComparisonAtom> setSelectionCondition(Query query) {

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


    //    Function to extract a relational atom given a relation name:
    public RelationalAtom extractRelationalAtom(String relationName) {
        List<Atom> body = query.getBody();
        for (Atom atom : body) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                if (relationalAtom.getName().equals(relationName)) {
                    return relationalAtom;
                }
            }
        }
        return null;
    }

//    private boolean evaluateSelectionCondition(Tuple tuple) {
//        for (ComparisonAtom atom : selectionCondition) {
//            if (!atom.evaluate(tuple)) {
//                return false;
//            }
//        }
//        return true;
//    }

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
            } else {
                System.out.println("Right term is not a variable");
            }


        }
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

    //    Function to create one scan operator, given a relation name:
    public ScanOperator createScanOperator(String relationName) {
        ScanOperator scanOperator = new ScanOperator(relationName);
        return scanOperator;
    }


    public List<Integer> getIndexes() {

//        Print selection condition:
        System.out.println("Selection condition: " + selectionCondition);

        //            List of integers to store the indexes of the variables in the relational atoms:
        List<Integer> indexes = new ArrayList<>();

//        Iterate over atoms in selection condition:
        for (ComparisonAtom comparisonAtom : selectionCondition) {
            System.out.println(comparisonAtom);

//            Get the right term of the comparison atom:
            Variable comparisonVariable = (Variable) comparisonAtom.getTerm1();

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

                }
                else {
                    System.out.println("Term is not a variable: " + term);

                }

                index++;



            }
//            return index;
            indexes.add(index);
        }

        //                Create a list of terms in the relational atom:
        List<Term> relationalAtomTerms = relationalAtom.getTerms();
        System.out.println(relationalAtomTerms);

//        Check if one of the terms in the relational atom is a constant:
        Integer constantIndex = 0;
        for (Term term : relationalAtomTerms) {
            if (term instanceof Constant) {
                Constant constant = (Constant) term;

//                Create new comparison atom with the constant as the right term:
//                Term here is a dumb variable
                ComparisonAtom comparisonAtom = new ComparisonAtom(term, constant, ComparisonOperator.EQ);
                System.out.println("Constant: " + constant);

//                Add the comparison atom to the selection condition:
                selectionCondition.add(comparisonAtom);
                System.out.println("New updated selection condition: " + selectionCondition);

                indexes.add(constantIndex);

            }

            constantIndex++;
        }

//        Print the indexes:
        System.out.println("Indexes: " + indexes);
        return indexes;


    };


        public void calculate(){
//        IN ALL PRINTS, PRINT THE NAME OF THE VARIABLE TO SEE WHAT IS BEING PRINTED!

//        Print selection condition:
            System.out.println("Selection condition: " + selectionCondition);

//        Iterate over atoms in selection condition:
            for (ComparisonAtom comparisonAtom : selectionCondition) {
                System.out.println(comparisonAtom);

//            Get the right term of the comparison atom:
                Variable comparisonVariable = (Variable) comparisonAtom.getTerm1();

//            Get relational atom for current relation:


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
                Tuple tuple = child.getNextTuple();
                System.out.println("Tuple from child:" + tuple);

//          Final index is:
                System.out.println("Final index is: " + index);

//          Get the value of the variable in the tuple:
                System.out.println(tuple.getTuple(index));

                Term toCompare = comparisonAtom.getTerm2();
                System.out.println("Variable to compare: " + toCompare);

//          Create new tuple. Left term is the value of the variable in the tuple, right term is the value of the variable in the comparison atom:
                Tuple tupleToSend = new Tuple(tuple.getTuple(index), toCompare);
                System.out.println("Tuple to send: " + tupleToSend);

                //                    Evaluate comparison atom, passing a tuple:
                System.out.println(comparisonAtom.evaluate(tupleToSend));


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



