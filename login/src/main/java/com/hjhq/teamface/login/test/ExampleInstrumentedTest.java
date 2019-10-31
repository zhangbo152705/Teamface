package com.hjhq.teamface.login.test;


import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.Iterator;
import java.util.Set;

public class ExampleInstrumentedTest {
    String myData = "{\"bean\":\"bean1540266426107\",\"data\":{\"location_1540266566053\":{\"area\":null,\"lng\":\"113.946463\",\"value\":\"广东省深圳市南山区高新南一道21-2号\",\"lat\":\"22.538108\"},\"formula_1540266602217\":null,\"number_1540266558376\":\"521.00\",\"multi_1540266592344\":[{\"color\":null,\"controlField\":null,\"hidenFields\":null,\"isCheck\":true,\"label\":\"选项2\",\"relyonList\":null,\"subList\":null,\"value\":\"1\"}],\"reference_1541725666584\":\"4\",\"department_1540266598532\":\"1\",\"barcode_1540266615818\":\"\",\"subform_1540266652959\":[{\"subform_multi_1540266822241\":\"\",\"subform_reference_1540267019402\":\"\",\"subform_email_1540266691217\":\"\",\"subform_location_1540266803978\":{\"area\":null,\"lng\":\"113.946648\",\"value\":\"广东省深圳市南山区高新南一道58号靠近思创科技大厦\",\"lat\":\"22.538563\"},\"subform_functionformula_1540267035118\":null,\"subform_mutlipicklist_1540266832095\":\"\",\"subform_phone_1540266684983\":\"\",\"subform_textarea_1540266678398\":\"\",\"subform_datetime_1540266747268\":\"\",\"subform_url_1540266760766\":\"\",\"subform_attachment_1540266754582\":\"\",\"subform_multitext_1540266679973\":\"\",\"subform_text_1540266676999\":\"\",\"subform_department_1540266870678\":\"\",\"subform_formula_1540267027747\":null,\"subform_personnel_1540266839656\":\"\",\"subform_picture_1540266814513\":\"\",\"subform_number_1540266741006\":\"\",\"subform_picklist_1540266681511\":\"\",\"subform_area_1540267069728\":\"\"}],\"multitext_1540266549214\":\"\",\"text_1541726870832\":\"5210.0\",\"text_1540266426107\":\"页签规则用22\",\"datetime_modify_time\":\"\",\"area_1540266604644\":\"430000:湖南省,430200:株洲市,430202:荷塘区\",\"email_1540266556046\":\"xingbaixian@sina.com\",\"personnel_modify_by\":\"\",\"personnel_1541726313098\":\"4\",\"personnel_1540266596015\":\"5,6\",\"personnel_principal\":\"3\",\"identifier_1540266606977\":\"\",\"url_1540266564285\":\"www.baidu.com\",\"picklist_1540266551925\":[{\"color\":\"#FFFFFF\",\"label\":\"选项3\",\"value\":\"2\"}],\"personnel_create_by\":\"\",\"seniorformula_1540266613583\":null,\"datetime_1540266560260\":1533109872000,\"reference_1540266609014\":\"3.0\",\"reference_1541726425621\":\"113.0\",\"datetime_create_time\":\"\",\"text_1541725711623\":\"935412594\",\"multi_1541725844248\":[{\"color\":null,\"controlField\":null,\"hidenFields\":null,\"isCheck\":true,\"label\":\"选项2\",\"relyonList\":null,\"subList\":null,\"value\":\"1\"},{\"color\":null,\"controlField\":null,\"hidenFields\":null,\"isCheck\":true,\"label\":\"新选项3\",\"relyonList\":null,\"subList\":null,\"value\":\"2\"}],\"picture_1540266567956\":\"\",\"text_1540266546380\":\"编辑后触发了编辑触发够给你做六自动化\",\"picklist_1541725759483\":[{\"color\":\"#FFFFFF\",\"label\":\"新选项5\",\"value\":\"4\"},{\"color\":\"#FFFFFF\",\"label\":\"新选项5\",\"value\":\"4\"},{\"color\":\"#FFFFFF\",\"label\":\"新选项5\",\"value\":\"4\"}],\"department_1541726318819\":\"3,2\",\"textarea_1540266544466\":\"多行文本中的文字\\n你看见了没\",\"attachment_1540266562539\":\"\",\"seas_pool_id\":null,\"phone_1540266554259\":\"13500000001\",\"number_1541726882627\":\"0.00\",\"mutlipicklist_1540266600546\":[{\"color\":\"#FFFFFF\",\"controlField\":null,\"hidenFields\":null,\"isCheck\":false,\"label\":\"一级选项1\",\"relyonList\":null,\"subList\":null,\"value\":\"0\"},{\"color\":\"#FFFFFF\",\"controlField\":null,\"hidenFields\":null,\"isCheck\":false,\"label\":\"二级选项2\",\"relyonList\":null,\"subList\":null,\"value\":\"1\"},{\"color\":\"#FFFFFF\",\"controlField\":null,\"hidenFields\":null,\"isCheck\":false,\"label\":\"三级选项3\",\"relyonList\":null,\"subList\":null,\"value\":\"0\"}],\"functionformula_1540266612042\":null},\"id\":null,\"moduleId\":null,\"title\":\"全字段映射\"}";
    String theirData = "{\"bean\":\"bean1540266426107\",\"data\":{\"reference_1541725666584\":4,\"text_1540266426107\":\"页签规则用66\",\"text_1541725711623\":\"935412594\",\"text_1540266546380\":\"编辑后触发了编辑触发够给你做六自动化\",\"textarea_1540266544466\":\"多行文本中的文字\\n你看见了没\",\"multitext_1540266549214\":\"\",\"picklist_1540266551925\":[{\"color\":\"#FFFFFF\",\"label\":\"选项3\",\"value\":\"2\"}],\"picklist_1541725759483\":[{\"color\":\"#FFFFFF\",\"label\":\"选项3\",\"value\":\"2\"},{\"color\":\"#FFFFFF\",\"label\":\"新选项4\",\"value\":\"3\"},{\"color\":\"#FFFFFF\",\"label\":\"新选项5\",\"value\":\"4\"}],\"multi_1540266592344\":[{\"label\":\"选项2\",\"value\":\"1\"}],\"multi_1541725844248\":[{\"label\":\"选项2\",\"value\":\"1\"},{\"label\":\"新选项3\",\"value\":\"2\"}],\"phone_1540266554259\":\"13500000001\",\"email_1540266556046\":\"xingbaixian@sina.com\",\"number_1540266558376\":\"521.00\",\"datetime_1540266560260\":1533109872000,\"attachment_1540266562539\":[],\"url_1540266564285\":\"www.baidu.com\",\"location_1540266566053\":{\"area\":\"广东省#深圳市#南山区\",\"lng\":113.946463,\"value\":\"广东省深圳市南山区高新南一道21-2号\",\"lat\":22.538108},\"picture_1540266567956\":\"\",\"mutlipicklist_1540266600546\":[{\"label\":\"一级选项1\",\"value\":\"0\",\"color\":\"#FFFFFF\"},{\"label\":\"二级选项2\",\"value\":\"1\",\"color\":\"#FFFFFF\"},{\"label\":\"三级选项3\",\"value\":\"0\",\"color\":\"#FFFFFF\"}],\"personnel_principal\":\"3\",\"area_1540266604644\":\"430000:湖南省,430200:株洲市,430202:荷塘区\",\"personnel_1541726313098\":\"4\",\"personnel_1540266596015\":\"5,6\",\"department_1540266598532\":\"1\",\"department_1541726318819\":\"3,2\",\"reference_1540266609014\":3,\"reference_1541726425621\":113,\"text_1541726870832\":5210,\"number_1541726882627\":\"0.00\",\"subform_1540266652959\":[{\"subform_text_1540266676999\":\"\",\"subform_textarea_1540266678398\":\"\",\"subform_multitext_1540266679973\":\"\",\"subform_picklist_1540266681511\":[],\"subform_phone_1540266684983\":\"\",\"subform_email_1540266691217\":\"\",\"subform_number_1540266741006\":\"\",\"subform_datetime_1540266747268\":\"\",\"subform_attachment_1540266754582\":[],\"subform_url_1540266760766\":\"\",\"subform_location_1540266803978\":{\"lng\":113.93702,\"lat\":22.52236,\"value\":\"广东省深圳市南山区粤海街道三诺智慧大厦深圳市软件产业基地\",\"area\":\"广东省#深圳市#南山区\"},\"subform_picture_1540266814513\":[],\"subform_multi_1540266822241\":[],\"subform_mutlipicklist_1540266832095\":[],\"subform_personnel_1540266839656\":\"\",\"subform_department_1540266870678\":\"\",\"subform_area_1540267069728\":\"\"}]}}";


