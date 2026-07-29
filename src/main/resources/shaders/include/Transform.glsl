mat4 createTransformMatrix(vec3 v, vec3 s) {
    mat4 translationMatrix = mat4(1.0);
    translationMatrix[3] = vec4(v, 1.0);

    mat4 scaleMatrix = mat4(1.0);
    scaleMatrix[0][0] = s.x;
    scaleMatrix[1][1] = s.y;
    scaleMatrix[2][2] = s.z;

    return translationMatrix * scaleMatrix;
}