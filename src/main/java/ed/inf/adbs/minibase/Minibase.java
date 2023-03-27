package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

// Import Catalog class created:


/**
 * In-memory database system
 *
 */
public class Minibase {

    public static void main(String[] args) {

        if (args.length != 3) {
            System.err.println("Usage: Minibase database_dir input_file output_file");
            return;
        }

        String databaseDir = args[0];
        String inputFile = args[1];
        String outputFile = args[2];

        try {
            evaluateCQ(databaseDir, inputFile, outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        parsingExample(inputFile);
    }






    public static List<String> getRelationsFromQuery(Query query) {
        List<String> relations = new ArrayList<>();
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                relations.add(relationalAtom.getName());
            }
        }
        return relations;
    }

    public static List<RelationalAtom> getRelationalAtomsFromQuery(Query query) {
        List<RelationalAtom> relationalAtoms = new ArrayList<>();
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                relationalAtoms.add(relationalAtom);
            }
        }
        return relationalAtoms;
    }


    public static String getQueryType(Query query) {

//        Only scan: if there are not comparisons atoms and if all terms in head are variables:
//        Iterate over the body of the query:

        boolean hasComparisonAtoms = false;
//        Save each relational atom in a list:

        List<String> relationalVariables = new ArrayList<>();
        for (Atom atom : query.getBody()) {
            if (atom instanceof ComparisonAtom) {
                hasComparisonAtoms = true;
            }

//            Get the terms of the relational atom:
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                List<Term> terms = relationalAtom.getTerms();
                for (Term term : terms) {
                    relationalVariables.add(term.toString());
                }
            }
        }


//        Iterate over the head of the query:
        boolean allHeadTermsAreVariables = true;

//        Iterate over the terms in the head of the query:
        Head head = query.getHead();

        List<String> headVariables = new ArrayList<>();

//        // Print all the variables in the head of the query and their type:
//        for (Term term : head.getVariables()) {
//            System.out.println("Variable: " + term.toString() + " Type: " + term.getClass());
//        }

        // Print sum aggregate function:
        SumAggregate sumAggregate = head.getSumAggregate();
        System.out.println("Sum aggregate function: " + sumAggregate);


        // Save all the variables in the head of the query:

        for (Term term : head.getVariables()) {
            headVariables.add(term.toString());
            if (!(term instanceof Variable)) {
                allHeadTermsAreVariables = false;
            }
        }

        // If length of headVariables is 0, then there are no variables in the head of the query:
        if (headVariables.size() == 0) {
            allHeadTermsAreVariables = false;
        }
//            Get number of relational atoms in body
        List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);
        int numberOfRelationalAtoms = relationalAtoms.size();

        if (!hasComparisonAtoms && allHeadTermsAreVariables && numberOfRelationalAtoms == 1) {

//            Check if the head is in the same order as the body:
            if (headVariables.equals(relationalVariables)) {
//                Only scan:
                return "scan";
            } else {
//                Scan, but change the order of the columns:
                return "projection";
            }

        }

        else if (hasComparisonAtoms && allHeadTermsAreVariables && numberOfRelationalAtoms == 1) {

//          Only one relational atom in body:

            if (headVariables.equals(relationalVariables)) {
//              Only selection:
                return "selection";
            } else {
//              Selection and projection:
                return "selection_projection";
            }

        }

        else if (numberOfRelationalAtoms > 1) {
            // For join, it will be detected at each step what is necessary to do:
            return "join";

        }


        return "other";
    }


