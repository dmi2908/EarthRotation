import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Math;

@SuppressWarnings("serial")
public class EarthRotation extends Canvas implements Runnable {

    private Thread thread;
    private JFrame frame;
    public final static int WIDTH = 800, HEIGHT = 600;
    private static String title = "Earth rotation";
    private static boolean running = false;
    private BufferedImage sunImage;
    private BufferedImage earthImage;

    public EarthRotation() {
        this.frame = new JFrame();
        this.frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        loadImages();
    }

    private void loadImages() {
        try {
            sunImage = ImageIO.read(new File("./res/sun.png"));
            earthImage = ImageIO.read(new File("./res/earth.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EarthRotation display = new EarthRotation();
        display.frame.setTitle(title);
        display.frame.add(display);
        display.frame.pack();
        display.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        display.frame.setLocationRelativeTo(null);
        display.frame.setResizable(false);
        display.frame.setVisible(true);
        display.start();
    }

    public synchronized void start() {
        running = true;
        this.thread = new Thread(this, "Display");
        this.thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            this.thread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double ns = 1e9 / 60;
        double delta = 0;
        int frames = 0;

        while(running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1) {
                update();
                delta--;
                render();
                frames++;
            }
            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                this.frame.setTitle(title + " | " + frames + " FPS");
                frames = 0;
            }
        }
        stop();
    }

    // random point on circle;
    int x = 487;
    int y = 29;
    //circle center and radius
    double r,a = 367,b = 250;
    double t = 0.01;
    int timer = 1000 / 50;

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        //Background
        g.setColor(Color.black);
        g.fillRect(-1, -1, WIDTH, HEIGHT);

        //Draw here!
        g.setFont(new Font("Comic Sans MS", 10, 20));

        g.drawImage(sunImage, (int)a, (int)b,null);
        g.drawImage(earthImage, x, y,null);
        getCircleCoordinates();
        g.dispose();

        bs.show();
        timer--;
        if(timer <= 0) {
            timer = 1000 / 50;
        }
    }
    public void update() {}

    public void getCircleCoordinates() {
        r = Math.sqrt(Math.pow(x - a,2) + Math.pow(y - b,2));
        x = (int) (a + r * Math.cos(t));
        y = (int) (b + r * Math.sin(t));
        t = t < 3.14 * 2 ? t + 0.1 : 0.01;
    }
}
