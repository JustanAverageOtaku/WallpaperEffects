precision highp float;

varying vec3 v_FragPosition;
varying vec2 v_TexCoordinate;
varying vec3 v_TangentViewPosition;
varying vec3 v_TangentFragPosition;

uniform sampler2D u_Texture;
uniform sampler2D u_TextureNormal;
uniform sampler2D u_TextureHeight;
uniform float uf_HeightScale;


vec2 ParallaxMapping(vec2 tc_TexCoords, vec3 vd_ViewDir)
{
    float height = texture2D(u_TextureHeight, tc_TexCoords).r;
    vec2 p = vd_ViewDir.xy / vd_ViewDir.z * (height * uf_HeightScale);

    return tc_TexCoords - p;
}

void main()
{
    vec3 vd_ViewDir = normalize(v_TangentViewPosition - v_TangentFragPosition);
    vec2 tc_TexCoords = v_TexCoordinate;
//
//
    if (texture2D(u_Texture, tc_TexCoords).a > 0.0)
    {
        tc_TexCoords = ParallaxMapping(v_TexCoordinate, vd_ViewDir);
        if (tc_TexCoords.x > 1.0 || tc_TexCoords.y > 1.0 || tc_TexCoords.x < 0.0 || tc_TexCoords.y < 0.0)
        {
            discard;
        }
        vec3 colour = texture2D(u_Texture, tc_TexCoords).rgb;
        vec3 ambient = 0.1 * colour;
        gl_FragColor = texture2D(u_Texture, tc_TexCoords);
        //gl_FragColor = vec4(colour, 1.0);
    }
    else
    {
        gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
    }
    //
}
