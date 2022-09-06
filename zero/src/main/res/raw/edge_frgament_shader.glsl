precision mediump float;

//uniform sampler2D u_Texture;
uniform float u_XDisplacement;
uniform float u_YDisplacement;
uniform vec2 u_StartPos;
uniform vec2 u_EndPos;

varying vec2 v_TextureCoordinates;


float PixelToTextureSpace(float point, float dim, float sign)
{
    float offset = 50.0 * sign;

    return (point + offset)/dim;
}

void main()
{
    float thickness = 100.0;
    float xThickness = 100.0; //115.0;
    float width = 1080.0;
    float height = 2400.0;
    float psign = 1.0;
    float nsign = -1.0;

    if (v_TextureCoordinates.x > (xThickness/width) && v_TextureCoordinates.x < (width-xThickness)/width && v_TextureCoordinates.y >= (thickness/height) && v_TextureCoordinates.y <= (height-thickness)/height)
    {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
    }
    else
    {
        //vec3 color1 = vec3(1.0, 0.55, 0.0);
        //vec3 color2 = vec3(0.226, 0.0, 0.615);
        //vec3 color2 = vec3(0.0, 0.0, 0.0);
        //float mixValue = distance(v_TextureCoordinates, vec2(u_XDisplacement, u_YDisplacement));
        //vec3 color = mix(color1, color2, mixValue);
        //gl_FragColor = vec4(color.rgb, mixValue);

        if (u_EndPos.y == 50.0)
        {
            if (v_TextureCoordinates.x <= PixelToTextureSpace(u_EndPos.x, width, psign) && v_TextureCoordinates.y <= PixelToTextureSpace(u_EndPos.y, height, psign))
            {
                gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);
            }
            else
            {
                gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
            }
        }
        else if (u_EndPos.x == 1030.0)
        {
            if (v_TextureCoordinates.x <= PixelToTextureSpace(u_StartPos.x, width, psign) && v_TextureCoordinates.y > PixelToTextureSpace(u_StartPos.y, height, psign))
            {
                gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
            }
            else if (v_TextureCoordinates.x < PixelToTextureSpace(u_EndPos.x, width, psign) && v_TextureCoordinates.y > PixelToTextureSpace(u_EndPos.y, height, psign))
            {
                gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
            }
            else if (v_TextureCoordinates.x <= PixelToTextureSpace(1030.0, width, nsign) && v_TextureCoordinates.y >= PixelToTextureSpace(2350.0, height, nsign))
            {
                gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
            }
            else
            {
                gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);
            }
        }
        else if (u_EndPos.y == 2350.0)
        {
            if (v_TextureCoordinates.x <= PixelToTextureSpace(50.0, width, psign) && v_TextureCoordinates.y > PixelToTextureSpace(50.0, height, psign) && v_TextureCoordinates.y < PixelToTextureSpace(2350.0, height, nsign))
            {
                gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
            }
            else if ((v_TextureCoordinates.x >= PixelToTextureSpace(u_StartPos.x, width, nsign) && v_TextureCoordinates.y <= PixelToTextureSpace(u_StartPos.y, height, psign)) || v_TextureCoordinates.x >= PixelToTextureSpace(1030.0, width, nsign))
            {
                gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);
            }
            else if (v_TextureCoordinates.x >= PixelToTextureSpace(u_EndPos.x, width, nsign)) //&& v_TextureCoordinates.y < PixelToTextureSpace(u_EndPos.y, height, nsign))
            {
                gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);
            }
            //else if (v_TextureCoordinates.x <= PixelToTextureSpace(1030.0, width, nsign) && v_TextureCoordinates.y >= PixelToTextureSpace(2350.0, height, nsign))
            //{
            //    gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
            //}
            else
            {
                gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
            }
        }
        else if (u_EndPos.x == 50.0 && u_EndPos.y != 50.0)
        {
            if (v_TextureCoordinates.x <= PixelToTextureSpace(u_StartPos.x, width, psign) && v_TextureCoordinates.y > PixelToTextureSpace(u_StartPos.y, height, psign) && v_TextureCoordinates.y < PixelToTextureSpace(u_EndPos.y, height, nsign))
            {
                gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
            }
            else
            {
                gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);
            }
        }
        else if (u_EndPos.x == 50.0 && u_EndPos.y == 50.0)
        {
            gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        }
    }

}
