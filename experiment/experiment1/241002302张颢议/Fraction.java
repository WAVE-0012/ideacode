// Fraction.java
import java.math.BigInteger;

/*
 * 分数类Fraction，形如 +a/b，可以进行加减乘除等运算。除了构造之外，内部数据不可更改
 */
public class Fraction implements Comparable<Fraction> {
    private int sign; //+1， 0， -1
    private BigInteger numerator; // 分子
    private BigInteger denominator; // 分母

    public static final Fraction ONE = new Fraction(1, 1); // 1/1
    public static final Fraction ZERO = new Fraction(0, 1);// 0/1
    public static final Fraction NaN = new Fraction(0, 0); // Not a Number
    public static final Fraction POSITIVE_INFINITY = new Fraction(1, 0); // 1/0， 表示正无穷
    public static final Fraction NEGATIVE_INFINITY = new Fraction(-1, 0); // -1/0，表示负无穷

    public Fraction() { // 默认构造函数，即ONE
        sign = 1;
        numerator = BigInteger.ONE;
        denominator = BigInteger.ONE;
    }
    public int getSign() {
        return sign;
    }
    public BigInteger getNumerator() {
        return numerator;
    }
    public BigInteger getDenominator() {
        return denominator;
    }
    /*
     * 最重要的构造方法。只输入分子分母两个参数，但不一定是最终的分子分母。
     * 构造过程中要实现约分为最简式！调用BigInteger类的gcd()方法。
     * 要处理特殊情况：
     * 如果是负无穷，负数/0，则最终为-1/0；
     * 如果是正无穷，正数/0，最终为+1/0；
     * 如果是 0/任何非零数，最终为0/1；
     * 如果是0/0，则最终为0/0，即NaN。
     */
    public Fraction(BigInteger bNumerator, BigInteger bDenominator) {
        //分母为零的情况
        if (bDenominator.equals(BigInteger.ZERO)) {
            if (bNumerator.equals(BigInteger.ZERO)) {//处理NaN
                this.sign = 0;
                this.numerator = BigInteger.ZERO;
                this.denominator = BigInteger.ZERO;
            } else {//处理无穷
                this.sign = bNumerator.signum();
                this.numerator = BigInteger.ONE;
                this.denominator = BigInteger.ZERO;
            }
            return;
        }
        //分子为零的情况
        if (bNumerator.equals(BigInteger.ZERO)) {
            this.sign = 0;
            this.numerator = BigInteger.ZERO;
            this.denominator = BigInteger.ONE;
            return;
        }
        //处理一般情况：约分
        this.sign = bNumerator.signum() * bDenominator.signum();//获取正负号
        BigInteger num = bNumerator.abs();//分子绝对值
        BigInteger den = bDenominator.abs();//分母绝对值
        BigInteger gcd = num.gcd(den);//最大公约数
        this.numerator = num.divide(gcd);//约分
        this.denominator = den.divide(gcd);//约分
    }
    public Fraction(long lNumerator, long lDenominator) { // 长整型构造方法
        this(new BigInteger("" + lNumerator), new BigInteger("" + lDenominator));
    }
    public Fraction(String sNumerator, String sDenominator) { // 字符出型构造方法
        this(new BigInteger(sNumerator), new BigInteger(sDenominator));
    }
    /*
     * 这是继承自Object的方法，返回描述对象的字符出，形如 +-a/b;
     * 如果是非数，则返回"NaN";
     * 如果是正无穷，则返回"+inf";
     * 如果是负无穷，则返回"-inf"；
     * 如果是零，则返回"0/1"；
     */
    @Override
    public String toString() {
        if (this.isNaN()) {
            return "NaN";
        }
        if (this.isPositiveInfinity()) {
            return "+inf";
        }
        if (this.isNegativeInfinity()) {
            return "-inf";
        }
        if (this.isZero()) {
            return "0/1";
        }
        return ((sign > 0) ? "+" : (sign < 0 ? "-" : "")) + numerator.toString() + "/" + denominator.toString();
    }
    /*
     * 继承自Object的方法，返回一个内容与自身完全一样的对象。
     * 虽然返回类型为Object，但本质类型是 Fraction。
     */
    @Override
    public Object clone() {
        return new Fraction(this.numerator,this.denominator);//对原分数进行备份处理。
    }
    @Override
    public int hashCode() { // 继承自Object的方法，计算哈希值。
        return sign + numerator.hashCode() + denominator.hashCode();
    }
    @Override
    public boolean equals(Object obj) { // 注意和compareTo有一点点冲突，权宜之计。
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Fraction that = (Fraction)obj;
        return this.sign == that.sign  &&  this.numerator.equals(that.numerator)  &&  this.denominator.equals(that.denominator);
    }
    /*
     * 实现接口Comparable，比较本对象和对象that的大小，返回-1，0，1三种值。
     * 设定正无穷和正无穷相等，负无穷和负无穷相等。
     * 设定ZERO和NaN相等（权宜之计）。
     */
    @Override
    public int compareTo(Fraction that) {
        // 处理 NaN 和 ZERO 相等的情况
        if ((this.isNaN() && that.isZero()) || (this.isZero() && that.isNaN())) {
            return 0;
        }
        // 处理 NaN - 让 NaN 排在 0 之后，正数之前
        if (this.isNaN() && that.isNaN()) return 0;
        if (this.isNaN()) {
            // NaN 大于 0，小于正数
            if (that.isZero()) return 0;
            if (that.isPositive() && !that.isZero()) return -1;
            return 1;
        }
        if (that.isNaN()) {
            if (this.isZero()) return 0;
            if (this.isPositive() && !this.isZero()) return 1;
            return -1;
        }
        // 处理无穷
        if (this.isPositiveInfinity()) return that.isPositiveInfinity() ? 0 : 1;
        if (this.isNegativeInfinity()) return that.isNegativeInfinity() ? 0 : -1;
        if (that.isPositiveInfinity()) return -1;
        if (that.isNegativeInfinity()) return 1;
        // 正常分数比较：交叉相乘
        BigInteger left = this.numerator.multiply(that.denominator);
        BigInteger right = that.numerator.multiply(this.denominator);
        // 考虑符号
        left = left.multiply(BigInteger.valueOf(this.sign));
        right = right.multiply(BigInteger.valueOf(that.sign));
        return left.compareTo(right);
    }
    public boolean isZero() {
        return equals(ZERO);
    }
    public boolean isOne() {
        return equals(ONE);
    }
    public boolean isNaN() {
        return equals(NaN);
    }
    public boolean isPositive() {
        return (sign > 0);
    }
    public boolean isNegative() {
        return (sign < 0);
    }
    public boolean isPositiveInfinity() {
        return equals(POSITIVE_INFINITY);
    }
    public boolean isNegativeInfinity() {
        return equals(NEGATIVE_INFINITY);
    }
    public boolean isInfinity() {
        return isPositiveInfinity() || isNegativeInfinity();
    }
    public Fraction abs() { // 返回另一个Fraction对象，是本对象的绝对值。
        if (this.sign >= 0) {
            return (Fraction) this.clone();
        } else {
            return this.negate();
        }
    }
    public Fraction negate() {// 返回本对象的相反数。
        //处理NaN
        if (this.isNaN()) return NaN;
        Fraction result = new Fraction();
        result.sign = -this.sign;
        result.numerator = this.numerator;
        result.denominator = this.denominator;
        return result;
    }
    /*
     * 私有方法，类内使用，类外不能使用。
     * 如果this对象和that对象都是非ZERO、非NaN，则比较两个分数的大小；
     * 返回 -1，0， 1 三者之一。
     */
    private int positiveCompareTo(Fraction that) {
        //假设二者都为正分数
        BigInteger left = this.numerator.multiply(that.denominator);
        BigInteger right = that.numerator.multiply(this.denominator);
        return left.compareTo(right);
    }
    /*
     * 私有方法，类内使用，类外不能使用。
     * 如果this和that对象非ZERO，非NaN，则计算其正值部分的和，也就是返回二者绝对值之和，也是一个分数。
     */
    private Fraction positiveAdd(Fraction that) {
        BigInteger newNum = this.numerator.multiply(that.denominator).add(that.numerator.multiply(this.denominator));
        BigInteger newDen = this.denominator.multiply(that.denominator);
        return new Fraction(newNum, newDen);
    }
    /*
     * 私有方法，类内使用，类外不能使用。
     * 两个分数的绝对值相减：abs(this)-abs(that)。
     * 注意结果有可能正分数，有可能是负分数。
     */
    private Fraction positiveSubtract(Fraction that) {
        BigInteger newNum = this.numerator.multiply(that.denominator).subtract(that.numerator.multiply(this.denominator));
        BigInteger newDen = this.denominator.multiply(that.denominator);
        return new Fraction(newNum, newDen);
    }
    /*
     * 两个分数相加，注意各种复杂情况的判断：
     * 正无穷和负无穷相加为NaN；
     * NaN与任何数相加都是NaN；
     */
    public Fraction add(Fraction that) {
        if(this.isNaN()||that.isNaN()){
            return NaN;
        }
        //处理无穷
        if(this.isInfinity()){
            if(that.isInfinity()){
                if(this.sign == that.sign){
                    return this;//同号不变
                }else {
                    return NaN;//异号为NaN
                }
            }else {
                return this;
            }
        }
        if(that.isInfinity()){
            return that;//有限数加无穷
        }
        //处理零
        if(this.isZero()){
            return that;
        }
        if(that.isZero()){
            return (Fraction) this.clone();
        }
        //一般情况
        if(this.sign == that.sign){
            Fraction result = this.positiveAdd(that);
            result.sign  =this.sign;
            return result;
        }else{
            //异号绝对值相减
            Fraction absThis = this.abs();
            Fraction absThat = that.abs();
            Fraction diff = absThis.positiveSubtract(absThat);
            if (diff.sign == 0) { // 差为零
                return ZERO;
            }
            // 差的符号取绝对值大的那个数的符号
            int cmp = absThis.positiveCompareTo(absThat);
            if (cmp > 0) {
                diff.sign = this.sign;
            } else {
                diff.sign = that.sign;
            }
            return diff;
        }
    }
    public Fraction subtract(Fraction that) { // 两个分数相减：
        return add(that.negate());
    }
    private Fraction positiveMultiply(Fraction that) { //两个非零非NaN的分数的绝对值相乘
        BigInteger newNum = this.numerator.multiply(that.numerator);
        BigInteger newDen = this.denominator.multiply(that.denominator);
        return new Fraction(newNum,newDen);
    }
    private Fraction positiveDivide(Fraction that) { //两个非零非NaN的分数的绝对值相除
        BigInteger newNum = this.numerator.multiply(that.denominator);
        BigInteger newDen = this.denominator.multiply(that.numerator);
        return new Fraction(newNum, newDen);
    }
    /*
     * 本分数扩大ratio倍并返回新分数对象。
     * 如果本分数是NaN，则返回NaN；
     * 如果ratio是0：如果本分数是无穷，则返回NaN；如果不是无穷，则返回ZERO；
     * 除此之外，非特殊运算要考虑到ratio的正负号。
     */
    public Fraction enlarge(long ratio) {
        if(this.isNaN()){
            return NaN;
        }
        if(ratio == 0){
            if(this.isInfinity()){
                return NaN;
            }else {
                return ZERO;
            }
        }
        //一般情况
        if(this.isZero()){
            return ZERO;
        }
        //有限的非零分数
        int newSign = this.sign * Long.signum(ratio);
        BigInteger newNum = this.numerator.multiply(BigInteger.valueOf(Math.abs(ratio)));
        return new Fraction(newSign > 0 ? newNum : newNum.negate(),this.denominator);
    }
    /*
     * 把本分数缩小ratio倍，并返回新的分数对象。
     * 如果本分数是NaN，则返回NaN；
     * 如果ratio是0，则：
     * （1）如果本分数是负无穷，则返回负无穷；
     * （2）如果本分数是正无穷，则返回正无穷；
     * （3）如果本分数是ZERO，则返回NaN；
     * （4）如果本分数是正数，则返回正无穷；
     * （5）如果本分数是负数，则返回负无穷。
     * 除此之外，非特殊运算时需要考虑ratio的正负号。
     */
    public Fraction diminish(long ratio) {
        if(this.isNaN()){
            return NaN;
        }
        if(ratio == 0){
            if(this.isNegativeInfinity()){
                return NEGATIVE_INFINITY;
            }
            if(this.isPositiveInfinity()){
                return POSITIVE_INFINITY;
            }
            if(this.isZero()){
                return NaN;
            }
            if(this.isPositive()){
                return POSITIVE_INFINITY;
            }
            if(this.isNegative()){
                return NEGATIVE_INFINITY;
            }
        }
        //一般情况,除以ratio
        if(this.isZero()){
            return ZERO;
        }
        if(this.isInfinity()){
            if(ratio>0){
                return this;
            }else {
                return this.negate();
            }
        }
        //有限非零数
        int newSign = this.sign * Long.signum(ratio);
        BigInteger newDen = this.denominator.multiply(BigInteger.valueOf(Math.abs(ratio)));
        return new Fraction(newSign > 0 ? this.numerator : this.numerator.negate(),newDen);
    }
    /*
     * 两分数相乘，注意各种复杂情况的判断：
     * NaN与任何数相乘都是NaN；
     * 正无穷与ZERO相乘为NaN；
     * 负无穷与ZERO相乘为NaN。
     */
    public Fraction multiply(Fraction that) {
        if(this.isNaN()||that.isNaN()){
            return NaN;
        }
        if (this.isInfinity() && that.isZero()) return NaN;
        if (this.isZero() && that.isInfinity()) return NaN;
        //处理无穷
        if (this.isInfinity() || that.isInfinity()) {
            int s = this.sign * that.sign;
            return s > 0 ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
        }
        //一般情况
        if (this.isZero() || that.isZero()) return ZERO;
        Fraction result = this.positiveMultiply(that);
        result.sign = this.sign * that.sign;
        return result;
    }
    /*
     * 求本分数对象的倒数：
     * 正负无穷的倒数是ZERO；
     * ZERO的倒数是NaN；
     * NaN的倒数是NaN。
     */
    public Fraction reciprocal() {
        if (this.isNaN()) {
            return NaN;
        }
        if (this.isZero()) {
            return NaN;
        }
        if (this.isPositiveInfinity()) {
            return ZERO;
        }
        if (this.isNegativeInfinity()) {
            return ZERO;
        }
        // 一般非零分数
        return new Fraction(this.sign * this.denominator.intValue(), this.numerator.intValue());
    }
    /*
     * 两数相除，注意复杂情况：
     * NaN做被除数和除数的结果都是NaN；
     * ZERO / ZERO的结果是NaN，ZERO/非ZERO非NaN的结果是ZERO；
     * 正数除以ZERO的结果是正无穷；负数除以ZERO的结果是负无穷；
     * 正负无穷除以正负无穷的结果是NaN。
     */
    public Fraction divide(Fraction that) {
        //处理NaN
        if (this.isNaN() || that.isNaN()) {
            return NaN;
        }
        //处理零
        if (this.isZero()) {
            if (that.isZero()) {
                return NaN;
            } else {
                return ZERO;
            }
        }
        if (that.isZero()) {
            if (this.isPositive()) {
                return POSITIVE_INFINITY;
            } else if (this.isNegative()) {
                return NEGATIVE_INFINITY;
            }
        }
        //处理无穷
        if (this.isInfinity()) {
            if (that.isInfinity()) {
                return NaN;
            } else {
                int newSign = this.sign * that.sign;
                return (newSign > 0) ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
            }
        }
        if (that.isInfinity()) {
            return ZERO; // 有限数除以无穷
        }
        // 一般情况
        Fraction result = this.positiveDivide(that);
        result.sign = this.sign * that.sign;
        return result;
    }
    /*
     * 静态方法估算两个正的大整数的比值。
     * positiveLarger >= positiveSmaller
     * 返回的形式是：5.3208E2025，意义是，大正数是小正数的5.3208 * 10^2025 倍：
     */
    private static String estimateRatio(BigInteger positiveLarger, BigInteger positiveSmaller) {
        if (positiveSmaller.equals(BigInteger.ZERO)) {
            return "Infinity";
        }

        // 计算两个大整数的位数
        int largerDigits = positiveLarger.toString().length();
        int smallerDigits = positiveSmaller.toString().length();

        // 计算绝对位数差
        int absDigitDiff = Math.abs(largerDigits - smallerDigits);

        // 根据位数差决定小数位数
        int decimalPlaces;

        if (absDigitDiff == 0) {
            // 位数相同：保留16位小数
            decimalPlaces = 16;
        } else if (absDigitDiff == 1) {
            // 位数差1：保留15位小数
            decimalPlaces = 15;
        } else if (absDigitDiff >= 2 && absDigitDiff <= 10) {
            // 位数差2-10：保留14位小数
            decimalPlaces = 14;
        } else if (absDigitDiff > 10 && absDigitDiff <= 50) {
            // 位数差11-50：保留13位小数
            decimalPlaces = 13;
        } else if (absDigitDiff > 50 && absDigitDiff <= 100) {
            // 位数差51-100：保留12位小数
            decimalPlaces = 12;
        } else {
            // 位数差超过100：保留11位小数
            decimalPlaces = 11;
        }

        // 特殊规则：当总位数很小时，保留1位小数
        if (largerDigits + smallerDigits <= 10) {
            decimalPlaces = Math.min(decimalPlaces, 1);
        }

        // 使用BigDecimal进行高精度计算
        java.math.BigDecimal larger = new java.math.BigDecimal(positiveLarger);
        java.math.BigDecimal smaller = new java.math.BigDecimal(positiveSmaller);

        // 计算比值
        java.math.BigDecimal ratio = larger.divide(smaller, decimalPlaces + 10, java.math.RoundingMode.HALF_UP);

        // 计算科学计数法的指数
        int exponent = 0;
        java.math.BigDecimal mantissa = ratio;

        // 将比值调整到[1.0, 10.0)范围内
        while (mantissa.compareTo(java.math.BigDecimal.TEN) >= 0) {
            mantissa = mantissa.divide(java.math.BigDecimal.TEN, decimalPlaces + 10, java.math.RoundingMode.HALF_UP);
            exponent++;
        }

        while (mantissa.compareTo(java.math.BigDecimal.ONE) < 0) {
            mantissa = mantissa.multiply(java.math.BigDecimal.TEN);
            exponent--;
        }

        // 四舍五入到指定小数位
        java.math.BigDecimal rounded = mantissa.setScale(decimalPlaces, java.math.RoundingMode.HALF_UP);

        // 去除末尾的0
        String mantissaStr = rounded.stripTrailingZeros().toPlainString();

        // 确保有小数点
        if (decimalPlaces > 0 && !mantissaStr.contains(".")) {
            mantissaStr = mantissaStr + ".0";
        }

        return mantissaStr + "E" + exponent;
    }
    /*
     * 因为分数对象的分子和分母会很大，这里给出估计，以字符串的形式给出。
     * 特殊情况：正无穷返回"+inf"；负无穷返回"-inf"；NaN返回"NaN"。
     * 一般情况：返回 -5.3208E2025;(2030d)/(5d) 的形式，表示分子是分母的-5.3208E2025倍，分子有2030十进制位，分母有5位；
     * 或者返回 1/(-5.3208E2025);(5d)/(2030d) 的形式，表示分母是分子的-5.3208E2025倍，分子有5十进制位，分母有2030位。
     * 注意E前面是[1.0,10)之间的一个数，其精度要求是double型数值所能表达的最大精度。
     */
    public String estimate() {
        if (this.isNaN()) {
            return "NaN";
        }
        if (this.isPositiveInfinity()) {
            return "+inf";
        }
        if (this.isNegativeInfinity()) {
            return "-inf";
        }
        if (this.isZero()) {
            return "0.0E0";
        }

        // 获取分子和分母的绝对值
        BigInteger absNum = this.numerator;
        BigInteger absDen = this.denominator;

        // 计算位数
        int numDigits = absNum.toString().length();
        int denDigits = absDen.toString().length();

        // 确定符号
        String signStr = (this.sign > 0) ? "" : "-";

        // 比较绝对值大小（注意：不要直接用 compareTo，因为可能分子分母都是正数）
        int cmp = absNum.compareTo(absDen);

        if (cmp > 0) {
            // 分子绝对值大于分母
            String ratio = estimateRatio(absNum, absDen);
            return signStr + ratio + ";(" + numDigits + "d)/(" + denDigits + "d)";
        }
        else if (cmp < 0) {
            // 分母绝对值大于分子
            String ratio = estimateRatio(absDen, absNum);
            return "1/(" + signStr + ratio + ");(" + numDigits + "d)/(" + denDigits + "d)";
        }
        else {
            // 分子分母绝对值相等
            return signStr + "1.0E0;(" + numDigits + "d)/(" + denDigits + "d)";
        }
    }
    /*
     * 方幂运算，times是幂次，可正、可负、可零。因为底已经化简，本运算为了提高效率，要求分子分母单独计算。
     * NaN 的任何次方幂都是NaN；
     * ZERO 的0次和负次方幂都是NaN；ZERO的正整数次方幂都是ZERO；
     * ONE的任何次方幂都是ONE；
     * 负无穷的正偶次方幂是正无穷，负无穷的正奇次方幂是负无穷；
     * 负无穷的零次幂是ONE，负数次幂是ZERO；
     * 正无穷的零次幂是ONE，正数次幂为正无穷，负数次幂为零。
     */
    public Fraction power(int times) {
        if(this.isNaN()){
            return NaN;
        }
        if(this.isZero()){
            if(times == 0){
                return NaN;
            } else if(times > 0){
                return ZERO;
            } else {// times < 0
                return NaN;
            }
        }
        if(this.isOne()){
            return ONE;
        }
        if(this.isPositiveInfinity()){
            if(times == 0){
                return ONE;
            }else if(times > 0){
                return POSITIVE_INFINITY;
            } else {// times < 0
                return ZERO;
            }
        }
        if(this.isNegativeInfinity()){
            if(times == 0){
                return ONE;
            } else if (times > 0) {
                if(times % 2 == 0) {
                    return POSITIVE_INFINITY;
                } else {
                    return NEGATIVE_INFINITY;
                }
            } else {// times < 0
                return ZERO;
            }
        }
        //一般有限非零，非一分数
        if(times == 0){
            return ONE;
        }
        int absTimes = Math.abs(times);
        BigInteger newNum = this.numerator.pow(absTimes);
        BigInteger newDen = this.denominator.pow(absTimes);
        int newSign = (this.sign == 1 || (absTimes % 2 == 0)) ? 1 : -1;
        if (times < 0) {
            // 负指数，取倒数
            BigInteger temp = newNum;
            newNum = newDen;
            newDen = temp;
        }
        return new Fraction(newSign > 0 ? newNum : newNum.negate(), newDen);
    }

