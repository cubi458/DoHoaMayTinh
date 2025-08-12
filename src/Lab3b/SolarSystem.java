package Lab3b;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import javax.swing.*;

public class SolarSystem extends GLJPanel implements GLEventListener {
    private GLU glu;
    private GLUquadric quad;
    private float alpha = 0;
    private final float earthDistance = 6.0f;

    public SolarSystem() {
        this.addGLEventListener(this);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // Khởi tạo GLU và tạo vật thể dạng lưới (wireframe)
        glu = new GLU();
        quad = glu.gluNewQuadric();
        glu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);

        // Bật depth test và màu nền
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glClearColor(1f, 1f, 1f, 1f); // màu nền trắng
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        // Camera nhìn từ trên xuống
        glu.gluLookAt(
                0.0f, 10.0f, 10.0f,   // eye position
                0.0f, 0.0f, 0.0f,     // center point
                0.0f, 1.0f, 0.0f      // up direction
        );

        // -------- Mặt trời (Sun) --------
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f); // đỏ
        glu.gluSphere(quad, 3.0f, 20, 20);

        // -------- Trái đất (Earth) --------
        gl.glRotatef(alpha, 0.0f, 1.0f, 0.0f); // Trái đất quay quanh mặt trời
        gl.glPushMatrix();
        gl.glTranslatef(earthDistance, 0.0f, 0.0f);
        gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f); // xanh
        glu.gluSphere(quad, 1.0f, 10, 10);

        // -------- Mặt trăng (Moon) --------
        gl.glRotatef(alpha * 3, 0.0f, 1.0f, 0.0f); // Mặt trăng quay quanh trái đất
        gl.glTranslatef(2.0f, 0.0f, 0.0f);
        gl.glColor4f(0.6f, 0.6f, 0.6f, 1.0f); // xám
        glu.gluSphere(quad, 0.3f, 10, 10);
        gl.glPopMatrix();

        // Tăng góc quay
        alpha += 1.5f;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        if (height == 0) height = 1;
        float aspect = (float) width / height;

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, aspect, 1.0, 100.0);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // không cần xử lý gì thêm
    }

    // ---------- Hàm main ----------
    public static void main(String[] args) {
        JFrame frame = new JFrame("Hệ mặt trời đơn giản - Wireframe");
        SolarSystem panel = new SolarSystem();
        panel.setPreferredSize(new java.awt.Dimension(800, 600));

        frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // Tạo animator để cập nhật màn hình liên tục
        FPSAnimator animator = new FPSAnimator(panel, 60); // 60 FPS
        animator.start();
    }
}

