#version 330 core

layout (location = 0) in vec3 vPosition;
layout (location = 1) in vec3 vColor;
layout (location = 2) in vec2 vTexCoords;
layout (location = 3) in vec3 vOffset;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

out vec3 out_color;
out vec2 out_texture;

void main() {
    gl_Position = uProjection * uView * uModel * vec4(vPosition + vOffset, 1.0);
    
    out_color = vColor;
    out_texture = vec2(vTexCoords);
}