package Lab3;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TeapotViewer extends JFrame implements GLEventListener, KeyListener {
    private GLCanvas canvas;
    private GLU glu;
    private GLUT glut;
    private FPSAnimator animator;

    // Biến quay
    private float rotateX = 0.0f;
    private float rotateY = 0.0f;
    private float rotateZ = 0.0f;

    public TeapotViewer() {
        setTitle("3D Teapot - Sử dụng phím mũi tên để quay");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Khởi tạo OpenGL canvas
        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        canvas.setFocusable(true);

        add(canvas);

        // Khởi tạo animator
        animator = new FPSAnimator(canvas, 60);

        setVisible(true);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        glut = new GLUT();

        // Thiết lập màu nền xanh dương
        gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

        // Kích hoạt depth testing
        gl.glEnable(GL2.GL_DEPTH_TEST);

        // Thiết lập ánh sáng
        setupLighting(gl);

        // Thiết lập material cho ấm trà
        setupMaterial(gl);
    }

    private void setupLighting(GL2 gl) {
        // Kích hoạt ánh sáng
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);

        // Thiết lập ánh sáng môi trường
        float[] ambient = {0.2f, 0.2f, 0.2f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);

        // Thiết lập ánh sáng khuếch tán
        float[] diffuse = {1.0f, 1.0f, 1.0f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);

        // Thiết lập ánh sáng phản chiếu
        float[] specular = {1.0f, 1.0f, 1.0f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular, 0);

        // Vị trí ánh sáng
        float[] position = {5.0f, 5.0f, 5.0f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);
    }

    private void setupMaterial(GL2 gl) {
        // Màu vật liệu - xám đậm như trong hình
        float[] materialAmbient = {0.3f, 0.3f, 0.3f, 1.0f};
        float[] materialDiffuse = {0.5f, 0.5f, 0.5f, 1.0f};
        float[] materialSpecular = {0.8f, 0.8f, 0.8f, 1.0f};
        float[] materialEmission = {0.3f, 0.3f, 0.0f, 1.0f}; // Ánh sáng vàng nhẹ

        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, materialAmbient, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, materialDiffuse, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, materialSpecular, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, materialEmission, 0);
        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 50.0f);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // Xóa buffer
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        // Reset matrix
        gl.glLoadIdentity();

        // Di chuyển camera
        glu.gluLookAt(0, 0, 5, 0, 0, 0, 0, 1, 0);

        // Áp dụng các phép quay
        gl.glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(rotateZ, 0.0f, 0.0f, 1.0f);

        // Vẽ ấm trà
        glut.glutSolidTeapot(1.0f);

        // Flush
        gl.glFlush();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        // Thiết lập viewport
        gl.glViewport(0, 0, width, height);

        // Thiết lập projection matrix
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        // Thiết lập perspective
        glu.gluPerspective(45.0, (double) width / height, 0.1, 100.0);

        // Chuyển về modelview matrix
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        if (animator != null) {
            animator.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                rotateY -= 5.0f;
                break;
            case KeyEvent.VK_RIGHT:
                rotateY += 5.0f;
                break;
            case KeyEvent.VK_UP:
                rotateX -= 5.0f;
                break;
            case KeyEvent.VK_DOWN:
                rotateX += 5.0f;
                break;
            case KeyEvent.VK_Q:
                rotateZ -= 5.0f;
                break;
            case KeyEvent.VK_E:
                rotateZ += 5.0f;
                break;
            case KeyEvent.VK_R:
                // Reset về vị trí ban đầu
                rotateX = 0.0f;
                rotateY = 0.0f;
                rotateZ = 0.0f;
                break;
        }
        canvas.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Không cần xử lý
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Không cần xử lý
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new TeapotViewer();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Lỗi khởi tạo OpenGL. Vui lòng kiểm tra JOGL đã được cài đặt đúng.");
            }
        });
    }
}
