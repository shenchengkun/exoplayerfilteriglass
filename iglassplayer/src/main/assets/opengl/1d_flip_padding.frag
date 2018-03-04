#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform float upperPadding_percentage;
uniform float bottomPadding_percentage;
uniform float leftHalfImgLeftPadding_percentage;
uniform float leftHalfImgRightPadding_percentage;
varying vec2 vTextureCoord;
uniform lowp sampler2D sTexture;

// Version: 1D. Ignore middle gap. flip
// I modify the code based on the 3D version. So some variable names may not make sense in this case.
// But this way may make the future modification of all shaders easier.
void main() {
  float x = vTextureCoord.x;
  float y = vTextureCoord.y;

 // set black in the padding area.
 // 1D version: modify 0.5 or 2.0 accordingly
  if (y < bottomPadding_percentage ||
  y > 1.0 - upperPadding_percentage ||
  x < leftHalfImgLeftPadding_percentage ||
  x > 1.0 - leftHalfImgRightPadding_percentage) {
  gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
  return;
  }
    // end: calculate x2_AsInLeftHalf

  // begin: calculate oriX_AsInLeftHalf, oriY_AsInLeftHalf
  // 1D version: modify 0.5 or 2.0 accordingly
  float oriX = 0.0;
  float oriY = 0.0;
  oriX = (x - leftHalfImgLeftPadding_percentage) /
  (1.0 - leftHalfImgRightPadding_percentage - leftHalfImgLeftPadding_percentage);
  oriY = (y - bottomPadding_percentage) /
  (1.0 - upperPadding_percentage - bottomPadding_percentage);
  // end: calculate oriX_AsInLeftHalf, oriY_AsInLeftHalf


  // begin: flip
  oriX = 1.0 - oriX;
  // end: flip

  gl_FragColor = texture2D(sTexture, vec2(oriX, oriY)).rgba;
}

