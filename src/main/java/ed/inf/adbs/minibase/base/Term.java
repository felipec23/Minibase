package ed.inf.adbs.minibase.base;

//public class Term {
//
//
////    public Term evaluate(Tuple tuple) {
////        return tuple;
////    }
//}
public class Term {

//    Create a function to check if two constants are equal:
    public boolean equals(Term right) {
        System.out.println("From Term: " + this.toString());
        if (this.toString().equals(right.toString())) {
            return true;
        }
        return false;
    }

//    public Term evaluate(Tuple tuple) {
//        return tuple;
//    }
}


