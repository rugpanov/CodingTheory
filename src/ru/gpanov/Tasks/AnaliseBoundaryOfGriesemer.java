package ru.gpanov.Tasks;

public class AnaliseBoundaryOfGriesemer implements TaskResolver {

    private final int minK;
    private final int maxK;
    private final int minD;
    private final int maxD;
    private static String SEPARATOR = " & ";
    private static String END_OF_LINE = "\\\\\n";

    public AnaliseBoundaryOfGriesemer(int minK, int maxK, int minD, int maxD) {
        this.minK = minK;
        this.maxK = maxK;
        this.minD = minD;
        this.maxD = maxD;
    }

    @Override
    public void resolve() {
        System.out.print("d\\k" + SEPARATOR);
        for (int k = minK; k <= maxK; k++) {
            if (k != maxK) {
                System.out.print(k + SEPARATOR);
            } else {
                System.out.print(k + END_OF_LINE);
            }
        }

        for (int d = minD; d <= maxD; d++) {
            System.out.print(d + SEPARATOR);
            for (int k = minK; k <= maxK; k++) {
                if (k != maxK) {
                    System.out.print(getMinPossibleN(k, d) + SEPARATOR);
                } else {
                    System.out.print(getMinPossibleN(k, d) + END_OF_LINE);
                }
            }
        }
    }

    private int getMinPossibleN(int k, int d) {
        int n = 0;
        for (int i = 0; i < k; i++) {
            n += Math.ceil(d / Math.pow(2, i));
        }
        return n;
    }
}
