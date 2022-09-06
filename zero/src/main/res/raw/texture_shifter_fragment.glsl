precision highp float;
//precision mediump float;

uniform sampler2D u_Texture;
uniform sampler2D u_DepthMap;
//uniform sampler2D u_NormalMap;
uniform float u_HeightScale;


varying vec2 v_TexCoordinate;
//varying vec3 v_TangentLightPos;
varying vec3 v_TangentViewPosition;
varying vec3 v_TangentFragPos;
//varying vec2 v_VPos;


//const float pi = 3.14159265;
//const float width = 1080.0; //4913.0;
//const float height = 2400.0; //7369.0;


vec2 SimpleParallaxMapping(vec2 texCoords, vec3 viewDir)
{
    float height = 1.0 - texture2D(u_DepthMap, texCoords).r;
    return texCoords - viewDir.xy / viewDir.z * (height * u_HeightScale);
}

//vec2 Displacementmapping()
//{
//    vec4 displacementMapTexel = texture2D(u_DepthMap, v_TexCoordinate);
//    vec2 displacement = vec2(abs(v_VPos) * displacementMapTexel.g);
//    return displacement;
//}

vec2 ParallaxMapping(vec2 texCoords, vec3 viewDir)
{
    const float minLayers = 10.0;
    const float maxLayers = 50.0;
    const float condition = 1.0;
    //const float condition = 0.0;
    const float whitExcess = 0.3;
    const float blackExcess = 0.7;
    const float excessValue = 0.3;
    //const float minLayers = 5.0; || beforeDepth > 0.74 || beforeDepth > blackExcess
    //const float maxLayers = 10.0; || currentDepthMapValue > 0.74 || currentDepthMapValue > blackExcess
    float numLayers = mix(maxLayers, minLayers, abs(dot(vec3(0.0, 0.0, 1.0), viewDir)));

    float layerDepth = 1.0 / numLayers;
    float currentLayerDepth = 0.0;

    vec2 p = viewDir.xy / viewDir.z * u_HeightScale;
    vec2 deltaTexCoords = p / numLayers;

    vec2 currentTexCoords = texCoords;
    float currentDepthMapValue = (1.0 - texture2D(u_DepthMap, currentTexCoords).r);
    //float currentDepthMapValue = texture2D(u_DepthMap, currentTexCoords).r;
    if ((currentDepthMapValue < whitExcess) && condition == 1.0)
    {
        currentDepthMapValue = excessValue;
    }
    //if ((currentDepthMapValue > blackExcess) && condition == 1.0)
    //{
    //    currentDepthMapValue = excessValue;
    //}

    while (currentLayerDepth < currentDepthMapValue)
    {
        currentTexCoords -= deltaTexCoords;
        currentDepthMapValue = (1.0 - texture2D(u_DepthMap, currentTexCoords).r);
        //currentDepthMapValue = texture2D(u_DepthMap, currentTexCoords).r;
        if ((currentDepthMapValue < whitExcess) && condition == 1.0)
        {
            currentDepthMapValue = excessValue;
        }
        //if ((currentDepthMapValue > blackExcess) && condition == 1.0)
        //{
        //    currentDepthMapValue = excessValue;
        //}
        currentLayerDepth += layerDepth;
    }

    vec2 prevTexCoords = currentTexCoords + deltaTexCoords;

    float afterDepth = currentDepthMapValue - currentLayerDepth;
    float beforeDepth = (1.0 - texture2D(u_DepthMap, prevTexCoords).r);
    //float beforeDepth = (texture2D(u_DepthMap, prevTexCoords).r);
    if ((beforeDepth < whitExcess) && condition == 1.0)
    {
        beforeDepth = excessValue;
    }
    //if ((beforeDepth > blackExcess) && condition == 1.0)
    //{
    //    beforeDepth = excessValue;
    //}
    beforeDepth = beforeDepth - currentLayerDepth + layerDepth;

    float weight = afterDepth / (afterDepth - beforeDepth);
    vec2 finalTexCoords = prevTexCoords * weight + currentTexCoords * (1.0 - weight);

    return finalTexCoords;
}

void main()
{
    //vec4 originaltexture = texture2D(u_Texture, v_TexCoordinate);

    vec3 eye = normalize(v_TangentViewPosition - v_TangentFragPos);

    vec2 texCoords = v_TexCoordinate;
    texCoords = ParallaxMapping(texCoords, eye);
    //texCoords = Displacementmapping();

    //if (texCoords.x > 1.0 || texCoords.y > 1.0 || texCoords.x < 0.0 || texCoords.y < 0.0)
    //{
        //gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
        //discard;
    //}

    //vec3 normal = texture2D(u_NormalMap, texCoords).rgb;
    //normal = normalize(normal * 2.0 - 1.0);

    vec4 color = texture2D(u_Texture, texCoords);

    gl_FragColor = color; //vec4(ambient + diffuse + specular, 1.0);
    //gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
}
