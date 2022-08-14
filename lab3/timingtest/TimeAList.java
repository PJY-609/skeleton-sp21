package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList<>();
        AList<Integer> opCounts = new AList<>();

        for(int i = 0; i < 8; i++){
            Ns.addLast(1000 * (int) Math.pow(2, i));
            opCounts.addLast(1000 * (int) Math.pow(2, i));
        }

        AList<Double> times = new AList<>();

        for(int i = 0; i < Ns.size(); i++){
            int N = Ns.get(i);
            Stopwatch sw = new Stopwatch();
            AList<Integer> aList = new AList<>();
            for(int n = 0; n < N; n++){
                aList.addLast(0);
            }
            times.addLast(sw.elapsedTime());
        }

        printTimingTable(Ns, times, opCounts);
    }
}
