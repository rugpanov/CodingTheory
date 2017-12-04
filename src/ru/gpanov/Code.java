package ru.gpanov;

public class Code {
    private int[] elements;

    Code(String rowAsString) {
        String[] elementsOfLine = rowAsString.split(" ");
        elements = new int[elementsOfLine.length];
        for (int i = 0; i < elementsOfLine.length; i++) {
            elements[i] = Integer.parseInt(elementsOfLine[i]);
        }
    }

    Code(int[] elements) {
        this.elements = elements.clone();
    }

    int size() {
        return elements.length;
    }

    int[] getElements() {
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

    Code shiftAndCreateNew() {
        int[] shiftedCodeElements = new int[this.size()];
        shiftedCodeElements[0] = elements[this.size() - 1];
        System.arraycopy(elements, 0, shiftedCodeElements, 1, this.size() - 1);
        return new Code(shiftedCodeElements);
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

    public static Code ZERO_CODE(int n) {
        int[] elements = new int[n];
        return new Code(elements);
    }

    @Override
    public Code clone() {
        return new Code(this.getElements());
    }
}