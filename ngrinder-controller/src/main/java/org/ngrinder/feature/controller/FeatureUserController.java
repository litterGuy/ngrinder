package org.ngrinder.feature.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.ngrinder.model.User;
import org.ngrinder.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 登陆接口，提供给中间件使用
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Controller
@RequestMapping("/feature/user")
public class FeatureUserController {
	private static final Logger LOG = LoggerFactory.getLogger(FeatureUserController.class);

	private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

	@Autowired
	private UserService userService;
	@Autowired
	private ShaPasswordEncoder passwordEncoder;

	@RequestMapping(value = "login", method = RequestMethod.POST)
	@ResponseBody
	public Object login(String userId,String password) {
		Map<String, Object> result = new HashMap<>();
		result.put("code", 0);
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(password)) {
			result.put("code", 1);
			result.put("errMsg", "userId or password can not be empty");
			return gson.toJson(result);
		}
		User tmp = userService.getOne(userId);
		if (tmp == null) {
			result.put("code", 1);
			result.put("errMsg", "userId is error and can`t find user");
			return gson.toJson(result);
		}
		if (!passwordEncoder.isPasswordValid(tmp.getPassword(), password, userId)) {
			result.put("code", 1);
			result.put("errMsg", "password is not right");
			return gson.toJson(result);
		}
		result.put("data", tmp);
		return gson.toJson(result);
	}
}
