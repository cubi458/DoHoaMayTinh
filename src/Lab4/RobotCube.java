package Lab4;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class RobotCube implements GLEventListener, KeyListener {
    private Texture floorTexture;

    private float armRightAngleZ = 0f;
    private float armRightRotateZ = 0f;

    public static void main(String[] args) {
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(capabilities);
        RobotCube robot = new RobotCube();
        canvas.addGLEventListener(robot);
        canvas.addKeyListener(robot);

        JFrame frame = new JFrame("JOGL Detailed Robot");
        frame.setSize(800, 600);
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
        gl.glClearColor(1, 1, 1, 1);
        gl.glEnable(GL2.GL_DEPTH_TEST);

        try {
            floorTexture = TextureIO.newTexture(new java.io.File("textures/floor.jpg"), true);
        } catch (Exception e) {
            System.err.println("Không load được ảnh nền: " + e.getMessage());
        }
    }


    @Override
    public void dispose(GLAutoDrawable drawable) {}

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glTranslatef(0f, -1.5f, -20f);
        gl.glRotatef(30, 1f, 0f, 0f); // nghiêng sàn cho giống hình
        gl.glRotatef(-20, 0f, 1f, 0f);

        drawFloor(gl);
        drawRobot(gl);
    }

    private void drawCube(GL2 gl, float r, float g, float b, float scaleX, float scaleY, float scaleZ) {
        gl.glPushMatrix();
        gl.glScalef(scaleX, scaleY, scaleZ);
        gl.glColor3f(r, g, b);
        gl.glBegin(GL2.GL_QUADS);

        // Mặt trước
        gl.glVertex3f(-0.5f, -0.5f, 0.5f);
        gl.glVertex3f(0.5f, -0.5f, 0.5f);
        gl.glVertex3f(0.5f, 0.5f, 0.5f);
        gl.glVertex3f(-0.5f, 0.5f, 0.5f);

        // Mặt sau
        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
        gl.glVertex3f(-0.5f, 0.5f, -0.5f);
        gl.glVertex3f(0.5f, 0.5f, -0.5f);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);

        // Mặt trái
        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
        gl.glVertex3f(-0.5f, -0.5f, 0.5f);
        gl.glVertex3f(-0.5f, 0.5f, 0.5f);
        gl.glVertex3f(-0.5f, 0.5f, -0.5f);

        // Mặt phải
        gl.glVertex3f(0.5f, -0.5f, -0.5f);
        gl.glVertex3f(0.5f, 0.5f, -0.5f);
        gl.glVertex3f(0.5f, 0.5f, 0.5f);
        gl.glVertex3f(0.5f, -0.5f, 0.5f);

        // Mặt trên
        gl.glVertex3f(-0.5f, 0.5f, -0.5f);
        gl.glVertex3f(-0.5f, 0.5f, 0.5f);
        gl.glVertex3f(0.5f, 0.5f, 0.5f);
        gl.glVertex3f(0.5f, 0.5f, -0.5f);

        // Mặt dưới
        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);
        gl.glVertex3f(0.5f, -0.5f, 0.5f);
        gl.glVertex3f(-0.5f, -0.5f, 0.5f);

        gl.glEnd();
        gl.glPopMatrix();
    }

    private void drawRobot(GL2 gl) {
        // Thân (hình hộp chữ nhật đứng, tỉ lệ hợp lý)
        drawCube(gl, 0.85f, 0.85f, 0.85f, 1.5f, 2.5f, 0.8f);

        // Cổ (nhỏ, cao vừa phải)
        gl.glPushMatrix();
        gl.glTranslatef(0f, 1.5f, 0f); // Đặt cổ lên trên thân
        drawCube(gl, 0.7f, 0.7f, 0.7f, 0.4f, 0.5f, 0.4f);
        gl.glPopMatrix();

        // Đầu (nhỏ hơn thân)
        gl.glPushMatrix();
        gl.glTranslatef(0f, 2.2f, 0f); // Đặt đầu lên trên cổ
        drawCube(gl, 0.6f, 0.6f, 0.6f, 0.8f, 0.8f, 0.8f);
        gl.glPopMatrix();

        // Tay trái (vai to, cánh tay và cẳng tay nhỏ dần)
        gl.glPushMatrix();
        gl.glTranslatef(-1.1f, 1.0f, 0f); // Vai trái
        drawCube(gl, 0.5f, 0.5f, 0.5f, 0.4f, 0.6f, 0.4f); // Vai
        gl.glTranslatef(0f, -0.7f, 0f); // Cánh tay trên
        drawCube(gl, 0.45f, 0.45f, 0.45f, 0.35f, 0.8f, 0.35f);
        gl.glTranslatef(0f, -0.9f, 0f); // Cẳng tay
        drawCube(gl, 0.4f, 0.4f, 0.4f, 0.3f, 0.7f, 0.3f);
        gl.glPopMatrix();

        // Tay phải (điều khiển được)
        gl.glPushMatrix();
        gl.glTranslatef(1.1f, 1.0f, 0f); // Vai phải
        gl.glRotatef(armRightAngleZ, 0f, 0f, 1f); // Xoay cánh tay quanh vai
        drawCube(gl, 0.5f, 0.5f, 0.5f, 0.4f, 0.6f, 0.4f); // Vai
        gl.glTranslatef(0f, -0.7f, 0f); // Cánh tay trên
        drawCube(gl, 0.45f, 0.45f, 0.45f, 0.35f, 0.8f, 0.35f);
        gl.glTranslatef(0f, -0.9f, 0f); // Đến khuỷu tay
        gl.glRotatef(armRightRotateZ, 0f, 0f, 1f); // Xoay cẳng tay quanh khuỷu
        drawCube(gl, 0.4f, 0.4f, 0.4f, 0.3f, 0.7f, 0.3f); // Cẳng tay
        gl.glPopMatrix();

        // Chân trái (đùi và cẳng chân tỉ lệ hợp lý)
        gl.glPushMatrix();
        gl.glTranslatef(-0.5f, -1.5f, 0f); // Đùi trái
        drawCube(gl, 0.6f, 0.6f, 0.6f, 0.4f, 0.9f, 0.4f);
        gl.glTranslatef(0f, -1.0f, 0f); // Cẳng chân trái
        drawCube(gl, 0.5f, 0.5f, 0.5f, 0.35f, 0.8f, 0.35f);
        gl.glPopMatrix();

        // Chân phải
        gl.glPushMatrix();
        gl.glTranslatef(0.5f, -1.5f, 0f); // Đùi phải
        drawCube(gl, 0.6f, 0.6f, 0.6f, 0.4f, 0.9f, 0.4f);
        gl.glTranslatef(0f, -1.0f, 0f); // Cẳng chân phải
        drawCube(gl, 0.5f, 0.5f, 0.5f, 0.35f, 0.8f, 0.35f);
        gl.glPopMatrix();
    }

    private void drawFloor(GL2 gl) {
        if (floorTexture != null) {
            floorTexture.enable(gl);
            floorTexture.bind(gl);
        } else {
            gl.glColor3f(0.8f, 0.8f, 0.8f); // fallback nếu không có texture
        }

        gl.glBegin(GL2.GL_QUADS);

        gl.glTexCoord2f(0f, 0f); gl.glVertex3f(-10f, -4.5f, -10f);
        gl.glTexCoord2f(1f, 0f); gl.glVertex3f(10f, -4.5f, -10f);
        gl.glTexCoord2f(1f, 1f); gl.glVertex3f(10f, -4.5f, 10f);
        gl.glTexCoord2f(0f, 1f); gl.glVertex3f(-10f, -4.5f, 10f);

        gl.glEnd();

        if (floorTexture != null) {
            floorTexture.disable(gl);
        }
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL2 gl = drawable.getGL().getGL2();
        float aspect = (float) width / height;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU glu = new GLU();
        glu.gluPerspective(45.0f, aspect, 1.0, 100.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'p': armRightAngleZ += 1f; break; // nâng cánh tay phải
            case 'h': armRightAngleZ -= 1f; break; // hạ cánh tay phải
            case 'a': armRightRotateZ += 1f; break; // quay cẳng tay phải lên
            case 'b': armRightRotateZ -= 1f; break; // quay cẳng tay phải xuống
        }
    }

    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}
