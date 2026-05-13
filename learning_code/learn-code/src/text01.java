import java.util.Scanner;
//public class text01 {
//    public static void main(String[] args){
//        Scanner s = new Scanner(System.in);
//        System.out.println("请输入一个价钱,输出相应的折扣数：");
//        int x = s.nextInt();
//        if(x<1000){
//            System.out.println("价格为："+ x);
//        }else if(x<2000){
//            System.out.println("价格为："+ x*0.9);
//        }else if(x<3000){
//            System.out.println("价格为："+ x*0.8);
//        }else{
//            System.out.println("价格为："+ x*0.7);
//        }
//    }
//}
//public class text01 {
//    public static void main(String[] args){
//        Scanner s = new Scanner(System.in);
//        System.out.println("请输入工资数计算征税值：");
//        double wage = s.nextDouble();
//        if(wage<200){
//            System.out.println("免征税");
//        }else if(wage<400){
//            System.out.println("按超出的3％征收税款，为："+ (wage-200)*0.03);
//        }else if(wage<5000){
//            System.out.println("按超出的4%征收税款，为："+ (wage-400)*0.04);
//        }else{
//            System.out.println("按超出的4%征收税款，为："+ (wage-400)*0.05);
//        }
//    }
//}
//public class text01 {
//    public static void main(String[] args) {
//        Scanner s = new Scanner(System.in);
//        System.out.println("请输入工时数：");
//        double hours = s.nextDouble();
//        double tax = 0;
//        double salary = 0;
//        double finalSalary = 0;
//        if (hours <= 40) {
//            salary = hours * 10;
//        } else {
//            salary = 40 * 10 + (hours - 40) * 1.5 * 10;
//        }
//        if (salary < 300) {
//            tax = salary * 0.15;
//        } else if (salary < 450) {
//            tax = 300 * 0.15 + (salary - 300) * 0.2;
//        } else {
//            tax = 300 * 0.15 + 150 * 0.2 + (salary - 450) * 0.25;
//        }
//        finalSalary = salary - tax;
//        System.out.println("最后的净工资为：" + finalSalary + "收税为：" + tax);
//    }
//}
/*
    设faHeight为其父身高，moHeight为其母身高，身高预测公式为
    男性成人时身高=(faHeight +moHeight）* 0。54cm
    女性成人时身高=(faHeight*0。923 + moHeight）／2cm
    此外，如果喜爱体育锻炼，那么可增加身高2%；
    如果有良好的卫生饮食习惯，那么可增加1.5%
 */
public class text01 {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        double myHeight=0;
        double faHeight=0;
        double moHeight=0;
        int sex = 0;//男1 女0
        int sport = 0;//喜欢运动1,否则0;
        int diet = 0;//饮食健康1，否则0;

        System.out.println("请输入父亲的身高：");
        faHeight = s.nextDouble();

        System.out.println("请输入母亲的身高：");
        moHeight = s.nextDouble();

        System.out.println("请输入性别（1是男性）：");
        sex = s.nextInt();

        System.out.println("请输入是否喜欢运动（1表示喜欢）：");
        sport = s.nextInt();

        System.out.println("请输入饮食是否健康（1表示健康）：");
        diet = s.nextInt();
        if(sex == 1){
            myHeight = (faHeight +moHeight)* 0.54;
        }else{
            myHeight = (faHeight*0.923 + moHeight)/2;
        }
        if(sport == 1){
            myHeight*=(1+0.02);
        }
        if(diet == 1){
            myHeight+=(1+0.015);
        }
        System.out.println("预测身高为："+myHeight);
    }
}