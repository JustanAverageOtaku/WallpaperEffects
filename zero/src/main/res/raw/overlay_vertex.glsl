// Neatness
uniform mat4 u_MVPMatrix;

attribute vec4 a_Position;
attribute vec2 a_TexturePosition;

varying vec2 v_TextureCoordinates;

void main()
{
    gl_Position = u_MVPMatrix * a_Position;
    v_TextureCoordinates = a_TexturePosition;
}
