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
    vec4 transformedVec = invModelView * vec4(0, 0, 1, 1);
    vec3 look = normalize(transformedVec.xyz / transformedVec.w);

    float thing = acos(dot(normalize(Normal), look)) / (Pi / 2);
    thing *= thing;
    thing -= 0.1;

    vertexColor = Color;
    vertexColor.a *= thing;
}
