package tauri.dev.jsg.renderer.stargate;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.loader.texture.TextureLoader;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static tauri.dev.jsg.renderer.stargate.StargateAbstractRenderer.isEhAnimatedLoaded;

public class StargateRendererStatic {
	static final float eventHorizonRadius = 3.790975f;

	private static final int quads = 16;
	private static final int sections = 36 * 2;
	private static final float sectionAngle = (float) (2*Math.PI/sections);

	private static final float innerCircleRadius = 0.25f;
	private static final float quadStep = (eventHorizonRadius - innerCircleRadius) / quads;

	private static List<Float> offsetList = new ArrayList<Float>();
	// private long horizonStateChange = 0;
	private static List<Float> sin = new ArrayList<Float>();
	private static List<Float> cos = new ArrayList<Float>();

	private static List<Float> quadRadius = new ArrayList<Float>();

	static InnerCircle innerCircle;
	static List<QuadStrip> quadStrips = new ArrayList<QuadStrip>();

	private static Random rand = new Random();

	private static float getRandomFloat() {
		return rand.nextFloat()*2-1;
	}

	private static float getOffset(int index, float tick, float mul, int quadStripIndex) {
		return (float) (Math.sin(tick/4f + offsetList.get(index)) * mul * (quadStripIndex/4f) * (quadStripIndex - quadStrips.size()) / 400f);
	}

	private static float toUV(float coord) {
		return (coord + 1) / 2f;
	}

	static {
		initEventHorizon();
		initKawoosh();
	}
	
	private static void initEventHorizon() {
		for (int i=0; i<sections*(quads+1); i++) {
			offsetList.add( getRandomFloat() * 3 );
		}
		
		for (int i=0; i<=sections; i++) {
			sin.add( MathHelper.sin(sectionAngle * i) );
			cos.add( MathHelper.cos(sectionAngle * i) );
		}
		
		innerCircle = new InnerCircle();
		
		for (int i=0; i<=quads; i++) {
			quadRadius.add( innerCircleRadius + quadStep*i );
		}
		
		for (int i=0; i<quads; i++) {
			quadStrips.add( new QuadStrip(i) );
		}

		// horizonStateChange = world.getTotalWorldTime();
	}
	
	public static class InnerCircle {
		private List<Float> x = new ArrayList<Float>();
		private List<Float> y = new ArrayList<Float>();
		
		private List<Float> tx = new ArrayList<Float>();
		private List<Float> ty = new ArrayList<Float>();
		
		public InnerCircle() {
			float texMul = (innerCircleRadius / eventHorizonRadius);
			
			for (int i=0; i<sections; i++) {			
				x.add( sin.get(i) * innerCircleRadius );
				y.add( cos.get(i) * innerCircleRadius );
				
				tx.add( toUV( sin.get(i) * texMul ) );
				ty.add( toUV( cos.get(i) * texMul ) );
			}
		}
		public void render(float tick, boolean white, Float alpha, float mul) {
			render(tick, white, alpha, mul, (byte) 0);
		}
		public void render(float tick, boolean white, Float alpha, float mul, byte animationOverride) {
			boolean animated = !JSGConfig.horizonConfig.disableAnimatedEventHorizon && isEhAnimatedLoaded();
			if (animationOverride == -1) animated = false;
			if (animationOverride == 1) animated = true;

			if (white) {
				GlStateManager.disableTexture2D();
				if (alpha > 0.5f)
					alpha = 1.0f - alpha;
			}
			
			glBegin(GL_TRIANGLE_FAN);
			
			int texIndex = (int) (tick*4 % 185);
			float xTexOffset = texIndex % 14 / 14f;
			float yTexOffset = texIndex / 14 / 14f;
			
			float xTex = 0.5f;
			float yTex = 0.5f;
			
			if (animated) {
				xTex /= 14.0f; xTex += xTexOffset;
				yTex /= 14.0f; yTex += yTexOffset;
			} else yTex *= -1;
			
			if (alpha != null) glColor4f(1.0f, 1.0f, 1.0f, alpha.floatValue());
			if (!white) glTexCoord2f(xTex, yTex);
			
			glVertex3f(0, 0, 0);
			
			int index = 0;
			for (int i=sections; i>=0; i--) {
				if (i == sections)
					index = 0;
				else
					index = i;
				
				xTex = tx.get(index);
				yTex = ty.get(index);
				
				if (animated) {
					xTex /= 14.0f; xTex += xTexOffset;
					yTex /= 14.0f; yTex += yTexOffset;
				} else yTex *= -1;
				
				if (!white) glTexCoord2f(xTex, yTex);
				glVertex3f( x.get(index), y.get(index), getOffset(index, tick*mul, mul, 0) );
			}

			glEnd();
			
			if (alpha != null) glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			if (white) GlStateManager.enableTexture2D();
		}
	}
	