//    function to do only scan
    public static void runScan(Query query){
        //            Iterate over each relational atom and get the relation name
        List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);

        for (RelationalAtom relationalAtom : relationalAtoms) {
            String relationName = relationalAtom.getName();
            System.out.println("Current relation name: " + relationName);

            //        Create ScanOperator object, given the query and the relation name
            ScanOperator scanOperator = new ScanOperator(relationName);

            System.out.println("Before dump:");
            scanOperator.dump();

        }
    }

    public static void runSelection(Query query) {
        //            Iterate over each relational atom and get the relation name
        List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);

        for (RelationalAtom relationalAtom : relationalAtoms) {
            String relationName = relationalAtom.getName();
            System.out.println("Current relation name: " + relationName);

            //        Create SelectionOperator object, given the query and the relation name
            SelectOperator selectionOperator = new SelectOperator(query, relationName);

            System.out.println("Selection operator dump:");
            selectionOperator.dump();
        }
    }


    public static void runProjection(Query query) {
        //            Iterate over each relational atom and get the relation name
        List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);

        for (RelationalAtom relationalAtom : relationalAtoms) {
            String relationName = relationalAtom.getName();
            System.out.println("Current relation name: " + relationName);

//            For this case, the child is a scan operator:
            ScanOperator scanOperator = new ScanOperator(relationName);

            //        Create ProjectionOperator object, given the query and the relation name
            ProjectOperator projectionOperator = new ProjectOperator(scanOperator, query);

            System.out.println("Projection operator dump:");
            projectionOperator.dump();
        }
    }

    public static void runSelectionProjection(Query query) {
        //            Iterate over each relational atom and get the relation name
        List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);

        for (RelationalAtom relationalAtom : relationalAtoms) {
            String relationName = relationalAtom.getName();
            System.out.println("Current relation name: " + relationName);


            //        Create SelectionOperator object, given the query and the relation name
            SelectOperator selectionOperator = new SelectOperator(query, relationName);

            //        Create ProjectionOperator object, given the query and the relation name
            ProjectOperator projectionOperator = new ProjectOperator(selectionOperator, query);

            System.out.println("Selection projection operator dump:");
            projectionOperator.dump();
        }

    }

