package DebugExercise;

/**
 * Exercise for learning how the debug, breakpoint, and step-into
 * feature work.
 */
public class DebugExercise1 {
    public static int divideThenRound(int top, int bottom) {
        if(bottom == 0){
            return 0;
        }
        double quotient = (double) top / bottom;
        int result = (int) Math.round(quotient);
        return result;
    }

    public static void main(String[] args) {
        int t = (int) (Math.random() * 10);
        int b = (int) (Math.random() * 100);
        int result = divideThenRound(t, b);
        System.out.println("round(" + t + "/" + b + ")=" + result);

        int t2 = (int) (Math.random() * 100);
        int b2 = (int) (Math.random() * 100);
        int result2 = divideThenRound(t2, b2);
        System.out.println("round(" + t2 + "/" + b2 + ")=" + result2);

        int t3 = (int) (Math.random() * 100);
        int b3 = (int) (Math.random() * 10);
        int result3 = divideThenRound(t3, b3);
        System.out.println("round(" + t3 + "/" + b3 + ")=" + result3);
    }
}
