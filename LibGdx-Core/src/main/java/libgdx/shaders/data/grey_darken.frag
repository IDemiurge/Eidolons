#ifdef GL_ES
         precision mediump float;
     #endif
     
     varying vec4 v_color;
     varying vec2 v_texCoords;
     uniform sampler2D u_texture;
    uniform float coef=1f;
    uniform float base=0.8f;
    uniform float base_grey=0.8f;

     float lerp( float prev, float curr, float coef ) {
     		return prev + coef * (curr - prev);
     	}
     void main() {
       vec4 c = v_color * texture2D(u_texture, v_texCoords);
           float grey = (c.r + c.g + c.b) / 3.0;

       gl_FragColor = vec4(
       lerp(c.r, grey, coef*base_grey)*coef*base,
       lerp(c.g, grey, coef*base_grey)*coef*base,
       lerp(c.b, grey, coef*base_grey)*coef*base , c.a);
     }