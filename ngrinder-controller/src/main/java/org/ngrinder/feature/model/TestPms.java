package org.ngrinder.feature.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建压测
 */
public class TestPms {
	private String name;//场景名
	private List<RequestPms> requestPmsList;//请求
	private List<FileData> fileDataList;//数据源

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

	public List<FileData> getFileDataList() {
		return fileDataList;
	}

	public void setFileDataList(List<FileData> fileDataList) {
		this.fileDataList = fileDataList;
	}
}
