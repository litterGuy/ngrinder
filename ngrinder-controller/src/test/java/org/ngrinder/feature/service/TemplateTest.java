package org.ngrinder.feature.service;

import HTTPClient.NVPair;
import com.google.gson.Gson;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.junit.Test;
import org.ngrinder.feature.model.*;
import org.ngrinder.infra.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.StringWriter;
import java.util.*;

import static org.ngrinder.common.util.ExceptionUtils.processException;

public class TemplateTest {

	@Test
	public void randomTest() {
		System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
	}

	@Autowired
	private Config config;

	@Test
	public void test() {
		TestPms testPms = new TestPms();
		testPms.setName("ngrinder login test");
		testPms.setFileDataList(this.getFileDataList());
		List<RequestPms> requestPmsList = new ArrayList<>();
		testPms.setRequestPmsList(requestPmsList);

		requestPmsList.add(this.getPostLogin());
		requestPmsList.add(this.getConsolidationPoint());
		requestPmsList.add(this.getGetReq());
		requestPmsList.add(this.getGetReq());

		//为了防止生成多个请求时参数名称重复，预先生成函数名称，最后在test内调用
		for (int i = 0; i < requestPmsList.size(); i++) {
			requestPmsList.get(i).setFunName("test_" + i);
		}

//		Gson gson = new Gson();
//		System.out.println(gson.toJson(testPms));

		Map<String, Object> map = new HashMap();
		map.put("userName", "admin");
		map.put("name", testPms.getName());
		map.put("list", testPms.getRequestPmsList());
		//为方便freemarker使用，将object转化成string
		map.put("fileDataList", this.getFileDataStrList(testPms));
		map.put("samplingUrl", "http://www.baidu.com/sampling");
		//传递redis
		map.put("redisHost","127.0.0.1");
		map.put("redisPort","6379");
		map.put("redisPassword","123456");
		System.out.println(this.getScriptTemplate(map));

	}

	public String getScriptTemplate(Map<String, Object> values) {
		try {
			Configuration freemarkerConfig = new Configuration();
			ClassPathResource cpr = new ClassPathResource("script_template");
			freemarkerConfig.setDirectoryForTemplateLoading(cpr.getFile());
			freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
			Template template = freemarkerConfig.getTemplate("basic_scenes_template_groovy.ftl");
			StringWriter writer = new StringWriter();
			template.process(values, writer);
			return writer.toString();
		} catch (Exception e) {
			throw processException("Error while fetching the script template.", e);
		}
	}

	private List<FileData> getFileDataList() {
		List<FileData> list = new ArrayList<>();

		FileData fileData = new FileData();
		fileData.setName("请求参数");
		fileData.setHasHead(0);
		fileData.setPath("./resources/test/user.csv");

		List<NVPair> nvPairList = new ArrayList<>();
		NVPair nvPair = new NVPair("j_username", 0 + "");
		nvPairList.add(nvPair);
		NVPair nvPair2 = new NVPair("j_password", 1 + "");
		nvPairList.add(nvPair2);
		fileData.setParamsList(nvPairList);

		list.add(fileData);
		return list;
	}

	private List<String> getFileDataStrList(TestPms testPms) {
		List<String> list = new ArrayList<>();
		Gson gson = new Gson();
		for (int i = 0; i < testPms.getFileDataList().size(); i++) {
			list.add(gson.toJson(testPms.getFileDataList().get(i)));
		}
		return list;
	}

	private RequestPms getPostLogin() {
		RequestPms requestPms = new RequestPms();
		requestPms.setId(11);
		requestPms.setApiName("登陆");
		requestPms.setSort(0);
		requestPms.setType(0);
		requestPms.setMethod("POST");
		requestPms.setUrl("http://192.168.0.11:8080/form_login");

		List<NVPair> headers = new ArrayList<>();
		headers.add(new NVPair("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
		headers.add(new NVPair("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36"));

		requestPms.setHeaderList(headers);
		requestPms.setContentType("application/x-www-form-urlencoded");
		requestPms.setBody(null);

		List<NVPair> params = new ArrayList<>();
		params.add(new NVPair("j_username", "${j_username}"));
		params.add(new NVPair("j_password", "${j_password}"));
		params.add(new NVPair("x", "47"));
		params.add(new NVPair("y", "34"));
		params.add(new NVPair("native_language", "cn"));
		params.add(new NVPair("user_timezone", "Asia/Shanghai"));

		requestPms.setParamList(params);

		List<OutParams> outParamsList = new ArrayList<>();
//		requestPms.setOutParamsList(outParamsList);
		OutParams outParams = new OutParams();
		outParams.setName("status");
		outParams.setIndex(0);
		outParams.setSource(1);
		outParams.setResolveExpress("status");
		outParamsList.add(outParams);

		List<Assertion> assertionList = new ArrayList<>();
//		requestPms.setAssertionList(assertionList);
		Assertion assertion = new Assertion();
		assertion.setType(1);
		assertion.setName("code");
		assertion.setFactor(">");
		assertion.setContent("0");
		assertionList.add(assertion);
		return requestPms;
	}

	private RequestPms getConsolidationPoint() {
		RequestPms requestPms = new RequestPms();
		requestPms.setId(13);
		requestPms.setApiName("检查点设置");
		requestPms.setSort(1);
		requestPms.setType(2);
		requestPms.setUrl("none");
		requestPms.setWaitVuserNum(1000);
		return requestPms;
	}

	private RequestPms getGetReq() {
		RequestPms requestPms = new RequestPms();
		requestPms.setId(12);
		requestPms.setApiName("首页获取");
		requestPms.setSort(0);
		requestPms.setType(1);
		requestPms.setMethod("GET");
		requestPms.setUrl("http://192.168.0.11:8080/home");

		List<NVPair> headers = new ArrayList<>();
		headers.add(new NVPair("Content-Type", "application/x-www-form-urlencoded"));

		requestPms.setHeaderList(headers);
		requestPms.setContentType("application/x-www-form-urlencoded");
		requestPms.setBody(null);
		requestPms.setParamList(null);

		List<OutParams> outParamsList = new ArrayList<>();
//		requestPms.setOutParamsList(outParamsList);
		OutParams outParams = new OutParams();
		outParams.setName("status");
		outParams.setIndex(0);
		outParams.setSource(1);
		outParams.setResolveExpress("status");
		outParamsList.add(outParams);

		List<Assertion> assertionList = new ArrayList<>();
//		requestPms.setAssertionList(assertionList);
		Assertion assertion = new Assertion();
		assertion.setType(1);
		assertion.setName("code");
		assertion.setFactor(">");
		assertion.setContent("0");
		assertionList.add(assertion);

		return requestPms;
	}
}
