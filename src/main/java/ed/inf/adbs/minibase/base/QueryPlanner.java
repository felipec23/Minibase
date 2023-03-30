package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.parser.QueryParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class plans the query and adds the needed operators, according to the specifications
 * of the query. In this class we also find the different conditions, both for selection or for
 * joining. The root variables is assigned as long as the query is being checked.
 */
public class QueryPlanner {

    private Operator root = null;

    private Query query = null;

    private List<String> relTermsToDelete;

    private Query originalQuery;


    public Operator plan(Query originalQuery) {

        this.originalQuery = originalQuery;
        this.relTermsToDelete = getDeletableRelTerms(originalQuery);
        this.query = getSimpleQuery(originalQuery);

        // Array to save all join terms:
        List<Term> processedTerms = new ArrayList<>();

        // Boolean to check if all head terms are variables:
        boolean allHeadTermsAreVariables = true;

        // Array to save all head variables:
        List<String> headVariables = new ArrayList<>();
        for (Term term : originalQuery.getHead().getVariables()) {
            headVariables.add(term.toString());
            if (!(term instanceof Variable)) {
                allHeadTermsAreVariables = false;
            }
        }

        // Get the relational atoms from the query:
        List<RelationalAtom> relationalAtoms = getRelationalAtomsFromQuery(query);

        // Get all terms from the query:
        List<String> allTermsFromBody = getRelationalVariablesFromQuery(originalQuery);

        // Get first atom
        RelationalAtom relationalAtom1 = relationalAtoms.get(0);

        // Get the name of the first relational atom:
        String relAtomName1 = relationalAtom1.getName();

        // Get the terms of the first relational atom, add them:
        processedTerms.addAll(relationalAtom1.getTerms());

        // Generate scan operator for the first atom:
        ScanOperator scanOp1 = new ScanOperator(relAtomName1, relTermsToDelete);

        List<ComparisonAtom> allComparisonAtoms =  analyzeConditions(query);

        // Find if there's need for a selection operator
        List<ComparisonAtom> comparisonAtomsBetweenAtom1 = getComparisonAtomsBetweenAtom(relAtomName1, allComparisonAtoms);

        // If there are comparison atoms between the first atom, create selection operator:
        if (comparisonAtomsBetweenAtom1.size() > 0) {

            // Create selection operator for the first atom:
            SelectionOperator selectionOp1 = new SelectionOperator(scanOp1, comparisonAtomsBetweenAtom1, relAtomName1);
            // Set the selection operator as the root:
            root = selectionOp1;

        } else {

            // Set the scan operator as the root:
            root = scanOp1;
        }

        // Define join conditions and operator 2:
        List<ComparisonAtom> joinConditions = null;
        Operator op2 = null;

        // Iterate for the rest of the relational atoms:
        for (int i = 1; i < relationalAtoms.size(); i++) {

            // Get the relational atom:
            RelationalAtom relationalAtom2 = relationalAtoms.get(i);
            // Get the name of the relational atom:
            String relAtomName2 = relationalAtom2.getName();
            // Get the terms of the relational atom:
            List<Term> terms2 = relationalAtom2.getTerms();
            // Add the terms to the processed terms:
            processedTerms.addAll(terms2);
            // Generate scan operator for the second atom:
            ScanOperator scanOp2 = new ScanOperator(relAtomName2, relTermsToDelete);
            // Find if there's need for a selection operator
            List<ComparisonAtom> comparisonAtomsBetweenAtom2 = getComparisonAtomsBetweenAtom(relAtomName2, allComparisonAtoms);
            // If there are comparison atoms between the second atom, create selection operator:
            if (comparisonAtomsBetweenAtom2.size() > 0) {

                // Create selection operator for the second atom:
                SelectionOperator selectionOp2 = new SelectionOperator(scanOp2, comparisonAtomsBetweenAtom2, relAtomName2);
                // Set the selection operator as the second operator:
                op2 = selectionOp2;
            } else {
                // Set the scan operator as the second operator:
                op2 = scanOp2;
            }
            // Find the join conditions between the two atoms:
            joinConditions = findJoinCondition(relAtomName1, relAtomName2, allComparisonAtoms);

            // Make a copy of relAtomName2:

            // Create join operator:
            JoinOperator joinOp = new JoinOperator(root, op2, joinConditions);
            // Set the join operator as the root:
            root = joinOp;
            // Set the second atom as the first atom, for the next iteration:
            relAtomName1 = relAtomName2;


        }

        // Checking for any aggregation operation:
        SumAggregate sumAgg = query.getHead().getSumAggregate();

        if (sumAgg != null) {

            // Create aggregation operator:
            SumOperator aggregateOp = new SumOperator(root, sumAgg, query);
            // Set the aggregation operator as the root:
            root = aggregateOp;

            // Aggregation operator does its own projection, so no need to do it again:
            return root;
        }


        // Apply projection operator:
        if (headVariables.equals(allTermsFromBody)) {
            // System.out.println("All head variables are in the body, no need for projection.");
        } else {
            // System.out.println("Applying projection operator.");
            // Create projection operator:
            ProjectOperator projectionOp = new ProjectOperator(root, query);
            // Set the projection operator as the root:
            root = projectionOp;
        }




        // Return the root:
        return root;

    }

