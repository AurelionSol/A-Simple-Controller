package sc.ustc.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import sc.ustc.entity.ClassPropertyBean;
import sc.ustc.entity.MappingBean;

public class Configuration {
	private static Configuration configuration;
	private String driver_class;
	private String url_path;
	private String db_username;
	private String db_userpassword;
	private List<MappingBean> mappingList;
	private List<ClassPropertyBean> propertyList;
	String xmlPath = "C://Users//AloneStar//workspace//303//src//or_mapping.xml";

	public static Configuration getConfiguration() {
		Configuration instance = null;
		if (null == configuration) {
			instance = new Configuration();
		} else {
			instance = configuration;
		}
		return instance;
	}

	Configuration() {
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(xmlPath));
			Element root = document.getRootElement();
			// 解析JDBC配置
			Element jdbc = root.element("JDBC");
			List<Element> jdbcProerty = jdbc.elements();
			for (Element jdbcPElement : jdbcProerty) {
				String name = jdbcPElement.element("name").getStringValue();
				String value = jdbcPElement.element("value").getStringValue();
				if ("driver_class".equals(name)) {
					driver_class = value;
				}
				if ("url_path".equals(name)) {
					url_path = value;
				}
				if ("db_username".equals(name)) {
					db_username = value;
				}
				if ("db_userpassword".equals(name)) {
					db_userpassword = value;
				}
			}
			// 解析MappingClasss
			List<Element> mappingEList = root.elements("class");
			mappingList=new ArrayList<MappingBean>();
			for (Element mElement : mappingEList) {
				MappingBean mBean = new MappingBean();
				Element cName = mElement.element("name");
				Element cTable = mElement.element("table");
				Element cId = mElement.element("id");
				mBean.setName(cName.getStringValue());
				mBean.setTable(cTable.getStringValue());
				mBean.setId(cId.element("name").getStringValue());
				
				propertyList = new ArrayList<ClassPropertyBean>();
				List<Element> propertyEList = mElement.elements("proerty");
				for (Element element : propertyEList) {
					ClassPropertyBean cpBean = new ClassPropertyBean();
					cpBean.setName(element.element("name").getStringValue());
					cpBean.setColumn(element.element("column").getStringValue());
					cpBean.setType(element.element("type").getStringValue());
					String lazy = element.element("lazy").getStringValue();
					if ("true".equals(lazy)) {
						cpBean.setLazy(true);
					}else {
						cpBean.setLazy(false);
					}
					propertyList.add(cpBean);
				}
				mBean.setPropertyList(propertyList);
				mappingList.add(mBean);
			}
			
			System.out.println(driver_class+"  "+url_path+"  "+db_username+"  "+db_userpassword+"  ");
			
		} catch (DocumentException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	public String getDriver_class() {
		return driver_class;
	}

	public void setDriver_class(String driver_class) {
		this.driver_class = driver_class;
	}

	public String getUrl_path() {
		return url_path;
	}

	public void setUrl_path(String url_path) {
		this.url_path = url_path;
	}

	public String getDb_username() {
		return db_username;
	}

	public void setDb_username(String db_username) {
		this.db_username = db_username;
	}

	public String getDb_userpassword() {
		return db_userpassword;
	}

	public void setDb_userpassword(String db_userpassword) {
		this.db_userpassword = db_userpassword;
	}

	public List<MappingBean> getMappingList() {
		return mappingList;
	}

	public void setMappingList(List<MappingBean> mappingList) {
		this.mappingList = mappingList;
	}

	public List<ClassPropertyBean> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(List<ClassPropertyBean> propertyList) {
		this.propertyList = propertyList;
	}
}