	public static class QuadStrip {
		private List<Float> x = new ArrayList<Float>();
		private List<Float> y = new ArrayList<Float>();
		
		private List<Float> tx = new ArrayList<Float>();
		private List<Float> ty = new ArrayList<Float>();
		
		private int quadStripIndex;
		
		public QuadStrip(int quadStripIndex) {
			// this(quadStripIndex, quadRadius.get(quadStripIndex), quadRadius.get(quadStripIndex+1), false, 0);
			this( quadStripIndex, quadRadius.get(quadStripIndex), quadRadius.get(quadStripIndex+1), null );
		}
		
		public QuadStrip(int quadStripIndex, float innerRadius, float outerRadius, Float tick) {
			this.quadStripIndex = quadStripIndex; 
			recalculate(innerRadius, outerRadius, tick);
		}
		
		public void recalculate(float innerRadius, float outerRadius, Float tick) {
			//this.quadStripIndex = quadStripIndex; 
			
			List<Float> radius = new ArrayList<Float>();
			List<Float> texMul = new ArrayList<Float>();
			
			/*radius.add( quadRadius.get( quadStripIndex   ) ); // Inner
			  radius.add( quadRadius.get( quadStripIndex+1 ) ); // Outer */
			
			radius.add( innerRadius );
			radius.add( outerRadius );
			
			for (int i=0; i<2; i++)
				texMul.add( radius.get(i) / eventHorizonRadius );
			
			for (int k=0; k<2; k++) {
				for (int i=0; i<sections; i++) {
					float rad = radius.get(k);
					
					if (tick != null) {
						rad += getOffset(i, tick, 1, quadStripIndex) * 2;
					}
					
					x.add( rad * sin.get(i) );
					y.add( rad * cos.get(i) );
					
					tx.add( toUV( sin.get(i) * texMul.get(k) ) );
					ty.add( toUV( cos.get(i) * texMul.get(k) ) );
				}
			}
		}

		public void render(float tick, boolean white, Float alpha, float mul) {
			render(tick, null, null, white, alpha, mul);
		}

		public void render(float tick, boolean white, Float alpha, float mul, byte animationOverride) {
			render(tick, null, null, white, alpha, mul, false, false, animationOverride);
		}
		
		public void render(float tick, Float outerZ, Float innerZ, boolean white, Float alpha, float mul) {
			render(tick, outerZ, innerZ, white, alpha, mul, false, false, (byte) 0);
		}
		
		public void renderBoth(float tick, Float outerZ, Float innerZ, boolean white, Float alpha, float mul, boolean red) {
			render(tick, outerZ, innerZ, white, alpha, mul, true, red, (byte) 0);
			render(tick, outerZ, innerZ, white, alpha, mul, false, red, (byte) 0);
		}
		
