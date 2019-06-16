#ifdef GL_ES
         precision mediump float;
     #endif
     
     varying vec4 v_color;
     varying vec2 v_texCoords;
     uniform sampler2D u_texture;
    uniform float coef=1f;
    uniform float base=0.8f;
     
     void main() {
       vec4 c = v_color * texture2D(u_texture, v_texCoords);
       gl_FragColor = vec4(c.r*0.8f, c.g*0.8f, c.b*0.8f, c.a);
     }