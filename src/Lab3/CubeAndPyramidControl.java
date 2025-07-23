package Lab3;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.event.*;

public class CubeAndPyramidControl extends JFrame implements GLEventListener, KeyListener {
    private float rotateX = 0, rotateY = 0, rotateZ = 0;
    private GLU glu = new GLU();

    public CubeAndPyramidControl() {
        setTitle("Cube & Pyramid - Rotate with Keyboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Fix Java 23 crash
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(caps);

        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        canvas.setFocusable(true);
        add(canvas);

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CubeAndPyramidControl().setVisible(true));
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glClearColor(0f, 0f, 0f, 1f); // Nền đen
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        glu.gluLookAt(0, 0, 8, 0, 0, 0, 0, 1, 0);

        // Áp dụng xoay theo trục
        gl.glRotatef(rotateX, 1, 0, 0);
        gl.glRotatef(rotateY, 0, 1, 0);
        gl.glRotatef(rotateZ, 0, 0, 1);

        drawPyramid(gl);
        gl.glTranslatef(3f, 0, 0); // dịch cube qua phải
        drawCube(gl);
    }

    private void drawCube(GL2 gl) {
        gl.glBegin(GL2.GL_QUADS);

        // Front - Green
        gl.glColor3f(0f, 1f, 0f);
        gl.glVertex3f(-1, -1, 1);
        gl.glVertex3f(1, -1, 1);
        gl.glVertex3f(1, 1, 1);
        gl.glVertex3f(-1, 1, 1);

        // Back - Yellow
        gl.glColor3f(1f, 1f, 0f);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f(-1, 1, -1);
        gl.glVertex3f(1, 1, -1);
        gl.glVertex3f(1, -1, -1);

        // Left - Red
        gl.glColor3f(1f, 0f, 0f);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f(-1, -1, 1);
        gl.glVertex3f(-1, 1, 1);
        gl.glVertex3f(-1, 1, -1);

        // Right - Cyan
        gl.glColor3f(0f, 1f, 1f);
        gl.glVertex3f(1, -1, -1);
        gl.glVertex3f(1, 1, -1);
        gl.glVertex3f(1, 1, 1);
        gl.glVertex3f(1, -1, 1);

        // Top - Magenta
        gl.glColor3f(1f, 0f, 1f);
        gl.glVertex3f(-1, 1, -1);
        gl.glVertex3f(-1, 1, 1);
        gl.glVertex3f(1, 1, 1);
        gl.glVertex3f(1, 1, -1);

        // Bottom - Blue
        gl.glColor3f(0f, 0f, 1f);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f(1, -1, -1);
        gl.glVertex3f(1, -1, 1);
        gl.glVertex3f(-1, -1, 1);

        gl.glEnd();
    }

    private void drawPyramid(GL2 gl) {
        gl.glBegin(GL2.GL_TRIANGLES);

        // Front - Red
        gl.glColor3f(1f, 0f, 0f);
        gl.glVertex3f(0, 1, 0);
        gl.glVertex3f(-1, -1, 1);
        gl.glVertex3f(1, -1, 1);

        // Right - Green
        gl.glColor3f(0f, 1f, 0f);
        gl.glVertex3f(0, 1, 0);
        gl.glVertex3f(1, -1, 1);
        gl.glVertex3f(1, -1, -1);

        // Back - Blue
        gl.glColor3f(0f, 0f, 1f);
        gl.glVertex3f(0, 1, 0);
        gl.glVertex3f(1, -1, -1);
        gl.glVertex3f(-1, -1, -1);

        // Left - Yellow
        gl.glColor3f(1f, 1f, 0f);
        gl.glVertex3f(0, 1, 0);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f(-1, -1, 1);

        gl.glEnd();

        // Đáy hình chóp
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor3f(1f, 0f, 1f); // Magenta
        gl.glVertex3f(-1, -1, 1);
        gl.glVertex3f(1, -1, 1);
        gl.glVertex3f(1, -1, -1);
        gl.glVertex3f(-1, -1, -1);
        gl.glEnd();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        if (h == 0) h = 1;
        float aspect = (float) w / h;

        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45, aspect, 1, 100);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override public void dispose(GLAutoDrawable drawable) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_X -> rotateX += 5;
            case KeyEvent.VK_Y -> rotateY += 5;
            case KeyEvent.VK_Z -> rotateZ += 5;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