//    Create a function to get the comparison atoms from the query:
    public static List<ComparisonAtom> getComparisonAtomsFromQuery(Query query) {
        List<ComparisonAtom> comparisonAtoms = new ArrayList<>();
        for (Atom atom : query.getBody()) {
            if (atom instanceof ComparisonAtom) {
                ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
                comparisonAtoms.add(comparisonAtom);
            }
        }
        return comparisonAtoms;
    }

    public static List<ComparisonAtom> detectImplicitConditions(List<RelationalAtom> relationalAtoms ) {

        // Detect implicit conditions, that is, if there is a relational atom with a constant:
        // Save the relational atoms with constants in a list:
        List<ComparisonAtom> newComparisonAtoms = new ArrayList<>();
        for (RelationalAtom relationalAtom : relationalAtoms) {
            // Create index for keeping track of the position of the constant:
            int index = 0;
            for (Term term : relationalAtom.getTerms()) {
                if (term instanceof Constant) {
                    // System.out.println("Implicit condition detected: " + relationalAtom + " = " + term);

                    // Save as a comparison atom:
                    ComparisonAtom comparisonAtom = new ComparisonAtom(term, term, ComparisonOperator.EQ);

                    // Set type and relation name and index:
                    comparisonAtom.setType("implicit");

                    // Create list with the relation names:
                    List<String> relationNames = new ArrayList<>();
                    relationNames.add(relationalAtom.getName());
                    comparisonAtom.setRelationsNames(relationNames);

                    // Create list with the indexes:
                    List<Integer> indexes = new ArrayList<>();
                    indexes.add(index);
                    comparisonAtom.setIndexes(indexes);

                    // Add to the list of new comparison atoms:
                    newComparisonAtoms.add(comparisonAtom);
                }
                index++;
            }
        }

        return newComparisonAtoms;
    }

    public static List<ComparisonAtom> detectEquijoinConditions(List<RelationalAtom> relationalAtoms){
        // Find if two relational atoms have the same variable:
        // If so, then it is an equi-join condition:

        // Save the relational atoms with constants in a list:
        List<ComparisonAtom> newComparisonAtoms = new ArrayList<>();

        // Iterate over each relational atom:
        for (int i = 0; i < relationalAtoms.size(); i++) {
            RelationalAtom relationalAtom1 = relationalAtoms.get(i);
            for (int j = i + 1; j < relationalAtoms.size(); j++) {
                RelationalAtom relationalAtom2 = relationalAtoms.get(j);

                // Iterate over each term of the first relational atom:
                for (Term term1 : relationalAtom1.getTerms()) {
                    // Iterate over each term of the second relational atom:
                    for (Term term2 : relationalAtom2.getTerms()) {
                        if (term1.equals(term2)) {
                            // System.out.println("Equi-join condition detected: " + relationalAtom1 + " = " + relationalAtom2);

                            // Save as a comparison atom:
                            ComparisonAtom comparisonAtom = new ComparisonAtom(term1, term2, ComparisonOperator.EQ);

                            // Set type and relation name and index:
                            comparisonAtom.setType("equi-join");
                            List<String> relationNames = new ArrayList<>();
                            relationNames.add(relationalAtom1.getName());
                            relationNames.add(relationalAtom2.getName());
                            comparisonAtom.setRelationsNames(relationNames);

                            // Create list with the indexes:
                            List<Integer> indexes = new ArrayList<>();
                            indexes.add(relationalAtom1.getTerms().indexOf(term1));
                            indexes.add(relationalAtom2.getTerms().indexOf(term2));
                            comparisonAtom.setIndexes(indexes);


                            // Add to the list of new comparison atoms:
                            newComparisonAtoms.add(comparisonAtom);
                        }
                    }
                }
            }
        }

        return newComparisonAtoms;

    }

    public static Map<String, Map<String, Integer>> createMap(List<RelationalAtom> relationalAtoms) {
        Map<String, Map<String, Integer>> myMap = new HashMap<>();

        // create a new inner map


        // Iterate over each relational atom:
        for (RelationalAtom relationalAtom : relationalAtoms) {
            // Iterate over each term of the relational atom:
            for (Term term : relationalAtom.getTerms()) {
                if (term instanceof Variable) {
                    // Get the index of the term in the relational atom:
                    Integer index = getTermIndex(term, relationalAtom);
                    // System.out.println("Index of " + term + " in " + relationalAtom + ": " + index);

                    // Add the term and the index to the inner map:
                    Map<String, Integer> innerMap = new HashMap<>();
                    innerMap.put(relationalAtom.getName(), index);

                    // Fetch the inner map from the outer map:
                    Map<String, Integer> innerMap2 = myMap.get(term.toString());

                    // If the inner map is not null, then add the new inner map to the existing inner map:
                    if (innerMap2 != null) {
                        innerMap2.put(relationalAtom.getName(), index);
                        myMap.put(term.toString(), innerMap2);
                    }
                    else {
                        // Add the inner map to the outer map:
                        myMap.put(term.toString(), innerMap);

                    }


                }
            }
        }

        System.out.println("Map: " + myMap);
        return myMap;
    }

    public static Integer getTermIndex(Term term, RelationalAtom relationalAtom){
        // Get the index of the term in the relational atom:
        Integer index = -1;
        for (int i = 0; i < relationalAtom.getTerms().size(); i++) {
            if (term.equals(relationalAtom.getTerms().get(i))) {
                index = i;
            }
        }
        return index;
    }


    public static List<ComparisonAtom>  analyzeConditions(Query query){
        // Get relational atoms from query:
        List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);

        // Detect implicit conditions, that is, if there is a relational atom with a constant:
        List<ComparisonAtom> relationalAtomsWithConstants = detectImplicitConditions(relationalAtoms);

        // Detect equi-join conditions:
        List<ComparisonAtom> relationalAtomsWithEquiJoinConditions = detectEquijoinConditions(relationalAtoms);

        // Get comparison atoms from query:
        List<ComparisonAtom> comparisonAtoms = getComparisonAtomsFromQuery(query);

        // Create map of where each variable is in the relational atoms:
        Map<String, Map<String, Integer>> map = createMap(relationalAtoms);

        // Between atoms: equijoin and implicit

        // Iterate over comparison atoms and get the type:
        for (ComparisonAtom comparisonAtom : comparisonAtoms) {

            // If the term to the left and right are variables:
            if (comparisonAtom.getTerm1() instanceof Variable && comparisonAtom.getTerm2() instanceof Variable) {

                // Get where the variables are in the relational atoms:
                Map<String, Integer> map1 = map.get(comparisonAtom.getTerm1().toString());
                Map<String, Integer> map2 = map.get(comparisonAtom.getTerm2().toString());

                // If the variables are in the same relational atom:
                if (map1.keySet().equals(map2.keySet())) {
                    // Set the type to "between atoms":
                    comparisonAtom.setType("between atom");
                    // Set the relation name:
                    comparisonAtom.setRelationsNames(new ArrayList<>(map1.keySet()));
                    // Create list with the indexes:
                    List<Integer> indexes = new ArrayList<>();
                    indexes.add(map1.get(comparisonAtom.getRelationsNames().get(0)));
                    indexes.add(map2.get(comparisonAtom.getRelationsNames().get(0)));
                    comparisonAtom.setIndexes(indexes);

                }

                // If the variables are in different relational atoms:
                else {
                    // Set the type to "different atoms":
                    comparisonAtom.setType("different atoms");
                    // Set the relation name:
                    List<String> relationNames = new ArrayList<>();
                    relationNames.addAll(map1.keySet());
                    relationNames.addAll(map2.keySet());
                    comparisonAtom.setRelationsNames(relationNames);
                    // Create list with the indexes:
                    List<Integer> indexes = new ArrayList<>();
                    indexes.add(map1.get(comparisonAtom.getRelationsNames().get(0)));
                    indexes.add(map2.get(comparisonAtom.getRelationsNames().get(1)));
                    comparisonAtom.setIndexes(indexes);

                }
            }

            // If the term to the left is a variable and the term to the right is a constant:
            else if (comparisonAtom.getTerm1() instanceof Variable && comparisonAtom.getTerm2() instanceof Constant) {
                // Get where the variable is in the relational atoms:
                Map<String, Integer> map1 = map.get(comparisonAtom.getTerm1().toString());

                // Set the type to "between atoms":
                comparisonAtom.setType("between atom");
                // Set the relation name:
                comparisonAtom.setRelationsNames(new ArrayList<>(map1.keySet()));
                // Create a list of indexes:
                List<Integer> indexes = new ArrayList<>();
                // Iterate over the relation names:
                for (String relationName : comparisonAtom.getRelationsNames()) {
                    // Add the index to the list of indexes:
                    indexes.add(map1.get(relationName));
                }
                comparisonAtom.setIndexes(indexes);


            }
        }

        // Add the relational atoms with constants to the list of comparison atoms:
        comparisonAtoms.addAll(relationalAtomsWithConstants);

        // Add the relational atoms with equi-join conditions ONLY if there's no a default comparison that uses the same variable:
