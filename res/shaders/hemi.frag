uniform sampler2D tex;
varying vec4 FinalColor;
varying vec4 diffuse;

void main()
{
 	vec4 color = texture2D(tex,gl_TexCoord[0].st);
	gl_FragColor = color * FinalColor;
}
