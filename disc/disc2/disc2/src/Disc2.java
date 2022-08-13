public class Disc2 {
    public static int fib(int n) {
        if (n == 0 || n == 1){
            return n;
        }
        else{
            return fib(n - 1) + fib(n - 2);
        }
    }

    public static int fib2(int n, int f0, int f1){
        if(n == 0){
            return f0;
        }
        else{
            return fib2(n - 1, f1, f0 + f1);
        }
    }

    public static void main(String[] args) {
        for(int i = 0; i < 10; i++){
            System.out.printf(""+fib(i)+"\n");
        }
        for(int i = 0; i < 10; i++){
            System.out.printf(""+fib2(i, 0, 1)+"\n");
        }
    }
}
