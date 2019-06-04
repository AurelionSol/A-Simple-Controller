package water.ustc.interceptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import sc.ustc.entity.ActionBean;
import sc.ustc.entity.ResultBean;

public class LogInterceptor implements MethodInterceptor {
	String outPath = "C://Users//AloneStar//workspace//303//log.xml";
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
	File file;
	Document doc;
	Element root;
	Element actionEle;
	OutputFormat format;
	SAXReader reader;

	public void preAction(String action) {
		// TODO 自动生成的方法存根
		System.out.println("日志记录开始");
		// 建立xml
		file = new File(outPath);
		if (!file.exists()) {
			try {
				file.createNewFile();
				doc = DocumentHelper.createDocument();
				root = doc.addElement("log");
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		} else {
			reader = new SAXReader();
			try {
				doc = reader.read(file);
			} catch (DocumentException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8");
		format.setNewlines(true);
		root = doc.getRootElement();
		actionEle = root.addElement("action");
		actionEle.addElement("name").setText((String) action);
		actionEle.addElement("s-time").setText(df.format(new Date()));

	}

	public void afterAction(String result) {
		// TODO 自动生成的方法存根
		// 写入执行结果
		actionEle.addElement("e-time").setText(df.format(new Date()));
		actionEle.addElement("name").setText((String) result);
		// 生成XML文件
		try {
			FileOutputStream fos = new FileOutputStream(file, false);
			OutputStreamWriter out = new OutputStreamWriter(fos, "utf-8");
			XMLWriter writer = new XMLWriter(out, format);
			writer.write(doc);
			writer.close();
			System.out.print("生成XML文件成功");
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			System.out.print("生成XML文件失败");
		}
	}

	public Object intercept(Object obj, Method method, Object[] arg, MethodProxy proxy) throws Throwable {
		// TODO 自动生成的方法存根
		Object object = null;
		if (method.getName().equals("handleLogin")) {
			System.out.println("代理拦截器开始工作");
			// 在action执行前执行preAction
			preAction((String) arg[0]);
			// 执行action响应方法
			object = proxy.invokeSuper(obj, arg);
			// 在action执行后执行afterAction
			afterAction(object.toString());
		} else {
			object = proxy.invokeSuper(obj, arg);
		}
		// preAction((String) arg[0]);
		// //执行action响应方法
		// Object object = proxy.invokeSuper(obj, arg);
		// //在action执行后执行afterAction
		// afterAction(object.toString());
		return object;
	}

	// 获得代理对象
	public Object getProxy(Class clz) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clz);
		enhancer.setCallback(new LogInterceptor());
		Object object = enhancer.create();
		return object;
	}

}
