package org.ngrinder.feature.service;

import HTTPClient.NVPair;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import groovy.json.JsonOutput;
import org.junit.Test;
import org.ngrinder.feature.model.*;
import org.springframework.core.io.ClassPathResource;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.ngrinder.common.util.CollectionUtils.newHashMap;
import static org.ngrinder.common.util.ExceptionUtils.processException;

public class TemplateTest {

	@Test
	public void randomTest(){
		System.out.println(UUID.randomUUID().toString().replaceAll("-",""));
	}

	@Test
	public void test() {
		TestPms testPms = new TestPms();
		testPms.setName("脚本测试生成");
		testPms.setFileDataList(this.getFileDataList());
		List<RequestPms> requestPmsList = new ArrayList<>();
		testPms.setRequestPmsList(requestPmsList);

		requestPmsList.add(this.getPostLogin());
		requestPmsList.add(this.getGetReq());

		Map<String, Object> map = newHashMap();
		map.put("userName", "admin");
		map.put("name", testPms.getName());
		map.put("list", testPms.getRequestPmsList());
		map.put("fileDataList", testPms.getFileDataList());
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

	private List<String> getFileDataList() {
		List<String> list = new ArrayList<>();

		FileData fileData = new FileData();
		fileData.setName("请求参数");
		fileData.setHasHead(0);
		fileData.setPath("resources/test/user.csv");

		List<NVPair> nvPairList = new ArrayList<>();
		NVPair nvPair = new NVPair("userId", 1 + "");
		nvPairList.add(nvPair);
		NVPair nvPair2 = new NVPair("appId", 2 + "");
		nvPairList.add(nvPair2);
		fileData.setParamsList(nvPairList);

		list.add(JsonOutput.toJson(fileData));
		return list;
	}

	private RequestPms getPostLogin() {
		RequestPms requestPms = new RequestPms();
		requestPms.setIndex(0);
		requestPms.setType(0);
		requestPms.setMethod("POST");
		requestPms.setUrl("http://www.baidu.com");

		List<NVPair> headers = new ArrayList<>();
		headers.add(new NVPair("Content-Type", "application/x-www-form-urlencoded"));

		requestPms.setHeaders(headers);
		requestPms.setContentType("application/x-www-form-urlencoded");
		requestPms.setBody(null);

		List<NVPair> params = new ArrayList<>();
		params.add(new NVPair("user_name", "wangw"));
		params.add(new NVPair("user_pwd", "admin"));
		params.add(new NVPair("user_placeholder", "${orderId}"));

		requestPms.setParams(params);

		List<OutParams> outParamsList = new ArrayList<>();
		requestPms.setOutParamsList(outParamsList);
		OutParams outParams = new OutParams();
		outParams.setName("status");
		outParams.setIndex(0);
		outParams.setSource(1);
		outParams.setResolveExpress("status");
		outParamsList.add(outParams);

		List<Assertion> assertionList = new ArrayList<>();
		requestPms.setAssertionList(assertionList);
		Assertion assertion = new Assertion();
		assertion.setType(1);
		assertion.setName("code");
		assertion.setFactor(">");
		assertion.setContent("0");
		assertionList.add(assertion);
		return requestPms;
	}

	private RequestPms getGetReq() {
		RequestPms requestPms = new RequestPms();
		requestPms.setIndex(0);
		requestPms.setType(1);
		requestPms.setMethod("GET");
		requestPms.setUrl("http://www.baidu.com");

		List<NVPair> headers = new ArrayList<>();
		headers.add(new NVPair("Content-Type", "application/x-www-form-urlencoded"));

		requestPms.setHeaders(headers);
		requestPms.setContentType("application/x-www-form-urlencoded");
		requestPms.setBody(null);
		requestPms.setParams(null);

		List<OutParams> outParamsList = new ArrayList<>();
		requestPms.setOutParamsList(outParamsList);
		OutParams outParams = new OutParams();
		outParams.setName("status");
		outParams.setIndex(0);
		outParams.setSource(1);
		outParams.setResolveExpress("status");
		outParamsList.add(outParams);

		List<Assertion> assertionList = new ArrayList<>();
		requestPms.setAssertionList(assertionList);
		Assertion assertion = new Assertion();
		assertion.setType(1);
		assertion.setName("code");
		assertion.setFactor(">");
		assertion.setContent("0");
		assertionList.add(assertion);

		return requestPms;
	}
}
