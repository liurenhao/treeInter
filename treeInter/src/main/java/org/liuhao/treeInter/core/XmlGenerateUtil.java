package org.liuhao.treeInter.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sinosoft.iplatform.proxy.Performance;
import com.sinosoft.iplatform.xml.domain.Action;
import com.sinosoft.iplatform.xml.domain.Packet;
import com.sinosoft.iplatform.xml.domain.PlatUrl;
import com.sinosoft.iplatform.xml.domain.SubService;
import com.sinosoft.iplatform.xml.domain.XMLModel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XmlGenerateUtil {
	
	final static String path = XmlGenerateUtil.class.getResource("/platform").getPath();
	public static Map<String,Object> listAction = new HashMap<String, Object>();
	public static Map<String,Object> listXML = new HashMap<String, Object>();
	public static Map<String,Object> listServices = new HashMap<String, Object>();
	public static Map<String,Object> listUrls = new HashMap<String, Object>();
	Map<String,Object> listCache = new HashMap<String, Object>();
	
	String modelName ="";
	
	public XmlGenerateUtil(){
		try {
			if(listXML.isEmpty() || listUrls.isEmpty()){
				System.out.println("【XML映射加载配置�?...�?");
				loadActionConfig();
				System.out.println("【XML映射加载配置成功！�??");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("【XML映射加载配置失败！�??");
		}
	}
	
	/**
	 * 加载配置到内存中     Action对象   XML模板
	 * @param listAction
	 * @throws Exception 
	 */
	private static void loadActionConfig() throws Exception{
		File file = new File(path+"/IPlatFormConfig.xml");
		BufferedReader reader = null;
		StringBuffer buffer = new StringBuffer();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			String tempStr = "";
			while((tempStr=reader.readLine())!=null){
				buffer.append(tempStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("平台基础配置模板（IPlatFormConfig.xml）不存在或存在错�?");
		} finally {
			reader.close();  
		} 
		String xml = buffer.toString();
		Packet packet =(Packet)parseXmlToObject(Packet.class, xml);
		List<Action> actions = packet.getActionList().getAction();
		List<XMLModel> models = packet.getXmlModelList().getXmlModel();
		List<SubService> subServices = packet.getSubServiceList().getSubServices();
		List<PlatUrl> platUrls = packet.getPlatUrlList().getPlatUrls();
		//清空缓存
		listAction.clear();
		listXML.clear();
		listServices.clear();
		listUrls.clear();
		//加载action配置
		if(actions!=null){
			for (Action action : actions) {
				Class cls = Class.forName(action.getType());
				Object objAction = cls.newInstance();
				listAction.put(action.getName(), objAction);
			}
		}
		//加载XML模板
		if(models!=null){
			for (XMLModel xmlModel : models) {
				file = new File(path+"/"+xmlModel.getPath());
				String charset = xmlModel.getCharset();
				if(StringUtils.isBlank(charset)){
					charset = "UTF-8";
				}
				reader = null;
				buffer = new StringBuffer();
				try {
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),charset));
					String tempStr = "";
					while((tempStr=reader.readLine())!=null){
						buffer.append(tempStr);
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("报文模板解析失败�?"+path+"/"+xmlModel.getPath()+"不存在或存在错误");
				} finally {  
					reader.close();  
				} 
				String xmlTemp = buffer.toString();
				Document docModel = null;
				System.out.println("["+xmlModel.getName()+"]"+xmlTemp);
				try {
					docModel = DocumentHelper.parseText(xmlTemp);
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("报文加载失败�?"+path+"/"+xmlModel.getPath()+"有误");
				}
				
				listXML.put(xmlModel.getName(), docModel);
				file = new File(path+"/"+xmlModel.getBackpath());
				reader = null;
				buffer = new StringBuffer();
				try {
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),charset));
					String tempStr = "";
					while((tempStr=reader.readLine())!=null){
						buffer.append(tempStr);
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("报文模板解析失败�?"+path+"/"+xmlModel.getPath()+"不存在或存在错误");
				} finally {  
					reader.close();  
				} 
				xmlTemp = buffer.toString();
				docModel = DocumentHelper.parseText(xmlTemp);
				listXML.put(xmlModel.getName()+"_back", docModel);
			}
		}
		//加载XML模板
		if(subServices!=null){
			for (SubService subService : subServices) {
				listServices.put(subService.getKey(),subService.getPath());
			}
		}
		//加载XML模板
		if(platUrls!=null){
			for (PlatUrl platUrl : platUrls) {
				listUrls.put(platUrl.getCi_key(),platUrl);
			}
		}
	}
	
	
	/**
	 * 将XML解析为对�?
	 * @return
	 * @throws Exception
	 */
	public static Object parseXmlToObject(Class respObj,String responseXml) throws Exception{
		Object xmlObject = null;
		if(responseXml!=null && !"".equals(responseXml)){
			try {
				XStream xsStream = new XStream(new DomDriver());
				xsStream.processAnnotations(respObj);
				xmlObject = xsStream.fromXML(responseXml);
			}catch(Exception e){
				e.printStackTrace();
				//处理解析失败，然后封装对象返回解析失败信�?
				throw new Exception("报文解析失败�?"+e.getMessage());
			}
		}
		return xmlObject;
	}
	
	
	/**
	 * 根据文档模板生成XML内容文档
	 * @param obj
	 * @param xmlType
	 * @return
	 * @throws Exception
	 */
	@Performance(methd="生成模板")
	public String generateXMLByModel(Object obj,String xmlType,String urlKey) throws Exception,Throwable{
		modelName = xmlType;
		PlatUrl platUrl = (PlatUrl)listUrls.get(urlKey);
		Document document = (Document) listXML.get(xmlType);
		Element rootElement = document.getRootElement();
		Document docNew = DocumentHelper.createDocument();
		docNew.setXMLEncoding(platUrl.getCi_charset());//指定编码格式
//		Element root = docNew.addElement(rootElement.getName());//添加节点
		Element root = docNew.addElement(rootElement.getQName());//添加节点
		if(rootElement!=null){
			generateChildrenByModel(rootElement,root,obj);
		}
		String xml = "";
		xml = docNew.asXML();
		//更新报文中的账号和密码信�?
		if(platUrl!=null){
			xml=xml.replace("${PLAT.USER}", platUrl.getCi_user());
			xml=xml.replace("${PLAT.PASSWORD}", platUrl.getCi_password());
		}else if(xml.indexOf("${PLAT.USER}")>0){
			throw new Exception("未获取到配置的账号信�?");
		}
		return xml;
	}
	
	private void generateChildrenByModel(Element elementModel,Element elementNew,Object obj) throws Throwable {
		Attribute tagAttr = elementModel.attribute("tagAttr");
		if(tagAttr!=null){//为节点添加固定属�?//
			String tag = tagAttr.getValue();
			String[] splitTag = tag.split(",");
			for (String tagMap : splitTag) {
				String[] splitTagMap = tagMap.split("=");
				if(splitTagMap.length==2){
					String attr = splitTagMap[0];
					String value = splitTagMap[1];
					elementNew.addAttribute(attr, value);
				}
			}
		}
		@SuppressWarnings("unchecked")
		List<Element> elements = elementModel.elements();
		for (Element elementM : elements) {
			if(elementM.attribute("isList")!=null && "1".equals(elementM.attribute("isList").getValue())){//该节点可能传多个
				Object objnew = null;
				try {
					String path = "";
					Attribute pathAttr = elementM.attribute("path");//获取取�?�路�?
					if(pathAttr!=null){
						path = pathAttr.getValue();
					}
					objnew = getObject(obj, path);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(objnew==null){
					continue;
				}
				
				String getMethd = "";
				String sizeMethd = "";
				String indexStr = "";
				Attribute indexAttr = elementM.attribute("listIndex");//获取集合索引�?
				if(indexAttr!=null){
					indexStr = indexAttr.getValue();
				}
				Attribute getMethdAttr = elementM.attribute("listMethd");//获取集合取�?�方�?
				if(getMethdAttr!=null){
					getMethd = getMethdAttr.getValue();
				}
				Attribute sizeAttr = elementM.attribute("listSizeMethd");//获取集合取长度方�?
				if(sizeAttr!=null){
					sizeMethd = sizeAttr.getValue();
				}
				Class cls = objnew.getClass();
				Method getAttrMethd = cls.getMethod(getMethd, int.class);//获取属�?�方�?  默认为传�?个int值的索引   不然没法�?
				if(indexStr!=null && !"".equals(indexStr)){//如果指定了索引�?�，则直接取索引�?
//					Element childElement = elementNew.addElement(elementM.getName());//添加节点
					Element childElement = elementNew.addElement(elementM.getQName());//添加节点
					int index = Integer.parseInt(indexStr);
					Object objnew1 = getAttrMethd.invoke(objnew, index);
					generateChildrenByModel(elementM,childElement,objnew1);
				}else{//如果没有指定索引值，则取该集合下�?有成�?
					//获取集合长度
					Method getSizeMthd = cls.getMethod(sizeMethd);
					int size = (Integer) getSizeMthd.invoke(objnew);
					for(int i=0;i<size;i++){
//						Element childElement = elementNew.addElement(elementM.getName());//添加节点
						Element childElement = elementNew.addElement(elementM.getQName());//添加节点
						Object objnew1 = getAttrMethd.invoke(objnew,i);
						generateChildrenByModel(elementM,childElement,objnew1);
					}
				}
			}else{
//				Element childElement = elementNew.addElement(elementM.getName());//添加节点
				if(elementM.elements().size()==0){//判断是否为取值节�?   现判断规则： 如果该节点不含子节点  则视为取值节�?
					Element childElement = elementNew.addElement(elementM.getQName());//添加节点
					String path = "";
					Attribute pathAttr = elementM.attribute("path");//获取取�?�路�?
					if(pathAttr!=null){
						path = pathAttr.getValue();
					}
					String endValue = "";
					//拆分path   可能有多个取值的情况
					String[] pathSplit = path.split(";");
					String[] valueArr = new String[pathSplit.length];
					for(int i=0;i<pathSplit.length;i++){//循环路径取�??
						try {
							valueArr[i] = getValue(pathSplit[i], obj);
						} catch (Exception e) {
							System.out.println("取�?�路径�??"+path+"】有�?!");
							valueArr[i] = "";
						}
					}
					String bindMethd = "";
					Attribute bindMethdAttr = elementM.attribute("bindMethd");//获取取�?�路�?
					if(bindMethdAttr!=null){
						bindMethd = bindMethdAttr.getValue();
					}
					if(valueArr.length==1){//如果只有�?个取值路�?  则先赋上�?终�??
						endValue = valueArr[0];
					}
					String igronNull = "";
					Attribute igronNullAttr = elementM.attribute("igronNull");//获取取�?�路�?
					if(igronNullAttr!=null){
						igronNull = igronNullAttr.getValue();
					}
					if(bindMethd!=null && !"".equals(bindMethd) && (isArrayNotEmpty(valueArr)||"1".equals(igronNull))){//现规则：如果有多个取值路径的节点,则必然有绑定事件进行处理
						//特殊事件处理
						Class[] clsArr = new Class[valueArr.length];
						for (int i = 0; i < clsArr.length; i++) {
							clsArr[i] = String.class;
						}
						String[] methds = bindMethd.split(",");
						for (int i = 0; i < methds.length; i++) {
							String methd = methds[i];
							String[] str = methd.split("\\.");
							if(str.length==2){
								String className = str[0]; //类名
								String methdName = str[1]; //方法�?
								Object objAction = listAction.get(className);
								if(objAction==null){
									System.out.println("【没有获取到"+className+"对象!�?");
								}else{
									Method md = null;
									Class<? extends Object> class1 = objAction.getClass();
									if(i==0){//第一次调用的方法，入参为path配置的个�?
										try {
											md = class1.getDeclaredMethod(methdName,clsArr);
										} catch (Exception e) {
											System.out.println("模板["+modelName+"]【节点["+elementM.getName()+"]的绑定方法["+methdName+"]不存在！�?");
											throw e.getCause();
										}
										try {
											endValue = (String) md.invoke(objAction, valueArr);
										} catch (Exception e) {
											System.out.println("模板["+modelName+"]【节点["+elementM.getName()+"]的绑定方法["+md.getName()+"]执行异常！�??");
											throw e.getCause();
										}
									}else{//除第�?次调用方法以外，其余链式调用的方法参数个数都�?1且为String
										try {
											md = class1.getDeclaredMethod(methdName,String.class);
										} catch (Exception e) {
											System.out.println("模板["+modelName+"]【节点["+elementM.getName()+"]的绑定方法["+methdName+"]不存在！�?");
											throw e.getCause();
										}
										try {
											endValue = (String) md.invoke(objAction, endValue);
										} catch (Exception e) {
											System.out.println("模板["+modelName+"]【节点["+elementM.getName()+"]的绑定方法["+md.getName()+"]执行异常！�??");
											throw e.getCause();
										}
									}
									
								}
							}
						}
					}
					//如果value为空  则取默认�?  //
					if(endValue ==null || "".equals(endValue)){
						endValue = "";
						//有默认�?�才�?   否则不取
						if(!"".equals(elementM.getText())){
							endValue = elementM.getText();//默认值为节点内容
						}
					}
					if("policyUpDate".equals(elementM.getName())){
						System.out.println("【name�?"+elementM.getName());
						System.out.println("【path�?"+path);
						System.out.println("【value�?"+endValue);
					}
					childElement.setText(endValue);
					continue;
				}else{
					//非叶子节�?
					String path = "";
					try {
						Attribute pathAttr = elementM.attribute("path");//获取取�?�路�?
						if(pathAttr!=null){
							path = pathAttr.getValue();
							Object objTemp = getObject(obj, path);
							if(objTemp==null || "0".equals(objTemp)){
								continue;
							}
							objTemp = null;
						}
					} catch (Exception e) {
						throw new Exception("节点�?"+elementM.getName()+"】的取�?�路径["+path+"]有误�?");
					}
					Element childElement = elementNew.addElement(elementM.getQName());//添加节点
					generateChildrenByModel(elementM,childElement,obj);
				}
			}
		}
	}
	
	/**
	 * 根据属�?�名获取属�?��??
	 * @param obj
	 * @param attribute
	 * @return
	 * @throws Exception 
	 */
	private Object getObject(Object obj,String path) throws Exception{
		if(List.class.isAssignableFrom(obj.getClass())){
			if(null == obj || ((List)obj).isEmpty())
				return null;
		} else if(null == obj) return null;
		String oldPath = path;
		Class cls = obj.getClass();
		int index = path.indexOf(".");
		String name = "";
		if(index!=-1){
			name = path.substring(0,index);
			path = path.substring(index+1);
		}else{
			name = path;
		}
		//拆分方法  示例  getAttr(0)  拆分�?  name = getAttr , parameter = 0
		Pattern p = Pattern.compile("(?<=\\()(.+?)(?=\\))");
	    Pattern p1 = Pattern.compile("(\\S+)(?=\\()");
	    Matcher m = p.matcher(name);
	    Matcher m2 = p1.matcher(name);
	    String methd = "";
	    String[] parameters = null;
	    if(m.find()) {
	    	parameters = m.group().split(",");
	    }
	    if(m2.find()) {
	    	methd = m2.group();
	    }
	    Object[] parametersNew = null;
	    if(parameters!=null){
	    	parametersNew = new Object[parameters.length];
	    }
	    if(methd==null || "".equals(methd)){
	    	throw new Exception("取�?�路径�??"+oldPath+"】有�?!");
	    }
	    Method[] declaredMethod = cls.getDeclaredMethods();
	    Object nodeVlued = null;
	    boolean flag = false;
	    for (Method method : declaredMethod) {
	    	if(method.getName().equals(methd)){
	    		Class<?>[] typeParameters = method.getParameterTypes();
	    		if(typeParameters.length!=0){
	    			if(parameters==null || parameters.length!=typeParameters.length){
	    				throw new Exception("取�?�路径�??"+oldPath+"】有�?!");
	    			}
	    			for(int i=0;i<typeParameters.length;i++){
		    			if("int".equals(typeParameters[i].getName())){
		    				parametersNew[i] = Integer.parseInt(parameters[i]);
		    			}else{
		    				parametersNew[i] = parameters[i];
		    			}
		    		}
	    			nodeVlued = method.invoke(obj,parametersNew);
	    		}else{
	    			nodeVlued = method.invoke(obj);
	    		}
	    		flag = true;
	    		break;
	    	}
		}
	    if(!flag){
	    	throw new Exception("取�?�路径�??"+oldPath+"】有�?!");
	    }
	    if(name.equals(path)){ //name=path 表示已经取到�?后一�?  
	    	return nodeVlued;
	    }else{
	    	return getObject(nodeVlued, path);
	    }
	}
	private static boolean isStringConst(String path){
		return path.startsWith("[") && path.endsWith("]");
	}
	/**
	 * 根据节点取�?�路径取�?
	 * @param path
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	private String getValue(String path,Object obj) throws Exception{
		if(StringUtils.isBlank(path)){
			return "";
		}
		if(isStringConst(path))
			return path.substring(1, path.length()-1);
		String oldPath = path;
		Class cls = obj.getClass();
		int index = path.indexOf(".");
		String name = "";
		if(index!=-1){
			name = path.substring(0,index);
			path = path.substring(index+1);
		}else{
			name = path;
		}
		//拆分方法  示例  getAttr(0)  拆分�?  name = getAttr , parameter = 0
		Pattern p = Pattern.compile("(?<=\\()(.+?)(?=\\))");
	    Pattern p1 = Pattern.compile("(\\S+)(?=\\()");
	    Matcher m = p.matcher(name);
	    Matcher m2 = p1.matcher(name);
	    String methd = "";
	    String[] parameters = null;
	    if(m.find()) {
	    	parameters = m.group().split(",");
	    }
	    if(m2.find()) {
	    	methd = m2.group();
	    }
	    Object[] parametersNew = null;
	    if(parameters!=null){
	    	parametersNew = new Object[parameters.length];
	    }
	    if(methd==null || "".equals(methd)){
	    	throw new Exception("取�?�路径�??"+oldPath+"】有�?!");
	    }
	    Method[] declaredMethod = cls.getDeclaredMethods();
	    Object nodeVlued = null;
	    boolean flag = false;
	    for (Method method : declaredMethod) {
	    	if(method.getName().equals(methd)){
	    		Class<?>[] typeParameters = method.getParameterTypes();
	    		if(typeParameters.length!=0){
	    			if(parameters==null || parameters.length!=typeParameters.length){
	    				throw new Exception("取�?�路径�??"+oldPath+"】有�?!");
	    			}
	    			for(int i=0;i<typeParameters.length;i++){
		    			if("int".equals(typeParameters[i].getName())){
		    				parametersNew[i] = Integer.parseInt(parameters[i]);
		    			}else{
		    				parametersNew[i] = parameters[i];
		    			}
		    		}
	    			nodeVlued = method.invoke(obj,parametersNew);//
	    		}else{
	    			nodeVlued = method.invoke(obj);
	    		}
	    		flag = true;
	    		break;
	    	}
		}
	    if(!flag){
	    	throw new Exception("取�?�路径�??"+oldPath+"】有�?!");
	    }
	    if(nodeVlued==null){
	    	return null;
	    }
	    if(nodeVlued instanceof String){
			return (String)nodeVlued;
		}else{
			return getValue(path, nodeVlued);
		}
	}
	
	private boolean isArrayNotEmpty(String[] arr){
		boolean flag = false;
		for (int i = 0; i < arr.length; i++) {
			if(!"".equals(arr[i])){
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	
	/**
	 * 根据XML模板解析文档
	 * @param obj
	 * @param xml
	 * @param xmlType
	 * @throws Exception
	 */
	@Performance(methd="解析模板")
	public void decodeXMLByModel(Object obj,String xml,String xmlType) throws Exception{
		Document docModel = (Document) listXML.get(xmlType);
		Document document = DocumentHelper.parseText(xml);
		Element rootElement = document.getRootElement();
		Element rootModel = docModel.getRootElement();
		if(!rootElement.getName().equals(rootModel.getName())){
			throw new Exception("接收文档根节点["+rootElement.getName()+"]与配置["+xmlType+"].["+rootModel.getName()+"]不一致！");
		}
		decodeChildrenByModel(rootElement,rootModel,obj);
	}
	
	/**
	 * 递归解析子节点并完成赋�??
	 * @param elenment
	 * @param eleModel
	 * @param obj
	 * @throws Exception
	 */
	private void decodeChildrenByModel(Element elenment,Element eleModel,Object obj) throws Exception{
		@SuppressWarnings("unchecked")
		List<Element> elements = elenment.elements();
		for (Element elementMol : elements) {
			Element elementM = eleModel.element(elementMol.getName());
			if(elementM==null){
				System.out.println("�?"+elementMol.getName()+"】找不到模板配置节点");
				continue;
			}
			String preNode = "";
			Attribute preNodeAttr = elementM.attribute("preNode");
			if(preNodeAttr!=null){
				preNode = preNodeAttr.getValue();
			}
			if(!"".equals(preNode)){//如果该节点有依赖节点  则先缓存  见依赖节点再放入
				Map<String, String> map = new HashMap<String, String>();
				map.put("value", elementMol.getText());
				map.put("path", elementM.attributeValue("path"));
				listCache.put(preNode, map);
				continue;
			}
			if(elementM!=null){//解析文档的节点跟模板节点对照，如果模板不存在该节�?  则忽�?
				if(elementM.attribute("isList")!=null && "1".equals(elementM.attribute("isList").getValue())){//该节点可能传多个
					List elementsTmp = elementMol.elements();
					if(elementsTmp==null || elementsTmp.size()<1){//如果返回节点为空，则跳过解析
						continue;
					}
					Object objnew = null;
					String path = "";
					Attribute pathAttr = elementM.attribute("path");//获取取�?�路�?
					if(pathAttr!=null){
						path = pathAttr.getValue();
					}
					if("0".equals(path)){//如果path�?0  则直接向下获�?
						decodeChildrenByModel(elementMol, elementM, obj);
						listCache.clear();
					}else{
						try {
							objnew = getObject(obj, path);
						} catch (Exception e) {
							if(e instanceof Exception){
								Exception exception = (Exception) e;
								System.out.println(exception.getMessage());
							}else{
								e.printStackTrace();
							}
						}
						if(objnew==null){
							continue;
						}
						
						String addMethd = "";
						Attribute addMethdAttr = elementM.attribute("listMethd");//获取集合add方法
						if(addMethdAttr!=null){
							addMethd = addMethdAttr.getValue();
						}
						Class cls = objnew.getClass();
						//TODO
						Method[] declaredMethods = cls.getDeclaredMethods();
						for (Method method : declaredMethods) {
							if(method.getName().equals(addMethd)){
								Class realClass = null;
								if(cls.isAssignableFrom(List.class) || cls.isAssignableFrom(Vector.class)
													||cls.isAssignableFrom(ArrayList.class)){//如果是list 则取泛型真实类型
									if(method.getGenericParameterTypes().length!=1){//目前限定add方法只能带一个参�?
										continue;
									}
									//正则表达式取属�?�名
									//拆分方法  示例  getAttr(0)  拆分�?  name = getAttr , parameter = 0
									String[] paths = path.split("\\.");
									Pattern p = Pattern.compile("(?<=get)(\\S+)(?=\\()");
								    Matcher m = p.matcher(paths[paths.length-1]);
									//获取属�?�名�?
								    String mStr = "";
								    while(m.find()) {
								    	mStr = m.group();
								    }
								    if("".equals(mStr)){
								    	throw new Exception("取�?�路径�??"+path+"】有误！");
								    }
									String filedName = mStr.substring(0,1).toLowerCase()+mStr.substring(1);
									Field field = null;
									//取父节点的�??
									Object temObj = null;
									try {
										if(path.lastIndexOf(".")>-1){//如果是多层取值，则获取该对象的父级对�?
											temObj = getObject(obj, path.substring(0,path.lastIndexOf(".")));
										}else{
											temObj = obj;
										}
										field = temObj.getClass().getDeclaredField(filedName);
									} catch (Exception e) {
										System.err.println("obj.getClass()"+temObj.getClass().getName());
									}
								    realClass = getRealGenericType(field);
								}else{ 
									Class<?>[] typeParameters2 = method.getParameterTypes();
									realClass = typeParameters2[0];
								}
								if(realClass==null){
									throw new Exception("�?"+path+"】集合泛型取值有误！");
								}
								Object newInstance = realClass.newInstance();
								if(listCache.get(elementMol.getName())!=null){//如果缓存有数据需要存进现有字�?
									Map<String, String> map = (Map<String, String>) listCache.get(elementMol.getName());
									try {
										setValue(map.get("value"), newInstance, map.get("path"));
									} catch (Exception e) {
										// TODO 处理异常，不抛出//
										System.out.println("取�?�路径�??"+map.get("path")+"】有�?!");
									}
								}
								decodeChildrenByModel(elementMol, elementM, newInstance);
								method.invoke(objnew, newInstance);
								break;
							}
						}
					}
				}else{
					if(elementM.elements().size()==0){//判断是否为取值节�?   现判断规则： 如果该节点不含子节点  则视为取值节�?
						//取�?�节点进�?
						String path = elementM.attributeValue("path");
						if(path!=null && !"".equals(path)){//如果取�?�路径为�?  则不处理  �?
							String value = elementMol.getText();
							//赋�?�之前先进行过滤事件处理
							String bindMethd = elementM.attributeValue("bindMethd");
							if(bindMethd!=null && !"".equals(bindMethd) && !"".equals(value)){//现规则：如果有多个取值路径的节点,则必然有绑定事件进行处理
								//特殊事件处理
								String[] methds = bindMethd.split(",");
								int index = 0;
								for (String methd : methds) {
									String[] str = methd.split("\\.");
									if(str.length==2){
										index++;
										String className = str[0]; //类名
										String methdName = str[1]; //方法�?
										Object objAction = listAction.get(className);
										if(objAction==null){
											System.out.println("【没有获取到"+className+"对象!�?");
										}else{
											//暂时默认都只带一个String参数
											Class<? extends Object> class1 = objAction.getClass();
											Method md = null;
											String[] args = null;
											Class<String>[] argsTypes = null;
											String fisrtMethdParams = elementM.attributeValue("params");
											
											if(1 == index && !StringUtils.isBlank(fisrtMethdParams)){ // 第一个方�? �?要解析params属�??
												String[] params = fisrtMethdParams.split(";");
												args = new String[params.length];
												argsTypes = new Class[params.length];
												for(int i = 0; i < params.length; i++){
													if(isStringConst(params[i])){
														args[i] = params[i].substring(1, params[i].length()-1);
													}else if("?".equals(params[i].trim())){
														args[i] = value;
													}else{
														args[i] = params[i];
													}
													argsTypes[i] = String.class;
												}
											}else{
												args = new String[]{value};
												argsTypes = new Class[]{String.class};
											}
											md = class1.getDeclaredMethod(methdName,argsTypes);
											try {
												value = (String) md.invoke(objAction, args);
											} catch (Exception e) {
												System.out.println("【绑定方法执行异常！�?");
												throw e;
											}
										}
									}
								}
							}
							String connectValue = "";
							Attribute connectValueAttr = elementM.attribute("connectValue");//获取取�?�路�?
							if(connectValueAttr!=null){
								connectValue = connectValueAttr.getValue();
							}
							if("1".equals(connectValue)){//连接字符�?
								Object tempV = listCache.get(elementM.getName());
								if(tempV!=null){
									value = (String)tempV + "," +value;
									listCache.remove(elementM.getName());//移除原来的�??
								}
								listCache.put(elementM.getName(), value);//放入新的�?
							}
							try {
								setValue(value, obj, path);//自动赋�??
							} catch (Exception e) {
								System.out.println("取�?�路径�??"+path+"】有�?!");
							}
						}
					}else{
						decodeChildrenByModel(elementMol,elementM,obj);
					}
				}
			}else{
				System.out.println("节点�?"+elementMol.getPath()+"/"+elementMol.getName()+"】无法解析！");
			}
		}
	}
	/**
	 * 根据节点取�?�路径赋�?
	 * @param value
	 * @param obj
	 * @param path
	 * @throws Exception 
	 */
	private void setValue(Object value,Object obj,String path) throws Exception{
		String oldPath = path;
		Class cls = obj.getClass();
		int index = path.indexOf(".");
		String name = "";
		if(index!=-1){
			name = path.substring(0,index);
			path = path.substring(index+1);
		}else{
			name = path;
		}
		
		//拆分方法  示例  getAttr(0)  拆分�?  name = getAttr , parameter = 0
		Pattern p = Pattern.compile("(?<=\\()(.+?)(?=\\))");
	    Pattern p1 = Pattern.compile("(\\S+)(?=\\()");
	    Matcher m = p.matcher(name);
	    Matcher m2 = p1.matcher(name);
	    String methd = "";
	    String[] parameters = null;
	    if(m.find()) {
	    	parameters = m.group().split(",");
	    }
	    if(m2.find()) {
	    	methd = m2.group();
	    }
	    Object[] parametersNew = null;
	    if(parameters!=null){
	    	parametersNew = new Object[parameters.length];
	    }
	    if(methd==null || "".equals(methd)){
	    	throw new Exception("取�?�路径�??"+oldPath+"】有�?!");
	    }
	    Object nodeVlued = null;
	    boolean flag = false;
	    Method[] declaredMethod = cls.getMethods();
		//如果name�? setValue类型  则直接进行反射赋�?
		if(name.startsWith("set")){
			for (Method method : declaredMethod) {
		    	if(method.getName().equals(methd)){
		    		Class<?>[] typeParameters = method.getParameterTypes();
		    		if(typeParameters.length==1){
		    			method.invoke(obj,value);
		    		}else{
		    			throw new Exception("取�?�路径�??"+oldPath+"】有�?");
		    		}
		    		flag = true;
		    		break;
		    	}
			}
		}else{ 
			for (Method method : declaredMethod) {
		    	if(method.getName().equals(methd)){
		    		Class<?>[] typeParameters = method.getParameterTypes();
		    		if(typeParameters.length!=0){
		    			if(parameters==null || parameters.length!=typeParameters.length){
		    				throw new Exception("取�?�路径�??"+oldPath+"】有�?!");
		    			}
		    			for(int i=0;i<typeParameters.length;i++){
			    			if("int".equals(typeParameters[i].getName())){
			    				parametersNew[i] = Integer.parseInt(parameters[i]);
			    			}else{
			    				parametersNew[i] = parameters[i];
			    			}
			    		}
		    			try {
		    				nodeVlued = method.invoke(obj,parametersNew);
		    				setValue(value, nodeVlued, path);
						} catch (Exception e) {
							for (Method declaredMethod2 : declaredMethod) {
								if(("set"+method.getName().substring(3)).equals(declaredMethod2.getName())){
									Class<?>[] typeParameters2 = declaredMethod2.getParameterTypes();
									Object newInstance = typeParameters2[0].newInstance();
									declaredMethod2.invoke(obj, newInstance);
									setValue(value, newInstance, path);
									break;
								}
							}
						}
		    		}else{
		    			nodeVlued = method.invoke(obj);
		    			setValue(value, nodeVlued, path);
		    		}
		    		flag = true;
		    		break;
		    	}
			}
		}
	    if(!flag){
	    	throw new Exception("取�?�路径�??"+oldPath+"】有�?,没有找到对应方法!");
	    }
	}
	
	private Class getRealGenericType(Field field){
		Class result = null;
		Class fieldClazz = field.getType();
		if(fieldClazz.isAssignableFrom(List.class) || fieldClazz.isAssignableFrom(Vector.class)) //�?2�?  
	    {   
	             Type fc = field.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类�?  
	             if(fc != null) {
	            	 if(fc instanceof ParameterizedType) // �?3】如果是泛型参数的类�?   
	            	 {   
	            		 ParameterizedType pt = (ParameterizedType) fc;  
	            		 result = (Class)pt.getActualTypeArguments()[0]; //�?4�? 得到泛型里的class类型对象�?  
	            	 }   
	             } 
	      }  
		return result;
	}
	
}
