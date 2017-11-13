package ru.gpanov;

import java.util.Arrays;
import java.util.List;

import static ru.gpanov.CodeUtils.*;

public class Main {

    private static final String INPUT_FILE_NAME = "input.txt";

    public static void main(String[] args) {
        Code[] matrix = readMatrix(INPUT_FILE_NAME);
        printMatrix(matrix);
        System.out.println();
        System.out.println(String.format("Min d = %d", findMinD(matrix)));
       // printMatrix(collectAllCodes(matrix));
        Code[] m = new Code[]{matrix[matrix.length - 1].shiftAndCreateNew()};
        CodeUtils.printMatrix(m);
        System.out.println(String.format("IsInclude = %b", isMatrixIncludeCode(matrix, matrix[matrix.length - 1].shiftAndCreateNew())));
      // List<Code[]> goodCyclic = CodeUtils.findGoodCyclic();
//        for (Code[] aGoodCyclic : goodCyclic) {
//            printMatrix(aGoodCyclic);
//            System.out.println();
//        }
    }
}



