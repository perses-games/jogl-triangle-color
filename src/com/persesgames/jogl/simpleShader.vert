#if __VERSION__ >= 130
  #define attribute in
  #define varying out
#endif

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

attribute vec4  attribute_Position;
attribute vec4  attribute_Color;

varying vec4 varying_Color;

void main(void) {
    mat4    uniform_Projection = mat4(1);

    gl_Position = uniform_Projection * attribute_Position;
    varying_Color = attribute_Color;
}
