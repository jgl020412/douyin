package com.atjgl.mo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

/**
 * @author 小亮
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document("message")
public class MessageMO {

    @Id
    private String id;              // 消息的主键ID

    @Field("fromUserId")
    private String fromUserId;      // 来自于那个用户的id
    @Field("fromNickname")
    private String fromNickname;    // 来源用户的昵称
    @Field("fromFace")
    private String fromFace;        // 来源用户的头像

    @Field("toUserId")
    private String toUserId;        // 目标用户的id

    @Field("msgType")
    private Integer msgType;        // 消息的种类
    @Field("msgContent")
    private Map msgContent;         // 消息的内容

    @Field("create_time")
    private Date createTime;        // 消息创建的时间

}