    public void format() {
        try {
            JSONObject onlyMy = new JSONObject();
            JSONObject onlyTheir = new JSONObject();
            JSONObject bothMy = new JSONObject();
            JSONObject bothTheir = new JSONObject();

            final JSONObject jMy = JSONObject.parseObject(myData);
            final JSONObject jTheir = JSONObject.parseObject(theirData);

            final Set<String> set1 = jMy.keySet();
            final Set<String> set2 = jTheir.keySet();

            final Iterator<String> iterator1 = set1.iterator();
            final Iterator<String> iterator2 = set2.iterator();

            while (iterator1.hasNext()) {
                final String next1 = iterator1.next();
                while (iterator2.hasNext()) {
                    final String next2 = iterator2.next();
                    if (next1.equals(next2)) {
                        bothMy.put(next1, jMy.get(next1));
                        bothTheir.put(next1, jTheir.get(next1));
                    }

                }

            }
            /*while (iterator1.hasNext()) {
                final String next1 = iterator1.next();
                while (iterator2.hasNext()) {
                    final String next2 = iterator2.next();
                    if (next1.equals(next2)) {
                        jTheir.remove(next1);
                    }

                }

            }*/
            /*while (iterator2.hasNext()) {
                final String next1 = iterator1.next();
                while (iterator1.hasNext()) {
                    final String next2 = iterator2.next();
                    if (next1.equals(next2)) {
                        jMy.remove(next1);
                    }

                }

            }*/
            Log.e("my", JSONObject.toJSONString(onlyMy));
            Log.e("their", JSONObject.toJSONString(onlyTheir));
            Log.e("bothMy", JSONObject.toJSONString(bothMy));
            Log.e("bothTheir", JSONObject.toJSONString(bothTheir));

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}