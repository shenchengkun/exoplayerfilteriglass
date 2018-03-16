#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform float upperPadding_percentage;
uniform float bottomPadding_percentage;
uniform float leftHalfImgLeftPadding_percentage;
uniform float leftHalfImgRightPadding_percentage;
uniform float flip;
uniform float distortion;
uniform float dup;

varying vec2 vTextureCoord;
uniform lowp sampler2D sTexture;

vec2 toPolar(vec2 point) {
    float r = length(point);
    float theta = atan(point.y, point.x);
    return vec2(r, theta);
}

//flip->distortion->duplicate->padding, but in code the order is reverse
void main() {
   float x=vTextureCoord.x,y=vTextureCoord.y;

   //padding
   if (y < bottomPadding_percentage ||y > 1.0 - upperPadding_percentage ||x < leftHalfImgLeftPadding_percentage ||x > 1.0 - leftHalfImgRightPadding_percentage) {
         gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
         return;
     }
     x = (x - leftHalfImgLeftPadding_percentage) /
     (1.0 - leftHalfImgRightPadding_percentage - leftHalfImgLeftPadding_percentage);
     y = (y - bottomPadding_percentage) /
     (1.0 - upperPadding_percentage - bottomPadding_percentage);

   //duplicate
   if(dup>0.5){
       if (x < 0.5) {
         x = 2.0 * x;
        } else {
         x = 2.0 * x  - 1.0;
        }
    }

  //distortion
  if(distortion>0.5){
    vec2 polarBase=toPolar(vec2(-0.25,0.75));
    vec2 polarCur=toPolar(vec2(x-0.5,y+0.75));
    float baseR=polarBase.x,baseTheta=polarBase.y,curR=polarCur.x,curTheta=polarCur.y;


    highp vec2 centre=vec2(0.3,0.4);
    highp float alpha = 0.2;
    highp vec2 p1 = vec2(2.0 * vec2(x,y) - 1.0) - centre;
    highp vec2 p2 = p1 / (1.0 - alpha * length(p1));
    p2 = (p2 + centre + 1.0) * 0.5;
    //vec2 p3=vec2((baseTheta-curTheta)/(2*curTheta-3.1415926),curR/baseR-1);
    if (all(greaterThanEqual(p2, vec2(0.0)))&&all(lessThanEqual(p2, vec2(1.0)))){
        x = p2.x;
        y = p2.y;
    } else{
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }
  }

 //flip
   if(flip>0.5) x = 1.0 - x;

 //end
  gl_FragColor = texture2D(sTexture, vec2(x,y));
}



