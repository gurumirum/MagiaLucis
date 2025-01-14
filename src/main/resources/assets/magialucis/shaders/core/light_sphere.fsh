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

float lightStrength(in vec2 intersection, in float lightRadius) {
    float lightAmount = asin((intersection.y - intersection.x) / 2 / lightRadius);
    return exp(-intersection.x * 0.01) * lightAmount * lightAmount; // idk what i am doing here lol
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

    float innerLightRadius = LightRadius * 0.5;
    innerLightRadius *= step(0.35, innerLightRadius);
    vec2 innerIntersection = sphIntersect(worldNear,
            normalize(worldFar - worldNear),
            LightPosition.xyz,
            innerLightRadius);

    fragColor = vec4(min(
            vec3(0.95),
            smoothstep(0, 3, ColorModulator.xyz * color.xyz * lightStrength(intersection, LightRadius - 0.1)) +
                    vec3(smoothstep(0, 3.5, lightStrength(innerIntersection, innerLightRadius) - 0.2))
    ), color.a * ColorModulator.a);
}