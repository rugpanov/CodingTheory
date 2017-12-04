package ru.gpanov;


import static ru.gpanov.CodeUtils.*;

public class Main {

    private static final String INPUT_FILE_NAME = "input.txt";

    public static void main(String[] args) {
        String fileName = INPUT_FILE_NAME;
        if (args != null && args.length > 0) {
            fileName = args[0];
        }
        Code[] matrix = readMatrix(fileName);
        printMatrix(matrix);
        System.out.println();
        System.out.println(String.format("Min d = %d", findMinD(matrix)));
      // List<Code[]> goodCyclic = CodeUtils.findGoodCyclic();
//        for (Code[] aGoodCyclic : goodCyclic) {
//            printMatrix(aGoodCyclic);
//            System.out.println();
//        }
    }
}



