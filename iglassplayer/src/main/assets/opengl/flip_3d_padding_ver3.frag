#version 300 es
precision mediump float;

uniform float upperPadding_percentage;
uniform float bottomPadding_percentage;
uniform float leftHalfImgLeftPadding_percentage;
uniform float leftHalfImgRightPadding_percentage;
in highp vec2 vTextureCoord;
uniform lowp sampler2D sTexture;
//uniform highp sampler2D lutTexture;
out vec4 fragmentColor;

// https://stackoverflow.com/questions/24903517/mixing-opengl-es-2-0-and-3-0
// http://www.shaderific.com/blog/2014/3/13/tutorial-how-to-update-a-shader-for-opengl-es-30
// https://stackoverflow.com/questions/24903517/mixing-opengl-es-2-0-and-3-0
// https://stackoverflow.com/questions/18672919/how-do-i-declare-the-opengl-version-in-shaders-on-android
// https://www.khronos.org/registry/OpenGL/specs/es/3.0/GLSL_ES_Specification_3.00.pdf
// for precision, refer to "Precision Qualifiers", p33 at: https://www.khronos.org/files/opengles_shading_language.pdf
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

  // begin: distortion
//  vec2 distortedXY = texture(lutTexture, vec2(oriX, oriY)).rg;

// for debug
vec2 distortedXY = vec2(0.1, 0.1);
  // end: distortion

//  gl_FragColor = texture2D(sTexture, vec2(oriX, oriY) + 0.00001*distortedXY).rgba;
//  gl_FragColor = texture2D(sTexture, distortedXY).rgba;
//gl_FragColor = vec4(0.0, 1.0, 1.0, 1.0);

//// Option:  black screen
//float gVal = texture2D(sTexture, vec2(oriX, oriY)).g;
//gl_FragColor = vec4(distortedXY.x/2.0, 0.00001 * gVal, 0.0, 1.0);


//// Option:  black screen
//float gVal = texture2D(sTexture, vec2(oriX, oriY)).g;
//gl_FragColor = vec4(distortedXY.x + 0.5, 0.00001 * gVal, 0.0, 1.0);

// Option:
fragmentColor = texture(sTexture, vec2(oriX, oriY) + distortedXY).rgba;

//// Option without distortion
//   gl_FragColor = texture2D(sTexture, vec2(oriX, oriY)).rgba;


}


//#version 300 es
//precision mediump float;
//
//uniform float upperPadding_percentage;
//uniform float bottomPadding_percentage;
//uniform float leftHalfImgLeftPadding_percentage;
//uniform float leftHalfImgRightPadding_percentage;
//in highp vec2 vTextureCoord;
//uniform lowp sampler2D sTexture;
//uniform highp sampler2D lutTexture;
//out vec4 fragmentColor;
//
//// https://stackoverflow.com/questions/24903517/mixing-opengl-es-2-0-and-3-0
//// http://www.shaderific.com/blog/2014/3/13/tutorial-how-to-update-a-shader-for-opengl-es-30
//// https://stackoverflow.com/questions/24903517/mixing-opengl-es-2-0-and-3-0
//// https://stackoverflow.com/questions/18672919/how-do-i-declare-the-opengl-version-in-shaders-on-android
//// https://www.khronos.org/registry/OpenGL/specs/es/3.0/GLSL_ES_Specification_3.00.pdf
//// for precision, refer to "Precision Qualifiers", p33 at: https://www.khronos.org/files/opengles_shading_language.pdf
//// Version: flip 3D (left image and right image)
//void main() {
//  float x = vTextureCoord.x;
//  float y = vTextureCoord.y;
//
//  // begin: calculate x2_AsInLeftHalf, thisHalfImgLeftPadding_percentage, thisHalfImgRightPadding_percentage
//  float thisHalfImgLeftPadding_percentage = leftHalfImgLeftPadding_percentage;
//  float thisHalfImgRightPadding_percentage = leftHalfImgRightPadding_percentage;
// float x2_AsInLeftHalf = x;
// if (x > 0.5) {
// x2_AsInLeftHalf = x - 0.5;
// thisHalfImgLeftPadding_percentage = leftHalfImgRightPadding_percentage;
// thisHalfImgRightPadding_percentage = leftHalfImgLeftPadding_percentage;
// }
//  // end: calculate x2_AsInLeftHalf, thisHalfImgLeftPadding_percentage, thisHalfImgRightPadding_percentage
//
// // set black in the padding area.
//  if (y < bottomPadding_percentage ||
//  y > 1.0 - upperPadding_percentage ||
//  x2_AsInLeftHalf < thisHalfImgLeftPadding_percentage ||
//  x2_AsInLeftHalf > 0.5 - thisHalfImgRightPadding_percentage) {
//  gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
//  return;
//  }
//    // end: calculate x2_AsInLeftHalf
//
//  // begin: calculate oriX_AsInLeftHalf, oriY_AsInLeftHalf
//  float oriX_AsInLeftHalf = 0.0;
//  float oriY_AsInLeftHalf = 0.0;
//  oriX_AsInLeftHalf = (x2_AsInLeftHalf - thisHalfImgLeftPadding_percentage) /
//  (0.5 - thisHalfImgRightPadding_percentage - thisHalfImgLeftPadding_percentage) * 0.5;
//  oriY_AsInLeftHalf = (y - bottomPadding_percentage) /
//  (1.0 - upperPadding_percentage - bottomPadding_percentage);
//  // end: calculate oriX_AsInLeftHalf, oriY_AsInLeftHalf
//
//  // begin: calculate oriX, oriY
//  float oriX, oriY;
//  if (x <= 0.5) {
//  oriX = oriX_AsInLeftHalf;
//  oriY = oriY_AsInLeftHalf;
//  } else {
//  oriX = oriX_AsInLeftHalf + 0.5;
//  oriY = oriY_AsInLeftHalf;
//  }
//  // end: calculate oriX, oriY
//
//  // begin: flip
//  if (x <= 0.5) {
//  oriX = 0.5 - oriX;
//  } else {
//  oriX = 1.5 - oriX;
//  }
//  // end: flip
//
//  // begin: distortion
//  vec2 distortedXY = texture(lutTexture, vec2(oriX, oriY)).rg;
//  // end: distortion
//
////  gl_FragColor = texture2D(sTexture, vec2(oriX, oriY) + 0.00001*distortedXY).rgba;
////  gl_FragColor = texture2D(sTexture, distortedXY).rgba;
////gl_FragColor = vec4(0.0, 1.0, 1.0, 1.0);
//
////// Option:  black screen
////float gVal = texture2D(sTexture, vec2(oriX, oriY)).g;
////gl_FragColor = vec4(distortedXY.x/2.0, 0.00001 * gVal, 0.0, 1.0);
//
//
////// Option:  black screen
////float gVal = texture2D(sTexture, vec2(oriX, oriY)).g;
////gl_FragColor = vec4(distortedXY.x + 0.5, 0.00001 * gVal, 0.0, 1.0);
//
//// Option:
//fragmentColor = texture(sTexture, vec2(oriX, oriY) + distortedXY).rgba;
//
////// Option without distortion
////   gl_FragColor = texture2D(sTexture, vec2(oriX, oriY)).rgba;
//
//
//}