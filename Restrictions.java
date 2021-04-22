public class Restrictions{

    String lhs;
    String operator;
    String rhs;
    int group;
    boolean passed;

    public Restrictions(String x, String y, String z, int a){

        lhs=x;
        operator=y;
        rhs=z;
        group=a;
        passed=false;
    }

    // prints contents of restriction, for testing
    public void printRestriction() {
        System.out.println("The restriction is: " + lhs + " " + operator + " " + rhs + " " + group);
    }

}
