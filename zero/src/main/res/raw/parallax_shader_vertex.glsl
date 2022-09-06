attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_TexCoordinate;

uniform mat4 u_MMatrix;
uniform mat4 u_MVPMatrix;
uniform vec3 u_ViewPosition;

varying vec3 v_FragPosition;
varying vec2 v_TexCoordinate;
varying vec3 v_TangentViewPosition;
varying vec3 v_TangentFragPosition;

void main()
{
    gl_Position = u_MVPMatrix * a_Position;
    v_FragPosition = vec3(u_MMatrix * a_Position);
    v_TexCoordinate = a_TexCoordinate;
//
    vec3 t_Tangent;
    vec3 b_Binormal;
    vec3 n_Normal = normalize(a_Normal*2.0 - 1.0);
//
    vec3 c1 = cross(n_Normal, vec3(0.0, 0.0, 1.0));
    vec3 c2 = cross(n_Normal, vec3(0.0, 1.0, 0.0));
//
    if (length(c1) > length(c2))
    {
        t_Tangent = c1;
        t_Tangent = normalize(t_Tangent);
    }
    else
    {
        t_Tangent = c2;
        t_Tangent = normalize(t_Tangent);
    }

    b_Binormal = normalize(cross(a_Normal, t_Tangent));

    vec3 T = normalize(mat3(u_MMatrix) * t_Tangent);
    vec3 B = normalize(mat3(u_MMatrix) * b_Binormal);
    vec3 N = normalize(mat3(u_MMatrix) * a_Normal);

    mat3 TBN = mat3(T, B, N);

    v_TangentViewPosition = u_ViewPosition * TBN;
    v_TangentFragPosition = u_ViewPosition * TBN;
}
