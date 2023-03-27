package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.Utils;

import java.util.ArrayList;
import java.util.List;

public class RelationalAtom extends Atom {
    private String name;

    private List<Term> terms;

    public RelationalAtom(String name, List<Term> terms) {
        this.name = name;
        this.terms = terms;
    }

    public String getName() {
        return name;
    }

    public List<Term> getTerms() {
        return terms;
    }

//    Get terms as list of strings:
    public List<String> getTermsAsString() {

//        Without using map:
        List<String> termsAsString = new ArrayList<>();
        for (Term term : terms) {
            termsAsString.add(term.toString());
        }
        return termsAsString;
    }

    @Override
    public String toString() {
        return name + "(" + Utils.join(terms, ", ") + ")";
    }
}
