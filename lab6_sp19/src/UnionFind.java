import java.util.Arrays;
import java.util.NoSuchElementException;

public class UnionFind {

    private int[] V;

    /* Creates a UnionFind data structure holding n vertices. Initially, all
       vertices are in disjoint sets. */
    public UnionFind(int n) {
        V = new int[n];
        Arrays.fill(V, -1);
    }

    /* Throws an exception if v1 is not a valid index. */
    private void validate(int vertex) {
        if(vertex < 0 || V.length <= vertex){
            throw new NoSuchElementException();
        }
    }

    /* Returns the size of the set v1 belongs to. */
    public int sizeOf(int v1) {
        validate(v1);
        int root = find(v1);
        return -V[root];
    }

    /* Returns the parent of v1. If v1 is the root of a tree, returns the
       negative size of the tree for which v1 is the root. */
    public int parent(int v1) {
        validate(v1);
        return V[v1];
    }

    /* Returns true if nodes v1 and v2 are connected. */
    public boolean connected(int v1, int v2) {
        validate(v1);
        validate(v2);

        int root1 = find(v1);
        int root2 = find(v2);

        return root1 == root2;
    }

    /* Connects two elements v1 and v2 together. v1 and v2 can be any valid
       elements, and a union-by-size heuristic is used. If the sizes of the sets
       are equal, tie break by connecting v1's root to v2's root. Unioning a
       vertex with itself or vertices that are already connected should not
       change the sets but may alter the internal structure of the data. */
    public void union(int v1, int v2) {
        validate(v1);
        validate(v2);

        int root1 = find(v1);
        int root2 = find(v2);

        if(V[root1] > V[root2]){
            int size = V[root1];
            V[root1] = root2;
            V[root2] += size;
        }
        else{
            int size = V[root2];
            V[root2] = root1;
            V[root1] += size;
        }
    }

    /* Returns the root of the set V belongs to. Path-compression is employed
       allowing for fast search-time. */
    public int find(int vertex) {
        validate(vertex);

        int v = vertex;
        while(V[v] > -1){
            v = V[v];
        }

        if(V[vertex] > -1){
            V[vertex] = v;
        }

        return v;
    }

    public static void main(String[] args){
        UnionFind uf = new UnionFind(10);

        System.out.println(uf.find(9));
        System.out.println(uf.find(4));

        uf.union(1, 2);
        System.out.println(uf.connected(1, 2));
        System.out.println(uf.connected(1, 3));

        uf.union(3, 4);
        uf.union(3, 5);
        uf.union(4, 6);
        uf.union(6, 7);
        System.out.println(uf.connected(3, 7));

        uf.union(2, 5);
        System.out.println(uf.connected(1, 7));
        System.out.println(uf.parent(1));
        System.out.println(uf.parent(2));

        System.out.println(uf.find(2));
        System.out.println(uf.parent(2));
    }
}
