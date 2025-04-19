#version 430 core

in float height;
in float alpha;

out vec4 FragColor;

uniform vec3 sunVector;
uniform vec3 sunColor;
uniform float sunIntensity;

// TODO: move to uniforms
vec3 grassColor0 = vec3(102.0, 97.0, 3.0) / 256.0;
vec3 grassColor1 = vec3(83.0, 82.0, 2.0) / 256.0;

// TODO: separate
float diffuse(vec3 direction, vec3 normal, float intensity)
{
  return max(0.04, dot(normal, -direction) * intensity);
}

void main() {
  FragColor = vec4(mix(grassColor0, grassColor1, vec3(height)), alpha);
  vec3 ambient = vec3(0.2, 0.2, 0.2);
  float diffuse = diffuse(-sunVector, vec3(0, 1, 0), sunIntensity);
  FragColor.rgb *= diffuse * sunColor + ambient;
}
