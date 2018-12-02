package theskidster.lwjgl.entity;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author J Hoffman
 * Created: Nov 28, 2018
 */

public abstract class Entity {
    
    public int vbo;
    public int ibo;
    
    public float vertices[];
    public int indices[];
    
    /**
     * Initialize this entities buffer objects.
     */
    public void init() {
        this.vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, this.vertices, GL_STATIC_DRAW);
        this.ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, this.indices, GL_DYNAMIC_DRAW);
    }
    
    /**
     * Bind the buffer required to render this entity.
     */
    public void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (6 * Float.BYTES), 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, (6 * Float.BYTES), (3 * Float.BYTES));
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
    }
    
    /**
     * Render this entity.
     */
    public void render() {
        glDrawElements(GL_TRIANGLES, this.indices.length, GL_UNSIGNED_INT, 0);
    }
    
    /**
     * Render multiple instances of this entity.
     */
    public void renderInstance() {
        
    }
    
}