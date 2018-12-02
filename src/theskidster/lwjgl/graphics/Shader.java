package theskidster.lwjgl.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author J Hoffman
 * Created: Nov 28, 2018
 */

public class Shader {
    
    public int id;
    
    public Shader(int type, CharSequence src) {
        id = glCreateShader(type);
        glShaderSource(id, src);
        glCompileShader(id);
        
        int status = glGetShaderi(id, GL_COMPILE_STATUS);
        if(status != GL_TRUE) throw new RuntimeException(glGetShaderInfoLog(id));
    }
    
}