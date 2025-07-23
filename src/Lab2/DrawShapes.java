package Lab2;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DrawShapes extends GLJPanel implements GLEventListener {
    private static final int WIDTH = 800, HEIGHT = 480, FPS = 60;
    private GLU glu;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GLJPanel canvas = new DrawShapes();
            canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
            JFrame frame = new JFrame("Fixed Finnish Flag and Red Star");
            frame.getContentPane().add(canvas);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    new Thread(() -> {
                        if (animator.isStarted()) animator.stop();
                        System.exit(0);
                    }).start();
                }
            });
            frame.pack();
            frame.setVisible(true);
            animator.start();
        });
    }

    public DrawShapes() {
        this.addGLEventListener(this);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        gl.glClearColor(1f, 1f, 1f, 1f); // nền trắng
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        if (height == 0) height = 1;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(0, width, 0, height); // hệ tọa độ 2D
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();

        // === 1. Vẽ quốc kỳ Phần Lan (dấu cộng đều nhau) ===
        float flagX = 50, flagY = 120;
        float flagWidth = 300, flagHeight = 200;

        // Vẽ nền trắng (nền đã trắng rồi)
        // Vẽ dấu cộng xanh lam đều nhau
        gl.glColor3f(0f, 0f, 1f); // xanh lam

        float barThickness = 30; // độ dày ngang = dọc

        // Dải ngang
        drawRect(gl,
                flagX,
                flagY + flagHeight / 2 - barThickness / 2,
                flagWidth,
                barThickness
        );

        // Dải dọc
        drawRect(gl,
                flagX + flagWidth / 3 - barThickness / 2,
                flagY,
                barThickness,
                flagHeight
        );

        // === 2. Vẽ ngôi sao đỏ đúng 5 cánh ===
        gl.glColor3f(1f, 0f, 0f); // đỏ
        drawStar(gl, 600, 220, 80); // centerX, centerY, radius
    }

    private void drawRect(GL2 gl, float x, float y, float width, float height) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y + height);
        gl.glVertex2f(x, y + height);
        gl.glEnd();
    }

    private void drawStar(GL2 gl, float cx, float cy, float outerRadius) {
        int numPoints = 5;
        double angleStep = Math.PI / numPoints;
        double startAngle = Math.PI / 2;

        float innerRadius = outerRadius * 0.4f; // bạn có thể điều chỉnh tỉ lệ này

        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glVertex2f(cx, cy); // Tâm ngôi sao (để tạo phần rỗng không cần vẽ)
        for (int i = 0; i <= numPoints * 2; i++) {
            double angle = startAngle + i * angleStep;
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            float x = cx + (float) (Math.cos(angle) * radius);
            float y = cy + (float) (Math.sin(angle) * radius);
            gl.glVertex2f(x, y);
        }
        gl.glEnd();
    }


    @Override
    public void dispose(GLAutoDrawable drawable) {
    }
}