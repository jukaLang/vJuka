uniform sampler2D m_ColorMap;

varying vec2 texCoord;

void main() {
    vec4 color = texture2D(m_ColorMap, texCoord);
    gl_FragColor = vec4(color.r, color.g * 0.5, color.b * 0.5, 1.0); // Simple anaglyph effect
}