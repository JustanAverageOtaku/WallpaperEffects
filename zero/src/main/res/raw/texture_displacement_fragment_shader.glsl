precision highp float;

uniform sampler2D u_Texture;
uniform sampler2D u_DepthMap;
uniform vec2 u_Gyro;

varying vec2 v_TextureCoordinates;

void main()
{
    //const float whiteExcess = 0.5;
    //const float normalizingvalue = 0.2;

    float mapColor = texture2D(u_DepthMap, v_TextureCoordinates).r;
    //if (mapColor > whiteExcess)
    //{
        //mapColor = normalizingvalue;
    //}

    vec2 displacement = vec2(u_Gyro * mapColor * 0.3);
    displacement.y = 0.0;

    gl_FragColor = texture2D(u_Texture, v_TextureCoordinates + displacement);

    if(gl_FragColor.a == 0.0)
    {
        discard;
    }
}
