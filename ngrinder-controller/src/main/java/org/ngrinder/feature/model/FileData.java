package org.ngrinder.feature.model;

import HTTPClient.NVPair;

import java.util.List;

public class FileData {

	private String name;//文件名称
	private String path;//文件路径
	private List<NVPair> paramsList;//参数列表，key为参数名称、value为对应的列
	private int hasHead;//文件上否有标题头，0：没有；1：有；

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<NVPair> getParamsList() {
		return paramsList;
	}

	public void setParamsList(List<NVPair> paramsList) {
		this.paramsList = paramsList;
	}

	public int getHasHead() {
		return hasHead;
	}

	public void setHasHead(int hasHead) {
		this.hasHead = hasHead;
	}
}
