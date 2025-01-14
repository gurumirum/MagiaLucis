#version 150

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

float lightStrength(in vec2 intersection) {
    float fogAmount = exp(-intersection.x * 0.05);
    //float lightAmount = asin((intersection.y - intersection.x) / 2 / (LightRadius - 0.1));
    float lightAmount = 1 - pow(0.5, (intersection.y - intersection.x));
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
    vec3 rayDirection = normalize(worldFar - worldNear);
    vec3 cylDirection = normalize(LightEnd.xyz - LightStart.xyz);

    vec2 intersection = cylIntersect(worldNear,
            rayDirection,
            LightStart.xyz,
            cylDirection,
            LightRadius - 0.1);

    if(intersection.y <= 0) {
        discard;
    }

    vec2 p = vec2(
            dot((worldNear + intersection.x * rayDirection) - LightStart.xyz, cylDirection),
            dot((worldNear + intersection.y * rayDirection) - LightStart.xyz, cylDirection));

    float innerLightCylRadius = 0.015; //LightRadius / 5 - 0.1;
    vec2 innerIntersection = cylIntersect(worldNear,
            rayDirection,
            LightStart.xyz,
            cylDirection,
            innerLightCylRadius) * step(0, innerLightCylRadius);
    
    fragColor = max(
            vec4(step(0, innerIntersection.y)),
            min(
                    vec4(vec3(0.95), 1),
                    color * ColorModulator * lightStrength(intersection)
            )
    );

    // fragColor = vec4(p / length(LightEnd.xyz - LightStart.xyz), 0, 1);
}