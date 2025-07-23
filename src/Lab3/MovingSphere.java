package Lab3;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;

public class MovingSphere extends JFrame implements GLEventListener {
    private GLU glu = new GLU();
    private float sphereX = -2.0f; // bắt đầu góc trên trái
    private float sphereY = 1.5f;
    private float sphereZ = 0.0f;

    public MovingSphere() {
        setTitle("Wireframe Sphere Movement");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
        add(canvas);

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MovingSphere().setVisible(true));
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glClearColor(1f, 1f, 1f, 1f); // nền trắng
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        // Camera
        glu.gluLookAt(0, 0, 5,  // eye
                0, 0, 0,  // center
                0, 1, 0); // up

        // Di chuyển và vẽ hình cầu wireframe
        gl.glPushMatrix();
        gl.glTranslatef(sphereX, sphereY, sphereZ);
        gl.glColor3f(0.0f, 0.0f, 0.0f); // màu đen

        GLUquadric quadric = glu.gluNewQuadric();
        glu.gluQuadricDrawStyle(quadric, GLU.GLU_LINE); // wireframe
        glu.gluSphere(quadric, 0.3, 20, 20); // bán kính, chi tiết
        glu.gluDeleteQuadric(quadric);
        gl.glPopMatrix();

        // Cập nhật vị trí (tốc độ vừa phải)
        sphereX += 0.01f;
        sphereY -= 0.005f;

        // Khi đến cạnh phải/dưới thì reset lại
        if (sphereX > 2.5f || sphereY < -2.5f) {
            sphereX = -2.0f;
            sphereY = 1.5f;
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        if (height == 0) height = 1;
        float aspect = (float) width / height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, aspect, 1.0f, 100.0f);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Không cần giải phóng gì ở đây
    }
}
