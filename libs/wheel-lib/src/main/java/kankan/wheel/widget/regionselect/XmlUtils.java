package kankan.wheel.widget.regionselect;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import kankan.wheel.widget.regionselect.model.CityWheelModel;
import kankan.wheel.widget.regionselect.model.DistrictWheelModel;
import kankan.wheel.widget.regionselect.model.ProvinceWheelModel;
import kankan.wheel.widget.regionselect.service.XmlAreaParserHandler;
import kankan.wheel.widget.regionselect.service.XmlJobParserHandler;

/**
 * @author Administrator
 */
public class XmlUtils {

    private XmlUtils() {
        // TODO Auto-generated constructor stub
    }

    private static class SingletonHolder {
        private static final XmlUtils INSTANCE = new XmlUtils();
    }

    /**
     * 获取实例
     *
     * @return
     */
    public static XmlUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 解析数据职业数据
     *
     * @param selectID
     * @return
     */
    public XmlJobParserHandler parseJobData(Context context, String selectID) {

        XmlJobParserHandler handler = null;

        try {

            handler = new XmlJobParserHandler();

            AssetManager asset = context.getAssets();

            InputStream input = asset.open("industry.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();

            handler.setId(selectID);

            parser.parse(input, handler);

            input.close();

        } catch (Exception e) {
            // TODO: handle exception
        }

        return handler;

    }

    /**
     * 解析省市数据
     *
     * @param selectID
     * @return
     */
    public XmlAreaParserHandler parseAreaData(Context context, String selectID) {

        XmlAreaParserHandler handler = null;

        try {
            handler = new XmlAreaParserHandler();

            AssetManager asset = context.getAssets();

            InputStream input = asset.open("ProvinceAndCity.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();

            handler.setId(selectID);

            parser.parse(input, handler);

            input.close();
        } catch (Exception e) {
            // TODO: handle exception

            e.printStackTrace();

        }

        return handler;

    }

    /**
     * 获取省列表
     *
     * @param context
     * @param selectID
     * @return
     */
    public List<ProvinceWheelModel> getProvinceData(Context context, String selectID) {
        XmlAreaParserHandler handler = null;
        List<ProvinceWheelModel> list = new ArrayList<>();
        handler = parseAreaData(context, selectID);
        if (handler != null) {
            list.addAll(handler.getDataList());
        }
        return list;
    }

    /**
     * 获取市列表
     *
     * @param context
     * @param selectID
     * @return
     */
    public List<CityWheelModel> getCityData(Context context, String selectID) {
        List<CityWheelModel> list = new ArrayList<>();
        List<ProvinceWheelModel> provinceData = getProvinceData(context, selectID);
        for (ProvinceWheelModel model : provinceData) {
            list.addAll(model.getCityList());
        }

        return list;
    }

    /**
     * 获取区县列表
     *
     * @param context
     * @param selectID
     * @return
     */
    public List<DistrictWheelModel> getDistrictData(Context context, String selectID) {
        List<DistrictWheelModel> list = new ArrayList<>();
        List<CityWheelModel> provinceData = getCityData(context, selectID);
        for (CityWheelModel model : provinceData) {
            list.addAll(model.getDistrictList());
        }
        return list;
    }

}
