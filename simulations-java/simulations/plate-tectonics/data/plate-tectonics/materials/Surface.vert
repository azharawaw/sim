uniform mat4 g_WorldViewProjectionMatrix;

attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec2 inTexCoord;
attribute vec2 inTexCoord2;

varying vec2 texCoord;
varying vec2 texCoord2;
varying float lightStrength;

float clampFloat( in float value, in float valueMin, in float valueMax ) {
    if( value < valueMin ) {
        return valueMin;
    }
    if( value > valueMax ) {
        return valueMax;
    }
    return value;
}

void main(){
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
    texCoord = inTexCoord;
    texCoord2 = inTexCoord2;

    float oneLightStrength = clampFloat(dot(inNormal,normalize(vec3(-0.3,1,0.0))),0.0,1.0);
    lightStrength = oneLightStrength * oneLightStrength * 0.6 + 0.4;
}