package Lab4;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TransformationExercise extends JFrame implements GLEventListener, KeyListener {
    private GLCanvas canvas;
    private GLU glu;
    private GLUT glut;
    private FPSAnimator animator;

    private int currentScene = 1; // 1 for scene a), 2 for scene b)
    private float rotateY = 0.0f;
    private boolean autoRotate = true;

    public TransformationExercise() {
        setTitle("Bài tập thực hành các phép biến đổi - Nhấn SPACE để chuyển scene, A để bật/tắt auto-rotate");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);

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

        // Thiết lập màu nền
        gl.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);

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
        float[] position = {5.0f, 5.0f, 5.0f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);
    }

    private void setMaterial(GL2 gl, float[] ambient, float[] diffuse, float[] specular, float shininess) {
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specular, 0);
        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, shininess);
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

    private void drawCone(GL2 gl, float radius, float height, int slices) {
        float angleStep = (float) (2 * Math.PI / slices);

        // Vẽ thân cone
        gl.glBegin(GL2.GL_TRIANGLES);
        for (int i = 0; i < slices; i++) {
            float angle1 = i * angleStep;
            float angle2 = (i + 1) * angleStep;

            float x1 = radius * (float) Math.cos(angle1);
            float z1 = radius * (float) Math.sin(angle1);
            float x2 = radius * (float) Math.cos(angle2);
            float z2 = radius * (float) Math.sin(angle2);

            // Tính normal cho surface
            float nx = (float) (Math.cos((angle1 + angle2) / 2) * height / Math.sqrt(radius * radius + height * height));
            float ny = (float) (radius / Math.sqrt(radius * radius + height * height));
            float nz = (float) (Math.sin((angle1 + angle2) / 2) * height / Math.sqrt(radius * radius + height * height));

            gl.glNormal3f(nx, ny, nz);
            gl.glVertex3f(0, height, 0);
            gl.glVertex3f(x1, 0, z1);
            gl.glVertex3f(x2, 0, z2);
        }
        gl.glEnd();

        // Vẽ đáy cone
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
    }

    private void drawCylinderWithCone(GL2 gl, float cylinderRadius, float cylinderHeight,
                                      float coneRadius, float coneHeight,
                                      float[] cylinderColor, float[] coneColor) {
        // Vẽ cylinder (thân)
        setMaterial(gl, cylinderColor, cylinderColor, new float[]{0.8f, 0.8f, 0.8f, 1.0f}, 50.0f);

        gl.glPushMatrix();
        gl.glTranslatef(0, -cylinderHeight/2, 0);
        drawCylinder(gl, cylinderRadius, cylinderHeight, 20);
        gl.glPopMatrix();

        // Vẽ cone (nón)
        setMaterial(gl, coneColor, coneColor, new float[]{0.8f, 0.8f, 0.8f, 1.0f}, 50.0f);

        gl.glPushMatrix();
        gl.glTranslatef(0, cylinderHeight/2, 0);
        drawCone(gl, coneRadius, coneHeight, 20);
        gl.glPopMatrix();
    }

    private void drawGroundPlane(GL2 gl) {
        // Vẽ mặt phẳng với texture gỗ
        float[] groundColor = {0.8f, 0.6f, 0.4f, 1.0f};
        setMaterial(gl, groundColor, groundColor, new float[]{0.3f, 0.3f, 0.3f, 1.0f}, 10.0f);

        gl.glPushMatrix();
        gl.glTranslatef(0, -3, 0);
        gl.glRotatef(-90, 1, 0, 0);

        // Vẽ mặt phẳng với pattern
        gl.glBegin(GL2.GL_QUADS);
        for (int i = -10; i < 10; i++) {
            for (int j = -10; j < 10; j++) {
                // Tạo pattern gỗ đơn giản
                float brightness = ((i + j) % 2 == 0) ? 0.9f : 0.7f;
                gl.glColor3f(brightness * 0.8f, brightness * 0.6f, brightness * 0.4f);

                gl.glVertex3f(i, j, 0);
                gl.glVertex3f(i+1, j, 0);
                gl.glVertex3f(i+1, j+1, 0);
                gl.glVertex3f(i, j+1, 0);
            }
        }
        gl.glEnd();

        gl.glPopMatrix();
    }

    private void drawSceneA(GL2 gl) {
        // Scene A: 3 hình nấm xanh với kích thước khác nhau
        float[] greenColor = {0.0f, 0.8f, 0.0f, 1.0f};
        float[] darkGreenColor = {0.0f, 0.5f, 0.0f, 1.0f};

        // Nấm 1 (nhỏ)
        gl.glPushMatrix();
        gl.glTranslatef(-3, 0, 0);
        gl.glScalef(0.6f, 0.6f, 0.6f);
        drawCylinderWithCone(gl, 0.5f, 1.5f, 1.0f, 0.8f, greenColor, darkGreenColor);
        gl.glPopMatrix();

        // Nấm 2 (trung bình)
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, 0);
        gl.glScalef(0.8f, 0.8f, 0.8f);
        drawCylinderWithCone(gl, 0.5f, 1.5f, 1.0f, 0.8f, greenColor, darkGreenColor);
        gl.glPopMatrix();

        // Nấm 3 (lớn)
        gl.glPushMatrix();
        gl.glTranslatef(3, 0, 0);
        gl.glScalef(1.0f, 1.0f, 1.0f);
        drawCylinderWithCone(gl, 0.5f, 1.5f, 1.0f, 0.8f, greenColor, darkGreenColor);
        gl.glPopMatrix();
    }

    private void drawSceneB(GL2 gl) {
        // Scene B: Đầu chim cách cụt đội nón trên nền gỗ
        drawGroundPlane(gl);

        // Đầu chim cách cụt (hình cầu xanh lá)
        float[] greenColor = {0.0f, 0.8f, 0.0f, 1.0f};
        setMaterial(gl, greenColor, greenColor, new float[]{0.8f, 0.8f, 0.8f, 1.0f}, 50.0f);

        gl.glPushMatrix();
        gl.glTranslatef(0, -0.5f, 0);
        gl.glScalef(1.5f, 1.3f, 1.5f); // Hơi dẹp để giống đầu chim
        glut.glutSolidSphere(1.0f, 20, 20);
        gl.glPopMatrix();

        // Mỏ chim (hình nón nhỏ màu cam)
        float[] orangeColor = {1.0f, 0.5f, 0.0f, 1.0f};
        setMaterial(gl, orangeColor, orangeColor, new float[]{0.8f, 0.8f, 0.8f, 1.0f}, 50.0f);

        gl.glPushMatrix();
        gl.glTranslatef(0, -0.3f, 1.4f);
        gl.glRotatef(90, 1, 0, 0); // Quay để mỏ hướng ra ngoài
        gl.glScalef(0.3f, 0.3f, 0.6f);
        drawCone(gl, 0.3f, 0.8f, 10);
        gl.glPopMatrix();

        // Mắt trái
        float[] blackColor = {0.0f, 0.0f, 0.0f, 1.0f};
        setMaterial(gl, blackColor, blackColor, new float[]{0.3f, 0.3f, 0.3f, 1.0f}, 10.0f);

        gl.glPushMatrix();
        gl.glTranslatef(-0.4f, 0.2f, 1.2f);
        gl.glScalef(0.15f, 0.15f, 0.15f);
        glut.glutSolidSphere(1.0f, 10, 10);
        gl.glPopMatrix();

        // Mắt phải
        gl.glPushMatrix();
        gl.glTranslatef(0.4f, 0.2f, 1.2f);
        gl.glScalef(0.15f, 0.15f, 0.15f);
        glut.glutSolidSphere(1.0f, 10, 10);
        gl.glPopMatrix();

        // Cái nón màu tím
        float[] purpleColor = {0.6f, 0.0f, 0.8f, 1.0f};
        setMaterial(gl, purpleColor, purpleColor, new float[]{0.8f, 0.8f, 0.8f, 1.0f}, 50.0f);

        gl.glPushMatrix();
        gl.glTranslatef(0, 1.0f, 0);
        gl.glScalef(1.3f, 1.2f, 1.3f);
        drawCone(gl, 1.0f, 1.5f, 20);
        gl.glPopMatrix();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        // Thiết lập camera
        glu.gluLookAt(0, 2, 8, 0, 0, 0, 0, 1, 0);

        // Auto rotate
        if (autoRotate) {
            rotateY += 0.5f;
            if (rotateY > 360) rotateY -= 360;
        }

        gl.glRotatef(rotateY, 0, 1, 0);

        // Vẽ scene tương ứng
        if (currentScene == 1) {
            drawSceneA(gl);
        } else {
            drawSceneB(gl);
        }

        // Vẽ text hướng dẫn
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glColor3f(0, 0, 0);
        gl.glRasterPos3f(-7, 4, 0);

        String text = "Scene " + (currentScene == 1 ? "A" : "B") + " - SPACE: Chuyển scene, A: Auto-rotate, ←→: Quay thủ công";
        for (char c : text.toCharArray()) {
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
            case KeyEvent.VK_SPACE:
                currentScene = (currentScene == 1) ? 2 : 1;
                break;
            case KeyEvent.VK_A:
                autoRotate = !autoRotate;
                break;
            case KeyEvent.VK_LEFT:
                rotateY -= 5.0f;
                break;
            case KeyEvent.VK_RIGHT:
                rotateY += 5.0f;
                break;
            case KeyEvent.VK_R:
                rotateY = 0.0f;
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
                new TransformationExercise();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Lỗi khởi tạo OpenGL. Vui lòng kiểm tra JOGL đã được cài đặt đúng.");
            }
        });
    }
}
