package com.gousade.captcha.carbon;

import cn.hutool.core.util.IdUtil;
import com.gousade.captcha.carbon.util.IndexFrom;
import com.gousade.util.FileUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.skija.*;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * Render a molecule object.
 * <p>
 * SKIA is used as the backend to render the molecule.
 */
public class MoleculeRender {

	private MoleculeRender() {
		throw new AssertionError("No instances");
	}

	private static Typeface sRobotoNormal = null;

	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class MoleculeRenderConfig {
		public int width;
		public int height;
		public float fontSize;
		public float scaleFactor;

		public int gridCountX = 5;
		public int gridCountY = 5;
		public boolean drawGrid = true;
		public float[] labelTop;

		@Nullable
		public List<Integer> shownChiralCarbons;

		public float[] labelLeft;
		public float[] labelRight;
		public float[] labelBottom;

		public float transformX(@NotNull Molecule molecule, float x) {
			float dx = 0 + fontSize;
			float mx = molecule.minX();
			return dx + scaleFactor * (x - mx);
		}

		public float transformY(@NotNull Molecule molecule, float y) {
			float dy = height - fontSize;
			float my = molecule.minY();
			return dy - scaleFactor * (y - my);
		}
	}

	public static MoleculeRenderConfig calculateRenderRect(@NotNull Molecule molecule, int maxSize) {
		float rx = molecule.rangeX();
		float ry = molecule.rangeY();
		if (rx == 0) {
			throw new IllegalArgumentException("Molecule has no X range");
		}
		if (ry == 0) {
			throw new IllegalArgumentException("Molecule has no Y range");
		}

		float scaleFactor = Math.min(maxSize / rx, maxSize / ry);

		float fontSize = molecule.getAverageBondLength() / 1.8f * scaleFactor;

		fontSize = Math.min(fontSize, maxSize / 16.0f);

		int width = (int) (rx * scaleFactor);
		int height = (int) (ry * scaleFactor);

		MoleculeRenderConfig config = new MoleculeRenderConfig();
		config.width = width + 2 * (int) fontSize;
		config.height = height + 2 * (int) fontSize;
		config.fontSize = fontSize;
		config.scaleFactor = scaleFactor;
		return config;
	}

	public static Image renderMoleculeAsImage(Molecule molecule, MoleculeRenderConfig cfg) {
		Objects.requireNonNull(cfg);
		Objects.requireNonNull(molecule);
		int weight = cfg.width;
		int height = cfg.height;
		if (weight * height == 0) {
			throw new IllegalArgumentException("width = " + weight + ", height = " + height);
		}
		if (cfg.fontSize <= 0 || cfg.scaleFactor <= 0) {
			throw new IllegalArgumentException("fontSize = " + cfg.fontSize + ", scaleFactor = " + cfg.scaleFactor);
		}
		FontMgr fontMgr = FontMgr.getDefault();
		if (sRobotoNormal == null) {
			ClassPathResource classPathResource = new ClassPathResource("static" + File.separator + "ttf"
					+ File.separator + "Roboto-Regular.ttf");
			try (InputStream input = classPathResource.getInputStream()) {
				Data data = Data.makeFromBytes(FileUtils.readFile(input));
				Typeface typeface = fontMgr.makeFromData(data);
				if (typeface == null) {
					throw new RuntimeException("Failed to create typeface");
				}
				sRobotoNormal = typeface;
			} catch (Exception e) {
				throw new RuntimeException("Failed to read assets Roboto-Regular.ttf", e);
			}

		}
		Typeface typeface = Objects.requireNonNull(sRobotoNormal, "sRobotoNormal is null");
		Font font = new Font(typeface, cfg.fontSize);
		Surface surface = Surface.makeRasterN32Premul(weight, height);
		Canvas canvas = surface.getCanvas();
		canvas.clear(0xffffffff);
		Paint paint = new Paint();
		if (cfg.drawGrid && cfg.gridCountX > 0 && cfg.gridCountY > 0) {
			doDrawGridTagForBackground(canvas, font, paint, cfg);
		}
		doDrawMolecule(canvas, paint, molecule, font, cfg);
		Image image = surface.makeImageSnapshot();
		surface.close();
		paint.close();
		return image;
	}

