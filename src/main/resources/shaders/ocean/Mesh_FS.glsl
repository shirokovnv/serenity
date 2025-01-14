#version 430 core

// Output of the fragment shader - output color
layout(location=0) out vec4 FragColor;

// Inputs coming from the vertex shader
in struct data
{
    vec3 position; // position in the world space
    vec3 normal;   // normal in the world space
    vec3 color;    // current color on the fragment
    vec2 uv;       // current uv-texture on the fragment

} fragment;

in float dy;

uniform sampler2D u_color_texture;
uniform mat4 view;       // View matrix (rigid transform) of the camera - to compute the camera position

vec3 u_bg_color = vec3(157.0,221.0,237.0)/256.0;
float u_fog_dmax = 10000.f;

// TODO: Move to light params
const vec3 direction = vec3(0.1, -1, 0.1);
const float intensity = 0.8;

float diffuse(vec3 direction, vec3 normal, float intensity)
{
    return max(0.04, dot(normal, -direction) * intensity);
}

void main() {

    // Compute the position of the center of the camera
    mat3 O = transpose(mat3(view));                   // get the orientation matrix
    vec3 last_col = vec3(view*vec4(0.0, 0.0, 0.0, 1.0)); // get the last column
    vec3 camera_position = -O*last_col;

    // Renormalize normal
    vec3 N = normalize(fragment.normal);

    //FOG effect
    float dmax = u_fog_dmax;
    float d = sqrt(dot(camera_position-fragment.position, camera_position - fragment.position));
    float a_bruma = min(d/dmax,1.f);
    vec3 color_bruma = u_bg_color;

    // Compute the base color of the object based on: vertex color, uniform color, and texture
    vec3 color_object  = fragment.color;

    // Change color object according to height displacement
    vec3 inner_color = vec3(36.0, 139.0, 171.0)/256.0;
    vec3 outter_color = vec3(1, 1, 1);
    color_object *= mix(inner_color, outter_color, dy/3.0);

    vec3 base_color = vec3(249.0/256.0, 215.0/256.0, 228.0/256.0);

    float diffuse = diffuse(direction, fragment.normal, intensity);
    vec3 color_shading = diffuse * base_color * color_object;
    color_shading = color_shading * (1-a_bruma) + a_bruma * color_bruma;

    // Texture color
    vec3 texColor = texture(u_color_texture, fragment.uv).rgb;

    // Output color, with the alpha component
    FragColor = vec4(color_shading, 1.0f );
    //FragColor = vec4(texColor, 1);
    //FragColor = vec4(1, dy, dy, 1);
}