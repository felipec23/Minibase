package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;



//public class QueryPlanner {
public class Generator {

    private static Head head;
    private static List<Atom> body;

    private static List<RelationalAtom> relations;

    private static List<ComparisonAtom> conditions;

    private List<Term> relation_variables;
    private Operator root = null;
    private List<Variable> free_variables_temp;

    private List<Term> free_variables;
//    public class  Operator planQuery(String queryPath) {
    public Generator(Query query) {
//        // Parse the query:
//        Query query = null;
//        try {
//            query = QueryParser.parse(Paths.get(queryPath));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        relations = new ArrayList<>();
        conditions = new ArrayList<>();

        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                relations.add(relationalAtom);
            } else if (atom instanceof ComparisonAtom) {
                ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
                conditions.add(comparisonAtom);

            }

        }

        // initialization of fields
        head = query.getHead();
        body = query.getBody();

        // Get variables in head
        free_variables_temp = head.getVariables();
        // read free variables and convert to list of terms
        List<Term> free_variables = new ArrayList<>();
        for (Variable variable : free_variables_temp) {
            free_variables.add(variable);
        }


        relation_variables = new ArrayList<>();
        for (RelationalAtom ra : relations) {
            List<Term> terms = ra.getTerms();
            for (Term term : terms) {
                if (!relation_variables.contains(term)) {
                    relation_variables.add(term);
                }
            }
        }

        checkImplicitSelectionConds();

        if(relations.size() > 1){
            for(int i = 0; i< relations.size()-1;i++){
                for(int j=i+1; j< relations.size();j++){
                    checkEquiJoin(relations.get(i),relations.get(j));
                }
            }
        }


    }

