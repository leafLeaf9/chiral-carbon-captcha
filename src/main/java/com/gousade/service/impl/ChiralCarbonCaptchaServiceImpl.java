package com.gousade.service.impl;

import cn.hutool.core.img.ImgUtil;
import com.gousade.captcha.carbon.Molecule;
import com.gousade.captcha.carbon.MoleculeRender;
import com.gousade.captcha.carbon.util.ChiralCarbonHelper;
import com.gousade.captcha.carbon.util.MoleculeUtils;
import com.gousade.entity.dto.ChiralCarbonCaptchaDTO;
import com.gousade.entity.query.ChiralCarbonCaptchaQuery;
import com.gousade.service.ChiralCarbonCaptchaService;
import com.gousade.util.ExcelUtils;
import com.gousade.util.ImageUtils;
import lombok.val;
import org.jetbrains.skija.Image;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author woxigousade <woxigsd@gmail.com>
 * @date 2022/12/03
 */
@Service
public class ChiralCarbonCaptchaServiceImpl implements ChiralCarbonCaptchaService {
	@Override
	public ChiralCarbonCaptchaDTO getChiralCarbonCaptcha(ChiralCarbonCaptchaQuery query) {
		Molecule molecule = MoleculeUtils.getInstance().randomMolecule();
		val cfg = initMoleculeConfig(molecule);
		val chiralCarbons = ChiralCarbonHelper.getMoleculeChiralCarbons(molecule);
		List<String> regions = chiralCarbons.stream().map(index -> {
			val gridWidth = cfg.width / cfg.gridCountX;
			val gridHeight = cfg.height / cfg.gridCountY;
			val x = cfg.transformX(molecule, molecule.atomX(index));
			val y = cfg.transformY(molecule, molecule.atomY(index));
			val xIndex = (int) Math.floor(x / gridWidth);
			val yIndex = (int) Math.floor(y / gridHeight);
			return ExcelUtils.getExcelColumn(xIndex) + (yIndex + 1);
		}).distinct().sorted().collect(Collectors.toList());
		cfg.setShownChiralCarbons(query.isHint() ? new ArrayList<>(chiralCarbons) : Collections.emptyList());
		Image image = MoleculeRender.renderMoleculeAsImage(molecule, cfg);
		//noinspection ConstantConditions
		String base64DataUri = ImageUtils.toBase64DataUri(image.encodeToData().getBytes(), ImgUtil.IMAGE_TYPE_PNG);
		return ChiralCarbonCaptchaDTO.builder().cid(molecule.getCid()).base64(base64DataUri)
				.regions(query.isAnswer() ? regions : Collections.emptyList()).build();
	}

	private MoleculeRender.MoleculeRenderConfig initMoleculeConfig(Molecule molecule) {
		val cfg = MoleculeRender.calculateRenderRect(molecule, 720);
		cfg.gridCountX = 5;
		cfg.gridCountY = 3;
		cfg.drawGrid = true;
		return cfg;
	}
}
