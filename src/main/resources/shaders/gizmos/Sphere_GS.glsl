#version 430 core

layout (points) in;
layout (line_strip, max_vertices = 256) out;

uniform vec3 uSphereCenter;
uniform float uSphereRadius;
uniform mat4 uViewProjection;

const int uSegmentsLongitude = 8;
const int uSegmentsLatitude = 6;
const float M_PI = 3.1415926535;

void main() {
    for (int i = 0; i <= uSegmentsLatitude; ++i) {
        float lat0 = M_PI * (-0.5 + (float(i - 1) / uSegmentsLatitude));
        float z0 = uSphereRadius * sin(lat0);
        float zr0 = uSphereRadius * cos(lat0);

        float lat1 = M_PI * (-0.5 + (float(i) / uSegmentsLatitude));
        float z1 = uSphereRadius * sin(lat1);
        float zr1 = uSphereRadius * cos(lat1);

        for (int j = 0; j <= uSegmentsLongitude; ++j) {
            float lng = 2 * M_PI * (float(j - 1) / uSegmentsLongitude);
            float x = cos(lng);
            float y = sin(lng);

            vec3 pos0 = vec3(x * zr0, y * zr0, z0) + uSphereCenter;
            vec3 pos1 = vec3(x * zr1, y * zr1, z1) + uSphereCenter;

            gl_Position = uViewProjection * vec4(pos0, 1.0);
            EmitVertex();
            gl_Position = uViewProjection * vec4(pos1, 1.0);
            EmitVertex();
            EndPrimitive();
        }
    }
    for (int j = 0; j <= uSegmentsLongitude; ++j) {
        float lng0 = 2 * M_PI * (float(j - 1) / uSegmentsLongitude);
        float x0 = cos(lng0);
        float y0 = sin(lng0);

        float lng1 = 2 * M_PI * (float(j) / uSegmentsLongitude);
        float x1 = cos(lng1);
        float y1 = sin(lng1);

        for (int i = 0; i <= uSegmentsLatitude; ++i) {
            float lat = M_PI * (-0.5 + (float(i) / uSegmentsLatitude));
            float z = uSphereRadius * sin(lat);
            float zr = uSphereRadius * cos(lat);

            vec3 pos0 = vec3(x0 * zr, y0 * zr, z) + uSphereCenter;
            vec3 pos1 = vec3(x1 * zr, y1 * zr, z) + uSphereCenter;

            gl_Position = uViewProjection * vec4(pos0, 1.0);
            EmitVertex();
            gl_Position = uViewProjection * vec4(pos1, 1.0);
            EmitVertex();
            EndPrimitive();
        }
    }
}