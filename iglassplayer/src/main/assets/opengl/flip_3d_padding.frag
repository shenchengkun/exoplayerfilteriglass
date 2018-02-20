#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform float upperPadding_percentage;
uniform float bottomPadding_percentage;
uniform float leftHalfImgLeftPadding_percentage;
uniform float leftHalfImgRightPadding_percentage;
varying vec2 vTextureCoord;
uniform lowp sampler2D sTexture;

// Version: flip 3D (left image and right image)
void main() {
  float x = vTextureCoord.x;
  float y = vTextureCoord.y;

  // begin: calculate x2_AsInLeftHalf, thisHalfImgLeftPadding_percentage, thisHalfImgRightPadding_percentage
  float thisHalfImgLeftPadding_percentage = leftHalfImgLeftPadding_percentage;
  float thisHalfImgRightPadding_percentage = leftHalfImgRightPadding_percentage;
 float x2_AsInLeftHalf = x;
 if (x > 0.5) {
 x2_AsInLeftHalf = x - 0.5;
 thisHalfImgLeftPadding_percentage = leftHalfImgRightPadding_percentage;
 thisHalfImgRightPadding_percentage = leftHalfImgLeftPadding_percentage;
 }
  // end: calculate x2_AsInLeftHalf, thisHalfImgLeftPadding_percentage, thisHalfImgRightPadding_percentage

 // set black in the padding area.
  if (y < bottomPadding_percentage ||
  y > 1.0 - upperPadding_percentage ||
  x2_AsInLeftHalf < thisHalfImgLeftPadding_percentage ||
  x2_AsInLeftHalf > 0.5 - thisHalfImgRightPadding_percentage) {
  gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
  return;
  }
    // end: calculate x2_AsInLeftHalf

  // begin: calculate oriX_AsInLeftHalf, oriY_AsInLeftHalf
  float oriX_AsInLeftHalf = 0.0;
  float oriY_AsInLeftHalf = 0.0;
  oriX_AsInLeftHalf = (x2_AsInLeftHalf - thisHalfImgLeftPadding_percentage) /
  (0.5 - thisHalfImgRightPadding_percentage - thisHalfImgLeftPadding_percentage) * 0.5;
  oriY_AsInLeftHalf = (y - bottomPadding_percentage) /
  (1.0 - upperPadding_percentage - bottomPadding_percentage);
  // end: calculate oriX_AsInLeftHalf, oriY_AsInLeftHalf

  // begin: calculate oriX, oriY
  float oriX, oriY;
  if (x <= 0.5) {
  oriX = oriX_AsInLeftHalf;
  oriY = oriY_AsInLeftHalf;
  } else {
  oriX = oriX_AsInLeftHalf + 0.5;
  oriY = oriY_AsInLeftHalf;
  }
  // end: calculate oriX, oriY

  // begin: flip
  if (x <= 0.5) {
  oriX = 0.5 - oriX;
  } else {
  oriX = 1.5 - oriX;
  }
  // end: flip

  gl_FragColor = texture2D(sTexture, vec2(oriX, oriY)).rgba;
}
