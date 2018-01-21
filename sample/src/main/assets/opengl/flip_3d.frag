#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 vTextureCoord;
uniform lowp sampler2D sTexture;
void main() {
  float x = vTextureCoord.x;
  float y = vTextureCoord.y;
 // Version: flip 3D (left image and right image)
  if (x <= 0.5) {
   x = 0.5 - x;
   } else {
   x = 1.5 - x;
   }
  gl_FragColor = texture2D(sTexture, vec2(x,y)).rgba;
}
