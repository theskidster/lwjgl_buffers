#version 330 core

in vec3 out_color;
in vec2 out_texture;

uniform sampler2D tex;

uniform bool uTexEnabled; //true for textures

out vec4 finalColor;

void main() {
    if(uTexEnabled) {
        vec4 finalTexture = texture(tex, out_texture);
        if(finalTexture.a == 0) discard;
        finalColor = finalTexture;
    } else {
        finalColor = vec4(out_color, 1.0);
    }
}