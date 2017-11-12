package ru.gpanov;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private static final String INPUT_FILE_NAME = "input.txt";

    public static void main(String[] args) {
        Code[] matrix = readMatrix(INPUT_FILE_NAME);
        printMatrix(matrix);
        System.out.println();
        System.out.println(String.format("Min d = %d", findMinD(matrix)));
    }

    private static Code[] readMatrix(String fileName) {
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

    private static void printMatrix(Code[] matrix) {
        for (Code code : matrix) {
            System.out.println(code);
        }
    }

    private static int findMinD(Code[] matrix) {
        Code[] allCodes = collectAllCodes(matrix);
        int minD = Integer.MAX_VALUE;
        for (Code code: allCodes) {
            int d = CodeUtils.getNumberOfOne(code);
            if (d < minD) {
                minD = d;
            }
        }
        return minD;
    }

    private static Code[] collectAllCodes(Code[] matrix) {
        List<Code> allCodes = new ArrayList<>();
        allCodes.addAll(Arrays.asList(matrix));
        collectAllCodes(Arrays.asList(matrix), allCodes);
        Set<Code> allCodesWithoutDuplicates = new HashSet<>(allCodes);
        return allCodesWithoutDuplicates.toArray(new Code[allCodesWithoutDuplicates.size()]);
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
}

class Code {
    private int[] elements;

    Code(String rowAsString) {
        String[] elementsOfLine = rowAsString.split(" ");
        elements = new int[elementsOfLine.length];
        for (int i = 0; i < elementsOfLine.length; i++) {
            elements[i] = Integer.parseInt(elementsOfLine[i]);
        }
    }

    private Code(int[] elements) {
        this.elements = elements;
    }

    private int size() {
        return elements.length;
    }

    public int[] getElements() {
        return elements;
    }

    Code addAndCreateNew(Code another) {
        if (another.size() != this.size()) {
            throw new IllegalArgumentException("Code sizes should be the same!");
        }
        int[] otherElements = another.getElements();
        int[] result = new int[size()];
        for (int i = 0; i < this.size(); i++) {
            result[i] = (elements[i] + otherElements[i]) % 2;
        }
        return new Code(result);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int el : elements) {
            sb.append(el).append(" ");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Code))
            return false;
        Code another = (Code)obj;
        if (this.size() != another.size()){
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (elements[i] != another.getElements()[i]){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 239;
        for (int i = 0; i < this.size(); i++) {
            result += i * 29 * elements[i];
        }
        return result;
    }
}

class CodeUtils {
    public static int getNumberOfOne(Code code) {
        int count = 0;
        for (int el: code.getElements()) {
            if (el > 0) {
                count++;
            }
        }
        return count;
    }
}