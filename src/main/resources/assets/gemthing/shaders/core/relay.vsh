#version 150

#define Pi 3.1415926535897932384626433832795

in vec3 Position;
in vec4 Color;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    mat4 invModelView = inverse(ModelViewMat);
    vec3 look = normalize((invModelView *  vec4(0, 0, 1, 1)).xyz);

    float thing = acos(dot(Normal, look)) / Pi;

    vertexColor = Color;
    vertexColor.a *= mix(0.2, 0.8, thing);
}
