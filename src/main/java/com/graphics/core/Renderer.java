package com.graphics.core;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

// Renderer 2D simple: primitivas y manejo basico de OpenGL.
public class Renderer {

    private static final int STRIDE = 5 * Float.BYTES;
    private static final int CIRCLE_SEGMENTS = 32;
    private static final int QUAD_VERTS = 6;
    private static final int TRIANGLE_VERTS = 3;
    private static final int CIRCLE_VERTS = CIRCLE_SEGMENTS + 2;

    private int vaoQuad, vaoTriangle, vaoCircle;
    private Shader shader;
    private int uUseTextureLoc, uDepthLoc, uProjectionLoc;

    public Renderer() {
        vaoQuad     = createVAO(new float[]{
            -0.5f,-0.5f,0, 0,0,  0.5f,-0.5f,0, 1,0,  0.5f,0.5f,0, 1,1,
            -0.5f,-0.5f,0, 0,0,  0.5f,0.5f,0, 1,1,  -0.5f,0.5f,0, 0,1
        });
        vaoTriangle = createVAO(new float[]{
            0,0.5f,0, 0.5f,1,  -0.5f,-0.5f,0, 0,0,  0.5f,-0.5f,0, 1,0
        });
        vaoCircle   = createCircleVAO();
        setupShader();
    }

    private int createCircleVAO() {
        float[] v = new float[(CIRCLE_SEGMENTS + 2) * 5];
        v[0]=0; v[1]=0; v[2]=0; v[3]=0.5f; v[4]=0.5f;
        for (int i = 0; i <= CIRCLE_SEGMENTS; i++) {
            double a = Math.PI * 2 * i / CIRCLE_SEGMENTS;
            float cos = (float)Math.cos(a), sin = (float)Math.sin(a);
            v[(i+1)*5]   = cos*0.5f;
            v[(i+1)*5+1] = sin*0.5f;
            v[(i+1)*5+2] = 0;
            v[(i+1)*5+3] = (cos+1)*0.5f;
            v[(i+1)*5+4] = (sin+1)*0.5f;
        }
        return createVAO(v);
    }

    private int createVAO(float[] vertices) {
        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buf = BufferUtils.createFloatBuffer(vertices.length);
        buf.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, STRIDE, 0);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, STRIDE, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);
        return vao;
    }

    private void setupShader() {
        String vertSrc = """
            #version 330 core
            layout (location = 0) in vec3 aPos;
            layout (location = 1) in vec2 aTexCoord;
            uniform mat4 uProjection;
            uniform vec2 uOffset;
            uniform vec2 uScale;
            uniform float uRotation;
            uniform float uDepth;
            out vec2 vTexCoord;
            void main() {
                float s = sin(uRotation);
                float c = cos(uRotation);
                mat2 rot = mat2(c, s, -s, c);
                vec2 rotatedPos = rot * (aPos.xy * uScale);
                gl_Position = uProjection * vec4(rotatedPos + uOffset, uDepth, 1.0);
                vTexCoord = aTexCoord;
            }
            """;
        String fragSrc = """
            #version 330 core
            in vec2 vTexCoord;
            uniform vec3 uColor;
            uniform sampler2D uTexture;
            uniform int uUseTexture;
            out vec4 fragColor;
            void main() {
                if (uUseTexture == 1) {
                    fragColor = texture(uTexture, vTexCoord);
                    if (fragColor.a < 0.1) discard;
                } else {
                    fragColor = vec4(uColor, 1.0);
                }
            }
            """;
        shader = new Shader(vertSrc, fragSrc);
        uUseTextureLoc = GL20.glGetUniformLocation(shader.getProgramId(), "uUseTexture");
        uDepthLoc      = GL20.glGetUniformLocation(shader.getProgramId(), "uDepth");
        uProjectionLoc = GL20.glGetUniformLocation(shader.getProgramId(), "uProjection");
        shader.use();
        int uTexLoc = GL20.glGetUniformLocation(shader.getProgramId(), "uTexture");
        if (uTexLoc != -1) GL20.glUniform1i(uTexLoc, 0);
        setOrtho(-1, 1, -1, 1);
    }

    // Define el espacio 2D. Ej: setOrtho(0, width, height, 0) para pixeles.
    public void setOrtho(float left, float right, float bottom, float top) {
        shader.use();
        float[] m = {
            2f/(right-left), 0, 0, 0,
            0, 2f/(top-bottom), 0, 0,
            0, 0, 1f, 0,
            -(right+left)/(right-left), -(top+bottom)/(top-bottom), 0, 1f
        };
        GL20.glUniformMatrix4fv(uProjectionLoc, false, m);
    }

    // Llamar una vez antes de dibujar el primer frame.
    public void start() {
        shader.use();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }

    public void drawRect(float x, float y, float z, float w, float h, float rot, float r, float g, float b) {
        prepareDraw(vaoQuad, z, false);
        shader.setUniforms(x, y, w, h, rot, r, g, b);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, QUAD_VERTS);
    }

    public void drawTexture(Texture tex, float x, float y, float z, float w, float h, float rot) {
        tex.bind();
        prepareDraw(vaoQuad, z, true);
        shader.setUniforms(x, y, w, h, rot, 1, 1, 1);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, QUAD_VERTS);
    }

    public void drawTriangle(float x, float y, float z, float w, float h, float rot, float r, float g, float b) {
        prepareDraw(vaoTriangle, z, false);
        shader.setUniforms(x, y, w, h, rot, r, g, b);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, TRIANGLE_VERTS);
    }

    public void drawCircle(float x, float y, float z, float w, float h, float r, float g, float b) {
        prepareDraw(vaoCircle, z, false);
        shader.setUniforms(x, y, w, h, 0, r, g, b);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, CIRCLE_VERTS);
    }

    private void prepareDraw(int vao, float z, boolean useTexture) {
        GL30.glBindVertexArray(vao);
        GL20.glUniform1i(uUseTextureLoc, useTexture ? 1 : 0);
        GL20.glUniform1f(uDepthLoc, z);
    }

    public void cleanup() {
        shader.cleanup();
        GL30.glDeleteVertexArrays(vaoQuad);
        GL30.glDeleteVertexArrays(vaoTriangle);
        GL30.glDeleteVertexArrays(vaoCircle);
    }
}
