#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;
out mat4 invProj;
out mat4 invModelView;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    vertexColor = Color;
    invProj = inverse(ProjMat);
    invModelView = inverse(ModelViewMat);
}
