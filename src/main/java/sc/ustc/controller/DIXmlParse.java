package sc.ustc.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import sc.ustc.entity.DependentBean;
import sc.ustc.entity.fieldBean;

public class DIXmlParse {
	private static List<DependentBean> list;
	private static String DIxmlPath = "C://Users//AloneStar//workspace//303//src//di.xml";
	public static List<DependentBean> getDependentBeanList() {
		list=new ArrayList<DependentBean>();
		try {
			SAXReader reader = new SAXReader();
			Document document;
			document = reader.read(new File(DIxmlPath));
			Element root = document.getRootElement();
			List<Element> beanElements = root.elements("bean");
			for (Element element : beanElements) {
				DependentBean dBean=new DependentBean();
				String id = element.attributeValue("id");
				String className = element.attributeValue("class");
				dBean.setId(id);
				dBean.setClassName(className);
				List<fieldBean> fieldList=new ArrayList<fieldBean>();
				List<Element> fieldElements=element.elements("field");
				for (Element fElement : fieldElements) {
					fieldBean fBean=new fieldBean();
					String name = fElement.attributeValue("name");
					String bean_ref = fElement.attributeValue("bean-ref");
					fBean.setName(name);
					fBean.setBean_ref(bean_ref);
					fieldList.add(fBean);
				}
				dBean.setFieldList(fieldList);
				list.add(dBean);
			}
		} catch (DocumentException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		
		return list;
	}
}
