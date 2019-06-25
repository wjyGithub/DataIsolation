package com.data.isolation.main;

import com.data.isolation.parser.DruidUtil;

/**
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/11 22:19.
 */
public class Main {

    public static void main(String[] args) {
//        String sql = "select if((select count(1) from xxx) / (select count(1) from xxxx) = 0,1,0), (select 1 from userr union all select 1 from tta union all select 1 from xx) from (select * from t_user) t inner join (select * from u) ON (t.id = u.id) left join (select * from xx) inner join (select * from xxi) union all select 1 from xx";
//        String sql = "SELECT\n" +
//                "            CASE WHEN t.num > 4 THEN \"其他\"\n" +
//                "            ELSE t.THING_CATEGORY END name,\n" +
//                "            SUM(t.faultNum) faultNum\n" +
//                "        FROM\n" +
//                "        (\n" +
//                "            SELECT\n" +
//                "                t.THING_CATEGORY,\n" +
//                "                t.faultNum,\n" +
//                "                @i:=@i + 1 num\n" +
//                "            FROM\n" +
//                "            (\n" +
//                "                SELECT\n" +
//                "                    THING_CATEGORY,\n" +
//                "                    COUNT(LEVEL = \"fault\" OR null) faultNum\n" +
//                "                FROM t_alert ta INNER JOIN t_alert_property_index_rel pir ON (ta.ALERT_ID = pir.ALERT_ID)\n" +
//                "                JOIN t_thing tt ON (tt.THING_ID = pir.THING_ID)\n" +
//                "                WHERE\n" +
//                "                    ta.EVENT_TIME >= date_add(curdate(), interval - day(curdate()) + 1 day)\n" +
//                "                    AND ta.IS_DEL = 0\n" +
//                "                    AND pir.IS_DEL = 0\n" +
//                "                    AND tt.IS_DEL = 0\n" +
//                "                GROUP BY THING_CATEGORY\n" +
//                "                ORDER BY 2 DESC\n" +
//                "            ) t,(SELECT @i:=0) tt\n" +
//                "        ) t\n" +
//                "        GROUP BY 1\n" +
//                "        ORDER BY name ='其他',faultNum DESC";
//        String sql = "select * from user u,order o inner join thint t ";
        String sql = "SELECT\n" +
                "            t.THING_PROPERTY_ID,\n" +
                "            t.THING_ID,\n" +
                "            (SELECT `VALUE` FROM t_property_value WHERE THING_ID = t.THING_ID AND DATA_ID = t.DATA_ID ) value,\n" +
                "            t.dataName,\n" +
                "            t.DATA_ID,\n" +
                "            t.typeName,\n" +
                "            t.`OPTIONS`,\n" +
                "            t.CATEGORY,\n" +
                "            t.DATA_TYPE,\n" +
                "            t.`CODE`,\n" +
                "            t.STATUS\n" +
                "        FROM\n" +
                "        (\n" +
                "            SELECT\n" +
                "                tpt.THING_ID,\n" +
                "                tp.THING_PROPERTY_ID,\n" +
                "                tp.`NAME` dataName,\n" +
                "                tp.DATA_ID,\n" +
                "                type.`NAME` typeName,\n" +
                "                type.`OPTIONS`,\n" +
                "                type.CATEGORY,\n" +
                "                type.DATA_TYPE,\n" +
                "                type.`CODE`,\n" +
                "                tp.STATUS\n" +
                "            FROM t_thing_property tp\n" +
                "            INNER JOIN t_thing_property_group_rel tpt ON (tp.THING_PROPERTY_ID = tpt.THING_PROPERTY_ID AND tpt.THING_PROPERTY_ID != -1)\n" +
                "            INNER JOIN t_thing_property_type type ON (tp.THING_PROPERTY_TYPE_ID = type.THING_PROPERTY_TYPE_ID)\n" +
                "            WHERE type.CATEGORY = 'status'\n" +
                "            UNION ALL\n" +
                "            SELECT\n" +
                "                tpt.THING_ID,\n" +
                "                tp.THING_PROPERTY_ID,\n" +
                "                tp.`NAME` dataName,\n" +
                "                tp.DATA_ID,\n" +
                "                type.`NAME` typeName,\n" +
                "                type.`OPTIONS`,\n" +
                "                type.CATEGORY,\n" +
                "                type.DATA_TYPE,\n" +
                "                type.`CODE`,\n" +
                "                tp.STATUS\n" +
                "            FROM t_thing_property tp\n" +
                "            INNER JOIN t_thing_property_group_rel tpt ON (tp.THING_PROPERTY_GROUP_ID = tpt.PROPERTY_GROUP_ID AND tpt.PROPERTY_GROUP_ID != -1 )\n" +
                "            INNER JOIN t_thing_property_type type ON (tp.THING_PROPERTY_TYPE_ID = type.THING_PROPERTY_TYPE_ID)\n" +
                "            WHERE type.CATEGORY = 'status'\n" +
                "        ) t\n" +
                "        WHERE t.THING_ID = 1 and t.cc between t.aa and t.cc and a.cc in (select * from xx) and (b=1 or a=1)";
        String sqlQ = "SELECT\n" +
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
        String sqlT= "select if((select * from xx union select aa from xx),1,1) t from user";
        String addSql = DruidUtil.addWhereForSelectSql(sqlT);
        System.out.println(addSql);

    }
}
