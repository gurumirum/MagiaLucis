#version 150

#define Pi 3.1415926535897932384626433832795

in vec4 vertexColor;
in mat4 invProj;
in mat4 invModelView;

uniform vec2 ScreenSize;
uniform vec4 ColorModulator;
uniform vec4 LightStart; // it's actually vec3 but mojang's shit ass json format doesn't allow me to specify that
uniform vec4 LightEnd;
uniform float LightRadius;

out vec4 fragColor;

// https://iquilezles.org/articles/intersectors/
// The MIT License
// Copyright © 2016 Inigo Quilez
vec2 cylIntersect(in vec3 ro, in vec3 rd, in vec3 cb, in vec3 ca, float cr){
    vec3  oc = ro - cb;
    float card = dot(ca,rd);
    float caoc = dot(ca,oc);
    float a = 1.0 - card*card;
    float b = dot( oc, rd) - caoc*card;
    float c = dot( oc, oc) - caoc*caoc - cr*cr;
    float h = b*b - a*c;
    if( h<0.0 ) return vec2(-1.0); //no intersection
    h = sqrt(h);
    return vec2(-b-h,-b+h)/a;
}

float intersectionPoint(in vec3 worldNear, in float point, in vec3 rayDirection, in vec3 cylDirection) {
    return dot((worldNear + point * rayDirection) - LightStart.xyz, cylDirection);
}

float lightStrength(in vec2 intersection, in float cylLen, in float cylRadius, in vec3 worldNear, in vec3 rayDirection, in vec3 cylDirection) { // TODO shit shitfuckshitshitshit
    float fogAmount = exp(-intersection.x * 0.1);

    float midpoint = (intersection.y + intersection.x) / 2;
    float b = intersectionPoint(worldNear, midpoint, rayDirection, cylDirection);
    float c = length((worldNear + midpoint * rayDirection) - LightStart.xyz);
    float a = sqrt(c * c - b * b);

    return fogAmount * sin((1 - smoothstep(0, cylRadius, a)) * Pi / 2)
            * smoothstep(-0.1, 0, c)
            * (1 - smoothstep(cylLen, cylLen + 0.1, c));
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
    vec3 rayDirection = normalize(worldFar - worldNear);
    vec3 cylDirection = normalize(LightEnd.xyz - LightStart.xyz);
    float cylLen = length(LightEnd.xyz - LightStart.xyz);

    vec2 intersection = cylIntersect(worldNear,
            rayDirection,
            LightStart.xyz,
            cylDirection,
            LightRadius - 0.1);

    if(intersection.y <= 0) {
        discard;
    }

    float innerLightRadius = LightRadius * 0.4;
    vec2 innerIntersection = cylIntersect(worldNear,
            rayDirection,
            LightStart.xyz,
            cylDirection,
            innerLightRadius);

    float beamLightCylRadius = LightRadius * 0.02;
    vec2 beamIntersection = cylIntersect(worldNear,
            rayDirection,
            LightStart.xyz,
            cylDirection,
            beamLightCylRadius);

    float beamNear = intersectionPoint(worldNear, beamIntersection.x, rayDirection, cylDirection);
    float beamFar = intersectionPoint(worldNear, beamIntersection.y, rayDirection, cylDirection);

    fragColor = vec4(max(
            min(
                    vec3(0.95),
                    smoothstep(0, 2.5, ColorModulator.xyz * color.xyz * lightStrength(intersection, cylLen, LightRadius - 0.1, worldNear, rayDirection, cylDirection)) +
                            vec3(step(0.15, innerLightRadius) * smoothstep(0, 2.5, lightStrength(innerIntersection, cylLen, innerLightRadius, worldNear, rayDirection, cylDirection) - 0.2))
            ),
            // beam
            vec3(step(0.005, beamLightCylRadius) *
                    step(0, beamIntersection.y) *
                    step(min(beamNear, beamFar), cylLen) *
                    step(0, max(beamNear, beamFar)))
    ), color.a * ColorModulator.a) /* + vec4(0, 0.1, 0, 0) */;
}