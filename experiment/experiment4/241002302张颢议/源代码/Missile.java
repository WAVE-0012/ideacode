import java.awt.*;
import java.util.Random;

public class Missile {
    private double x, y;
    private double vx, vy;
    private boolean isFromLeft;
    private double targetY;
    private Random random = new Random();
    private int size = 30;

    public Missile(double windowWidth, double windowHeight) {
        isFromLeft = random.nextBoolean();
        y = random.nextDouble() * 0.8;
        targetY = y + random.nextDouble() * (1.0 - y);
        double deltaY = targetY - y;

        if (isFromLeft) {
            x = 0;
            vx = 0.003 + random.nextDouble() * 0.002;
            vy = deltaY * vx;
        } else {
            x = 1;
            vx = -(0.003 + random.nextDouble() * 0.002);
            vy = deltaY * (-vx);
        }
    }

    public void update(double speedMultiplier) {
        x += vx * speedMultiplier;
        y += vy * speedMultiplier;
    }

    public boolean isOutOfBounds() {
        return x < -0.1 || x > 1.1;
    }

    public void draw(Graphics g, int panelWidth, int panelHeight) {
        int px = (int)(x * panelWidth);
        int py = (int)(y * panelHeight);

        if (px >= 0 && px <= panelWidth && py >= 0 && py <= panelHeight) {
            g.setColor(Color.BLUE);
            g.fillOval(px - size/2, py - size/2, size, size);
            g.setColor(new Color(100, 150, 255));
            g.drawOval(px - size/2, py - size/2, size, size);
            g.setColor(new Color(150, 200, 255));
            g.fillOval(px - size/4, py - size/4, size/3, size/3);
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public Rectangle getBounds(int panelWidth, int panelHeight) {
        int px = (int)(x * panelWidth);
        int py = (int)(y * panelHeight);
        return new Rectangle(px - size/2, py - size/2, size, size);
    }
}