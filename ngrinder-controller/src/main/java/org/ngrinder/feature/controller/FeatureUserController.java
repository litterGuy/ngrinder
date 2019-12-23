package org.ngrinder.feature.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.ngrinder.agent.service.AgentManagerService;
import org.ngrinder.common.constant.ControllerConstants;
import org.ngrinder.common.constant.WebConstants;
import org.ngrinder.infra.config.Config;
import org.ngrinder.model.AgentInfo;
import org.ngrinder.model.PerfTest;
import org.ngrinder.model.RampUp;
import org.ngrinder.model.User;
import org.ngrinder.operation.service.AnnouncementService;
import org.ngrinder.perftest.service.AgentManager;
import org.ngrinder.perftest.service.PerfTestService;
import org.ngrinder.region.service.RegionService;
import org.ngrinder.security.SecuredUser;
import org.ngrinder.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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
	@Autowired
	private PerfTestService perfTestService;
	@Autowired
	private AgentManagerService agentManagerService;
	@Autowired
	private RegionService regionService;
	@Autowired
	private AgentManager agentManager;
	@Autowired
	private Config config;
	@Autowired
	private AnnouncementService announcementService;


	@RequestMapping(value = "login", method = RequestMethod.POST)
	@ResponseBody
	public Object login(String userId, String password) {
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

	@RequestMapping(value = "perfList", method = RequestMethod.GET)
	@ResponseBody
	public Object perfList(String userId, int page, int pageSize, String name, String queryFilter) {
		User user = this.setUser(userId);
		PageRequest pageRequest = new PageRequest(page, pageSize, new Sort(Sort.Direction.DESC, "id"));
		final Page<PerfTest> testList = perfTestService.getPagedAll(user, name, null, queryFilter, pageRequest);
		Map<String, Object> result = new HashMap<>();
		result.put("code", 0);
		Map<String, Object> tmp = new HashMap<>();
		tmp.put("total", testList.getTotalElements());
		tmp.put("list", testList.getContent());
		result.put("data", tmp);
		return gson.toJson(result);
	}

	@RequestMapping(value = "perfDelete", method = RequestMethod.GET)
	@ResponseBody
	public Object perfDelete(String userId, String ids) {
		User user = this.setUser(userId);
		for (String idStr : StringUtils.split(ids, ",")) {
			perfTestService.delete(user, NumberUtils.toLong(idStr, 0));
		}
		Map<String, Object> result = new HashMap<>();
		result.put("code", 0);
		return gson.toJson(result);
	}

	@RequestMapping(value = "agentList", method = RequestMethod.GET)
	@ResponseBody
	public Object agentList(String ip, int page, int limit) {
		PageRequest pageRequest = new PageRequest(page, limit, new Sort(Sort.Direction.DESC, "id"));
		final Page<AgentInfo> testList = agentManagerService.getPagedAll(ip, pageRequest);
		Map<String, Object> result = new HashMap<>();
		result.put("code", 0);
		Map<String, Object> tmp = new HashMap<>();
		tmp.put("total", testList.getTotalElements());
		tmp.put("list", testList.getContent());
		result.put("data", tmp);
		return gson.toJson(result);
	}


	@RequestMapping(value = "agentConfig", method = RequestMethod.GET)
	@ResponseBody
	public Object agentConfig(String userId) {
		User user = this.setUser(userId);
		ModelMap model = new ModelMap();
		Map<String, MutableInt> agentCountMap = agentManagerService.getAvailableAgentCountMap(user);
		model.addAttribute(WebConstants.PARAM_REGION_AGENT_COUNT_MAP, agentCountMap);
		model.addAttribute(WebConstants.PARAM_REGION_LIST, regionService.getAllVisibleRegionNames());
		model.addAttribute(WebConstants.PARAM_PROCESS_THREAD_POLICY_SCRIPT, perfTestService.getProcessAndThreadPolicyScript());
		addDefaultAttributeOnModel(model);
		String timeZone = user.getTimeZone();
		int offset;
		if (StringUtils.isNotBlank(timeZone)) {
			offset = TimeZone.getTimeZone(timeZone).getOffset(System.currentTimeMillis());
		} else {
			offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
		}
		model.addAttribute(WebConstants.PARAM_TIMEZONE_OFFSET, offset);
		Map<String, Object> result = new HashMap<>();
		result.put("code", 0);
		result.put("data", model);
		return result;
	}

	@RequestMapping(value = "announcement", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object announcement() {
		String announcement = announcementService.getOne();
		Map<String, Object> result = new HashMap<>();
		result.put("code", 0);
		result.put("data", announcement);
		return gson.toJson(result);
	}

	private void addDefaultAttributeOnModel(ModelMap model) {
		model.addAttribute(WebConstants.PARAM_AVAILABLE_RAMP_UP_TYPE, RampUp.values());
		model.addAttribute(WebConstants.PARAM_MAX_VUSER_PER_AGENT, agentManager.getMaxVuserPerAgent());
		model.addAttribute(WebConstants.PARAM_MAX_RUN_COUNT, agentManager.getMaxRunCount());
		if (config.isSecurityEnabled()) {
			model.addAttribute(WebConstants.PARAM_SECURITY_LEVEL, config.getSecurityLevel());
		}
		model.addAttribute(WebConstants.PARAM_MAX_RUN_HOUR, agentManager.getMaxRunHour());
		model.addAttribute(WebConstants.PARAM_SAFE_FILE_DISTRIBUTION,
			config.getControllerProperties().getPropertyBoolean(ControllerConstants.PROP_CONTROLLER_SAFE_DIST));
	}

	private User setUser(String userId) {
		User user = userService.getOne(userId);
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new SecuredUser(user, null), null);
		SecurityContextImpl context = new SecurityContextImpl();
		context.setAuthentication(token);
		SecurityContextHolder.setContext(context);
		return user;
	}
}
