public class Puzzle {
    public static class A {
        int fish(A other) {
            return 1;
        }

        int fish(B other) {
            return 2;
        }

    }

    public static class B extends A {
        @Override
        int fish(B other) {
            return 3;
        }
    }

    public static void main(String[] args) {
        A y = new B();
        B z = new B();

        int a = y.fish(y); // 1
        int b = z.fish(y); // 1

        int c = y.fish(z); // 3
        int d = z.fish(z); // 3

        System.out.println(a + " " + b + " " + c + " " + d);
    }
}
