#version 430 core

in float height;
in float alpha;

out vec4 FragColor;

// TODO: move to uniforms
vec3 grassColor0 = vec3(102.0, 97.0, 3.0) / 256.0;
vec3 grassColor1 = vec3(83.0, 82.0, 2.0) / 256.0;

void main() {
  FragColor = vec4(mix(grassColor0, grassColor1, vec3(height)), alpha);
}
