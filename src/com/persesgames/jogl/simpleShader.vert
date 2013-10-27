#if __VERSION__ >= 130
  #define attribute in
  #define varying out
#endif

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

attribute vec4  attribute_Position;

void main(void) {
    mat4    uniform_Projection = mat4(1);

    gl_Position = uniform_Projection * attribute_Position;
}
