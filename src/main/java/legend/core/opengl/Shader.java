package legend.core.opengl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20C.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20C.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20C.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20C.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20C.glAttachShader;
import static org.lwjgl.opengl.GL20C.glCompileShader;
import static org.lwjgl.opengl.GL20C.glCreateProgram;
import static org.lwjgl.opengl.GL20C.glCreateShader;
import static org.lwjgl.opengl.GL20C.glDeleteProgram;
import static org.lwjgl.opengl.GL20C.glDeleteShader;
import static org.lwjgl.opengl.GL20C.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20C.glGetProgrami;
import static org.lwjgl.opengl.GL20C.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20C.glGetShaderi;
import static org.lwjgl.opengl.GL20C.glGetUniformLocation;
import static org.lwjgl.opengl.GL20C.glLinkProgram;
import static org.lwjgl.opengl.GL20C.glShaderSource;
import static org.lwjgl.opengl.GL20C.glUniform1f;
import static org.lwjgl.opengl.GL20C.glUniform1i;
import static org.lwjgl.opengl.GL20C.glUniform3f;
import static org.lwjgl.opengl.GL20C.glUniform3fv;
import static org.lwjgl.opengl.GL20C.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.opengl.GL30C.glBindBufferBase;
import static org.lwjgl.opengl.GL31C.GL_INVALID_INDEX;
import static org.lwjgl.opengl.GL31C.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL31C.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31C.glUniformBlockBinding;

public class Shader {
  private static final Logger LOGGER = LogManager.getLogger(Shader.class.getName());

  private final int shader;

  public Shader(final Path vert, final Path frag) throws IOException {
    final int vsh = this.compileShader(vert, GL_VERTEX_SHADER);
    final int fsh = this.compileShader(frag, GL_FRAGMENT_SHADER);
    this.shader = this.linkProgram(vsh, fsh);
    glDeleteShader(vsh);
    glDeleteShader(fsh);
  }

  private int compileShader(final Path file, final int type) throws IOException {
    final int shader = glCreateShader(type);
    glShaderSource(shader, Files.readString(file));
    glCompileShader(shader);

    if(glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
      LOGGER.error("Shader compile error {}: {}", file, glGetShaderInfoLog(shader));
    }

    return shader;
  }

  private int linkProgram(final int vsh, final int fsh) {
    final int shader = glCreateProgram();
    glAttachShader(shader, vsh);
    glAttachShader(shader,fsh);
    glLinkProgram(shader);

    if(glGetProgrami(shader, GL_LINK_STATUS) == 0) {
      LOGGER.error("Program link error: {}", glGetProgramInfoLog(shader));
    }

    return shader;
  }

  public void bindUniformBlock(final CharSequence name, final int binding) {
    final int index = glGetUniformBlockIndex(this.shader, name);

    if(index == GL_INVALID_INDEX) {
      LOGGER.error("Uniform block {} not found in shader {}", name, this.shader);
    } else {
      glUniformBlockBinding(this.shader, index, binding);
    }
  }

  public void use() {
    glUseProgram(this.shader);
  }

  public void delete() {
    glDeleteProgram(this.shader);
  }

  private final FloatBuffer floatBuffer16 = BufferUtils.createFloatBuffer(16);

  private class Uniform {
    final int loc;

    private Uniform(final String name) {
      this.loc = glGetUniformLocation(Shader.this.shader, name);

      if(this.loc == GL_INVALID_INDEX) {
        LOGGER.error("Uniform {} not found in shader {}", name, Shader.this.shader);
      }
    }
  }

  public static class UniformBuffer {
    public static final int TRANSFORM = 0;
    public static final int TRANSFORM2 = 1;

    private final int id;

    public UniformBuffer(final long size, final int binding) {
      this.id = glGenBuffers();

      glBindBuffer(GL_UNIFORM_BUFFER, this.id);
      glBufferData(GL_UNIFORM_BUFFER, size, GL_DYNAMIC_DRAW);
      glBindBuffer(GL_UNIFORM_BUFFER, 0);

      glBindBufferBase(GL_UNIFORM_BUFFER, binding, this.id);
    }

    public void set(final FloatBuffer buffer) {
      this.set(0L, buffer);
    }

    public void set(final long offset, final FloatBuffer buffer) {
      glBindBuffer(GL_UNIFORM_BUFFER, this.id);
      glBufferSubData(GL_UNIFORM_BUFFER, offset, buffer);
      glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }

    public void set(final Matrix4fc mat) {
      this.set(0L, mat);
    }

    public void set(final long offset, final Matrix4fc matrix) {
      //TODO: zero instantiation
      final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
      matrix.get(buffer);

      this.set(offset, buffer);
    }

    public void set(final float value) {
      this.set(0L, value);
    }

    public void set(final long offset, final float value) {
      //TODO: zero instantiation
      final FloatBuffer buffer = BufferUtils.createFloatBuffer(1);
      buffer.put(value);
      buffer.flip();

      this.set(offset, buffer);
    }
  }

  public class UniformMat4 extends Uniform {
    public UniformMat4(final String name) {
      super(name);
    }

    public void set(final FloatBuffer buffer) {
      glUniformMatrix4fv(this.loc, false, buffer);
    }

    public void set(final Matrix4fc mat) {
      Shader.this.floatBuffer16.clear();
      mat.get(Shader.this.floatBuffer16);
      this.set(Shader.this.floatBuffer16);
    }
  }

  public class UniformVec3 extends Uniform {
    public UniformVec3(final String name) {
      super(name);
    }

    public void set(final FloatBuffer buffer) {
      glUniform3fv(this.loc, buffer);
    }

    public void set(final Vector3fc vec) {
      glUniform3f(this.loc, vec.x(), vec.y(), vec.z());
    }

    public void set(final float x, final float y, final float z) {
      glUniform3f(this.loc, x, y, z);
    }
  }

  public class UniformInt extends Uniform {
    public UniformInt(final String name) {
      super(name);
    }

    public void set(final int val) {
      glUniform1i(this.loc, val);
    }
  }

  public class UniformFloat extends Uniform {
    public UniformFloat(final String name) {
      super(name);
    }

    public void set(final float val) {
      glUniform1f(this.loc, val);
    }
  }
}
