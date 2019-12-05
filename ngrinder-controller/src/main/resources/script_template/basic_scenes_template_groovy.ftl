import static net.grinder.script.Grinder.grinder
import static org.junit.Assert.*
import static org.hamcrest.Matchers.*
import net.grinder.plugin.http.HTTPRequest
import net.grinder.plugin.http.HTTPPluginControl
import net.grinder.script.GTest
import net.grinder.script.Grinder
import net.grinder.scriptengine.groovy.junit.GrinderRunner
import net.grinder.scriptengine.groovy.junit.annotation.BeforeProcess
import net.grinder.scriptengine.groovy.junit.annotation.BeforeThread
import net.grinder.scriptengine.groovy.junit.annotation.AfterProcess
import net.grinder.scriptengine.groovy.junit.annotation.AfterThread
import static net.grinder.util.GrinderUtils.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import java.util.Date
import java.util.List
import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.CopyOnWriteArrayList

import HTTPClient.Cookie
import HTTPClient.CookieModule
import HTTPClient.HTTPResponse
import HTTPClient.NVPair

import static org.junit.Assert.assertTrue
import org.ngrinder.recorder.RecorderUtils
import org.json.JSONArray
import groovy.json.JsonOutput
import redis.clients.jedis.Jedis;

/**
 * A scenes test
 *
 * This script is automatically generated by ngrinder.
 *
 * @author ${userName}
 */
@RunWith(GrinderRunner)
class TestRunner {

	public static GTest test
	public static HTTPRequest request
	public static ConcurrentMap<String,Object> dataMap = new ConcurrentHashMap<>()

	public ConcurrentMap<String,Object> map = new ConcurrentHashMap<>()
	public Cookie[] cookies = []

	//请求信息和响应结果
	public static List<ConcurrentMap<String,Object>> processList = new ArrayList<>()
	public ConcurrentMap<String,Object> samplingMap = new ConcurrentHashMap<>()
	public List<Map<String,Object>> samplingList = new CopyOnWriteArrayList<>()

	public Jedis jedis;
	public static String VUSERNUM_KEY = "vuser_num_redis_key"

	//初始化csv数据
	public static void loadData(String paramStr){
		def json_object = RecorderUtils.parseRequestToJson(paramStr)
		String path = json_object.getString("path")
		int hasHead = json_object.getInt("hasHead")
		JSONArray params = json_object.getJSONArray("paramsList")
		File file = new File(path)
		assertTrue(file.exists())
		//声明n个list存放解析结果
		Map<String,List<String>> rstMap = new HashMap<>()
		for(int i=0; i< params.length();i++){
			rstMap.put(i as String,new ArrayList<String>())
		}
		file.eachLine("UTF-8", 1) { line,num ->
			if(hasHead != 1 || num != 1){
				String[] str = line.split(',');
				for(int i=0; i< str.length;i++){
					rstMap.get(i as String).add(str[i].replaceAll('"',''))
				}
			}
		}
		for(int i=0;i<params.length();i++){
			def vue = rstMap.get(params.getJSONObject(i).getString("value"))
			if (vue!=null && vue.size() == 1){
				vue = vue.get(0)
			}
			dataMap.put(params.getJSONObject(i).getString("name"), vue)
		}
	}

	@BeforeProcess
	public static void beforeProcess() {
		processList = new ArrayList<>()
		HTTPPluginControl.getConnectionDefaults().timeout = 6000
		test = new GTest(1, "${name}")
		request = new HTTPRequest()
		grinder.logger.info("before process.");

		//读取参数文件
		<#if fileDataList?? && fileDataList?size != 0>
            <#list fileDataList as fileData>
		this.loadData('${fileData}')
			</#list>
		</#if>

		VUSERNUM_KEY += grinder.getProperties().get("grinder.test.id").toString()
		jedis = new Jedis("${redisHost}", ${redisPort})
		<#if redisPassword??>
			jedis.auth("${redisPassword}")
		</#if>
	}

	@BeforeThread
	public void beforeThread() {
		test.record(this, "test")
		grinder.statistics.delayReports=true;
		grinder.logger.info("before thread.");
	}

	@Before
	public void before() {
		samplingMap = new ConcurrentHashMap<>()
		samplingList = new CopyOnWriteArrayList<>()
		map.putAll(dataMap)
		// reset to the all cookies
		def threadContext = HTTPPluginControl.getThreadHTTPClientContext()
		cookies = CookieModule.listAllCookies(threadContext)
		cookies.each {
			CookieModule.removeCookie(it, threadContext)
		}

		//如果存在登陆，则先进行登陆操作获取cookies
        <#if list?? && list?size != 0>
			<#list list as reqPms>
				<#if reqPms.type == 0 && reqPms.funName??>
        ${reqPms.funName}()
				</#if>
			</#list>
        </#if>
		cookies = CookieModule.listAllCookies(threadContext)

		cookies.each { CookieModule.addCookie(it, HTTPPluginControl.getThreadHTTPClientContext()) }
		grinder.logger.info("before thread. init headers and cookies");
	}

	@Test
	public void test(){
		//调用生成的函数
        <#if list?? && list?size != 0>
            <#list list as reqPms>
				<#if reqPms.funName?? && reqPms.type != 0>
		${reqPms.funName}()
				</#if>
            </#list>
        </#if>
		samplingMap.put("pftestId", grinder.getProperties().get("grinder.test.id").toString())
		samplingMap.put("sampling", samplingList)
		processList.add(samplingMap)
	}

	//循环进行请求生成
	<#if list?? && list?size != 0>
		<#list list as reqPms>
            <#include "basic_base_template_groovy.ftl"/>
		</#list>
	</#if>


	@AfterThread
	public void afterThread(){

	}

	@AfterProcess
	public static void afterProcess(){
		//取样收集，采集10%
		int size = processList.size() / 10 > 0 ? processList.size() / 10 : 1
		List<ConcurrentMap<String, Object>> tmpList = new ArrayList<>()
		for(int i=0;i< processList.size();i++){
			reservoirSampling(tmpList, processList.get(i),size)
		}

		def jsonOutput = new JsonOutput()
		String json = jsonOutput.toJson(tmpList);
		//发送请求
		HTTPRequest requestSamp = new HTTPRequest()
		NVPair[] headers = [
			new NVPair("Content-Type", "application/json")
		]

		String sampUrl = "${samplingUrl}"

		request.POST(sampUrl, json.bytes, headers)
	}

	private static void reservoirSampling(List samples, ConcurrentMap<String, Object> sample, int num) {
		if (samples.size() < num) {
			//如果集合数量小于样本数量
			samples.add(sample);
		} else {
			Random random = new Random();
			int i = random.nextInt(num);
			if (i < num) {
				//直接用当前样本替换掉之前样本库中的索引为i的样本
				samples.set(i, sample);
			}
		}
	}
}
