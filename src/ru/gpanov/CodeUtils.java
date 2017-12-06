package ru.gpanov;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class CodeUtils {

    private static int getNumberOfOne(int[] elements) {
        int count = 0;
        for (int el : elements) {
            if (el > 0) {
                count++;
            }
        }
        return count;
    }

    private static int getNumberOfOne(Code code) {
        return getNumberOfOne(code.getElements());
    }

    static Code[] collectAllCodes(Code[] matrix) {
        List<Code> allCodes = new ArrayList<>();
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

    static void printMatrix(List<Code> matrix) {
        printMatrix(matrix.toArray(new Code[matrix.size()]));
    }

    static void printMatrix(Code[] matrix) {
        for (Code code : matrix) {
            System.out.println(code);
        }
    }

    static void printMatrixForLaTex(List<Code> matrix) {
        printMatrixForLaTex(matrix.toArray(new Code[matrix.size()]));
    }

    static void printMatrixForLaTex(Code[] matrix) {
        for (Code code : matrix) {
            for (int i = 0; i < code.size(); i++) {
                int el = code.getElements()[i];
                System.out.print(el);
                if (i != code.size() - 1) {
                    System.out.print(" & ");
                } else {
                    System.out.println("\\\\");
                }
            }
        }
    }

    static void printMatrixForSharp(Code[] matrix, String prefix) {
        System.out.println(prefix + ":---------------------");
        if (matrix == null && matrix.length < 1) {
            return;
        }
        if (matrix.length < 2) {
            printMatrix(matrix);
            return;
        }
        printMatrix(matrix);
        int n = matrix[0].size();
        if (n < 2) {
            return;
        }
        List<Code> matrixWithZero = new ArrayList<>();
        List<Code> matrixWithOne = new ArrayList<>();
        for (Code code : matrix) {
            if (code.getElements()[0] == 0) {
                matrixWithZero.add(getReducedCode(code));
            } else {
                matrixWithOne.add(getReducedCode(code));
            }
        }
        printMatrixForSharp(matrixWithZero.toArray(new Code[matrixWithZero.size()]), prefix + "0");
        printMatrixForSharp(matrixWithOne.toArray(new Code[matrixWithZero.size()]), prefix + "1");
    }

    private static Code getReducedCode(Code code) {
        int[] el = code.getElements();
        int[] newEl = new int[code.size() - 1];
        System.arraycopy(el, 1, newEl, 0, el.length - 1);
        return new Code(newEl);
    }

    static int findMinD(Code[] matrix) {
        Code[] allCodes = collectAllCodes(matrix);
        //printMatrixForSharp(allCodes, "");
        int minD = Integer.MAX_VALUE;
        for (Code code : allCodes) {
            int w = getNumberOfOne(code);
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

    public static List<Code> findZeroNeighbors(Code[] h) {
        return findCoverageOfZeroNeighborhood(h);
    }

    private static List<Code> findCoverageOfZeroNeighborhood(Code[] h) {
        Map<Code, List<Code>> decisiveAreas = findDecisiveAreas(h);
        List<Code> decisiveAreaOfZero = decisiveAreas.get(Code.ZERO_CODE(h[0].size()));
        decisiveAreas.remove(Code.ZERO_CODE(h[0].size()));
        List<Code> allCodesNearbyZero = findAllCodesNearbyZero(decisiveAreaOfZero);
        return findMinCoverage(allCodesNearbyZero, decisiveAreas);
    }

    private static Map<Code, List<Code>> findDecisiveAreas(Code[] h) {
        Code[] allCodesFromH = collectAllCodes(h);
        Map<Code, List<Code>> decisiveAreas = new HashMap<>();
        initMapWithCodes(decisiveAreas, allCodesFromH);
        int[] allPossibleCodes = new int[allCodesFromH[0].size()];
        for (int i = 0; i < Math.pow(2, allCodesFromH[0].size()); i++) {
            Code currentCodeForCheck = new Code(allPossibleCodes);
            List<Code> nearestCodesFromCurrent = findNearestCodes(allCodesFromH, currentCodeForCheck);
            for (Code nextCode : nearestCodesFromCurrent) {
                decisiveAreas.get(nextCode).add(currentCodeForCheck);
            }
            incrementElements(allPossibleCodes);
        }
        return decisiveAreas;
    }

    private static void initMapWithCodes(Map<Code, List<Code>> map, Code[] codesToInitWith) {
        for (Code code : codesToInitWith) {
            map.put(code, new ArrayList<Code>());
        }
    }

    private static List<Code> findNearestCodes(Code[] allOtherCodes, Code current) {
        int minDistance = Integer.MAX_VALUE;
        List<Code> result = new ArrayList<>();
        for (Code code : allOtherCodes) {
            int currentDistance = current.distance(code);
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                result.clear();
                result.add(code);
            } else if (currentDistance == minDistance) {
                result.add(code);
            }
        }
        return result;
    }

    private static List<Code> findAllCodesNearbyZero(List<Code> decisiveAreaOfZero) {
        Set<Code> codesNearbyZero = new HashSet<>();
        for (Code code : decisiveAreaOfZero) {
            for (int i = 0; i < code.size(); i++) {
                int[] codeElements = code.getElements().clone();
                codeElements[i] = codeElements[i] == 1 ? 0 : 1;
                codesNearbyZero.add(new Code(codeElements));
            }
        }
        for (Code code : decisiveAreaOfZero) {
            codesNearbyZero.remove(code);
        }
        return new ArrayList<>(codesNearbyZero);
    }

    private static List<Code> findMinCoverage(List<Code> allCodesNearbyZero, Map<Code, List<Code>> decisiveAreas) {
        Map<Code, List<Code>> coverage = new HashMap<>();
        Set<Code> codesFromH = decisiveAreas.keySet();
        initMapWithCodes(coverage, allCodesNearbyZero.toArray(new Code[allCodesNearbyZero.size()]));
        for (Code codeNearbyZero : allCodesNearbyZero) {
            for (Code codeFromH : codesFromH) {
                if (decisiveAreas.get(codeFromH).contains(codeNearbyZero)) {
                    coverage.get(codeNearbyZero).add(codeFromH);
                }
            }
        }

        Set<Code> result = new HashSet<>();
        for (Code codeNearbyZero : allCodesNearbyZero) {
            if (coverage.get(codeNearbyZero).size() == 1) {
                result.add(coverage.get(codeNearbyZero).get(0));
            }
        }
        for (Code codeNearbyZero : allCodesNearbyZero) {
            if (coverage.get(codeNearbyZero).size() > 1) {
                boolean isContains = false;
                for (Code alreadyInResult : result) {
                    if (coverage.get(codeNearbyZero).contains(alreadyInResult)) {
                        isContains = true;
                        break;
                    }
                }
                if (!isContains) {
                    System.out.println("ALERT!");
                }
            }
        }
        return new ArrayList<>(result);
    }

    public static Code[] generateTheCodeOfVarshamovaGilberta(int n, int k) {
        System.out.printf("Начато построение кода, удовлетворяющего границе Варшамова-Гилберта с n = %d, k = %d %n", n, k);
        int r = findDForVarshamovaGilberta(n, k);
        k = n - k; //т.к. коды для проверочной
        System.out.printf("Для заданных n и k, r = %d %n", r);

        List<Code> incompatibleCodes = new ArrayList<>();
        incompatibleCodes.add(Code.ZERO_CODE(k));
        List<Code> transposedMatrix = new ArrayList<>();
        System.out.println("Взятые коды:");
        Code previousCode = Code.ZERO_CODE(k);
        for (int i = 0; i < n; i++) {
            System.out.println("Взятие следующего ряда и удаление всех комбинаций с ним, которые нам теперь не подходят.");
            Code nextCode = getNextAndAddIncompatibleCodes(transposedMatrix, incompatibleCodes, previousCode, r, n);
            System.out.printf("Взят %d ряд генерируемой проверочной матрицы: %s, %n", i, nextCode);
            transposedMatrix.add(nextCode);
            previousCode = nextCode.clone();
        }
        return transposeMatrix(transposedMatrix);
    }

    private static ArrayList<Code> generateAllPossibleCodes(int k) {
        Code[] allPossibleCodes = new Code[(int) Math.pow(2, k)];
        System.out.println(allPossibleCodes.length);
        int[] currentCode = new int[k];
        for (int i = 0; i < allPossibleCodes.length; i++) {
            allPossibleCodes[i] = new Code(currentCode);
            incrementElements(currentCode);
        }
        return new ArrayList<>(Arrays.asList(allPossibleCodes));
    }

    private static int findDForVarshamovaGilberta(int n, int k) {
        int sum = (int) (Math.pow(2, n - k) - 1);
        int i;
        for (i = 0; sum > 0; i++) {
            sum -= binomial(n - 1, i + 1);
        }
        return i + 1;
    }

    private static int binomial(int n, int k) {
        if (k > n - k) {
            return binomial(n, n - k);
        }
        int b = 1;
        int i = 1;
        int m = n;
        while (i <= k) {
            b = b * m / i;
            i++;
            m--;
        }
        return b;
    }

    private static Code getNextAndAddIncompatibleCodes(List<Code> previouslyAdded, List<Code> incompatibleCodes, Code previousCode, int r, int n) {
        int k = previousCode.size();
        if (incompatibleCodes.size() == Math.pow(2, k)) {
            throw new RuntimeException("Something went wrong!");
        }
        Code next = findNext(incompatibleCodes, previousCode, r);
        if (previouslyAdded.size() != n - 1)
            addNewIncompatibleCodes(previouslyAdded, incompatibleCodes, next, r);
        return next;
    }

    private static Code findNext(List<Code> incompatibleCodes, Code previous, int r) {
        int k = previous.size();
        Code currentCode = new Code(incrementElements(previous.getElements()));
        while (!currentCode.equals(Code.ZERO_CODE(k))) {
            if (incompatibleCodes.contains(currentCode)) {
                incrementElements(currentCode.getElements());
                continue;
            }
            return currentCode;
        }
        throw new RuntimeException("Impossible to build the code");
    }

    private static void addNewIncompatibleCodes(List<Code> previouslyAdded, List<Code> incompatibleCodes, Code current, int r) {
        for (int i = 1; i < r - 1; i++) {
            int[] currentCombination = new int[previouslyAdded.size()];
            for (int combinationCounter = 0; combinationCounter < Math.pow(2, previouslyAdded.size()); combinationCounter++) {
                if (getNumberOfOne(currentCombination) > i) {
                    incrementElements(currentCombination);
                    continue;
                }
                Code incompatible = Code.ZERO_CODE(current.size());
                incompatible.add(current);
                for (int codeIndex = 0; codeIndex < previouslyAdded.size(); codeIndex++) {
                    if (currentCombination[codeIndex] == 1) {
                        incompatible.add(previouslyAdded.get(codeIndex));
                    }
                }
                incrementElements(currentCombination);
                incompatibleCodes.add(incompatible);
            }
        }
    }

//    private static Code getNextAndRemoveIncompatibleFromPossibleCodes(List<Code> previouslyAdded, List<Code> allPossibleCodes, int r) {
//        if (allPossibleCodes.size() == 0) {
//            throw new RuntimeException("Something went wrong!");
//        }
//        Code possibleNext = allPossibleCodes.get(0);
//        removeIncompatible(previouslyAdded, allPossibleCodes, possibleNext, r);
//        return possibleNext;
//    }
//
//    private static void removeIncompatible(List<Code> previouslyAdded, List<Code> allPossibleCodes, Code possibleNext, int r) {
//        for (int i = 1; i < r - 1; i++) {
//            int[] currentCombination = new int[previouslyAdded.size()];
//            for (int combinationCounter = 0; combinationCounter < Math.pow(2, previouslyAdded.size()); combinationCounter++) {
//                if (getNumberOfOne(currentCombination) > i) {
//                    incrementElements(currentCombination);
//                    continue;
//                }
//                Code incompatible = Code.ZERO_CODE(possibleNext.size());
//                incompatible.add(possibleNext);
//                for (int codeIndex = 0; codeIndex < previouslyAdded.size(); codeIndex++) {
//                    if (currentCombination[codeIndex] == 1) {
//                        incompatible.add(previouslyAdded.get(codeIndex));
//                    }
//                }
//                incrementElements(currentCombination);
//                allPossibleCodes.remove(incompatible);
//            }
//        }
//    }

    public static Code[] transposeMatrix(List<Code> matrix) {
        return transposeMatrix(matrix.toArray(new Code[matrix.size()]));
    }

    public static Code[] transposeMatrix(Code[] matrix) {
        int n = matrix.length;
        int k = matrix[0].size();
        Code[] transposedMatrix = new Code[k];
        for (int oldRowIndex = 0; oldRowIndex < k; oldRowIndex++) {
            int[] elements = new int[n];
            for (int oldCodeIndex = 0; oldCodeIndex < n; oldCodeIndex++) {
                elements[oldCodeIndex] = matrix[oldCodeIndex].getElements()[oldRowIndex];
            }
            transposedMatrix[oldRowIndex] = new Code(elements);
        }
        return transposedMatrix;
    }
}