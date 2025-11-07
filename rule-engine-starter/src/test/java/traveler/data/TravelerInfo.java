package traveler.data;

import lombok.*;
import lombok.experimental.SuperBuilder;
import traveler.pojo.Traveler;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TravelerInfo extends Traveler {
    //旅客Id
    private String travelerId;
    //身份证
    private String idCard;
    //护照
    private String passport;
    //重点人员
    private Integer keyPerson;
    //黑名单人员
    private Integer blacklist;
}
