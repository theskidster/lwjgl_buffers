package theskidster.lwjgl.main;

import java.io.*;
import java.nio.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_load;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;
import theskidster.lwjgl.entity.EntityPyramid;
import theskidster.lwjgl.entity.EntityTriangle;
import theskidster.lwjgl.graphics.Shader;

/**
 * @author J Hoffman
 * Created: Nov 28, 2018
 */

public class MainContainer implements Runnable {

    private int scale = 3;
    private final int WIDTH = 384 * scale;
    private final int HEIGHT = 216 * scale;
    private int errCode;
    private int prog;
    private int vao;
    private int texID;
    
    private long context;
    
    private float lastPosX = WIDTH / 2;
    private float lastPosY = HEIGHT / 2;
    private float camYaw = -90.0f;
    private float camPitch = 0.0f;
    
    private boolean firstMouse = true;
    
    private Shader vs;
    private Shader fs;
    
    private FloatBuffer buffModel = BufferUtils.createFloatBuffer(16);
    private FloatBuffer buffView = BufferUtils.createFloatBuffer(16);
    private FloatBuffer buffProj = BufferUtils.createFloatBuffer(16);
    
    private ByteBuffer bb;
    
    private Matrix4f matProj = new Matrix4f();
    
    private Vector3f camPos = new Vector3f(0.0f, 0.0f, 3.0f);
    private Vector3f camDir = new Vector3f(0.0f, 0.0f, -0.5f);
    private Vector3f camUp = new Vector3f(0.0f, 1.0f, 0.0f);
    
    private float offset[] = {
        -1.0f,  0.0f,  0.0f,
        -2.0f,  1.0f, -3.0f,
         0.0f, -1.0f,  0.0f
    };
    
    private EntityTriangle e1;
    private EntityPyramid e2;
    