		public void render(float tick, Float outerZ, Float innerZ, boolean white, Float alpha, float mul, boolean reversed, boolean red, byte animationOverride) {
			boolean animate = !tauri.dev.jsg.config.JSGConfig.horizonConfig.disableAnimatedEventHorizon && isEhAnimatedLoaded();
			if (animationOverride == -1) animate = false;
			if (animationOverride == 1) animate = true;

			if (white) {
				GlStateManager.disableTexture2D();
				if (alpha > 0.5f)
					alpha = 1.0f - alpha;
			}
//			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
			if (alpha != null) glColor4d(1, red?0:1, red?0:1, alpha.floatValue());
			
			glBegin(GL_QUAD_STRIP);
			
			int index = 0;
			
			int texIndex = (int) (tick*4 % 185);
			float xTexOffset = texIndex % 14 / 14f;
			float yTexOffset = texIndex / 14 / 14f;
						
			for (int i = reversed ? 0 : sections; (reversed && i<=sections) || (!reversed && i>=0); i+=(reversed ? 1 : -1)) {
				if (i == sections)
					index = 0;
				else
					index = i;
				
				float z;
				
				if (outerZ != null) z = outerZ.floatValue();
				else z = getOffset(index + sections*quadStripIndex, tick*mul, mul, quadStripIndex);
				
				float xTex = tx.get(index);
				float yTex = ty.get(index);
				
				if (animate) {
					xTex /= 14.0f; xTex += xTexOffset;
					yTex /= 14.0f; yTex += yTexOffset;
				} else yTex *= -1;
				
				if (!white) glTexCoord2f( xTex, yTex );
				glVertex3f( x.get(index), y.get(index),  z );
				
//				JSG.info("z: " + z);
				
				index = index + sections;
				
				xTex = tx.get(index);
				yTex = ty.get(index);
				
				if (animate) {
					xTex /= 14.0f; xTex += xTexOffset;
					yTex /= 14.0f; yTex += yTexOffset;
				} else yTex *= -1;
				
				if (innerZ != null) z = innerZ.floatValue();
				else z = getOffset(index + sections*quadStripIndex, tick*mul, mul, quadStripIndex+1);
				
				if (!white) glTexCoord2f( xTex, yTex );
				glVertex3f( x.get(index), y.get(index), z );
			}
			
			glEnd();
			
			if (white) GlStateManager.enableTexture2D();
			if (alpha != null) glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		}
	}
	
	static final float kawooshRadius = 2.5f;
	private static final float kawooshSize = 7f;
	private static final float kawooshSections = 128; 
	
	static Map<Float, Float> Z_RadiusMap;
	
	// Generate kawoosh shape using 4 functions
	private static void initKawoosh() {
		Z_RadiusMap = new LinkedHashMap<Float, Float>();
		
		float begin = 0;
		float end   = 0.545f;
		
		float rng = end - begin;

		float step = rng / kawooshSections;
		boolean first = true;
		
		float scaleX = kawooshSize / rng;
		float scaleY = 1;
		
		for (int i=0; i<=kawooshSections; i++) {
			float x = begin + step*i;
			float y = 0;
			
			float border1 = 0.2575f;
			float border2 = 0.4241f;
			float border3 = 0.4577f;
			
			
			if ( x >= 0 && x <= border1 ) {
				float a = 2f;
				float b = -4.7f;
				float c = 2.1f;
				
				float p = x + (b/20f);
				y = (a/2f) * (p*p) + (c/30f);
				
				if (first) {
				first = false;
				scaleY = kawooshRadius / y;
				// JSG.info("radius: " + kawooshRadius + "  y: " + y + "  scale: "+kawooshScaleFactor);
				}
			}
			
			else if ( x > border1 && x <= border2 ) {
				float a = 1.4f;
				float b = -4.3f;
				float c = 1.4f;
				
				float p = x + (b/20f);
				y = (a/5f) * (p*p) + (c/20f);
			}
			
			else if ( x > border2 && x <= border3 ) {
				float a = -7.4f;
				float b = -8.6f;
				float c = 3.3f;
				
				float p = x + (b/20f);
				y = a * (p*p) + (c/40f);
			}
			
			else if ( x > border3 && x <= 0.545f ) {
				float a = 5.2f;
				
				y = (float) (a/20f * Math.sqrt( 0.545f - x ));
			}
			
			Z_RadiusMap.put(x*scaleX, y*scaleY);
		}
	}
	
}
