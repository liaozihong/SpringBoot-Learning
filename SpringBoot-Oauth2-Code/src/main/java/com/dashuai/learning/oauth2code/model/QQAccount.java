package com.dashuai.learning.oauth2code.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Qq account
 * <p/>
 * Created in 2019.05.14
 * <p/>
 *
 * @author Liaozihong
 */
@Data
@EqualsAndHashCode(of = "qq")
@ToString(exclude = "fans")
@Builder
public class QQAccount {

    private String qq;
    private String nickName;
    private String level;
    private List<QQAccount> fans;

}
