precision mediump float;

uniform sampler2D uTexture;

varying vec2 vTexPosition;

void main()
{
    //gl_FragColor = vec4(texture2D(uTexture, vTexPosition).rgb, 0.0);
    gl_FragColor = texture2D(uTexture, vTexPosition);
    //gl_FragColor = vec4(0.5, 0.5, 0.5, 0.0);
}
