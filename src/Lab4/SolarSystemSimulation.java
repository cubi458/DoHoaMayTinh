package Lab4;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SolarSystemSimulation implements GLEventListener, KeyListener {
    private GLU glu = new GLU();
    private boolean isSolid = false;
    private boolean isRunning = false;
    private int day = 0; // Số ngày đã trôi qua
    private int dayStep = 1; // Số ngày tăng mỗi lần nhấn 'd'

    public static void main(String[] args) {
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(capabilities);
        SolarSystemSimulation sim = new SolarSystemSimulation();
        canvas.addGLEventListener(sim);
        canvas.addKeyListener(sim);

        JFrame frame = new JFrame("Solar System Simulation");
        frame.setSize(900, 700);
        frame.add(canvas);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        canvas.requestFocusInWindow();

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0f, 0f, 0.1f, 1f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0f, 0f, -30f);
        gl.glRotatef(20, 1f, 0f, 0f);

        // Mặt trời
        gl.glPushMatrix();
        gl.glColor3f(1f, 0.8f, 0f); // vàng
        drawSphere(gl, 2.5f);
        gl.glPopMatrix();

        // Trái đất quay quanh mặt trời
        gl.glPushMatrix();
        gl.glRotatef(day * 360f / 365f, 0f, 1f, 0f); // Quay quanh mặt trời
        gl.glTranslatef(10f, 0f, 0f); // Bán kính quỹ đạo
        gl.glRotatef(day * 360f, 0f, 1f, 0f); // Trái đất tự quay quanh trục
        gl.glColor3f(0.2f, 0.5f, 1f); // xanh dương
        drawSphere(gl, 1f);

        // Mặt trăng quay quanh trái đất
        gl.glPushMatrix();
        gl.glRotatef(day * 360f / 30f, 0f, 1f, 0f); // Quay quanh trái đất
        gl.glTranslatef(2.5f, 0f, 0f); // Bán kính quỹ đạo mặt trăng
        gl.glColor3f(0.8f, 0.8f, 0.8f); // xám
        drawSphere(gl, 0.3f);
        gl.glPopMatrix();

        gl.glPopMatrix(); // Kết thúc vẽ trái đất và mặt trăng
    }

    private void drawSphere(GL2 gl, float radius) {
        GLUquadric quad = glu.gluNewQuadric();
        if (isSolid) {
            glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        } else {
            glu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
        }
        glu.gluSphere(quad, radius, 32, 32);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL2 gl = drawable.getGL().getGL2();
        float aspect = (float) width / height;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, aspect, 1.0, 100.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (c == 'd' || c == 'D') {
            isRunning = !isRunning;
        } else if (c == 's' || c == 'S') {
            isSolid = true;
        } else if (c == 'w' || c == 'W') {
            isSolid = false;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    // Tăng ngày nếu đang chạy
    public void update() {
        if (isRunning) {
            day = (day + dayStep) % 365;
        }
    }
} 