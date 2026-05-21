package com.graphics.core;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

// Wrapper minimo para compilar y usar un shader 2D.
public class Shader {

    private final int programId;
    private final int uOffsetLoc, uScaleLoc, uColorLoc, uRotationLoc;

    public Shader(String vertexSrc, String fragmentSrc) {
        int vs = compile(vertexSrc, GL20.GL_VERTEX_SHADER);
        int fs = compile(fragmentSrc, GL20.GL_FRAGMENT_SHADER);

        programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vs);
        GL20.glAttachShader(programId, fs);
        GL20.glLinkProgram(programId);

        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Shader link error: " + GL20.glGetProgramInfoLog(programId));
        }

        uOffsetLoc   = GL20.glGetUniformLocation(programId, "uOffset");
        uScaleLoc    = GL20.glGetUniformLocation(programId, "uScale");
        uRotationLoc = GL20.glGetUniformLocation(programId, "uRotation");
        uColorLoc    = GL20.glGetUniformLocation(programId, "uColor");

        GL20.glDeleteShader(vs);
        GL20.glDeleteShader(fs);
    }

    private int compile(String src, int type) {
        int s = GL20.glCreateShader(type);
        GL20.glShaderSource(s, src);
        GL20.glCompileShader(s);
        if (GL20.glGetShaderi(s, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String label = (type == GL20.GL_VERTEX_SHADER) ? "Vertex" : "Fragment";
            throw new RuntimeException(label + " shader: " + GL20.glGetShaderInfoLog(s));
        }
        return s;
    }

    public void use() { GL20.glUseProgram(programId); }

    // Posicion, tamano, rotacion y color (RGB) en espacio 2D.
    public void setUniforms(float x, float y, float w, float h, float rot, float r, float g, float b) {
        GL20.glUniform2f(uOffsetLoc, x, y);
        GL20.glUniform2f(uScaleLoc, w, h);
        GL20.glUniform1f(uRotationLoc, rot);
        GL20.glUniform3f(uColorLoc, r, g, b);
    }

    public void cleanup() { GL20.glDeleteProgram(programId); }
    public int getProgramId() { return programId; }
}
