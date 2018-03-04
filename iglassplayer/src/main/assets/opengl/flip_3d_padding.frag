#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform float upperPadding_percentage;
uniform float bottomPadding_percentage;
uniform float leftHalfImgLeftPadding_percentage;
uniform float leftHalfImgRightPadding_percentage;
varying vec2 vTextureCoord;
uniform lowp sampler2D sTexture;
uniform mediump sampler2D NO1_byte_fromRight_Texture;
uniform mediump sampler2D NO2_byte_fromRight_Texture;

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

  // coordinate lookup table
  vec2 byte1 = texture2D(NO1_byte_fromRight_Texture, vec2(oriX, oriY)).rg;
  vec2 byte2 = texture2D(NO2_byte_fromRight_Texture, vec2(oriX, oriY)).rg;

  // internal format GL_RGB, converts to float [0, 1].
  // int cast: floor it
  int temp1 = int(byte1.x * 255.0);
  int temp2 = int(byte2.x * 255.0);
  // ERROR: 0:74: '<<' :  supported in pack/unpack shaders only
  // int temp2ShiftLeft = temp2<<8;
  int temp2ShiftLeft = temp2 * 256;
  int temp3 = temp1 + temp2ShiftLeft;
  float distortedX = float(temp3) / 65535.0;

  temp1 = int(byte1.y * 255.0);
  temp2 = int(byte2.y * 255.0);
  temp2ShiftLeft = temp2 * 256;
  temp3 = temp1 + temp2ShiftLeft;
  float distortedY = float(temp3) / 65535.0;

  gl_FragColor = texture2D(sTexture, vec2(distortedX, distortedY)).rgba;
}
