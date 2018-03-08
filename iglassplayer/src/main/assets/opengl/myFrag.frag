#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform float upperPadding_percentage;
uniform float bottomPadding_percentage;
uniform float leftHalfImgLeftPadding_percentage;
uniform float leftHalfImgRightPadding_percentage;
uniform float flip;
uniform float distortion;
uniform float dup;
uniform highp vec2 center;
uniform highp float radius;
uniform highp float scale;

varying vec2 vTextureCoord;
uniform lowp sampler2D sTexture;

void main() {

  highp vec2 textureCoordinateToUse = vTextureCoord;

  //distortion
  if(distortion>0.5){
      highp float dist = distance(center, vTextureCoord);
      textureCoordinateToUse -= center;
      if (dist < radius) {
      highp float percent = 1.0 - ((radius - dist) / radius) * scale;
      percent = percent * percent;
      textureCoordinateToUse = textureCoordinateToUse * percent;
      }
      textureCoordinateToUse += center;
  }

//padding
  float x = textureCoordinateToUse.x;
  float y = textureCoordinateToUse.y;
  if (y < bottomPadding_percentage ||y > 1.0 - upperPadding_percentage ||x < leftHalfImgLeftPadding_percentage ||x > 1.0 - leftHalfImgRightPadding_percentage) {
      gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
      return;
  }
  float oriX = 0.0;
  float oriY = 0.0;
  oriX = (x - leftHalfImgLeftPadding_percentage) /
  (1.0 - leftHalfImgRightPadding_percentage - leftHalfImgLeftPadding_percentage);
  oriY = (y - bottomPadding_percentage) /
  (1.0 - upperPadding_percentage - bottomPadding_percentage);

  if(dup>0.5){
      if (oriX < 0.5) {
        oriX = 1.0 - 2.0 * oriX;
       } else {
        oriX = oriX - 0.5;
        oriX = 1.0 - 2.0 * oriX;
       }
   }

//flip
  if(flip>0.5) oriX = 1.0 - oriX;

//end
  gl_FragColor = texture2D(sTexture, vec2(oriX, oriY)).rgba;
}

