package com.gousade.service;

import com.gousade.entity.dto.ChiralCarbonCaptchaDTO;
import com.gousade.entity.query.ChiralCarbonCaptchaQuery;

/**
 * @author woxigousade <woxigsd@gmail.com>
 * @date 2022/12/03
 */

public interface ChiralCarbonCaptchaService {

	ChiralCarbonCaptchaDTO getChiralCarbonCaptcha(ChiralCarbonCaptchaQuery query);
}
