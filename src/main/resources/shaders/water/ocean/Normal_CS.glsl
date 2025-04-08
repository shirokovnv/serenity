#version 430 core

#define WORK_GROUP_DIM 16

layout (local_size_x = WORK_GROUP_DIM, local_size_y = WORK_GROUP_DIM) in;

layout (binding = 0, rgba32f) readonly uniform image2D u_dy_map;
layout (binding = 1, rgba32f) readonly uniform image2D u_dx_map;
layout (binding = 2, rgba32f) readonly uniform image2D u_dz_map;
layout (binding = 3, rgba32f) writeonly uniform image2D u_normal_map;
layout (binding = 4, rgba32f) writeonly uniform image2D u_displacement_map;

vec3 load_disp(in ivec2 pixel_coord) {
    return vec3(imageLoad(u_dx_map, pixel_coord).r, imageLoad(u_dy_map, pixel_coord).r, imageLoad(u_dz_map, pixel_coord).r);
}

vec3 load_normal(in ivec2 pixel_coord) {
    return vec3(imageLoad(u_dx_map, pixel_coord).b, imageLoad(u_dy_map, pixel_coord).b, imageLoad(u_dz_map, pixel_coord).b);
}

void main()
{
    ivec2 pixel_coord = ivec2(gl_GlobalInvocationID.xy);

    imageStore(u_normal_map, pixel_coord, vec4(load_normal(pixel_coord).rbg, 1.f));
    imageStore(u_displacement_map, pixel_coord, vec4(load_disp(pixel_coord).rbg, 1.f));
}