    /**
     * create context, setup window, etc.
     */
    public MainContainer() {    
        if(!glfwInit()) throw new RuntimeException("Are you running this on a potato?");
        
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        
        context = glfwCreateWindow(WIDTH, HEIGHT, "LWJGL3: buffer objects.", NULL, NULL);
        GLFWVidMode vm = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(context, (vm.width() - WIDTH) / 2, (vm.height() - HEIGHT) / 2);
        
        glfwSetKeyCallback(context, (long window, int key, int scancode, int action, int mods) -> {
            if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(context, true);
            
            //Room for improvement here, but it'll do for this example.
            if(key == GLFW_KEY_W) {
                camPos.set(camDir);
                adjustCam();
            }
            if(key == GLFW_KEY_S) {
                camPos.add(camDir.normalize());
                adjustCam();
            }
            if(key == GLFW_KEY_A) {
                camPos.add(camDir.cross(0.0f, 0.1f, 0.0f));
                adjustCam();
            }
            if(key == GLFW_KEY_D) {
                camPos.sub(camDir.cross(0.0f, 0.1f, 0.0f));
                adjustCam();
            }
        });
        
        glfwSetCursorPosCallback(context, GLFWCursorPosCallback.create((long window, double xpos, double ypos) -> {
            if(firstMouse) {
                xpos = (float) lastPosX;
                ypos = (float) lastPosY;
                firstMouse = false;
            }
            
            float xOffset = (float) (xpos - lastPosX);
            float yOffset = (float) (lastPosY - ypos);
            lastPosX = (float) xpos;
            lastPosY = (float) ypos;
            
            float sensitivity = 0.25f;
            xOffset *= sensitivity;
            yOffset *= sensitivity;
            
            camYaw += xOffset;
            camPitch += yOffset;
            
            if(camPitch > 89.0f) camPitch = 89.0f;
            if(camPitch < -89.0f) camPitch = -89.0f;
            
            adjustCam();
        }));
        
        glfwSetInputMode(context, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwMakeContextCurrent(context);
        glfwSwapInterval(1);
        glfwShowWindow(context);
        GL.createCapabilities();
        
        init();
    }
    
    /**
     * Create shaders, setup shader program, initialize buffers.
     */
    private void init() {
        vs = loadShader(GL_VERTEX_SHADER, "ShaderVertex.glsl");
        fs = loadShader(GL_FRAGMENT_SHADER, "ShaderFragment.glsl");
        
        prog = glCreateProgram();
        glAttachShader(prog, vs.id);
        glAttachShader(prog, fs.id);
        glLinkProgram(prog);
        glUseProgram(prog);
        
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        
        glEnable(GL_DEPTH_TEST);
    }

    /**
     * Simple rendering loop.
     */
    @Override
    public void run() {
        glUniformMatrix4fv(glGetUniformLocation(prog, "uProjection"), false, matProj
            .perspective((float) Math.toRadians(45.0), (float) WIDTH / HEIGHT, 0.1f, 64.0f)
            .get(buffProj));
        
        //If this were a state machine, the following lines would be placed in either an init method or a constructor
        loadTexture("img_eye.png", 512, 512);
        
        e1 = new EntityTriangle();
        for(int i = 0; i < 3; i++) e1.instancedInit(offset);
        
        e2 = new EntityPyramid();
        
        while(!glfwWindowShouldClose(context)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glClearColor((51f / 255f), (153f / 255f), (255f / 255f), 1.0f);
            
            glUniformMatrix4fv(glGetUniformLocation(prog, "uView"), false, new Matrix4f()
                .lookAt(camPos, camDir, camUp)
                .get(buffView));
            
            e1.bindTextured();
            e1.instancedBind();
                glUniformMatrix4fv(glGetUniformLocation(prog, "uModel"), false, new Matrix4f()
                    .get(buffModel));
                glUniform1i(glGetUniformLocation(prog, "uTexEnabled"), 1);
            e1.instancedRender(6);
            
            e2.bind();
                glUniform1i(glGetUniformLocation(prog, "uTexEnabled"), 0);
                glUniformMatrix4fv(glGetUniformLocation(prog, "uModel"), false, new Matrix4f()
                    .translate(1.0f, 0.0f, 0.0f)
                    .get(buffModel));
            e2.render();
            
            glfwSwapBuffers(context);
            glfwPollEvents();
            
            errCode = glGetError();
            if(errCode != GL_NO_ERROR) throw new RuntimeException("OPENGL ERROR: " + errCode);
        }
        glfwTerminate();
    }
    
    /**
     * Parses the glsl file provided and sets it to whatever type of shader specified.
     * 
     * @param type      - the shader type (vertex, fragment, etc.)
     * @param fileName  - glsl file containing the shader code to be read from.
     * @return          - a new shader object that compiles and links the new shader.
     */
    public Shader loadShader(int type, String fileName) {
        StringBuilder sb = new StringBuilder();
        
        try(InputStream in = getClass().getResourceAsStream("/theskidster/lwjgl/graphics/" + fileName); BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while((line = br.readLine()) != null) sb.append(line).append("\n");
        } catch(IOException e) {
            e.printStackTrace();
            throw new RuntimeException("failed to parse glsl file.");
        }
        CharSequence src = sb.toString();
        return new Shader(type, src);
    }
    
    /**
     * Loading textures is a bit tricky in modern OpenGL, since we first need to manually allocate data for the buffer which
     * holds the texture info, luckily STBI does most of the heavy lifting here and we just need to pass it the data we parsed
     * from the file. Regardless, I'd recommend creating a texture class and grouping that inside an entity object, but for 
     * this example having the load method here will work just fine since we only have one image.
     * 
     * @param fileName  - name of the file to load.
     * @param width     - width of the image. (in pixels)
     * @param height    - height of the image.
     */
    public void loadTexture(String fileName, int width, int height) {
        try(MemoryStack ms = MemoryStack.stackPush()) {
            IntBuffer w = ms.mallocInt(1);
            IntBuffer h = ms.mallocInt(1);
            IntBuffer c = ms.mallocInt(1);
            
            bb = stbi_load(getClass().getResource("/theskidster/lwjgl/assets/").toString().substring(6) + fileName, w, h, c, STBI_rgb_alpha);
        }
        
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, bb);
    }
    
    /**
     * Used to readjust the camera, perhaps an event based approach would work better here?
     */
    public void adjustCam() {
        camDir.x = (float) (Math.cos(Math.toRadians(camYaw)) * Math.cos(Math.toRadians(camPitch)));
        camDir.y = (float) Math.sin(Math.toRadians(camPitch));
        camDir.z = (float) (Math.sin(Math.toRadians(camYaw)) * Math.cos(Math.toRadians(camPitch)));
        camDir.normalize();
        camDir.add(camPos);
    }
    
}