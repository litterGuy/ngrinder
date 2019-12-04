package org.ngrinder.feature.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.ngrinder.agent.service.AgentManagerService;
import org.ngrinder.common.constants.GrinderConstants;
import org.ngrinder.common.controller.BaseController;
import org.ngrinder.feature.model.FileData;
import org.ngrinder.feature.model.TestPms;
import org.ngrinder.feature.utils.BeanCovertUtils;
import org.ngrinder.infra.config.Config;
import org.ngrinder.model.PerfTest;
import org.ngrinder.model.Status;
import org.ngrinder.model.User;
import org.ngrinder.perftest.service.AgentManager;
import org.ngrinder.perftest.service.PerfTestService;
import org.ngrinder.script.handler.ScriptHandler;
import org.ngrinder.script.model.FileEntry;
import org.ngrinder.script.service.FileEntryService;
import org.ngrinder.security.SecuredUser;
import org.ngrinder.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.ngrinder.common.util.CollectionUtils.buildMap;
import static org.ngrinder.common.util.Preconditions.*;

/**
 * 增加界面会生成脚本
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Controller
@RequestMapping("/feature")
public class FeatureTestController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(FeatureTestController.class);

	@Autowired
	private IUserService userService;
	@Autowired
	private FileEntryService fileEntryService;
	@Autowired
	private Config config;
	@Autowired
	private AgentManager agentManager;
	@Autowired
	private AgentManagerService agentManagerService;
	@Autowired
	private PerfTestService perfTestService;

	/**
	 * 创建压测脚本，创建测试，执行测试
	 * 因为场景在ngrinder-sampling中已经生成，所以直接生成并执行测试、不再有保存测试
	 *
	 * @return
	 */
	@RequestMapping(value = "/createTest", method = RequestMethod.POST)
	@ResponseBody
	public Object createTest(@RequestBody TestPms testPms) {
		User user = userService.getOne("admin");
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new SecuredUser(user, null), null);
		SecurityContextImpl context = new SecurityContextImpl();
		context.setAuthentication(token);
		SecurityContextHolder.setContext(context);

		String samplingUrl = config.getControllerProperties().getProperty("controller.samp_url");
		//1、获取参数，生成脚本
		String scriptType = "groovy";
		String fileName = "TestRunner.groovy";//暂时定为groovy，后续如果增加python脚本再做修改
		String name = testPms.getName();
		String path = testPms.getId().toString();//scenesId做中间路径
		ScriptHandler scriptHandler = fileEntryService.getScriptHandler(scriptType);
		FileEntry entry = new FileEntry();
		entry.setPath(fileName);

		entry = fileEntryService.prepareNewEntryForScenes(user, path, fileName, name, testPms.getRequestPmsList(), scriptHandler,
			false, samplingUrl, this.getFileDataStrList(testPms));
		//如果存在targetHosts，那么需要往svn中存储
		if (StringUtils.isNotEmpty(testPms.getTargetHosts())) {
			entry.setProperties(buildMap("targetHosts", testPms.getTargetHosts()));
		}
		//如果存在数据源文件，增加到脚本svn文件属性中
		if (testPms.getFileDataList().size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (FileData fileData : testPms.getFileDataList()) {
				sb.append(fileData.getPath());
				sb.append(",");
			}
			entry.setProperties(buildMap("resourcesData", sb.toString()));
		}
		fileEntryService.save(user, entry);

		String basePath = entry.getPath();
		//TODO 增加脚本校验功能，看流程上怎么修改合适
		//2、转化perTest,生成perf_test数据
		PerfTest perfTest = BeanCovertUtils.getPerfTestBean(testPms);
		perfTest.setScriptName(basePath);
		perfTest.setScriptRevision(-1L);
		perfTest.setStatus(Status.valueOf("READY"));
		perfTest.setScheduledTime(testPms.getScheduledTime());
		validate(user, null, perfTest);
		perfTest.prepare(false);
		perfTest = perfTestService.save(user, perfTest);

		Map<String, Object> result = new HashMap<>();
		result.put("code", 0);
		if (perfTest.getId() == null || perfTest.getId() <= 0) {
			result.put("code", 1);
		}
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		return gson.toJson(result);
	}

	/**
	 * 跳往新建脚本ui界面
	 * TODO
	 *
	 * @return
	 */
	@RequestMapping(value = "/createScript", method = RequestMethod.GET)
	public String createScript() {

		return "feature/create";
	}


	/**
	 * 上传数据文件
	 *
	 * @return
	 */
	@RequestMapping(value = "/uploadData", method = RequestMethod.POST)
	@ResponseBody
	public String uploadData(@RequestParam("uploadFile") MultipartFile file) {
		//TODO 删除多余的数据文件
		User user = userService.getOne("admin");
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new SecuredUser(user, null), null);
		SecurityContextImpl context = new SecurityContextImpl();
		context.setAuthentication(token);
		SecurityContextHolder.setContext(context);

		//读取文件封装成FileEntry
		FileEntry fileEntry = new FileEntry();
		try {
			fileEntry.setContentBytes(file.getBytes());
			fileEntry.setPath("resources/" + UUID.randomUUID().toString().replaceAll("-", "") + "/" + file.getOriginalFilename());
			fileEntryService.save(user, fileEntry);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return fileEntry.getPath();
	}

	private List<String> getFileDataStrList(TestPms testPms) {
		List<String> list = new ArrayList<>();
		Gson gson = new Gson();
		for (int i = 0; i < testPms.getFileDataList().size(); i++) {
			list.add(gson.toJson(testPms.getFileDataList().get(i)));
		}
		return list;
	}

	@SuppressWarnings("ConstantConditions")
	private void validate(User user, PerfTest oldOne, PerfTest newOne) {
		if (oldOne == null) {
			oldOne = new PerfTest();
			oldOne.init();
		}
		newOne = oldOne.merge(newOne);
		checkNotEmpty(newOne.getTestName(), "testName should be provided");
		checkArgument(newOne.getStatus().equals(Status.READY) || newOne.getStatus().equals(Status.SAVED),
			"status only allows SAVE or READY");
		if (newOne.isThresholdRunCount()) {
			final Integer runCount = newOne.getRunCount();
			checkArgument(runCount > 0 && runCount <= agentManager
					.getMaxRunCount(),
				"runCount should be equal to or less than %s", agentManager.getMaxRunCount());
		} else {
			final Long duration = newOne.getDuration();
			checkArgument(duration > 0 && duration <= (((long) agentManager.getMaxRunHour()) *
					3600000L),
				"duration should be equal to or less than %s", agentManager.getMaxRunHour());
		}
		Map<String, MutableInt> agentCountMap = agentManagerService.getAvailableAgentCountMap(user);
		MutableInt agentCountObj = agentCountMap.get(isClustered() ? newOne.getRegion() : Config.NONE_REGION);
		checkNotNull(agentCountObj, "region should be within current region list");
		int agentMaxCount = agentCountObj.intValue();
		checkArgument(newOne.getAgentCount() <= agentMaxCount, "test agent should be equal to or less than %s",
			agentMaxCount);
		if (newOne.getStatus().equals(Status.READY)) {
			checkArgument(newOne.getAgentCount() >= 1, "agentCount should be more than 1 when it's READY status.");
		}

		checkArgument(newOne.getVuserPerAgent() <= agentManager.getMaxVuserPerAgent(),
			"vuserPerAgent should be equal to or less than %s", agentManager.getMaxVuserPerAgent());
		if (getConfig().isSecurityEnabled() && GrinderConstants.GRINDER_SECURITY_LEVEL_NORMAL.equals(getConfig().getSecurityLevel())) {
			checkArgument(StringUtils.isNotEmpty(newOne.getTargetHosts()),
				"targetHosts should be provided when security mode is enabled");
		}
		if (newOne.getStatus() != Status.SAVED) {
			checkArgument(StringUtils.isNotBlank(newOne.getScriptName()), "scriptName should be provided.");
		}
		checkArgument(newOne.getVuserPerAgent() == newOne.getProcesses() * newOne.getThreads(),
			"vuserPerAgent should be equal to (processes * threads)");
	}
}
