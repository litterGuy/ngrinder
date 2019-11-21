package org.ngrinder.feature.controller;

import org.ngrinder.common.controller.BaseController;
import org.ngrinder.model.User;
import org.ngrinder.script.model.FileEntry;
import org.ngrinder.script.service.FileEntryService;
import org.ngrinder.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

}