	public static void doDrawGridTagForBackground(Canvas canvas, Font font, Paint paint, MoleculeRenderConfig cfg) {
		float unitSizeX = (float) cfg.width / cfg.gridCountX;
		float unitSizeY = (float) cfg.height / cfg.gridCountY;
		float fontSize = Math.min(Math.min(unitSizeX, unitSizeY) / 2.0f, cfg.fontSize);
		final int gridColor1 = 0xFFFFFFFF;
		final int gridColor2 = 0xFFE0E0E0;
		final int gridTagTextColor1 = 0xFFA0A0A0;
		final int gridTagTextColor2 = 0xFFFFFFFF;
		// draw block background
		for (int i = 0; i < cfg.gridCountX; i++) {
			for (int j = 0; j < cfg.gridCountY; j++) {
				float x = i * unitSizeX;
				float y = j * unitSizeY;
				paint.setColor((i + j) % 2 == 0 ? gridColor1 : gridColor2);
				canvas.drawRect(Rect.makeLTRB(x, y, x + unitSizeX, y + unitSizeY), paint);
			}
		}
		// draw tag text
		FontMetrics metrics = font.getMetrics();
		font.setSize(fontSize);
		for (int i = 0; i < cfg.gridCountX; i++) {
			for (int j = 0; j < cfg.gridCountY; j++) {
				float x = i * unitSizeX;
				float y = j * unitSizeY;
				paint.setColor((i + j) % 2 == 0 ? gridTagTextColor1 : gridTagTextColor2);
				String sb = new StringBuilder().appendCodePoint('A' + i).appendCodePoint('1' + j).toString();
				canvas.drawString(sb, x + fontSize * 0.25f, y - metrics.getTop(), font, paint);
			}
		}
	}

	public static void calcLinePointConfined(float x, float y, float x2, float y2, float left,
											 float right, float top, float bottom, float[] out) {
		float w = x2 > x ? right : left;
		float h = y2 < y ? top : bottom;
		float k = (float) Math.atan2(h, w);
		float sigx = Math.signum(x2 - x);
		float sigy = Math.signum(y2 - y);
		float absRad = (float) Math.atan2(Math.abs(y2 - y), Math.abs(x2 - x));
		if (absRad > k) {
			out[0] = (float) (x + sigx * h / Math.tan(absRad));
			out[1] = y + sigy * h;
		} else {
			out[0] = x + sigx * w;
			out[1] = (float) (y + sigy * w * Math.tan(absRad));
		}
	}

