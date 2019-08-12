#ifdef GL_ES
         precision mediump float;
     #endif


     varying vec4 v_color;
     varying vec2 v_texCoords;
     uniform sampler2D u_texture;

    uniform float coef=1f;
    uniform float margin=1f;
    uniform float height=1f;

    uniform boolean doUp;
    uniform boolean doBot;
    uniform boolean doLeft;
    uniform boolean doRight;

     void main() {
       vec4 c = v_color * texture2D(u_texture, v_texCoords);

         float dist = ( v_texCoords.x - margin)* (v_texCoords.x - margin);

        gl_FragColor = vec4(1-c.r, 1-c.g, 1-c.b, c.a);
         float lightness = (gl_FragColor.r+gl_FragColor.g+gl_FragColor.b)/3;
         gl_FragColor.a= 1-lightness;
     }