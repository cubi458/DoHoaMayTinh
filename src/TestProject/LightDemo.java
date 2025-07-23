package TestProject;

import static com.jogamp.opengl.GL2.*; // GL constants

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * JOGL 2.0 Program Template (GLCanvas) This is a "Component" which can be added
 * into a top-level "Container". It also handles the OpenGL events to render
 * graphics.
 */
public class LightDemo extends GLJPanel implements GLEventListener {
    // Define constants for the top-level container
    private static String TITLE = "JOGL 2.0 Setup (GLJPanel)"; // window's title
    private static final int CANVAS_WIDTH = 640; // width of the drawable
    private static final int CANVAS_HEIGHT = 480; // height of the drawable
    private static final int FPS = 60; // animator's target frames per second

    public static void main(String[] args) {
        // Run the GUI codes in the event-dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Create the OpenGL rendering canvas
                GLJPanel panel = new LightDemo();
                panel.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

                // Create a animator that drives canvas' display() at the specified FPS.
                final FPSAnimator animator = new FPSAnimator(panel, FPS, true);

                // Create the top-level container
                final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
                frame.getContentPane().add(panel);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // Use a dedicate thread to run the stop() to ensure
                        // that the animator stops before program exits.
                        new Thread() {
                            public void run() {
                                if (animator.isStarted())
                                    animator.stop();
                                System.exit(0);
                            }
                        }.start();
                    }
                });
                frame.setTitle(TITLE);
                frame.pack();
                frame.setVisible(true);
                animator.start(); // start the animation loop
            }
        });
    }

    // Setup OpenGL Graphics Renderer
    private GLU glu; // for the GL Utility
    private GLUT glut; // OpenGL Utility Toolkit

    /** Constructor to setup the GUI for this Component */
    public LightDemo() {
        this.addGLEventListener(this);
    }

    // ------ Implement methods declared in GLEventListener ------

    /**
     * Called back immediately after the OpenGL context is initialized. Can be used
     * to perform one-time initialization. Run only once.
     */
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2(); // get the OpenGL graphics context
        glu = new GLU(); // get GL Utilities
        glut = new GLUT();

        float ambient[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        float diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float position[] = { 0.0f, 3.0f, 2.0f, 0.0f };
        float lmodel_ambient[] = { 0.4f, 0.4f, 0.4f, 1.0f };
        float local_view[] = { 0.0f };

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, local_view, 0);

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);

        gl.glClearColor(0.0f, 0.1f, 0.1f, 0.0f); // set background (clear) color
        gl.glClearDepth(1.0f); // set clear depth value to farthest
        gl.glEnable(GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
        gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting
    }

    /**
     * Call-back handler for window re-size event. Also called when the drawable is
     * first set to visible.
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        if (width <= (height * 2)) //
            gl.glOrtho(-6.0, 6.0, -3.0 * ((float) height * 2) / width, //
                    3.0 * ((float) height * 2) / width, -10.0, 10.0);
        else
            gl.glOrtho(-6.0 * (float) width / (height * 2), //
                    6.0 * (float) width / (height * 2), -3.0, 3.0, -10.0, 10.0);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    /**
     * Called back by the animator to perform rendering.
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        GLUT glut = new GLUT();
        //
        float no_mat[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        float mat_ambient[] = { 0.7f, 0.7f, 0.7f, 1.0f };
        float mat_ambient_color[] = { 0.8f, 0.8f, 0.2f, 1.0f };
        float mat_diffuse[] = { 0.1f, 0.5f, 0.8f, 1.0f };
        float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float no_shininess[] = { 0.0f };
        float low_shininess[] = { 5.0f };
        float high_shininess[] = { 100.0f };
        float mat_emission[] = { 0.3f, 0.2f, 0.2f, 0.0f };

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        /*
         * draw sphere in first row, first column diffuse reflection only; no ambient or
         * specular
         */
        gl.glPushMatrix();
        gl.glTranslatef(-3.75f, 3.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, no_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, no_mat, 0);
        glut.glutSolidSphere(1.0, 20, 20);
        gl.glPopMatrix();
        /*
         * draw sphere in first row, second column diffuse and specular reflection; low
         * shininess; no ambient
         */
        gl.glPushMatrix();
        gl.glTranslatef(-1.25f, 3.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, low_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, no_mat, 0);
        glut.glutSolidSphere(1.0f, 20, 20);
        gl.glPopMatrix();
        /*
         * draw sphere in first row, third column diffuse and specular reflection; high
         * shininess; no ambient
         */
        gl.glPushMatrix();
        gl.glTranslatef(1.25f, 3.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, high_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, no_mat, 0);
        glut.glutSolidSphere(1.0f, 20, 20);
        gl.glPopMatrix();
        /*
         * draw sphere in first row, fourth column diffuse reflection; emission; no
         * ambient or specular reflection
         */
        gl.glPushMatrix();
        gl.glTranslatef(3.75f, 3.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, no_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, mat_emission, 0);
        glut.glutSolidSphere(1.0f, 20, 20);
        gl.glPopMatrix();
        /*
         * draw sphere in second row, first column ambient and diffuse reflection; no
         * specular
         */
        gl.glPushMatrix();
        gl.glTranslatef(-3.75f, 0.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, no_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, no_mat, 0);
        glut.glutSolidSphere(1.0f, 20, 20);
        gl.glPopMatrix();
        /*
         * draw sphere in second row, second column ambient, diffuse and specular
         * reflection; low shininess
         */
        gl.glPushMatrix();
        gl.glTranslatef(-1.25f, 0.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, low_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, no_mat, 0);
        glut.glutSolidSphere(1.0f, 20, 20);
        gl.glPopMatrix();
        /*
         * draw sphere in second row, third column ambient, diffuse and specular
         * reflection; high shininess
         */
        gl.glPushMatrix();
        gl.glTranslatef(1.25f, 0.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, high_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, no_mat, 0);
        glut.glutSolidSphere(1.0f, 20, 20);
        gl.glPopMatrix();
        /*
         * draw sphere in second row, fourth column ambient and diffuse reflection;
         * emission; no specular
         */
        gl.glPushMatrix();
        gl.glTranslatef(3.75f, 0.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, no_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, mat_emission, 0);
        glut.glutSolidSphere(1.0f, 20, 20);
        gl.glPopMatrix();
        /*
         * draw sphere in third row, first column colored ambient and diffuse
         * reflection; no specular
         */
        gl.glPushMatrix();
        gl.glTranslatef(-3.75f, -3.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_ambient_color, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, no_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, no_mat, 0);
        glut.glutSolidSphere(1.0f, 20, 20);
        gl.glPopMatrix();
        /*
         * draw sphere in third row, second column colored ambient, diffuse and specular
         * reflection; low shininess
         */
        gl.glPushMatrix();
        gl.glTranslatef(-1.25f, -3.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_ambient_color, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, low_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, no_mat, 0);
        glut.glutSolidSphere(1.0f, 20, 20);
        gl.glPopMatrix();
        /*
         * draw sphere in third row, third column colored ambient, diffuse and specular
         * reflection; high shininess
         */
        gl.glPushMatrix();
        gl.glTranslatef(1.25f, -3.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_ambient_color, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, high_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, no_mat, 0);
        glut.glutSolidSphere(1.0f, 20, 20);
        gl.glPopMatrix();
        /*
         * draw sphere in third row, fourth column colored ambient and diffuse
         * reflection; emission; no specular
         */
        gl.glPushMatrix();
        gl.glTranslatef(3.75f, -3.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_ambient_color, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, no_mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, no_shininess, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, mat_emission, 0);
        glut.glutSolidSphere(1.0f, 20, 20);
        gl.glPopMatrix();
        gl.glFlush();
    }

    /**
     * Called back before the OpenGL context is destroyed. Release resource such as
     * buffers.
     */
    public void dispose(GLAutoDrawable drawable) {
    }
}