    /**
     * This method removes some terms from the query and returns the string parsed back into a Query object.
     * The idea here is to not use those variables that won't be used by any operator a long the process. This is
     * part of the query optimization for Task 3.
     * @param originalQuery
     * @return simpleQuery: returns a more simplified version of the query, given the terms that can be deleted list.
     */
    public Query getSimpleQuery(Query originalQuery){


        String queryString = originalQuery.toString();

        // Iterate over terms that can be removed:
        for (String term : this.relTermsToDelete) {
            // Remove the term from the query:
            queryString = queryString.replace(term + ", ", "");
            queryString = queryString.replace(", " + term, "");

        }

        Query query = QueryParser.parse(queryString);

        return query;

    }

    /**
     * This method finds out which terms can be avoided in order to optimize the query evaluation. First, we add
     * all the variables in the head, as well as the product terms. Then, from the body, we add the left term of
     * all the comparison atoms. We later add all constants that appear in the relational atoms, and store in a
     * HashMap how many times appears each term (in relational atoms). Those terms with 2 or more appearances
     * represent a join condition, so they can't be removed. At the end, the variables that might be removed
     * are the ones with a frequency of 1.
     * @param query receives the query object/parsed.
     * @return list of terms that might be deleted.
     */
    public List<String> getDeletableRelTerms(Query query) {


        // From the head:
        List<String> queryTerms = new ArrayList<>();
        SumAggregate sumAgg = query.getHead().getSumAggregate();

        if (sumAgg != null) {
            // Create list of product terms head
            List<Term> productTerms = sumAgg.getProductTerms();

            for (Term term : productTerms) {
                queryTerms.add(term.toString());
            }
        }

        // Add all the variables in the head to the list
        for (Variable variable : query.getHead().getVariables()) {
            queryTerms.add(variable.toString());
        }

        // From the body:

        // Create hashmap to store the frequency of each term
        HashMap<String, Integer> termFrequency = new HashMap<>();

        List<Atom> body = query.getBody();
        for (Atom atom : body) {
            if (atom instanceof ComparisonAtom) {
                ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
                Term term1 = comparisonAtom.getTerm1();
                queryTerms.add(term1.toString());

            } else if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                List<Term> terms = relationalAtom.getTerms();
                for (Term term : terms) {

                    // If term is IntegerConstant, add it to the list
                    if (term instanceof Constant) {
                        queryTerms.add(term.toString());
                    }

                    // If term is Variable, add it to the list
                    else if (term instanceof Variable) {
                        // Add to hashmap if it is not already present
                        if (!termFrequency.containsKey(term.toString())) {
                            termFrequency.put(term.toString(), 1);
                        }
                        // If it is already present, increment the frequency
                        else {
                            termFrequency.put(term.toString(), termFrequency.get(term.toString()) + 1);

                        }
                    }
                }
            }
        }

        // Add the terms with frequency greater than 1 to the list
        // If a term appears twice or more, it means that is a possible join condition
        List<String> termsDeletable = new ArrayList<>();
        for (String term : termFrequency.keySet()) {
            if (termFrequency.get(term) > 1) {
                queryTerms.add(term);
            }
            else {

                if (!queryTerms.contains(term)) {
                    termsDeletable.add(term);
                }
            }
        }

        // Delete duplicates
        List<String> queryTermsNoDuplicates = new ArrayList<>();
        for (String term : queryTerms) {
            if (!queryTermsNoDuplicates.contains(term)) {
                queryTermsNoDuplicates.add(term);
            }
        }


