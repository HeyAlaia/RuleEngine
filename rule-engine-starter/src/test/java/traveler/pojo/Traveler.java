package traveler.pojo;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Traveler {
    //姓名
    private String name;
    //旅客类型
    private String travelerType;
    //证件类型
    private Integer cardType;
    //证件号
    private String cardNumber;
    //出生年月日
    private String birthday;
    // 性别
    private Integer sex;
    // 国别
    private String country;
    //姓名配音
    private String namePinyin;
    //抓拍图片
    private String picture;
    //是否成功
    private Integer pass;
    //方向
    private String direction;
    //通道号
    private String passageNo;
}
