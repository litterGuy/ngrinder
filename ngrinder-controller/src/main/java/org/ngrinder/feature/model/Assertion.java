package org.ngrinder.feature.model;

/**
 * 检查点
 */
public class Assertion {
	private int type;//检查点类型,包括0：header；1：响应码；2：body；3：出参；
	private String name;//检查对象，相对类型的参数名称
	private String factor;//检查条件，大于、等于、小于
	private String content;//检查内容，仅支持文字,不支持数组、区间

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFactor() {
		return factor;
	}

	public void setFactor(String factor) {
		this.factor = factor;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
