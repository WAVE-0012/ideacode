import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GamePanel extends JPanel {
    private RedFlag parent;
    private List<Missile> missiles;
    private List<Interceptor> interceptors;
    private Random random;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private int currentSpeed = 5;
    private javax.swing.Timer updateTimer;

    public GamePanel(RedFlag parent) {
        this.parent = parent;
        missiles = Collections.synchronizedList(new ArrayList<>());
        interceptors = Collections.synchronizedList(new ArrayList<>());
        random = new Random();
        setFocusable(true);
        startMissileSpawner();
        setupUpdateTimer();
    }

    private void setupUpdateTimer() {
        updateTimer = new javax.swing.Timer(16, e -> {
            if (isRunning && !isPaused) {
                updateGame();
                repaint();
            }
        });
        updateTimer.start();
    }

    private void startMissileSpawner() {
        Thread spawnThread = new Thread(() -> {
            while (true) {
                if (isRunning && !isPaused) {
                    // 提高导弹生成频率
                    double lambda = 0.6;
                    double delay = -Math.log(1 - random.nextDouble()) / lambda;
                    int spawnDelay = (int)(delay * 1000);
                    spawnDelay = Math.max(spawnDelay, 300);  // 最小间隔300ms
                    spawnDelay = Math.min(spawnDelay, 1500); // 最大间隔1500ms

                    try {
                        Thread.sleep(spawnDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    SwingUtilities.invokeLater(() -> {
                        Missile newMissile = new Missile(getWidth(), getHeight());
                        missiles.add(newMissile);
                    });
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        spawnThread.setDaemon(true);
        spawnThread.start();
    }

    public void updateGame() {
        if (!isRunning || isPaused) return;

        double speedMultiplier = currentSpeed / 5.0;

        synchronized(missiles) {
            Iterator<Missile> iter = missiles.iterator();
            while (iter.hasNext()) {
                Missile m = iter.next();
                m.update(speedMultiplier);
                if (m.isOutOfBounds()) {
                    iter.remove();
                }
            }
        }

        synchronized(interceptors) {
            Iterator<Interceptor> iter = interceptors.iterator();
            while (iter.hasNext()) {
                Interceptor interceptor = iter.next();
                interceptor.update(speedMultiplier);
                if (!interceptor.isActive()) {
                    iter.remove();
                }
            }
        }

        checkCollisions();
    }

    private void checkCollisions() {
        synchronized(missiles) {
            synchronized(interceptors) {
                Iterator<Missile> missileIter = missiles.iterator();
                while (missileIter.hasNext()) {
                    Missile missile = missileIter.next();
                    Iterator<Interceptor> interceptorIter = interceptors.iterator();
                    while (interceptorIter.hasNext()) {
                        Interceptor interceptor = interceptorIter.next();
                        if (checkCollision(missile, interceptor)) {
                            missileIter.remove();
                            interceptorIter.remove();
                            parent.addHit();
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean checkCollision(Missile missile, Interceptor interceptor) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (panelWidth <= 0 || panelHeight <= 0) return false;

        Rectangle missileBounds = missile.getBounds(panelWidth, panelHeight);
        Rectangle interceptorBounds = interceptor.getBounds(panelWidth, panelHeight);

        return missileBounds.intersects(interceptorBounds);
    }

    public void fireInterceptor(double speed, int direction) {
        Interceptor interceptor = new Interceptor(speed, direction, getWidth(), getHeight());
        interceptors.add(interceptor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        // 使用默认背景

        // 绘制导弹
        synchronized(missiles) {
            for (Missile m : missiles) {
                m.draw(g, width, height);
            }
        }

        // 绘制拦截弹
        synchronized(interceptors) {
            for (Interceptor interceptor : interceptors) {
                interceptor.draw(g, width, height);
            }
        }
    }

    public void setRunning(boolean running) { this.isRunning = running; }
    public void setPaused(boolean paused) { this.isPaused = paused; }
    public void setCurrentSpeed(int speed) { this.currentSpeed = speed; }
}