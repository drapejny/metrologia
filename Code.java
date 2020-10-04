import java.util.Scanner;

public class Main {
    public static void main(String args[]) {
        Scanner scan = new Scanner(System.in);
        int j, k, l, n, m, max = 0;
        boolean bool1, bool2 = true;
        do {                                                          //проверка на ввод
            System.out.println("Введите натуральное число n");        //натуральных n и m
            while (!scan.hasNextInt()) {
                System.out.println("n должно быть типа int");
                scan.next();
            }
            n = scan.nextInt();
            System.out.println("Введите натуральное число m");
            while (!scan.hasNextInt()) {
                System.out.println("m должно быть типа int");
                scan.next();
            }
            m = scan.nextInt();
            if (n <= 0 || m <= 0)
                System.out.println("Числа n и m должны быть положительными");
        } while (n <= 0 || m <= 0);
        int arr[][] = new int[n + 2][m + 2];
        /*int arr[][] = { {2, 3, 4},               //ввод матрицы
                          {-3, 5, 7},
                          {3, -5, 9} };
         */
        for (int i = 1; i <= n; i++) {
            for (j = 1; j <= m; j++) {
                while (!scan.hasNextInt())         // ввод матрицы с клавиатуры
                    scan.next();
                arr[i][j] = scan.nextInt();
            }
            System.out.println();
        }
        for (int i = 1; i <= n; i++) {              //вывод матрицы на экран
            for (j = 1; j <= m; j++) {
                System.out.printf("%4s", arr[i][j]);
            }
            System.out.println();
        }
        System.out.println();
        for (int i = 1; i <= n; i++)
            for (j = 1; j <= m; j++) {
                bool1 = true;
                for (k = i - 1; k <= i + 1; k++)
                    for (l = j - 1; l <= j + 1; l++)
                        if (k >= 1 && k <= n && l >= 1 && l <= m && (k != i || l != j))
                            if (arr[i][j] >= arr[k][l])
                                bool1 = false;

                if (bool1) {
                    System.out.print(arr[i][j] + " ");
                    if (bool2) {
                        max = arr[i][j];
                        bool2 = false;
                    }
                    if (arr[i][j] >= max)
                        max = arr[i][j];
                }
            }
        System.out.println();
        System.out.println("max = " + max);


        scan = new Scanner(System.in);
        String line = new String();
        boolean flag;
        do {
            flag = true;
            line = scan.nextLine();
            for (int i = 0; i < line.length(); i++) {
                int ch = line.charAt(i);
                if (!(ch > 96 && ch < 123 || ch > 64 && ch < 91) && ch != 32) {
                    flag = false;
                    System.out.println("Вы ввели недопустимые символы, повторите ввод");
                    break;
                }
            }
        } while (flag == false);
        String alfavit = "abcdefghijklmnopqrstuvwxyz";
        String s1 = new String();
        String s2 = new String();
        line = line.trim();
        String s[] = line.split("\\s+");
        for (int i = 0; i < s.length; i++)
            if (!s[i].contentEquals(s[s.length - 1]) && alfavit.indexOf(s[i].toLowerCase()) == 0)
                s1 += s[i] + " ";
        for (int i = 0; i < s.length; i++)
            if (!s[i].contentEquals(s[s.length - 1]))
                s2 += s[i].charAt(s[i].length() - 1) + s[i].substring(0, s[i].length() - 1) + " ";
        if (s[0].length() == 0)
            System.out.println("Вы ввели только пробелы, либо пустую строку");
        else {
            if (s1.length() != 0)
                System.out.println(s1);
            else System.out.println("1. Таких слов нет");
            if (s2.length() != 0)
                System.out.println(s2);
            else System.out.println("2. Таких слов нет");
        }


         scan = new Scanner(System.in);
        System.out.println("Введите многочлен P");
        List listP = new List(enterSize());
        enterList(listP);
        System.out.println("Введите многочлен Q");
        List listQ = new List(enterSize());
        enterList(listQ);
        listP.print();
        System.out.println();
        listQ.print();
        System.out.println();
        System.out.println("Equality(P,Q):" + equality(listP, listQ));
        System.out.println("\nMeaning(P,X):");
        System.out.println("Введите значение X");
        while (!scan.hasNextInt())
            scan.nextInt();
        int x = scan.nextInt();
        listP.print();
        System.out.printf("= %d , при x = %d\n\n", meaning(listP, x), x);
        System.out.println("Add(P,Q,R):");
        listP.print();
        System.out.println("\n+");
        listQ.print();
        System.out.println("\n=");
        add(listP, listQ).print();

    }

    public static List add(List list_1, List list_2) {
        list_1.reset();
        list_2.reset();
        Node current_1 = list_1.getCurrent();
        Node current_2 = list_2.getCurrent();
        List list_3 = new List(list_1.getSize() >= list_2.getSize() ? list_1.getSize() : list_2.getSize());
        while (current_1 != null || current_2 != null) {
            if (current_1 == null) {
                list_3.add(new Node(current_2.getSt(), current_2.getCof()));
                current_2 = current_2.getNext();
            } else if (current_2 == null) {
                list_3.add(new Node(current_1.getSt(), current_1.getCof()));
                current_1 = current_1.getNext();
            } else {
                if (current_1.getSt() > current_2.getSt()) {
                    list_3.add(new Node(current_1.getSt(), current_1.getCof()));
                    current_1 = current_1.getNext();
                } else if (current_1.getSt() == current_2.getSt()) {
                    list_3.add(new Node(current_1.getSt(), current_1.getCof() + current_2.getCof()));
                    current_1 = current_1.getNext();
                    current_2 = current_2.getNext();
                } else if (current_2.getSt() > current_1.getSt()) {
                    list_3.add(new Node(current_2.getSt(), current_2.getCof()));
                    current_2 = current_2.getNext();
                }
            }
        }
        return list_3;
    }

    public static int meaning(List list, int x) {
        list.reset();
        int sum = 0;
        while (list.getCurrent() != null) {
            sum += (int) (list.getCurrent().getCof() * Math.pow(x, list.getCurrent().getSt()));
            list.setCurrent(list.getCurrent().getNext());
        }
        return sum;
    }

    public static boolean equality(List list_1, List list_2) {
        if (list_1.getSize() != list_2.getSize() || list_1.getSize() == 0 || list_2.getSize() == 0)
            return false;
        else {
            list_1.reset();
            list_2.reset();
            while (list_1.getCurrent() != null) {
                if (list_1.getCurrent().getSt() != list_2.getCurrent().getSt() || list_1.getCurrent().getCof() != list_2.getCurrent().getCof())
                    return false;
                list_1.setCurrent(list_1.getCurrent().getNext());
                list_2.setCurrent(list_2.getCurrent().getNext());
            }
            return true;
        }
    }
    public static int enterSize() {
        System.out.println("Введите степень многочлена");
        Scanner scan = new Scanner(System.in);
        int size = -1;
        do {
            while (!scan.hasNextInt())
                scan.next();
            size = scan.nextInt();
        } while (size < 0);
        return size;
    }

    public static void enterList(List list) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Введите коэффициенты многочлена");
        for (int i = list.getSize(); i >= 0; i--) {
            while (!scan.hasNextInt())
                scan.next();
            list.add(new Node(i, scan.nextInt()));
        }
        list.setSize(list.getHead().getSt());
    }
}
