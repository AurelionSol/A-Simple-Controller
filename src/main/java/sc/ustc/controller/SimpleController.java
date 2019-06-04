package sc.ustc.controller;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.util.RegexpPatternMapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import sc.ustc.dao.Configuration;
import sc.ustc.entity.ActionBean;
import sc.ustc.entity.DependentBean;
import sc.ustc.entity.ResultBean;
import sc.ustc.entity.fieldBean;

import water.ustc.interceptor.LogInterceptor;

public class SimpleController extends HttpServlet {
	private static HttpServletRequest httpServletRequest;
	private static HttpServletResponse httpServletResponse;

	private ArrayList<ActionBean> actionBeanList = new ArrayList<ActionBean>();
	private ArrayList<ResultBean> resultBeanList = new ArrayList<ResultBean>();
	private List<Element> actions;
	// 是否有对应的请求
	private boolean isMatch;
	// 是否有对应的响应
	private boolean isResp;
	// 是否配置拦截器
	private boolean isIntercept;
	// 是否依赖注入
	private List<DependentBean> DependentBeans;

	private LogInterceptor proxyInterceptor;

	private Configuration configuration;

	public void init() {
		isMatch = false;
		isResp = false;
		isIntercept = false;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO 自动生成的方法存根
		httpServletRequest = req;
		httpServletResponse = resp;
		doPost(req, resp);
		return;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO 自动生成的方法存根
		// 初始化
		httpServletRequest = req;
		httpServletResponse = resp;
		init();
		proxyInterceptor = new LogInterceptor();

		DependentBeans = DIXmlParse.getDependentBeanList();
		// 获取action名称
		System.out.println("开始了~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		String path = req.getServletPath();
		String method = req.getParameter("method");
		System.out.println(method);
		String actionPath = path.replace("/", "").replace(".", "").replace("sc", "").trim();
		System.out.println(actionPath);
		// interceptor.preAction(actionPath);
		// 解析XML
		String xmlPath = "C://Users//AloneStar//workspace//303//src//controller.xml";

		System.out.println(xmlPath);
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(xmlPath));
			Element root = document.getRootElement();
			// 获取所有action节点
			for (Iterator<Element> iterator = root.elementIterator(); iterator.hasNext();) {
				Element element = iterator.next();
				actions = element.elements();
				for (Element e : actions) {
					// 遍历所有action节点将其放在实体类中
					List<Node> attrs = e.attributes();
					ActionBean actionBean = new ActionBean();
					for (int j = 0; j < attrs.size(); j++) {
						Node actionNode = attrs.get(j);
						String name = actionNode.getName();
						String value = actionNode.getStringValue();
						// 获取属性名
						System.out.print("属性名：" + actionNode.getName());
						// 获取属性值
						System.out.println("--属性值" + actionNode.getStringValue());
						if ("name".equals(name)) {
							actionBean.setName(value);
						}
						if ("class".equals(name)) {
							actionBean.setClassName(value);
						}
						if ("method".equals(name)) {
							actionBean.setMethod(value);
						}
					}
					actionBeanList.add(actionBean);
				}
			}

			for (ActionBean actionBean : actionBeanList) {
				// 解析获得的action实体类集合找出符合请求的并处理
				if (actionPath.equals(actionBean.getName())) {
					isMatch = true;
				}
				for (Element e : actions) {
					if (e.attributeValue("name").equals(actionBean.getName())) {
						List<Element> actionChildElements = e.elements();
						for (Element re : actionChildElements) {
							// 是否存在拦截器
							if (re.getName().equals("ibterceptro-ref")) {
								isIntercept = true;
								// proxyInterceptor.preAction(actionPath);
								continue;
							}
							// 解析获得resultBean并放在resultBeanList
							List<Node> attrs = re.attributes();
							ResultBean resultBean = new ResultBean();
							for (int j = 0; j < attrs.size(); j++) {
								Node resultNode = attrs.get(j);
								String name = resultNode.getName();
								String value = resultNode.getStringValue();
								// 获取属性名
								System.out.print("属性名：" + resultNode.getName());
								// 获取属性值
								System.out.println("--属性值" + resultNode.getStringValue());
								if ("name".equals(name)) {
									resultBean.setName(value);
								}
								if ("type".equals(name)) {
									resultBean.setType(value);
								}
								if ("value".equals(name)) {
									resultBean.setValue(value);
								}
							}
							resultBeanList.add(resultBean);
						}
						// 执行对应action方法
						// Class clz =
						// Class.forName(actionBean.getClassName());
						// Object o = clz.newInstance();
						// Method met =
						// clz.getMethod(actionBean.getMethod());
						// String result = (String) met.invoke(o);
						Class clz = Class.forName(actionBean.getClassName());
						Object o = isIntercept ? proxyInterceptor.getProxy(clz) : clz.newInstance();
						for (DependentBean dependentBean : DependentBeans) {
							if (dependentBean.getId().equals(actionPath)) {
								// 内省获得属性、公开的方法
								BeanInfo beanInfo = Introspector.getBeanInfo(clz,Object.class);
								PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
								// 获得解析的fieldbean，遍历查找匹配并实例化
								List<fieldBean> fBeans = dependentBean.getFieldList();
								for (fieldBean fieldBean : fBeans) {
									String fieldName = fieldBean.getName();
									for (PropertyDescriptor proDescriptor : proDescrtptors) {
										if (proDescriptor.getName().equals(fieldName)) {
											// 实例化被依赖的bean对象
											Class proClz = proDescriptor.getPropertyType();
											Object proObj = proClz.newInstance();
											BeanInfo proInfo = Introspector.getBeanInfo(proClz,Object.class);
											PropertyDescriptor[] proProDescrtptors = proInfo.getPropertyDescriptors();
											for (PropertyDescriptor proProDescriptor : proProDescrtptors) {
												if (proProDescriptor.getName().equals("userName")) {
													Method methodSetUserName = proProDescriptor.getWriteMethod();
													methodSetUserName.invoke(proObj, req.getParameter("userName"));
												}
												if (proProDescriptor.getName().equals("userPassword")) {
													Method methodSetPassWord = proProDescriptor.getWriteMethod();
													methodSetPassWord.invoke(proObj, req.getParameter("userPassword"));
												}
											}
											if (proDescriptor.getPropertyType().equals(proClz)) {
												Method methodSetUserBean = proDescriptor.getWriteMethod();
												methodSetUserBean.invoke(o, proObj);
											}
										
										}
									}
								}
							}
						}
						Object[] arg = { (Object) actionPath };
						Method met = clz.getMethod(actionBean.getMethod(), String.class);
						String result = (String) met.invoke(o, arg);
						// 根据返回结果响应对应的result
						for (ResultBean resultBean : resultBeanList) {
							if (resultBean.getName().equals(result)) {
								// proxyInterceptor.afterAction(resultBean.getName());
								isResp = true;
								String resultPage;
								if (resultBean.getName().equals("success")) {
									resultPage = resultBean.getValue().replaceAll("/", "\\\\");
									String resXmlPath = getServletConfig().getServletContext().getRealPath("/")
											+ resultPage;
									OutputStream out = resp.getOutputStream();
									TransformerFactory tFactory = TransformerFactory.newInstance();
									Source xslDoc = new StreamSource(resXmlPath.replaceAll(".xml", ".xsl"));
									Source xmlDoc = new StreamSource(resXmlPath);
									Transformer trasform = tFactory.newTransformer(xslDoc);
									trasform.transform(xmlDoc, new StreamResult(out));
								} else {
									resp.setContentType("text/html;charset=UTF-8");
									resultPage = resultBean.getValue();
									resp.sendRedirect(req.getContextPath() + resultPage);
								}

								return;
							}
						}

						if (!isResp) {
							System.out.println("没有请求的资源");
							resp.setContentType("text/html;charset=UTF-8");
							resp.sendRedirect(req.getContextPath() + "/error.html");
							return;
						}
					}
				}
			}

			if (!isMatch) {
				System.out.println("不合法的请求");
				resp.setContentType("text/html;charset=UTF-8");
				resp.sendRedirect(req.getContextPath() + "/error.html");
				return;
			}

		} catch (

		DocumentException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (TransformerConfigurationException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		} catch (TransformerException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		} catch (IntrospectionException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}

		// resp.setContentType("text/html;charset=UTF-8");
		// PrintWriter writer =resp.getWriter();
		// String strHtml="<html>\n
		// <head>\n<title>SimpleController</title>\n</head>\n<body>"
		// + "欢迎使用SimpleController!</body>\n</html>";
		// System.out.println(strHtml);
		// writer.println(strHtml);
		// writer.flush();
		// writer.close();
	}

	public static HttpServletRequest getRequest() {
		return httpServletRequest;
	}

	public static HttpServletResponse getResponse() {
		return httpServletResponse;
	}
}