//
//    public Operator generateQueryPlan(){
//        // firstly, get the first relation and create a scan operator for it
//        List<Term> joined_terms = new ArrayList<>();
//        RelationalAtom ra = relations.get(0);
//        joined_terms.addAll(ra.getTerms());
//        root = new ScanOperator(ra.getName());
//        // then find that whether it is involved in selection operator
//        // if so create a selection operator
//        List<ComparisonAtom> involvedSelectionConds = findSelectionConds(ra);
//        if(involvedSelectionConds != null){
//            root = new SelectionOperator(involvedSelectionConds, ra.getName());
//        }
//        List<ComparisonAtom> joinConds = null;
//        Operator op2 = null;
//
//        // for the rest relations, repeat the procedure of create scan operator
//        // check whether involved in selection
//        for(int i=1; i< relations.size();i++){
//            RelationalAtom ra2 = relations.get(i);
////            op2 = new ScanOperator(ra2);
//            involvedSelectionConds = findSelectionConds(ra2);
//            if(involvedSelectionConds != null){
//                op2 = new SelectionOperator(involvedSelectionConds, ra2.getName());
//            }
//            // use the currently joined terms and the terms of the focused relation
//            // to check whether they are involved in a join
//            joinConds = findJoinConds(joined_terms,ra2.getTerms());
//            root = new JoinOperator(root,op2,joinConds);
//            joined_terms.addAll(ra2.getTerms());
//        }
//
////        // check whether the head contains an aggregation operation
////        // if so create the corresponding operator
////        if(head.getAggregateVariable() != null){
////            if(head.getAggregateVariable().getType().equals("AVG")){
////                root = new AvgOperator(root,head.getAggregateVariable(),head.getTerms());
////            }else{
////                root = new SumOperator(root,head.getAggregateVariable(),head.getTerms());
////            }
////            return root;
////        }
////
////        // if no aggregation operation and the free variables are not equal to
////        // the relation variables of the body, do the projection
////        if(!free_variables.equals(relation_variables)){
////            root = new ProjectOperator(free_variables,root);
////        }
//
////        root = new ProjectOperator(free_variables,root);
//
//        return root;
//    }





    /**
     * This method is used to find all the involved conditions for any given
     * relational atom. It calls the containedIn method of ComparisonAtom.
     * @see ed.inf.adbs.minibase.base.ComparisonAtom#containedIn(List)
     * @param ra the relation atom that needs to check.
     * @return A list of conditions that this relational atom involves in.
     */
    public List<ComparisonAtom> findSelectionConds(RelationalAtom ra){
        List<ComparisonAtom> invlovedConds = new ArrayList<>();
        List<Term> raTerms = ra.getTerms();
        // If all the terms in the comparison atom are contained
        // in the relational atom's terms, it means that
        // this relational atom is involved in this condition
        for ( ComparisonAtom ca:conditions) {
            if(ca.containedIn(raTerms)){
                invlovedConds.add(ca);
            }
        }
        if(invlovedConds.size()>0){
            return invlovedConds;
        }
        return null;
    }

    /**
     * This method is used to find all the involved join conditions for any given two lists of terms.
     * The strategy here is quite simple, any conditions that are involved in this two lists of
     * terms must be the subset of their union. For instance, if we try to join R(x,y,z),S(xx,u,w),x=xx,x>4,z='adbs'
     * Only x = xx, are contained in {x,y,z,xx,u,w}, thus it is the join-conds to be returned.
     * @param terms1 A list that records currently joined terms.
     * @param terms2 Terms of the relational atom to be joined.
     */
    public List<ComparisonAtom> findJoinConds(List<Term> terms1, List<Term> terms2){
        List<ComparisonAtom> invlovedConds = new ArrayList<>();
        List<Term> unitonTerms = new ArrayList<>();
        unitonTerms.addAll(terms1);
        unitonTerms.addAll(terms2);

        for ( ComparisonAtom ca:conditions) {
            if(ca.containedIn(unitonTerms)){
                invlovedConds.add(ca);
            }
        }
        if(invlovedConds.size()>0){
            return invlovedConds;
        }
        return null;
    }

    private void checkImplicitSelectionConds(){
        for(RelationalAtom ra: relations){
            if(hasConstant(ra)){
                List<Term> old_terms = ra.getTerms();
                List<Term> new_terms = new ArrayList<>();
                for (Term term: old_terms) {
                    if(term instanceof Variable){
                        new_terms.add(term);
                    }else{
                        String new_variable_name = generateNewVariableName();
                        // make sure that the generated name do no crash with
                        // the existing variable name
                        while(relation_variables.contains(new_variable_name)){
                            new_variable_name = generateNewVariableName();
                        }
                        Variable replacement = new Variable(new_variable_name);
                        relation_variables.set(relation_variables.indexOf(term),replacement);
                        new_terms.add(replacement);
                        ComparisonAtom additional_cond = new ComparisonAtom(replacement,term,ComparisonOperator.EQ);
                        conditions.add(additional_cond);
                    }
                }
                RelationalAtom new_ra = new RelationalAtom(ra.getName(),new_terms);
                relations.set(relations.indexOf(ra),new_ra);
            }
        }
    }



    private boolean hasConstant(RelationalAtom ra) {
        List<Term> terms = ra.getTerms();
        for (Term term : terms) {
            if (term instanceof Constant) {
                return true;
            }
        }
        return false;

    }

    private void checkEquiJoin(RelationalAtom ra1, RelationalAtom ra2) {
        List<Term> terms1 = ra1.getTerms();
        List<Term> terms2 = ra2.getTerms();

        for(int i=0; i<terms1.size(); i++){
            for(int j=0; j< terms2.size();j++){
                if(terms1.get(i).equals(terms2.get(j))){
                    Variable variable = (Variable) (terms2.get(j));
                    // if we find same variables in this two atoms, rename one of them with double its name
                    Variable new_variable = new Variable(variable.getName()+variable.getName());
                    terms2.set(j,new_variable);

                    for(int k =0; k <conditions.size(); k++){
                        ComparisonAtom ca = conditions.get(k);
                        Term left_term = ca.getTerm1();
                        Term right_term = ca.getTerm2();
                        if(left_term.equals(variable)){
                            left_term = new_variable;
                        }
                        if (right_term.equals(variable)){
                            right_term = new_variable;
                        }
                        ComparisonAtom new_ca = new ComparisonAtom(left_term,right_term,ca.getOp());
                        if (!conditions.contains(new_ca)){
                            conditions.add(new_ca);
                        }
                    }
                    conditions.add(new ComparisonAtom(variable,new_variable,ComparisonOperator.EQ));
                    relation_variables.add(new_variable);
                }
            }
        }
    }

    public String generateNewVariableName(){
        String alphabetsLowerCase = "abcdefghijklmnopqrstuvwxyz";
        StringBuffer stringBuffer = new StringBuffer();
        // generate a random number between 0 and length of characters set
        int randomIndex = (int)(Math.random() * alphabetsLowerCase.length());
        stringBuffer.append(alphabetsLowerCase.charAt(randomIndex));
        randomIndex = (int)(Math.random() * alphabetsLowerCase.length());
        stringBuffer.append(alphabetsLowerCase.charAt(randomIndex));
        return new String(stringBuffer);
    }

}
