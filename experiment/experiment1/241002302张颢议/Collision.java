// Collision.java
public class Collision {
    /*
     * 如果大方块的质量是小方块质量的k倍，小方块的碰撞前速度是currU，大方块碰撞前的速度是currV：
     * 计算发生碰撞后大小方块各自的速度，并放在仅含两个分数对象的一维数组中，并输出。
     * 一维数组的第一个元素是小方块的速度，第二个元素是大方块的速度。注意速度都带有正负号。
     */
    private static Fraction[] collideOnce(int k, Fraction currU, Fraction currV) {
        // 使用弹性碰撞公式：
        // u' = [(m1 - m2)*u + 2*m2*v] / (m1 + m2)
        // v' = [2*m1*u + (m2 - m1)*v] / (m1 + m2)
        // 其中 m1 = 1, m2 = k

        Fraction m1 = new Fraction(1, 1);  // 小方块质量
        Fraction m2 = new Fraction(k, 1);  // 大方块质量

        // 计算 m1 + m2
        Fraction m1_plus_m2 = m1.add(m2);

        // 计算 m1 - m2
        Fraction m1_minus_m2 = m1.subtract(m2);

        // 计算 m2 - m1
        Fraction m2_minus_m1 = m2.subtract(m1);

        // 计算 2*m1
        Fraction two_m1 = m1.enlarge(2);

        // 计算 2*m2
        Fraction two_m2 = m2.enlarge(2);

        // 计算小方块碰撞后速度: u' = [(m1 - m2)*u + 2*m2*v] / (m1 + m2)
        Fraction term1 = m1_minus_m2.multiply(currU);
        Fraction term2 = two_m2.multiply(currV);
        Fraction newU = term1.add(term2).divide(m1_plus_m2);

        // 计算大方块碰撞后速度: v' = [2*m1*u + (m2 - m1)*v] / (m1 + m2)
        Fraction term3 = two_m1.multiply(currU);
        Fraction term4 = m2_minus_m1.multiply(currV);
        Fraction newV = term3.add(term4).divide(m1_plus_m2);

        return new Fraction[]{newU, newV};
    }

    /*
     * 如果大方块质量是小方块的k倍，小方块开始静止，大方块从右边以速度-1撞向小方块，小方块左侧有墙：
     * 求总共发生撞击的次数，和大小方块大的收尾速度（有正负号）。
     * 次数、小方块的收尾速度、大方块的收尾速度都是一个分数对象，放在一个一维数组中返回。
     */
    public static Fraction[] collideThorough(int k) {
        Fraction u = new Fraction(0, 1);     // 小方块初始速度
        Fraction v = new Fraction(-1, 1);    // 大方块初始速度
        Fraction collisionCount = new Fraction(0, 1);

        while (true) {
            // 检查两方块是否会碰撞
            if (v.compareTo(u) < 0) {
                // 两方块碰撞
                Fraction[] newSpeeds = collideOnce(k, u, v);
                u = newSpeeds[0];
                v = newSpeeds[1];
                collisionCount = collisionCount.add(new Fraction(1, 1));
            }
            // 检查是否会撞墙
            else if (u.compareTo(new Fraction(0, 1)) < 0) {
                // 小方块与墙碰撞
                u = u.negate();
                collisionCount = collisionCount.add(new Fraction(1, 1));
            }
            // 结束条件：v ≥ u ≥ 0
            else {
                break;
            }
        }

        return new Fraction[]{collisionCount, u, v};
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("There need one integer as the parameter.");
            return;
        }
        int k = 2;
        try {
            k = Integer.parseInt(args[0]);
        } catch(NumberFormatException nfe) {
            System.out.println("The first parameter must be an integer.");
            return;
        }
        Fraction[] terminal = collideThorough(k);
        System.out.println("Total collision times: ");
        System.out.println(terminal[0].getNumerator());
        System.out.println();
        System.out.println("Terminal velocity of the smaller cube:");
        System.out.println("" + terminal[1] + " ## " + terminal[1].estimate());
        System.out.println();
        System.out.println("Terminal velocity of the larger cube:");
        System.out.println("" + terminal[2] + " ## " + terminal[2].estimate());
        System.out.println();
    }
}