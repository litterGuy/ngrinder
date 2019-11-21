package org.ngrinder.feature.model;

import java.util.List;

/**
 * 创建压测
 */
public class TestPms {
	private String name;//场景名
	private List<RequestPms> requestPmsList;//请求
	private List<String> fileDataList;//数据源

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RequestPms> getRequestPmsList() {
		return requestPmsList;
	}

	public void setRequestPmsList(List<RequestPms> requestPmsList) {
		this.requestPmsList = requestPmsList;
	}

	public List<String> getFileDataList() {
		return fileDataList;
	}

	public void setFileDataList(List<String> fileDataList) {
		this.fileDataList = fileDataList;
	}
}
