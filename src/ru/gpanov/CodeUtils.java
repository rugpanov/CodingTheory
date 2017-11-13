package ru.gpanov;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class CodeUtils {
    private static int getNumberOfOne(Code code) {
        int count = 0;
        for (int el : code.getElements()) {
            if (el > 0) {
                count++;
            }
        }
        return count;
    }

    static Code[] collectAllCodes(Code[] matrix) {
        List<Code> allCodes = new ArrayList<>();
        int numberOfCombination = (int) Math.round(Math.pow(2, matrix.length));
        int[] nextCombination = new int[matrix.length];
        for (int i = 0; i < Math.pow(2, matrix.length); i++) {
            nextCombination = incrementElements(nextCombination);
            Code sum = Code.ZERO_CODE(matrix[0].size());
            for (int el = 0; el < matrix.length; el++) {
                if (nextCombination[el] != 0) {
                    sum = sum.addAndCreateNew(matrix[el]);
                }
            }
            allCodes.add(sum);
        }
        return allCodes.toArray(new Code[allCodes.size()]);
        //    allCodes.addAll(Arrays.asList(matrix));
        //    collectAllCodes(Arrays.asList(matrix), allCodes);
        //    Set<Code> allCodesWithoutDuplicates = new HashSet<>(allCodes);
        //   return allCodesWithoutDuplicates.toArray(new Code[allCodesWithoutDuplicates.size()]);
    }

    private static void collectAllCodes(List<Code> lastCodes, List<Code> allCodes) {
        if (lastCodes.size() <= 1) {
            return;
        }

        List<Code> newCodes = new ArrayList<>();
        for (int i = 0; i < lastCodes.size() - 1; i++) {
            newCodes.add(lastCodes.get(i).addAndCreateNew(lastCodes.get(i + 1)));
        }
        allCodes.addAll(newCodes);
        collectAllCodes(newCodes, allCodes);
        for (int i = 1; i < lastCodes.size() - 1; i++) {
            collectAllCodes(lastCodes.subList(i, lastCodes.size()), allCodes);
        }
    }

    static Code[] readMatrix(String fileName) {
        try {
            Path path = Paths.get(fileName);
            List<String> lines = Files.readAllLines(path,
                    Charset.defaultCharset());
            Code[] matrix = new Code[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                matrix[i] = new Code(lines.get(i));
            }
            return matrix;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void printMatrix(Code[] matrix) {
        for (Code code : matrix) {
            System.out.println(code);
        }
    }

    static int findMinD(Code[] matrix) {
        Code[] allCodes = collectAllCodes(matrix);
        int minD = Integer.MAX_VALUE;
        for (Code code : allCodes) {
            int w = CodeUtils.getNumberOfOne(code);
            if (w != 0 && w < minD) {
                minD = w;
            }
        }
        return minD;
    }

    static boolean isMatrixIncludeCode(Code[] matrix, Code code) {
        Code[] allCodes = collectAllCodes(matrix);
        for (Code codeToCheck : allCodes) {
            if (codeToCheck.equals(code)) {
                return true;
            }
        }
        return false;
    }

    public static List<Code[]> findGoodCyclic() {
        List<Code[]> result = new ArrayList<>();
        for (int n = 7; n <= 20; n++) {
            for (int k = 3; k <= 10; k++) {
                int[] firstRowElements = new int[n];
                for (int i = 0; i < Math.pow(2, n) - 2; i++) {
                    firstRowElements = incrementElements(firstRowElements);
                    Code[] cyclicMatrix = createCyclicMatrix(firstRowElements, k);
                    if (checkWhetherCyclicMatrix(cyclicMatrix) && checkOtherParameters(cyclicMatrix)) {
                        printMatrix(cyclicMatrix);
                        result.add(cyclicMatrix);
                        System.out.println();
                    }
                }
            }
        }
        return result;
    }

    private static int[] incrementElements(int[] elements) {
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] == 0) {
                elements[i]++;
                break;
            }
            elements[i] = 0;
        }
        return elements;
    }

    private static Code[] createCyclicMatrix(int[] elements, int k) {
        Code[] matrix = new Code[k];
        matrix[0] = new Code(elements);
        for (int i = 1; i < k; i++) {
            matrix[i] = matrix[i - 1].shiftAndCreateNew();
        }
        return matrix;
    }

    private static boolean checkWhetherCyclicMatrix(Code[] matrix) {
        Code nextCodeToCheck = matrix[matrix.length - 1].shiftAndCreateNew();
        if (!isLastElementsZero(matrix)) {
            return false;
        }
        for (int i = 0; i < nextCodeToCheck.size(); i++) {
            if (!isMatrixIncludeCode(matrix, nextCodeToCheck)) {
                return false;
            }
            nextCodeToCheck = nextCodeToCheck.shiftAndCreateNew();
        }
        return true;
    }

    private static boolean isLastElementsZero(Code[] matrix) {

        int k = matrix.length;
        Code firstRow = matrix[0];
        if (firstRow.size() < k) {
            return false;
        }
        for (int i = firstRow.size() - k + 1; i < firstRow.size(); i++) {
            if (firstRow.getElements()[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkOtherParameters(Code[] matrix) {
        int minD = findMinD(matrix);
        int k = matrix.length;
        int n = matrix[0].size();
        return minD == 5 && n < k * minD;
    }
}