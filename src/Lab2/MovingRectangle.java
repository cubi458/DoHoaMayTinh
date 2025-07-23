package Lab2;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MovingRectangle extends GLJPanel implements GLEventListener, MouseListener, MouseMotionListener {
    private static final String TITLE = "Moving Rectangle with Mouse";
    private static final int WIDTH = 640, HEIGHT = 480, FPS = 60;
    private GLU glu;

    // Vị trí chuột
    private float mouseX = 0;
    private float mouseY = 0;
    private boolean leftPressed = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GLJPanel canvas = new MovingRectangle();
            canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));

            FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
            JFrame frame = new JFrame(TITLE);
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

    public MovingRectangle() {
        this.addGLEventListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        gl.glClearColor(0f, 0f, 0f, 1f);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        if (height == 0) height = 1;
        float aspect = (float) width / height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(0, width, 0, height); // 2D
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();

        // Vẽ hình chữ nhật tại vị trí chuột
        float rectWidth = 50, rectHeight = 30;

        if (leftPressed) {
            gl.glColor3f(1.0f, 0.0f, 0.0f); // đỏ
        } else {
            gl.glColor3f(1.0f, 1.0f, 0.0f); // vàng
        }

        float x = mouseX;
        float y = mouseY;

        // Vẽ hình chữ nhật (lưu ý hệ trục OpenGL y từ dưới lên)
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + rectWidth, y);
        gl.glVertex2f(x + rectWidth, y + rectHeight);
        gl.glVertex2f(x, y + rectHeight);
        gl.glEnd();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    // Mouse events
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftPressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftPressed = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateMousePosition(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateMousePosition(e);
    }

    private void updateMousePosition(MouseEvent e) {
        // Lưu ý: OpenGL gốc tọa độ ở dưới, còn Java Swing ở trên => đảo y
        mouseX = e.getX();
        mouseY = getHeight() - e.getY();
        repaint(); // trigger vẽ lại
    }

    // Unused
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
