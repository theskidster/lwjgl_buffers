package theskidster.lwjgl.entity;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31C.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * @author J Hoffman
 * Created: Nov 28, 2018
 */

public abstract class Entity {
    
    public int vbo;
    public int ibo;
    public int obo; //(position) offset buffer object
    
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
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, this.indices, GL_STATIC_DRAW);
    }
    
    public void instancedInit(float[] positions) {
        init();
        
        this.obo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, obo);
        glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
    }
    
    public void bindTextured() {
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
    }
    
    public void instancedBind() {
        glBindBuffer(GL_ARRAY_BUFFER, obo);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glVertexAttribDivisor(3, 3);
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
    public void instancedRender(int numEntites) {
        glDrawElementsInstanced(GL_TRIANGLES, this.indices.length, GL_UNSIGNED_INT, 0, numEntites);
    }
    
}