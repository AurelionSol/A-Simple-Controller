package sc.ustc.entity;

import java.util.List;

public class MappingBean {
	private String name;
	private String table;
	private String id;
	private List<ClassPropertyBean> propertyList;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String type) {
		this.table = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<ClassPropertyBean> getPropertyList() {
		return propertyList;
	}
	public void setPropertyList(List<ClassPropertyBean> propertyList) {
		this.propertyList = propertyList;
	}
}
