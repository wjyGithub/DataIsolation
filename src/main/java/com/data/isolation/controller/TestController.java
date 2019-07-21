package com.data.isolation.controller;

import com.data.isolation.parser.DruidUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/25 19:14.
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {

    @GetMapping(value = "")
    private String processor(@RequestParam(required = false,name = "sqlArgs") String sqlArgs) {
        String sqlT= "SELECT\n" +
                "            if(ifnull(tp.DISABLED, 1)=1,0,1) di,\n" +
                "            ifnull(t.code,'') code,\n" +
                "            ifnull(t.description,'') description,\n" +
                "            ifnull(t.hierarchyId,-1) hierarchyId,\n" +
                "            ifnull(t.`name`,'') name,\n" +
                "            ifnull(t.parentId,-1) parentId,\n" +
                "            ifnull(t.result,'') result,\n" +
                "            ifnull(t.thingId,-1)thingId,\n" +
                "            ifnull(t.thingTmpltId,-1)thingTmpltId,\n" +
                "            ifnull(t.thingTmpltName,'')thingTmpltName,\n" +
                "            ifnull(t.type,'')type,\n" +
                "            del,\n" +
                "            register,\n" +
                "            useRestrict,\n" +
                "            ifnull(imgUrl,'') imgUrl\n" +
                "        FROM\n" +
                "            (\n" +
                "                SELECT\n" +
                "                    t.TYPE AS type,\n" +
                "                    t. CODE AS CODE,\n" +
                "                    t. NAME AS NAME,\n" +
                "                    tt. NAME AS thingTmpltName,\n" +
                "                    tt.THING_TMPLT_ID thingTmpltId,\n" +
                "                    t.DESCRIPTION AS description,\n" +
                "                    h.HIERARCHY_ID AS hierarchyId,\n" +
                "                    t.PARENT_ID AS parentId,\n" +
                "                    t.THING_ID AS thingId,\n" +
                "                    queryHierarchyById (1,1) AS result,\n" +
                "                    pv.DATA_ID,\n" +
                "                    pv.`VALUE`," +
                "                    CASE\n" +
                "                     WHEN\n" +
                "                     (SELECT\n" +
                "                     COUNT(1)\n" +
                "                     FROM\n" +
                "                     t_property_value\n" +
                "                     WHERE\n" +
                "                     t_property_value.DATA_ID != t.`CODE`+'-A-00'\n" +
                "                     AND\n" +
                "                     FIND_IN_SET(THING_ID, QUERYTHINGCHILD( t.THING_ID))) > 0\n" +
                "                     THEN\n" +
                "                     1\n" +
                "                     ELSE 0\n" +
                "                     END del,\n" +
                "                    CASE\n" +
                "                        WHEN\n" +
                "                            (SELECT\n" +
                "                                    COUNT(1 OR NULL)\n" +
                "                                FROM\n" +
                "                                    hiot_hub.t_thing_secret\n" +
                "                                WHERE\n" +
                "                                    FIND_IN_SET(THING_ID, hiot_thing.QUERYTHINGCHILD(t.THING_ID))) > 0\n" +
                "                        THEN\n" +
                "                            1\n" +
                "                        ELSE 0\n" +
                "                    END register,\n" +
                "                    tt.USE_RESTRICT useRestrict,\n" +
                "                    t.IMG_URL imgUrl\n" +
                "                FROM\n" +
                "                    t_thing t\n" +
                "                    LEFT JOIN t_thing_tmplt tt ON t.THING_TMPLT_ID = tt.THING_TMPLT_ID\n" +
                "                    LEFT JOIN t_hierarchy h ON t.HIERARCHY_ID = h.HIERARCHY_ID\n" +
                "                    LEFT JOIN t_property_value pv ON t.THING_ID = pv.THING_ID\n" +
                "                                                     AND pv.DATA_ID = CONCAT(t. CODE, '-', 'A', '-', '00')\n" +
                "                WHERE\n" +
                "                    t.THING_ID = 1\n" +
                "                    AND t.ORGANIZATION_ID = 1\n" +
                "            ) t\n" +
                "            LEFT JOIN t_thing_property tp ON tp.DATA_ID = t.DATA_ID";
//        String sqlQ = "SELECT \n" +
//                " 1\n" +
//                "FROM t_thing\n" +
//                "where id = (select 1 from t_thing where id = (select 1 from t_thing where id = exists(select 1 from a union select 1 from b)) union select 1 from tt where id = (select 1 from yy))";
      String sqlArg = "SELECT\n" +
              "\tt.* \n" +
              "FROM\n" +
              "\t(\n" +
              "SELECT\n" +
              "\tcow.UUID cowUUID,\n" +
              "\tcow.COW_NUM,\n" +
              "\tcow.cow_NUM cowEar,\n" +
              "\thouse.HOUSE_NAME houseName,\n" +
              "\tcow.PARITY,\n" +
              "\t(\n" +
              "SELECT\n" +
              "\tMAX( mating.CURRENT_MATING_TIME ) \n" +
              "FROM\n" +
              "\tt_fy_mating mating \n" +
              "WHERE\n" +
              "\tmating.COW_UUID = cow.uuid \n" +
              "\tAND mating.PARITY = cow.PARITY \n" +
              "\tAND mating.del = 0 \n" +
              "\t) matingTime,\n" +
              "\tDATEDIFF( CURRENT_DATE ( ), cow.CALVING_DATE ) lactationDay,\n" +
              "\tcow.STATE,\n" +
              "\tdhi.PEAK_MILK_VOLUME,\n" +
              "\tdhi.FAT_305,\n" +
              "\t(\n" +
              "SELECT\n" +
              "\tsum(\n" +
              "\tIFNULL( LEFT_FRONT, 0 ) + IFNULL( RIGHT_FRONT, 0 ) + IFNULL( LEFT_BACK, 0 ) + IFNULL( RIGHT_BACK, 0 ) \n" +
              "\t) nipSum \n" +
              "FROM\n" +
              "\tt_sd_healthcare h \n" +
              "WHERE\n" +
              "\th.DEL = 0 \n" +
              "\tAND NURSING_TYPE = 'hypoplastic_nipple' \n" +
              "\tAND h.COW_UUID = cow.uuid \n" +
              "\t) nipSum,\n" +
              "\t(\n" +
              "SELECT\n" +
              "\tcount( 1 ) \n" +
              "FROM\n" +
              "\tt_sd_morbidity sd \n" +
              "WHERE\n" +
              "\tsd.del = 0 \n" +
              "\tAND cow.uuid = sd.cow_uuid \n" +
              "\tAND sd.DISEASE_DATE >= cow.CALVING_DATE \n" +
              "\t) mobSum,\n" +
              "\t(\n" +
              "SELECT\n" +
              "\tcount( 1 ) \n" +
              "FROM\n" +
              "\tt_fy_abortion abortion \n" +
              "WHERE\n" +
              "\tcow.PARITY = abortion.PARITY \n" +
              "\tAND cow.uuid = abortion.cow_UUID \n" +
              "\tAND abortion.del = 0 \n" +
              "\t) abortionSum,\n" +
              "\t(\n" +
              "SELECT\n" +
              "\tifnull( SUM( simpleWeekMilkVolume ), dhi.milk_volume ) \n" +
              "FROM\n" +
              "\t(\n" +
              "SELECT\n" +
              "\tmilk.cow_uuid,\n" +
              "\tmilk.shift,\n" +
              "\tAVG( milk.SINGLE_MILK_VOLUME ) simpleWeekMilkVolume \n" +
              "FROM\n" +
              "\tt_nt_milking_process milk \n" +
              "WHERE\n" +
              "\tmilk.del = 0 \n" +
              "\tAND milk.MILKING_DATE BETWEEN DATE_SUB( CURRENT_DATE ( ), INTERVAL 7 DAY ) \n" +
              "\tAND CURRENT_DATE ( ) \n" +
              "GROUP BY\n" +
              "\tmilk.cow_uuid,\n" +
              "\tmilk.SHIFT \n" +
              "HAVING\n" +
              "\tmilk.SHIFT IS NOT NULL \n" +
              "\t) t \n" +
              "WHERE\n" +
              "\tt.cow_uuid = cow.UUID \n" +
              "\t) weekAvgMilkVolume \n" +
              "FROM\n" +
              "\tt_nz_cow cow\n" +
              "\tINNER JOIN t_jc_house house ON ( house.UUID = cow.HOUSE_UUID AND house.DEL = 0 )\n" +
              "\tLEFT JOIN t_nt_dhi_checking dhi ON (\n" +
              "\tdhi.COW_UUID = cow.UUID \n" +
              "\tAND ( dhi.test_date BETWEEN DATE_SUB( CURRENT_DATE ( ), INTERVAL 60 DAY ) AND CURRENT_DATE ( ) ) \n" +
              "\tAND dhi.del = 0 \n" +
              "\tAND NOT EXISTS (\n" +
              "SELECT\n" +
              "\t1 \n" +
              "FROM\n" +
              "\tt_nt_dhi_checking idh \n" +
              "WHERE\n" +
              "\tidh.COW_UUID = dhi.COW_UUID \n" +
              "\tAND idh.test_date > dhi.test_date \n" +
              "\tAND idh.del = 0 \n" +
              "\t) \n" +
              "\t) \n" +
              "WHERE\n" +
              "\tcow.DEL = 0\n" +
              ") t";
      String sql = "select if(2=(select * from t),1,0) from t";
        return  DruidUtil.isolationEntry(sql);
    }
}
