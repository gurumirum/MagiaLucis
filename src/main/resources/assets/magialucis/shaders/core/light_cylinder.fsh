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

// https://www.shadertoy.com/view/4lcSRn
// https://iquilezles.org/articles/intersectors/
// The MIT License
// Copyright © 2016 Inigo Quilez
float iCylinder(in vec3 ro, in vec3 rd, in vec3 pa, in vec3 pb, float ra) {
    vec3 ba = pb - pa;
    vec3 oc = ro - pa;

    float baba = dot(ba, ba);
    float bard = dot(ba, rd);
    float baoc = dot(ba, oc);

    float k2 = baba - bard * bard;
    float k1 = baba * dot(oc, rd) - baoc * bard;
    float k0 = baba * dot(oc, oc) - baoc * baoc - ra * ra * baba;

    if(k2 == 0.0) {
        float ta = -dot(ro - pa, ba) / bard;
        float tb = ta + baba / bard;

        vec4 pt = (bard > 0.0) ? vec4(pa, -ta) : vec4(pb, tb);

        vec3 q = ro + rd * abs(pt.w) - pt.xyz;
        if(dot(q, q) > ra * ra) {
            return -1.0;
        }

        return abs(pt.w);
    }

    float h = k1 * k1 - k2 * k0;
    if(h < 0.0)
        return -1.0;
    h = sqrt(h);
    float t = (-k1 - h) / k2;

    // body
    float y = baoc + t * bard;
    if(y > 0.0 && y < baba)
        return t;

    // caps
    t = (((y < 0.0) ? 0.0 : baba) - baoc) / bard;
    if(abs(k1 + k2 * t) < h)
        return t;

    return -1.0;
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

    float frontIntersection = iCylinder(worldNear,
            normalize(worldFar - worldNear),
            LightStart.xyz,
            LightEnd.xyz,
            LightRadius - 0.1);

    float offsetAmount = max(frontIntersection, 0) + length(LightStart.xyz - LightEnd.xyz) + 1;
    vec3 offset = normalize(worldFar - worldNear) * offsetAmount;

    float backIntersection = iCylinder(worldNear + offset,
            normalize(worldNear - worldFar),
            LightStart.xyz,
            LightEnd.xyz,
            LightRadius - 0.1);

    if(backIntersection <= 0) {
        discard;
    }

    vec2 intersection = vec2(frontIntersection, -backIntersection + offsetAmount);

    fragColor = min(
            vec4(vec3(0.95), 1),
            color * ColorModulator * lightStrength(intersection));
    
    // fragColor = vec4(intersection.xy, 0, 1);
}