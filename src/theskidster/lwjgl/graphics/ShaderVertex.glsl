#version 330 core

layout (location = 0) in vec3 vPosition;
layout (location = 1) in vec3 vColor;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

out vec3 out_color;

void main() {
    gl_Position = uProjection * uView * uModel * vec4(vPosition, 1.0); 
    out_color = vColor;
}