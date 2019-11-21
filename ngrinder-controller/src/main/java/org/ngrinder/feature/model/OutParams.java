package org.ngrinder.feature.model;

/**
 * 出参设置
 */
public class OutParams {
	private String name;//出参名
	private int source;//参数来源，包括0:body text；1：body json；2：header；3、cookie；4、响应码等类型；
	private String resolveExpress;//解析表达式,暂时仅支持字符串，未来支持正则
	private int index;//取第几个匹配项

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public String getResolveExpress() {
		return resolveExpress;
	}

	public void setResolveExpress(String resolveExpress) {
		this.resolveExpress = resolveExpress;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
