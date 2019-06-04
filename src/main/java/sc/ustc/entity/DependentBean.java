package sc.ustc.entity;

import java.util.List;

public class DependentBean {
	private String id;
	private String className;
	private List<fieldBean> fieldList;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public List<fieldBean> getFieldList() {
		return fieldList;
	}
	public void setFieldList(List<fieldBean> fieldList) {
		this.fieldList = fieldList;
	}
}
