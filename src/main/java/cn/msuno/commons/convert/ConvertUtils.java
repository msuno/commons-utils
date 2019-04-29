package cn.msuno.commons.convert;

import cn.msuno.commons.Constants;

import com.alibaba.fastjson.JSONObject;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 静态类型转换工具，基本包括80%的类型转换
 *
 * @author msuno
 * @version 1.0-SNAPSHOT
 * @since 19
 **/
public class ConvertUtils {

    /**
     * 通过Map创建Xml
     *
     * @author moshunwei
     * @version 1.0-SNAPSHOT
     * @param parentName 根节点
     * @param params Map参数key-value
     * @param isCDATA 是否添加CDATA
     **/
    public static String MapToXml(String parentName, Map<String, Object> params, boolean isCDATA) throws ConvertException {
        Document doc = DocumentHelper.createDocument();
        doc.addElement(parentName);
        String xml = iteratorXml(doc.getRootElement(), parentName, params, isCDATA);
        return formatXML(xml);
    }

    /**
     * 通过Map创建Xml
     *
     * @author moshunwei
     * @version 1.0-SNAPSHOT
     * @param parentName 根节点
     * @param params Map参数key-value
     **/
    public static String MapToXml(String parentName, Map<String, Object> params) throws ConvertException {
        return MapToXml(parentName, params, false);
    }

    /**
     * 通过Map创建Xml,默认父节点为Document
     *
     * @author moshunwei
     * @version 1.0-SNAPSHOT
     * @param params Map参数key-value
     **/
    public static String MapToXml(Map<String, Object> params) throws ConvertException {
        return MapToXml(Constants.XMLDEFAULTNAME, params);
    }

    /**
     * 通过Map创建Xml,递归创建XML
     *
     * @author moshunwei
     * @version 1.0-SNAPSHOT
     * @param element 父元素
     * @param parentName 父节点名称
     * @param params Map参数
     * @param isCDATA 值添加CDATA
     **/
    private static String iteratorXml(Element element, String parentName, Map<String, Object> params, boolean isCDATA) {
        Element e = element.addElement(parentName);
        Set<String> set = params.keySet();
        Iterator it = set.iterator();
        while(it.hasNext()) {
            String key = (String)it.next();
            if (params.get(key) instanceof Map) {
                iteratorXml(e, key, (Map)params.get(key), isCDATA);
            } else {
                String value = params.get(key) == null ? Constants.EMPTYSTR : params.get(key).toString();
                if (isCDATA) {
                    e.addElement(key).addCDATA(value);
                } else {
                    e.addElement(key).addText(value);
                }
            }
        }
        return e.asXML();
    }

    /**
     * 格式换XML，美化
     *
     * @author moshunwei
     * @version 1.0-SNAPSHOT
     * @param inputXML xml
     **/
    public static String formatXML(String inputXML) throws ConvertException {
        String requestXML = null;
        XMLWriter writer = null;
        Document document;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(new StringReader(inputXML));
            if (document != null) {
                StringWriter stringWriter = new StringWriter();
                OutputFormat format = new OutputFormat(Constants.INDENT, true);
                format.setNewLineAfterDeclaration(false);
                format.setNewlines(true);
                writer = new XMLWriter(stringWriter, format);
                writer.write(document);
                writer.flush();
                requestXML = stringWriter.getBuffer().toString();
            }
           return requestXML;
        } catch (Exception var16) {
            throw new ConvertException(var16.getMessage(),var16);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException var15) {
                    var15.printStackTrace();
                }
            }

        }
    }

    /**
     * Map转XML
     *
     * @author moshunwei
     * @version 1.0-SNAPSHOT
     * @param xml xml
     **/
    public static Map<String, Object> MapToXml(String xml) throws ConvertException {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml);
        } catch (DocumentException var4) {
            throw new ConvertException(var4.getMessage(),var4);
        }
        Map<String, Object> map = new HashMap<String,Object>();
        if (doc == null) {
            return map;
        } else {
            Element rootElement = doc.getRootElement();
            elementToMap(rootElement, map);
            return map;
        }
    }

    /**
     * Map转XML
     *
     * @author moshunwei
     * @version 1.0-SNAPSHOT
     * @param element 父节点
     * @param map Map参数
     **/
    private static Map<String, Object> elementToMap(Element element, Map<String, Object> map) {
        List<Element> list = element.elements();
        int size = list.size();
        if (size == 0) {
            map.put(element.getName(), element.getTextTrim());
        } else {
            Map<String, Object> innerMap = new HashMap();
            Iterator var6 = list.iterator();
            while(var6.hasNext()) {
                Element ele1 = (Element)var6.next();
                String eleName = ele1.getName();
                Object obj = innerMap.get(eleName);
                if (obj == null) {
                    elementToMap(ele1, innerMap);
                } else if (obj instanceof Map) {
                    List<Map<String, Object>> list1 = new ArrayList();
                    list1.add((Map)innerMap.remove(eleName));
                    elementToMap(ele1, innerMap);
                    list1.add((Map)innerMap.remove(eleName));
                    innerMap.put(eleName, list1);
                } else if(obj instanceof List) {
                    elementToMap(ele1, innerMap);
                    ((List)obj).add(innerMap);
                }
            }
            map.put(element.getName(), innerMap);
        }
        return map;
    }

    /**
     * 对象转Map
     *
     * @author moshunwei
     * @version 1.0-SNAPSHOT
     * @param obj 对象
     **/
    public static JSONObject BeanToJson(Object obj) throws ConvertException{
        if(obj == null){
            return null;
        }
        Class clazz = obj.getClass();
        JSONObject json = new JSONObject();
        try {
            while (clazz != null){
                Field[] fields = clazz.getDeclaredFields();
                for(Field field : fields){
                    field.setAccessible(true);
                    Object o = field.get(obj);
                    json.put(field.getName(),o);
                }
                clazz = clazz.getSuperclass();
            }
        } catch (Exception e) {
            throw new ConvertException(e.getMessage(),e);
        }
        return json;
    }

    /**
     * 对象转Map
     *
     * @author moshunwei
     * @version 1.0-SNAPSHOT
     * @param clazz 对象
     **/
    public static <T> JSONObject BeanToJson(Class<T> clazz) throws ConvertException{
        try {
            return BeanToJson(clazz.newInstance());
        } catch (Exception e) {
            throw new ConvertException(e.getMessage(),e);
        }
    }

}
