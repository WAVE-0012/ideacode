// Olympiad.java
public class Olympiad {
    // 1/1 + 1/3 + 1/6 + 1/10 + 1/15 + 1/21 + ......
    // 1 (2) 3 (3) 6 (4) 10 (5) 15 (6) 21
    //  an = 1 + (2) + (3) + ... + (n) = n(n+1)/2
    // Sn = 1/a1 + 1/a2 + 1/a3 + ...... + 1/an

    // Sn = 2/(1*2) + 2/(2*3) + 2/(3*4) + ... + 2/(n(n+1))
    //      = 2[1/1 - 1/2 + 1/2 - 1/3 + 1/3 - 1/4 + ... + 1/n - 1/(n+1)]
    //      = 2[1/1 - 1/(n+1)] = 2n/(n+1)
    // 本方法对于给定的规模n，计算上述数组的前n项和：
    public static Fraction accumulateProductReciprocal(int n) {
        // 公式: 2n / (n+1)
        return new Fraction(2L * n, n + 1);

    }
    // 本方法对于给定的规模n，计算调和级数和：1/1 + 1/2 + 1/3 + ...... + 1/(n-1) + 1/n
    public static Fraction accumulateHarmonicSeries(int n) {
        Fraction sum = new Fraction(0,1);//初始化
        for (int i = 1; i <= n; i++) {
            Fraction term = new Fraction(1, i);
            sum = sum.add(term);
        }
        return sum;
    }
    // 本函数计算交错奇数的和，输入n为项数：
    // 1/1 - 1/2 + 1/3 - 1/4 + 1/5 - ...... +-1/n
    public static Fraction accumulateAlternatingSeries(int n) {
        Fraction sum = new Fraction(0, 1);//初始化
        for (int i = 1; i <= n; i++) {
            Fraction term = new Fraction((i % 2 == 1) ? 1 : -1, i);//实现交错输入
            sum = sum.add(term);
        }
        return sum;
    }
    public static void main(String[] args) {
        int n = 2025;
        Fraction sum = accumulateProductReciprocal(n);
        System.out.println("2/1*2 + 2/2*3 + 2/3*4 + ... + 2/" + n + "*" + (n+1) + ":");
        System.out.println(sum);
        System.out.println();
        sum = accumulateHarmonicSeries(n);
        System.out.println("1/1 + 1/2 + 1/3 + 1/4 + ...... + 1/" + n + ":");
        System.out.println(sum);
        System.out.println("sum.estimate():");
        System.out.println(sum.estimate());
        System.out.println();
        sum = accumulateAlternatingSeries(n);
        System.out.println("1/1 - 1/2 + 1/3 - 1/4 + ...... +-1/" + n + ":");
        System.out.println(sum);
        System.out.println();
        System.out.println("sum.estimate():");
        System.out.println(sum.estimate());
    }
}