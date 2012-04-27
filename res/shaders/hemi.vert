varying vec4 FinalColor;

void main()
{
  vec3 LightPosition = vec3(0, 100, -15);
	vec3 ecPosition = vec3(gl_ModelViewMatrix * gl_Vertex);
	vec3 tnorm = normalize(gl_NormalMatrix * gl_Normal);
	vec3 lightVec = normalize(LightPosition - ecPosition);

	float costheta = dot(tnorm, lightVec);
	float a = costheta * 0.5 + 0.5;
	
  gl_TexCoord[0]  = gl_MultiTexCoord0;
	FinalColor = mix(vec4(0,0,0,1), vec4(1,1,1,1), a) * gl_Color;

	gl_Position = ftransform();
}