//        for (ComparisonAtom equiJoin : relationalAtomsWithEquiJoinConditions) {
//            boolean add = true;
//            for (ComparisonAtom comparisonAtom : comparisonAtoms) {
//                if (comparisonAtom.getTerm1().equals(equiJoin.getTerm1())) {
//                    add = false;
//                }
//            }
//            if (add) {
//                comparisonAtoms.add(equiJoin);
//            }
//        }

        comparisonAtoms.addAll(relationalAtomsWithEquiJoinConditions);

        // Iterate over comparison atoms and print them and their type and their relation names:
        for (ComparisonAtom comparisonAtom : comparisonAtoms) {
            System.out.println("Comparison atom: " + comparisonAtom);
            System.out.println("Type: " + comparisonAtom.getType());
            System.out.println("Relation names: " + comparisonAtom.getRelationsNames());
            System.out.println("Index: " + comparisonAtom.getIndexes());
        }


        return comparisonAtoms;


    }


    public static List<List<RelationalAtom>> getJoinGroups(Query query) {

        // Read the query atoms and group them by two, from left to right:
        List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);

        // Get number of relational atoms:
        int numberOfRelationalAtoms = relationalAtoms.size();

        // Create list of relational atoms that will be joined together:
        List<List<RelationalAtom>> joinsGroups = new ArrayList<>();

        // Iterate over size of relational atoms:
        for (int i = 0; i < numberOfRelationalAtoms; i= i + 2) {

            // Check if this atom is the last one:
            if (i + 1 == numberOfRelationalAtoms) {
                List<RelationalAtom> lastGroup = new ArrayList<>();
                lastGroup.add(relationalAtoms.get(i));
                joinsGroups.add(lastGroup);
                break;
            }

            else {
                // Join the current relational atom with the next relational atom:
                List<RelationalAtom> group = new ArrayList<>();
                group.add(relationalAtoms.get(i));
                group.add(relationalAtoms.get(i + 1));

                // Add the group to the list of groups:
                joinsGroups.add(group);
            }



        }

        // Iterate over the list of groups and print them:
        for (List<RelationalAtom> group : joinsGroups) {
            System.out.println("Group: " + group);
        }

        // Return

        return joinsGroups;


    }

    // Function to get the comparison atoms that are between the first atom
    public static List<ComparisonAtom> getComparisonAtomsBetweenAtom(String relAtomName, List<ComparisonAtom> allComparisonAtoms) {

        List<ComparisonAtom> comparisonAtomsBetweenAtom = new ArrayList<>();

        for (ComparisonAtom comparisonAtom : allComparisonAtoms) {
            if (comparisonAtom.getType().equals("between atom") || comparisonAtom.getType().equals("implicit")) {

                if (comparisonAtom.getRelationsNames().contains(relAtomName)) {
                    comparisonAtomsBetweenAtom.add(comparisonAtom);
                }

            }
        }

        // Return the list of comparison atoms that are between the first atom:
        return comparisonAtomsBetweenAtom;

    }

    public static List<ComparisonAtom> findJoinCondition(String relAtom1Name, String relAtom2Name, List<ComparisonAtom>comparisonAtoms){

        // Create list for saving the relational atoms that have equi-join conditions:
        List<ComparisonAtom> joinConditions = new ArrayList<>();

        // Iterate over the comparison atoms:
        for (ComparisonAtom comparisonAtom : comparisonAtoms) {
            // If the comparison atom is between the two relational atoms:
            if (comparisonAtom.getRelationsNames().contains(relAtom1Name) && comparisonAtom.getRelationsNames().contains(relAtom2Name)) {
                // If the comparison atom type is an equi-join condition or "different atoms":
                if (comparisonAtom.getType().equals("equi-join") || comparisonAtom.getType().equals("different atoms")) {
                    // Add the comparison atom to the list of join conditions:
                    joinConditions.add(comparisonAtom);
                }
            }
        }

        // Return the list of join conditions:
        return joinConditions;


    }


    public static void getIndexesForJoin(RelationalAtom relAtom1, RelationalAtom relAtom2){

        // This function finds the common variables between the two relational atoms and gets the indexes of the common variables in the two relational atoms.

        // Get the terms of the two relational atoms:
        List<Term> relAtom1Variables = relAtom1.getTerms();
        List<Term> relAtom2Variables = relAtom2.getTerms();

        // Create list for saving the common variables:
        List<Term> commonVariables = new ArrayList<>();

        // Create list for saving the indexes of the common variables:
        List<Integer> commonVariablesIndexes = new ArrayList<>();

        // Create list for saving the indexes of the common variables in the first relational atom:
        List<Integer> commonVariablesIndexesInRelAtom1 = new ArrayList<>();

        // Create list for saving the indexes of the common variables in the second relational atom:
        List<Integer> commonVariablesIndexesInRelAtom2 = new ArrayList<>();

        // Iterate over the terms of the first relational atom:
        for (Term term1 : relAtom1Variables) {
            // Iterate over the terms of the second relational atom:
            for (Term term2 : relAtom2Variables) {
                // If the two terms are equal:
                if (term1.equals(term2)) {
                    // Add the term to the list of common variables:
                    commonVariables.add(term1);

                    // Get the index of the common variable in the first relational atom:
                    int index1 = relAtom1Variables.indexOf(term1);

                    // Get the index of the common variable in the second relational atom:
                    int index2 = relAtom2Variables.indexOf(term2);

                    // Make a list with both indexes:
                    List<Integer> indexes = new ArrayList<>();
                    indexes.add(index1);
                    indexes.add(index2);
                }
            }
        }



    }







    public static void runJoins(List<List<RelationalAtom>> joinsGroups, List<ComparisonAtom> comparisonAtoms){

        // Create list for saving the results of each join:
        List<List<Tuple>> joinedTuples = new ArrayList<>();

        List<List<Term>> joinedTerms = new ArrayList<>();

        List<List<String>> joinedRelationsNames = new ArrayList<>();

        // Iterate over the groups:
        for (List<RelationalAtom> group : joinsGroups) {
            // If the group has only one relational atom:
            if (group.size() == 1) {

                List<Tuple> tuplesToStore = new ArrayList<>();
//                List<RelationalAtom> atomsToSave = new ArrayList<>();
//
                RelationalAtom relationalAtom1 = group.get(0);
                String relAtomName1 = relationalAtom1.getName();
                List<ComparisonAtom> comparisonAtomsBetweenAtom1 = getComparisonAtomsBetweenAtom(relAtomName1, comparisonAtoms);
                SelectionOperator selectionOp1 = new SelectionOperator(comparisonAtomsBetweenAtom1, relationalAtom1.getName());

                // Saving order of terms
                List<Term> termsToStore = new ArrayList<>();
                termsToStore.addAll(relationalAtom1.getTerms());

                // Saving order of relations names
                List<String> relationsNamesToStore = new ArrayList<>();
                relationsNamesToStore.add(relAtomName1);


                // Itearate over all the tuples from the selection operator:
                while (true){
                    Tuple tuple = selectionOp1.getNextTuple();
                    if (tuple == null){
                        break;
                    }

                    tuplesToStore.add(tuple);


                    // Create list of terms with the tuple:
//                    List<Term> terms = new ArrayList<>();
//                    for (int i = 0; i < tuple.getTuple().size(); i++) {
//                        terms.add(new Term(tuple.getTuple(i)));
//                    }
//                    atomsToSave.add(new RelationalAtom(relAtomName1, tuple.getTuple()));
                }

                joinedTuples.add(tuplesToStore);
                joinedTerms.add(termsToStore);
                joinedRelationsNames.add(relationsNamesToStore);

            }

            // If the group has two relational atoms:
            else if (group.size() == 2) {

                // Create list for storing tuples
                List<Tuple> tuplesToStore = new ArrayList<>();

                // Run the join:
                RelationalAtom relationalAtom1 = group.get(0);
                RelationalAtom relationalAtom2 = group.get(1);

                // Name of each relational atom:
                String relAtomName1 = relationalAtom1.getName();
                String relAtomName2 = relationalAtom2.getName();

                // Join the two array of variables of the two atoms:
                List<Term> termsToStore = new ArrayList<>();
                termsToStore.addAll(relationalAtom1.getTerms());
                termsToStore.addAll(relationalAtom2.getTerms());

                // Join the two array of relations names of the two atoms:
                List<String> relationsNamesToStore = new ArrayList<>();
                relationsNamesToStore.add(relAtomName1);
                relationsNamesToStore.add(relAtomName2);

                // Get the comparison atoms that are between the first atom
                // Between atoms have the type: "between atoms", "implicit"
                List<ComparisonAtom> comparisonAtomsBetweenAtom1 = getComparisonAtomsBetweenAtom(relAtomName1, comparisonAtoms);
                List<ComparisonAtom> comparisonAtomsBetweenAtom2 = getComparisonAtomsBetweenAtom(relAtomName2, comparisonAtoms);

                // Print both:
                System.out.println("Comparison atoms between atom 1: " + comparisonAtomsBetweenAtom1);
                System.out.println("Comparison atoms between atom 2: " + comparisonAtomsBetweenAtom2);

                // Create selection operator for the first atom:
                SelectionOperator selectionOp1 = new SelectionOperator(comparisonAtomsBetweenAtom1, relationalAtom1.getName());

                // Create selection operator for the second atom:
                SelectionOperator selectionOp2 = new SelectionOperator(comparisonAtomsBetweenAtom2, relationalAtom2.getName());


                // Dump the selection operators:
//                selectionOp1.dump();
//                selectionOp2.dump();

                // Find the join conditions between the two atoms:
                List<ComparisonAtom> joinConditions = findJoinCondition(relAtomName1, relAtomName2, comparisonAtoms);

                // Print the join conditions:
                System.out.println("Join conditions: " + joinConditions);

                // Iterate over all the tuples in the first selection operator:
                while (true) {
                    System.out.println("Selection operator 1 has next");
                    // Get the next tuple:
                    Tuple tuple1 = selectionOp1.getNextTuple();

                    // If tuple1 is null, no more tuples in the selection operator:
                    if (tuple1 == null) {
                        System.out.println("Tuple 1 is null. No more tuples in selection operator 1");
                        break;
                    }

                    // Iterate over all the tuples in the second selection operator:
                    while (true) {
                        // Get the next tuple:
                        Tuple tuple2 = selectionOp2.getNextTuple();

                        // If tuple2 is null, no more tuples in the selection operator:
                        if (tuple2 == null) {
                            System.out.println("Tuple 2 is null. No more tuples in selection operator 2");
                            System.out.println("Reseting selection operator 2 for next tuple 1");
                            selectionOp2.reset();
                            break;
                        }

                        // Print the tuples:
                        System.out.println("Tuple 1: " + tuple1);
                        System.out.println("Tuple 2: " + tuple2);

                        if (checkTuples(tuple1, tuple2, joinConditions)) {
                            // Create a new tuple that will be the result of the join:
                            // Creat list of terms:
                            List<Term> terms = new ArrayList<>();

                            // Add the terms from the first tuple:
                            // Iterate over the terms in the tuple:
                            for (Term term : tuple1.getTuple()) {
                                // Add the term to the list of terms:
                                terms.add(term);
                            }

                            // Add the terms from the second tuple:
                            // Iterate over the terms in the tuple:
                            for (Term term : tuple2.getTuple()) {
                                // Add the term to the list of terms:
                                terms.add(term);
                            }

                            // Create the join tuple:
                            Tuple joinTuple = new Tuple(terms.toArray(new Term[0]));

                            // Print the join tuple:
                            System.out.println("Join tuple: " + joinTuple);

                            // Add the join tuple to the list of tuples:
                            tuplesToStore.add(joinTuple);
                        }




                    }

                    // Create new map

                    // Reset the second selection operator:
//                    selectionOp2.reset();

                }

                // Create join operator:
//                JoinOperator joinOp = new JoinOperator(selectionOp1, selectionOp2, relationalAtom1.getName(), relationalAtom2.getName());


                // Add the tuples to the list of joined tuples:
                joinedTuples.add(tuplesToStore);
                joinedTerms.add(termsToStore);
                joinedRelationsNames.add(relationsNamesToStore);





            }


        }

        //
//        return joinedTuples;
        // Print the joined tuples:
        System.out.println("Joined tuples: " + joinedTuples);
        System.out.println("Joined terms: " + joinedTerms);
        System.out.println("Joined relations names: " + joinedRelationsNames);

        // New join will be between the right of the first join and the left of the second join (or the only one in second join)


        String relNameRight = joinedRelationsNames.get(0).get(1);
        String relNameLeft = joinedRelationsNames.get(1).get(0);

        // Find the join conditions between the two atoms:
        List<ComparisonAtom> newJoinConditions = findJoinCondition(relNameRight, relNameLeft, comparisonAtoms);

        // Convert to string a

        // Print the new join conditions:
        System.out.println("New join conditions: " + newJoinConditions);

        // Update positions of the terms in the new join conditions:
        // Iterate over the new join conditions:
        for (ComparisonAtom comparisonAtom : newJoinConditions) {
            // Get the left and right terms:
            Term leftTerm = comparisonAtom.getTerm1();
            Term rightTerm = comparisonAtom.getTerm2();

            // Get the position of the left term:
            int leftTermPosition = joinedTerms.get(0).indexOf(leftTerm);

            // Get the position of the right term:
            int rightTermPosition = joinedTerms.get(1).indexOf(rightTerm);

            // List with new indexes:
            List<Integer> newIndexes = new ArrayList<>();
            newIndexes.add(leftTermPosition);
            newIndexes.add(rightTermPosition);

            // Set the new indexes:
            comparisonAtom.setIndexes(newIndexes);

            // Print the comparison atom:
            System.out.println("Comparison atom: " + comparisonAtom);
            // Print the indexes:
            System.out.println("Indexes: " + comparisonAtom.getIndexes());

        }

        // Iterate over the joined tuples and find the tuples that satisfy the new join conditions:
        // Iterate over the joined tuples:
        List<Tuple> tuplesLeft = joinedTuples.get(0);
        List<Tuple> tuplesRight = joinedTuples.get(1);

        // Tuplestostore:
        List<Tuple> tuplesToStore = new ArrayList<>();

        // Iterate over the tuples in the left:
        for (Tuple tupleLeft : tuplesLeft) {
            // Iterate over the tuples in the right:
            for (Tuple tupleRight : tuplesRight) {
                // Check if the tuples satisfy the new join conditions:
                if (checkTuples(tupleLeft, tupleRight, newJoinConditions)) {
                    // Create a new tuple that will be the result of the join:
                    // Creat list of terms:
                    List<Term> terms = new ArrayList<>();

                    // Add the terms from the first tuple:
                    // Iterate over the terms in the tuple:
                    for (Term term : tupleLeft.getTuple()) {
                        // Add the term to the list of terms:
                        terms.add(term);
                    }

                    // Add the terms from the second tuple:
                    // Iterate over the terms in the tuple:
                    for (Term term : tupleRight.getTuple()) {
                        // Add the term to the list of terms:
                        terms.add(term);
                    }

                    // Create the join tuple:
                    Tuple joinTuple = new Tuple(terms.toArray(new Term[0]));

                    // Print the join tuple:
                    System.out.println("Join tuple: " + joinTuple);

                    // Store the join tuple:
                    tuplesToStore.add(joinTuple);
                }
            }
        }
        // Iterate over

        // Print the tuples to store:
        System.out.println("Tuples to store: " + tuplesToStore);


    }


    public static boolean checkTuples(Tuple tupleLeft, Tuple tupleRight, List<ComparisonAtom> newJoinConditions){
        //        List to save all the results of the evaluation of the comparison atoms:
        List<Boolean> results = new ArrayList<>();

//        Start counter:
        int i = 0;

        // Iterate over the new join conditions:
        for (ComparisonAtom comparisonAtom : newJoinConditions) {

            // Get the position of the left term:
            int leftTermPosition = comparisonAtom.getIndexes().get(0);

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





    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) throws IOException {
        // TODO: add your implementation

        try {
            Query query = QueryParser.parse(Paths.get(inputFile));

//            Print query:
            System.out.println("Query parsed: " + query);

            //  Get type of query:
            String queryType = getQueryType(query);
            System.out.println("Query type: " + queryType);

            if (queryType.equals("scan")) {
                runScan(query);
            }

            else if (queryType.equals("projection")) {
                runProjection(query);
            }

            else if (queryType.equals("selection")) {
                runSelection(query);

            }

            else if (queryType.equals("selection_projection")) {
                runSelectionProjection(query);

            }

            else if (queryType.equals("join")) {
                List<ComparisonAtom> allComparisonAtoms =  analyzeConditions(query);

                // If there are no comparison atoms, then it's a cross product:
                if (allComparisonAtoms.isEmpty()) {
                    // runCrossProduct(query);
                }

                else {
                    // Get the join groups:
                    List<List<RelationalAtom>> joinGroups = getJoinGroups(query);

                    // Run the joins:
                    runJoins(joinGroups, allComparisonAtoms);


                }

            }


            else {
                System.out.println("Query type not recognized");
                analyzeConditions(query);
            }


//
////            Get list of relational atoms from query
//            List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);
//
////            Iterate over each relational atom and get the relation name
//            for (RelationalAtom relationalAtom : relationalAtoms) {
//                String relationName = relationalAtom.getName();
//                System.out.println("Current relation name: " + relationName);
//
//                //        Create SelectionOperator object, given the query and the relation name
//                SelectOperator selectionOperator = new SelectOperator(query, relationName);
//
//                System.out.println("Selection operator: " + selectionOperator);
//
////                selectionOperator.getNextTuple();
//
//
////              Testing the projection operator:
//                ProjectOperator projectionOperator = new ProjectOperator(query, relationName);
//                System.out.println("Projection operator: " + projectionOperator);
//
////////                Iterate 8 times:
////                for (int i = 0; i < 8; i++) {
////                    Tuple tuple = selectionOperator.getNextTuple();
////                    System.out.println("TupleResult: " + tuple);
////                }
//
//            }




        }

         catch (IOException e) {
            throw new RuntimeException(e);
        }








////        Print scanOperator relationName, relationPath and reader
//            System.out.println(scanOperator.getRelationName());
//            System.out.println(scanOperator.getRelationPath());
//            System.out.println(scanOperator.getReader());
//
//            //        Try to use the get next tuple function:
//            Tuple tupleHere = scanOperator.getNextTuple();
//            System.out.println("tupleHere: " + tupleHere);
//            Tuple tuple2 = scanOperator.getNextTuple();
//            System.out.println(tuple2);
////       Reset the reader:
//            scanOperator.reset();
//
//            System.out.println("Reset the reader");
//            Tuple tuple3 = scanOperator.getNextTuple();
//            System.out.println(tuple3);
////        Dump operator:
////        Print dumping
//            System.out.println("Dumping");
//            scanOperator.dump();












//        Prinit tuple elements:
//        System.out.println(tuple.toList());

//        Create ProjectOperator object, given the scanOperator and the schema






    }

    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static void parsingExample(String filename) {
        try {
            Query query = QueryParser.parse(Paths.get(filename));
            // Query query = QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w), z < w");
            // Query query = QueryParser.parse("Q(SUM(x * 2 * x)) :- R(x, 'z'), S(4, z, w), 4 < 'test string' ");

            System.out.println("Entire query: " + query);
            Head head = query.getHead();
            System.out.println("Head: " + head);
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);
        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

}
