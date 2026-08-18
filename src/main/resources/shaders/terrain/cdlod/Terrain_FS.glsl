#version 430

in float mapHeight_FS;
out vec4 color;

void main() {
    vec3 col = vec3(mapHeight_FS);
    color = vec4(col.xyz, 1.0f);
}