        return termsDeletable;

    }

    /**
     * Function to fetch the relational variables in a query.
     * @param query
     * @return list of relational variables, as string.
     */
    public static List<String> getRelationalVariablesFromQuery(Query query) {

        // Create list for saving the relational variables:
        List<String> relationalVariables = new ArrayList<>();

        // Iterate over the atoms in the query:
        for (Atom atom : query.getBody()) {
            // If the atom is relational:
            if (atom instanceof RelationalAtom) {
                // Get the terms of the relational atom:
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                List<Term> terms = relationalAtom.getTerms();
                for (Term term : terms) {
                    relationalVariables.add(term.toString());
                }
            }
        }

        // Return the list of relational variables:
        return relationalVariables;

    }

    /**
     * Here we take advantage of the comparison atoms types. We want to find the filters/selections that apply to
     * only one atom. For this, we iterate over each comparison atom and filter those called "between atom" (for example
     * y = 5), or "implicit" [x, 5, z].
     * @param relAtomName
     * @param allComparisonAtoms
     * @return list of comparison atoms that apply only to the given relational atom
     */
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

    /**
     * Now, we need to find the join conditions. For this purpose we also fetch two types of comparison atoms:
     * "equi-join" R[x, y], S[x, z] and "different atoms" R[x, y], S[u, v], x < v. These two represent join conditions.
     * @param relAtom1Name
     * @param relAtom2Name
     * @param comparisonAtoms
     * @return list of join conditions for a given pair of relational atoms
     */
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

    /**
     * Return the relational atoms in a query
     * @param query
     * @return list of relational atoms
     */

    public List<RelationalAtom> getRelationalAtomsFromQuery(Query query) {
        List<RelationalAtom> relationalAtoms = new ArrayList<>();
        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;

                relationalAtoms.add(relationalAtom);
            }
        }
        return relationalAtoms;
    }


    /**
     * This function creates all the possible comparisons that appear in a query. The idea is to have both
     * comparison and join conditions in one variable. For this, we call some of the previous functions defined before.
     * The idea is to get the implicit and equi-join conditions, too. After achieving this, we create a map that
     * tracks where each variable (x, y, z) appears (that is, in relation R, S...) and the index (0, for X, for the
     * previous example). With this information, we can now map where (in which relations) each comparison atom has influence.
     * @param query
     * @return list of all possible comparison atoms in the query.
     */
    public List<ComparisonAtom>  analyzeConditions(Query query){
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
        comparisonAtoms.addAll(relationalAtomsWithEquiJoinConditions);


        return comparisonAtoms;


    }

    /**
     * Return the index of a term inside a relational atom.
     * @param term
     * @param relationalAtom
     * @return index (position)
     */
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

    /**
     * Function to create a mapping of where each variable is present and the index it has on each relation.
     * @param relationalAtoms
     * @return map object.
     */
    public Map<String, Map<String, Integer>> createMap(List<RelationalAtom> relationalAtoms) {
        Map<String, Map<String, Integer>> myMap = new HashMap<>();

        // create a new inner map


        // Iterate over each relational atom:
        for (RelationalAtom relationalAtom : relationalAtoms) {
            // Iterate over each term of the relational atom:
            for (Term term : relationalAtom.getTerms()) {
                if (term instanceof Variable) {

                    // If term is in relTermsToDelete, then skip it:
                    if (relTermsToDelete.contains(term.toString())) {
                        System.out.println("Skipping: " + term + " .It is in relTermsToDelete");
                        continue;
                    }

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


    /**
     * Get all comparison atoms of a query.
     * @param query
     * @return list of comparison atoms.
     */
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

    /**
     * Functino for detecting implicit conditions in a query. We iterate over each term and check if it's
     * a Constant object. If it is, we create a new Comparison Atom with the equal operator. Then, we assign
     * the implicit type, as well as add the relation name/s where this specific filter is applying. We also
     * define the indexes of the atom. In the indexes we store the position of the comparison variable in the
     * actual tuple.
     * @param relationalAtoms
     * @return list of implicit comparison atoms.
     */
    public static List<ComparisonAtom> detectImplicitConditions(List<RelationalAtom> relationalAtoms ) {

        // Detect implicit conditions, that is, if there is a relational atom with a constant:
        // Save the relational atoms with constants in a list:
        List<ComparisonAtom> newComparisonAtoms = new ArrayList<>();
        for (RelationalAtom relationalAtom : relationalAtoms) {
            // Create index for keeping track of the position of the constant:
            int index = 0;
            for (Term term : relationalAtom.getTerms()) {
                if (term instanceof Constant ) {
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


    /**
     * This function detectes equi-join conditions. We iterate over each term of all the comparison atoms in order
     * to find common terms between different atoms. When we find them, we also create a new comparison atom, and
     * as we did before, we also store the relation name/s and the indexes.
     * @param relationalAtoms
     * @return list of all equi-join conditions.
     */

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

}
