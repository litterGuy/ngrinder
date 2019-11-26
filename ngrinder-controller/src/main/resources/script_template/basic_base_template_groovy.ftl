	<#if reqPms.funName??>
	public void ${reqPms.funName}(){
	</#if>
		//设置header
		NVPair[] headers = []
	<#if reqPms.headers?? && reqPms.headers?size != 0>
		List<NVPair> headerList = new ArrayList<NVPair>()
		<#list reqPms.headers as header>
		headerList.add(new NVPair("${header["name"]?j_string}", "${header["value"]?j_string?replace("$", "\\$")}"))
		</#list>
		headers = headerList.toArray()
	</#if>
		request.setHeaders(headers)
		HTTPResponse result;
		//设置参数
		def sampReqPams = ""
		def jsonOutput = new JsonOutput()
	<#if reqPms.method == 'GET'>
		result = request.GET("${reqPms.url}")
	<#elseif reqPms.method == 'POST'>
		<#if reqPms.params?? && reqPms.params?size != 0>
		NVPair[] params = []
		List<NVPair> paramList = new ArrayList<NVPair>()
			<#list reqPms.params as param>
		def pValue_${param.name} = "${param.value?j_string?replace("$", "\\$")}"
		//检测参数中是否含有占位符，如果有从出参集合中获取值
		if(pValue_${param.name} ==~ /^\$\{.*\}$/){
			def keyTmp = pValue_${param.name}.replace('$','').replace('{', "").replace('}', "")
			if(map.containsKey(keyTmp)){
				//判断参数类型，如果是list类型、则随机取一条数据，不是则直接赋值
				def tmp = map.get(keyTmp)
				if (tmp instanceof ArrayList){
					pValue_${param.name} = tmp.get(new Random().nextInt(tmp.size()))
				}else{
					pValue_${param.name} = tmp
				}
			}
		}
		paramList.add(new NVPair("${param["name"]?j_string}", pValue_${param.name}.replace('$',"\$")))
			</#list>
		params = paramList.toArray()
		</#if>
		<#if reqPms["body"]??>
			<#assign body = reqPms["body"]>
		</#if>
		String body = <#if body??>"${body?j_string?replace("$", "\\$")}"<#else>null</#if>
		<#if body??>
		sampReqPams = body
		<#else>
		sampReqPams = jsonOutput.toJson(params)
		</#if>
		result = request.POST("${reqPms.url}", <#if body??>body.getBytes()<#else>params</#if>)
	<#elseif reqPms.method == 'PUT'>
		<#if reqPms.params?? && reqPms.params?size != 0>
		NVPair[] params = []
		List<NVPair> paramList = new ArrayList<NVPair>()
			<#list reqPms.params as param>
		def pValue_${param.name} = "${param.value?j_string?replace("$", "\\$")}"
		//检测参数中是否含有占位符，如果有从出参集合中获取值
		if(pValue_${param.name} ==~ /^\$\{.*\}$/){
			def keyTmp = pValue_${param.name}.replace('$','').replace('{', "").replace('}', "")
			if(map.containsKey(keyTmp)){
				//判断参数类型，如果是list类型、则随机取一条数据，不是则直接赋值
				def tmp = map.get(keyTmp)
				if (tmp instanceof ArrayList){
					pValue_${param.name} = tmp.get(new Random().nextInt(tmp.size()))
				}else{
					pValue_${param.name} = tmp
				}
			}
		}
		paramList.add(new NVPair("${param["name"]?j_string}", pValue_${param.name}.replace("$","\$")))
			</#list>
		params = paramList.toArray()
		String json = jsonOutput.toJson(params);
		</#if>
		<#if reqPms["body"]??>
			<#assign body = reqPms["body"]>
		</#if>
		String body = <#if body??>"${body?j_string?replace("$", "\\$")}"<#else>null</#if>

		<#if body??>
			sampReqPams = body
		<#else>
			sampReqPams = jsonOutput.toJson(params)
		</#if>

		result = request.PUT("${reqPms.url}", <#if body??>body.getBytes()<#else>json.getBytes()</#if>)
    <#elseif reqPms.method == 'DELETE'>
		result = request.DELETE("${reqPms.url}")
	</#if>
		grinder.logger.info("----{}----", request.headers)
		//输出请求返回值
		grinder.logger.info("----{}----", result.getText())//返回的文本
		grinder.logger.info("----{}----", result.getStatusCode())//返回的状态码
		grinder.logger.info("----{}----", result.getEffectiveURI())//返回的url
		grinder.logger.info("---{}---", result)//返回的请求头所有参数
		//获取出参
	<#if reqPms.outParamsList?? && reqPms.outParamsList?size != 0>
		<#list reqPms.outParamsList as outParams>
			<#if outParams.source == 0>
		//body text
		def matchers = 	result.data =~ 	/${outParams.resolveExpress }/
		if(matchers!=null && matchers.length>0 && matches.length >= ${outParams.index }){
			map.put("${outParams.name }",matchers[${outParams.index }]);
		}

			<#elseif outParams.source == 1>
		//body json
		def returnData = RecorderUtils.parseRequestToJson(new String(result.data))
		String out = returnData.getString("${outParams.resolveExpress }")
		map.put("${outParams.name }",out);

			<#elseif outParams.source == 2>
		//header
		NVPair[] headers = request.headers
		for(int i=0; i < headers.size(); i++){
			if(headers[i].name == ${outParams.resolveExpress }){
				map.put("${outParams.name }",headers[i].value);
				break;
			}
		}

			<#elseif outParams.source == 3>
		//cookie
		cookies = CookieModule.listAllCookies(threadContext)
		for(int i=0; i< cookies.size();i++){
			if(cookies[i].name == ${outParams.resolveExpress }){
				map.put("${outParams.name }",cookies[i].value);
				break;
			}
		}
			<#elseif outParams.source == 4>
		//响应码
		map.put("${outParams.name }",request.statusCode);
			</#if>
		</#list>
	</#if>

		//TODO 随机取样算法，决定本次是否收集请求信息进行发送，暂时按照取模10（10取1的方式）
		int cur_num = getThreadUniqId()
		if((cur_num-1) % 10 == 0){
			Map<String,Object> sampMap = new HashMap<>()
			sampMap.put("func","${reqPms.apiName}")
			sampMap.put("http_req_url","${reqPms.url}")
			sampMap.put("http_req_method","${reqPms.method}")
			sampMap.put("http_res_status", result.statusCode)
			sampMap.put("http_req_headers", request.headers)
			sampMap.put("http_req_body", sampReqPams)

			Map<String, Object> resHeaderMap = new HashMap<>();
			def resHeaders = result.listHeaders()
			while (resHeaders.hasMoreElements()) {
				String hdr = (String) resHeaders.nextElement()
				resHeaderMap.put(hdr, result.getHeader(hdr))
			}
			sampMap.put("http_res_headers", resHeaderMap)
			sampMap.put("http_res_body", result.text)
			sampMap.put("timestamp", System.currentTimeMillis())
			//获取以下参数
			Map<String, Object> outPamsMap = new HashMap<>();
			<#if reqPms.outParamsList?? && reqPms.outParamsList?size != 0>
				<#list reqPms.outParamsList as outParams>
			outPamsMap.put("${outParams.name }",map.get("${outParams.name }"))
				</#list>
			</#if>
			sampMap.put("export_content", outPamsMap)

			List<Map<String,Object>> assertList = new ArrayList<>()
			<#if reqPms.assertionList?? && reqPms.assertionList?size != 0>
			Map<String, Object> assertMap = new HashMap<>();
				<#list reqPms.assertionList as assertion>
			assertMap = new HashMap<>();
			assertMap.put("name","${assertion.name }")
			assertMap.put("factor","${assertion.factor}")
			assertMap.put("content","${assertion.content}")
			assertMap.put("type","${assertion.type}")
			assertList.add(assertMap)
				</#list>
			</#if>
			sampMap.put("check_result", assertList)
			sampMap.put("agent", grinder.getProperties().get("grinder.consoleHost").toString()+":"+grinder.getProperties().get("grinder.consolePort").toString())
			samplingList.add(sampMap)
		}

		//检测断言
	<#if reqPms.assertionList?? && reqPms.assertionList?size != 0>
		<#list reqPms.assertionList as assertion>
			<#if assertion.type == 0>
		//header
		NVPair[] headers = request.headers
		for(int i=0; i < headers.size(); i++){
			if(headers[i].name == ${assertion.name }){
				assertTrue(headers[i].value ${assertion.factor} ${assertion.content})
				break;
			}
		}

			<#elseif assertion.type == 1>
		//响应码
		assertTrue(result.statusCode ${assertion.factor} ${assertion.content})

			<#elseif assertion.type == 2>
		//body
		assertTrue(new String(result.data) ==~ 	/${assertion.content }/)

			<#elseif assertion.type == 3>
		//出参
		if(map.containsKey("${assertion.name }")){
			assertTrue(map.get("${assertion.name }") ${assertion.factor} ${assertion.content})
		}
			</#if>
		</#list>
	</#if>

		if (result.statusCode == 301 || result.statusCode == 302) {
			grinder.logger.warn("Warning. The response may not be correct. The response code was {}.", result.statusCode);
		} else {
			assertThat(result.statusCode, is(200));
		}

    <#if reqPms.funName??>
	}
	</#if>
