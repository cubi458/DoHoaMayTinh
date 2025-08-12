package Lab3b;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import javax.swing.*;

public class Draw extends GLJPanel implements GLEventListener {

    private GLU glu;
    private GLUquadric quad;

    float roll = 0, pitch = 30, yaw = -30;
    float cx = 0.0f, cy = 0.0f, cz = 100.0f;

    public Draw() {
        this.addGLEventListener(this);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        glu = new GLU();
        quad = glu.gluNewQuadric();
        glu.gluQuadricDrawStyle(quad, GLU.GLU_LINE); // Wireframe style

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f); // Background white
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        if (height == 0) height = 1;
        float aspect = (float) width / height;

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, aspect, 1.0, 200.0);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        // Đặt camera
        gl.glTranslatef(-cx, -cy, -cz);
        gl.glRotatef(roll, 0.0f, 0.0f, 1.0f);
        gl.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(yaw, 0.0f, 1.0f, 0.0f);

        // ---------- HEAD ----------
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        glu.gluSphere(quad, 30.0f, 15, 15); // đầu

        // ---------- NOSE ----------
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, 30.0f); // trước mặt
        glu.gluCylinder(quad, 5.0f, 0.0f, 15.0f, 10, 10); // mũi nhọn
        gl.glPopMatrix();

        // ---------- RIGHT EAR ----------
        gl.glPushMatrix();
        gl.glTranslatef(30.0f, 0.0f, 0.0f); // tai phải
        glu.gluPartialDisk(quad, 0.0f, 10.0f, 10, 10, 0.0f, 180.0f);
        gl.glPopMatrix();

        // ---------- LEFT EAR ----------
        gl.glPushMatrix();
        gl.glTranslatef(-30.0f, 0.0f, 0.0f); // tai trái
        glu.gluPartialDisk(quad, 0.0f, 10.0f, 10, 10, 0.0f, -180.0f);
        gl.glPopMatrix();

        // ---------- HAT ----------
        gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f); // xanh
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 30.0f, 0.0f); // đỉnh đầu
        gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f); // xoay để dựng lên

        glu.gluDisk(quad, 10.0f, 25.0f, 10, 10); // vành mũ dưới
        glu.gluCylinder(quad, 10.0f, 10.0f, 10.0f, 10, 10); // thân mũ

        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, 10.0f); // lên trên để vẽ nắp mũ
        glu.gluDisk(quad, 0.0f, 10.0f, 10, 10); // vành mũ trên
        gl.glPopMatrix();

        gl.glPopMatrix();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Clean up nếu cần
    }

    // ---------- MAIN ----------
    public static void main(String[] args) {
        JFrame frame = new JFrame("GLU 3D Wireframe Face");
        Draw panel = new Draw();
        panel.setPreferredSize(new java.awt.Dimension(800, 600));

        frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        FPSAnimator animator = new FPSAnimator(panel, 60); // 60 FPS
        animator.start();
    }
}
