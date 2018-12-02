package theskidster.lwjgl.entity;

/**
 * @author J Hoffman
 * Created: Nov 28, 2018
 */

public class EntityTriangle extends Entity {
    
    public EntityTriangle() {
        this.vertices = new float[] {
            //position              //color
             0.0f,  0.5f,  0.0f,    1.0f, 1.0f, 1.0f, //top
            -0.5f, -0.5f,  0.0f,    0.0f, 0.0f, 0.0f, //left
             0.5f, -0.5f,  0.0f,    0.5f, 0.5f, 0.5f, //right
        };
        
        //this shape really doesnt need indices, but whatever we'll include them anyway.
        this.indices = new int[] {
            0, 1, 2
        };
        
        this.init();
    }
    
}