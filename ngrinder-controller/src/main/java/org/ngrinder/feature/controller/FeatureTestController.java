package org.ngrinder.feature.controller;

import org.aspectj.util.FileUtil;
import org.ngrinder.common.controller.BaseController;
import org.ngrinder.model.User;
import org.ngrinder.script.model.FileEntry;
import org.ngrinder.script.service.FileEntryService;
import org.ngrinder.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FilenameUtils.getPath;

/**
 * 增加界面会生成脚本
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Controller
@RequestMapping("/feature")
public class FeatureTestController extends BaseController {

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
	 * @return
	 */
	@RequestMapping(value = "/uploadData", method = RequestMethod.GET)
	@ResponseBody
	public String uploadData() {
		//获取用户
		User user = userService.getOne("admin");

		//读取文件封装成FileEntry
		FileEntry fileEntry = new FileEntry();
		try {
			fileEntry.setContent(FileUtil.readAsString(new File("E:\\tmp\\user.csv")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileEntry.setPath("resources/jd.com.groovy/user.csv");

		fileEntryService.save(user, fileEntry);

		fileEntryService.getOne(user,"resources/jd.com.groovy/user.csv").getContent();

		//TODO 需要将上传资源和脚本关联

		String basePath = getPath(fileEntry.getPath());
		System.out.println(basePath);
		return basePath;
	}

}
