package org.ngrinder.feature.controller;

import com.google.gson.Gson;
import org.ngrinder.common.controller.BaseController;
import org.ngrinder.feature.model.TestPms;
import org.ngrinder.infra.config.Config;
import org.ngrinder.model.User;
import org.ngrinder.script.model.FileEntry;
import org.ngrinder.script.service.FileEntryService;
import org.ngrinder.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.apache.commons.io.FilenameUtils.getPath;

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
		//1、获取参数，生成脚本

		//2、生成perf_test数据
		//3、开启执行测试

		Gson gson = new Gson();
		return gson.toJson(user);
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
	@RequestMapping(value = "/uploadData", method = RequestMethod.GET)
	@ResponseBody
	public String uploadData(User user, @RequestParam("uploadFile") MultipartFile file) {

		//读取文件封装成FileEntry
		FileEntry fileEntry = new FileEntry();
		try {
			fileEntry.setContentBytes(file.getBytes());
			fileEntry.setPath("resources/" + UUID.randomUUID().toString().replaceAll("-", "") + "/user.csv");
			fileEntryService.save(user, fileEntry);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return getPath(fileEntry.getPath());
	}

	@Autowired
	private Config config;

	@RequestMapping(value = "/foo", method = RequestMethod.GET)
	@ResponseBody
	public Object foo() {
		//采样数据发送的地址
		String url = config.getControllerProperties().getProperty("controller.samp_url");
		return url;
	}
}
