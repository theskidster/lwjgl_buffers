package theskidster.lwjgl.entity;

/**
 * @author J Hoffman
 * Created: Nov 30, 2018
 */

public class EntityPyramid extends Entity {
    
    public EntityPyramid() {
        this.vertices = new float[] {
            //sides                 //colors
             0.0f,  0.5f,  0.0f,    1.0f, 0.0f, 0.0f, //top
            -0.5f, -0.5f,  0.5f,    0.0f, 1.0f, 0.0f, //left
             0.5f, -0.5f,  0.5f,    0.0f, 0.0f, 1.0f, //right
             0.0f, -0.5f, -0.5f,    1.0f, 1.0f, 0.0f, //back
        };
        
        this.indices = new int[] {
            0, 1, 2, //front
            0, 1, 3, //left side
            0, 2, 3, //right side
            1, 2, 3  //bottom
        };
        
        this.init();
    }
    
}