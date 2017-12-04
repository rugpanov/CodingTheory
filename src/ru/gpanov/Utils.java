package ru.gpanov;

class Utils {

    static double binomialCoefficient(double n, int k) {
            int result = 1;
            for (double i=n-k+1; i<=n; ++i)
                result *= i;
            for (int i=2; i<=k; ++i)
                result /= i;
        return result;
    }

    static double fuct(int n) {
        int result = 1;
        for (int i = 2; i <= n; i++ ){
            result *= i;
        }
        return result;
    }
}