    public static void testAll() {
        Fraction fraction = new Fraction();
        System.out.println("Default fraction: " + fraction);

        fraction = new Fraction(new BigInteger("1234567890123456789"), new BigInteger("-987654321"));
        System.out.println("new Fraction(1234567890123456789, -987654321):");
        System.out.println(fraction);

        fraction = new Fraction(-258, 369);
        System.out.println("new Fraction(-258, 369):");
        System.out.println(fraction);

        fraction = new Fraction(-258, 0);
        System.out.println("new Fraction(-258, 0): ");
        System.out.println(fraction);

        fraction = new Fraction(0, -10);
        System.out.println("new Fraction(0, -10): ");
        System.out.println(fraction);

        fraction = new Fraction(12, -15);
        System.out.println("new Fraction(12, -15): ");
        System.out.println(fraction);

        System.out.println("new Fraction(12, -15).negate(): ");
        System.out.println(fraction.negate());

        System.out.println("new Fraction(12, -15).reciprocal(): ");
        System.out.println(fraction.reciprocal());

        Fraction fa = NEGATIVE_INFINITY;
        Fraction fb = new Fraction(-5, 10);
        System.out.println("NEGATIVE_INFINITY ? Fraction(-5, 10):");
        System.out.println(fa.compareTo(fb));

        System.out.println("NEGATIVE_INFINITY ? NEGATIVE_INFINITY:");
        System.out.println(fa.compareTo(fa));

        System.out.println("NaN ? ZERO:");
        System.out.println(NaN.compareTo(ZERO));

        System.out.println("POSITIVE_INFINITY ? NEGATIVE_INFINITY:");
        System.out.println(POSITIVE_INFINITY.compareTo(fa));

        System.out.println("ZERO equals NaN? :");
        System.out.println(ZERO.equals(NaN));

        System.out.println("NEGATIVE_INFINITY equals NEGATIVE_INFINITY? :");
        System.out.println(NEGATIVE_INFINITY.equals(NEGATIVE_INFINITY));

        fa = new Fraction(-6, 8);
        fb = new Fraction(15, -20);
        System.out.println("-6/8  equals  15/-20?:");
        System.out.println(fa.equals(fb));

        fa = ONE.negate().enlarge(-10);
        System.out.println("ONE.negate().enlarge(-10):");
        System.out.println(fa);

        fa = NEGATIVE_INFINITY.enlarge(-10);
        System.out.println("NEGATIVE_INFINITY.enlarge(-10):");
        System.out.println(fa);

        fa = ONE.negate().diminish(0);
        System.out.println("ONE.negate().diminish(0):");
        System.out.println(fa);

        fa = new Fraction(1000, -1001);
        System.out.println("new Fraction(1000, -1001).negate():");
        System.out.println(fa.negate());

        System.out.println("new Fraction(1000, -1001).reciprocal():");
        System.out.println(fa.reciprocal());

        System.out.println("NEGATIVE_INFINITY.reciprocal():");
        System.out.println(NEGATIVE_INFINITY.reciprocal());

        System.out.println("ZERO.reciprocal():");
        System.out.println(ZERO.reciprocal());

        System.out.println("NaN.reciprocal():");
        System.out.println(NaN.reciprocal());

        fa = new Fraction(5, 10);
        fb = new Fraction(1, -2);
        System.out.println("5/10  +  1/-2:");
        System.out.println(fa.add(fb));

        System.out.println("POSITIVE_INFINITY  +  1/-2:");
        System.out.println(POSITIVE_INFINITY.add(fb));

        System.out.println("NEGATIVE_INFINITY  +  POSITIVE_INFINITY:");
        System.out.println(NEGATIVE_INFINITY.add(POSITIVE_INFINITY));

        fa = new Fraction(1, 2);
        fb = new Fraction(-2, 3);
        System.out.println("new Fraction(1, 2).positiveAdd(new Fraction(-2, 3)): ");
        System.out.println(fa.positiveAdd(fb));

        System.out.println("new Fraction(1, 2).positiveMultiply(new Fraction(-2, 3)): ");
        System.out.println(fa.positiveMultiply(fb));

        System.out.println("new Fraction(1, 2).positiveDivide(new Fraction(-2, 3)): ");
        System.out.println(fa.positiveDivide(fb));

        System.out.println("new Fraction(1, 2).multiply(new Fraction(-2, 3)): ");
        System.out.println(fa.multiply(fb));

        System.out.println("new Fraction(1, 2).divide(new Fraction(-2, 3)): ");
        System.out.println(fa.divide(fb));

        System.out.println("NEGATIVE_INFINITY  *  NEGATIVE_INFINITY:");
        System.out.println(NEGATIVE_INFINITY.multiply(NEGATIVE_INFINITY));

        System.out.println("NEGATIVE_INFINITY  *  ZERO:");
        System.out.println(NEGATIVE_INFINITY.multiply(ZERO));

        System.out.println("ONE / NEGATIVE_INFINITY");
        System.out.println(ONE.divide(NEGATIVE_INFINITY));

        System.out.println("NEGATIVE_INFINITY / NEGATIVE_INFINITY");
        System.out.println(NEGATIVE_INFINITY.divide(NEGATIVE_INFINITY));

        Fraction[] fractions = {ONE, POSITIVE_INFINITY, ZERO, NaN, ONE, NEGATIVE_INFINITY, new Fraction(6, -8), new Fraction(2, 1), NEGATIVE_INFINITY };
        java.util.Arrays.sort(fractions);
        System.out.println("sort {ONE, POSITIVE_INFINITY, ZERO, NaN, ONE, NEGATIVE_INFINITY, new Fraction(6, -8), new Fraction(2, 1), NEGATIVE_INFINITY }:");
        for (int i=0; i<fractions.length; i++)
            System.out.print("" + fractions[i] + ", ");
        System.out.println();

        String piece = "1234567890";
        String sLong = "";
        for (int i=0; i<20; i++)
            sLong += piece;
        fa = new Fraction(sLong, "-" + piece + "8");
        System.out.println("" + fa + ".estimate():");
        System.out.println(fa.estimate());

        fa = new Fraction(piece + "8", "-" + sLong);
        System.out.println("" + fa + ".estimate():");
        System.out.println(fa.estimate());
    }
    public static void main(String[] args) {
        testAll();
    }
}