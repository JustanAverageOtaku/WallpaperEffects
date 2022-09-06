uniform mat4 u_MVPMatrix; // Translated mvp matrix
//uniform mat4 u_OriginalMVPMatrix; // Original Untranslated mvp matrix
uniform mat4 u_ModelMatrix;
uniform vec3 u_ViewPosition;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_TexCoordinate;

varying vec2 v_TexCoordinate;
//varying vec3 v_ShiftingFactor;
//varying vec3 v_TangentLightPos;
varying vec3 v_TangentViewPosition;
varying vec3 v_TangentFragPos;
//varying vec2 v_VPos;
//varying vec2 vertCoordinate;
//varying vec3 v_ScaledCoordinates;


void main()
{
    //float disp;
    vec4 pos = u_MVPMatrix * (a_Position);
    //vec4 opos = u_OriginalMVPMatrix * a_Position;

    gl_Position = pos;
    v_TexCoordinate = a_TexCoordinate;

    vec3 fragPos = vec3(u_ModelMatrix * a_Position);

    vec3 tangent;
    vec3 binormal;
    vec3 normal = normalize(a_Normal*2.0 - 1.0);

    vec3 c1 = cross(normal, vec3(0.0, 0.0, 1.0));
    vec3 c2 = cross(normal, vec3(0.0, 1.0, 0.0));

    if (length(c1) > length(c2))
    {
        tangent = c1;
    }
    else
    {
        tangent = c2;
    }

    tangent = normalize(tangent);
    binormal = normalize(cross(a_Normal, tangent));

    vec3 T = normalize(mat3(u_ModelMatrix) * tangent);
    vec3 B = normalize(mat3(u_ModelMatrix) * binormal);
    vec3 N = normalize(mat3(u_ModelMatrix) * a_Normal);
    mat3 TBN = mat3(T, B, N);
    //vec4 scaledPos = u_ScaledMVPMatrix * a_Position;
    //vec3 lightpos = vec3(0.0, 0.0, 4.0);
    //v_TangentLightPos = lightpos * TBN;
    v_TangentViewPosition = u_ViewPosition * TBN;
    v_TangentFragPos = fragPos * TBN;
    //v_VPos = u_ViewPosition.xy;

    //v_ShiftingFactor = vec3(pos - opos);
    //v_ScaledCoordinates = vec3(opos - scaledPos);
    //vertCoordinate = vec2(pos);
}