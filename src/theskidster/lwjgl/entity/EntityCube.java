package theskidster.lwjgl.entity;

/**
 * @author J Hoffman
 * Created: Nov 28, 2018
 */

public class EntityCube extends Entity {
    
    public EntityCube() {
        this.vertices = new float[] {
            //position              //color
             0.5f,  0.5f,  0.5f,       1.0f, 0.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,       0.0f, 1.0f, 0.0f,
            -0.5f, -0.5f,  0.5f,       0.0f, 0.0f, 1.0f,
             0.5f, -0.5f,  0.5f,       1.0f, 1.0f, 0.0f,
             
             0.5f,  0.5f, -0.5f,       1.0f, 0.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,       0.0f, 1.0f, 0.0f,
            -0.5f, -0.5f, -0.5f,       0.0f, 0.0f, 1.0f,
             0.5f, -0.5f, -0.5f,       1.0f, 1.0f, 0.0f,
        };
        
        this.indices = new int[] {
            
        };
    }
    
}