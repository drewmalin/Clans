uniform vec4 ColorTest;
uniform int numLights;

varying vec4 FinalColor;

void main()
{
  for(int i = 0; i < numLights; i++) {
    
  }
  
	vec3 LightPosition = vec3(gl_LightSource[0].position);
	vec3 ecPosition = vec3(gl_ModelViewMatrix * gl_Vertex);
	vec3 tnorm = normalize(gl_NormalMatrix * gl_Normal);
	vec3 lightVec = normalize(LightPosition - ecPosition);

	float costheta = dot(tnorm, lightVec);
	float a = costheta * 0.5 + 0.5;
	
  gl_TexCoord[0]  = gl_MultiTexCoord0;
	FinalColor = mix(vec4(0,0,0,1), vec4(1,1,1,1), a) * gl_Color;
  	gl_TexCoord[0]  = gl_MultiTexCoord0;
	FinalColor = mix(vec4(0,0,0,1), gl_LightSource[0].specular, a) * gl_Color;

	gl_Position = ftransform();
}