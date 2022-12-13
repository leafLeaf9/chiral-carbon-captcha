package com.gousade.captcha;


import com.gousade.captcha.carbon.Molecule;
import com.gousade.captcha.carbon.MoleculeRender;
import org.jetbrains.skija.Image;

public class MoleculeRenderTests {
	public static void main(String[] args) {
		MoleculeRender.MoleculeRenderConfig renderConfig = new MoleculeRender.MoleculeRenderConfig();
		renderConfig.setWidth(3);
		renderConfig.setHeight(3);
		renderConfig.setFontSize(16.0F);
		renderConfig.setScaleFactor(0.75F);

		Molecule.Atom atom = new Molecule.Atom(1, "H", 1,1,1,1,1,1, 5.0F, 5.0F, 0.0F, new String[]{});

		Molecule.Bond bond = new Molecule.Bond(1, 2, 1, 0, new String[]{});

		Image image = MoleculeRender.renderMoleculeAsImage(new Molecule(500002L, new Molecule.Atom[]{atom, atom, atom},
						new Molecule.Bond[]{bond, bond, bond}, ""),
				renderConfig);
		MoleculeRender.saveImage(image);
	}
}
