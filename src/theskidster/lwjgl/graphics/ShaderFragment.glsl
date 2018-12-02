#version 330 core

in vec3 out_color;

out vec4 finalColor;

void main() {
    finalColor = vec4(out_color, 1.0);
}