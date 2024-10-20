package proxy;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        MyList list = new ListProvider().getList();

        list.add(1);
        list.add(2);
        list.remove(0);
        list.addAll(List.of(3, 4, 5));

        System.out.println(list);

    }

}
