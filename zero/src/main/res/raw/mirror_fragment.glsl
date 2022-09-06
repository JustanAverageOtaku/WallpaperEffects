precision mediump float;

uniform sampler2D u_Texture;
uniform vec4 u_ReflectionColor;

varying vec2 v_TextureCoordinates;

void main()
{
    vec4 color = texture2D(u_Texture, v_TextureCoordinates);
    color *= u_ReflectionColor; //vec4(0.4, 0.4, 0.4, 1.0);
    //gl_FragColor = vec4(texture2D(u_Texture, v_TextureCoordinates).rgb, 0.5);
    //gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
    gl_FragColor = color;
}
