package com.gousade.captcha.carbon.util;

import cn.hutool.core.io.file.LineSeparator;
import com.gousade.captcha.carbon.MdlMolParser;
import com.gousade.captcha.carbon.Molecule;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author woxigousade <woxigsd@gmail.com>
 * @date 2022/12/03
 */
public class MoleculeUtils {
	private static final List<Molecule> molecules = init();

	private MoleculeUtils() {
	}

	private static List<Molecule> init() {
		List<Molecule> result = new ArrayList<>();
		String locationPattern = String.join(File.separator, ResourceUtils.CLASSPATH_URL_PREFIX, "static",
				"captcha", "carbon", "mol", "*.mol");
		try {
			Resource[] resources = new PathMatchingResourcePatternResolver().getResources(locationPattern);
			Arrays.stream(resources).forEach(resource -> {
				StringBuilder molStr = new StringBuilder();
				try (InputStreamReader input = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
					 BufferedReader bufferReader = new BufferedReader(input)) {
					String line;
					while ((line = bufferReader.readLine()) != null) {
						molStr.append(line).append(LineSeparator.LINUX.getValue());
					}
					Molecule molecule = MdlMolParser.parseString(molStr.toString());
					result.add(molecule);
				} catch (IOException | MdlMolParser.BadMolFormatException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static MoleculeUtils getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public Molecule randomMolecule() {
		return molecules.get(new Random().nextInt(molecules.size()));
	}

	private static class InstanceHolder {
		private static final MoleculeUtils INSTANCE = new MoleculeUtils();
	}
}
