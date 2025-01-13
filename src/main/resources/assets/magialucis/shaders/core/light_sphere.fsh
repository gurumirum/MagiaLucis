#version 150

in vec4 vertexColor;
in mat4 invProj;
in mat4 invModelView;

uniform vec2 ScreenSize;
uniform vec4 ColorModulator;
uniform vec4 LightPosition; // it's actually vec3 but mojang's shit ass json format doesn't allow me to specify that
uniform float LightRadius;

out vec4 fragColor;

// https://www.shadertoy.com/view/4d2XWV
// https://iquilezles.org/articles/intersectors/
// The MIT License
// Copyright © 2014 Inigo Quilez
vec2 sphIntersect(in vec3 ro, in vec3 rd, in vec3 ce, float ra) {
    vec3 oc = ro - ce;
    float b = dot(oc, rd);
    vec3 qc = oc - b * rd;
    float h = ra * ra - dot(qc, qc);
    if(h < 0.0)
        return vec2(-1); // no intersection
    h = sqrt(h);
    return vec2(-b - h, -b + h);
}

float lightStrength(in vec2 intersection) {
    float fogAmount = exp(-intersection.x * 0.05);
    float lightAmount = asin((intersection.y - intersection.x) / 2 / LightRadius);
    return lightAmount * fogAmount; // idk what i am doing here lol

    // return ((intersection.y - intersection.x) / 2) / sqrt(intersection.x);
}

void main() {
    vec4 color = vertexColor;
    if(color.a == 0.0) {
        discard;
    }

    vec2 normalizedXY = (gl_FragCoord.xy / ScreenSize - vec2(0.5, 0.5)) * 2.0;

    vec4 nearClip = invModelView * invProj * vec4(normalizedXY, -1, 1);
    vec4 farClip = invModelView * invProj * vec4(normalizedXY, -0.5, 1);

    vec3 worldNear = nearClip.xyz / nearClip.w;
    vec3 worldFar = farClip.xyz / farClip.w;

    vec2 intersection = sphIntersect(worldNear,
            normalize(worldFar - worldNear),
            LightPosition.xyz,
            LightRadius - 0.1);

    if(intersection.y <= 0) {
        discard;
    }

    fragColor = min(
            vec4(vec3(0.95), 1),
            color * ColorModulator * lightStrength(intersection));
}