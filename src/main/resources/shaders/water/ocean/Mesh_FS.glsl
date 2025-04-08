#version 430 core

// Output of the fragment shader - output color
layout (location = 0) out vec4 FragColor;

// Inputs coming from the vertex shader
in struct data
{
    vec3 position; // position in the world space
    vec3 normal;   // normal in the world space
    vec3 color;    // current color on the fragment
    vec2 uv;       // current uv-texture on the fragment

} fragment;

uniform mat4 view;       // View matrix (rigid transform) of the camera - to compute the camera position
uniform vec3 sunVector;
uniform float sunIntensity;
uniform vec3 sunColor;

vec3 u_bg_color = vec3(157.0, 221.0, 237.0) / 256.0;
float u_fog_dmax = 5000.f;

float diffuse(vec3 direction, vec3 normal, float intensity)
{
    return max(0.04, dot(normal, -direction) * intensity);
}

void main() {

    // Compute the position of the center of the camera
    mat3 O = transpose(mat3(view));                   // get the orientation matrix
    vec3 last_col = vec3(view * vec4(0.0, 0.0, 0.0, 1.0)); // get the last column
    vec3 camera_position = -O * last_col;

    // Renormalize normal
    vec3 N = normalize(fragment.normal);

    //FOG effect
    float dmax = u_fog_dmax;
    float d = sqrt(dot(camera_position - fragment.position, camera_position - fragment.position));
    float a_bruma = min(d / dmax, 1.0f);
    vec3 color_bruma = u_bg_color;

    // Compute the base color of the object based on: vertex color, uniform color, and texture
    vec3 color_object = fragment.color;

    // Change color object according to the view angle
    vec3 w_color0 = vec3(0.15f, 0.4f, 0.5f);
    vec3 w_color1 = vec3(0.1f, 0.15f, 0.3f);
    float cosTheta = clamp(dot(-normalize(camera_position), N), 0.0f, 1.0f);

    color_object *= mix(w_color0, w_color1, cosTheta);

    float diffuse = diffuse(-sunVector, N, sunIntensity);
    vec3 color_shading = diffuse * color_object * sunColor;
    color_shading = color_shading * (1 - a_bruma) + a_bruma * color_bruma;

    // Output color, with the alpha component
    FragColor = vec4(color_shading, 1.0f);
}