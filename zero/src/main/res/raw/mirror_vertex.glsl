// Left for neatness
uniform mat4 u_MVPMatrix;
uniform vec2 u_Flip; // Values must be either 0.0 or 1.0

attribute vec4 a_Position;
attribute vec2 a_TexturePosition;

varying vec2 v_TextureCoordinates;

void main()
{
    gl_Position = u_MVPMatrix * a_Position;
    v_TextureCoordinates = vec2(abs(u_Flip.x - a_TexturePosition.x), abs(u_Flip.y - a_TexturePosition.y));
}
