package Lab2;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.event.*;

public class CubeControl extends JFrame implements GLEventListener, KeyListener {
    private GLU glu = new GLU(); // Đối tượng hỗ trợ các hàm tiện ích OpenGL
    private float x = 0.0f, y = 0.0f; // Tọa độ di chuyển của cube
    private boolean wireframe = true; // Biến kiểm tra có phải đang ở chế độ wireframe hay không

    public CubeControl() {
        // Thiết lập cửa sổ Swing
        setTitle("Cube Control - Wireframe and Color");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Khởi tạo GLCanvas cho OpenGL
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this); // lắng nghe các sự kiện OpenGL
        canvas.addKeyListener(this);     // lắng nghe sự kiện bàn phím
        canvas.setFocusable(true);       // nhận focus để lắng nghe phím
        add(canvas);                     // thêm canvas vào JFrame

        // Tạo animator chạy 60 FPS
        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    public static void main(String[] args) {
        // Chạy chương trình trong thread sự kiện
        SwingUtilities.invokeLater(() -> new CubeControl().setVisible(true));
    }

    // =========================== GLEventListener methods ===========================

    @Override
    public void init(GLAutoDrawable drawable) {
        // Khởi tạo OpenGL (chạy 1 lần)
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_DEPTH_TEST); // Bật chế độ kiểm tra độ sâu (3D)
        gl.glClearColor(1f, 1f, 1f, 1f); // Nền trắng
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // Hàm render chính (gọi liên tục mỗi frame)
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // Xóa buffer
        gl.glLoadIdentity();

        // Thiết lập camera
        glu.gluLookAt(0, 0, 5,  // Vị trí camera
                0, 0, 0,  // Nhìn vào gốc tọa độ
                0, 1, 0); // Hướng lên là trục Y

        // Dịch chuyển cube theo x, y
        gl.glTranslatef(x, y, 0);

        // Chế độ vẽ: wireframe hay tô màu
        if (wireframe) {
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE); // Vẽ khung (wireframe)
        } else {
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL); // Tô đầy mặt
        }

        drawCube(gl); // Vẽ cube
    }

    private void drawCube(GL2 gl) {
        // Vẽ một hình lập phương với mỗi mặt 1 màu
        gl.glBegin(GL2.GL_QUADS);

        // --- Mỗi mặt là một màu và 4 đỉnh (counter-clockwise) ---

        // Mặt trước - đỏ
        gl.glColor3f(1, 0, 0);
        gl.glVertex3f(-1, -1,  1);
        gl.glVertex3f( 1, -1,  1);
        gl.glVertex3f( 1,  1,  1);
        gl.glVertex3f(-1,  1,  1);

        // Mặt sau - xanh lá
        gl.glColor3f(0, 1, 0);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f(-1,  1, -1);
        gl.glVertex3f( 1,  1, -1);
        gl.glVertex3f( 1, -1, -1);

        // Mặt trái - xanh dương
        gl.glColor3f(0, 0, 1);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f(-1, -1,  1);
        gl.glVertex3f(-1,  1,  1);
        gl.glVertex3f(-1,  1, -1);

        // Mặt phải - vàng
        gl.glColor3f(1, 1, 0);
        gl.glVertex3f(1, -1, -1);
        gl.glVertex3f(1,  1, -1);
        gl.glVertex3f(1,  1,  1);
        gl.glVertex3f(1, -1,  1);

        // Mặt trên - cam
        gl.glColor3f(1, 0.5f, 0);
        gl.glVertex3f(-1, 1, -1);
        gl.glVertex3f(-1, 1,  1);
        gl.glVertex3f( 1, 1,  1);
        gl.glVertex3f( 1, 1, -1);

        // Mặt dưới - tím
        gl.glColor3f(1, 0, 1);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f( 1, -1, -1);
        gl.glVertex3f( 1, -1,  1);
        gl.glVertex3f(-1, -1,  1);

        gl.glEnd();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        // Gọi khi thay đổi kích thước cửa sổ
        GL2 gl = drawable.getGL().getGL2();
        if (h == 0) h = 1;
        float aspect = (float) w / h;

        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        // Thiết lập phối cảnh 3D
        glu.gluPerspective(45, aspect, 1, 100);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Giải phóng tài nguyên nếu cần (không dùng ở đây)
    }

    // =========================== KeyListener ===========================

    @Override
    public void keyPressed(KeyEvent e) {
        // Xử lý phím di chuyển và toggle wireframe
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> x -= 0.1f;
            case KeyEvent.VK_RIGHT -> x += 0.1f;
            case KeyEvent.VK_UP -> y += 0.1f;
            case KeyEvent.VK_DOWN -> y -= 0.1f;
            case KeyEvent.VK_SPACE -> wireframe = !wireframe; // Đổi chế độ vẽ
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}

