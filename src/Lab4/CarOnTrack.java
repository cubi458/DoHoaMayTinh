package Lab4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;

public class CarOnTrack extends JFrame implements GLEventListener, ActionListener {
    private GLCanvas canvas;
    private GLU glu;
    private GLUquadric quadric;
    private FPSAnimator animator;
    private float carAngle = 0.0f;
    private Timer timer;

    // Rotation angles for 3D view
    private float rotateX = -20.0f;
    private float rotateY = 0.0f;

    public CarOnTrack() {
        setTitle("Car on Circular Track - Java OpenGL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(profile);
        canvas = new GLCanvas(caps);
        canvas.addGLEventListener(this);
        add(canvas);

        // Mouse rotation
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            int lastX, lastY;

            @Override
            public void mouseDragged(MouseEvent e) {
                int deltaX = e.getX() - lastX;
                int deltaY = e.getY() - lastY;
                rotateY += deltaX * 0.5f;
                rotateX += deltaY * 0.5f;
                lastX = e.getX();
                lastY = e.getY();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });

        timer = new Timer(50, this);
        timer.start();

        animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        quadric = glu.gluNewQuadric();

        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);

        float[] lightPos = {5.0f, 5.0f, 5.0f, 1.0f};
        float[] lightAmbient = {0.3f, 0.3f, 0.3f, 1.0f};
        float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDiffuse, 0);

        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);

        gl.glClearColor(0.0f, 0.0f, 0.4f, 1.0f);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        glu.gluLookAt(0, 5, 8, 0, 0, 0, 0, 1, 0);

        gl.glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotateY, 0.0f, 1.0f, 0.0f);

        drawTrack(gl);
        drawCar(gl);

        gl.glFlush();
    }

    private void drawTrack(GL2 gl) {
        gl.glPushMatrix();
        gl.glColor3f(0.0f, 0.6f, 0.0f);
        gl.glRotatef(-90, 1, 0, 0);
        glu.gluDisk(quadric, 0, 3.5f, 60, 1);

        gl.glColor3f(0.4f, 0.4f, 0.4f);
        glu.gluDisk(quadric, 2.0f, 3.0f, 60, 1);

        gl.glColor3f(1.0f, 1.0f, 1.0f); // continuous white stripe
        glu.gluDisk(quadric, 2.45f, 2.55f, 60, 1);  // 360 degrees stripe

        gl.glColor3f(0.0f, 0.8f, 0.0f);
        glu.gluDisk(quadric, 0, 2.0f, 60, 1);
        gl.glPopMatrix();
    }

    private void drawCar(GL2 gl) {
        float radius = 2.5f;
        float carX = radius * (float) Math.cos(Math.toRadians(carAngle));
        float carZ = radius * (float) Math.sin(Math.toRadians(carAngle));

        gl.glPushMatrix();
        gl.glTranslatef(carX, 0.3f, carZ);
        gl.glRotatef(-carAngle, 0, 1, 0);

        // Thân xe chính (body) - chiều dài lớn hơn chiều rộng
        gl.glPushMatrix();
        gl.glColor3f(0.9f, 0.1f, 0.1f); // Màu đỏ đẹp hơn
        gl.glTranslatef(0, 0.1f, 0);
        gl.glScalef(1.8f, 0.3f, 0.8f); // dài x rộng x cao
        glutSolidCube(gl, 1.0f);
        gl.glPopMatrix();

        // Cabin (phần cabin) - cũng theo tỷ lệ thực tế
        gl.glPushMatrix();
        gl.glColor3f(0.2f, 0.2f, 0.8f); // Màu xanh dương
        gl.glTranslatef(-0.2f, 0.35f, 0);
        gl.glScalef(1.0f, 0.3f, 0.7f); // dài x cao x rộng
        glutSolidCube(gl, 1.0f);
        gl.glPopMatrix();

        // Mui xe (hood) - phần đầu xe
        gl.glPushMatrix();
        gl.glColor3f(0.9f, 0.1f, 0.1f);
        gl.glTranslatef(0.6f, 0.15f, 0);
        gl.glScalef(0.6f, 0.15f, 0.75f); // dài x cao x rộng
        glutSolidCube(gl, 1.0f);
        gl.glPopMatrix();

        // Đuôi xe (trunk) - phần sau xe
        gl.glPushMatrix();
        gl.glColor3f(0.9f, 0.1f, 0.1f);
        gl.glTranslatef(-0.7f, 0.15f, 0);
        gl.glScalef(0.4f, 0.15f, 0.75f); // dài x cao x rộng
        glutSolidCube(gl, 1.0f);
        gl.glPopMatrix();

        // Đèn pha trước
        gl.glPushMatrix();
        gl.glColor3f(1.0f, 1.0f, 0.8f); // Màu trắng vàng
        gl.glTranslatef(0.9f, 0.15f, 0.25f);
        glutSolidSphere(gl, 0.08f, 8, 8);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glColor3f(1.0f, 1.0f, 0.8f);
        gl.glTranslatef(0.9f, 0.15f, -0.25f);
        glutSolidSphere(gl, 0.08f, 8, 8);
        gl.glPopMatrix();

        // Đèn hậu
        gl.glPushMatrix();
        gl.glColor3f(1.0f, 0.2f, 0.2f); // Màu đỏ
        gl.glTranslatef(-0.9f, 0.15f, 0.3f);
        glutSolidSphere(gl, 0.06f, 6, 6);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glColor3f(1.0f, 0.2f, 0.2f);
        gl.glTranslatef(-0.9f, 0.15f, -0.3f);
        glutSolidSphere(gl, 0.06f, 6, 6);
        gl.glPopMatrix();

        // Kính chắn gió trước
        gl.glPushMatrix();
        gl.glColor3f(0.6f, 0.8f, 1.0f); // Màu xanh nhạt như kính
        gl.glTranslatef(0.25f, 0.35f, 0);
        gl.glScalef(0.02f, 0.25f, 0.65f);
        glutSolidCube(gl, 1.0f);
        gl.glPopMatrix();

        // Kính sau
        gl.glPushMatrix();
        gl.glColor3f(0.6f, 0.8f, 1.0f);
        gl.glTranslatef(-0.65f, 0.35f, 0);
        gl.glScalef(0.02f, 0.25f, 0.65f);
        glutSolidCube(gl, 1.0f);
        gl.glPopMatrix();

        // Cửa sổ bên trái
        gl.glPushMatrix();
        gl.glColor3f(0.6f, 0.8f, 1.0f);
        gl.glTranslatef(-0.2f, 0.35f, 0.35f);
        gl.glScalef(0.8f, 0.25f, 0.02f);
        glutSolidCube(gl, 1.0f);
        gl.glPopMatrix();

        // Cửa sổ bên phải
        gl.glPushMatrix();
        gl.glColor3f(0.6f, 0.8f, 1.0f);
        gl.glTranslatef(-0.2f, 0.35f, -0.35f);
        gl.glScalef(0.8f, 0.25f, 0.02f);
        glutSolidCube(gl, 1.0f);
        gl.glPopMatrix();

        // Ăng-ten
        gl.glPushMatrix();
        gl.glColor3f(0.3f, 0.3f, 0.3f); // Màu xám
        gl.glTranslatef(-0.2f, 0.5f, 0.25f);
        gl.glScalef(0.02f, 0.2f, 0.02f);
        glutSolidCube(gl, 1.0f);
        gl.glPopMatrix();

        // Gương chiếu hậu trái
        gl.glPushMatrix();
        gl.glColor3f(0.2f, 0.2f, 0.2f);
        gl.glTranslatef(0.1f, 0.25f, 0.45f);
        gl.glScalef(0.08f, 0.05f, 0.12f);
        glutSolidCube(gl, 1.0f);
        gl.glPopMatrix();

        // Gương chiếu hậu phải
        gl.glPushMatrix();
        gl.glColor3f(0.2f, 0.2f, 0.2f);
        gl.glTranslatef(0.1f, 0.25f, -0.45f);
        gl.glScalef(0.08f, 0.05f, 0.12f);
        glutSolidCube(gl, 1.0f);
        gl.glPopMatrix();

        // Bánh xe với vị trí thực tế hơn
        drawEnhancedWheel(gl, 0.5f, -0.05f, 0.35f);   // trước phải
        drawEnhancedWheel(gl, 0.5f, -0.05f, -0.35f);  // trước trái
        drawEnhancedWheel(gl, -0.5f, -0.05f, 0.35f);  // sau phải
        drawEnhancedWheel(gl, -0.5f, -0.05f, -0.35f); // sau trái

        gl.glPopMatrix();
    }

    // Hàm hỗ trợ cho sphere
    private void glutSolidSphere(GL2 gl, float radius, int slices, int stacks) {
        for (int i = 0; i < stacks; i++) {
            float lat0 = (float) (Math.PI * (-0.5 + (double) i / stacks));
            float z0 = (float) (radius * Math.sin(lat0));
            float zr0 = (float) (radius * Math.cos(lat0));

            float lat1 = (float) (Math.PI * (-0.5 + (double) (i + 1) / stacks));
            float z1 = (float) (radius * Math.sin(lat1));
            float zr1 = (float) (radius * Math.cos(lat1));

            gl.glBegin(GL2.GL_QUAD_STRIP);
            for (int j = 0; j <= slices; j++) {
                float lng = (float) (2 * Math.PI * j / slices);
                float x = (float) Math.cos(lng);
                float y = (float) Math.sin(lng);

                gl.glNormal3f(x * zr0, y * zr0, z0);
                gl.glVertex3f(x * zr0, y * zr0, z0);

                gl.glNormal3f(x * zr1, y * zr1, z1);
                gl.glVertex3f(x * zr1, y * zr1, z1);
            }
            gl.glEnd();
        }
    }
    private void drawEnhancedWheel(GL2 gl, float x, float y, float z) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);

        // Lốp xe (màu đen)
        gl.glColor3f(0.1f, 0.1f, 0.1f);
        gl.glPushMatrix();
        gl.glRotatef(90, 1, 0, 0);
        glutSolidTorus(gl, 0.06f, 0.12f, 8, 16);
        gl.glPopMatrix();

        // Mâm xe (màu bạc)
        gl.glColor3f(0.8f, 0.8f, 0.9f);
        gl.glPushMatrix();
        gl.glRotatef(90, 1, 0, 0);
        glutSolidCylinder(gl, 0.08f, 0.04f, 8, 2);
        gl.glPopMatrix();

        // Trung tâm mâm
        gl.glColor3f(0.4f, 0.4f, 0.5f);
        gl.glPushMatrix();
        gl.glRotatef(90, 1, 0, 0);
        glutSolidCylinder(gl, 0.03f, 0.06f, 6, 2);
        gl.glPopMatrix();

        gl.glPopMatrix();
    }

    // Hàm hỗ trợ cho torus (nếu chưa có)
    private void glutSolidTorus(GL2 gl, float innerRadius, float outerRadius, int sides, int rings) {
        for (int i = 0; i < rings; i++) {
            gl.glBegin(GL2.GL_QUAD_STRIP);
            for (int j = 0; j <= sides; j++) {
                for (int k = 0; k <= 1; k++) {
                    double s = (i + k) % rings + 0.5;
                    double t = j % sides;

                    double x = (outerRadius + innerRadius * Math.cos(s * 2 * Math.PI / rings)) * Math.cos(t * 2 * Math.PI / sides);
                    double y = (outerRadius + innerRadius * Math.cos(s * 2 * Math.PI / rings)) * Math.sin(t * 2 * Math.PI / sides);
                    double z = innerRadius * Math.sin(s * 2 * Math.PI / rings);

                    gl.glVertex3d(x, y, z);
                }
            }
            gl.glEnd();
        }
    }

    // Hàm hỗ trợ cho cylinder (nếu chưa có)
    private void glutSolidCylinder(GL2 gl, float radius, float height, int slices, int stacks) {
        for (int i = 0; i < stacks; i++) {
            float z1 = (float) i * height / stacks;
            float z2 = (float) (i + 1) * height / stacks;

            gl.glBegin(GL2.GL_QUAD_STRIP);
            for (int j = 0; j <= slices; j++) {
                double angle = 2 * Math.PI * j / slices;
                float x = (float) (radius * Math.cos(angle));
                float y = (float) (radius * Math.sin(angle));

                gl.glVertex3f(x, y, z1);
                gl.glVertex3f(x, y, z2);
            }
            gl.glEnd();
        }
    }

    private void drawWheel(GL2 gl, float x, float y, float z) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        gl.glRotatef(90, 0, 1, 0);
        gl.glColor3f(0.1f, 0.1f, 0.1f);
        glu.gluCylinder(quadric, 0.1f, 0.1f, 0.05f, 16, 1);
        gl.glTranslatef(0, 0, 0.05f);
        glu.gluDisk(quadric, 0, 0.1f, 16, 1);
        gl.glTranslatef(0, 0, -0.05f);
        glu.gluDisk(quadric, 0, 0.1f, 16, 1);
        gl.glPopMatrix();
    }

    private void glutSolidCube(GL2 gl, float size) {
        float half = size / 2.0f;

        gl.glBegin(GL2.GL_QUADS);
        // Top
        gl.glNormal3f(0, 1, 0);
        gl.glVertex3f(-half, half, -half);
        gl.glVertex3f(half, half, -half);
        gl.glVertex3f(half, half, half);
        gl.glVertex3f(-half, half, half);
        // Bottom
        gl.glNormal3f(0, -1, 0);
        gl.glVertex3f(-half, -half, -half);
        gl.glVertex3f(half, -half, -half);
        gl.glVertex3f(half, -half, half);
        gl.glVertex3f(-half, -half, half);
        // Front
        gl.glNormal3f(0, 0, 1);
        gl.glVertex3f(-half, -half, half);
        gl.glVertex3f(half, -half, half);
        gl.glVertex3f(half, half, half);
        gl.glVertex3f(-half, half, half);
        // Back
        gl.glNormal3f(0, 0, -1);
        gl.glVertex3f(-half, -half, -half);
        gl.glVertex3f(half, -half, -half);
        gl.glVertex3f(half, half, -half);
        gl.glVertex3f(-half, half, -half);
        // Left
        gl.glNormal3f(-1, 0, 0);
        gl.glVertex3f(-half, -half, -half);
        gl.glVertex3f(-half, -half, half);
        gl.glVertex3f(-half, half, half);
        gl.glVertex3f(-half, half, -half);
        // Right
        gl.glNormal3f(1, 0, 0);
        gl.glVertex3f(half, -half, -half);
        gl.glVertex3f(half, -half, half);
        gl.glVertex3f(half, half, half);
        gl.glVertex3f(half, half, -half);
        gl.glEnd();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        if (height == 0) height = 1;
        float aspect = (float) width / height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45.0, aspect, 0.1, 100.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        if (quadric != null) {
            glu.gluDeleteQuadric(quadric);
        }
        if (timer != null) {
            timer.stop();
        }
        if (animator != null) {
            animator.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        carAngle += 2.0f;
        if (carAngle >= 360.0f) carAngle -= 360.0f;
        canvas.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new CarOnTrack().setVisible(true);
            } catch (Exception e) {
                System.err.println("JOGL error: Make sure libraries are in classpath");
                e.printStackTrace();
            }
        });
    }
}