	public static void doDrawMolecule(Canvas canvas, Paint paint, Molecule molecule, Font font, MoleculeRenderConfig cfg) {
		paint.setAntiAlias(true);

		final int textColor = 0xFF000000;

		final float scaleFactor = cfg.scaleFactor;

		long beginTimestamp = System.currentTimeMillis();

		List<Integer> selectedChiral = cfg.shownChiralCarbons;

		float dx = 0 + cfg.fontSize;
		float dy = cfg.height - cfg.fontSize;
		float mx = molecule.minX();
		float my = molecule.minY();
		FontMetrics fontMetrics = font.getMetrics();

		float distance = (fontMetrics.getBottom() - fontMetrics.getTop()) / 2 - fontMetrics.getBottom();
		if (cfg.labelTop == null || cfg.labelTop.length < molecule.atomCount()) {
			cfg.labelTop = new float[molecule.atomCount()];
			cfg.labelBottom = new float[molecule.atomCount()];
			cfg.labelLeft = new float[molecule.atomCount()];
			cfg.labelRight = new float[molecule.atomCount()];
		}
		paint.setColor(textColor);
		paint.setStrokeWidth(cfg.fontSize / 12);
		Molecule.Atom atom, p1, p2;
		Molecule.Bond bond;
		for (int i = 0; i < molecule.atomCount(); i++) {
			atom = molecule.getAtom(i + 1);
			font.setSize(cfg.fontSize);
			if (atom.element.equals("C") && atom.charge == 0 && atom.unpaired == 0 &&
					(atom.showFlag & Molecule.SHOW_FLAG_EXPLICIT) == 0) {
				cfg.labelLeft[i] = cfg.labelRight[i] = 0;
				cfg.labelTop[i] = cfg.labelBottom[i] = 0;
				if (selectedChiral != null && selectedChiral.contains(i + 1)) {
					float textWidth = font.measureTextWidth("*");
					float r = textWidth / 4 + font.getSize() / 4;
					float cx, cy;
					if (atom.spareSpace == Molecule.DIRECTION_BOTTOM) {
						cx = dx + scaleFactor * (atom.x - mx);
						cy = dy - scaleFactor * (atom.y - my) + 2 * r;
					} else if (atom.spareSpace == Molecule.DIRECTION_LEFT) {
						cx = dx + scaleFactor * (atom.x - mx) - 2 * r;
						cy = dy - scaleFactor * (atom.y - my);
					} else if (atom.spareSpace == Molecule.DIRECTION_TOP) {
						cx = dx + scaleFactor * (atom.x - mx);
						cy = dy - scaleFactor * (atom.y - my) - 2 * r;
					} else {//DIRECTION_RIGHT
						cx = dx + scaleFactor * (atom.x - mx) + 2 * r;
						cy = dy - scaleFactor * (atom.y - my);
					}
					canvas.drawString("*", cx - textWidth / 2, cy + distance, font, paint);
				}
			} else {
				cfg.labelLeft[i] = cfg.labelRight[i] = font.measureTextWidth(atom.element) / 2;
				cfg.labelTop[i] = (-fontMetrics.getAscent()) / 2;
				cfg.labelBottom[i] = (fontMetrics.getDescent() / 2 - fontMetrics.getAscent()) / 2;
				drawStringCenterHorizontal(canvas, atom.element, dx + scaleFactor * (atom.x - mx),
						dy - scaleFactor * (atom.y - my) + distance, font, paint);
				if (selectedChiral != null && selectedChiral.contains(i + 1)) {
					float sWidth = font.measureTextWidth("*");
					canvas.drawString("*",
							dx + scaleFactor * (atom.x - mx) - cfg.labelLeft[i] - sWidth / 2f,
							dy - scaleFactor * (atom.y - my) + distance, font, paint);
					cfg.labelLeft[i] += sWidth;
				}
				if (atom.charge != 0) {
					int c = atom.charge;
					String text;
					if (c > 0) {
						if (c == 1) {
							text = "+";
						} else {
							text = c + "+";
						}
					} else {
						if (c == -1) {
							text = "-";
						} else {
							text = -c + "-";
						}
					}
					font.setSize(cfg.fontSize / 1.5f);
					float chgwidth = font.measureTextWidth(text);
					FontMetrics chgFontMetrics = font.getMetrics();
					float chgdis = (chgFontMetrics.getBottom() - chgFontMetrics.getTop()) / 2
							- chgFontMetrics.getBottom();
					drawStringCenterHorizontal(canvas, text,
							dx + scaleFactor * (atom.x - mx) + cfg.labelRight[i] + chgwidth / 2,
							dy - scaleFactor * (atom.y - my) + fontMetrics.getTop() / 3 + chgdis,
							font, paint);
				}
				if (atom.hydrogenCount > 0) {
					int hCount = atom.hydrogenCount;
					float hNumWidth = 0;
					if (hCount > 1) {
						font.setSize(cfg.fontSize / 2);
						hNumWidth = font.measureTextWidth("" + hCount);
					}
					font.setSize(cfg.fontSize);
					float hWidth = font.measureTextWidth("H");
					float hcx, hcy;
					if (atom.spareSpace == Molecule.DIRECTION_BOTTOM) {
						hcx = dx + scaleFactor * (atom.x - mx);
						hcy = dy - scaleFactor * (atom.y - my) - fontMetrics.getAscent();
						cfg.labelBottom[i] += -fontMetrics.getAscent();
					} else if (atom.spareSpace == Molecule.DIRECTION_LEFT) {
						hcx = dx + scaleFactor * (atom.x - mx) - cfg.labelLeft[i] - hWidth / 2
								- hNumWidth;
						cfg.labelLeft[i] += hWidth + hNumWidth / 2 * 2;
						hcy = dy - scaleFactor * (atom.y - my);
					} else if (atom.spareSpace == Molecule.DIRECTION_TOP) {
						hcx = dx + scaleFactor * (atom.x - mx);
						hcy = dy - scaleFactor * (atom.y - my) + fontMetrics.getAscent();
						cfg.labelTop[i] += -fontMetrics.getAscent();
					} else {//DIRECTION_RIGHT
						hcx = dx + scaleFactor * (atom.x - mx) + cfg.labelRight[i] + hWidth / 2;
						cfg.labelRight[i] += hWidth + hNumWidth / 2 * 2;
						hcy = dy - scaleFactor * (atom.y - my);
					}
					drawStringCenterHorizontal(canvas, "H", hcx, hcy + distance, font, paint);
					if (hCount > 1) {
						font.setSize(cfg.fontSize / 2);
						drawStringCenterHorizontal(canvas, "" + hCount, hcx + hWidth / 2 + hNumWidth / 2,
								hcy - fontMetrics.getTop() / 2, font, paint);
					}
				}
			}
		}
		for (int i = 0; i < molecule.bondCount(); i++) {
			bond = molecule.getBond(i + 1);
			p1 = molecule.getAtom(bond.from);
			p2 = molecule.getAtom(bond.to);
			drawBond(canvas, paint, cfg,
					dx + scaleFactor * (p1.x - mx), dy - scaleFactor * (p1.y - my),
					dx + scaleFactor * (p2.x - mx), dy - scaleFactor * (p2.y - my),
					bond.type, bond.from - 1, bond.to - 1);
		}
	}

