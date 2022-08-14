public class EvenOdd {
    public static class IntList {
        public int first;
        public IntList rest;

        public IntList (int f, IntList r) {
            this.first = f;
            this.rest = r;
        }

        public static void evenOdd(IntList lst) {
            if (lst == null) {
                return;
            }
            IntList firstHalf = lst;
            IntList odd = lst.rest;
            while (odd != null && odd.rest != null) {
                IntList even = odd.rest;
                odd.rest = even.rest;
                even.rest = firstHalf.rest;
                firstHalf.rest = even;
                firstHalf = firstHalf.rest;
                odd = odd.rest;
            }
        }
    }

    public static void main(String[] args){
        IntList lst = new IntList(0, new IntList(3, new IntList(1, new IntList(4, new IntList(2, new IntList(5, null))))));

        IntList.evenOdd(lst);

        IntList l = lst;
        while(l != null){
            System.out.print(l.first + " ");
            l = l.rest;
        }

        lst = new IntList(0, new IntList(3, new IntList(1, new IntList(4, new IntList(2, null)))));

        IntList.evenOdd(lst);

        l = lst;
        while(l != null){
            System.out.print(l.first + " ");
            l = l.rest;
        }
    }
}
