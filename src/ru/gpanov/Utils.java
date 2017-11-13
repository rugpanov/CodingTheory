package ru.gpanov;

class Utils {

    static double binomialCoefficient(int n, int k) {
            int result = 1;
            for (int i=n-k+1; i<=n; ++i)
                result *= i;
            for (int i=2; i<=k; ++i)
                result /= i;
        return result;
    }

}
