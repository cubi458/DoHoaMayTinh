package Lab4;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DiningTableViewer extends JFrame implements GLEventListener, KeyListener {
    private GLCanvas canvas;
    private GLU glu;
    private GLUT glut;
    private FPSAnimator animator;

    private float rotateX = -20.0f;
    private float rotateY = 30.0f;
    private float zoom = 8.0f;

    public DiningTableViewer() {
        setTitle("Bàn ăn kích thước W200cm x H100cm x D100cm - Phím mũi tên: quay, +/-: zoom");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);

        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        canvas.setFocusable(true);

        add(canvas);

        animator = new FPSAnimator(canvas, 60);

        setVisible(true);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        glut = new GLUT();

        // Thiết lập màu nền xám nhạt
        gl.glClearColor(0.95f, 0.95f, 0.95f, 1.0f);

        // Kích hoạt depth testing
        gl.glEnable(GL2.GL_DEPTH_TEST);

        // Thiết lập ánh sáng
        setupLighting(gl);

        // Kích hoạt smooth shading
        gl.glShadeModel(GL2.GL_SMOOTH);
    }

    private void setupLighting(GL2 gl) {
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);

        // Ánh sáng môi trường
        float[] ambient = {0.3f, 0.3f, 0.3f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);

        // Ánh sáng khuếch tán
        float[] diffuse = {0.8f, 0.8f, 0.8f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);

        // Ánh sáng phản chiếu
        float[] specular = {1.0f, 1.0f, 1.0f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular, 0);

        // Vị trí ánh sáng
        float[] position = {5.0f, 8.0f, 5.0f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);
    }

    private void setMaterial(GL2 gl, float[] ambient, float[] diffuse, float[] specular, float shininess) {
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);
        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, shininess);
    }

    private void drawCube(GL2 gl, float width, float height, float depth) {
        float w = width / 2.0f;
        float h = height / 2.0f;
        float d = depth / 2.0f;

        gl.glBegin(GL2.GL_QUADS);

        // Mặt trước
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(-w, -h, d);
        gl.glVertex3f(w, -h, d);
        gl.glVertex3f(w, h, d);
        gl.glVertex3f(-w, h, d);

        // Mặt sau
        gl.glNormal3f(0.0f, 0.0f, -1.0f);
        gl.glVertex3f(-w, -h, -d);
        gl.glVertex3f(-w, h, -d);
        gl.glVertex3f(w, h, -d);
        gl.glVertex3f(w, -h, -d);

        // Mặt trên
        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(-w, h, -d);
        gl.glVertex3f(-w, h, d);
        gl.glVertex3f(w, h, d);
        gl.glVertex3f(w, h, -d);

        // Mặt dưới
        gl.glNormal3f(0.0f, -1.0f, 0.0f);
        gl.glVertex3f(-w, -h, -d);
        gl.glVertex3f(w, -h, -d);
        gl.glVertex3f(w, -h, d);
        gl.glVertex3f(-w, -h, d);

        // Mặt phải
        gl.glNormal3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3f(w, -h, -d);
        gl.glVertex3f(w, h, -d);
        gl.glVertex3f(w, h, d);
        gl.glVertex3f(w, -h, d);

        // Mặt trái
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);
        gl.glVertex3f(-w, -h, -d);
        gl.glVertex3f(-w, -h, d);
        gl.glVertex3f(-w, h, d);
        gl.glVertex3f(-w, h, -d);

        gl.glEnd();
    }

    private void drawCylinder(GL2 gl, float radius, float height, int slices) {
        float angleStep = (float) (2 * Math.PI / slices);

        // Vẽ thân cylinder
        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 0; i <= slices; i++) {
            float angle = i * angleStep;
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);

            gl.glNormal3f(x/radius, 0, z/radius);
            gl.glVertex3f(x, 0, z);
            gl.glVertex3f(x, height, z);
        }
        gl.glEnd();

        // Vẽ đáy dưới
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glNormal3f(0, -1, 0);
        gl.glVertex3f(0, 0, 0);
        for (int i = 0; i <= slices; i++) {
            float angle = i * angleStep;
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            gl.glVertex3f(x, 0, z);
        }
        gl.glEnd();

        // Vẽ đáy trên
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glNormal3f(0, 1, 0);
        gl.glVertex3f(0, height, 0);
        for (int i = slices; i >= 0; i--) {
            float angle = i * angleStep;
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            gl.glVertex3f(x, height, z);
        }
        gl.glEnd();
    }

    private void drawTable(GL2 gl) {
        // Màu gỗ cho bàn
        float[] woodColor = {0.8f, 0.6f, 0.4f, 1.0f};
        float[] woodSpec = {0.3f, 0.3f, 0.3f, 1.0f};
        setMaterial(gl, woodColor, woodColor, woodSpec, 20.0f);

        // Kích thước bàn theo tỷ lệ (200cm x 100cm x 100cm)
        float tableWidth = 4.0f;   // 200cm
        float tableDepth = 2.0f;   // 100cm
        float tableHeight = 0.1f;  // Độ dày mặt bàn
        float legHeight = 2.0f;    // 100cm chiều cao chân bàn
        float legThickness = 0.1f; // Độ dày chân bàn

        // Vẽ mặt bàn
        gl.glPushMatrix();
        gl.glTranslatef(0, legHeight + tableHeight/2, 0);
        drawCube(gl, tableWidth, tableHeight, tableDepth);
        gl.glPopMatrix();

        // Vẽ 4 chân bàn
        float legOffsetX = tableWidth/2 - legThickness/2;
        float legOffsetZ = tableDepth/2 - legThickness/2;

        // Chân 1 (trước trái)
        gl.glPushMatrix();
        gl.glTranslatef(-legOffsetX, legHeight/2, legOffsetZ);
        drawCube(gl, legThickness, legHeight, legThickness);
        gl.glPopMatrix();

        // Chân 2 (trước phải)
        gl.glPushMatrix();
        gl.glTranslatef(legOffsetX, legHeight/2, legOffsetZ);
        drawCube(gl, legThickness, legHeight, legThickness);
        gl.glPopMatrix();

        // Chân 3 (sau trái)
        gl.glPushMatrix();
        gl.glTranslatef(-legOffsetX, legHeight/2, -legOffsetZ);
        drawCube(gl, legThickness, legHeight, legThickness);
        gl.glPopMatrix();

        // Chân 4 (sau phải)
        gl.glPushMatrix();
        gl.glTranslatef(legOffsetX, legHeight/2, -legOffsetZ);
        drawCube(gl, legThickness, legHeight, legThickness);
        gl.glPopMatrix();
    }

    private void drawTeapot(GL2 gl) {
        // Vẽ ấm trà màu hồng/tím
        float[] teapotColor = {0.9f, 0.4f, 0.8f, 1.0f};
        float[] teapotSpec = {0.8f, 0.8f, 0.8f, 1.0f};
        setMaterial(gl, teapotColor, teapotColor, teapotSpec, 50.0f);

        gl.glPushMatrix();
        gl.glTranslatef(0, 2.2f, 0); // Đặt trên mặt bàn
        gl.glScalef(0.3f, 0.3f, 0.3f); // Thu nhỏ ấm trà
        glut.glutSolidTeapot(1.0f);
        gl.glPopMatrix();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        // Thiết lập camera
        glu.gluLookAt(0, 3, zoom, 0, 1, 0, 0, 1, 0);

        // Áp dụng các phép quay
        gl.glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotateY, 0.0f, 1.0f, 0.0f);

        // Vẽ bàn
        drawTable(gl);

        // Vẽ ấm trà
        drawTeapot(gl);

        // Vẽ thông tin kích thước
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glColor3f(0, 0, 0);
        gl.glRasterPos3f(-3, 3, 0);

        String info = "Bàn ăn: W200cm x H100cm x D100cm";
        for (char c : info.toCharArray()) {
            glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_18, c);
        }

        gl.glRasterPos3f(-3, 2.7f, 0);
        String controls = "Điều khiển: ←→↑↓ quay, +/- zoom, R reset";
        for (char c : controls.toCharArray()) {
            glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_12, c);
        }

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glFlush();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45.0, (double) width / height, 0.1, 100.0);

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
            case KeyEvent.VK_PLUS:
            case KeyEvent.VK_EQUALS:
                zoom -= 0.5f;
                if (zoom < 2.0f) zoom = 2.0f;
                break;
            case KeyEvent.VK_MINUS:
                zoom += 0.5f;
                if (zoom > 15.0f) zoom = 15.0f;
                break;
            case KeyEvent.VK_R:
                rotateX = -20.0f;
                rotateY = 30.0f;
                zoom = 8.0f;
                break;
        }
        canvas.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new DiningTableViewer();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Lỗi khởi tạo OpenGL. Vui lòng kiểm tra JOGL đã được cài đặt đúng.");
            }
        });
    }
}
