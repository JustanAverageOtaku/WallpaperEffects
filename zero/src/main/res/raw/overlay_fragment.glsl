// Really doesn't feel right starting at the first line
precision mediump float;

uniform sampler2D u_Texture;
uniform vec4 u_OverlayColor;

varying vec2 v_TextureCoordinates;

void main()
{
    //vec4 color1 = vec4(1.0, 0.0, 0.0, 1.0);
    vec4 color2 = vec4(0.0, 0.0, 0.0, 0.0);
    //float dist = distance(v_TextureCoordinates, vec2(0.5, 0.5));
    float Xdist = distance(v_TextureCoordinates.x, 0.5);
    float yDist = distance(v_TextureCoordinates.y, 0.5);

    if (Xdist >= 0.37 || yDist >= 0.372)
    {
        //float mixValue = distance(v_TextureCoordinates, vec2(0.5, 0.5));
        vec4 color = mix(color2, u_OverlayColor, 1.0);
        gl_FragColor = u_OverlayColor;
    }
    else if (Xdist >= 0.35 || yDist >= 0.35)
    {
        //vec4 color = mix(color2, u_OverlayColor, 0.8);
        //gl_FragColor = u_OverlayColor;
        gl_FragColor = u_OverlayColor * vec4(0.8, 0.8, 0.8, 0.5);
    }
    else if (Xdist >= 0.31 || yDist >= 0.32)
    {
        //vec4 color = mix(color2, u_OverlayColor, 0.5);
        //gl_FragColor = u_OverlayColor;
        gl_FragColor = u_OverlayColor * vec4(0.5, 0.5, 0.5, 0.5);
    }
    else if (Xdist >= 0.25 || yDist >= 0.25)
    {
        //vec4 color = mix(color2, u_OverlayColor, 0.2);
        //gl_FragColor = u_OverlayColor;
        gl_FragColor = u_OverlayColor * vec4(0.2, 0.2, 0.2, 0.2);
    }
    else
    {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
    }





    //float mixValue = distance(v_TextureCoordinates, vec2(0.5, 0.5)) * 0.3;
    //vec4 color = mix(color2, u_OverlayColor, mixValue);

    //gl_FragColor = color; //vec4(color.rgb, mixValue);
}
