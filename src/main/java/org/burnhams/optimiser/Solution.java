package org.burnhams.optimiser;

import java.lang.reflect.Array;
import java.util.*;

public class Solution<T> implements Iterable<T>, Cloneable {

    private final T[] solution;
    private final List<T> listView;

    protected boolean hasChanged = true;

    public Solution(Solution<T> other) {
        this(Arrays.copyOf(other.solution, other.size()));
    }

    public Solution(Collection<T> items) {
        solution = items.toArray((T[])Array.newInstance(items.iterator().next().getClass(), items.size()));
        listView = Arrays.asList(solution);
    }

    @SafeVarargs
    public Solution(T... items) {
        solution = items;
        listView = Arrays.asList(solution);
    }

    public void shuffle() {
        Random rnd = new Random();
        for (int i = solution.length - 1; i >= 0; i--)
        {
            swap(rnd.nextInt(i + 1), i);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return listView.iterator();
    }

    public int size() {
        return solution.length;
    }

    public T get(int index) {
        return solution[index];
    }

    public List<T> getList() {
        return new ArrayList<>(listView);
    }

    public boolean swap(int index1, int index2) {
        T s = solution[index1];
        if (s.equals(solution[index2])) {
            return false;
        } else {
            solution[index1] = solution[index2];
            solution[index2] = s;
            hasChanged = true;
            return true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Solution solution1 = (Solution) o;

        if (!Arrays.equals(solution, solution1.solution)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(solution);
    }

    @Override
    public Solution<T> clone() {
        return new Solution<T>(this);
    }

    @Override
    public String toString() {
        return "Solution{" +
                "solution=" + Arrays.toString(solution) +
                '}';
    }
}
