package ed.inf.adbs.minibase.base;

public class Term {


    //    Create a function to check if two constants are equal:
    public boolean equals(Term right) {

        if (this.toString().equals(right.toString())) {

            return true;
        }
        else{

            return false;
        }

    }

}


