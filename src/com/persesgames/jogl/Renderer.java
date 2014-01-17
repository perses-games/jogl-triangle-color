package com.persesgames.jogl;

import com.jogamp.common.nio.Buffers;
import com.jogamp.newt.opengl.GLWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.media.opengl.*;
import java.nio.FloatBuffer;

/**
 * Date: 10/25/13
 * Time: 7:42 PM
 */
public class Renderer implements GLEventListener  {
    private final static Logger logger = LoggerFactory.getLogger(Renderer.class);

    private volatile boolean stopped    = false;
    private volatile boolean dirty      = false;

    private ShaderProgram shaderProgram;

    private final GLWindow glWindow;

    private float[]                 vertices = {
            -0.5f, -0.5f,
             1.0f,  0.0f, 0.0f, 0.0f,
             0.5f, -0.5f,
             0.0f,  1.0f, 0.0f, 0.0f,
             0.0f,  0.5f,
             0.0f,  0.0f, 1.0f, 0.0f,
    };

    private FloatBuffer             fbVertices          = Buffers.newDirectFloatBuffer(vertices);

    private int vboHandle;

    public Renderer(GLWindow glWindow) {
        this.glWindow = glWindow;
    }

    public void stop() {
        stopped = true;
    }

    public void redraw() {
        dirty = true;
    }

    public void run() {
        Renderer.this.glWindow.display();

        while(!stopped) {
            if (dirty) {
                logger.info("rendering+" + System.currentTimeMillis());
                Renderer.this.glWindow.display();
                Renderer.this.glWindow.swapBuffers();
                dirty = false;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }

        Renderer.this.glWindow.destroy();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2ES2 gl = drawable.getGL().getGL2ES2();

        logger.info("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        logger.info("INIT GL IS: " + gl.getClass().getName());
        logger.info("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
        logger.info("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
        logger.info("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));

        int [] result = new int[1];
        gl.glGetIntegerv(GL2.GL_MAX_VERTEX_ATTRIBS, result, 0);
        logger.info("GL_MAX_VERTEX_ATTRIBS=" + result[0]);

        shaderProgram = new ShaderProgram(gl, Util.loadAsText(getClass(), "simpleShader.vert"), Util.loadAsText(getClass(), "simpleShader.frag"));

        int [] vboHandles = new int[1];
        gl.glGenBuffers(1, vboHandles, 0);

        vboHandle = vboHandles[0];

        // Select the VBO, GPU memory data, to use for vertices
        gl.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboHandle);

        // transfer data to VBO, this perform the copy of data from CPU -> GPU memory
        gl.glBufferData(GL.GL_ARRAY_BUFFER, fbVertices.limit() * 4, fbVertices, GL.GL_STATIC_DRAW);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL2ES2 gl = drawable.getGL().getGL2ES2();

        gl.glDeleteBuffers(1, new int [] { vboHandle }, 0);

        shaderProgram.dispose();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        logger.info("display+" + System.currentTimeMillis());

        GL2ES2 gl = drawable.getGL().getGL2ES2();

        // Clear screen
        gl.glClearColor(0.2f, 0, 0.2f, 1f);
        gl.glClear(GL2ES2.GL_COLOR_BUFFER_BIT);

        shaderProgram.begin();

        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);

        // Associate Vertex attribute 0 with the last bound VBO
        gl.glVertexAttribPointer(0 /* the vertex attribute */, 2,
                GL2ES2.GL_FLOAT, false /* normalized? */, 24 /* stride */,
                0 /* The bound VBO data offset */);

        // Associate Vertex attribute 0 with the last bound VBO
        gl.glVertexAttribPointer(1 /* the vertex attribute */, 4,
                GL2ES2.GL_FLOAT, false /* normalized? */, 24 /* stride */,
                8 /* The bound VBO data offset */);


        gl.glDrawArrays(GL2ES2.GL_TRIANGLES, 0, 3); //Draw the vertices as triangle

        gl.glDisableVertexAttribArray(0); // Allow release of vertex position memory
        gl.glDisableVertexAttribArray(1); // Allow release of vertex position memory

        shaderProgram.end();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        logger.info("reshape+" + System.currentTimeMillis());

        GL2ES2 gl = drawable.getGL().getGL2ES2();

        gl.glViewport(0, 0, w, h);
    }

}
