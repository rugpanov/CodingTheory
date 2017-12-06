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
        for (Code code: matrix) {
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
            for(Code nextCode: nearestCodesFromCurrent) {
                decisiveAreas.get(nextCode).add(currentCodeForCheck);
            }
            incrementElements(allPossibleCodes);
        }
        return decisiveAreas;
    }

    private static void initMapWithCodes(Map<Code, List<Code>> map, Code[] codesToInitWith) {
        for (Code code: codesToInitWith) {
            map.put(code, new ArrayList<Code>());
        }
    }

    private static List<Code> findNearestCodes(Code[] allOtherCodes, Code current) {
        int minDistance = Integer.MAX_VALUE;
        List<Code> result = new ArrayList<>();
        for (Code code: allOtherCodes) {
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
        for (Code code: decisiveAreaOfZero) {
            for (int i = 0; i < code.size(); i++) {
                int[] codeElements = code.getElements().clone();
                codeElements[i] = codeElements[i] == 1 ? 0 : 1;
                codesNearbyZero.add(new Code(codeElements));
            }
        }
        for (Code code: decisiveAreaOfZero) {
            codesNearbyZero.remove(code);
        }
        return new ArrayList<>(codesNearbyZero);
    }

    private static List<Code> findMinCoverage(List<Code> allCodesNearbyZero, Map<Code, List<Code>> decisiveAreas) {
        Map<Code, List<Code>> coverage = new HashMap<>();
        Set<Code> codesFromH = decisiveAreas.keySet();
        initMapWithCodes(coverage, allCodesNearbyZero.toArray(new Code[allCodesNearbyZero.size()]));
        for (Code codeNearbyZero: allCodesNearbyZero) {
            for (Code codeFromH: codesFromH) {
                if (decisiveAreas.get(codeFromH).contains(codeNearbyZero)) {
                    coverage.get(codeNearbyZero).add(codeFromH);
                }
            }
        }

        Set<Code> result = new HashSet<>();
        for (Code codeNearbyZero: allCodesNearbyZero) {
            if (coverage.get(codeNearbyZero).size() == 1) {
                result.add(coverage.get(codeNearbyZero).get(0));
            }
        }
        for (Code codeNearbyZero: allCodesNearbyZero) {
            if (coverage.get(codeNearbyZero).size() > 1) {
                boolean isContains = false;
                for (Code alreadyInResult: result) {
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
}