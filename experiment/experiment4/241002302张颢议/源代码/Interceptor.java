import java.awt.*;

public class Interceptor {
    private double x, y;
    private double vx, vy;
    private boolean active = true;
    private int size = 18;  // 调小尺寸

    public Interceptor(double speed, int dir, double windowWidth, double windowHeight) {
        x = 0.5;
        y = 0.95;

        double targetX = 0.5;
        double targetY = 0.0;

        if (dir == -3) { targetX = 0.0; targetY = 0.5; }
        else if (dir == -2) { targetX = 0.0; targetY = 0.0; }
        else if (dir == -1) { targetX = 0.25; targetY = 0.0; }
        else if (dir == 0) { targetX = 0.5; targetY = 0.0; }
        else if (dir == 1) { targetX = 0.75; targetY = 0.0; }
        else if (dir == 2) { targetX = 1.0; targetY = 0.0; }
        else if (dir == 3) { targetX = 1.0; targetY = 0.5; }

        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance > 0) {
            vx = (dx / distance) * speed;
            vy = (dy / distance) * speed;
        }
    }

    public void update(double speedMultiplier) {
        x += vx * speedMultiplier;
        y += vy * speedMultiplier;

        if (x < -0.1 || x > 1.1 || y < -0.1 || y > 1.1) {
            active = false;
        }
    }

    public void draw(Graphics g, int panelWidth, int panelHeight) {
        if (!active) return;
        int px = (int)(x * panelWidth);
        int py = (int)(y * panelHeight);

        if (px >= 0 && px <= panelWidth && py >= 0 && py <= panelHeight) {
            g.setColor(Color.RED);
            g.fillOval(px - size/2, py - size/2, size, size);
            g.setColor(Color.ORANGE);
            g.drawOval(px - size/2, py - size/2, size, size);
            g.setColor(new Color(255, 200, 100));
            g.fillOval(px - size/5, py - size/5, size/3, size/3);
        }
    }

    public boolean isActive() { return active; }

    public Rectangle getBounds(int panelWidth, int panelHeight) {
        int px = (int)(x * panelWidth);
        int py = (int)(y * panelHeight);
        return new Rectangle(px - size/2, py - size/2, size, size);
    }
}