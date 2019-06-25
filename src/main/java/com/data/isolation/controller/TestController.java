package com.data.isolation.controller;

import com.data.isolation.parser.DruidUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/25 19:14.
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {

    @GetMapping(value = "")
    private String processor() {
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
        return  DruidUtil.addWhereForSelectSql(sqlT);
    }
}