	private static void drawBond(Canvas canvas, Paint paint, MoleculeRenderConfig cfg, float x1, float y1, float x2, float y2, int type,
								 @IndexFrom(0) int idx1, @IndexFrom(0) int idx2) {
		float[] ret = new float[2];
		float rad = (float) Math.atan2(y2 - y1, x2 - x1);
		calcLinePointConfined(x1, y1, x2, y2, cfg.labelLeft[idx1], cfg.labelRight[idx1], cfg.labelTop[idx1],
				cfg.labelBottom[idx1], ret);
		float basex1 = ret[0];
		float basey1 = ret[1];
		calcLinePointConfined(x2, y2, x1, y1, cfg.labelLeft[idx2], cfg.labelRight[idx2], cfg.labelTop[idx2],
				cfg.labelBottom[idx2], ret);
		float delta = cfg.fontSize / 6;
		float basex2 = ret[0];
		float basey2 = ret[1];
		float dx = (float) (Math.sin(rad) * delta);
		float dy = (float) (Math.cos(rad) * delta);
		switch (type) {
			case 1:
				canvas.drawLine(basex1, basey1, basex2, basey2, paint);
				break;
			case 2:
				canvas.drawLine(basex1 + dx / 2, basey1 - dy / 2, basex2 + dx / 2, basey2 - dy / 2, paint);
				canvas.drawLine(basex1 - dx / 2, basey1 + dy / 2, basex2 - dx / 2, basey2 + dy / 2, paint);
				break;
			case 3:
				canvas.drawLine(basex1, basey1, basex2, basey2, paint);
				canvas.drawLine(basex1 + dx, basey1 - dy, basex2 + dx, basey2 - dy, paint);
				canvas.drawLine(basex1 - dx, basey1 + dy, basex2 - dx, basey2 + dy, paint);
				break;
			default:
				throw new IllegalArgumentException("Unknown bond type: " + type);
		}
	}

	private static void drawStringCenterHorizontal(@NotNull Canvas canvas, @NotNull String s, float x, float y, Font font, @NotNull Paint paint) {
		float width = font.measureTextWidth(s);
		canvas.drawString(s, x - width / 2, y, font, paint);
	}

	public static void saveImage(Image image) {
		FileUtils.write2File(Objects.requireNonNull(image.encodeToData()).getBytes(), "files",
				IdUtil.simpleUUID() + ".png");
	}
}