package info.kgeorgiy.ja.podkorytov.arrayset;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> lst = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            lst.add(i);
        }
        ArraySet<Integer> set = new ArraySet<>(lst);
        System.out.println(set.ceiling(6));
    }
}
